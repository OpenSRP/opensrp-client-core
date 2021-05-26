package org.smartregister.view.controller;

import android.content.Context;

import com.google.gson.Gson;

import org.ei.drishti.dto.MonthSummaryDatum;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.domain.Report;
import org.smartregister.domain.ReportIndicator;
import org.smartregister.domain.ReportsCategory;
import org.smartregister.repository.AllReports;
import org.smartregister.util.DateUtil;
import org.smartregister.view.contract.CategoryReports;
import org.smartregister.view.contract.IndicatorReport;

import java.util.Arrays;
import java.util.List;

public class ReportIndicatorListViewControllerTest {
    @Mock
    private Context context;

    @Mock
    private AllReports allReports;
    private ReportIndicatorListViewController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        DateUtil.fakeIt(LocalDate.parse("2012-10-10"));
        controller = new ReportIndicatorListViewController(context, allReports, ReportsCategory.FPS.value());
    }

    @Test
    public void shouldGetIndicatorReportsForGivenCategory() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = Arrays.asList(new MonthSummaryDatum("10", "2012", "2", "2", Arrays.asList("123", "456")));
        Report iudReport = new Report("IUD", "40", new Gson().toJson(monthlySummaries));
        Report condomReport = new Report("CONDOM", "30", new Gson().toJson(monthlySummaries));
        Mockito.when(allReports.allFor(ArgumentMatchers.anyListOf(ReportIndicator.class))).thenReturn(Arrays.asList(iudReport, condomReport));

        String indicatorReports = controller.get();

        IndicatorReport iud = new IndicatorReport("IUD", "IUD Adoption", "40", "2", "10", "2012", "2");
        IndicatorReport condom = new IndicatorReport("CONDOM", "Condom Usage", "30", "2", "10", "2012", "2");
        String expectedIndicatorReports = new Gson().toJson(new CategoryReports("Family Planning Services", Arrays.asList(iud, condom)));
        Assert.assertEquals(expectedIndicatorReports, indicatorReports);
    }

    @Test
    public void shouldIgnoreThoseReportsWhichHaveEmptyMonthlySummary() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = Arrays.asList(new MonthSummaryDatum("10", "2012", "2", "2", Arrays.asList("123", "456")));
        Report iudReport = new Report("IUD", "40", new Gson().toJson(monthlySummaries));
        Report condomReport = new Report("CONDOM", "30", "[]");
        Mockito.when(allReports.allFor(ArgumentMatchers.anyListOf(ReportIndicator.class))).thenReturn(Arrays.asList(iudReport, condomReport));

        String indicatorReports = controller.get();

        IndicatorReport iud = new IndicatorReport("IUD", "IUD Adoption", "40", "2", "10", "2012", "2");
        String expectedIndicatorReports = new Gson().toJson(new CategoryReports("Family Planning Services", Arrays.asList(iud)));
        Assert.assertEquals(expectedIndicatorReports, indicatorReports);
    }

    @Test
    public void shouldUseCurrentMonthDataForIndicatorReport() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = Arrays.asList(new MonthSummaryDatum("1", "2012", "2", "2", Arrays.asList("123", "456")),
                new MonthSummaryDatum("10", "2012", "2", "4", Arrays.asList("321", "654")));
        Report earlyANCRegistrationReport = new Report("ANC_LT_12", "40", new Gson().toJson(monthlySummaries));
        Mockito.when(allReports.allFor(ReportsCategory.ANC_SERVICES.indicators())).thenReturn(Arrays.asList(earlyANCRegistrationReport));

        controller = new ReportIndicatorListViewController(context, allReports, ReportsCategory.ANC_SERVICES.value());
        String reports = controller.get();

        IndicatorReport earlyANCRegistration = new IndicatorReport("EARLY_ANC_REGISTRATIONS", "Early ANC Registration", "40", "2", "10", "2012", "4");
        String expectedIndicatorReports = new Gson().toJson(new CategoryReports("ANC Services", Arrays.asList(earlyANCRegistration)));
        Assert.assertEquals(expectedIndicatorReports, reports);
    }

    @Test
    public void shouldUseLatestMonthDataIfCurrentMonthDataNotAvailable() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = Arrays.asList(new MonthSummaryDatum("6", "2012", "2", "2", Arrays.asList("123", "456")),
                new MonthSummaryDatum("8", "2012", "2", "4", Arrays.asList("321", "654")));
        Report iudReport = new Report("IUD", "40", new Gson().toJson(monthlySummaries));
        Mockito.when(allReports.allFor(ReportsCategory.FPS.indicators())).thenReturn(Arrays.asList(iudReport));

        String indicatorReports = controller.get();

        IndicatorReport iud = new IndicatorReport("IUD", "IUD Adoption", "40", "0", "10", "2012", "4");
        String expectedIndicatorReports = new Gson().toJson(new CategoryReports("Family Planning Services", Arrays.asList(iud)));
        Assert.assertEquals(expectedIndicatorReports, indicatorReports);
    }

    @Test
    public void shouldShowNAIfAnnualTargetNotAvailable() throws Exception {
        List<MonthSummaryDatum> monthlySummaries = Arrays.asList(new MonthSummaryDatum("6", "2012", "2", "2", Arrays.asList("123", "456")),
                new MonthSummaryDatum("8", "2012", "2", "4", Arrays.asList("321", "654")));
        Report iudReport = new Report("IUD", null, new Gson().toJson(monthlySummaries));
        Mockito.when(allReports.allFor(ReportsCategory.FPS.indicators())).thenReturn(Arrays.asList(iudReport));

        String indicatorReports = controller.get();

        IndicatorReport iud = new IndicatorReport("IUD", "IUD Adoption", "NA", "0", "10", "2012", "4");
        String expectedIndicatorReports = new Gson().toJson(new CategoryReports("Family Planning Services", Arrays.asList(iud)));
        Assert.assertEquals(expectedIndicatorReports, indicatorReports);
    }
}
