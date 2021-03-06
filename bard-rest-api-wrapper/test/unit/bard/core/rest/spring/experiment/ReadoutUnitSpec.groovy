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

package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ReadoutUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    final String READ_OUT_JSON = '''
   {
       "name": "INHIBITION",
       "s0": 3.57,
       "sInf": 100,
       "hill": 1.35,
       "ac50": 0.0000033999999999999996,
       "cr":
       [
           [
               1.02e-8,
               11.67
           ],
           [
               3.05e-8,
               4.38
           ],
           [
               9.15e-8,
               3.57
           ],
           [
               2.74e-7,
               4.13
           ],
           [
               8.230000000000001e-7,
               12.16
           ],
           [
               0.00000247,
               39.47
           ],
           [
               0.00000741,
               72.93
           ],
           [
               0.000022199999999999998,
               93.92
           ],
           [
               0.0000667,
               100
           ]
       ],
       "npoint": 9,
       "concUnit": "m",
       "responseUnit": null
   }
'''

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Readout Dose response"() {
        when:
        Readout readOut = objectMapper.readValue(READ_OUT_JSON, Readout.class)
        then:
        assert readOut
        assert !readOut.responseUnit
        assert readOut.concentrationUnits
        assert readOut.numberOfPoints == 9
        assert readOut.name == "INHIBITION"
        assert readOut.s0 == 3.57
        assert readOut.getSInf() == 100
        assert readOut.coef == 1.35
        assert readOut.slope == 0.0000033999999999999996
        assert readOut.getCr().size() == 9
        assert readOut.getConcAsList().size() == 9
        assert readOut.getResponseAsList().size() == 9
        assert readOut.toHillCurveValue()
        assert readOut.getResponse()
        assert readOut.getConc()
        assert readOut.size()

    }

    void "test addPoint"() {
        given:
        Readout readOut = new Readout()
        when:
        readOut.addPoint(new Double(2.0), new Double(2.0))
        then:
        readOut.getResponseAsList().size() == 1
        readOut.getConcAsList().size() == 1


    }

    void "test addCrcs Empty List"() {
        given:
        List<List<Double>> cr = [[]]
        Readout readout = new Readout()
        when:
        readout.addCrcs(cr)
        then:
        assert readout.getCr()
        assert !readout.getSInf()

    }

    void "test addCrcs only one value in list"() {
        given:
        List<List<Double>> cr = [[2.0]]
        Readout readout = new Readout()
        when:
        readout.addCrcs(cr)
        then:
        assert readout.getCr()
    }
    void "test toHillCurveValue with Points"() {
        given:

        Readout readout = new Readout()
        readout.addPoint(2,2)
        when:
        readout.toHillCurveValue()
        then:
        assert readout.getConc()
        assert readout.getResponse()

    }
    void "test toHillCurveValue with no Points"() {
        given:

        Readout readout = new Readout()
        when:
        readout.toHillCurveValue()
        then:
        assert !readout.getConc()
        assert !readout.getResponse()

    }
}

