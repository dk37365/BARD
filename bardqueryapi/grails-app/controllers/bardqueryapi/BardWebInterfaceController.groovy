package bardqueryapi

import elasticsearchplugin.ElasticSearchService
import elasticsearchplugin.ESAssay
import elasticsearchplugin.ESCompound
import wslite.json.JSONObject
import chemaxon.formats.MolImporter
import chemaxon.struc.Molecule
import elasticsearchplugin.QueryExecutorService

/**
 * TODO: Unify the use of JSONObject
 * Created with IntelliJ IDEA.
 * User: gwalzer
 * Date: 6/8/12
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Mixin(AutoCompleteHelper)
class BardWebInterfaceController {

    ElasticSearchService elasticSearchService
    QueryExecutorInternalService queryExecutorInternalService

    def index() {
        homePage()
    }

    def homePage() {
        render(view: "homePage", totalCompounds: 0, model: [assays: [], compounds: [], experiments: [], projects: []])
    }
    /**
     * TODO: This will require refactoring after this iteration
     * when we add more functionality
     * @return
     */
    def search() {
        def searchString = params.searchString?.trim()
        if (searchString) {
            Map<String, List> result = elasticSearchService.search(searchString)

            List<Map> assays = []
            for (ESAssay assay in result.assays) {
                String assayString = assay.toString()
                String bardAssayViewUrl = grailsApplication.config.bard.assay.view.url
                String showAssayResource = "${bardAssayViewUrl}/${assay.assayNumber}"
                def assayMap = [assayName: assayString, assayResource: showAssayResource, assayNumber: assay.assayNumber] as Map
                assays.add(assayMap)
            }

            Set<String> compounds = [] as Set
            for (ESCompound compound in result.compounds) {
                compounds.add(compound)
            }

            render(view: "homePage", model: [totalCompounds: compounds.size(), assays: assays as List<Map>, compounds: compounds.toList(), compoundHeaderInfo: result.compoundHeaderInfo, experiments: [], projects: []])
            return
        }
        flash.message = 'Search String is required'
        redirect(action: "homePage")
    }


    def showCompound(Integer cid) {
        Integer compoundId = cid ?: params.id as Integer//if 'assay' param is provided, use that; otherwise, try the default id one

        if (compoundId) {
            JSONObject compoundESDocument = elasticSearchService.getCompoundDocument(compoundId)
            JSONObject compoundJson = [cid: compoundESDocument?._id,
                    sids: compoundESDocument?._source?.sids,
                    probeId: compoundESDocument?._source?.probeId,
                    smiles: compoundESDocument?._source?.smiles] as JSONObject
            render(view: "showCompound", model: [compoundJson: compoundJson, compoundId: compoundId])
        }
        else {
            render "Compound ID (CID) parameter required"
        }
    }


    def autoCompleteAssayNames() {
        final String elasticSearchRootURL = grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl
        final List<String> assayNames = handleAutoComplete(this.elasticSearchService, elasticSearchRootURL)

        render(contentType: "text/json") {
            for (String assayName : assayNames) {
                element assayName
            }
            if (!assayNames) {
                element ""
            }
        }
    }


    def structureSearch(String smiles, String structureSearchType) {
        StructureSearchType searchType = structureSearchType as StructureSearchType
        String searchString = ''

        String ncgcSearchBaseUrl = grailsApplication.config.ncgc.server.structureSearchPlugin.root.url

        String searchModifiers
        switch (searchType) {
            case StructureSearchType.SUB_STRUCTURE:
                searchModifiers = '&type=sub'
                break
            case StructureSearchType.SIMILARITY:
                searchModifiers = "&type=sim&cutoff=0.9"
                break
            case StructureSearchType.EXACT_MATCH:
                searchModifiers = '&type=exact'
                break
            default:
                throw new RuntimeException("Undeifined structure-search type")
                break
        }

        String searchUrl = "${ncgcSearchBaseUrl}?q=${smiles}${searchModifiers}&method=search"
        String resultSdf = queryExecutorInternalService.executeGetRequestString(searchUrl, null)

        InputStream smilesInputStream = new ByteArrayInputStream(resultSdf.getBytes());
        MolImporter molImporter = new MolImporter(smilesInputStream, 'sdf')
        List<Molecule> molecules = []
        Molecule molecule
        while (molecule = molImporter.read()) {
            molecules << molecule
        }

        if (molecules.isEmpty()) {
            flash.message = message(code: 'structure.search.nonFound', default: 'Sub-structure did not find any structure')
            render(view: 'homePage', model: [totalCompounds: 0, assays: [] as List<Map>, compounds: [], compoundHeaderInfo: '', experiments: [], projects: []])
            return
        }
        else {
            searchString = molecules*.moleculeName.join(' ')
            redirect(action: "search", params: ['searchString': searchString])
            return
        }
    }
}
/**
 * We would use this helper class as Mixin for
 * the RestController
 */
class AutoCompleteHelper {

    final String AUTO_COMPLETE_SEARCH_URL = "assays/_search"

    /**
     * The JSON representation that would be used for Autocompletion of assay names
     * We  return the first ten matched objects
     */
    final String ELASTIC_AUTO_COMPLETE_SEARCH = '''{
  "fields": ["name"],
  "query": {
    "query_string": {
      "default_field": "name",
      "query": "*"
    }
  },
  "size": 10
}
'''
    /**
     * Construct a query String query and pass it on to Elastic Search
     * @param elasticSearchService
     * @param elasticSearchRootURL
     * @return
     */
    protected List<String> handleAutoComplete(final ElasticSearchService elasticSearchService, final String elasticSearchRootURL) {
        final String urlToElastic = "${elasticSearchRootURL}/${AUTO_COMPLETE_SEARCH_URL}"
        String request = ELASTIC_AUTO_COMPLETE_SEARCH
        if (params?.term) {
            request = ELASTIC_AUTO_COMPLETE_SEARCH.replaceAll("\\*", "${params.term}*")

        }
        final JSONObject jsonObject = new JSONObject(request)

        final JSONObject responseObject = elasticSearchService.searchQueryStringQuery(urlToElastic, jsonObject)
        return responseObject?.hits?.hits.collect { it.fields }.collect { it.name }

    }
}

public enum StructureSearchType {
    EXACT_MATCH("Exact match"),
    SUB_STRUCTURE("Sub-structure"),
    SIMILARITY("Similarity");

    final String description

    StructureSearchType(String description) {
        this.description = description
    }

    String getDescription() {
        return this.description;
    }
}
