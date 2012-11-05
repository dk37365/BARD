package bard.db.experiment

import bard.db.registration.AbstractContextConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Shared
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/17/12
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([ProjectStep, StepContext])
@Unroll
class StepContextConstraintUnitSpec extends AbstractContextConstraintUnitSpec {

    @Shared ProjectStep validProjectStep

    @Before
    void doSetup() {
        domainInstance = StepContext.buildWithoutSave()
        validProjectStep = ProjectStep.build()
    }

    void "test projectStep constraints #desc projectStep: '#valueUnderTest'"() {
        final String field = 'projectStep'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        println("field : $field")
        println("valueUnderTest : $valueUnderTest")
        println("validProjectStep : $validProjectStep")
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest   | valid | errorCode
        'null not valid' | null             | false | 'nullable'
        'valid step'     | validProjectStep | true  | null
    }

}