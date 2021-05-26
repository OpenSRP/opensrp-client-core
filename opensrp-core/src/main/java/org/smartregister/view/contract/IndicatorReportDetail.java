package org.smartregister.view.contract;

import org.ei.drishti.dto.MonthSummaryDatum;

import java.util.List;

public class IndicatorReportDetail {
    private final String categoryDescription;
    private final String description;
    private final String identifier;
    private final String annualTarget;
    private final List<MonthSummaryDatum> monthlySummaries;

    public IndicatorReportDetail(String categoryDescription, String description, String
            identifier, String annualTarget, List<MonthSummaryDatum> monthlySummaries) {
        this.categoryDescription = categoryDescription;
        this.description = description;
        this.identifier = identifier;
        this.annualTarget = annualTarget;
        this.monthlySummaries = monthlySummaries;
    }
}
