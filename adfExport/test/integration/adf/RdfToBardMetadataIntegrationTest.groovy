package adf

import bard.db.registration.Assay
import org.springframework.transaction.TransactionStatus
import org.hibernate.Session
import bard.db.audit.BardContextUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 4/8/14
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */
class RdfToBardMetadataIntegrationTest extends GroovyTestCase {
    def rdfToBardMetadataService
    void testSomething() {
        def m = rdfToBardMetadataService.createModel("test1.n3")
        rdfToBardMetadataService.handleAssay(m)
    }
}
