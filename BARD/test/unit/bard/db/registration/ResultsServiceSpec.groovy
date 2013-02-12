package bard.db.registration

import bard.db.dictionary.Element
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentMeasure
import bard.db.experiment.Result
import bard.db.experiment.ResultContextItem
import bard.db.experiment.Substance
import grails.buildtestdata.mixin.Build
import grails.test.mixin.services.ServiceUnitTestMixin
import spock.lang.Unroll

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(ServiceUnitTestMixin)
@Build([Assay, Measure, AssayContext, AssayContextItem, AssayContextMeasure, Element, Substance, Experiment, ExperimentMeasure])
class ResultsServiceSpec extends spock.lang.Specification {

    void setup() {
        // Setup logic here
    }

    void 'test create template from assay'() {
        when:
        // an assay with 1 measure, and two contexts, one of which is associated with the measure
        // yes, this is a lot scaffolded data, but I think that is more a result of the design of the data model
        // and I prefer using the real domain then mocking much of it out.
        Assay assay = Assay.build()
        Experiment experiment = Experiment.build(assay:assay)
        AssayContext assayContext = AssayContext.build(assay: assay)
        AssayContext measureContext = AssayContext.build(assay: assay)
        AssayContextItem assayContextItem = AssayContextItem.build(assayContext: assayContext, attributeType: AttributeType.Free, attributeElement: Element.build(label:"cell line"))
        Measure measure = Measure.build(resultType: Element.build(label:"ec50"))
        AssayContextMeasure assayContextMeasure = AssayContextMeasure.build(assayContext: measureContext, measure: measure)
        AssayContextItem measureContextItem = AssayContextItem.build(assayContext: measureContext, attributeType: AttributeType.Free, attributeElement: Element.build(label:"hill slope"))
        measureContext.assayContextMeasures = [assayContextMeasure] as Set
        assay.assayContexts = [assayContext, measureContext]
        assay.measures = [measure] as Set
        experiment.experimentMeasures = [ExperimentMeasure.build(measure: measure)] as Set

        ResultsService service = new ResultsService();
        service.itemService = new ItemService()

        ResultsService.Template template = service.generateMaxSchema(experiment)

        then:
        template.constantItems.size() == 1
        template.constantItems.get(0).name == "cell line"
        template.experiment == experiment
        template.columns.size() == 2
        template.columns.get(0).name == "ec50"
        template.columns.get(1).name == "hill slope"
    }

    def createSampleFile() {
        Assay assay = Assay.build()
        Experiment experiment = Experiment.build(assay:assay)
        Measure inhibitionMeasure = Measure.build()
        Measure ec50Measure = Measure.build()

        def columns = [new ResultsService.Column("Inhibition", inhibitionMeasure), new ResultsService.Column("EC50", ec50Measure)]
        def constantItems = []
        ResultsService.Template template = new ResultsService.Template(experiment: experiment, columns: columns, constantItems: constantItems)

        String sample = ",Experiment ID,123\n"+
                "\n" +
                "Row #,Substance,Replicate #,Parent Row #,Inhibition,EC50\n" +
                "1,100,,,10,\n" +
                "2,100,,1,,5\n"

        return [sample: sample, template: template]
    }

    void 'test initial parse'() {
        when:
        def fixture = createSampleFile()

        ResultsService service = new ResultsService();
        service.itemService = new ItemService()

        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()
        ResultsService.InitialParse result = service.initialParse(new StringReader(fixture.sample), errors, fixture.template)

        then:
        result.constants.size() == 1
        result.constants.get("Experiment ID") == "123"
        result.rows.size() == 2
        !errors.hasErrors()

        when:
        ResultsService.Row row0 = result.rows.get(0)
        ResultsService.Row row1 = result.rows.get(1)

        then:
        row0.parentRowNumber == null
        row0.cells.size() == 1
        row0.cells.get(0).value == 10
        row0.sid == 100
        row0.rowNumber == 1
        row0.replicate == null

        row1.parentRowNumber == 1
        row1.cells.size() == 1
        row1.cells.get(0).value == 5
    }

    @Unroll("test parsing problem: #desc")
    void 'test parse failures'() {
        when:
        def fixture = createSampleFile()
        // break apart table
        def table = fixture.sample.split("\n").collect { it.split(",") as List }
        // mutate
        def r = table.get(row)
        while (column >= r.size()) {
            r.add("")
        }
        r.set(column, newValue)
        // reassemble
        def sample = (table.collect { it.join(",") }).join("\n")

        ResultsService service = new ResultsService();
        service.itemService = new ItemService()

        ResultsService.ImportSummary errors = new ResultsService.ImportSummary()
        ResultsService.InitialParse result = service.initialParse(new StringReader(sample), errors, fixture.template)

        then:
        errors.errors.size() == expectedErrorCount

        where:
        desc                     | row | column | newValue | expectedErrorCount
        "bad constant section"   | 0   | 0      | "x"      | 1
        "bad constant name"      | 0   | 1      | "x"      | 1
        "bad fixed column"       | 2   | 0      | "x"      | 1
        "bad dynamic column"     | 2   | 4      | "x"      | 1
        "duplicate column"       | 2   | 4      | "EC50"   | 1
        "duplicate row number"   | 4   | 0      | "1"      | 1
        "extra column"           | 3   | 6      | "1"      | 1
        "missing row number"     | 3   | 0      | ""       | 1
    }

    @Unroll("test parsing cell containing #cellString")
    void 'test parse measure cell'() {
        when:
        Measure measure = Measure.build()
        ResultsService.Column column = new ResultsService.Column("x", measure)

        ResultsService.Cell cell = column.parseValue(cellString)

        then:
        cell.value == expectedValue
        cell.maxValue == maxVal
        cell.minValue == minVal
        cell.qualifier == expectedQualifier

        where:
        desc                     | cellString | expectedValue | expectedQualifier | minVal | maxVal
        "simple scalar"          | "1"        | 1.0           | "="               | null   | null
        "scientific notation"    | "1e4"      | 1e4           | "="               | null   | null
        "including qualifier"    | "<10"      | 10.0          | "<"               | null   | null
        "spaced qualifier"       | ">> 10"    | 10.0          | ">>"              | null   | null
        "range"                  | "2-3"      | null          | null              | 2.0    | 3.0
    }

    void 'test creating measure result'() {
        when:
        def substance = Substance.build()
        substance.save()

        then:
        substance.id != null

        when:
        ResultsService service = new ResultsService();

        def resultType = Element.build()
        def measure = Measure.build(resultType: resultType)
        def column = new ResultsService.Column("a", measure)
        def cell = new ResultsService.Cell(column: column, qualifier: "=", value: 5)
        def row = new ResultsService.Row(rowNumber: 1, sid: substance.id, cells: [cell], replicate: 1)
        def parse = new ResultsService.InitialParse(constants: [:], rows: [row])

        def errors = new ResultsService.ImportSummary()

        def measuresForItem = [:]
        measuresForItem.put(AssayContextItem.build(), [measure])

        def results = service.createResults( parse, errors, [:])

        then:
        !errors.hasErrors()
        results.size() == 1
        Result result = results.first()
        result.replicateNumber == 1
        result.qualifier == "= "
        result.valueNum == 5.0
        result.resultType == resultType
        result.substance == substance
    }

    void 'test creating measure and item result'() {
        when:
        ResultsService service = new ResultsService();
        ItemService itemService = new ItemService()

        def substance = Substance.build()
        substance.save()

        def attribute = Element.build()
        def item = itemService.getLogicalItems([AssayContextItem.build(attributeElement: attribute)])[0]
        def resultType = Element.build()
        def measure = Measure.build(resultType: resultType)
        def measureColumn = new ResultsService.Column( "a", measure)
        def itemColumn = new ResultsService.Column( "b", item)

        // construct a row of two cells: a measurement and an associated context
        def mCell = new ResultsService.Cell(column: measureColumn, qualifier: "=", value: 5)
        def iCell = new ResultsService.Cell(column: itemColumn, qualifier: "<", value: 15)
        def row = new ResultsService.Row(rowNumber: 1, replicate: 1, sid: substance.id, cells: [mCell, iCell])
        def parse = new ResultsService.InitialParse(constants: [:], rows: [row])

        def errors = new ResultsService.ImportSummary()

        def measuresForItem = [:]
        measuresForItem.put(item, [measure])

        def results = service.createResults( parse, errors, measuresForItem)

        then:
        !errors.hasErrors()
        results.size() == 1
        Result rMeasure = results.first()
        rMeasure.replicateNumber == 1
        rMeasure.qualifier == "= "
        rMeasure.valueNum == 5.0
        rMeasure.resultType == resultType
        rMeasure.substance == substance
        rMeasure.resultContextItems.size() == 1
        ResultContextItem rci = rMeasure.resultContextItems.first()
        rci.qualifier == "<"
        rci.valueNum == 15.0
        rci.attributeElement == attribute
    }

    @Unroll("test parsing context item containing #cellString")
    void 'test parse non-element context item cell'() {
        when:
        ItemService itemService = new ItemService()
        def item = itemService.getLogicalItems([AssayContextItem.build(attributeElement: Element.build(), attributeType: AttributeType.Free)])[0]
        ResultsService.Column column = new ResultsService.Column("x", item)

        ResultsService.Cell cell = column.parseValue(cellString)

        then:
        cell.value == expectedValue
        cell.maxValue == maxVal
        cell.minValue == minVal
        cell.qualifier == expectedQualifier

        where:
        desc                     | cellString | expectedValue | expectedQualifier | minVal | maxVal
        "simple scalar"          | "1"        | 1.0           | "="              | null   | null
        "scientific notation"    | "1e4"      | 1e4           | "="              | null   | null
        "including qualifier"    | "<10"      | 10.0          | "<"               | null   | null
        "spaced qualifier"       | ">> 10"    | 10.0          | ">>"              | null   | null
        "range"                  | "2-3"      | null          | null              | 2.0    | 3.0
    }

    void 'test parse context item from element list'() {
        when:
        def tubaElement = Element.build(label: "tuba")
        def attribute = Element.build()
        def context = AssayContext.build()
        def tubaItem = AssayContextItem.build(attributeElement: attribute, attributeType: AttributeType.List, valueElement: tubaElement, assayContext: context)
        def trumpetElement = Element.build(label: "trumpet")
        def trumpetItem = AssayContextItem.build(attributeElement: attribute, attributeType: AttributeType.List, valueElement: trumpetElement, assayContext: context)
        ItemService itemService = new ItemService()
        def item = itemService.getLogicalItems([tubaItem, trumpetItem])[0]
        ResultsService.Column column = new ResultsService.Column("x",item)

        ResultsService.Cell c0 = column.parseValue("tuba")

        then:
        c0.element == tubaElement

        when:
        ResultsService.Cell c1 = column.parseValue("trumpet")

        then:
        c1.element == trumpetElement

        then:
        !(column.parseValue("pony") instanceof ResultsService.Cell)
    }


    void 'test parse context item from value list'() {
        when:
        def attribute = Element.build()
        def context = AssayContext.build()
        def small = AssayContextItem.build(attributeElement: attribute, assayContext: context, attributeType: AttributeType.List, valueNum: 1e2 )
        def large = AssayContextItem.build(attributeElement: attribute, assayContext: context, attributeType: AttributeType.List, valueNum: 2e2 )
        ItemService itemService = new ItemService()
        def item = itemService.getLogicalItems([large, small])[0]
        ResultsService.Column column = new ResultsService.Column("x", item)

        ResultsService.Cell c0 = column.parseValue("1e2")

        then:
        c0.value == 1e2

        when:
        ResultsService.Cell c1 = column.parseValue("100")

        then:
        c1.value == 1e2

        when:
        ResultsService.Cell c2 = column.parseValue("200.0")

        then:
        c2.value == 2e2

        when:
        ResultsService.Cell c3 = column.parseValue("200.1")

        then:
        c3.value == 2e2

        then:
        !(column.parseValue("pony") instanceof ResultsService.Cell)
    }


}
