/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db.registration

import bard.db.enums.AssayType
import bard.db.enums.DocumentType
import bard.db.enums.ReadyForExtraction
import bard.db.enums.Status
import bard.db.enums.hibernate.AssayTypeEnumUserType
import bard.db.enums.hibernate.ReadyForExtractionEnumUserType
import bard.db.enums.hibernate.StatusEnumUserType
import bard.db.experiment.Experiment
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceAware
import bard.db.guidance.GuidanceRule
import bard.db.guidance.GuidanceUtils
import bard.db.guidance.owner.ContextItemShouldHaveValueRule
import bard.db.guidance.owner.MinimumOfOneBiologyGuidanceRule
import bard.db.guidance.owner.OneItemPerNonFixedAttributeElementRule
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.people.Person
import bard.db.people.Role
import bard.db.project.ProjectSingleExperiment
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.grails.datastore.mapping.validation.ValidationErrors
import org.springframework.validation.Errors

class Assay extends AbstractContextOwner implements GuidanceAware {
    public static final int ASSAY_NAME_MAX_SIZE = 1000
    private static final int ASSAY_VERSION_MAX_SIZE = 10
    public static final int DESIGNED_BY_MAX_SIZE = 100
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int APPROVED_BY_MAX_SIZE = 40
    private static final int ASSAY_SHORT_NAME_MAX_SIZE = 250

    /**
     This transient variable determines whether context items should be fully validated or not
     This is a short term fix, until we implement the Guidance work, that Dan is working on
     By default this is set to true and it is only used in  bard.db.registration.AssayContextItem#valueValidation(Errors errors)
     This is not in the assayContextItem class because we would have to set it for every single context item that we do not want to validate.
     Keeping it here means that it is located in one place.
     */
    boolean fullyValidateContextItems = true


    def capPermissionService
    def springSecurityService
    Status assayStatus = Status.DRAFT
    String assayName
    String assayVersion
    String designedBy
    ReadyForExtraction readyForExtraction = ReadyForExtraction.NOT_READY
    AssayType assayType = AssayType.REGULAR
    Long ncgcWarehouseId;

    String modifiedBy
    // grails auto-timestamp
    Date dateCreated
    Date lastUpdated = new Date()
    Person approvedBy
    Date approvedDate

    Set<Experiment> experiments = [] as Set<Experiment>
    //TODO: Mark for deletion
    // Set<Measure> measures = [] as Set<Measure>
    List<AssayContext> assayContexts = [] as List<AssayContext>
    Set<AssayDocument> assayDocuments = [] as Set<AssayDocument>
    Set<PanelAssay> panelAssays = [] as Set

    Role ownerRole //The team that owns this object. This is used by the ACL to allow edits etc

    // if this is set, then don't automatically update readyForExtraction when this entity is dirty
    // this is needed to change the value to anything except "Ready"
    boolean disableUpdateReadyForExtraction = false

    static belongsTo = [ownerRole: Role]
    static hasMany = [
            experiments: Experiment,
            //measures: Measure,
            assayContexts: AssayContext,
            assayDocuments: AssayDocument,
            panelAssays: PanelAssay

    ]

    static constraints = {
        assayStatus()
        assayName(maxSize: ASSAY_NAME_MAX_SIZE, blank: false)
        assayVersion(maxSize: ASSAY_VERSION_MAX_SIZE, blank: false)
        designedBy(nullable: true, maxSize: DESIGNED_BY_MAX_SIZE)
        ncgcWarehouseId(nullable: true)
        readyForExtraction(nullable: false)
        // TODO we can use enum mapping for this http://stackoverflow.com/questions/3748760/grails-enum-mapping
        // the ' - ' is this issue in this case
        assayType(nullable: false)
        dateCreated(nullable: false)
        lastUpdated(nullable: false)
        ownerRole(nullable: false)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
        approvedBy(nullable: true)
        approvedDate(nullable: true)
    }

    static mapping = {
        id(column: "ASSAY_ID", generator: "sequence", params: [sequence: 'ASSAY_ID_SEQ'])
        assayStatus(type: StatusEnumUserType)
        readyForExtraction(type: ReadyForExtractionEnumUserType)
        assayType(type: AssayTypeEnumUserType)
        assayContexts(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'true', cascade: 'all-delete-orphan')
        approvedBy(column: "APPROVED_BY")
    }
    static transients = ['fullyValidateContextItems', 'assayContextItems', 'publications', 'externalURLs', 'comments', 'protocols', 'otherDocuments', 'descriptions', "disableUpdateReadyForExtraction"]

    def afterInsert() {
        Assay.withNewSession {
            capPermissionService?.addPermission(this)
        }
    }

    String getOwner() {
        final String objectOwner = this.ownerRole?.displayName
        return objectOwner
    }


    List<AssayDocument> getPublications() {
        final List<AssayDocument> documents = assayDocuments.findAll {
            it.documentType == DocumentType.DOCUMENT_TYPE_PUBLICATION
        } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<AssayDocument> getExternalURLs() {
        final List<AssayDocument> documents = assayDocuments.findAll {
            it.documentType == DocumentType.DOCUMENT_TYPE_EXTERNAL_URL
        } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<AssayDocument> getComments() {
        final List<AssayDocument> documents = assayDocuments.findAll {
            it.documentType == DocumentType.DOCUMENT_TYPE_COMMENTS
        } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<AssayDocument> getDescriptions() {
        final List<AssayDocument> documents = assayDocuments.findAll {
            it.documentType == DocumentType.DOCUMENT_TYPE_DESCRIPTION
        } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<AssayDocument> getProtocols() {
        final List<AssayDocument> documents = assayDocuments.findAll {
            it.documentType == DocumentType.DOCUMENT_TYPE_PROTOCOL
        } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<AssayDocument> getOtherDocuments() {
        final List<AssayDocument> documents = assayDocuments.findAll {
            it.documentType == DocumentType.DOCUMENT_TYPE_OTHER
        } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<AssayContextItem> getAssayContextItems() {
        Set<AssayContextItem> assayContextItems = new HashSet<AssayContextItem>()
        for (AssayContext assayContext : this.assayContexts) {
            assayContextItems.addAll(assayContext.assayContextItems)
        }
        return assayContextItems as List<AssayContextItem>
    }

    /**
     * duck typing to look like project
     * @return assayDocuments
     */
    Set<AssayDocument> getDocuments() {
        this.assayDocuments
    }
    /**
     *  duck typing to look like project
     * @return assayName
     */
    String getName() {
        this.assayName
    }

    List<AssayContext> getContexts() {
        this.assayContexts
    }

    int getMaxNumProjectsInExperiments() {
        int maxProjectExperiments = 0;
        for (Experiment exp : this.experiments) {
            int count = 0;
            exp.projectExperiments.each { ProjectSingleExperiment pe ->
                if (pe.project.projectStatus != Status.RETIRED) {
                    count++
                }
            }
            if (count > maxProjectExperiments) {
                maxProjectExperiments = count
            }
        }
        return maxProjectExperiments;
    }


    boolean allowsNewExperiments() {
        return (assayStatus != Status.RETIRED && assayType != AssayType.TEMPLATE)
    }

    @Override
    void removeContext(AbstractContext context) {
        this.removeFromAssayContexts(context)
    }

    @Override
    AbstractContext createContext(Map properties) {
        AssayContext context = new AssayContext(properties)
        addToAssayContexts(context)
        return context
    }

    @Override
    List<GuidanceRule> getGuidanceRules() {
        [new MinimumOfOneBiologyGuidanceRule(this), new OneItemPerNonFixedAttributeElementRule(this), new ContextItemShouldHaveValueRule(this)]
    }

    @Override
    List<Guidance> getGuidance() {
        GuidanceUtils.getGuidance(getGuidanceRules())
    }

    public boolean permittedToSeeEntity() {
        if ((assayStatus == Status.DRAFT) &&
                (!SpringSecurityUtils.ifAnyGranted('ROLE_BARD_ADMINISTRATOR') &&
                        !SpringSecurityUtils.principalAuthorities.contains(this.ownerRole))) {
            return false
        }
        return true
    }
    /**
     * if the status is Approved look at all the contextItems an ensure all Fixed items have values
     * for Draft or Retired, fixed contextItems can have null values. ( guidance will ask the user to fill in values )
     *
     * Initially, had this wired into the validation for the assayStatus field, but, this resulted in some errors
     * see http://naleid.com/blog/2010/11/21/grails-issue-not-processed-by-flush-root-causes which mentions reading dependent
     * objects in as part of a validation.
     */
    void validateItems() {
        validate()
        final List<String> itemLabels = []
        if (this.assayStatus == Status.APPROVED || this.assayStatus==Status.PROVISIONAL) {

            for (AssayContextItem assayContextItem in AssayContext.findByAssay(this).assayContextItems.flatten()) {
                if (ContextItemShouldHaveValueRule.isFixedAndAllValuesNull(assayContextItem)) {
                    itemLabels.add(assayContextItem.attributeElement.label)
                }
            }

        }
        if (itemLabels) {
            final String quotedCommaSeparatedlabels = itemLabels.sort().collect { "'$it'" }.join(',')
            final String msg = "You must provide values for the following annotations before this assay can be approved: ${quotedCommaSeparatedlabels}"
            final ValidationErrors localErrors = new ValidationErrors(this)
            localErrors.rejectValue('assayStatus', 'assay.assayStatus.requires.items.with.values', msg)
            addToErrors(localErrors)
        }
    }

    protected addToErrors(ValidationErrors localErrors) {
        if (errors) {
            errors.addAllErrors(localErrors)
        } else {
            setErrors(localErrors)
        }
    }

}
