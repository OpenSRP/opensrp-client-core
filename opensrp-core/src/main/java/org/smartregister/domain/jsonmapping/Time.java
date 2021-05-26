package org.smartregister.domain.jsonmapping;

import org.smartregister.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Jason Rogena (jrogena@ona.io) on 19/05/2017.
 */
public class Time {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Utils.getDefaultLocale());
    private String time;
    private String timeZone;

    public Time(Date time, TimeZone timeZone) {
        setTime(time);
        setTimeZone(timeZone);
    }

    public String getTime() {
        return time;
    }

    public void setTime(Date time) {
        if (time != null) this.time = DATE_FORMAT.format(time);
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        if (timeZone != null) this.timeZone = timeZone.getID();
    }
}