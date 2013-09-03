package modules

import geb.Page
import geb.Module
import geb.navigator.Navigator

class SummaryModule extends Module {
	static content = {
		dtLabel { $("dt") }
		ddValue { $("dd") }
	}
}