package org.smartregister.view.controller;

import android.content.Context;

import com.google.gson.Gson;

import org.smartregister.domain.ReportIndicator;
import org.smartregister.view.contract.Beneficiary;
import org.smartregister.view.contract.IndicatorReportCases;

import java.util.List;

import static org.smartregister.domain.ReportIndicator.valueOf;

public class ReportIndicatorCaseListViewController {
    private final Context context;
    private final List<String> caseIds;
    private final String month;
    private String indicator;

    public ReportIndicatorCaseListViewController(Context context, String indicator, List<String>
            caseIds, String month) {
        this.context = context;
        this.indicator = indicator;
        this.caseIds = caseIds;
        this.month = month;
    }

    public String get() {
        List<Beneficiary> beneficiaries = valueOf(indicator).fetchCaseList(caseIds);
        return new Gson().toJson(new IndicatorReportCases(month, beneficiaries));
    }

    public void startReportIndicatorCaseDetail(String caseId) {
        ReportIndicator.valueOf(indicator).startCaseDetailActivity(context, caseId);
    }
}
