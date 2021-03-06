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

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 4/3/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */

@Unroll
class ElementAbbreviationLabelComparatorUnitSpec extends Specification {

    void "test comparator #desc"() {
        given:
        def elements = initial.collect {
            new Element(label: it.l, abbreviation: it.a)
        }
        when:
        elements.sort(true, new ElementAbbreviationLabelComparator())
        then:
        elements*.abbreviation == expectedAbbreviationOrder
        elements*.label == expectedLabelOrder

        where:
        desc                                   | initial                                                                                        | expectedAbbreviationOrder | expectedLabelOrder
        'happy case all lowercase'             | [[a: 'c', l: 'c'], [a: 'b', l: 'b'], [a: 'a', l: 'a']]                                         | ['a', 'b', 'c']           | ['a', 'b', 'c']
        'happy case case-insensitive'          | [[a: 'C', l: 'C'], [a: 'b', l: 'b'], [a: 'A', l: 'A']]                                         | ['A', 'b', 'C']           | ['A', 'b', 'C']
        'null abbreviation, fallback to label' | [[a: 'C', l: 'C'], [a: null, l: 'b'], [a: 'A', l: 'A']]                                        | ['A', null, 'C']          | ['A', 'b', 'C']
        'abbreviations same, sort on label'    | [[a: '%', l: 'mass percentage'], [a: '%', l: 'percent'], [a: '%', l: 'confluence percentage']] | ['%', '%', '%']           | ['confluence percentage', 'mass percentage', 'percent']

    }
}
