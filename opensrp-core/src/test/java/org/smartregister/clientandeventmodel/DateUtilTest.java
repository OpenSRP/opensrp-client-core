package org.smartregister.clientandeventmodel;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;

import java.util.Date;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class DateUtilTest extends BaseUnitTest {

    private final long default_time = 1447487308000l;
    private LocalDate start = new LocalDate(default_time);
    private String mockDate = "2017-10-10";

    @Before
    public void setUp() {
        DateUtil.fakeIt(start);
    }

    @Test
    public void assertDateUtilNotNUll() {
        Assert.assertNotNull(new DateUtil());
    }

    @Test
    public void assertFakeIt() throws Exception {
        //methods calls depends on fakeit
        Assert.assertNotNull(DateUtil.today());
        Assert.assertNotNull(DateUtil.millis());
        Assert.assertEquals(DateUtil.isDateWithinGivenPeriodBeforeToday(null, null), true);
        Assert.assertNotNull(DateUtil.parseDate(mockDate));
        Assert.assertNotNull(DateUtil.parseDate("2017-10-10 10:10:10"));
        Assert.assertNotNull(DateUtil.parseDate("1985-07-24T00:00:00.000Z"));
        Assert.assertNotNull(DateUtil.tryParse(mockDate, start));
        Assert.assertNotNull(DateUtil.toDate(new Date(0l)));
        Assert.assertNotNull(DateUtil.fromDate(new Date(0l)));
        Assert.assertNotNull(DateUtil.getDateFromString("1985-07-24T00:00:00.000Z"));
    }

}
