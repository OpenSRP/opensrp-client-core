package org.smartregister.util;

import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepositoryInformationHolder;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 6/25/18.
 */
public class DatabaseMigrationUtils {

    private static final String TAG = "DatabaseMigrationUtils";

    private static final String TABLE_PREFIX = "_v2";

    public static boolean isColumnExists(SQLiteDatabase db, String table, String column) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                if (column.equalsIgnoreCase(name)) {
                    cursor.close();
                    return true;
                }
            }
            cursor.close();
        }
        return false;
    }

    public static boolean addColumnIfNotExists(SQLiteDatabase db, String table, String column, String dataType) {
        if (!isColumnExists(db, table, column)) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s %s", table, column, dataType));
        }
        return false;
    }

    public static boolean addIndexIfNotExists(SQLiteDatabase db, String table, String columm) {
        db.execSQL(String.format("CREATE INDEX IF NOT EXISTS  %s_%s_idx  ON %s(%s)", table, columm, table, columm));
        return false;
    }

    public static void addFieldsToFTSTable(SQLiteDatabase database, CommonFtsObject commonFtsObject, String originalTableName, List<String> newlyAddedFields) {

        Set<String> searchColumns = new LinkedHashSet<>();
        searchColumns.add(CommonFtsObject.idColumn);
        searchColumns.add(CommonFtsObject.relationalIdColumn);
        searchColumns.add(CommonFtsObject.phraseColumn);
        searchColumns.add(CommonFtsObject.isClosedColumn);

        String[] mainConditions = commonFtsObject.getMainConditions(originalTableName);
        if (mainConditions != null)
            for (String mainCondition : mainConditions) {
                if (!mainCondition.equals(CommonFtsObject.isClosedColumnName))
                    searchColumns.add(mainCondition);
            }

        String[] sortFields = commonFtsObject.getSortFields(originalTableName);
        if (sortFields != null) {
            for (String sortValue : sortFields) {
                if (sortValue.startsWith("alerts.")) {
                    sortValue = sortValue.split("\\.")[1];
                }
                searchColumns.add(sortValue);
            }
        }

        String joinedSearchColumns = StringUtils.join(searchColumns, ",");

        String searchSql = "create virtual table "
                + CommonFtsObject.searchTableName(originalTableName) + TABLE_PREFIX
                + " using fts4 (" + joinedSearchColumns + ");";
        Log.d(TAG, "Create query is\n---------------------------\n" + searchSql);

        database.execSQL(searchSql);

        ArrayList<String> oldFields = new ArrayList<>();

        for (String curColumn : searchColumns) {
            curColumn = curColumn.trim();
            if (curColumn.contains(" ")) {
                String[] curColumnParts = curColumn.split(" ");
                curColumn = curColumnParts[0];
            }

            if (!newlyAddedFields.contains(curColumn)) {
                oldFields.add(curColumn);
            } else {
                Log.d(TAG, "Skipping field " + curColumn + " from the select query");
            }
        }

        String insertQuery = "insert into "
                + CommonFtsObject.searchTableName(originalTableName) + TABLE_PREFIX
                + " (" + StringUtils.join(oldFields, ", ") + ")"
                + " select " + StringUtils.join(oldFields, ", ") + " from "
                + CommonFtsObject.searchTableName(originalTableName);

        Log.d(TAG, "Insert query is\n---------------------------\n" + insertQuery);
        database.execSQL(insertQuery);

        // Run the drop query
        String dropQuery = "drop table " + CommonFtsObject.searchTableName(originalTableName);
        Log.d(TAG, "Drop query is\n---------------------------\n" + dropQuery);
        database.execSQL(dropQuery);

        // Run rename query
        String renameQuery = "alter table "
                + CommonFtsObject.searchTableName(originalTableName) + TABLE_PREFIX
                + " rename to " + CommonFtsObject.searchTableName(originalTableName);
        Log.d(TAG, "Rename query is\n---------------------------\n" + renameQuery);
        database.execSQL(renameQuery);

    }


    public static void recreateSyncTableWithExistingColumnsOnly(SQLiteDatabase database, EventClientRepository.Table table) {

        database.beginTransaction();
        try {
            //rename original table
            database.execSQL("ALTER TABLE " + table.name() + " RENAME TO " + TABLE_PREFIX + table.name());
            //recreate table
            EventClientRepository.createTable(database, table, table.columns());
            //
            String insertQuery = "INSERT INTO "
                    + table.name()
                    + " (" + StringUtils.join(table.columns(), ", ") + ")"
                    + " SELECT " + StringUtils.join(table.columns(), ", ") + " FROM "
                    + TABLE_PREFIX + table.name();

            Log.d(TAG, "Insert query is\n---------------------------\n" + insertQuery);

            database.execSQL(insertQuery);

            database.execSQL("DROP TABLE " + TABLE_PREFIX + table.name());

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

    }


    public static void recreateSyncTableWithExistingColumnsOnly(SQLiteDatabase database, String table, String[] columns, String createTableDDL) {
        database.beginTransaction();
        try {
            //rename original table
            database.execSQL("ALTER TABLE " + table + " RENAME TO " + TABLE_PREFIX + table);
            //recreate table
            database.execSQL(createTableDDL);
            //
            String insertQuery = "INSERT INTO "
                    + table
                    + " (" + StringUtils.join(columns, ", ") + ")"
                    + " SELECT " + StringUtils.join(columns, ", ") + " FROM "
                    + TABLE_PREFIX + table;

            Log.d(TAG, "Insert query is\n---------------------------\n" + insertQuery);

            database.execSQL(insertQuery);

            database.execSQL("DROP TABLE " + TABLE_PREFIX + table);

            database.setTransactionSuccessful();

        } finally {
            database.endTransaction();
        }

    }


    private static boolean tableExists(SQLiteDatabase database, String tableName) {
        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name= ?", new String[]{tableName});
        boolean exists = false;
        if (cursor != null) {
            exists = cursor.getCount() > 0;
            cursor.close();
        }
        return exists;
    }

    public static void createAddedECTables(SQLiteDatabase database, Set<String> bindings, CommonFtsObject commonFtsObject) {
        ArrayList<CommonRepositoryInformationHolder> bindTypes = org.smartregister.Context.bindtypes;
        for (CommonRepositoryInformationHolder bindType : bindTypes) {
            if (bindings.contains(bindType.getBindtypename()) && !tableExists(database, bindType.getBindtypename())) {
                CoreLibrary.getInstance().context().commonrepository(bindType.getBindtypename()).onCreate(database);
            }
        }


        if (commonFtsObject != null) {
            for (String ftsTable : commonFtsObject.getTables()) {
                String ftsSearchTableName = CommonFtsObject.searchTableName(ftsTable);
                if (bindings.contains(ftsTable) && !tableExists(database, ftsSearchTableName)) {
                    Set<String> searchColumns = new LinkedHashSet<String>();
                    searchColumns.add(CommonFtsObject.idColumn);
                    searchColumns.add(CommonFtsObject.relationalIdColumn);
                    searchColumns.add(CommonFtsObject.phraseColumn);
                    searchColumns.add(CommonFtsObject.isClosedColumn);

                    String[] mainConditions = commonFtsObject.getMainConditions(ftsTable);
                    if (mainConditions != null) {
                        for (String mainCondition : mainConditions) {
                            if (!mainCondition.equals(CommonFtsObject.isClosedColumnName)) {
                                searchColumns.add(mainCondition);
                            }
                        }
                    }

                    String[] sortFields = commonFtsObject.getSortFields(ftsTable);
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
                            "create virtual table " + ftsSearchTableName
                                    + " using fts4 (" + joinedSearchColumns + ");";
                    database.execSQL(searchSql);
                }
            }
        }
    }

    public static boolean performCipherMigrationToV4(@NonNull SQLiteDatabase database) {
        if (!CoreLibrary.getInstance().context().allSharedPreferences().isMigratedToSqlite4()) {
            Cursor cursor = null;
            try {
                cursor = database.rawQuery("PRAGMA cipher_migrate", new Object[]{});

                if (cursor != null && cursor.moveToFirst()) {
                    String value = cursor.getString(0);

                    return (!TextUtils.isEmpty(value) && "0".equals(value));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return false;
        } else {
            Timber.i("SQLiteCipher database is already v4");
            return true;
        }
    }
}
