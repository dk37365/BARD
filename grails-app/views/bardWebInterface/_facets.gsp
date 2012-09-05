<div class="span3">
    <g:if test="${facets}">
        <div class="facets">
            <h2>Filters</h2>
            <g:form name="${formName}" controller="bardWebInterface" action="applyFilters">
                <g:hiddenField name="searchString" value="${params?.searchString}"/>
                <g:submitButton name="Apply Filters" class="btn"/>
                <g:each in="${facets}" var="facet">
                    <fieldset>
                        <h3>${facet.id}</h3>
                        <g:each in="${facet.children}" var="entry" status="i">
                            <label class="checkbox">
                                <g:checkBox name="filters[i].filterValue" value="${entry.id}" checked="${params?.filters[i]?.filterValue == entry.id}"/> ${entry.id} (${entry.value})
                            </label>
                            <g:hiddenField name="filters[i].filterName" value="${facet.id}"/>
                        </g:each>
                    </fieldset>
                </g:each>
            </g:form>
        </div>
    </g:if>
</div>