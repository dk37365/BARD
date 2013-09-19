<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <g:layoutHead/>
    <r:layoutResources/>
    <title>BARD: Catalog of Assay Protocols</title>
    <r:external uri="/css/layout.css"/>
    <r:external uri="/css/table.css"/>
</head>

<body>
<div class="navbar navbar-inverse navbar-static-top">
    <div class="navbar-inner">
        <div class="container-fluid">
            <div class="row-fluid">
                <div class="span12">
                    <a class="brand" href="/BARD">
                        <img width="140" height="43" src="${resource(dir: 'images', file: 'bard_logo_small.png')}"
                             alt="BioAssay Research Database"/>
                    </a>
                    <ul class="nav">
                        <li><a href="/BARD">CAP</a></li>

                        <sec:ifAnyGranted roles="ROLE_BARD_ADMINISTRATOR">
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    Admin
                                    <b class="caret"></b>
                                </a>

                                <ul class="dropdown-menu">
                                    <li class="controller"><g:link
                                            controller="register">Register External BARD User</g:link></li>
                                    <li class="controller"><g:link controller="register"
                                                                   action="listUsersAndGroups">List External BARD Users</g:link></li>
                                    <li class="controller"><g:link controller="person"
                                                                   action="list">List Person Table</g:link></li>
                                    <li class="controller"><g:link controller="moveExperiments"
                                                                   action="show">Move Experiments</g:link></li>
                                    <li class="controller"><g:link controller="mergeAssayDefinition"
                                                                   action="show">Merge Assays</g:link></li>
                                    <li class="controller"><g:link controller="assayDefinition"
                                                                   action="assayComparisonReport">Compare Assays</g:link></li>
                                    <li class="controller"><g:link controller="splitAssayDefinition"
                                                                   action="show">Split Assays</g:link></li>
                                </ul>

                            </li>
                        </sec:ifAnyGranted>

                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Assay Definitions
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu">
                                <li class="controller"><g:link controller="assayDefinition"
                                                               action="groupAssays">My Assay Definitions</g:link></li>
                                <li class="controller"><g:link controller="assayDefinition"
                                                               action="findById">Search by Assay Definition ID</g:link></li>
                                <li class="controller"><g:link controller="assayDefinition"
                                                               action="findByName">Search by Assay Definition Name</g:link></li>
                                <li class="controller"><g:link controller="assayDefinition"
                                                               action="create">Create Assay Definition</g:link></li>
                                <li class="controller"><g:link controller="assayDefinition"
                                                               action="assayComparisonReport">Compare Assay Definitions</g:link></li>
                            </ul>
                        </li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Projects
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu">
                                <li class="controller"><g:link controller="project"
                                                               action="groupProjects">My Projects</g:link></li>
                                <li class="controller"><g:link controller="project"
                                                               action="findById">Search by Project ID</g:link></li>
                                <li class="controller"><g:link controller="project"
                                                               action="findByName">Search by Project Name</g:link></li>
                                <li class="controller"><g:link controller="project"
                                                               action="create">Create a New Project</g:link></li>
                            </ul>
                        </li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                Panels
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu">
                                <li class="controller"><g:link controller="panel"
                                                               action="myPanels">My Panels</g:link></li>

                                <li class="controller"><g:link controller="panel"
                                                               action="findById">Search by Panel ID</g:link></li>
                                <li class="controller"><g:link controller="panel"
                                                               action="findByName">Search by Panel Name</g:link></li>
                                <li class="controller"><g:link controller="panel"
                                                               action="create">Create New Panel</g:link></li>
                            </ul>
                        </li>
                        <li>
                            <g:link url="${grailsApplication.config.bard.home.page}">Bard Web Client</g:link>
                        </li>
                    </ul>
                    <sec:ifLoggedIn>
                        <g:form class="navbar-form pull-right" name="logoutForm" controller="logout">
                            <span
                                    style="color: white; font-weight: bold;">Logged in as: <sec:username/></span>&nbsp;&nbsp;
                            <button type="submit" class="btn" id="logoutButton">Logout</button>
                        </g:form>
                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                        <g:form class="navbar-form pull-right" name="loginForm" controller="login">
                            Not logged in&nbsp;&nbsp;
                            <button type="submit" class="btn">Login</button>
                        </g:form> OR
                        <a class="btn btn-large" id='signin'>Sign in with your Email</a>
                    </sec:ifNotLoggedIn>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="spinner-container">
                <div id="spinner" class="spinner" style="display:none; color: blue;"><g:message code="spinner.alt"
                                                                                                default="Loading&hellip;"/></div>
            </div>
            <g:layoutBody/>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span12 cap-footer">
            <b>Version:</b> ${grailsApplication?.metadata['app.version']} <b>branch:</b> ${grailsApplication?.metadata['git.branch.name']} <b>revision:</b> ${grailsApplication?.metadata['git.branch.version']}
        </div>
    </div>
</div>

<r:layoutResources/>
</body>
</html>