package org.smartregister.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ei.drishti.dto.MonthSummaryDatum;

import java.io.Serializable;
import java.util.List;

import static org.smartregister.domain.ReportIndicator.parseToReportIndicator;

public class Report implements Serializable {
    private final String indicator;
    private final String annualTarget;
    private final String monthlySummaries;

    public Report(String indicator, String annualTarget, String monthlySummaries) {
        this.indicator = indicator;
        this.annualTarget = annualTarget;
        this.monthlySummaries = monthlySummaries;
    }

    public String indicator() {
        return indicator;
    }

    public ReportIndicator reportIndicator() {
        return parseToReportIndicator(indicator);
    }

    public String annualTarget() {
        return annualTarget;
    }

    public String monthlySummariesJSON() {
        return monthlySummaries;
    }

    public List<MonthSummaryDatum> monthlySummaries() {
        return new Gson().fromJson(monthlySummaries, new TypeToken<List<MonthSummaryDatum>>() {
        }.getType());
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
