package org.smartregister.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.smartregister.domain.ReportIndicator.CONDOM;
import static org.smartregister.domain.ReportIndicator.parseToReportIndicator;

public class ReportIndicatorTest {

    @Test
    public void shouldParseStringToReportIndicator() throws Exception {
        assertEquals(CONDOM, parseToReportIndicator("condom"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenValueIsNotAValidReportIndicator() throws Exception {
        parseToReportIndicator("invalid value");
    }
}
