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

package bard.dm.minimumassayannotation.validateCreatePersist

import spock.lang.Specification
import grails.buildtestdata.mixin.Build
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import grails.test.mixin.Mock

import bard.dm.minimumassayannotation.ContextLoadResultsWriter
import bard.dm.minimumassayannotation.ContextDTO
import bard.db.registration.Assay
import bard.db.dictionary.Element
import bard.dm.minimumassayannotation.ContextItemDto
import bard.db.registration.AttributeType

import org.apache.log4j.Logger

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 3/21/13
 * Time: 7:35 PM
 * To change this template use File | Settings | File Templates.
 */

@Build([AssayContext, AssayContextItem, Assay, Element])
@Mock([AssayContext, AssayContextItem, Assay])
class AssayContextsValidatorCreatorAndPersistorTestSpec extends Specification {

    private static final Map<String, String> keyValueMap = ["test key 1":"test value 1", "test key 2":"test value 2"]

    void "test to not add 2 duplicate contextDTO's"() {
        setup:
        final Map<String, Element> findByLabelILikeMap = new HashMap<String, Element>()
        for (String key : keyValueMap.keySet()) {
            findByLabelILikeMap.put(key, Element.build(label: key))

            String value = keyValueMap.get(key)
            findByLabelILikeMap.put(value, Element.build(label: value))
        }

        Element.metaClass.static.findByLabelIlike = { String label ->
            return findByLabelILikeMap.get(label)
        }

        Assay assay = Assay.build()
        AssayContext assayContext = AssayContext.build()
        assayContext.assayContextItems.add(AssayContextItem.build())
        assay.assayContexts.add(assayContext)

        MockAssayContextsValidatorCreatorAndPersistor mockAssayContextsValidatorCreatorAndPersistor =
            new MockAssayContextsValidatorCreatorAndPersistor(assay)


        List<ContextDTO> contextDTOList = new ArrayList<ContextDTO>(2)

        for (int i = 0; i < 2; i++) {
            ContextDTO contextDTO = new ContextDTO(name: "test ContextDTO", aid: 1234)
            contextDTOList.add(contextDTO)

            for (String key : keyValueMap.keySet()) {
                String value = keyValueMap.get(key)

                ContextItemDto contextItemDto = new ContextItemDto(key, value, AttributeType.Fixed)
                contextDTO.contextItemDtoList.add(contextItemDto)
            }
        }

        when:
        mockAssayContextsValidatorCreatorAndPersistor.createAndPersist(contextDTOList)

        then:
        mockAssayContextsValidatorCreatorAndPersistor.assay.assayContexts.size() == 2

    }

    void "test to not crash when encountering assay without assayContext"() {
        setup:
        final Map<String, Element> findByLabelILikeMap = new HashMap<String, Element>()
        for (String key : keyValueMap.keySet()) {
            findByLabelILikeMap.put(key, Element.build(label: key))

            String value = keyValueMap.get(key)
            findByLabelILikeMap.put(value, Element.build(label: value))
        }

        Element.metaClass.static.findByLabelIlike = { String label ->
            return findByLabelILikeMap.get(label)
        }

        MockAssayContextsValidatorCreatorAndPersistor mockAssayContextsValidatorCreatorAndPersistor =
            new MockAssayContextsValidatorCreatorAndPersistor(Assay.build())

        List<ContextDTO> contextDTOList = new ArrayList<ContextDTO>(2)

        for (int i = 0; i < 2; i++) {
            ContextDTO contextDTO = new ContextDTO(name: "test ContextDTO", aid: 1234)
            contextDTOList.add(contextDTO)

            for (String key : keyValueMap.keySet()) {
                String value = keyValueMap.get(key)

                ContextItemDto contextItemDto = new ContextItemDto(key, value, AttributeType.Fixed)
                contextDTO.contextItemDtoList.add(contextItemDto)
            }
        }

        when:
        mockAssayContextsValidatorCreatorAndPersistor.createAndPersist(contextDTOList)

        then:
        mockAssayContextsValidatorCreatorAndPersistor.assay.assayContexts.size() == 1
    }

    void "test to not crash when encountering assay whose assayContext does not have assayContextItem"() {
        setup:
        final Map<String, Element> findByLabelILikeMap = new HashMap<String, Element>()
        for (String key : keyValueMap.keySet()) {
            findByLabelILikeMap.put(key, Element.build(label: key))

            String value = keyValueMap.get(key)
            findByLabelILikeMap.put(value, Element.build(label: value))
        }

        Element.metaClass.static.findByLabelIlike = { String label ->
            return findByLabelILikeMap.get(label)
        }

        Assay assay = Assay.build()
        assay.assayContexts.add(AssayContext.build())
        MockAssayContextsValidatorCreatorAndPersistor mockAssayContextsValidatorCreatorAndPersistor =
            new MockAssayContextsValidatorCreatorAndPersistor(assay)

        List<ContextDTO> contextDTOList = new ArrayList<ContextDTO>(2)

        for (int i = 0; i < 2; i++) {
            ContextDTO contextDTO = new ContextDTO(name: "test ContextDTO", aid: 1234)
            contextDTOList.add(contextDTO)

            for (String key : keyValueMap.keySet()) {
                String value = keyValueMap.get(key)

                ContextItemDto contextItemDto = new ContextItemDto(key, value, AttributeType.Fixed)
                contextDTO.contextItemDtoList.add(contextItemDto)
            }
        }

        when:
        mockAssayContextsValidatorCreatorAndPersistor.createAndPersist(contextDTOList)

        then:
        mockAssayContextsValidatorCreatorAndPersistor.assay.assayContexts.size() == 2
    }

    void "test to not add when contextDTO that does not have contextItemDTO"() {
        setup:
        final Map<String, Element> findByLabelILikeMap = new HashMap<String, Element>()
        for (String key : keyValueMap.keySet()) {
            findByLabelILikeMap.put(key, Element.build(label: key))

            String value = keyValueMap.get(key)
            findByLabelILikeMap.put(value, Element.build(label: value))
        }

        Element.metaClass.static.findByLabelIlike = { String label ->
            return findByLabelILikeMap.get(label)
        }

        MockAssayContextsValidatorCreatorAndPersistor mockAssayContextsValidatorCreatorAndPersistor =
            new MockAssayContextsValidatorCreatorAndPersistor(Assay.build())

        List<ContextDTO> contextDTOList = new ArrayList<ContextDTO>(2)

        ContextDTO contextDTO = new ContextDTO(name: "test ContextDTO", aid: 1234)
//        contextDTOList.add(contextDTO)
//        String key = keyValueMap.keySet().iterator().next()
//        String value = keyValueMap.get(key)
//        ContextItemDto contextItemDto = new ContextItemDto(key, value, AttributeType.Fixed)
//        contextDTO.contextItemDtoList.add(contextItemDto)

        contextDTO = new ContextDTO(name: "test ContextDTO", aid: 1234)
        contextDTOList.add(contextDTO)

        when:
        mockAssayContextsValidatorCreatorAndPersistor.createAndPersist(contextDTOList)

        then:
        mockAssayContextsValidatorCreatorAndPersistor.assay.assayContexts.size() == 0
    }
}


class MockContextLoadResultsWriter extends ContextLoadResultsWriter {
    @Override
    void write(ContextDTO contextDTO, Long adid, ContextLoadResultsWriter.LoadResultType resultType,
               Integer numExistingContextsInDb, int numContextsLoaded, String message) {
    }
}

class MockAssayContextsValidatorCreatorAndPersistor extends AssayContextsValidatorCreatorAndPersistor {
    Assay assay

    MockAssayContextsValidatorCreatorAndPersistor(Assay assay) {
        super("test_modified_by", new MockContextLoadResultsWriter(), false, new MockLogger())

        this.assay = assay
    }

    @Override
    Assay getAssayFromAid(long AID) {
        return assay
    }

    @Override
    boolean postProcessAssayContextItem(AssayContextItem assayContextItem, ContextDTO contextDTO) {
        return true
    }
}

class MockLogger extends Logger {
    MockLogger() {
        super("test logger name")
    }

    @Override
    void trace(Object message) {
    }

    @Override
    void debug(Object message) {
    }

    @Override
    void error(Object message) {
    }

    @Override
    void info(Object message) {
    }

    @Override
    void trace(Object message, Throwable t) {
    }

    @Override
    void debug(Object message, Throwable t) {
    }

    @Override
    void error(Object message, Throwable t) {
    }

    @Override
    void fatal(Object message) {
    }

    @Override
    void fatal(Object message, Throwable t) {
    }

    @Override
    void info(Object message, Throwable t) {
    }
}
