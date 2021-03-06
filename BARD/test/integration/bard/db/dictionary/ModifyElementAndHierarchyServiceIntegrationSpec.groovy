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

package bard.db.dictionary

import bard.db.enums.AddChildMethod
import bard.db.enums.ExpectedValueType
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.After
import org.junit.Before
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 1/28/13
 * Time: 3:05 PM
 */
@Unroll
class ModifyElementAndHierarchyServiceIntegrationSpec extends IntegrationSpec {

    ModifyElementAndHierarchyService modifyElementAndHierarchyService
    @Shared
    Element first_element
    @Shared
    Element second_element
    @Shared
    Element third_element
    @Shared
    Element fourth_element
    @Shared
    Element fifth_element

    @Before
    void setup() {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        first_element = Element.build(label: '1st_element')
        second_element = Element.build(label: '2nd_element')
        third_element = Element.build(label: '3rd_element')
        fourth_element = Element.build(label: '4th_element')
        fifth_element = Element.build(label: '5th_element')
        //reset the hierarchies before each run.
//        Element.list().each { Element element ->
//            element.parentHierarchies = [] as Set
//            element.childHierarchies = [] as Set
//        }
    }

    @After
    void tearDown() {}

    public static ElementHierarchy buildElementHierarchy(Element parent, Element child, String relationshipType) {
        ElementHierarchy elementHierarchy = new ElementHierarchy(parentElement: parent, childElement: child,
                relationshipType: relationshipType, dateCreated: new Date())
        assert elementHierarchy.save()

        parent.parentHierarchies.add(elementHierarchy)
        child.childHierarchies.add(elementHierarchy)

        return elementHierarchy
    }

    void "test checkPathForLoop with multiple parents hierarchies"() {
        given:
        //Create a hierarchy path: 1st -> 2nd
        ElementHierarchy eh1stTo2nd = buildElementHierarchy(first_element, second_element, "subClassOf")
        //Create a hierarchy path: 3rd -> 4th
        ElementHierarchy eh3rdTo4th = buildElementHierarchy(third_element, fourth_element, "subClassOf")
        //Create a hierarchy path: 3rd -> 5th
        ElementHierarchy eh3rdTo5th = buildElementHierarchy(third_element, fifth_element, "subClassOf")
        //Create a hierarchy path: 3rd -> 1st
        ElementHierarchy eh3rdTo1st = buildElementHierarchy(third_element, first_element, "subClassOf")

        when:
        //Try to add the 3rd element at the end of the hierarchy path - a loop! (1st -> 2nd -> 3rd -> 1st)
        List<Element> loopPath = modifyElementAndHierarchyService.checkPathForLoop([first_element, second_element], third_element)

        then:
        assert loopPath == [first_element, second_element, third_element, first_element]
    }

    void "test checkPathForLoop with s nested loop element"() {
        given:
        //Create a hierarchy path: 1st -> 2nd
        ElementHierarchy eh1stTo2nd = buildElementHierarchy(first_element, second_element, "subClassOf")
        //Create a hierarchy path: 3rd -> 4th
        ElementHierarchy eh3rdTo4th = buildElementHierarchy(third_element, fourth_element, "subClassOf")
        //Create a hierarchy path: 3rd -> 5th
        ElementHierarchy eh3rdTo5th = buildElementHierarchy(third_element, fifth_element, "subClassOf")
        //Create a hierarchy path: 5th -> 1st
        ElementHierarchy eh5thTo1st = buildElementHierarchy(fifth_element, first_element, "subClassOf")

        when:
        //Try to add the 3rd element at the end of the hierarchy path - a nested loop! (1st -> 2nd -> 3rd -> 5th -> 1st)
        List<Element> loopPath = modifyElementAndHierarchyService.checkPathForLoop([first_element, second_element], third_element)

        then:
        assert loopPath == [first_element, second_element, third_element, fifth_element, first_element]
    }

    void "test checkPathForLoop add the 3rd element after the 5th element - a loop"() {
        given:
        //Create a hierarchy path: 1st -> 2nd -> 3rd -> 4th -> 5th
        ElementHierarchy eh1stTo2nd = buildElementHierarchy(first_element, second_element, "subClassOf")
        ElementHierarchy eh2ndTo3rd = buildElementHierarchy(second_element, third_element, "subClassOf")
        ElementHierarchy eh3rdTo4th = buildElementHierarchy(third_element, fourth_element, "subClassOf")
        ElementHierarchy eh4thTo5th = buildElementHierarchy(fourth_element, fifth_element, "subClassOf")

        when:
        //Try to add the third element at the end of the hierarchy path - a loop!
        List<Element> loopPath = modifyElementAndHierarchyService.checkPathForLoop([first_element, second_element, third_element, fourth_element, fifth_element], third_element)

        then:
        assert loopPath == [first_element, second_element, third_element, fourth_element, fifth_element, third_element]
    }

    void "test checkPathForLoop with a hidden loop"() {
        given:
        //Create a hierarchy path: 1st -> 2nd -> 3rd -> 4th
        ElementHierarchy eh1stTo2nd = buildElementHierarchy(first_element, second_element, "subClassOf")
        ElementHierarchy eh2ndTo3rd = buildElementHierarchy(second_element, third_element, "subClassOf")
        ElementHierarchy eh3rdTo4th = buildElementHierarchy(third_element, fourth_element, "subClassOf")
        //Create a hierarchy path: 5th -> 3rd
        ElementHierarchy eh5thTo3rd = buildElementHierarchy(fifth_element, third_element, "subClassOf")

        when:
        //Try to add the fifth element at the end of the hierarchy path - a loop! (1st -> 2nd -> 3rd -> 4th -> 5th - 3rd)
        List<Element> loopPath = modifyElementAndHierarchyService.checkPathForLoop([first_element, second_element, third_element, fourth_element], fifth_element)

        then:
        assert loopPath == [first_element, second_element, third_element, fourth_element, fifth_element, third_element]
    }
}
