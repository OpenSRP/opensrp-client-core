package org.smartregister.repository;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.exception.DatabaseMigrationException;
import org.smartregister.repository.helper.OpenSRPDatabaseErrorHandler;
import org.smartregister.util.DatabaseMigrationUtils;
import org.smartregister.util.Session;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import timber.log.Timber;

public class Repository extends SQLiteOpenHelper {
    protected CommonFtsObject commonFtsObject;
    private DrishtiRepository[] repositories;
    private File databasePath = new File(DrishtiApplication.getAppDir() + "/databases/" + AllConstants.DATABASE_NAME);
    private Context context;
    private String dbName;
    private Session session;

    private static SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
        @Override
        public void preKey(SQLiteDatabase database) {
            // Do nothing before keying
        }

        @Override
        public void postKey(SQLiteDatabase database) {
            if (!DatabaseMigrationUtils.performCipherMigrationToV4(database)) {
                throw new DatabaseMigrationException("Database migration to SQLiteCipher v4 was not successful");
            } else {
                CoreLibrary.getInstance().context().allSharedPreferences().setMigratedToSqlite4();
                Timber.i("Database migration to Cipher 4 complete");
            }

            // Disable cipher memory security which makes database operations slow
            database.execSQL("PRAGMA cipher_memory_security = OFF;");
            //set journal mode to TRUNCATE
            database.rawExecSQL("PRAGMA journal_mode = TRUNCATE;");
        }
    };

    public Repository(Context context, Session session, DrishtiRepository... repositories) {
        super(context, (session != null ? session.repositoryName() : AllConstants.DATABASE_NAME),
                null, 1, hook);
        this.repositories = repositories;
        this.context = context;
        this.session = session;
        this.dbName = session != null ? session.repositoryName() : AllConstants.DATABASE_NAME;
        this.databasePath = context != null ? context.getDatabasePath(dbName)
                : new File("/data/data/org.smartregister" + ".indonesia/databases/" + AllConstants.DATABASE_NAME);

        SQLiteDatabase.loadLibs(context);
        for (DrishtiRepository repository : repositories) {
            repository.updateMasterRepository(this);
        }
    }

    public Repository(Context context, Session session, CommonFtsObject commonFtsObject,
                      DrishtiRepository... repositories) {
        this(context, session, repositories);
        this.commonFtsObject = commonFtsObject;
    }

    public Repository(Context context, String dbName, int version, Session session,
                      CommonFtsObject commonFtsObject, DrishtiRepository... repositories) {
        super(context, dbName, null, version, hook);
        this.dbName = dbName;
        this.repositories = repositories;
        this.context = context;
        this.session = session;
        this.databasePath = context != null ? context.getDatabasePath(dbName)
                : new File("/data/data/org.smartregister" + ".indonesia/databases/" + AllConstants.DATABASE_NAME);

        SQLiteDatabase.loadLibs(context);
        for (DrishtiRepository repository : repositories) {
            repository.updateMasterRepository(this);
        }
        this.commonFtsObject = commonFtsObject;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        for (DrishtiRepository repository : repositories) {
            repository.onCreate(database);
        }

        if (this.commonFtsObject != null) {
            for (String ftsTable : commonFtsObject.getTables()) {
                Set<String> searchColumns = new LinkedHashSet<String>();
                searchColumns.add(CommonFtsObject.idColumn);
                searchColumns.add(CommonFtsObject.relationalIdColumn);
                searchColumns.add(CommonFtsObject.phraseColumn);
                searchColumns.add(CommonFtsObject.isClosedColumn);

                String[] mainConditions = this.commonFtsObject.getMainConditions(ftsTable);
                if (mainConditions != null) {
                    for (String mainCondition : mainConditions) {
                        if (!mainCondition.equals(CommonFtsObject.isClosedColumnName)) {
                            searchColumns.add(mainCondition);
                        }
                    }
                }

                String[] sortFields = this.commonFtsObject.getSortFields(ftsTable);
                if (sortFields != null) {
                    for (String sortValue : sortFields) {
                        if (sortValue.startsWith("alerts.")) {
                            sortValue = sortValue.split("\\.")[1];
                        }
                        searchColumns.add(sortValue);
                    }
                }

                String joinedSearchColumns = StringUtils.join(searchColumns, ",");

                String searchSql =
                        "create virtual table " + CommonFtsObject.searchTableName(ftsTable)
                                + " using fts4 (" + joinedSearchColumns + ");";
                database.execSQL(searchSql);

            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    public SQLiteDatabase getReadableDatabase() {
        if (password() == null) {
            throw new RuntimeException("Password has not been set!");
        }
        return getReadableDatabase(password());
    }

    public SQLiteDatabase getWritableDatabase() {
        if (password() == null) {
            throw new RuntimeException("Password has not been set!");
        }
        return getWritableDatabase(password());
    }

    @VisibleForTesting
    protected boolean isDatabaseWritable(byte[] password) {
        SQLiteDatabase database = SQLiteDatabase
                .openDatabase(databasePath.getPath(), password, null,
                        SQLiteDatabase.OPEN_READONLY, hook, new OpenSRPDatabaseErrorHandler());
        database.close();
        return true;
    }

    public boolean canUseThisPassword(byte[] password) {
        try {
            return isDatabaseWritable(password);
        } catch (SQLiteException e) {
            Timber.e(e);
            if (e.getMessage().contains("attempt to write a readonly database")) {
                File journal = new File(databasePath.getPath() + "-journal");
                Timber.w("Journal exists: %s", journal.exists());
                if (journal.exists() && journal.canWrite()) {
                    Timber.w("Journal space: %s, its not possible to recover transactions!!! deleting Journal ", journal.getTotalSpace());
                    try {
                        new FileOutputStream(journal).write(new byte[]{});
                        return isDatabaseWritable(password);
                    } catch (FileNotFoundException e1) {
                        Timber.e(e1);
                    } catch (IOException e1) {
                        Timber.e(e1);
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] password() {
        return DrishtiApplication.getInstance().getPassword();
    }

    public boolean deleteRepository() {
        close();
        boolean deleteTry1 = context.deleteDatabase(dbName);
        boolean deleteTry2 = context.getDatabasePath(dbName).delete();

        return deleteTry1 || deleteTry2;
    }

}
