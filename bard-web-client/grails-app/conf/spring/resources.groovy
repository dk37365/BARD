import bard.core.rest.spring.AssayRestService
import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.ExperimentRestService
import bard.core.rest.spring.ProjectRestService
import bardqueryapi.QueryService
import grails.util.GrailsUtil
import mockServices.MockQueryService
import org.springframework.web.client.RestTemplate
import java.util.concurrent.Executors
import bard.core.rest.spring.SubstanceRestService
import bard.core.rest.spring.DataExportRestService

/**
 * Spring Configuration of resources
 */
beans = {
    final String ncgcBaseURL = grailsApplication.config.ncgc.server.root.url
    final String badApplePromiscuityUrl = grailsApplication.config.promiscuity.badapple.url
    final String exportApiKey = grailsApplication.config.dataexport.apikey
    final String elementAcceptType = grailsApplication.config.dataexport.element.accept.type
    final String exportElementBaseURL = grailsApplication.config.dataexport.element.url


    restTemplate(RestTemplate)

    compoundRestService(CompoundRestService) {
        baseUrl = ncgcBaseURL
        promiscuityUrl = badApplePromiscuityUrl
        restTemplate = ref('restTemplate')
        executorService = Executors.newCachedThreadPool()
        loggerService = ref('loggerService')
    }
    dataExportRestService(DataExportRestService) {
        dataExportApiKey = exportApiKey
        dictionaryElementAcceptType = elementAcceptType
        dataExportElementBaseURL = exportElementBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    experimentRestService(ExperimentRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    projectRestService(ProjectRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    assayRestService(AssayRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    substanceRestService(SubstanceRestService) {
        baseUrl = ncgcBaseURL
        restTemplate = ref('restTemplate')
        loggerService = ref('loggerService')
    }
    switch (GrailsUtil.environment) {
        case "offline":
            queryService(MockQueryService) {
                queryHelperService = ref('queryHelperService')
            }
            break;
        default:
            queryService(QueryService) {
                queryHelperService = ref('queryHelperService')
                compoundRestService = ref('compoundRestService')
                projectRestService = ref('projectRestService')
                assayRestService = ref('assayRestService')
                substanceRestService=ref('substanceRestService')
                experimentRestService=ref('experimentRestService')
                dataExportRestService=ref('dataExportRestService')
            }
    }
    crowdAuthenticationProvider(org.broadinstitute.cbip.crowd.CrowdAuthenticationProviderService) {// beans here
        crowdClient = ref('crowdClient')
        grailsApplication = application
    }
    inMemMapAuthenticationProviderService(org.broadinstitute.cbip.crowd.noServer.MockCrowdAuthenticationProviderService)
}
