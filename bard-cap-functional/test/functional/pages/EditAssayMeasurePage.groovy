import java.awt.TextArea;

import geb.Page
import geb.Module
import geb.navigator.Navigator
import modules.SelectToContainerModule
import modules.SelectInputModule
import modules.SelectResultPopListModule

class EditAssayMeasurePage extends Page{
	static url=""
	static at = { waitFor(10, 0.5){$("div.span12.well.well-small").find("h4").text().contains("Editing Measures") }
	}

	static content = {
		finishEditingBtn { $("a.btn.btn-small.btn-primary")}  // Finish Editing asssay measure btn
		addTopMeasureBtn { $("a#add-measure-at-top") } // add Top Measure button

		measuresHolder(wait: true) { $("ul.dynatree-container").find("li") }
		addMeasureForm { $("form#add-measure-form") }
		selectResultTye { module SelectToContainerModule, $("div#s2id_resultTypeId") }
		selectStatistics { module SelectToContainerModule, $("div#s2id_statisticId") }
		//measureDetail { measureName -> module MeasureDetailsModule, $("div.measure-detail-card").find("h1", text:"Measure: $measureName").parent() }
		measureDetail { module MeasureDetailsModule }
		enterInput { module SelectInputModule }
		resultPopulated { module SelectResultPopListModule, $("div.select2-drop.select2-drop-active") }
		footerBtns { $("div.modal-footer").find("button") }
	}

	def newMeasure(resultType, resultVal){
		//Thread.sleep(2000)
		waitFor(15, 2){ selectResultTye.selectLink.displayed }
		assert selectResultTye.selectLink
		selectResultTye.selectLink.click()
		waitFor { enterInput.enterResult }
		enterInput.enterResult.value("$resultType")
		Thread.sleep(3000)
		waitFor(15, 2){ resultPopulated.resultPopup }
		resultPopulated.resultPopup[0].click()
		assert selectStatistics.selectLink
		selectStatistics.selectLink.click()
		waitFor { enterInput.enterResult }
		enterInput.enterResult.value("$resultVal")
		Thread.sleep(2000)
		waitFor(15, 2){ resultPopulated.resultPopup }
		resultPopulated.resultPopup[0].click()
		footerBtns[1].click()
		waitFor(10, 3) { measuresHolder }
	}

	def isMeasureAdded(resultType, resultVal){
		waitFor(){ measuresHolder }
		assert $("a.dynatree-title", text:"$resultType ($resultVal)")
	}

	def navigateToChildMeasure(topMeasureType, topMeasureVal, childMeasureType, childMeasureVal){
		def treeNaviSpan = measuresHolder.find("a", text:"$topMeasureType ($topMeasureVal)")
		treeNaviSpan.parent().next().find("a").click()
		//def treeNaviSpan = measuresHolder.find("a", text:"$topMeasureType ($topMeasureVal)").parent()
		//if(treeNaviSpan.next().isDisplayed()){
		//	measuresHolder.find("ul").find("a", text:"$childMeasureType ($childMeasureVal)").click()
		//}else{
		//	treeNaviSpan.find("span.dynatree-expander").click()
		//	measuresHolder.find("ul").find("a", text:"$childMeasureType ($childMeasureVal)").click()
		//}
	}
}

class MeasureDetailsModule extends Module {
	static content = {
		assayContext(wait: true) { mName -> $("h1", text:"Measure: $mName").parent().find("#assayContextId")}
		//measureForm { $("form") }
		//assayContext { BtnText -> $("a.btn", text:"Click to add new measure under $BtnText").parent().find("#assayContextId")}
		//assayContext { measureForm.find("#assayContextId") }
		associateBtn(wait: true) { mName -> $("h1", text:"Measure: $mName").parent().find("button", text:"Associate") }
		//associateBtn { measureForm.find("button", text:"Associate") }
		addChildMeasureBtn(wait: true) { BtnText -> $("a.btn", text:"Click to add new measure under $BtnText") }
		disasiciateBtn(wait: true) { BtnText -> $("button", text:"Disassociate context from $BtnText") }
		deleteMeasure(wait: true) { BtnText -> $("button", text:"Click to delete $BtnText"+" entirely")}
	}
}

/*class SelectToContainer extends Module {
	static content = {
		selectLink(wait: true) { $("a") }
	}
}

class SelectInputModule extends Module {
	static content = {
		select2Input { $("div.select2-drop.select2-drop-active")}
		enterResult(wait: true) { select2Input.find("input.select2-input") }
	}
}
*/