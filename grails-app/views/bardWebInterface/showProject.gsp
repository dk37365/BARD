<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="details"/>
    <title>BARD : Project : ID ${projectInstance?.id}</title>
    <r:script>
	$(document).ready(function() {
		$( "#accordion" ).accordion({ autoHeight: false });
	}) 
</r:script>

</head>
<body>
<h1 class="detail">Project Detail for ID ${projectInstance?.id}</h1>
<div class="row-fluid" style="clear:both;">
    <div class="span12 header">
        <h3>${projectInstance?.name}</h3>
    </div>
</div>
<r:script>
    $(document).ready(function () {
        $('.collapse').on('show', function () {
            var icon = $(this).siblings().find("i.icon-chevron-right");
            icon.removeClass('icon-chevron-right').addClass('icon-chevron-down');
        });
        $('.collapse').on('hide', function () {
            var icon = $(this).siblings().find("i.icon-chevron-down");
            icon.removeClass('icon-chevron-down').addClass('icon-chevron-right');
        });
    })
</r:script>
<div class="row-fluid">
    <div class="span12 accordion">
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#summary-header" id="summary-header" class="accordion-toggle" data-toggle="collapse" data-target="#summary-info"><i class="icon-chevron-down"></i> Summary Info</a>
                <div id="summary-info" class="accordion-body in collapse">
                    <div class="accordion-inner">
                        <g:render template="projectSummary" model="['projectInstance': projectInstance]" />
                    </div>
                </div>
            </div>
        </div>
        <div class="accordion-group">
            <div class="accordion-heading">
                <a href="#document-header" id="document-header" class="accordion-toggle" data-toggle="collapse" data-target="#document-info"><i class="icon-chevron-right"></i> Documents</a>
                <div id="document-info" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <g:render template="projectDocuments" model="['projectInstance': projectInstance]" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>