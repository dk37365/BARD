package bard.db.registration

import org.hibernate.SessionFactory

class AssayDefinitionController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    CardFactoryService cardFactoryService
    AssayContextService assayContextService

    def index() {
        redirect(action: "description", params: params)
    }

    def description() {
        [assayInstance: new Assay(params)]
    }

    def save() {
        def assayInstance = new Assay(params)
        if (!assayInstance.save(flush: true)) {
            render(view: "description", model: [assayInstance: assayInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'assay.label', default: 'Assay'), assayInstance.id])
        render(view: "description", model: [assayInstance: assayInstance])
//		redirect(action: "show", id: assayInstance.id)
    }

    def show() {
        def assayInstance = Assay.get(params.id)

        if (!assayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        }
        else
            flash.message = null

        List<CardDto> cardDtoList = cardFactoryService.createCardDtoListForAssay(assayInstance)

        [assayInstance: assayInstance, cardDtoList: cardDtoList]
    }

    def findById() {
        if (params.assayId && params.assayId.isLong()) {
            def assayInstance = Assay.findById(params.assayId.toLong())
            log.debug "Find assay by id"
            if (assayInstance?.id)
                redirect(action: "show", id: assayInstance.id)
            else
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.assayId])
        }
    }

    def findByName() {
        if (params.assayName) {
            def assays = Assay.findAllByAssayNameIlike("%${params.assayName}%")
            if (assays?.size() != 0) {
                if (assays.size() > 1)
                    render(view: "findByName", params: params, model: [assays: assays])
                else
                    redirect(action: "show", id: assays.get(0).id)
            }
            else
                flash.message = message(code: 'default.not.found.property.message', args: [message(code: 'assay.label', default: 'Assay'), "name", params.assayName])

        }
    }

    def addItemToCardAfterItem(Long src_assay_context_item_id, Long target_assay_context_item_id) {
        AssayContextItem target = AssayContextItem.findById(target_assay_context_item_id)
        AssayContextItem source = AssayContextItem.findById(src_assay_context_item_id)
        AssayContext targetAssayContext = target.assayContext
        int index = targetAssayContext.assayContextItems.indexOf(target)
        assayContextService.addItem(index, source,targetAssayContext)
        List<CardDto> cardDtoList = cardFactoryService.createCardDtoListForAssay(targetAssayContext.assay)
        render(template: "cards", model: [cardDtoList: cardDtoList])
    }

    def addItemToCard(Long src_assay_context_item_id, Long target_assay_context_id) {
        AssayContext targetAssayContext = AssayContext.findById(target_assay_context_id)
        AssayContextItem source = AssayContextItem.findById(src_assay_context_item_id)
        assayContextService.addItem(source, targetAssayContext)
        List<CardDto> cardDtoList = cardFactoryService.createCardDtoListForAssay(targetAssayContext.assay)
        render(template: "cards", model: [cardDtoList: cardDtoList])
    }



    def updateCardTitle(Long src_assay_context_item_id, Long target_assay_context_id) {
        AssayContextItem sourceAssayContextItem = AssayContextItem.findById(src_assay_context_item_id)
        AssayContext targetAssayContext = AssayContext.findById(target_assay_context_id)
        if (targetAssayContext && targetAssayContext.assayContextItems.contains(sourceAssayContextItem)) {
            targetAssayContext.contextName = sourceAssayContextItem.valueDisplay
        }
        CardDto cardDto = cardFactoryService.createCardDto(targetAssayContext)
        render(template: "cardDto", model: [card: cardDto])
    }
}
