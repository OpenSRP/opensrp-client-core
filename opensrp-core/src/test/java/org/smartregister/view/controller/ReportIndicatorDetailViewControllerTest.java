package org.smartregister.view.controller;

import android.content.Context;

import com.google.gson.Gson;

import org.ei.drishti.dto.MonthSummaryDatum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.domain.Report;
import org.smartregister.view.contract.IndicatorReportDetail;

import java.util.Arrays;
import java.util.List;

public class ReportIndicatorDetailViewControllerTest {
    @Mock
    private Context context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetIndicatorReportsForGivenCategory() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = Arrays.asList(new MonthSummaryDatum("10", "2012", "2", "2", Arrays.asList("123", "456")));
        Report iudReport = new Report("IUD", "40", new Gson().toJson(monthlySummaries));

        ReportIndicatorDetailViewController controller = new ReportIndicatorDetailViewController(context, iudReport, "Family Planning");
        String indicatorReportDetail = controller.get();

        String expectedIndicatorReports = new Gson().toJson(new IndicatorReportDetail("Family Planning", "IUD Adoption", "IUD", "40", monthlySummaries));
        Assert.assertEquals(expectedIndicatorReports, indicatorReportDetail);
    }

    @Test
    public void shouldShowNAIfAnnualTargetNotAvailable() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = Arrays.asList(new MonthSummaryDatum("6", "2012", "2", "2", Arrays.asList("123", "456")),
                new MonthSummaryDatum("8", "2012", "2", "4", Arrays.asList("321", "654")));
        Report iudReport = new Report("IUD", null, new Gson().toJson(monthlySummaries));

        ReportIndicatorDetailViewController controller = new ReportIndicatorDetailViewController(context, iudReport, "Family Planning");
        String indicatorReportDetail = controller.get();

        String expectedIndicatorReports = new Gson().toJson(new IndicatorReportDetail("Family Planning", "IUD Adoption", "IUD", "NA", monthlySummaries));
        Assert.assertEquals(expectedIndicatorReports, indicatorReportDetail);
    }

}
