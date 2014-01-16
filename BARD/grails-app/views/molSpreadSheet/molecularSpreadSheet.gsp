<%@ page import="molspreadsheet.SpreadSheetActivityStorage; molspreadsheet.MolSpreadSheetData; molspreadsheet.SpreadSheetActivity; molspreadsheet.MolecularSpreadSheetService; bardqueryapi.FacetFormType" %>
<%@ page import="molspreadsheet.MolSpreadSheetCellType; molspreadsheet.MolSpreadSheetCell;" %>
<%@ page import="com.metasieve.shoppingcart.ShoppingCartService;" %>
<r:require module="export"/>
<html>
<head>
    <meta name="layout" content="basic"/>
    <title>Molecular Spreadsheet</title>
    <r:require modules="molecularSpreadSheet,promiscuity,compoundOptions"/>
    %{--Fixes the problem where Twitter Bootstrap's popover box was too narrow--}%
    <style type="text/css">
    div.tooltip-inner {
        max-width: 500px;
    }
    </style>
</head>

<body>
<div class="row-fluid">
    <div class="span12 page-header">
        <div class="span12 pagination-centered">
            <h1>Molecular Spreadsheet</h1>
        </div>
    </div>
</div>

<div id="molecularSpreadSheet"
     href="${createLink(controller: 'molSpreadSheet', action: 'molecularSpreadSheet', params: [pid: pid, cid: cid, norefresh: norefresh, transpose: transpose, ChangeNorm: ChangeNorm, showActive: showActive])}">
</div>

<g:render template="spreadsheetColorKey"/>

</body>
</html>