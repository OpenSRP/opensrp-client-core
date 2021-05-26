package org.smartregister.view.contract;

import java.util.List;

public class IndicatorReportCases {
    private final String month;
    private final List<Beneficiary> beneficiaries;

    public IndicatorReportCases(String month, List<Beneficiary> beneficiaries) {
        this.month = month;
        this.beneficiaries = beneficiaries;
    }
}
