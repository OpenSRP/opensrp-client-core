package org.smartregister.util;


import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Weeks;
import org.smartregister.CoreLibrary;
import org.smartregister.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

interface DateUtility {
    LocalDate today();
}

public class DateUtil {
    public static String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    public static String DATE_FORMAT_FOR_TIMELINE_EVENT = "dd-MM-yyyy";

    private static DateUtility dateUtility = new RealDate();

    public static void fakeIt(LocalDate fakeDayAsToday) {
        dateUtility = new MockDate(fakeDayAsToday);
    }

    public static void setDefaultDateFormat(String defaultDateFormat) {
        DEFAULT_DATE_FORMAT = defaultDateFormat;
    }

    public static LocalDate today() {
        return dateUtility.today();
    }

    public static String formatDateForTimelineEvent(String unformattedDate) {
        return formatDate(unformattedDate, DATE_FORMAT_FOR_TIMELINE_EVENT);
    }

    public static String formatDate(String unformattedDate) {
        return formatDate(unformattedDate, DEFAULT_DATE_FORMAT);
    }

    public static String formatDate(String date, String pattern) {
        try {
            return LocalDate.parse(date).toString(pattern);
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatDate(LocalDate date, String pattern) {
        try {
            return date.toString(pattern);
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatFromISOString(String date, String pattern) {
        try {
            return getLocalDateFromISOString(date).toString(pattern);
        } catch (Exception e) {
            return "";
        }
    }

    public static LocalDate getLocalDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Utils.getDefaultLocale());
            Date formattedDate = format.parse(date);
            return new LocalDate(formattedDate);
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDate getLocalDateFromISOString(String date) {
        try {
            return LocalDateTime.parse(date).toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }

    public static int dayDifference(LocalDate startDate, LocalDate endDate) {
        try {
            return Math.abs(Days.daysBetween(startDate, endDate).getDays());
        } catch (Exception e) {
            return 0;
        }
    }

    public static int weekDifference(LocalDate startDate, LocalDate endDate) {
        try {
            return Math.abs(Weeks.weeksBetween(startDate, endDate).getWeeks());
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isValidDate(String dateString) {
        if (dateString == null || dateString.length() != "yyyy-MM-dd".length()) {
            return false;
        }

        return dateString.matches("\\d{4}-\\d{2}-\\d{2}");

    }

    public static String getDuration(DateTime dateTime) {
        return getDuration(CoreLibrary.getInstance().context().applicationContext(), dateTime);
    }

    public static String getDuration(Context context, DateTime dateTime) {
        if (dateTime != null) {
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(dateTime.toDate());
            dateCalendar.set(Calendar.HOUR_OF_DAY, 0);
            dateCalendar.set(Calendar.MINUTE, 0);
            dateCalendar.set(Calendar.SECOND, 0);
            dateCalendar.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long timeDiff = Math.abs(dateCalendar.getTimeInMillis() - today.getTimeInMillis());
            return getDuration(timeDiff, getLocale());
        }
        return null;
    }

    public static String getDuration(long timeDiff) {

        return getDuration(timeDiff, getLocale());

    }

    private static Locale getLocale() {

        Locale locale = CoreLibrary.getInstance().context().applicationContext().getResources().getConfiguration().locale;
        locale = locale != null && locale.toString().startsWith("ar") ? Locale.ENGLISH : locale;

        return locale;

    }

    public static String getDuration(long timeDiff, Locale locale) {

        Context context = CoreLibrary.getInstance().context().applicationContext();
        String duration = "";
        if (timeDiff >= 0
                && timeDiff <= TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS)) {
            // Represent in days
            long days = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
            duration = String.format(locale, context.getResources().getString(R.string.x_days), days);
        } else if (timeDiff > TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS)
                && timeDiff <= TimeUnit.MILLISECONDS.convert(97, TimeUnit.DAYS)) {
            // Represent in weeks and days
            int weeks = (int) Math.floor((float) timeDiff /
                    TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));
            int days = (int) Math.floor((float) (timeDiff -
                    TimeUnit.MILLISECONDS.convert(weeks * 7, TimeUnit.DAYS)) /
                    TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

            if (days >= 7) {
                days = 0;
                weeks++;
            }

            if (days > 0) {
                duration = String.format(locale, context.getResources().getString(R.string.x_weeks_days), weeks, days);
            } else {
                duration = String.format(locale, context.getResources().getString(R.string.x_weeks), weeks);
            }

        } else if (timeDiff > TimeUnit.MILLISECONDS.convert(97, TimeUnit.DAYS)
                && timeDiff <= TimeUnit.MILLISECONDS.convert(363, TimeUnit.DAYS)) {
            // Represent in months and weeks
            int months = (int) Math.floor((float) timeDiff /
                    TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));
            int weeks = (int) Math.floor((float) (timeDiff - TimeUnit.MILLISECONDS.convert(
                    months * 30, TimeUnit.DAYS)) /
                    TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));

            if (weeks >= 4) {
                weeks = 0;
                months++;
            }

            if (months < 12) {
                if (weeks > 0) {
                    duration = String.format(locale, context.getResources().getString(R.string.x_months_weeks), months, weeks);
                } else {
                    duration = String.format(locale, context.getResources().getString(R.string.x_months), months);
                }
            } else {
                duration = String.format(locale, context.getResources().getString(R.string.x_years), 1);
            }
        } else {
            // Represent in years and months
            int years = (int) Math.floor((float) timeDiff
                    / TimeUnit.MILLISECONDS.convert(365, TimeUnit.DAYS));
            int months = (int) Math.floor((float) (timeDiff -
                    TimeUnit.MILLISECONDS.convert(years * 365, TimeUnit.DAYS)) /
                    TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));

            if (months >= 12) {
                months = 0;
                years++;
            }

            if (months > 0) {
                duration = String.format(locale, context.getResources().getString(R.string.x_years_months), years, months);
            } else {
                duration = String.format(locale, context.getResources().getString(R.string.x_years), years);
            }
        }

        return duration;

    }

    public static boolean checkIfDateThreeMonthsOlder(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(cal.getTime());
        cal.add(Calendar.DATE, -90);
        Date dateBefore90Days = cal.getTime();
        return date.before(dateBefore90Days);
    }

    public static Long getMillis(DateTime dateTime) {
        return dateTime == null ? null : dateTime.getMillis();
    }

    public static Long getMillis(LocalDate localDate) {
        return localDate == null ? null : localDate.toDate().getTime();
    }

    public static DateTime getDateTimeFromMillis(Long milliSeconds) {
        return milliSeconds == null || milliSeconds == 0 ? null : new DateTime(milliSeconds);
    }

    public static LocalDate getDateFromMillis(Long milliSeconds) {
        return milliSeconds == null || milliSeconds == 0 ? null : new LocalDate(milliSeconds);
    }
}

class RealDate implements DateUtility {
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}

class MockDate implements DateUtility {
    private LocalDate fakeDay;

    MockDate(LocalDate fakeDay) {
        this.fakeDay = fakeDay;
    }

    @Override
    public LocalDate today() {
        return fakeDay;
    }
}
