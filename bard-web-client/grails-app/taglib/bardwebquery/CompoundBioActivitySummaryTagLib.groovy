package bardwebquery

class CompoundBioActivitySummaryTagLib {
    def assayDescription = { attrs, body ->

        out << "<p>" << attrs.name << " (${attrs.adid})" << "</p>"
    }

    def projectDescription = { attrs, body ->

        out << "<p>" << attrs.name << " (${attrs.pid})" << "</p>"
    }

    def experimentDescription = { attrs, body ->

        out << "<p>" << attrs.name << " (${attrs.bardExperimentId})" << "</p>"
    }

    def curvePlot = { attrs, body ->

        out << """<img alt="curve here" title="title here"
                    src="${
            createLink(
                    controller: 'doseResponseCurve',
                    action: 'doseResponseCurve',
                    params: [
                            sinf: attrs?.curveFitParameters?.sInf,
                            s0: attrs?.curveFitParameters?.s0,
                            slope: attrs?.slope,
                            hillSlope: attrs?.curveFitParameters?.hillCoef,
                            concentrations: attrs.concentrationSeries,
                            activities: attrs.activitySeries,
                            xAxisLabel: attrs.xAxisLabel,
                            yAxisLabel: attrs.yAxisLabel,
                            yNormMin: attrs?.yMinimum,
                            yNormMax: attrs?.yMaximum
                    ]
            )
        }"/>
        """

        out << "<p><b>${attrs?.title?.value?.left?.value ?: ''}: ${attrs?.title?.value?.right?.value ?: ''}</b></p>"
        out << "<p>sinf: ${attrs?.curveFitParameters?.sInf ?: ''}</p>"
        out << "<p>s0: ${attrs?.curveFitParameters?.s0 ?: ''}</p>"
        out << "<p>hillSlope: ${attrs?.curveFitParameters?.hillCoef ?: ''}</p>"
        out << "<p>slope: ${attrs?.attrs?.slope ?: ''}</p>"
    }

    def curveValues = { attrs, body ->

        out << "<h5>${attrs.title.value.left.value}</h5>"

        int i = 0
        while (i < attrs.concentrationSeries.size()) {
            out << "<p><small>${attrs.activitySeries[i]} ${attrs.responseUnit ? '[' + attrs.responseUnit + ']' : ''} @ ${attrs.concentrationSeries[i]} ${attrs.testConcentrationUnit}</small></p>"
            i++
        }
    }
}
