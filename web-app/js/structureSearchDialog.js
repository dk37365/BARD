var adjustMarvinSketchWindow = function () {
    var width = window.innerWidth / 2;
    var height = window.innerHeight / 2;
//    $('#MarvinSketch').css('width', width)
//    $('#MarvinSketch').css('height', height)
    document.MarvinSketch.width = width
    document.MarvinSketch.height = height
}

//$(window).resize(alert("Adjusting"));
$(window.parent).resize(adjustMarvinSketchWindow);

$(document).ready(function () {
    $('#modalDiv').modal({
        show:false
    });
    $('#modalDiv').css('width', 'auto') //Disable the default width=560px from bootstrap.css
//    $('#modalDiv').on('show', adjustMarvinSketchWindow);
    $('#structureSearchButton').click(function () {
        var structureSearchTypeSelected = $('input:radio[name=structureSearchType]:checked').val();
        var marvinSketch = $('#MarvinSketch')[0];
        var smiles = marvinSketch.getMol('smiles');

        //construct the query into a form that we want
        var constructedSearch = structureSearchTypeSelected + ":" + smiles;
        $('#searchString').attr('value', constructedSearch);
        $('#searchForm').submit();

    });
    // TODO Write method to get search type and smiles from searchString and push it into marvin
//    $('#modalDiv').on('show', function() {
//        var searchString = $('#searchString').val();
//        var regex = '(\w+):[\s]?(\S+)';
//        var searchType = regex.
//        var allSearchOptions = [];
//        $("[name=structureSearchType]").each(function() {
//            allSearchOptions.push($(this).val());
//        });
//
//    });
});
