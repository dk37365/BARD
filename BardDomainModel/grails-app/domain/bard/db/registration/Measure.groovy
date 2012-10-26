package bard.db.registration

import bard.db.dictionary.Element

class Measure {

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy
    AssayContext assayContext
    String entryUnit
    Element element
    Assay assay
    Measure parentMeasure

    static belongsTo = [Assay]
    static hasMany = [children: Measure]

    static mapping = {
        id(column: 'MEASURE_ID', generator: 'sequence', params: [sequence: 'MEASURE_ID_SEQ'])
        parentMeasure column: "PARENT_MEASURE_ID"
        entryUnit column: "entry_unit"
        element column: 'result_type_id'
    }

    static constraints = {
        parentMeasure nullable: true
        dateCreated maxSize: 19
        lastUpdated nullable: true, maxSize: 19
        modifiedBy nullable: true, maxSize: 40
        element nullable: false
        assayContext nullable: true
        entryUnit nullable: true
    }
}