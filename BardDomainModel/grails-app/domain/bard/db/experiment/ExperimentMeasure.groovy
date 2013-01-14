package bard.db.experiment

import bard.db.registration.Measure

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/30/12
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentMeasure {

    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int PARENT_CHILD_RELATIONSHIP_MAX_SIZE = 20

    ExperimentMeasure parent
    String parentChildRelationship

    Experiment experiment
    Measure measure

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static constraints = {
        parent(nullable: true)
        parentChildRelationship(nullable: true, blank: false, maxSize: PARENT_CHILD_RELATIONSHIP_MAX_SIZE, inList: ['Derived from', 'has Child', 'has Sibling'])
        experiment()
        measure()

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
    static mapping = {
        table('EXPRMT_MEASURE')
        id(column: "EXPRMT_MEASURE_ID", generator: "sequence", params: [sequence: 'EXPRMT_MEASURE_ID_SEQ'])
        parent(column: 'PARENT_EXPRMT_MEASURE_ID')
    }
}