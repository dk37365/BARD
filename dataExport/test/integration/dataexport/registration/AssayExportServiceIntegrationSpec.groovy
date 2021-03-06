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

package dataexport.registration

import bard.db.dictionary.Element
import bard.db.enums.*
import bard.db.registration.*
import common.tests.XmlTestAssertions
import common.tests.XmlTestSamples
import dataexport.util.ResetSequenceUtil
import exceptions.NotFoundException
import grails.buildtestdata.TestDataConfigurationHolder
import grails.plugin.spock.IntegrationSpec
import groovy.xml.MarkupBuilder
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import spock.lang.Unroll

import javax.sql.DataSource

import static bard.db.enums.ReadyForExtraction.COMPLETE
import static bard.db.enums.ReadyForExtraction.READY
import static javax.servlet.http.HttpServletResponse.*

@Unroll
class AssayExportServiceIntegrationSpec extends IntegrationSpec {
    AssayExportService assayExportService
    Writer writer
    MarkupBuilder markupBuilder
    DataSource dataSource
    ResetSequenceUtil resetSequenceUtil
    Resource schemaResource = new FileSystemResource(new File("web-app/schemas/assaySchema.xsd"))

    void setup() {
        this.writer = new StringWriter()
        this.markupBuilder = new MarkupBuilder(this.writer)

        TestDataConfigurationHolder.reset()
        resetSequenceUtil = new ResetSequenceUtil(dataSource)
        [
                'ASSAY_ID_SEQ',
                'ASSAY_CONTEXT_ID_SEQ',
                'ASSAY_CONTEXT_MEASURE_ID_SEQ',
                'ASSAY_DOCUMENT_ID_SEQ',
                'PANEL_ID_SEQ',
                'PANEL_ASSAY_ID_SEQ',
                'ELEMENT_ID_SEQ',
                'MEASURE_ID_SEQ',
                'ROLE_ID_SEQ'
        ].each {
            this.resetSequenceUtil.resetSequence(it)
        }
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test update Not Found Status"() {
        given: "Given a non-existing Assay"
        when: "We call the assay service to update this assay"
        this.assayExportService.update(new Long(100000), 0, COMPLETE)

        then: "An exception is thrown, indicating that the project does not exist"
        thrown(NotFoundException)
    }

    void "test update #label"() {
        given: "Given an Assay with id #id and version #version"
        Assay.build(readyForExtraction: initialReadyForExtraction, capPermissionService: null)

        when: "We call the assay service to update this assay"
        final BardHttpResponse bardHttpResponse = this.assayExportService.update(assayId, version, COMPLETE)

        then: "An ETag of #expectedETag is returned together with an HTTP Status of #expectedStatusCode"
        assert bardHttpResponse
        assert bardHttpResponse.ETag == expectedETag
        assert bardHttpResponse.httpResponseCode == expectedStatusCode
        assert Assay.get(assayId).readyForExtraction == expectedReadyForExtraction

        where:
        label                                           | expectedStatusCode     | expectedETag | assayId | version | initialReadyForExtraction | expectedReadyForExtraction
        "Return OK and ETag 1"                          | SC_OK                  | 1            | 1       | 0       | READY                     | COMPLETE
        "Return CONFLICT and ETag 0"                    | SC_CONFLICT            | 0            | 1       | -1      | READY                     | READY
        "Return PRECONDITION_FAILED and ETag 0"         | SC_PRECONDITION_FAILED | 0            | 1       | 2       | READY                     | READY
        "Return OK and ETag 0, Already completed Assay" | SC_OK                  | 0            | 1       | 0       | COMPLETE                  | COMPLETE
    }

    void "test generate and validate AssayDocument"() {
        given: "Given an Assay "
        final AssayDocument assayDocument = AssayDocument.build(documentType: DocumentType.DOCUMENT_TYPE_DESCRIPTION)

        when: "A service call is made to generate the measures for that Assay"
        this.assayExportService.generateAssayDocument(this.markupBuilder, assayDocument.id)

        then: "An XML is generated that conforms to the expected XML"
        XmlTestAssertions.validate(schemaResource, this.writer.toString())
    }

    void "test generate and validate Assay"() {

        given:
        Element element = Element.build(expectedValueType: ExpectedValueType.FREE_TEXT)
        Assay assay = Assay.build(capPermissionService: null, readyForExtraction: ReadyForExtraction.READY)
        AssayContext assayContext = AssayContext.build(assay: assay, contextType: ContextType.UNCLASSIFIED)
        AssayContextItem.build(assayContext: assayContext, attributeElement: element, valueType: ValueType.FREE_TEXT, valueDisplay: "valueDisplay")
        AssayDocument.build(assay: assay)

        Panel panel = Panel.build()
        PanelAssay.build(assay: assay, panel: panel)
        when:
        this.assayExportService.generateAssay(this.markupBuilder, assay.id)

        then: "An XML is generated that conforms to the expected XML"
        String actualXml = this.writer.toString()
        XmlTestAssertions.assertResultsWithOverrideAttributes(XmlTestSamples.ASSAY_FULL_DOC, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)
    }



    void "test generate and validate Assays #label"() {
        given: "Given there is at least one assay ready for extraction"
        Assay.build(readyForExtraction: READY, capPermissionService: null)

        when: "A service call is made to generate a list of assays ready to be extracted"
        this.assayExportService.generateAssays(this.markupBuilder)

        then: "An XML is generated that conforms to the expected XML"

        def actualXml = this.writer.toString()
        XmlTestAssertions.assertResults(XmlTestSamples.ASSAYS, actualXml)
        XmlTestAssertions.validate(schemaResource, actualXml)
    }
}
