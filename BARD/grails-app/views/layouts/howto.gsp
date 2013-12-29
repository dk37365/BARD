<%@ page import="bardqueryapi.IDSearchType" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>BARD: <g:layoutTitle default="BioAssay Research Database"/></title>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link href='https://fonts.googleapis.com/css?family=Lato:400,400italic,700,700italic,900,900italic,300,300italic'
          rel='stylesheet' type='text/css'>
    <r:require modules="bardHomepage,idSearch,jquerynotifier,downtime,autocomplete"/>
    <!--[if lt IE 9]><link rel="stylesheet" href="${resource(dir: 'css/bardHomepage', file: 'ieBardHomepage.css')}" media="screen" /><![endif]-->
    <!--[if IE]><script src="${resource(dir: 'js/bardHomepage', file: 'ie.js')}" /></script><![endif]-->

    <g:layoutHead/>

    <r:layoutResources/>

    <style type="text/css">
    @media (min-width: 768px) {
        /* start of modification for 5 columns.  Must follow after bootstrap definitions */
        .fivecolumns .span2 {
            width: 18.2%;
            *width: 18.2%;
        }
    }

    @media (min-width: 1200px) {
        .fivecolumns .span2 {
            width: 17.9%;
            *width: 17.8%;
        }
    }

    @media (min-width: 768px) and (max-width: 979px) {
        .fivecolumns .span2 {
            width: 17.7%;
            *width: 17.7%;
        }
    }
    </style>

    <ga:trackPageview/>

</head>

<body>
<noscript>
    <a href="http://www.enable-javascript.com/" target="javascript">
        <img src="${resource(dir: 'images', file: 'enable_js.png')}"
             alt="Please enable JavaScript to access the full functionality of this site."/>
    </a>
</noscript>

<div id="wrapper">
    %{--The control area at the top of the page is all contained within this header--}%
    <g:render template="/bardWebInterface/homepageHeader"/>

    <div class="container-fluid">
        <div class="row-fluid">
            <div class="span12">
                <div class="spinner-container">
                    <div id="spinner" class="spinner" style="display:none; color: blue;"><g:message code="spinner.alt"
                                                                                                    default=""/></div>
                </div>
                <g:layoutBody/>
            </div>
        </div>
    </div>

    <g:render template="/layouts/templates/footer"/>

</div>
<r:layoutResources/>
</body>
</html>
