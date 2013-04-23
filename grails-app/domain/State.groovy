
class State {

	String name
	String code
	Country country

	static mapping = {
		version false
	}

	static constraints = {
		name maxSize: 128
		code maxSize: 2
	}

	String toString() {
		name
	}
}

