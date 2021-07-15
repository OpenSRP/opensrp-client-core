package org.smartregister.repository;

import org.smartregister.repository.contract.MigrationSource;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 29-01-2021.
 */
public class MigrationImpl implements MigrationSource.Migration {

    private int dbVersion;
    private String[] migrations;
    private MigrationType migrationType;

    @Override
    public int getDbVersion() {
        return dbVersion;
    }

    @Override
    public String[] getUpMigrationQueries() {
        return migrations;
    }

    @Override
    public void setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
    }

    @Override
    public void setMigrations(String[] migrations) {
        this.migrations = migrations;
    }

    @Override
    public MigrationType getMigrationType() {
        return migrationType;
    }

    @Override
    public void setMigrationType(MigrationType migrationType) {
        this.migrationType = migrationType;
    }
}
