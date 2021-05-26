package org.smartregister.repository;

import org.ei.drishti.dto.Action;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Report;
import org.smartregister.domain.ReportIndicator;
import org.smartregister.util.ActionBuilder;

import java.util.Arrays;
import java.util.List;

public class AllReportsTest extends BaseUnitTest {

    @Mock
    private ReportRepository repository;
    private AllReports allReports;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        allReports = new AllReports(repository);
    }

    @Test
    public void shouldDelegateActionToReportRepository() throws Exception {
        Action iudAction = ActionBuilder.actionForReport("IUD", "40");

        allReports.handleAction(iudAction);

        Mockito.verify(repository).update(new Report("IUD", "40", "some-month-summary-json"));
    }

    @Test
    public void shouldGetReportsForGivenIndicators() throws Exception {
        List<Report> expectedReports = Arrays.asList(new Report("IUD", "40", "some-month-summary-json"), new Report("ANC_LT_12", "30", "some-month-summary-json"));
        Mockito.when(repository.allFor("IUD", "CONDOM", "ANC_LT_12")).thenReturn(expectedReports);

        List<Report> reports = allReports.allFor(Arrays.asList(ReportIndicator.IUD, ReportIndicator.CONDOM, ReportIndicator.EARLY_ANC_REGISTRATIONS));

        Assert.assertEquals(expectedReports, reports);
    }
}
