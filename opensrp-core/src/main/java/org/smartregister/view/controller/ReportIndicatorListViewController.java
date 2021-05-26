package org.smartregister.view.controller;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import org.ei.drishti.dto.MonthSummaryDatum;
import org.joda.time.LocalDate;
import org.smartregister.domain.Report;
import org.smartregister.domain.ReportIndicator;
import org.smartregister.domain.ReportsCategory;
import org.smartregister.repository.AllReports;
import org.smartregister.view.activity.ReportIndicatorDetailActivity;
import org.smartregister.view.contract.CategoryReports;
import org.smartregister.view.contract.IndicatorReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.AllConstants.CATEGORY_DESCRIPTION;
import static org.smartregister.AllConstants.INDICATOR_DETAIL;
import static org.smartregister.util.DateUtil.today;

public class ReportIndicatorListViewController {
    private final Context context;
    private final AllReports allReports;
    private final String category;
    private List<Report> reports;
    private ReportsCategory reportsCategory;

    public ReportIndicatorListViewController(Context context, AllReports allReports, String
            category) {
        this.context = context;
        this.allReports = allReports;
        this.category = category;
    }

    public String get() {
        reportsCategory = ReportsCategory.valueOf(category);

        reports = allReports.allFor(reportsCategory.indicators());

        List<IndicatorReport> indicatorReports = new ArrayList<IndicatorReport>();
        for (Report report : reports) {
            ReportIndicator indicator = report.reportIndicator();
            List<MonthSummaryDatum> monthSummaryData = report.monthlySummaries();
            if (monthSummaryData.size() == 0) {
                continue;
            }
            sortMonthlySummaries(monthSummaryData);
            MonthSummaryDatum currentMonthSummary = monthSummaryData.get(0);

            String currentMonth = today().monthOfYear().getAsString();
            String currentProgress =
                    currentMonthSummary.month().equals(currentMonth) ? currentMonthSummary
                            .currentProgress() : "0";
            String annualTarget = (isBlank(report.annualTarget())) ? "NA" : report.annualTarget();

            indicatorReports.add(new IndicatorReport(indicator.name(), indicator.description(),
                    annualTarget, currentProgress, currentMonth, currentMonthSummary.year(),
                    currentMonthSummary.aggregatedProgress()));
        }

        return new Gson()
                .toJson(new CategoryReports(reportsCategory.description(), indicatorReports));
    }

    public void sortMonthlySummaries(List<MonthSummaryDatum> monthSummaryData) {
        Collections.sort(monthSummaryData, new Comparator<MonthSummaryDatum>() {
            @Override
            public int compare(MonthSummaryDatum monthSummaryDatum, MonthSummaryDatum
                    anotherMonthSummaryDatum) {
                LocalDate date = new LocalDate().withYear(parseInt(anotherMonthSummaryDatum.year()))
                        .withMonthOfYear(parseInt(anotherMonthSummaryDatum.month()));
                LocalDate anotherDate = new LocalDate().withYear(parseInt(monthSummaryDatum.year()))
                        .withMonthOfYear(parseInt(monthSummaryDatum.month()));
                return date.compareTo(anotherDate);
            }
        });
    }

    public void startReportIndicatorDetail(String indicator) {
        for (Report report : reports) {
            if (report.reportIndicator().name().equals(indicator)) {
                Intent intent = new Intent(context.getApplicationContext(),
                        ReportIndicatorDetailActivity.class);
                intent.putExtra(INDICATOR_DETAIL, report);
                intent.putExtra(CATEGORY_DESCRIPTION, reportsCategory.description());
                context.startActivity(intent);
                return;
            }
        }
    }
}
