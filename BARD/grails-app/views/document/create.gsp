<%@ page import="bard.db.registration.*" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <r:require modules="core,bootstrap,assayshow,richtexteditorForCreate"/>
    <meta name="layout" content="basic"/>
    <title>Create Assay Document</title>

</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <h3>Create a New Document</h3>
    </div>
    <g:render template="/common/message"/>
    <div class="row-fluid">
        <div class="span12">
            <g:form class="form-horizontal" action="save">
                <g:hiddenField name="assayId" value="${document?.assayId}"/>
                <g:hiddenField name="projectId" value="${document?.projectId}"/>
                <g:hiddenField name="experimentId" value="${document?.experimentId}"/>

                <g:render template="editProperties" model="${[document: document]}"/>

                <div class="control-group">
                    <div class="controls">
                        <g:link controller="${document?.ownerController}" action="show" id="${document?.ownerId}"
                                fragment="documents-header" class="btn">Cancel</g:link>
                        <input type="submit" class="btn btn-primary" value="Create">
                    </div>
                </div>

            </g:form>
        </div>
    </div>
</div>
</body>
</html>