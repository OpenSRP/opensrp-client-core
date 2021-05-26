package org.smartregister.domain;

import org.smartregister.R;

/**
 * Created by Jason Rogena - jrogena@ona.io on 18/05/2017.
 */

public enum TimeStatus {
    OK(R.string.device_time_ok), TIMEZONE_MISMATCH(R.string.timezone_mismatch), TIME_MISMATCH(
            R.string.time_mismatch), ERROR(R.string.time_error);

    private final int message;

    TimeStatus(int message) {
        this.message = message;
    }

    public int getMessage() {
        return this.message;
    }
}
