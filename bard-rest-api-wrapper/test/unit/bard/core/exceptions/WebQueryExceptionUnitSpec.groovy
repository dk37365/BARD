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

package bard.core.exceptions

import bard.core.DataSource
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WebQueryExceptionUnitSpec extends Specification {

    @Shared
    String message = "message"
    @Shared
    Throwable throwable = new Exception(message)


    void "test Constructors #label"() {
        when:
        webQueryException.toString()
        then:
        assert webQueryException.message == expectedMessage
        assert webQueryException.getCause() == expectedThrowable
        where:
        label                              | webQueryException                         | expectedMessage                | expectedThrowable
        "empty arg constructor"            | new WebQueryException()                   | null                           | null
        "2 arg constructor"                | new WebQueryException(message, throwable) | message                        | throwable
        "1 arg constructor with throwable" | new WebQueryException(throwable)          | "java.lang.Exception: message" | throwable
        "1 arg constructor with message"   | new WebQueryException(message)            | message                        | null
    }


}

