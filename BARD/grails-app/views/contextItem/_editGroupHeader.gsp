<g:set var="buttonId" value="${'addNewBtn-'+cardSection.replaceAll(/( |>)/, '-')}"/>
<div class="row-fluid">
    <button id="${buttonId}" class="btn add-card-button" cardsection="${cardSection}">Add New Card</button>
</div>
