
class Library {

	String name

	def getBooks() {
		return Book.findByLibrary(this)
	}

	String toString() {
		name
	}
}
