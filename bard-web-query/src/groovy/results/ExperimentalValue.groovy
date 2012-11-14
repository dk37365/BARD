package results

import molspreadsheet.MolSpreadSheetCellType
import molspreadsheet.MolSpreadSheetCellUnit
import org.apache.commons.lang.NotImplementedException

import java.text.NumberFormat
import java.math.MathContext

/**
 * ExperimentalValue is designed to present numbers with a number of significant digits
 * that is suitable for scientists likely to use Bard
 */
class ExperimentalValue {
    final String NO_ACTIVITY_STRING = "(no activity)"
    final int DESIRED_PRECISION = 3
    BigDecimal value = 0.0
    ExperimentalValueUnit experimentalValueUnit = ExperimentalValueUnit.unknown  // describes what we hold
    ExperimentalValueUnit insistOnOutputUnits = ExperimentalValueUnit.unknown   // if not unknown then force output to this unit
    Boolean activity = true
    ExperimentalValueType experimentalValueType = ExperimentalValueType.unknown
    Boolean printUnits = true
    Boolean valueNegative = false



    /**
     * ExperimentalValue ctor
     * @param value
     * @param experimentalValueUnit
     * @param experimentalValueType
     * @param activity
     */
    public ExperimentalValue(BigDecimal value,
                             ExperimentalValueUnit experimentalValueUnit,
                             ExperimentalValueType experimentalValueType,
                             Boolean activity = true) {
        this.value = value
        this.experimentalValueUnit = experimentalValueUnit
        if (this.experimentalValueUnit==null)  {
            this.experimentalValueUnit =  ExperimentalValueUnit.unknown
        }
        this.experimentalValueType = experimentalValueType
        this.activity = activity
        if (this.value < 0) {
            this.value = 0 - this.value
            valueNegative = true
        }
    }

    /**
     *  ExperimentalValue ctor
     * @param value
     * @param printUnits
     */
    public ExperimentalValue(BigDecimal value,
                             Boolean printUnits) {
        this.value = value
        this.experimentalValueUnit = ExperimentalValueUnit.Molar
        this.experimentalValueType = ExperimentalValueType.numeric
        this.printUnits = printUnits
        if (this.value < 0) {
            this.value = 0 - this.value
            valueNegative = true
        }
    }


    /**
     *  ExperimentalValue ctor
     * @param value
     * @param printUnits
     */
    public ExperimentalValue(Double value) {
        this.value = value
        this.experimentalValueUnit = ExperimentalValueUnit.Micromolar
        this.experimentalValueType = ExperimentalValueType.numeric
        this.printUnits = false
        if (this.value < 0) {
            this.value = 0 - this.value
            valueNegative = true
        }
    }




    /**
     * The rest of the methods are here to support toString, since this is the one that converts
     * a numerical value into a string with the right degree of precision
     * @return
     */
    @Override
    String toString() {
        StringBuilder stringBuilder = new StringBuilder()
        if (!activity) {
            stringBuilder.append(NO_ACTIVITY_STRING)
        }
        else if ((experimentalValueType == ExperimentalValueType.lessThanNumeric) ||
                (experimentalValueType == ExperimentalValueType.greaterThanNumeric) ||
                (experimentalValueType == ExperimentalValueType.percentageNumeric) ||
                (experimentalValueType == ExperimentalValueType.numeric)) {
            if (value != 0)  {
                stringBuilder <<= prepend(experimentalValueType, valueNegative)
                stringBuilder <<= "${roundoffToDesiredPrecision(deliverDesiredValue())}"
                stringBuilder <<= append(experimentalValueType)
                if (printUnits) {
                    stringBuilder <<= experimentalValueUnit.toString()
                }
            }  else { // 0 is a special case
                stringBuilder <<= append(experimentalValueType)
                stringBuilder <<= "0."
                (DESIRED_PRECISION-1).times {
                    stringBuilder <<= "0"
                }
            }
        } else {
            stringBuilder.append(deliverDesiredValue())
        }
        stringBuilder.toString()
    }

    /**
     * Treat values differently based on the number of digits. Values are reaching
     * this routine must already have undergone unit conversion so that they are neither
     * < .01 or else > 1000.
     *
     * @param bigDecimal
     * @return
     */
    String roundoffToDesiredPrecision(BigDecimal bigDecimal) {
        BigDecimal displayVal = bigDecimal
        Boolean defaultToEngineeringNotation = false
        NumberFormat numberFormat = NumberFormat.instance
        if (((new BigDecimal("0.01")).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal("0.1")).compareTo(bigDecimal) > 0)) {
            defaultToEngineeringNotation = true
        } else if (((new BigDecimal("0.1")).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal("1")).compareTo(bigDecimal) > 0)) {
            numberFormat.setMinimumFractionDigits(0)
            numberFormat.setMaximumFractionDigits(DESIRED_PRECISION)
        } else if (((new BigDecimal("1")).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal("10")).compareTo(bigDecimal) > 0)) {
            numberFormat.setMinimumFractionDigits(0)
            numberFormat.setMaximumFractionDigits(DESIRED_PRECISION - 1)
        } else if (((new BigDecimal("10")).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal("100")).compareTo(bigDecimal) > 0)) {
            numberFormat.setMinimumFractionDigits(0)
            numberFormat.setMaximumFractionDigits(DESIRED_PRECISION - 2)
        } else if (((new BigDecimal("100")).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal("1000")).compareTo(bigDecimal) > 0)) {
            numberFormat.setMinimumFractionDigits(0)
            numberFormat.setMaximumFractionDigits(DESIRED_PRECISION - 3)
        } else {      // If someone insists on an output format, or else if the numbers are absurdly big or small
            //  then give up on trying to be pretty and put that number into engineering format
            defaultToEngineeringNotation = true
        }
        if (!defaultToEngineeringNotation) {
            numberFormat.format(displayVal.doubleValue())
        }
        else {
            MathContext mathContext = new MathContext(DESIRED_PRECISION)
            displayVal.round(mathContext).toEngineeringString()
        }

    }

    /**
     *  deliverDesiredValue checks to see whether the user is insisting on output units are not.
     * @return
     */
    BigDecimal deliverDesiredValue() {
        if (insistOnOutputUnits != ExperimentalValueUnit.unknown) {  // the calling routine wants to set the output units
            insistOnOutputUnit()
        } else { // pick the optimum units based on the value of the number
            chooseOutputUnit()
        }
        value
    }

    /**
     * Select a suitable output unit based on the value of the number. Note the plan is to iterate through the different options
     * rather than trying to jump to the right one.
     * @return
     */
    void chooseOutputUnit() {
        Boolean keepGoing = (value < 0.1) || (value >= 1000)
        while (keepGoing) {
            if ((value < 0.1) &&
                    (experimentalValueUnit.decimalPlacesFromMolar > ExperimentalValueUnit.SmallestPossibleUnit.decimalPlacesFromMolar)) {
                performUnitNormalization(ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar),
                        ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar - 3))
            } else if ((value >= 1000) &&
                    (experimentalValueUnit.decimalPlacesFromMolar < ExperimentalValueUnit.LargestPossibleUnit.decimalPlacesFromMolar)) {
                performUnitNormalization(ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar),
                        ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar + 3))
            } else {
                keepGoing = false
            }
        }
    }

    /**
     * What to do with the calling routine knows what units it wants to use
     * @return
     */
    void insistOnOutputUnit() {
        Boolean keepGoing = (insistOnOutputUnits == ExperimentalValueUnit.unknown) ? false : (experimentalValueUnit != insistOnOutputUnits)
        while (keepGoing) {
            if ((experimentalValueUnit.decimalPlacesFromMolar > insistOnOutputUnits.decimalPlacesFromMolar) &&
                    (experimentalValueUnit.decimalPlacesFromMolar > ExperimentalValueUnit.SmallestPossibleUnit.decimalPlacesFromMolar)) {
                performUnitNormalization(ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar),
                        ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar - 3))
            } else if ((experimentalValueUnit.decimalPlacesFromMolar < insistOnOutputUnits.decimalPlacesFromMolar) &&
                    (experimentalValueUnit.decimalPlacesFromMolar < ExperimentalValueUnit.LargestPossibleUnit.decimalPlacesFromMolar)) {
                performUnitNormalization(ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar),
                        ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar + 3))
            } else {
                keepGoing = false
            }
        }
    }

    /**
     *  The routine we call each time we want to move up or down the scale, changing the magnitude of the results by
     *  a factor of 1000
     * @param inComingUnit
     * @param outGoingUnit
     */
    void performUnitNormalization(ExperimentalValueUnit inComingUnit, ExperimentalValueUnit outGoingUnit) {
        if ((inComingUnit != ExperimentalValueUnit.unknown) &&
                (outGoingUnit != ExperimentalValueUnit.unknown)) {
            int unitSwap = inComingUnit.decimalPlacesFromMolar - outGoingUnit.decimalPlacesFromMolar
            value = value.scaleByPowerOfTen(unitSwap)
            experimentalValueUnit = outGoingUnit
        }
    }

    /**
     * for some of the values we want to display we might want to pretend they < or >.  Note also
     * that this is where a number becomes negative -- the numbers restore inside this class are
     * greater than zero, but may utilize the switch valueNegative to indicate that the value should
     * considered to be less than zero.
     *
     * @param experimentalValueType
     * @param valueNegative
     * @return
     */
    String prepend(ExperimentalValueType experimentalValueType, Boolean valueNegative) {
        StringBuilder stringBuilder = new StringBuilder()
        switch (experimentalValueType) {
            case ExperimentalValueType.lessThanNumeric:
                stringBuilder.append("< ")
                break;
            case ExperimentalValueType.greaterThanNumeric:
                stringBuilder.append("> ")
                break;
            default:
                stringBuilder.append("")
        }
        if (valueNegative) {
            stringBuilder.append("-")
        }
        stringBuilder.toString()
    }

    /**
     * Anything that might go after the number. Right now it's only a percentage sign.
     * @param experimentalValueType
     * @return
     */
    String append(ExperimentalValueType experimentalValueType) {
        String returnValue
        switch (experimentalValueType) {
            case ExperimentalValueType.percentageNumeric:
                returnValue = " %"
                break;
            default:
                returnValue = ""
        }
        returnValue
    }

}

/**
 * enumeration to hold units
 */
enum ExperimentalValueUnit {
    Molar(" M", 0),
    Millimolar(" mM", -3),
    Micromolar(" uM", -6),
    Nanomolar(" nM", -9),
    Picomolar(" pM", -12),
    Femtomolar(" fM", -15),
    Attamolar(" aM", -18),
    Zeptomolar(" zM", -21),
    Yoctomolar(" yM", -24),
    unknown("", 0);

    static ExperimentalValueUnit convert(MolSpreadSheetCellUnit molSpreadSheetCellUnit) {
        switch (molSpreadSheetCellUnit) {
            case MolSpreadSheetCellUnit.Molar:
                return ExperimentalValueUnit.Molar;
            case MolSpreadSheetCellUnit.Millimolar:
                return ExperimentalValueUnit.Millimolar;
            case MolSpreadSheetCellUnit.Micromolar:
                return ExperimentalValueUnit.Micromolar;
            case MolSpreadSheetCellUnit.Nanomolar:
                return ExperimentalValueUnit.Nanomolar;
            case MolSpreadSheetCellUnit.Picomolar:
                return ExperimentalValueUnit.Picomolar;
            case MolSpreadSheetCellUnit.Femtomolar:
                return ExperimentalValueUnit.Femtomolar;
            case MolSpreadSheetCellUnit.Attamolar:
                return ExperimentalValueUnit.Attamolar;
            case MolSpreadSheetCellUnit.Zeptomolar:
                return ExperimentalValueUnit.Zeptomolar;
            case MolSpreadSheetCellUnit.Yoctomolar:
                return ExperimentalValueUnit.Yoctomolar;
            case MolSpreadSheetCellUnit.unknown:
                return ExperimentalValueUnit.unknown;
            default:
                throw new NotImplementedException(molSpreadSheetCellUnit.toString() + " not yet implemented");
        }
    }

    private final String value
    private final int decimalPlacesFromMolar

    static ExperimentalValueUnit LargestPossibleUnit = Molar
    static ExperimentalValueUnit SmallestPossibleUnit = Yoctomolar

    ExperimentalValueUnit(String value, int decimalPlacesFromMolar) {
        this.value = value;
        this.decimalPlacesFromMolar = decimalPlacesFromMolar;
    }

    public int getDecimalPlacesFromMolar() {
        decimalPlacesFromMolar
    }

    public String toString() {
        return value;
    }


    public static ExperimentalValueUnit getByDecimalValue(int value) {
        for (final ExperimentalValueUnit element : EnumSet.allOf(ExperimentalValueUnit)) {
            if (element.decimalPlacesFromMolar == value) {
                return element;
            }
        }
        return null;
    }

    public static ExperimentalValueUnit getByValue(String value) {
        ExperimentalValueUnit returnValue = ExperimentalValueUnit.unknown
        if (value != null ) {
            for (final ExperimentalValueUnit element : EnumSet.allOf(ExperimentalValueUnit)) {
                if (element.value.trim().toUpperCase() == value.trim().toUpperCase()) {
                    returnValue =  element;
                    break;
                }
            }
        }
        return returnValue;
    }


}

/**
 * Enumeration to hold the type of the value
 */
enum ExperimentalValueType {
    lessThanNumeric,
    greaterThanNumeric,
    percentageNumeric,
    numeric,
    identifier,
    image,
    string,
    unknown,
    unhandled;

    static ExperimentalValueType convert(MolSpreadSheetCellType molSpreadSheetCellType) {
        switch (molSpreadSheetCellType) {
            case MolSpreadSheetCellType.lessThanNumeric:
                return ExperimentalValueType.lessThanNumeric;
            case MolSpreadSheetCellType.greaterThanNumeric:
                return ExperimentalValueType.greaterThanNumeric;
            case MolSpreadSheetCellType.percentageNumeric:
                return ExperimentalValueType.percentageNumeric;
            case MolSpreadSheetCellType.numeric:
                return ExperimentalValueType.numeric;
            case MolSpreadSheetCellType.image:
                return ExperimentalValueType.image;
            case MolSpreadSheetCellType.string:
                return ExperimentalValueType.string;
            case MolSpreadSheetCellType.unknown:
                return ExperimentalValueType.unknown;
            default:
                throw new NotImplementedException("${molSpreadSheetCellType} not yet implemented");
        }
    }

}
