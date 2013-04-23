class LibraryController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [libraryInstanceList: Library.list(params), libraryInstanceTotal: Library.count()]
    }

    def create = {
        def libraryInstance = new Library()
        libraryInstance.properties = params
        return [libraryInstance: libraryInstance]
    }

    def save = {
        def libraryInstance = new Library(params)
        if (libraryInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'library.label', default: 'Library'), libraryInstance.id])}"
            redirect(action: "show", id: libraryInstance.id)
        }
        else {
            render(view: "create", model: [libraryInstance: libraryInstance])
        }
    }

    def show = {
        def libraryInstance = Library.get(params.id)
        if (!libraryInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'library.label', default: 'Library'), params.id])}"
            redirect(action: "list")
        }
        else {
            [libraryInstance: libraryInstance]
        }
    }

    def edit = {
        def libraryInstance = Library.get(params.id)
        if (!libraryInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'library.label', default: 'Library'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [libraryInstance: libraryInstance]
        }
    }

    def update = {
        def libraryInstance = Library.get(params.id)
        if (libraryInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (libraryInstance.version > version) {
                    
                    libraryInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'library.label', default: 'Library')] as Object[], "Another user has updated this Library while you were editing")
                    render(view: "edit", model: [libraryInstance: libraryInstance])
                    return
                }
            }
            libraryInstance.properties = params
            if (!libraryInstance.hasErrors() && libraryInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'library.label', default: 'Library'), libraryInstance.id])}"
                redirect(action: "show", id: libraryInstance.id)
            }
            else {
                render(view: "edit", model: [libraryInstance: libraryInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'library.label', default: 'Library'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def libraryInstance = Library.get(params.id)
        if (libraryInstance) {
            try {
                libraryInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'library.label', default: 'Library'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'library.label', default: 'Library'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'library.label', default: 'Library'), params.id])}"
            redirect(action: "list")
        }
    }
}
