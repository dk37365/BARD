package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.util.ETag
import bard.core.rest.spring.util.ETagCollection
import bard.core.rest.spring.util.Facet
import bard.core.rest.spring.util.StructureSearchParams
import grails.test.mixin.TestFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

import bard.core.rest.spring.compounds.*

@Unroll
@TestFor(CompoundRestService)
class CompoundRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate
    ExecutorService executorService

    void setup() {
        this.restTemplate = Mock(RestTemplate)
        this.executorService = Mock(ExecutorService)
        service.executorService = this.executorService
        service.restTemplate = this.restTemplate
        service.promiscuityUrl = "badapple"
        service.baseUrl = "http://ncgc"
    }

    void tearDown() {
        // Tear down logic here
    }

    void "validateSimilarityThreshold"() {
        when:
        service.validateSimilarityThreshold(null)
        then:
        thrown(IllegalArgumentException)
    }

    void "getResourceContext"() {
        when:
        String resourceContext = service.getResourceContext()

        then:
        assert resourceContext == RestApiConstants.COMPOUNDS_RESOURCE
    }

    void "getSearchResource"() {
        when:
        String searchResource = service.getSearchResource()
        then:
        assert searchResource == "http://ncgc/search/compounds/?"
    }

    void "getResource"() {
        when:
        String resource = service.getResource()
        then:
        assert resource == "http://ncgc/compounds/"

    }

    void "buildPromiscuityScoreURL"() {
        when:
        String promiscuityScoreURL = service.buildPromiscuityScoreURL()
        then:
        assert promiscuityScoreURL == "badapple{cid}?expand={expand}&repr={mediaType}"
    }

    void "buildQueryForTestedAssays #label"() {
        when:
        String constructedURL = service.buildQueryForTestedAssays(cid, isActiveOnly)
        then:
        assert constructedURL == expectedURL

        where:
        label                 | cid | isActiveOnly | expectedURL
        "With CID + Active"   | 22  | true         | "http://ncgc/compounds/22/assays?expand=true&filter=active"
        "With CID + InActive" | 22  | false        | "http://ncgc/compounds/22/assays?expand=true"
    }

    void "buildStructureSearchURL #label"() {
        given:
        final StructureSearchParams structureSearchParam = new StructureSearchParams("query", structureSearchType)
        if (withSkip) {
            structureSearchParam.setSkip(2)
        }
        when:
        final String url = service.buildStructureSearchURL(structureSearchParam, false)
        then:
        assert url == expectedURL
        where:
        label             | withSkip | structureSearchType                       | expectedURL
        "Sub Structure"   | true     | StructureSearchParams.Type.Substructure   | "http://ncgc/compounds/?filter=query[structure]&type=sub&expand=true&top=100&skip=2"
        "Super Structure" | false    | StructureSearchParams.Type.Superstructure | "http://ncgc/compounds/?filter=query[structure]&type=sup&expand=true&top=100&skip=0"
        "Exact"           | false    | StructureSearchParams.Type.Exact          | "http://ncgc/compounds/?filter=query[structure]&type=exact&expand=true&top=100&skip=0"
        "Similarity"      | false    | StructureSearchParams.Type.Similarity     | "http://ncgc/compounds/?filter=query[structure]&type=sim&cutoff=0.700&expand=true&top=100&skip=0"
    }

    void "doStructureSearch"() {
        given:
        final StructureSearchParams structureSearchParam = new StructureSearchParams("query", StructureSearchParams.Type.Substructure)
        final Map<String, Long> eTags = [:]
        when:
        CompoundResult compoundSearchResult = service.doStructureSearch(structureSearchParam, eTags)
        then:
        restTemplate.exchange(_, _, _, _) >> {new ResponseEntity(HttpStatus.ACCEPTED)}
        assert compoundSearchResult

    }

    void "handleFutures with Exception"() {
        when:
        final CompoundResult futures = service.handleFutures([1, 2])
        then:
        assert !futures

    }

    void "buildQueryForCompoundSummary"() {
        when:
        String query = service.buildQueryForCompoundSummary(2L)
        then:
        assert query == "http://ncgc/compounds/2/summary"

    }

    void "Get summary for compound"() {
        given:
        Long cid = 2
        when:
        CompoundSummary compoundSummary = service.getSummaryForCompound(cid)
        then:
        restTemplate.getForObject(_, _) >> {new CompoundSummary()}
        assert compoundSummary
    }

    void "getTestedAssays #label"() {
        when:
        List<Assay> assays = service.getTestedAssays(cid, activeOnly)
        then:
        restTemplate.getForObject(_, _) >> {new AssayResult(assays: [new Assay()])}
        assert assays
        where:
        label            | cid | activeOnly
        "CID + InActive" | 22  | false
        "CID + Active"   | 22  | true

    }



    void "findAnnotations"() {
        given:
        Long cid = 200
        when:
        final CompoundAnnotations annotations = service.findAnnotations(cid)
        then:
        this.restTemplate.getForObject(_, _) >> {new CompoundAnnotations()}
        assert annotations
    }

    void "getCompoundById #label"() {
        given:
        FutureTask<CompoundAnnotations> compoundAnnotationsTask = Mock(FutureTask)
        FutureTask<Compound> compoundTask = Mock(FutureTask)
        when:
        Compound foundCompound = service.getCompoundById(cid)
        then:
        restTemplate.getForObject(_, _, _) >> {compounds}
        compoundAnnotationsTask.get() >> {null}
        compoundTask.get() >> {compounds.get(0)}
        executorService.invokeAll(_, _, _) >> {[compoundAnnotationsTask, compoundTask]}
        assert (foundCompound != null) == noErrors
        where:
        label                   | cid | compounds        | noErrors
        "Existing Compound"     | 179 | [new Compound()] | true
        "Non_Existing Compound" | -1  | null             | false
    }

    void "searchCompoundsByIds #label"() {
        when:
        final CompoundResult compoundResult = service.searchCompoundsByIds(cids)
        then:
        0 * restTemplate.exchange(_, _, _, _)
        assert compoundResult == null
        where:
        label                        | cids
        "With null cids"             | null
        "With an empty list of cids" | []
    }

    void "findCompoundsByFreeTextSearch"() {
        given:
        final SearchParams searchParams = new SearchParams("dna repair")
        when:
        final CompoundResult compoundResult =
            service.findCompoundsByFreeTextSearch(searchParams)
        then:
        restTemplate.getForObject(_, _) >> {new CompoundResult()}
        assert compoundResult != null
    }

    void "findPromiscuityScoreForCompound"() {
        given:
        final Long cid = 200
        when:
        final PromiscuityScore compound = service.findPromiscuityScoreForCompound(cid)
        then:
        restTemplate.getForObject(_, _, _) >> {new PromiscuityScore()}
        assert compound != null
    }

    void "getSynonymsForCompound"() {
        when:
        List<String> synonyms = service.getSynonymsForCompound(200)
        then:
        restTemplate.getForObject(_, _, _, _) >> {["String"]}
        assert synonyms
    }

    void "getETags"() {

        when:
        final List<ETag> eTags = service.getETags(10, 10)
        then:
        this.restTemplate.getForObject(_, _) >> {new ETagCollection(etags: [new ETag()])}
        assert eTags
    }

    void "getFacetsByETag"() {
        when:
        List<Facet> facets = service.getFacetsByETag("etag")
        then:
        restTemplate.getForObject(_, _) >> {[new Facet()]}
        assert facets
    }

    void "getResourceCount"() {
        when:
        int count = service.getResourceCount()
        then:
        restTemplate.getForObject(_, _) >> {"1"}
        assert count == 1
    }

    void "test findCompoundsByETag"() {
        when:
        CompoundResult returnedCompoundResult = service.findCompoundsByETag(eTagName)

        then:
        //Expecting two calls:
        // 1st: for findCompoundsByETag -> findAllETagsForResource
        // 2nd: for findCompoundsByETag -> List<Compound>
        (0..2) * this.restTemplate.getForObject(_, _) >>> [new ETagCollection(etags: etags), compounds]
        assert returnedCompoundResult.compounds == expextedCompounds

        where:
        label                 | eTagName   | etags                        | compounds                         | expextedCompounds
        'etag name found'     | 'etagName' | [new ETag(name: 'etagName')] | [new Compound(name: 'compound1')] | [new Compound(name: 'compound1')]
        'etag name not found' | 'noName'   | [new ETag(name: 'etagName')] | [new Compound(name: 'compound1')] | []
        'no compounds found'  | 'etagName' | [new ETag(name: 'etagName')] | []                                | []
    }
}

