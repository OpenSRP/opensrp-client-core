package org.smartregister.repository.dao;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import org.apache.commons.io.FileUtils;
import org.smartregister.AllConstants;
import org.smartregister.repository.MigrationImpl;
import org.smartregister.repository.contract.MigrationSource;
import org.smartregister.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 29-01-2021.
 */
public class AppFolderMigrationSource implements MigrationSource {

    public Context context;

    public AppFolderMigrationSource(Context context) {
        this.context = context;
    }

    @Override
    public HashMap<Integer, ArrayList<Migration>> getMigrations() {
        return getMigrations(0);
    }

    @Override
    public HashMap<Integer, ArrayList<Migration>> getMigrations(int fromDbVersion) {
        HashMap<Integer, ArrayList<Migration>> migrationMap = new HashMap<>();
        try {
            File appFolderDirectory = new File(Environment.getDataDirectory(), "/data/" + Utils.getAppId(context) + "/files/migrations");
            String[] migrationFileNames = appFolderDirectory.list();

            String regex = AllConstants.MIGRATION_FILENAME_PATTERN;
            Pattern filePattern  = Pattern.compile(regex);

            if (migrationFileNames != null) {
                for (String migrationFile: migrationFileNames) {
                    // Check if migration file name matches the Regex
                    Matcher fileNameMatcher = filePattern.matcher(migrationFile);
                    if (fileNameMatcher.matches()) {
                        String versionString = fileNameMatcher.group(1);
                        String migrationType = fileNameMatcher.group(2);

                        int version = Integer.parseInt(versionString);

                        if (version >= fromDbVersion) {

                            File file = new File(appFolderDirectory, migrationFile);
                            String queries = FileUtils.readFileToString(file);

                            Migration versionMigration = new MigrationImpl();
                            versionMigration.setDbVersion(version);
                            versionMigration.setMigrations(queries.split("\\n"));

                            if (!TextUtils.isEmpty(migrationType)) {
                                versionMigration.setMigrationType(Migration.MigrationType.UP.name().equals(migrationType)
                                        ? Migration.MigrationType.UP : Migration.MigrationType.DOWN);
                            }

                            ArrayList<Migration> versionMigrations = migrationMap.get(version);
                            if (versionMigrations == null) {
                                versionMigrations = new ArrayList<>();
                            }

                            versionMigrations.add(versionMigration);
                            migrationMap.put(version, versionMigrations);
                        }
                    }
                }
            }

        } catch (IOException e) {
            Timber.e(e);
        }

        return migrationMap;
    }
}
