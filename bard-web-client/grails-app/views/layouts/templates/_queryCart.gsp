<r:require module="cart"/>

<div class="well well-small">
    <g:include controller="queryCart" action="refreshSummaryView" params="[searchString:flash.searchString ?: params?.searchString]" />
</div>

<div class="panel" style="z-index: 10">
    <a class="trigger" href="#">Click to hide query cart</a>
    <g:include controller="queryCart" action="refreshDetailsView" params="[searchString:flash.searchString ?: params?.searchString]"/>
</div>