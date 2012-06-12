package bard.db.dictionary

class Stage {

    static expose = 'stage'

	String stage
	String description
    Element element
    Stage parent
    String stageStatus

    static hasMany = [children: Stage]

    static mapping = {
		id column: "node_id", generator: "assigned"
        parent column: "parent_node_id"
        version false
        element column: "stage_id"
	}

	static constraints = {
		stage maxSize: 128
        element nullable: false
		description nullable: true, maxSize: 1000
        stageStatus maxSize: 20, nullable: false, inList: ["Pending", "Published", "Deprecated", "Retired"]

    }
}
