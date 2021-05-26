package org.smartregister.domain;

import java.io.Serializable;

/**
 * Created by Richard Kareko on 6/4/20.
 */

public class SyncProgress implements Serializable {
    private SyncEntity syncEntity;
    private int percentageSynced;
    private long totalRecords;

    public SyncEntity getSyncEntity() {
        return syncEntity;
    }

    public void setSyncEntity(SyncEntity syncEntity) {
        this.syncEntity = syncEntity;
    }

    public int getPercentageSynced() {
        return percentageSynced;
    }

    public void setPercentageSynced(int percentageSynced) {
        this.percentageSynced = percentageSynced;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }
}
