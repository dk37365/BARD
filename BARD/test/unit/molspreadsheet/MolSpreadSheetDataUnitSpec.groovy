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

package molspreadsheet

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(MolSpreadSheetData)
@Unroll
class MolSpreadSheetDataUnitSpec extends Specification {

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void "Smoke test can we build a molecular spreadsheet data"() {
        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        assertNotNull(molSpreadSheetData)

        then:
        assertNotNull molSpreadSheetData.mssData
        assertNotNull molSpreadSheetData.rowPointer
        assertNotNull molSpreadSheetData.rowPointer
    }

    void "test getColumnsDescr with no headers"() {
        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        List<String> columnsDescr = molSpreadSheetData.getColumnsDescr()

        then:
        columnsDescr.size()  == 0
    }

    void "test getColumnsDescr with headers"() {
        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        MolSpreadSheetColumnHeader molSpreadSheetColumnHeader = new MolSpreadSheetColumnHeader ()
        MolSpreadSheetColSubHeader molSpreadSheetColSubHeader1 =  new MolSpreadSheetColSubHeader()
        molSpreadSheetColSubHeader1.unitsInColumn = 'uM'
        MolSpreadSheetColSubHeader molSpreadSheetColSubHeader2 =  new MolSpreadSheetColSubHeader()
        molSpreadSheetColSubHeader2.unitsInColumn = 'pM'
        molSpreadSheetColumnHeader.molSpreadSheetColSubHeaderList =[molSpreadSheetColSubHeader1,molSpreadSheetColSubHeader2]
        molSpreadSheetData.mssHeaders =  [molSpreadSheetColumnHeader]
        List<String> columnsDescr = molSpreadSheetData.getColumnsDescr()

        then:
        columnsDescr.size()  == 2
        columnsDescr[0]  == 'uM'
        columnsDescr[1]  == 'pM'
    }


    void "test getSubColumns with headers"() {
        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        MolSpreadSheetColumnHeader molSpreadSheetColumnHeader = new MolSpreadSheetColumnHeader ()
        MolSpreadSheetColSubHeader molSpreadSheetColSubHeader1 =  new MolSpreadSheetColSubHeader()
        molSpreadSheetColSubHeader1.unitsInColumn = 'uM'
        MolSpreadSheetColSubHeader molSpreadSheetColSubHeader2 =  new MolSpreadSheetColSubHeader()
        molSpreadSheetColSubHeader2.unitsInColumn = 'pM'
        molSpreadSheetColumnHeader.molSpreadSheetColSubHeaderList =[molSpreadSheetColSubHeader1,molSpreadSheetColSubHeader2]
        molSpreadSheetData.mssHeaders =  [molSpreadSheetColumnHeader]
        List<String> subColumns0 = molSpreadSheetData. getSubColumns(0)
        List<String> subColumns1 = molSpreadSheetData. getSubColumns(1)

        then:
        subColumns0.size()  == 2
        subColumns1.size()  == 0
    }



    void "test getSubColumnList with headers"() {
        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        MolSpreadSheetColumnHeader molSpreadSheetColumnHeader = new MolSpreadSheetColumnHeader ()
        MolSpreadSheetColSubHeader molSpreadSheetColSubHeader1 =  new MolSpreadSheetColSubHeader()
        molSpreadSheetColSubHeader1.unitsInColumn = 'uM'
        MolSpreadSheetColSubHeader molSpreadSheetColSubHeader2 =  new MolSpreadSheetColSubHeader()
        molSpreadSheetColSubHeader2.unitsInColumn = 'pM'
        molSpreadSheetColumnHeader.molSpreadSheetColSubHeaderList =[molSpreadSheetColSubHeader1,molSpreadSheetColSubHeader2]
        molSpreadSheetData.mssHeaders =  [molSpreadSheetColumnHeader]
        List<MolSpreadSheetColSubHeader> retrievedMolSpreadSheetColSubHeader0 = molSpreadSheetData. getSubColumns(0)
        List<MolSpreadSheetColSubHeader> retrievedMolSpreadSheetColSubHeader1 = molSpreadSheetData. getSubColumns(1)

        then:
        retrievedMolSpreadSheetColSubHeader0.size()  == 2
        retrievedMolSpreadSheetColSubHeader1.size()  == 0
    }



    void "test determineResponseTypesPerAssay"() {
        given:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

        when:
        assertNotNull(molSpreadSheetData)
        molSpreadSheetData.mapColumnsToAssay[0]=''
        molSpreadSheetData.mapColumnsToAssay[1]=''
        molSpreadSheetData.mapColumnsToAssay[2]=''
        molSpreadSheetData.mapColumnsToAssay[3]=''
        molSpreadSheetData.mapColumnsToAssay[4]='346'
        molSpreadSheetData.mapColumnsToAssay[5]='2199'
        molSpreadSheetData.mapColumnsToAssay[6]='2199'
        molSpreadSheetData.mapCapAssayIdsToAssayNames[346L]='Fluorescence-based dose response'
        molSpreadSheetData.mapCapAssayIdsToAssayNames[2199L]='HTS small molecules'
        molSpreadSheetData.experimentFullNameList<<'Fluorescence-based dose response'
        molSpreadSheetData.experimentFullNameList<<'HTS small molecules'
        molSpreadSheetData.mapColumnsToAssayName[4]='Fluorescence-based dose response'
        molSpreadSheetData.mapColumnsToAssayName[5]='HTS small molecules'
        List <LinkedHashMap<String,String>> returnValue  = molSpreadSheetData.determineResponseTypesPerAssay()

        then:
        assertNotNull returnValue
        assert returnValue.size()==2
        assert returnValue[0]."assayName"=='346'
        assert returnValue[0]."numberOfResultTypes"==1
        assert returnValue[0]."fullAssayName"=='Fluorescence-based dose response'
        assert returnValue[1]."assayName"=='2199'
        assert returnValue[1]."numberOfResultTypes"==2
        assert returnValue[1]."fullAssayName"=='HTS small molecules'
    }

    void "test determineResponseTypesPerAssay - error condition 1"() {
        given:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

        when:
        assertNotNull(molSpreadSheetData)
        molSpreadSheetData.mapColumnsToAssay[0]='Fluorescence-based dose response'
        molSpreadSheetData.experimentFullNameList<<'Fluorescence-based dose response'
        molSpreadSheetData.mapColumnsToAssayName[0]='HTS small molecules'
        List <LinkedHashMap<String,String>> returnValue  = molSpreadSheetData.determineResponseTypesPerAssay()

        then:
        assertNotNull returnValue
        assert returnValue.size()==0
    }

    void "test determineResponseTypesPerAssay - error condition 2"() {
        given:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

        when:
        assertNotNull(molSpreadSheetData)
        molSpreadSheetData.mapColumnsToAssay[0]=''
        molSpreadSheetData.mapColumnsToAssay[1]=''
        molSpreadSheetData.mapColumnsToAssay[2]=''
        molSpreadSheetData.mapColumnsToAssay[3]=''
        molSpreadSheetData.mapColumnsToAssay[4]='346'
        molSpreadSheetData.experimentFullNameList<<'Fluorescence-based dose response'
        molSpreadSheetData.mapColumnsToAssayName[5]='HTS small molecules'
        List <LinkedHashMap<String,String>> returnValue  = molSpreadSheetData.determineResponseTypesPerAssay()

        then:
        assertNotNull returnValue
        returnValue.size()==1
        returnValue[0]."assayName" == "346"
        returnValue[0]."numberOfResultTypes" == 1
        returnValue[0]."fullassayName" == null
    }

    void "test determineResponseTypesPerAssay - error condition 3"() {
        given:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

        when:
        assertNotNull(molSpreadSheetData)
        molSpreadSheetData.mapColumnsToAssay[0]=''
        molSpreadSheetData.mapColumnsToAssay[1]=''
        molSpreadSheetData.mapColumnsToAssay[2]=''
        molSpreadSheetData.mapColumnsToAssay[3]=''
        molSpreadSheetData.mapColumnsToAssay[4]=null
        molSpreadSheetData.experimentFullNameList<<'Fluorescence-based dose response'
        molSpreadSheetData.mapColumnsToAssayName[5]='HTS small molecules'
        List <LinkedHashMap<String,String>> returnValue  = molSpreadSheetData.determineResponseTypesPerAssay()

        then:
        assertNotNull returnValue
        assert returnValue.size()==1
        returnValue[0]."assayName" == null
        returnValue[0]."numberOfResultTypes" == 1
        returnValue[0]."fullAssayName" == 'Data error: please contact your system administrator'
    }


    void "test determineResponseTypesPerAssay - mapColumnsToAssay is Empty"() {
        given:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

        when:
        List <LinkedHashMap<String,String>> returnValue  = molSpreadSheetData.determineResponseTypesPerAssay()

        then:
        assert returnValue.isEmpty()
    }


    void "Test displayValue method"() {
        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        assertNotNull(molSpreadSheetData)

        molSpreadSheetData.mssData["0_0"] = new MolSpreadSheetCell(incoming, MolSpreadSheetCellType.identifier)
        molSpreadSheetData.mssData["0_1"] = new MolSpreadSheetCell(incoming, MolSpreadSheetCellType.image)


        then:
        assert molSpreadSheetData.displayValue(row, column)["value"] == returnValue

        where:
        row | column | incoming | returnValue
        0   | 0      | "123"    | "123"
        0   | 1      | "123"    | null
        47  | 47     | "123"    | "-"
    }

    void "Test getSuperColumnCount in degenerate case"() {
        given:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()

        when:
        int numberOfColumns = molSpreadSheetData.getSuperColumnCount()

        then:
        assert numberOfColumns == 0
    }


    void "Test getSubColumns in typical case"() {
        given:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader ()
        molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader (molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'one')])
        molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader (molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'one'),
                new MolSpreadSheetColSubHeader(columnTitle:'two')])
        molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader (molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'one'),
                new MolSpreadSheetColSubHeader(columnTitle:'two'),
                new MolSpreadSheetColSubHeader(columnTitle:'three')])

        when:
        List<String> numberOfSubColumns = molSpreadSheetData.getSubColumns(experimentCount)
        List<String> accumulatedColumns = molSpreadSheetData.getColumns() - numberOfSubColumns

        then:
        assert numberOfSubColumns == predictedSubs
        assert accumulatedColumns == predictedAccumulatedColumns

        where:
        predictedSubs           | experimentCount | predictedAccumulatedColumns
        []                      | 0               | ['one', 'one', 'two', 'one', 'two', 'three']
        ['one']                 | 1               | ['two', 'two', 'three']
        ['one', 'two']          | 2               | ['three']
        ['one', 'two', 'three'] | 3               | []
        []                      | 50              | ['one', 'one', 'two', 'one', 'two', 'three']

    }


    void "Test getColumns in degenerate case"() {
        given:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.mssHeaders = null

        when:
        List<String> accumulatedColumns = molSpreadSheetData.getColumns()

        then:
        assert accumulatedColumns == []

    }




    void "Test findSpreadSheetActivity method - return null"() {
        given:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        when:
        final SpreadSheetActivityStorage activity = molSpreadSheetData.findSpreadSheetActivity(1, 1)
        then:
        assert !activity

    }



    void "Test findSpreadSheetActivity method"() {
        given:
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage(eid: 1 as Long, cid: 2 as Long, sid: 3 as Long)

        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        assertNotNull(molSpreadSheetData)

        MolSpreadSheetCell molSpreadSheetCell_0_0 = new MolSpreadSheetCell("1", MolSpreadSheetCellType.identifier)
        molSpreadSheetCell_0_0.setSpreadSheetActivityStorage(spreadSheetActivityStorage)

        MolSpreadSheetCell molSpreadSheetCell_0_1 = new MolSpreadSheetCell("1", MolSpreadSheetCellType.image)
        molSpreadSheetCell_0_1.setSpreadSheetActivityStorage(spreadSheetActivityStorage)

        MolSpreadSheetCell molSpreadSheetCell_1_0 = new MolSpreadSheetCell("1", MolSpreadSheetCellType.numeric)
        molSpreadSheetCell_1_0.setSpreadSheetActivityStorage(spreadSheetActivityStorage)

        MolSpreadSheetCell molSpreadSheetCell_1_1 = new MolSpreadSheetCell("1", MolSpreadSheetCellType.string)
        molSpreadSheetCell_1_1.setSpreadSheetActivityStorage(spreadSheetActivityStorage)

        molSpreadSheetData.mssData["0_0"] = molSpreadSheetCell_0_0
        molSpreadSheetData.mssData["0_1"] = molSpreadSheetCell_0_1
        molSpreadSheetData.mssData["1_0"] = molSpreadSheetCell_1_0
        molSpreadSheetData.mssData["1_1"] = molSpreadSheetCell_1_1


        then:
        assert molSpreadSheetData.findSpreadSheetActivity(row, column).eid == 1 as Long
        assert molSpreadSheetData.findSpreadSheetActivity(row, column).cid == 2 as Long
        assert molSpreadSheetData.findSpreadSheetActivity(row, column).sid == 3 as Long

        where:
        row | column
        0   | 0
        0   | 1
        1   | 0
        1   | 1
    }

    void "Test degenerate method"() {
        given:
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage(eid: 1 as Long, cid: 2 as Long, sid: 3 as Long)

        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        assertNotNull(molSpreadSheetData)

        MolSpreadSheetCell molSpreadSheetCell_0_0 = new MolSpreadSheetCell("1", MolSpreadSheetCellType.identifier)
        molSpreadSheetCell_0_0.setSpreadSheetActivityStorage(spreadSheetActivityStorage)

        MolSpreadSheetCell molSpreadSheetCell_0_1 = new MolSpreadSheetCell("1", MolSpreadSheetCellType.image)
        molSpreadSheetCell_0_1.setSpreadSheetActivityStorage(spreadSheetActivityStorage)

        MolSpreadSheetCell molSpreadSheetCell_1_0 = new MolSpreadSheetCell("1", MolSpreadSheetCellType.numeric)
        molSpreadSheetCell_1_0.setSpreadSheetActivityStorage(spreadSheetActivityStorage)

        MolSpreadSheetCell molSpreadSheetCell_1_1 = new MolSpreadSheetCell("1", MolSpreadSheetCellType.string)
        molSpreadSheetCell_1_1.setSpreadSheetActivityStorage(spreadSheetActivityStorage)

        molSpreadSheetData.mssData["0_0"] = molSpreadSheetCell_0_0
        molSpreadSheetData.mssData["0_1"] = molSpreadSheetCell_0_1
        molSpreadSheetData.mssData["1_0"] = molSpreadSheetCell_1_0
        molSpreadSheetData.mssData["1_1"] = molSpreadSheetCell_1_1


        then:
        assert molSpreadSheetData.findSpreadSheetActivity(row, column).eid == 1 as Long
        assert molSpreadSheetData.findSpreadSheetActivity(row, column).cid == 2 as Long
        assert molSpreadSheetData.findSpreadSheetActivity(row, column).sid == 3 as Long

        where:
        row | column
        0   | 0
        0   | 1
        1   | 0
        1   | 1
    }




    void "Test rowCount and colCount method"() {
        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        assertNotNull(molSpreadSheetData)

        molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader (molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'col 1')])
        molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader (molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'col 2')])
        molSpreadSheetData.mssHeaders << new MolSpreadSheetColumnHeader (molSpreadSheetColSubHeaderList:[new MolSpreadSheetColSubHeader(columnTitle:'col 3')])
        molSpreadSheetData.rowPointer[1 as Long] = 47
        molSpreadSheetData.rowPointer[2 as Long] = 48

        then:
        assert molSpreadSheetData.rowCount == 2
        assert molSpreadSheetData.columnCount == 3
    }

    void "Test rowCount and colCount method with null maps"() {
        given:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.mssHeaders = null
        molSpreadSheetData.rowPointer = null

        when:
        final int columnCount = molSpreadSheetData.columnCount

        and:
        final int rowCount = molSpreadSheetData.rowCount
        then:
        assert columnCount == 0
        assert rowCount == 0
    }



    void "Test should never happen -- empty rowPointer"() {
        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        assertNotNull(molSpreadSheetData)

        molSpreadSheetData.rowPointer = null

        then:
        assert molSpreadSheetData.rowCount == 0
    }




    void "Test constraints for molecular spreadsheet data"() {
        given:
        mockForConstraintsTests(MolSpreadSheetData)

        when:
        MolSpreadSheetData molSpreadSheetData = new MolSpreadSheetData()
        molSpreadSheetData.validate()
        !molSpreadSheetData.hasErrors()

        then:
        def mssData = molSpreadSheetData.mssData
        molSpreadSheetData.setMssData(null)
        assertFalse molSpreadSheetData.validate()
        molSpreadSheetData.setMssData(mssData)
        assertTrue molSpreadSheetData.validate()

        def rowPointer = molSpreadSheetData.rowPointer
        molSpreadSheetData.setRowPointer(null)
        assertFalse molSpreadSheetData.validate()
        molSpreadSheetData.setRowPointer(rowPointer)
        assertTrue molSpreadSheetData.validate()

        def columnPointer = molSpreadSheetData.columnPointer
        molSpreadSheetData.setColumnPointer(null)
        assertFalse molSpreadSheetData.validate()
        molSpreadSheetData.setColumnPointer(columnPointer)
        assertTrue molSpreadSheetData.validate()

        molSpreadSheetData.setMssHeaders(null)
        assertTrue molSpreadSheetData.validate()

    }

    void "test MolSpreadsheetDerivedMethod enum"() {
        given:

        when:
        String description = molSpreadsheetDerivedMethod.description()

        then:
        assert description == expectedDescription

        where:
        molSpreadsheetDerivedMethod                               | expectedDescription
        MolSpreadsheetDerivedMethod.Compounds_NoAssays_NoProjects | "Only compound(s); no assays or projects"
        MolSpreadsheetDerivedMethod.NoCompounds_Assays_NoProjects | "Only assay(s); no compounds or projects"
        MolSpreadsheetDerivedMethod.NoCompounds_NoAssays_Projects | "Only project(s); no assays or compounds"
    }
}
