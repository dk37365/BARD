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

package maas

import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

/**
 * Read a file containing mapping between terms that need to be treated as external ontology, and its ID. For example:
 * "dna damage response, signal transduction by p53 class mediator": "0030330"
 *
 * It will be displayed in the UI as a URL
 *
 *
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/13/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
class ExternalTermMapping {
    // As an initial stage, let's expecting there is a file under data/maas/externalTermMapping.txt
    //private static final String MAPPING_FILE_NAME = "data/maas/externalTermMapping.txt"
    private static final String MAPPING_FILE_NAME = ConfigurationHolder.config.data.migration.termmapping.base + 'external_term_mapping.txt'

    private static final Map<String, String> externalTerms

    static Map<String, String> build(String fileName) {
        if (externalTerms)
            return externalTerms
        println("in build external term mapping, read term from ${MAPPING_FILE_NAME}")
        if (StringUtils.isBlank(fileName)) {
           fileName = MAPPING_FILE_NAME
        }

        Map<String, String> externalTerms = [:]
        new File(fileName).eachLine {String line ->
            String[] elements = line.split("\t")
            if (StringUtils.isNotBlank(StringUtils.trim(elements[0]))) {
                externalTerms.put(StringUtils.trim(elements[0]).toLowerCase(), StringUtils.trim(elements[1]))
            }
         }
        return externalTerms
    }
}
