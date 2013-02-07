package bardqueryapi

import bard.core.Value
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.compounds.Promiscuity
import bard.core.rest.spring.util.StructureSearchParams
import grails.plugins.springsecurity.Secured
import molspreadsheet.MolecularSpreadSheetService
import org.apache.commons.lang.StringUtils
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException

import javax.servlet.http.HttpServletResponse

/**
 *
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 6/8/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Mixin(SearchHelper)
@Mixin(InetAddressUtil)
@Secured(['isFullyAuthenticated()'])
class BardWebInterfaceController {
    def shoppingCartService
    BardUtilitiesService bardUtilitiesService
    IQueryService queryService
    MolecularSpreadSheetService molecularSpreadSheetService
    MobileService mobileService
    List<SearchFilter> filters = []

    //An AfterInterceptor to handle mobile-view routing.
    def afterInterceptor = [action: this.&handleMobile]


    protected handleMobile(model, modelAndView) {
        if (modelAndView && mobileService.detect(request)) {
            String newView = '/mobile' + modelAndView.viewName
            if (mobileService.gspExists(newView)) {
                modelAndView.viewName = newView
            }
            else {
                modelAndView.viewName = "/mobile/bardWebInterface/missingPageError"
            }
        }
    }

    Boolean isMobile() {
        return mobileService.detect(request)
    }

    def index() {
    }

    def search() {
        flash.searchString = params.searchString
        redirect(action: 'searchResults')
    }

    def searchResults() {
    }

    def showExperiment(Long id, String normalizeYAxis, String activityOutcome) {

        if (isHTTPBadRequest(id, 'Experiment ID is a required Field', bardUtilitiesService.username)) {
            return
        }

        try {
            Map<String, Integer> searchParams = handleSearchParams()
            final Integer top = searchParams.top
            final Integer skip = searchParams.skip
            NormalizeAxis normalizeAxis = normalizeYAxis ? normalizeYAxis as NormalizeAxis : NormalizeAxis.Y_NORM_AXIS
            ActivityOutcome outcome = activityOutcome ? activityOutcome as ActivityOutcome : ActivityOutcome.ALL

            final Map experimentDataMap = queryService.findExperimentDataById(id, top, skip, normalizeAxis, bardqueryapi.ActivityOutcome.ALL)
            final Map modelMap = [experimentId: params.id, experimentDataMap: experimentDataMap]
            if (request.getHeader('X-Requested-With') == 'XMLHttpRequest') {  //if ajax then render template
                render(template: 'experimentResultData', model: modelMap)
                return
            }
            //this should do a full page reload
            render(view: 'showExperiment', model: modelMap)
        }
        catch (HttpClientErrorException httpClientErrorException) { //we are assuming that this is a 404, even though it could be a bad request
            String message = "Experiment with ID ${id} does not exists"
            handleClientInputErrors(httpClientErrorException, message, bardUtilitiesService.username)
        }
        catch (Exception ee) {

            String message = "Problem finding Experiment ${id}"
            log.error(message + getUserIpAddress(bardUtilitiesService.username), ee)

            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "${message}")
        }
    }

    def promiscuity(Long cid) {

        if (isHTTPBadRequest(cid, "A CID is required in order to get a promiscuity score", bardUtilitiesService.username)) {
            return
        }
        //Get the Promiscuity score for this CID
        try {
            Map results = queryService.findPromiscuityForCID(cid)
            if (results.status == 200) {
                final Promiscuity promiscuity = results.promiscuityScore
                render(template: 'promiscuity', model: [scaffolds: promiscuity.promiscuityScaffolds])
            }
            else { //status code of NOT OK returned. Usually CID has no promiscuity score
                log.error(results.message + getUserIpAddress(bardUtilitiesService.username))

                return response.sendError(results.status,
                        "${results.message}")
            }
        } catch (HttpClientErrorException httpClientErrorException) { //we are assuming that this is a 404, even though it could be a bad request
            String message = "No promiscuity score for Compound with CID: ${cid}"
            handleClientInputErrors(httpClientErrorException, message, bardUtilitiesService.username)

        } catch (Exception ee) { //error is thrown
            String errorMessage = "A server side error occurred and we could not retrieve a promiscuity score for ${cid}"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), ee)


            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "${errorMessage}")
        }
    }
    //================ Search By IDs ================================

    /**
     *
     * Given a list of Compound ids, invoke this method
     */
    def searchCompoundsByIDs(SearchCommand searchCommand) {

        if (isHTTPBadRequest(searchCommand.searchString, 'Search String is required', bardUtilitiesService.username)) {
            return
        }

        try {

            String originalSearchString = searchCommand.searchString
            final String[] searchStringSplit = searchCommand.searchString.split(":")
            if (searchStringSplit.length == 2) {  //if the search string is of the form ADID:1234,3456...
                final String searchTypeString = searchStringSplit[0]
                final String ids = searchStringSplit[1]
                handleIdSearchInput(searchTypeString, ids, "CID", "Input String start with CID:", "Please enter CIDs after the string CID:")
                searchCommand.searchString = ids
            }
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)

            Map<String, Integer> searchParams = handleSearchParams()
            int top = searchParams.top
            int skip = searchParams.skip

            //strip out all spaces
            final List<Long> compoundIds = searchStringToIdList(searchCommand.searchString)

            final List<Long> cids = []
            for (def id : compoundIds) {
                cids.add(new Long(id))
            }

            Map compoundAdapterMap = this.queryService.searchCompoundsByCids(cids, top, skip, searchFilters)
            List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters

            render(template: 'compounds', model: [
                    compoundAdapters: compoundAdapters,
                    facets: compoundAdapterMap.facets,
                    nhits: compoundAdapterMap.nHits,
                    searchString: "${originalSearchString}",
                    appliedFilters: getAppliedFilters(searchFilters, compoundAdapterMap.facets)
            ]
            )
        }
        catch (HttpClientErrorException httpClientErrorException) { //we are assuming that this is a 404, even though it could be a bad request
            final String message = "Search String ${searchCommand.searchString} Not found"
            handleClientInputErrors(httpClientErrorException, message, bardUtilitiesService.username)
        }
        catch (Exception exp) {
            final String errorMessage = "Compound search has encountered an Internal Error. Please try again."
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), exp)

            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    errorMessage)
        }
    }
/**
 * Given a list of assays ids, invoke this action
 */
    def searchAssaysByIDs(SearchCommand searchCommand) {
        if (isHTTPBadRequest(searchCommand.searchString, 'Search String is required', bardUtilitiesService.username)) {
            return
        }
        try {
            String originalSearchString = searchCommand.searchString
            final String[] searchStringSplit = searchCommand.searchString.split(":")
            if (searchStringSplit.length == 2) {  //if the search string is of the form ADID:1234,3456...
                final String searchTypeString = searchStringSplit[0]
                final String ids = searchStringSplit[1]
                handleIdSearchInput(searchTypeString, ids, "ADID", "Input String start with ADID:", "Please enter Assay Ids after the string ADID:")
                searchCommand.searchString = ids
            }
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)

            Map<String, Integer> searchParams = handleSearchParams()
            int top = searchParams.top
            int skip = searchParams.skip

            final List<Long> adids = searchStringToIdList(searchCommand.searchString)

            final List<Long> capIds = []
            for (def id : adids) {
                capIds.add(new Long(id))
            }

            final Map assayAdapterMap = this.queryService.findAssaysByCapIds(capIds, top, skip, searchFilters)
            render(template: "assays", model: [
                    assayAdapters: assayAdapterMap.assayAdapters,
                    facets: assayAdapterMap.facets,
                    nhits: assayAdapterMap.nHits,
                    searchString: "${originalSearchString}",
                    appliedFilters: getAppliedFilters(searchFilters, assayAdapterMap.facets)])
        }
        catch (HttpClientErrorException httpClientErrorException) { //we are assuming that this is a 404, even though it could be a bad request
            final String message = "Search String ${searchCommand.searchString} Not found"
            handleClientInputErrors(httpClientErrorException, message, bardUtilitiesService.username)
        }
        catch (Exception exp) {
            final String errorMessage = "Error searching for assays by IDs"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), exp)

            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "${errorMessage}")
        }
    }
/**
 * Given a list of ProjectSearchResult ids, invoke this action
 */
    def searchProjectsByIDs(SearchCommand searchCommand) {
        if (isHTTPBadRequest(searchCommand.searchString, 'Search String is required for project search by Ids', bardUtilitiesService.username)) {
            return
        }
        try {

            String originalSearchString = searchCommand.searchString
            final String[] searchStringSplit = searchCommand.searchString.split(":")

            if (searchStringSplit.length == 2) {  //if the search string is of the form PID:1234,3456...
                final String searchTypeString = searchStringSplit[0]
                final String ids = searchStringSplit[1]
                handleIdSearchInput(searchTypeString, ids, "PID", "Input String start with PID:", "Please enter Project Ids after the string PID:")
                //assign the list of ids only to the command object
                searchCommand.searchString = ids
            }
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)

            Map<String, Integer> searchParams = handleSearchParams()
            int top = searchParams.top
            int skip = searchParams.skip

            final List<Long> projectIds = searchStringToIdList(searchCommand.searchString)
            final List<Long> capIds = []
            for (def id : projectIds) {
                capIds.add(new Long(id))
            }
            Map projectAdapterMap = this.queryService.findProjectsByCapIds(capIds, top, skip, searchFilters)
            render(template: 'projects', model: [
                    projectAdapters: projectAdapterMap.projectAdapters,
                    facets: projectAdapterMap.facets,
                    nhits: projectAdapterMap.nHits,
                    searchString: "${originalSearchString}",
                    appliedFilters: getAppliedFilters(searchFilters, projectAdapterMap.facets)])
        }
        catch (HttpClientErrorException httpClientErrorException) { //we are assuming that this is a 404, even though it could be a bad request
            final String message = "Search String ${searchCommand.searchString} Not found"
            handleClientInputErrors(httpClientErrorException, message, bardUtilitiesService.username)

        }
        catch (Exception exp) {
            final String errorMessage = 'Error while searching project by ids : ' + searchCommand.searchString
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    "${errorMessage}")
        }

    }

//=================== Structure Searches (Exact, Similarity, SubStructure, Exact and SupeStructure Searches ===================

/**
 *
 * Do structure searches
 */
    def searchStructures(SearchCommand searchCommand) {

        if (isHTTPBadRequest(searchCommand?.searchString, "Search String is required, must be of the form StructureSearchType:Smiles", bardUtilitiesService.username)) {
            return
        }
        try {
            final Map map = handleStructureSearch(queryService, searchCommand)
            if (map) {
                render(template: 'compounds', model: map)
            }
            else {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND)
            }
        }
        catch (HttpClientErrorException httpClientErrorException) { //we are assuming that this is a 404, even though it could be a bad request
            final String message = "Search String ${searchCommand.searchString} Not found"
            handleClientInputErrors(httpClientErrorException, message, bardUtilitiesService.username)
        }
        catch (Exception exp) {
            final String errorMessage = "Structure search has encountered an error:\n${exp.message}"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), exp)
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    errorMessage)
        }
    }
/**
 *
 * @param cid - Given a single CID
 *
 */
    def findSubstanceIds(Long cid) {
        Long compoundId = cid ?: params.id as Long//if '' param is provided, use that; otherwise, try the default id one
        if (isHTTPBadRequest(compoundId, "CID required to find SIDS", bardUtilitiesService.username)) {
            return
        }

        try {

            final List<Long> sids = this.queryService.findSubstancesByCid(compoundId)
            render(template: "substanceIds", model: [sids: sids])

        }
        catch (HttpClientErrorException httpClientErrorException) {
            String message = "Could not find substances with CID ${cid}"
            handleClientInputErrors(httpClientErrorException, message, bardUtilitiesService.username)
        }
        catch (Exception exp) {
            final String errorMessage = "Show compound page has encountered an error while trying to fetch SIDs:\n${exp.message} "
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    errorMessage)
        }
    }
//============================================ Show Pages ===================================================================

/**
 *
 * @param cid - Given a single CID
 *
 */
    def showCompound(Integer cid) {
        Integer compoundId = cid ?: params.id as Integer//if '' param is provided, use that; otherwise, try the default id one
        if (isHTTPBadRequest(compoundId, "Compound ID is required", bardUtilitiesService.username)) {
            return
        }
        try {

            final CompoundAdapter compoundAdapter = this.queryService.showCompound(compoundId)
            if (compoundAdapter) {
                render(view: "showCompound", model: [compound: compoundAdapter, searchString: params.searchString])
            }
            else {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND)
            }
        }
        catch (HttpClientErrorException httpClientErrorException) {
            String message = "Could not find Compound with CID ${cid}"
            handleClientInputErrors(httpClientErrorException, message, bardUtilitiesService.username)
        }
        catch (Exception exp) {
            final String errorMessage = "Show compound page has encountered an error:\n${exp.message}"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    errorMessage)
        }
    }
/**
 *
 * @param assayProtocolId
 */
    def showAssay(Integer assayProtocolId) {
        Integer assayId = assayProtocolId ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one
        if (isHTTPBadRequest(assayId, "Assay Id is required", bardUtilitiesService.username)) {
            return
        }
        try {
            Map assayMap = this.queryService.showAssay(assayId)
            AssayAdapter assayAdapter = assayMap.assayAdapter
            if (assayAdapter) {
                render(view: "showAssay", model: [
                        assayAdapter: assayAdapter,
                        experiments: assayMap.experiments,
                        projects: assayMap.projects, searchString: params.searchString
                ]
                )
            }
            else {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Could not find Assay Id ${assayId}")
            }
        }
        catch (HttpClientErrorException httpClientErrorException) {
            String message = "Could not find Assay with ID ${assayId}"
            handleClientInputErrors(httpClientErrorException, message, bardUtilitiesService.username)
        }
        catch (Exception exp) {
            final String errorMessage = "Search For Assay Id ${assayId}:\n${exp.message}"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(), errorMessage)
        }

    }

    def showProject(Integer projectId) {
        Integer projId = projectId ?: params.id as Integer//if 'project' param is provided, use that; otherwise, try the default id one
        if (isHTTPBadRequest(projId, "Project Id is required", bardUtilitiesService.username)) {
            return
        }

        try {

            Map projectMap = this.queryService.showProject(projId)
            if (projectMap) {
                ProjectAdapter projectAdapter = projectMap.projectAdapter
                render(view: "showProject", model: [projectAdapter: projectAdapter, experiments: projectMap.experiments, assays: projectMap.assays,
                        searchString: params.searchString])
            }
            else {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Could not find Project Id ${projId}")
            }
        }
        catch (HttpClientErrorException httpClientErrorException) {
            String message = "Could not find Project with ID ${projId}"
            handleClientInputErrors(httpClientErrorException, message, bardUtilitiesService.username)
        }
        catch (Exception exp) {
            final String errorMessage = "Search For Project By Id ${projectId}:\n${exp.message}"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(), errorMessage)
        }

    }

//================= Free Text Searches ======================================================
/**
 *
 * Find Compounds annotated with Search String
 */
    def searchCompounds(SearchCommand searchCommand) {

        handleCompoundSearches(this.queryService, searchCommand, isMobile(), bardUtilitiesService.username)

    }
/**
 *
 * Find Assays annotated with Search String
 */
    def searchAssays(SearchCommand searchCommand) {

        handleAssaySearches(this.queryService, searchCommand, isMobile(), this.bardUtilitiesService.username)
    }
/**
 *
 * Find Projects annotated with Search String
 */
    def searchProjects(SearchCommand searchCommand) {
        handleProjectSearches(this.queryService, searchCommand, isMobile(), this.bardUtilitiesService.username)
    }

/**
 * Autocomplete
 */
    def autoCompleteAssayNames() {
        try {
            final List<Map<String, String>> assayNames = this.queryService.autoComplete(params.term)
            // return assayNames as JSON
            render(contentType: "text/json") {
                assayNames
            }
        }
        catch (Exception exp) {
            final String errorMessage = "AutoComplete encoutered an error :\n${exp.message}"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR.intValue(),
                    errorMessage)
        }
    }

    def turnoffMobileExperience() {
        session.putValue('mobileExperienceDisabled', true)
        redirect(action: 'index')
    }

    def showProbeList() {
        Map results = queryService.showProbeList()
        if (params.searchString) {
            results.put("searchString", params.searchString)
        }
        String template = isMobile() ? "/mobile/bardWebInterface/compounds" : "compounds"
        render(template: template,
                model: [
                        compoundAdapters: results.compoundAdapters,
                        facets: [],
                        nhits: results.compoundAdapters?.size(),
                        appliedFilters: [:]
                ]
        )

    }

}
/**
 * We would use this helper class as Mixin for
 * the RestController
 */
@Mixin(InetAddressUtil)
class SearchHelper {
    void handleIdSearchInput(String searchTypeString, String ids, String prefix, String messageForPrefix, String messageForIds) {
        if (!prefix.equals(searchTypeString.trim().toUpperCase())) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, messageForPrefix)
        }
        if (StringUtils.isBlank(ids)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, messageForIds)
        }
    }

    Map handleStructureSearch(final bardqueryapi.IQueryService queryService, final SearchCommand searchCommand) {
        final Map structureSearchResultsMap = [:]
        final Map<String, Integer> searchParams = handleSearchParams()
        final Integer top = searchParams.top
        final Integer skip = searchParams.skip
        final Integer nhits = searchParams.nhits

        removeDuplicatesFromSearchString(searchCommand)
        final String[] searchStringSplit = searchCommand.searchString.split(":")
        if (searchStringSplit.length == 2) {
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)

            final String searchTypeString = searchStringSplit[0]
            final String inputAfterColon = searchStringSplit[1]
            //if smiles is a number then assume that is is a CID
            //we make the first character capitalized to match the ENUM
            final StructureSearchParams.Type searchType = searchTypeString.toLowerCase().capitalize() as StructureSearchParams.Type
            Map compoundAdapterMap = null
            if (inputAfterColon.isInteger()) { //we assume that this is a CID
                compoundAdapterMap = queryService.structureSearch(new Integer(inputAfterColon), searchType, searchFilters, top, skip, nhits)
            } else {
                compoundAdapterMap = queryService.structureSearch(inputAfterColon, searchType, searchFilters, top, skip, nhits)
            }
            List<CompoundAdapter> compoundAdapters = compoundAdapterMap.compoundAdapters
            structureSearchResultsMap = [
                    compoundAdapters: compoundAdapters,
                    facets: compoundAdapterMap.facets,
                    nhits: compoundAdapterMap.nHits,
                    searchString: "${searchCommand.searchString}",
                    appliedFilters: getAppliedFilters(searchFilters, compoundAdapterMap.facets)
            ]
        }
        return structureSearchResultsMap
    }

    def handleAssaySearches(final bardqueryapi.IQueryService queryService, final SearchCommand searchCommand, Boolean isMobile = false, String username) {
        if (isHTTPBadRequest(searchCommand.searchString, "Search String required for Assay searches", username)) {
            return
        }

        try {
            removeDuplicatesFromSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)
            final String searchString = searchCommand.searchString.trim()

            Map<String, Integer> searchParams = handleSearchParams()
            int top = searchParams.top
            int skip = searchParams.skip

            final Map assaysByTextSearchResultsMap = queryService.findAssaysByTextSearch(searchString, top, skip, searchFilters)
            String template = isMobile ? "/mobile/bardWebInterface/assays" : "assays"
            render(template: template, model: [
                    assayAdapters: assaysByTextSearchResultsMap.assayAdapters,
                    facets: assaysByTextSearchResultsMap.facets,
                    nhits: assaysByTextSearchResultsMap.nHits,
                    searchString: "${searchString}",
                    appliedFilters: getAppliedFilters(searchFilters, assaysByTextSearchResultsMap.facets)])
        }
        catch (HttpClientErrorException httpClientErrorException) {
            String message = "Could not find Assay with given search String ${searchCommand.searchString}"
            handleClientInputErrors(httpClientErrorException, message, username)
        }
        catch (Exception exp) {
            log.error("Error performing assay search." + getUserIpAddress(username), exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Assay search has encountered an error:\n${exp.message}")
        }

    }

    def handleCompoundSearches(final bardqueryapi.IQueryService queryService, final SearchCommand searchCommand, Boolean isMobile = false, String username) {

        if (isHTTPBadRequest(searchCommand.searchString, "Search String required for Compound searches", username)) {
            return
        }
        try {
            removeDuplicatesFromSearchString(searchCommand)
            List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)

            final String searchString = searchCommand.searchString.trim()
            final Map<String, Integer> searchParams = handleSearchParams()
            final int top = searchParams.top
            final int skip = searchParams.skip
            final Map compoundsByTextSearchResultsMap = queryService.findCompoundsByTextSearch(searchString, top, skip, searchFilters)
            String template = isMobile ? "/mobile/bardWebInterface/compounds" : "compounds"
            render(template: template,
                    model: [
                            compoundAdapters: compoundsByTextSearchResultsMap.compoundAdapters,
                            facets: compoundsByTextSearchResultsMap.facets,
                            nhits: compoundsByTextSearchResultsMap.nHits,
                            searchString: "${searchString}",
                            appliedFilters: getAppliedFilters(searchFilters, compoundsByTextSearchResultsMap.facets)]
            )
        }
        catch (HttpClientErrorException httpClientErrorException) {
            String message = "Could not find Compound with given search String ${searchCommand.searchString}"
            handleClientInputErrors(httpClientErrorException, message, username)
        }
        catch (Exception exp) {
            log.error("Error performing compound search." + getUserIpAddress(username), exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Compound search has encountered an error:\n${exp.message}")
        }
    }

    def handleProjectSearches(final bardqueryapi.IQueryService queryService, final SearchCommand searchCommand,
                              Boolean isMobile = false, String username) {

        if (isHTTPBadRequest(searchCommand.searchString, "Search String required for Project searches", username)) {
            return
        }

        try {
            removeDuplicatesFromSearchString(searchCommand)
            final List<SearchFilter> searchFilters = searchCommand.appliedFilters ?: []
            queryService.findFiltersInSearchBox(searchFilters, searchCommand.searchString)
            final String searchString = searchCommand.searchString.trim()
            Map<String, Integer> searchParams = handleSearchParams()
            int top = searchParams.top
            int skip = searchParams.skip
            final Map projectsByTextSearch = queryService.findProjectsByTextSearch(searchString, top, skip, searchFilters)
            String template = isMobile ? "/mobile/bardWebInterface/projects" : "projects"
            render(template: template, model: [
                    projectAdapters: projectsByTextSearch.projectAdapters,
                    facets: projectsByTextSearch.facets,
                    nhits: projectsByTextSearch.nHits,
                    searchString: "${searchString}",
                    appliedFilters: getAppliedFilters(searchFilters, projectsByTextSearch.facets)])
        }
        catch (HttpClientErrorException httpClientErrorException) {
            final String message = "Could not find Project with given search String ${searchCommand.searchString}"
            handleClientInputErrors(httpClientErrorException, message, username)
        }
        catch (Exception exp) {
            log.error("Error performing project search." + getUserIpAddress(username), exp)
            return response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "ProjectSearchResult search has encountered an error:\n${exp.message}")
        }
    }

    protected void handleClientInputErrors(HttpClientErrorException httpClientErrorException, String message, String user) {
        if (httpClientErrorException.statusCode == HttpStatus.NOT_FOUND) {
            log.warn(message + getUserIpAddress(user), httpClientErrorException)
        } else {
            log.error(message + getUserIpAddress(user), httpClientErrorException)
        }
        response.sendError(httpClientErrorException.statusCode.value(),
                "${message}")
    }

    protected boolean isHTTPBadRequest(def input, String message, String user) {
        if (!input) {//this is a bad request
            log.error(message + getUserIpAddress(user))

            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "${message}")
            return true
        }
        return false
    }
    /**
     *
     * @param relativePath for example /search/compounds
     */
    public Map<String, Integer> handleSearchParams() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.offset = params.offset ? params.int('offset') : 0
        params.nhits = params.nhits ? params.int('nhits') : 0
        return [top: new Integer(params.max), skip: new Integer(params.offset), nhits: new Integer(params.nhits)]
    }
    /**
     * Strip out all empty spaces, split on ',' and return as a list of longs
     */
    public List<Long> searchStringToIdList(String searchString) {
        //we also want to remove duplicates
        final Set<Long> ids = new HashSet<Long>()
        //first split using spaces
        List<String> splitSpaces = searchString.split("\\s+") as List<String>
        for (String id : splitSpaces) {
            String trimmedString = id.trim()
            ids.addAll(trimmedString.split(",") as Set<Long>)
        }
        return ids as List<Long>
    }

    /**
     * we need to remove duplicates, these duplicates occur we read the search string
     * both from the hidden field in the apply filters form
     * and from the param object during paging
     * this would go away if NCGC supports filters for all searches, then we would
     * not need to add the search string to the paging object
     */
    protected void removeDuplicatesFromSearchString(SearchCommand searchCommand) {
        List<String> searchCommandSplit = []
        if (!searchCommand.searchString.matches(/[^"]*"[^"]+"/)) {
            // This is a little complicated.
            // If the search string contains a quote-delimited string anywhere in it, take the search string as it is.
            // Otherwise, split it on commas, then remove the duplicates.
            // We could do this with a Set instead of performing unique() on the list, but then the order gets a little scrambled.
            // Regex: Zero or more things that aren't ", then ", then one or more things that aren't " (so empty strings are not allowed), then "
            searchCommandSplit.addAll(searchCommand.searchString.trim().split(",") as List<String>)
            searchCommand.searchString = searchCommandSplit.unique().join(",")
        }
        params.searchString = searchCommand.searchString

    }

    Map getAppliedFilters(List<SearchFilter> searchFilters, Collection<Value> facets) {
        //Includes all the applied search-filters (selected previously) that were also returned with the new filtering faceting.
        //1. Check if the facet group-names (a.k.a., parent.id) match
        //2. Check if the facet/filter name (a.k.a., child.id) match
        List<SearchFilter> appliedFiltersAlreadyInFacets = getAppliedFiltersAlreadyInFacets(searchFilters, facets)

        //Groups all the applied search-filters in facets into a parent-facet/children-facets map. We use this group to display the applied search filters WITHIN the facet groups
        //If the facet-group exists but the applied-filter's corresponding facet didn't come back after the filtering, we still want to display the filter in its appropriate (facet) group, if we can.
//        Map appliedFiltersNotInFacetsGrouped = ((searchFilters ?: []) - appliedFiltersAlreadyInFacets) ?
//            (searchFilters - appliedFiltersAlreadyInFacets).groupBy { SearchFilter filter -> filter.filterName.trim()} : [:]
        List<SearchFilter> searchFiltersNotYetApplied = (searchFilters ?: []) - appliedFiltersAlreadyInFacets

        Map<String, List<SearchFilter>> appliedFiltersNotInFacetsGrouped = groupSearchFilters(searchFiltersNotYetApplied)

        //Includes all the applied filters we know would not have any facet group since no facet in this group came back after the filtering was applied.
        //We need to group these filters, rebuild their groups (parent) and display them next to the facets
        List<SearchFilter> appliedFiltersDisplayedOutsideFacets = getAppliedFiltersDisplayedOutsideFacets(searchFiltersNotYetApplied, facets)

        //Group all the applied filters so we can use the keys as group (parent) names.
        Map<String, List<SearchFilter>> appliedFiltersDisplayedOutsideFacetsGrouped = groupSearchFilters(appliedFiltersDisplayedOutsideFacets)

        return [
                searchFilters: searchFilters,
                appliedFiltersDisplayedOutsideFacetsGrouped: appliedFiltersDisplayedOutsideFacetsGrouped,
                appliedFiltersNotInFacetsGrouped: appliedFiltersNotInFacetsGrouped
        ]
    }

    //Compares based on child (facet) name between SearchFilter and facet
    protected List<SearchFilter> getAppliedFiltersAlreadyInFacets(List<SearchFilter> searchFilters, Collection<Value> facets) {
        List<SearchFilter> appliedFiltersAlreadyInFacets = searchFilters.findAll {
            SearchFilter filter ->
            Value parent = facets.find {Value parent ->
                parent.id.trim().equalsIgnoreCase(filter.filterName.trim())
            }
            return parent?.children?.find {
                Value child ->
                child.id.trim().equalsIgnoreCase(filter.filterValue.trim().replace('"', ''))
            }
        }

        return appliedFiltersAlreadyInFacets
    }

    protected Map<String, List<SearchFilter>> groupSearchFilters(List<SearchFilter> searchFiltersNotYetApplied) {
        Map<String, List<SearchFilter>> appliedFiltersNotInFacetsGrouped = [:]
        if (searchFiltersNotYetApplied) {
            appliedFiltersNotInFacetsGrouped = searchFiltersNotYetApplied.groupBy { SearchFilter filter -> filter.filterName.trim()}
        }

        return appliedFiltersNotInFacetsGrouped
    }

    //Compares based on parent (group) name between SearchFilter and facet
    protected List<SearchFilter> getAppliedFiltersDisplayedOutsideFacets(List<SearchFilter> searchFiltersNotYetApplied, Collection<Value> facets) {
        List<SearchFilter> appliedFiltersDisplayedOutsideFacets = []
        if (searchFiltersNotYetApplied) {
            appliedFiltersDisplayedOutsideFacets = searchFiltersNotYetApplied.findAll {SearchFilter filter ->
                //filter.filterName is not in any of the parents' ids
                return !(facets.find { Value parent -> parent.id.trim().equalsIgnoreCase(filter.filterName.trim())})
            }
        }

        return appliedFiltersDisplayedOutsideFacets
    }


}
