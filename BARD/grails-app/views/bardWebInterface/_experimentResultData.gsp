<%--
  Created by IntelliJ IDEA.
  User: gwalzer
  Date: 9/21/12
  Time: 10:55 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${preview}">
    <h2 style="background-color:green;">PREVIEW OF Experiment: ${tableModel?.additionalProperties?.experimentName}</h2>
</g:if>
<g:else>
    <h2>Experiment: ${tableModel?.additionalProperties?.experimentName}</h2>
</g:else>




<g:render template="/experiment/experimentReferences"
          model="[experiment: capExperiment, excludedLinks: ['bardWebInterface.showExperiment']]"/>

<div class="row-fluid">
<g:if test="${tableModel.data}">
    <g:if test="${!preview}">
        <div class="row-fluid ">
            <div id="histogramHere" class="span12"></div>
        </div>
    </g:if>


    <div class="row-fluid">
    <g:hiddenField name="paginationUrl"
                   id="paginationUrl"/> %{--Used to hold the pagination url, if a paging link has been clicked--}%
    <div class="pagination offset2">

        <g:paginate
                total="${totalNumOfCmpds}"
                params='[id: "${params?.id}", normalizeYAxis: "${tableModel?.additionalProperties.normalizeYAxis}"]'/>
    </div>

    <div id="resultData">

        <g:render template="experimentResultRenderer"
                  model="[tableModel: tableModel, landscapeLayout: true, innerBorder: innerBorder]"/>
    </div>

    <div class="pagination offset2">
        <g:paginate
                total="${totalNumOfCmpds}"
                params='[id: "${params?.id}", normalizeYAxis: "${tableModel?.additionalProperties.normalizeYAxis}"]'/>
    </div>
</g:if>
<g:else>
    <p class="text-info"><i
            class="icon-warning-sign"></i> No experimental data found
    </p>
</g:else>
</div>
