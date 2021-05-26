package org.smartregister.view.controller;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import org.ei.drishti.dto.MonthSummaryDatum;
import org.smartregister.domain.Report;
import org.smartregister.view.activity.ReportIndicatorCaseListActivity;
import org.smartregister.view.contract.IndicatorReportDetail;

import java.util.ArrayList;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.AllConstants.CASE_IDS;
import static org.smartregister.AllConstants.INDICATOR;
import static org.smartregister.AllConstants.MONTH;

public class ReportIndicatorDetailViewController {
    private final Context context;
    private final Report indicatorDetails;
    private String categoryDescription;

    public ReportIndicatorDetailViewController(Context context, Report indicatorDetails, String
            categoryDescription) {
        this.context = context;
        this.indicatorDetails = indicatorDetails;
        this.categoryDescription = categoryDescription;
    }

    public String get() {
        String annualTarget =
                (isBlank(indicatorDetails.annualTarget())) ? "NA" : indicatorDetails.annualTarget();

        return new Gson().toJson(new IndicatorReportDetail(categoryDescription,
                indicatorDetails.reportIndicator().description(),
                indicatorDetails.reportIndicator().name(), annualTarget,
                indicatorDetails.monthlySummaries()));
    }

    public void startReportIndicatorCaseList(String month) {
        for (MonthSummaryDatum summary : indicatorDetails.monthlySummaries()) {
            if (summary.month().equals(month)) {
                Intent intent = new Intent(context.getApplicationContext(),
                        ReportIndicatorCaseListActivity.class);
                intent.putExtra(MONTH, month);
                intent.putExtra(INDICATOR, indicatorDetails.reportIndicator().name());
                intent.putStringArrayListExtra(CASE_IDS,
                        new ArrayList<String>(summary.externalIDs()));
                context.startActivity(intent);
                return;
            }
        }
    }
}
