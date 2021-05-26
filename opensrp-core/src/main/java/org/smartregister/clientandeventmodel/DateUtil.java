package org.smartregister.clientandeventmodel;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

interface DateUtility {
    LocalDate today();

    long millis();
}

public class DateUtil {
    public static DateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static DateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    public static DateFormat yyyyMMddTHHmmssSSSZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss" + ".SSS'Z'", Locale.ENGLISH);
    private static DateUtility dateUtility = new RealDate();
    //2017-03-01T14:04:20.865Z

    public static void fakeIt(LocalDate fakeDayAsToday) {
        dateUtility = new MockDate(fakeDayAsToday);
    }

    public static LocalDate today() {
        return dateUtility.today();
    }

    public static long millis() {
        return dateUtility.millis();
    }

    public static boolean isDateWithinGivenPeriodBeforeToday(LocalDate referenceDateForSchedule,
                                                             Period period) {
        //TODO:
        //return inRange(toTime(referenceDateForSchedule), toTime(today().minus(period)), toTime
        // (today()));
        return true;
    }

    private static DateTime toTime(LocalDate referenceDateForSchedule) {
        return referenceDateForSchedule.toDateTime(new LocalTime(0, 0));
    }

    /**
     * Parses dates of following formats
     * - yyyy-MM-dd
     * - yyyy-MM-dd HH:mm:ss
     * - yyyy-MM-dd'T'HH:mm:ss.SSSZ
     *
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date) throws ParseException {
        try {
            return yyyyMMddTHHmmssSSSZ.parse(date);
        } catch (ParseException e) {
        }
        try {
            return yyyyMMddHHmmss.parse(date);
        } catch (ParseException e) {
        }

        return yyyyMMdd.parse(date);
    }

    public static LocalDate tryParse(String value, LocalDate defaultValue) {
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Date getDateFromString(String dateString) {
        Date parsed = null;
        try {
            if (dateString != null && !dateString.equals("null") && dateString.length() > 0) {
                parsed = yyyyMMddTHHmmssSSSZ.parse(dateString.trim());
            }
        } catch (ParseException e) {
            Timber.w(e);
        }
        return parsed;
    }

    public static Date toDate(Object dateObject) {
        if (dateObject instanceof Date) {
            return (Date) dateObject;
        } else if (dateObject instanceof Long) {
            return new Date((Long) dateObject);
        } else if (dateObject instanceof String) {
            try {
                String dateString = (String) dateObject;
                if (dateString.isEmpty()) {
                    return null;
                }

                return yyyyMMddTHHmmssSSSZ.parse(dateString);
            } catch (ParseException e) {
                return null;
            }
        }

        return null;
    }

    public static String fromDate(Date date) {
        return yyyyMMddTHHmmssSSSZ.format(date);
    }

}

class RealDate implements DateUtility {
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }

    @Override
    public long millis() {
        return DateTime.now().getMillis();
    }
}

class MockDate implements DateUtility {
    private DateTime fakeDay;

    MockDate(LocalDate fakeDay) {
        this.fakeDay = fakeDay.toDateTimeAtStartOfDay();
    }

    @Override
    public LocalDate today() {
        return fakeDay.toLocalDate();
    }

    @Override
    public long millis() {
        return fakeDay.getMillis();
    }
}


