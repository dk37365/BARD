package bard.db.project

import bard.db.experiment.ExperimentContextItem
import org.junit.Before
import bard.db.model.AbstractContextItemIntegrationSpec
import bard.db.project.ProjectContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:42 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectContextItemConstraintIntegrationSpec extends AbstractContextItemIntegrationSpec<ProjectContextItem> {

    @Before
    @Override
    void doSetup() {
        this.domainInstance = constructInstance([:])
    }

    ProjectContextItem constructInstance(Map props) {
        def instance = ProjectContextItem.buildWithoutSave(props)
        instance.attributeElement.save()

        return instance
    }
}
