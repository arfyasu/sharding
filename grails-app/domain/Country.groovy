
class Country {

	String name
	String code

	static mapping = {
		version false
	}

	static constraints = {
		name maxSize: 128, unique: true
		code maxSize: 2, unique: true
	}

	String toString() {
		name
	}
}
