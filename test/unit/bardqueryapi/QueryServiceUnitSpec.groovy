package bardqueryapi

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTProjectService
import elasticsearchplugin.ElasticSearchService
import elasticsearchplugin.QueryExecutorService
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import wslite.json.JSONObject
import bard.core.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(QueryService)
class QueryServiceUnitSpec extends Specification {
    QueryServiceWrapper queryServiceWrapper
    QueryExecutorService queryExecutorService
    ElasticSearchService elasticSearchService
    RESTCompoundService restCompoundService
    RESTProjectService restProjectService
    RESTAssayService restAssayService
    final static String AUTO_COMPLETE_NAMES = '''
{
    "hits": {
        "hits": [
            {
                "fields": {
                    "name": "Broad Institute MLPCN Platelet Activation"
                }
            }
        ]
    }
  }
'''

    void setup() {
        queryExecutorService = Mock(QueryExecutorService.class)
        restCompoundService = Mock(RESTCompoundService.class)
        restProjectService = Mock(RESTProjectService.class)
        restAssayService = Mock(RESTAssayService.class)
        elasticSearchService = Mock(ElasticSearchService.class)
        queryServiceWrapper = Mock(QueryServiceWrapper.class)
        service.queryExecutorService = queryExecutorService
        service.elasticSearchService = elasticSearchService
        service.queryServiceWrapper = queryServiceWrapper
        service.elasticSearchRootURL = 'httpMock://'
        service.ncgcSearchBaseUrl = 'httpMock://'
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test compoundsToAdapters #label"() {
        when:
        final List<CompoundAdapter> foundCompoundAdapters = service.compoundsToAdapters(compounds)

        then:
        assert foundCompoundAdapters.size() == expectedCompoundAdapters.size()
        for (int index = 0; index < foundCompoundAdapters.size(); index++) {
            assert foundCompoundAdapters.get(index).name == expectedCompoundAdapters.get(index).name
        }

        where:
        label                | compounds                                | expectedCompoundAdapters
        "Single Compound"    | [new Compound("c1")]                     | [new CompoundAdapter(new Compound("c1"))]
        "Multiple Compounds" | [new Compound("c1"), new Compound("c2")] | [new CompoundAdapter(new Compound("c1")), new CompoundAdapter(new Compound("c2"))]
        "No Compounds"       | []                                       | []

    }

    void "test assaysToAdapters #label"() {
        when:
        final List<AssayAdapter> foundAssayAdapters = service.assaysToAdapters(assays)

        then:
        assert foundAssayAdapters.size() == expectedAssayAdapters.size()
        for (int index = 0; index < foundAssayAdapters.size(); index++) {
            assert foundAssayAdapters.get(index).name == expectedAssayAdapters.get(index).name
        }

        where:
        label             | assays                             | expectedAssayAdapters
        "Single Assay"    | [new Assay("c1")]                  | [new AssayAdapter(new Assay("c1"))]
        "Multiple Assays" | [new Assay("c1"), new Assay("c2")] | [new AssayAdapter(new Assay("c1")), new AssayAdapter(new Assay("c2"))]
        "No Assays"       | []                                 | []

    }

    void "test projectsToAdapters #label"() {
        when:
        final List<ProjectAdapter> foundProjectsAdapters = service.projectsToAdapters(projects)

        then:
        assert foundProjectsAdapters.size() == expectedProjectsAdapters.size()
        for (int index = 0; index < foundProjectsAdapters.size(); index++) {
            assert foundProjectsAdapters.get(index).name == expectedProjectsAdapters.get(index).name
        }

        where:
        label               | projects                               | expectedProjectsAdapters
        "Single Project"    | [new Project("c1")]                    | [new ProjectAdapter(new Project("c1"))]
        "Multiple Projects" | [new Project("c1"), new Project("c2")] | [new ProjectAdapter(new Project("c1")), new ProjectAdapter(new Project("c2"))]
        "No Projects"       | []                                     | []

    }

    void "test applySearchFiltersToSearchParams #label"() {
        when:
        service.applySearchFiltersToSearchParams(searchParams, searchFilters)

        then:
        if (searchFilters.size() > 0) {
            searchParams.filters.size() == searchFilters.size()
        }
        else {
            assert !searchParams.filters
        }

        where:
        label                     | searchParams       | searchFilters
        "No Search Filters"       | new SearchParams() | []
        "Multiple Search Filters" | new SearchParams() | [new SearchFilter("name1", "value1"), new SearchFilter("name2", "value2")]
        "Single Search Filter"    | new SearchParams() | [new SearchFilter("name1", "value1")]

    }

    void "test constructSearchParams #label"() {
        when:
        final SearchParams searchParams = service.constructSearchParams(searchString, top, skip, searchFilters)

        then:
        assert searchParams
        searchParams.skip == skip
        searchParams.top == top
        searchParams.query == searchString
        if (searchFilters.size() > 0) {
            searchParams.filters.size() == searchFilters.size()
        }
        else {
            assert !searchParams.filters
        }

        where:
        label                     | searchString | top | skip | searchFilters
        "No Search Filters"       | "stuff"      | 10  | 0    | []
        "Multiple Search Filters" | "stuff"      | 10  | 0    | [new SearchFilter("name1", "value1"), new SearchFilter("name2", "value2")]
        "Single Search Filter"    | "stuff"      | 10  | 10   | [new SearchFilter("name1", "value1")]

    }
    /**
     */
    void "test autoComplete #label"() {

        when:
        final List<String> response = service.autoComplete(term)

        then:
        elasticSearchService.searchQueryStringQuery(_, _) >> { jsonResponse }

        assert response == expectedResponse

        where:
        label                       | term  | jsonResponse                        | expectedResponse
        "Partial match of a String" | "Bro" | new JSONObject(AUTO_COMPLETE_NAMES) | ["Broad Institute MLPCN Platelet Activation"]
        "Empty String"              | ""    | new JSONObject()                    | []
    }
    /**
     */
    void "test handleAutoComplete #label"() {

        when:
        final List<String> response = service.handleAutoComplete(term)

        then:
        elasticSearchService.searchQueryStringQuery(_, _) >> { jsonResponse }
        assert response == expectedResponse

        where:
        label                       | term  | jsonResponse                        | expectedResponse
        "Partial match of a String" | "Bro" | new JSONObject(AUTO_COMPLETE_NAMES) | ["Broad Institute MLPCN Platelet Activation"]
        "Empty String"              | ""    | new JSONObject()                    | []
    }
    /**
     */
    void "test Show Compound #label"() {

        when: "Client enters a CID and the showCompound method is called"
        CompoundAdapter compoundAdapter = service.showCompound(compoundId)
        then: "The CompoundDocument is called"
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        restCompoundService.get(_) >> {compound}
        if (compound) {
            assert compoundAdapter
            assert compoundAdapter.compound
        } else {
            assert !compoundAdapter
        }

        where:
        label                       | compoundId       | compound
        "Return a Compound Adapter" | new Integer(872) | new Compound(name: "C1")
        "Unknown Compound"          | new Integer(872) | null
        "Null CompoundId"           | null             | null
    }



    void "test Show Project"() {
        when: "Client enters a project ID and the showProject method is called"
        ProjectAdapter foundProjectAdpater = service.showProject(projectId)
        then: "The Project document is displayed"
        queryServiceWrapper.getRestProjectService() >> { restProjectService }
        restProjectService.get(_) >> {project}
        if (project) {
            assert foundProjectAdpater
            assert foundProjectAdpater.project
        } else {
            assert !foundProjectAdpater
        }

        where:
        label                      | projectId        | project
        "Return a Project Adapter" | new Integer(872) | new Project(name: "C1")
        "Unknown Project"          | new Integer(872) | null
        "Null projectId"           | null             | null
    }

    void "test Show Assay"() {
        when: "Client enters a assay ID and the showAssay method is called"
        AssayAdapter foundAssayAdapter = service.showAssay(assayId)
        then: "The Assay document is displayed"
        queryServiceWrapper.getRestAssayService() >> { restAssayService }
        restAssayService.get(_) >> {assay}
        if (assay) {
            assert foundAssayAdapter
            assert foundAssayAdapter.assay
        } else {
            assert !foundAssayAdapter
        }
        where:
        label                     | assayId          | assay
        "Return an Assay Adapter" | new Integer(872) | new Assay(name: "C1")
        "Unknown Assay"           | new Integer(872) | null
        "Null assayId"            | null             | null

    }

    void "test findCompoundsByCIDs #label"() {
        when:
        Map responseMap = service.findCompoundsByCIDs(cids)
        then:
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        expectedNumberOfCalls * restCompoundService.get(_) >> {compound}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.compoundAdapters.size() == expectedNumberOfHits
        where:
        label                    | cids                           | compound                                             | expectedNumberOfCalls | expectedNumberOfHits
        "Multiple Compound Ids"  | [new Long(872), new Long(111)] | [new Compound(name: "C1"), new Compound(name: "C2")] | 1                     | 2
        "Unknown Compound Id"    | [new Long(802)]                | null                                                 | 1                     | 0
        "Single Compound Id"     | [new Long(872)]                | [new Compound(name: "C1")]                           | 1                     | 1
        "Empty Compound Id list" | []                             | null                                                 | 0                     | 0

    }

    void "test findAssaysByPIDs #label"() {
        when:
        Map responseMap = service.findAssaysByADIDs(assayIds)
        then:
        queryServiceWrapper.getRestAssayService() >> { restAssayService }
        expectedNumberOfCalls * restAssayService.get(_) >> {assay}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.assayAdapters.size() == expectedNumberOfHits
        where:
        label                 | assayIds                       | assay                                          | expectedNumberOfCalls | expectedNumberOfHits
        "Multiple Assay Ids"  | [new Long(872), new Long(111)] | [new Assay(name: "C1"), new Assay(name: "C2")] | 1                     | 2
        "Unknown Assay Id"    | [new Long(802)]                | null                                           | 1                     | 0
        "Single Assay Id"     | [new Long(872)]                | [new Assay(name: "C1")]                        | 1                     | 1
        "Empty Assay Id list" | []                             | null                                           | 0                     | 0

    }

    void "test findProjectsByPIDs #label"() {
        when:
        Map responseMap = service.findProjectsByPIDs(projectIds)
        then:
        queryServiceWrapper.getRestProjectService() >> { restProjectService }
        expectedNumberOfCalls * restProjectService.get(_) >> {project}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.projectAdapters.size() == expectedNumberOfHits
        where:
        label                   | projectIds                     | project                                            | expectedNumberOfCalls | expectedNumberOfHits
        "Multiple Project Ids"  | [new Long(872), new Long(111)] | [new Project(name: "C1"), new Project(name: "C2")] | 1                     | 2
        "Unknown Project Id"    | [new Long(802)]                | null                                               | 1                     | 0
        "Single Project Id"     | [new Long(872)]                | [new Project(name: "C1")]                          | 1                     | 1
        "Empty Project Id list" | []                             | null                                               | 0                     | 0

    }

    void "test Structure Search #label"() {
        given:
        ServiceIterator<Compound> iter = Mock(ServiceIterator.class)
        when:
        service.structureSearch(smiles, structureSearchParamsType)
        then:
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        restCompoundService.structureSearch(_) >> {iter}

        where:
        label                    | structureSearchParamsType                 | smiles
        "Sub structure Search"   | StructureSearchParams.Type.Substructure   | "CC"
        "Exact match Search"     | StructureSearchParams.Type.Exact          | "CC"
        "Similarity Search"      | StructureSearchParams.Type.Similarity     | "CC"
        "Super structure search" | StructureSearchParams.Type.Superstructure | "CC"
    }
}
