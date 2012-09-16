package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 9/14/12
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
class MolSpreadSheetData {
    LinkedHashMap<String,MolSpreadSheetCell> mssData
    LinkedHashMap<Long,Integer> rowPointer
    List mssHeaders = new ArrayList()

    MolSpreadSheetData()  {
        mssData = new LinkedHashMap<String,MolSpreadSheetCell> ()
        rowPointer = new LinkedHashMap<Long,Integer>()
        mssHeaders = new ArrayList()
    }

    // test data
    MolSpreadSheetData(String s)  {
        mssData = new LinkedHashMap<String,MolSpreadSheetCell> ()
        rowPointer = new LinkedHashMap<Long,Integer>()
        mssHeaders = new ArrayList()
        mssData.put("0_0", new MolSpreadSheetCell("1",MolSpreadSheetCellType.string))
        mssData.put("1_0", new MolSpreadSheetCell("3888711",MolSpreadSheetCellType.identifier))
        mssData.put("2_0", new MolSpreadSheetCell("0.0000144",MolSpreadSheetCellType.numeric))
        mssData.put("3_0", new MolSpreadSheetCell("0.00000529",MolSpreadSheetCellType.numeric))
        mssData.put("4_0", new MolSpreadSheetCell("0.000000823",MolSpreadSheetCellType.numeric))
        mssHeaders.add("Chemical Structure")
        mssHeaders.add("CID")
        mssHeaders.add("DNA polymerase (Q9Y253) ADID : 1 IC50")
        mssHeaders.add("Serine-protein kinase (Q13315) ADID : 1 IC50")
        mssHeaders.add("Tyrosine-DNA phosphodiesterase 1 (Q9NUW8) ADID: 514789")
        rowPointer.put(5342L,0)
    }

    String displayValue(int rowCnt, int colCnt) {
        String returnValue
        String key = "${rowCnt}_${colCnt}"
        if (mssData.containsKey(key)) {
            returnValue = mssData["${rowCnt}_${colCnt}"].toString()
        } else
            returnValue = "error: val unknown"
        returnValue
    }


    int getRowCount(){
        if (rowPointer == null)
            return 0
        else
            return rowPointer.size()
    }

    int getColumnCount(){
        if (mssHeaders == null)
            return 0
        else
            return mssHeaders.size()
    }

}
