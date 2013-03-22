package maas

import bard.db.experiment.Experiment
import org.springframework.transaction.support.DefaultTransactionStatus
import org.junit.Ignore

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/2/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentHandlerServiceIntegrationTest extends GroovyTestCase {
    def experimentHandlerService

    @Ignore
    void testLoadExperimentsContext() {
        File file = new File("data/maas/maasDataset1")
        def inputFiles = [file]
        experimentHandlerService.loadExperimentsContext("xiaorong", inputFiles)
    }

    @Ignore
    void testLoadProjectsContextFrom(){
        final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('test/exampleData/maas/most_recent_probe_aids.csv')
        def dirs = ['test/exampleData/maas/what_we_should_load']
        experimentHandlerService.handle('xiaorong', dirs, mustLoadAids)
    }

    @Ignore
    void testLoad() {
        final List<Long> mustLoadAids = MustLoadAid.mustLoadedAids('data/maas/maasDataset1/aids_dataset_1.csv')
        def dirs = ['data/maas/maasDataset1']
            experimentHandlerService.handle('xiaorong', dirs, mustLoadAids)
    }

}