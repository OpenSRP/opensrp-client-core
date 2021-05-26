package org.smartregister.domain;

/**
 * Created by samuelgithengi on 11/22/18.
 */
public class Location extends PhysicalLocation {

    private String syncStatus;

    private Long rowid;

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Long getRowid() {
        return rowid;
    }

    public void setRowid(Long rowid) {
        this.rowid = rowid;
    }
}
