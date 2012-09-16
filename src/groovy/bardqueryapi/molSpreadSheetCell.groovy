package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 9/14/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
class MolSpreadSheetCell {
    MolSpreadSheetCellType molSpreadSheetCellType = MolSpreadSheetCellType.unknown
    Object objInternalValue
    String strInternalValue = "f"
    BigDecimal numInternalValue = 1.2
    Integer  intInternalValue = 0
    MolSpreadSheetCell( String value, MolSpreadSheetCellType molSpreadSheetCellType ){
        this.molSpreadSheetCellType = molSpreadSheetCellType
        switch (this.molSpreadSheetCellType) {
            case MolSpreadSheetCellType.numeric :
                numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.percentageNumeric :
                numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.greaterThanNumeric :
                numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.lessThanNumeric :
                numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.identifier :
                intInternalValue = new Integer(value)
                break;
            case MolSpreadSheetCellType.string :
                strInternalValue = new String(value)
                break;
            default:
                objInternalValue = value
        }

    }
    @Override
    String toString() {
        String returnValue = ""
        switch (molSpreadSheetCellType) {
            case MolSpreadSheetCellType.numeric :
                returnValue = "${numInternalValue}"
                break;
            case MolSpreadSheetCellType.percentageNumeric :
                returnValue = "${numInternalValue} %"
                break;
            case MolSpreadSheetCellType.greaterThanNumeric :
                returnValue = "> ${numInternalValue}"
                break;
            case MolSpreadSheetCellType.lessThanNumeric :
                returnValue = "< ${numInternalValue}"
                break;
            case MolSpreadSheetCellType.identifier :
                returnValue = "${intInternalValue}"
                break;
            case MolSpreadSheetCellType.string :
                returnValue = "${strInternalValue}"
                break;
            default:
                returnValue = "${objInternalValue}"
        }
        returnValue
    }

}
//MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("4.2",MolSpreadSheetCellType.numeric)

enum  MolSpreadSheetCellType {
    lessThanNumeric,
    greaterThanNumeric,
    percentageNumeric,
    numeric,
    identifier,
    image,
    string,
    unknown
}