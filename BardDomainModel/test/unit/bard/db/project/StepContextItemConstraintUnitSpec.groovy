package bard.db.project

import bard.db.experiment.ExperimentContextItem
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll
import bard.db.model.AbstractContextItemConstraintUnitSpec
import bard.db.project.StepContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */
@Build(StepContextItem)
@Unroll
class StepContextItemConstraintUnitSpec extends AbstractContextItemConstraintUnitSpec<StepContextItem>{
    @Before
     void doSetup(){
        this.domainInstance = constructInstance([:])
    }

    StepContextItem constructInstance(Map props) {
        def instance = StepContextItem.buildWithoutSave(props)
        instance.attributeElement.save(failOnError:true, flush: true)

        return instance
    }
}