package bard.db.experiment

import bard.db.registration.ExternalReference
import bard.db.enums.ReadyForExtraction

class Project {
    private static final int PROJECT_NAME_MAX_SIZE = 256
    private static final int READY_FOR_EXTRACTION_MAX_SIZE = 20
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int DESCRIPTION_MAX_SIZE = 1000
    private static final int GROUP_TYPE_MAX_SIZE = 20

    String projectName
    String groupType
    String description
    Date dateCreated
    Date lastUpdated
    String modifiedBy
    ReadyForExtraction readyForExtraction = ReadyForExtraction.Pending

    static hasMany = [projectExperiments: ProjectStep, externalReferences: ExternalReference]

    static mapping = {
        id(column: "PROJECT_ID", generator: "sequence", params: [sequence: 'PROJECT_ID_SEQ'])
    }

    static constraints = {
        projectName( maxSize: PROJECT_NAME_MAX_SIZE)
        // TODO make enum
        groupType( maxSize: GROUP_TYPE_MAX_SIZE, blank:false, inList: ['Project', 'Probe Report', 'Campaign', 'Panel', 'Study'])
        description(nullable: true, maxSize: DESCRIPTION_MAX_SIZE)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
        readyForExtraction( nullable: false, maxSize: READY_FOR_EXTRACTION_MAX_SIZE, )
    }
}
