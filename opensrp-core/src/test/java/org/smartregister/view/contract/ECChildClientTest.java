package org.smartregister.view.contract;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.util.DateUtil;

import static org.junit.Assert.assertEquals;

public class ECChildClientTest extends BaseUnitTest {

    private LocalDate today;

    @Before
    public void setUp() {
        today = LocalDate.parse("2014-04-27");
        DateUtil.fakeIt(today);
    }

    @Test
    public void shouldReturnZeroDaysForNullCurrentDate() {
        ECChildClient child = new ECChildClient("abcd1", "male", null);
        assertEquals("0d", child.getAgeInString());
    }

    @Test
    public void shouldReturnZeroDaysForEmptyCurrentDate() {
        ECChildClient child = new ECChildClient("abcd1", "male", "");
        assertEquals("0d", child.getAgeInString());
    }

    @Test
    public void shouldReturnZeroDaysForCurrentDate() {
        ECChildClient child = new ECChildClient("abcd1", "male", today.toString());
        assertEquals("0d", child.getAgeInString());
    }

    @Test
    public void shouldReturn5DaysForCurrentDateMinus5Days() {
        ECChildClient child = new ECChildClient("abcd1", "male", today.minusDays(5).toString());
        assertEquals("5d", child.getAgeInString());
    }

    @Test
    public void shouldReturn27DaysForCurrentDateMinus27Days() {
        ECChildClient child = new ECChildClient("abcd1", "male", today.minusDays(27).toString());
        assertEquals("27d", child.getAgeInString());
    }

    @Test
    public void shouldReturn4WeeksForCurrentDateMinus28Days() {
        ECChildClient child = new ECChildClient("abcd1", "male", today.minusDays(28).toString());
        assertEquals("4w", child.getAgeInString());
    }

    @Test
    public void shouldReturn16WeeksForCurrentDateMinus118Days() {
        ECChildClient child = new ECChildClient("abcd1", "male", today.minusDays(118).toString());
        assertEquals("16w", child.getAgeInString());
    }

    @Test
    public void shouldReturn3MonthsForCurrentDateMinus119Days() {
        ECChildClient child = new ECChildClient("abcd1", "male", today.minusDays(119).toString());
        assertEquals("3m", child.getAgeInString());
    }

    @Test
    public void shouldReturn23MonthsForCurrentDateMinus719Days() {
        ECChildClient child = new ECChildClient("abcd1", "male", today.minusDays(719).toString());
        assertEquals("23m", child.getAgeInString());
    }

    @Test
    public void shouldReturn1YearsForCurrentDateMinus720Days() {
        ECChildClient child = new ECChildClient("abcd1", "male", today.minusDays(720).toString());
        assertEquals("1y", child.getAgeInString());
    }

    @Test
    public void shouldReturn1YearsForCurrentDateMinus1000Days() {
        ECChildClient child = new ECChildClient("abcd1", "male", today.minusDays(1000).toString());
        assertEquals("2y", child.getAgeInString());
    }
}
