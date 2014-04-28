%{-- Copyright (c) 2014, The Broad Institute
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
 --}%

<%--
  Created by IntelliJ IDEA.
  User: xiaorong
  Date: 12/5/12
  Time: 5:34 PM
  To change this template use File | Settings | File Templates.
--%>


<%-- A template for showing summary for both project and assay def, show as card --%>
<div id="card-${context.id}" class="card roundedBorder card-table-container">
    <table class="table table-hover">
        <caption id="${context.id}" class="assay_context">
            <div class="cardTitle">
                <p>${context.name}</p>
                <g:if test="${!context.relatedMeasures.isEmpty()}">
                    <p>Measure<g:if test="${context.relatedMeasures.size() > 1}">s</g:if>:
                        <g:each in="${context.relatedMeasures}" status="i" var="assayContextMeasure">
                            <a href="#measures-header" class="treeNode" id="${assayContextMeasure.id}">
                                ${assayContextMeasure.comps.first().display}
                                <g:if test="${i < context.relatedMeasures.size() - 1}">,  </g:if></a>
                        </g:each>
                    </p>
                </g:if>
            </div>
        </caption>
        <tbody>
        <g:each in="${context.getContextItems()}" status="i" var="contextItem">
            <tr id="${contextItem.id}" class='context_item_row'>
                <td class="attributeLabel">${contextItem.key}</td>
                <td class="valuedLabel">
                    <g:if test="${contextItem.url}">
                        <a href="${contextItem.url}" target="_blank">${contextItem.display}</a>
                    </g:if>
                    <g:else>
                        ${contextItem.display}
                    </g:else>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

