package org.smartregister.commonregistry;

import org.smartregister.domain.ColumnDetails;

/**
 * Created by Raihan Ahmed on 3/16/15.
 */
public class CommonRepositoryInformationHolder {
    private String bindtypename;
    private ColumnDetails[] columnNames;

    public CommonRepositoryInformationHolder(String bindtypename, ColumnDetails[] columnNames) {
        this.bindtypename = bindtypename;
        this.columnNames = columnNames;
    }

    public String getBindtypename() {
        return bindtypename;
    }

    public void setBindtypename(String bindtypename) {
        this.bindtypename = bindtypename;
    }

    public ColumnDetails[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(ColumnDetails[] columnNames) {
        this.columnNames = columnNames;
    }
}
