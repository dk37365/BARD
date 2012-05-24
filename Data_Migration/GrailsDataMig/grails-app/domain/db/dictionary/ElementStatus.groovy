package db.dictionary

class ElementStatus {

    static expose = 'element-status'

	String elementStatus
	String capability
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static mapping = {
		id column: "Element_Status_ID"
	}

	static constraints = {
		elementStatus maxSize: 20
		capability nullable: true, maxSize: 256
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
