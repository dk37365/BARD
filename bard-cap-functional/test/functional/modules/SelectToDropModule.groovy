package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class SelectToDropModule extends Module {
	static content = {
		selectToResult(wait: true, required: false) { $("ul.select2-results") }
		searchResult(wait: true, required: false) { selectToResult.find("li") }
//		selectToSearch(wait: true, required: false) { $("div.select2-search") }
//		searchInput(wait: true, required: false) { selectToSearch.find("input.select2-input") }
	}
}