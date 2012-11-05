package bard.db.experiment

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.experiment.ExperimentContext.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString
import spock.lang.Shared
import bard.db.registration.AbstractContextConstraintUnitSpec

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/17/12
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Experiment,ExperimentContext])
@Unroll
class ExperimentContextConstraintUnitSpec extends AbstractContextConstraintUnitSpec {



    @Shared Experiment validExperiment

    @Before
    void doSetup() {
        domainInstance = ExperimentContext.buildWithoutSave()
        validExperiment = Experiment.build()
    }

    void "test experiment constraints #desc experiment: '#valueUnderTest'"() {
        final String field = 'experiment'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        println("field : $field")
        println("valueUnderTest : $valueUnderTest")
        println("validExperiment : $validExperiment")
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest  | valid | errorCode
        'null not valid'   | null            | false | 'nullable'
        'valid experiment' | validExperiment | true  | null
        // TODO valueUnderTest is null for the @Shared validExperiment
    }

}