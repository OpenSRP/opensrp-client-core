package org.smartregister.repository.contract;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 29-01-2021.
 */
public interface MigrationSource {

    HashMap<Integer, ArrayList<Migration>> getMigrations();

    HashMap<Integer, ArrayList<Migration>> getMigrations(int fromDbVersion);

    interface Migration {

        enum MigrationType {
            UP,
            DOWN
        }

        int getDbVersion();

        String[] getUpMigrationQueries();

        void setDbVersion(int dbVersion);

        void setMigrations(String[] migrations);

        MigrationType getMigrationType();

        void setMigrationType(MigrationType migrationType);
    }
}
