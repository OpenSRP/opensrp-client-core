package org.smartregister.util;

import org.smartregister.AllConstants;
import org.smartregister.security.SecurityHelper;

import java.util.Date;

import static org.joda.time.DateTimeConstants.MILLIS_PER_SECOND;
import static org.joda.time.DateTimeConstants.MINUTES_PER_HOUR;
import static org.joda.time.DateTimeConstants.SECONDS_PER_MINUTE;

public class Session {
    private byte[] password;
    private String repositoryName = AllConstants.DATABASE_NAME;
    private long sessionExpiryTime = 0;

    public long lengthInMilliseconds() {
        return 24 * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND;
    }

    public byte[] password() {
        return password;
    }

    public String repositoryName() {
        return repositoryName;
    }

    public Session setPassword(byte[] password) {
        this.password = password;
        return this;
    }

    public Session setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    public Session start(long numberOfMillisecondsAfterNowThatThisSessionEnds) {
        setSessionExpiryTimeTo(
                new Date().getTime() + numberOfMillisecondsAfterNowThatThisSessionEnds);
        return this;
    }

    public boolean hasExpired() {
        if (password == null) {
            return true;
        }

        long now = new Date().getTime();
        return now > sessionExpiryTime;
    }

    public void expire() {
        SecurityHelper.clearArray(this.password);
        this.password = null;
        setSessionExpiryTimeTo(new Date().getTime() - 1);
    }

    private void setSessionExpiryTimeTo(long expiryTimeInMillisecondsSinceEpoch) {
        this.sessionExpiryTime = expiryTimeInMillisecondsSinceEpoch;
    }
}
