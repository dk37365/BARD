/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package pages

import common.TestData


/**
 * @author Muhammad.Rafique
 * Date Created: 2013/02/07
 */
class ViewAssayDefinitionPage extends CapScaffoldPage{
	static url="assayDefinition/show/"+TestData.assayId
	static at = { waitFor {$("h4").text().contains("View Assay Definition")}}

	static content = {
		//		editAssayMeasureBtn { $("a.btn", text:"Edit Measures") } // Edit Measure btn
	}

	/*Map<String, String> getUIAssociatedMeasure(def cardName){
		def contextMeasure = [:]
		if(cardHolders.cardTable){
			cardHolders.cardTable.each{ cards ->
				def cardCaption = cards.find("div.cardTitle").find("p")
				if(cardCaption[0].text().equalsIgnoreCase(cardName) && cardCaption[1]){
					def uiMeasure = cardCaption[1].find("a").text()
					def measure = uiMeasure.takeWhile { it != '('}.trim()
					contextMeasure = ['measure':measure, 'context':cardCaption[0].text()]
				}
			}
		}
		return contextMeasure
	}*/

}
