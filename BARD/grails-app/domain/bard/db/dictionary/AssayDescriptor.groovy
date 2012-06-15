package bard.db.dictionary

class AssayDescriptor {

    static expose = 'assay-descriptor'

	AssayDescriptor parent
	Element element
	String label
	String description
	String abbreviation
	String acronym
	String synonyms
	String externalURL
	String elementStatus
	Unit unit

	static hasMany = [children: AssayDescriptor]

	static mapping = {
		id column: "node_id", generator: "assigned"
		parent column: "parent_node_id"
		externalURL column: "external_url"
		unit column: "unit"
		version false
	}

	static constraints = {
		parent nullable: true
		label maxSize: 128
		description nullable: true, maxSize: 1000
		abbreviation nullable: true, maxSize: 20
		acronym nullable: true, maxSize: 20
		synonyms nullable: true, maxSize: 1000
		externalURL nullable: true, maxSize: 1000
		unit nullable: true
		elementStatus maxSize: 20, nullable: false, inList: ["Pending", "Published", "Deprecated", "Retired"]
	}
}
