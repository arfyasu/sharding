
class Visit {

	long libraryId
	String person
	Date visitDate

	def getLibrary() {
		return Library.get(libraryId)
	}
}
