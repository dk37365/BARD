/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db.registration

import acl.CapPermissionService
import bard.db.enums.Status
import bard.db.people.Role
import bard.db.project.InlineEditableCommand
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import org.springframework.security.access.AccessDeniedException
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse

/**
 */


@TestFor(PanelController)
@Build([Assay, Panel, PanelAssay, Role])
@Mock([Assay, Panel, PanelAssay, Role])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class PanelControllerUnitSpec extends AbstractInlineEditingControllerUnitSpec {

    Panel panel
    Role role


    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        this.role = Role.build(authority: "ROLE_TEAM_A")
        controller.metaClass.mixin(EditingHelper)

        CapPermissionService capPermissionService = Mock(CapPermissionService)
        PanelService panelService = Mock(PanelService)
        controller.panelService = panelService
        controller.springSecurityService = Mock(SpringSecurityService)
        controller.capPermissionService = capPermissionService
        panel = Panel.build(name: 'Test')
        assert panel.validate()
    }

    void 'test save success'() {
        final Role role = Role.build()
        given:
        PanelCommand panelCommand = new PanelCommand(name: "Some Name", springSecurityService: controller.springSecurityService, ownerRole: role.authority)
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [role]
        }
        when:
        controller.save(panelCommand)
        then:
        assert controller.response.redirectedUrl.startsWith("/panel/show/")
    }

    void 'test save failure'() {
        given:
        PanelCommand panelCommand = new PanelCommand(springSecurityService: controller.springSecurityService)
        when:
        controller.save(panelCommand)
        then:
        assert response.status == 200
    }

    void 'test create panel success'() {
        when:
        controller.create()
        then:
        assert response.status == 200
    }

    void 'test addAssayToPanel'() {
        given:
        params.assayIds = "22"
        when:
        controller.addAssayToPanel()
        then:
        assert response.status == 200
    }


    void 'test edit Panel Name success'() {
        given:
        Panel newPanel = Panel.build(version: 0, name: "My Name")
        Panel updatedPanel = Panel.build(name: "My New Name", version: 1, modifiedBy: "Designer")
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newPanel.id,
                version: newPanel.version, name: newPanel.name, value: updatedPanel.name)
        when:
        controller.editPanelName(inlineEditableCommand)
        then:
        controller.panelService.updatePanelName(_, _) >> { return updatedPanel }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedPanel.name
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Panel Name - access denied'() {
        given:
        accessDeniedRoleMock()
        Panel newPanel = Panel.build(version: 0, name: "My Name")
        Panel updatedPanel = Panel.build(name: "My New Name", version: 1, modifiedBy: "Designer")
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newPanel.id,
                version: newPanel.version, name: newPanel.name, value: updatedPanel.name)
        when:
        controller.editPanelName(inlineEditableCommand)
        then:
        controller.panelService.updatePanelName(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Panel Name with errors'() {
        given:
        Panel newPanel = Panel.build(version: 0)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newPanel.id, version: newPanel.version, name: newPanel.name, value: "Designer")
        controller.metaClass.message = { Map p -> return "foo" }

        when:
        controller.editPanelName(inlineEditableCommand)
        then:
        controller.panelService.updatePanelName(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }

    void 'test edit Panel owner role success'() {
        given:
        Role roleB = Role.build(authority: "ROLE_TEAM_B", displayName: "displayName");

        Panel newPanel = Panel.build(version: 0, name: "My Name", ownerRole: this.role)  //no designer
        Panel updatedPanel = Panel.build(name: "My New Name", version: 1, lastUpdated: new Date(), ownerRole: roleB)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newPanel.id,
                version: newPanel.version, name: newPanel.name, value: updatedPanel.ownerRole.displayName)
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [this.role, roleB]
        }
        when:
        controller.editOwnerRole(inlineEditableCommand)
        then:
        controller.panelService.updatePanelOwnerRole(_, _) >> { return updatedPanel }
        assert response.status == HttpServletResponse.SC_OK
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJSON = mapper.readValue(response.text, JsonNode.class);

        assert responseJSON.get("version").asText() == "0"
        assert responseJSON.get("data").asText() == updatedPanel.ownerRole.displayName
        assert responseJSON.get("lastUpdated").asText()
        assert response.contentType == "text/json;charset=utf-8"
    }

    void 'test edit Panel owner new role not in list - fail'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return false
        }
        Panel newPanel = Panel.build(version: 0, name: "My Name", ownerRole: this.role)  //no designer

        Role notInUsersRole = Role.build(authority: "ROLE_TEAM_C", displayName: "displayName");
        Panel updatedPanel = Panel.build(name: "My New Name", version: 1, lastUpdated: new Date(), ownerRole: notInUsersRole)
        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newPanel.id,
                version: newPanel.version, name: newPanel.name, value: updatedPanel.ownerRole.displayName)
        when:
        controller.editOwnerRole(inlineEditableCommand)
        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST

    }

    void 'test edit Panel owner role - access denied'() {
        given:
        accessDeniedRoleMock()
        Panel newPanel = Panel.build(version: 0, name: "My Name", ownerRole: this.role)  //no designer

        Role roleB = Role.build(authority: "ROLE_TEAM_B", displayName: "displayName");
        Panel updatedPanel = Panel.build(name: "My New Name", version: 1, lastUpdated: new Date(), ownerRole: roleB)

        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newPanel.id,
                version: newPanel.version, name: newPanel.name, value: updatedPanel.ownerRole.displayName)
        when:
        controller.editPanelName(inlineEditableCommand)
        then:
        controller.panelService.updatePanelName(_, _) >> { throw new AccessDeniedException("msg") }
        assertAccesDeniedErrorMessage()
    }

    void 'test edit Panel owner role with errors'() {
        given:

        Panel newPanel = Panel.build(version: 0, name: "My Name", ownerRole: this.role)  //no designer

        Role roleB = Role.build(authority: "ROLE_TEAM_B", displayName: "displayName");
        Panel updatedPanel = Panel.build(name: "My New Name", version: 1, lastUpdated: new Date(), ownerRole: roleB)

        InlineEditableCommand inlineEditableCommand = new InlineEditableCommand(pk: newPanel.id,
                version: newPanel.version, name: newPanel.name, value: updatedPanel.ownerRole.displayName)

        controller.metaClass.message = { Map p -> return "foo" }

        when:
        controller.editPanelName(inlineEditableCommand)
        then:
        controller.panelService.updatePanelName(_, _) >> { throw new Exception("") }
        assertEditingErrorMessage()
    }


    void 'test edit optimistic lock failure'() {
        given:
        Panel newPanel = Panel.build()
        InlineEditableCommand inlineEditableCommand =
            new InlineEditableCommand(pk: newPanel.id, version: newPanel.version, name: newPanel.name, value: "Designer")
        when:
        controller.editPanelName(inlineEditableCommand)
        then:
        inlineEditableCommand.validateVersions(_, _) >> { "Some error message" }
        assertOptimisticLockFailure()
    }

    void 'test show'() {
        given:
        CapPermissionService capPermissionService = controller.capPermissionService
        when:
        params.id = panel.id
        def model = controller.show()

        then:
        capPermissionService.getOwner(_) >> { 'owner' }
        model.panelInstance == panel
    }


    void 'test show - fail #desc'() {
        when:
        params.id = panelId
        def model = controller.show()

        then:
        model.message == expectedMessage
        where:
        desc             | expectedMessage                    | panelId
        "With ID=0"      | "default.not.found.message"        | "0"
        "With ID=STRING" | "Supplied ID not a valid Panel ID" | "STRING"
    }
    void 'test add assays - provisional'() {
        given:
        request.method = "POST"
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay", assayStatus: Status.PROVISIONAL)
        params.id = panel.id
        params.assayIds = assay.id

        when:
        def panelService = mockFor(PanelService)
        panelService.demand.associateAssays(1) { Long id, List<Assay> assays ->
            assert [assay] == assays
            assert panel.id == id
        }
        controller.setPanelService(panelService.createMock())
        controller.addAssays()

        then:
        assert "/panel/show/${panel.id}" == response.redirectedUrl

        when:
        panelService.verify()

        then:
        notThrown(Exception.class)
    }

    void 'test add assays'() {
        given:
        request.method = "POST"
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay", assayStatus: Status.APPROVED)
        params.id = panel.id
        params.assayIds = assay.id

        when:
        def panelService = mockFor(PanelService)
        panelService.demand.associateAssays(1) { Long id, List<Assay> assays ->
            assert [assay] == assays
            assert panel.id == id
        }
        controller.setPanelService(panelService.createMock())
        controller.addAssays()

        then:
        assert "/panel/show/${panel.id}" == response.redirectedUrl

        when:
        panelService.verify()

        then:
        notThrown(Exception.class)
    }

    void 'test add assays - access denied'() {
        given:
        accessDeniedRoleMock()
        request.method = "POST"
        Panel panel = Panel.build(name: "name")
        Assay assay = Assay.build(assayName: 'Test')

        params.id = panel.id
        params.assayIds = assay.id
        when:
        def model = controller.addAssays()

        then:
        controller.panelService.associateAssays(_, _) >> { throw new AccessDeniedException("msg") }
        final AssociatePanelCommand associatePanelCommand = model.associatePanelCommand
        assert associatePanelCommand.id == panel.id
        assert associatePanelCommand.assayIds == assay.id.toString()
        assert associatePanelCommand.hasErrors()
    }

    void 'test add assay'() {
        given:
        request.method = "POST"
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay")
        params.id = panel.id
        params.assayIds = assay.id

        when:
        def panelService = mockFor(PanelService)
        panelService.demand.associateAssay(1) { Long id, Assay assay1 ->
            assert assay == assay1
            assert panel.id == id
        }
        controller.setPanelService(panelService.createMock())
        controller.addAssay()

        then:
        assert "/panel/show/${panel.id}" == response.redirectedUrl

        when:
        panelService.verify()

        then:
        notThrown(Exception.class)
    }


    void 'test add assay - access denied'() {
        given:
        accessDeniedRoleMock()
        request.method = "POST"
        Panel panel = Panel.build(name: "name")
        Assay assay = Assay.build(assayName: 'Test')

        params.id = panel.id
        params.assayIds = assay.id
        when:
        def model = controller.addAssay()

        then:
        controller.panelService.associateAssay(_, _) >> { throw new AccessDeniedException("msg") }
        final AssociatePanelCommand associatePanelCommand = model.associatePanelCommand
        assert associatePanelCommand.id == panel.id
        assert associatePanelCommand.assayIds == assay.id.toString()
        assert associatePanelCommand.hasErrors()
    }

    void 'test delete panel'() {
        when:
        panel = Panel.build(name: 'Test')
        params.id = panel.id

        def panelService = mockFor(PanelService)
        panelService.demand.deletePanel(1) { Long id ->
            assert panel.id == id
        }
        controller.setPanelService(panelService.createMock())
        controller.deletePanel()

        then:
        assert "/panel/myPanels" == response.redirectedUrl

        when:
        panelService.verify()

        then:
        notThrown(Exception.class)
    }


    void 'test delete panel - access denied'() {
        given:
        accessDeniedRoleMock()
        Panel panel = Panel.build(name: "name")

        params.id = panel.id

        when:
        controller.deletePanel()

        then:
        controller.panelService.deletePanel(_) >> { throw new AccessDeniedException("msg") }

        assertAccesDeniedErrorMessage()
    }

    void 'test remove assay'() {
        given:
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay")
        PanelAssay panelAssay = PanelAssay.build(assay: assay, panel: panel)
        panel.addToPanelAssays(panelAssay)
        assay.addToPanelAssays(panelAssay)

        params.id = panel.id
        params.assayIds = assay.id
        when:


        def panelService = mockFor(PanelService)
        panelService.demand.disassociateAssay(1) { Assay assay1, Long id ->
            assert assay == assay1
            assert panel.id == id
        }
        controller.setPanelService(panelService.createMock())
        controller.removeAssay()

        then:
        response.redirectedUrl == '/panel/show/' + panel.id

        when:
        panelService.verify()

        then:
        notThrown(Exception.class)
    }


    void 'test remove assay - access denied'() {

        given:
        accessDeniedRoleMock()
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay")
        PanelAssay panelAssay = PanelAssay.build(assay: assay, panel: panel)
        panel.addToPanelAssays(panelAssay)
        assay.addToPanelAssays(panelAssay)

        params.id = panel.id
        params.assayIds = assay.id
        when:
        def model = controller.removeAssay()

        then:
        controller.panelService.disassociateAssay(_, _) >> { throw new AccessDeniedException("msg") }
        final AssociatePanelCommand associatePanelCommand = model.associatePanelCommand
        assert associatePanelCommand.id == panel.id
        assert associatePanelCommand.assayIds == assay.id.toString()
        assert associatePanelCommand.hasErrors()

    }

    void 'test remove assays'() {
        given:
        request.method = "POST"
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay")
        PanelAssay panelAssay = PanelAssay.build(assay: assay, panel: panel)
        panel.addToPanelAssays(panelAssay)
        assay.addToPanelAssays(panelAssay)

        params.id = panel.id
        params.assayIds = assay.id
        when:


        def panelService = mockFor(PanelService)
        panelService.demand.disassociateAssays(1) { Long id, List<Assay> assays ->
            assert [assay] == assays
            assert panel.id == id
        }
        controller.setPanelService(panelService.createMock())
        controller.removeAssays()

        then:
        "/panel/show/${panel.id}" == response.redirectedUrl

        when:
        panelService.verify()

        then:
        notThrown(Exception.class)
    }


    void 'test remove assays - access denied'() {

        given:
        request.method = "POST"
        accessDeniedRoleMock()
        panel = Panel.build(name: 'Test')
        Assay assay = Assay.build(assayName: "assay")
        PanelAssay panelAssay = PanelAssay.build(assay: assay, panel: panel)
        panel.addToPanelAssays(panelAssay)
        assay.addToPanelAssays(panelAssay)

        params.id = panel.id
        params.assayIds = assay.id
        when:
        def model = controller.removeAssays()

        then:
        controller.panelService.disassociateAssays(_, _) >> { throw new AccessDeniedException("msg") }
        final AssociatePanelCommand associatePanelCommand = model.associatePanelCommand
        assert associatePanelCommand.id == panel.id
        assert associatePanelCommand.assayIds == assay.id.toString()
        assert associatePanelCommand.hasErrors()
    }


    void "test index"() {
        when:
        controller.index()
        then:
        "/panel/myPanels" == response.redirectedUrl
    }
}
