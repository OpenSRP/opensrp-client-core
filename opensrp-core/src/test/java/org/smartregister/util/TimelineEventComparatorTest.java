package org.smartregister.util;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.smartregister.domain.TimelineEvent;

public class TimelineEventComparatorTest {
    @Test
    public void shouldReturnMinusOneWhenFirstTimeLineEventOccursLater() throws Exception {
        int result = compareTimelineEvents(LocalDate.now(), LocalDate.now().minusDays(1));

        Assert.assertEquals(-1, result);
    }

    @Test
    public void shouldReturnOneWhenFirstTimeLineEventOccursEarlier() throws Exception {
        int result = compareTimelineEvents(LocalDate.now().minusDays(1), LocalDate.now());

        Assert.assertEquals(1, result);
    }

    @Test
    public void shouldReturnZeroWhenFirstTimeLineEventOccurAtSameTime() throws Exception {
        int result = compareTimelineEvents(LocalDate.now(), LocalDate.now());

        Assert.assertEquals(0, result);
    }

    private int compareTimelineEvents(LocalDate date1, LocalDate date2) {
        return new TimelineEventComparator().compare(
                new TimelineEvent("CASE A", "FPCHANGE", date1, "FP Method Change", null, null),
                new TimelineEvent("CASE A", "PREGNANT", date2, "GOT PREGNANT", null, null));
    }
}
