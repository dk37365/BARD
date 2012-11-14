package bardqueryapi

import bard.core.HillCurveValue
import spock.lang.Specification
import spock.lang.Unroll
import molspreadsheet.SpreadSheetActivity

import static junit.framework.Assert.assertNotNull

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
class SpreadSheetActivityUnitSpec extends Specification {

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }
    /**
     * {@link SpreadSheetActivity#interpretHillCurveValue}
     */

    void "test interpret Hill Curve Value with null HillCurveValue"() {
        when:
        final SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        then:
        assertNotNull spreadSheetActivity
    }

}