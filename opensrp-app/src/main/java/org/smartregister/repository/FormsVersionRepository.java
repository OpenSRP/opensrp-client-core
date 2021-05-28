package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.FormDefinitionVersion;
import org.smartregister.domain.SyncStatus;
import org.smartregister.util.EasyMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.sqlcipher.DatabaseUtils.longForQuery;

/**
 * Created by Dimas Ciputra on 3/21/15.
 */
public class FormsVersionRepository extends DrishtiRepository {

    public static final String FORM_NAME_COLUMN = "formName";
    public static final String VERSION_COLUMN = "formDataDefinitionVersion";
    public static final String FORM_DIR_NAME_COLUMN = "formDirName";
    public static final String SYNC_STATUS_COLUMN = "syncStatus";
    private static final String FORM_VERSION_SQL =
            "CREATE TABLE all_forms_version(id INTEGER " + "PRIMARY KEY,"
                    + "formName VARCHAR, formDirName VARCHAR, formDataDefinitionVersion "
                    + "VARCHAR, syncStatus VARCHAR)";
    private static final String FORM_VERSION_TABLE_NAME = "all_forms_version";
    private static final String ID_COLUMN = "id";
    public static final String[] FORM_VERSION_TABLE_COLUMNS = new String[]{ID_COLUMN,
            FORM_NAME_COLUMN, FORM_DIR_NAME_COLUMN, VERSION_COLUMN, SYNC_STATUS_COLUMN};

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(FORM_VERSION_SQL);
    }

    public FormDefinitionVersion fetchVersionByFormName(String formName) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(FORM_VERSION_TABLE_NAME, FORM_VERSION_TABLE_COLUMNS,
                FORM_NAME_COLUMN + " " + "" + "= ?", new String[]{formName}, null, null, null);
        return readFormVersion(cursor).get(0);
    }

    public FormDefinitionVersion fetchVersionByFormDirName(String formDirName) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(FORM_VERSION_TABLE_NAME, FORM_VERSION_TABLE_COLUMNS,
                FORM_DIR_NAME_COLUMN + " = ?", new String[]{formDirName}, null, null, null);
        return readFormVersion(cursor).get(0);
    }

    public String getVersion(String formDirName) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(FORM_VERSION_TABLE_NAME, FORM_VERSION_TABLE_COLUMNS,
                FORM_DIR_NAME_COLUMN + " = ?", new String[]{formDirName}, null, null, null);
        return (readFormVersion(cursor).get(0)).getVersion();
    }

    public FormDefinitionVersion getFormByFormDirName(String formDirName) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(FORM_VERSION_TABLE_NAME, FORM_VERSION_TABLE_COLUMNS,
                FORM_DIR_NAME_COLUMN + " = ?", new String[]{formDirName}, null, null, null);
        return (readFormVersion(cursor).get(0));
    }

    public List<FormDefinitionVersion> getAllFormWithSyncStatus(SyncStatus status) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(FORM_VERSION_TABLE_NAME, FORM_VERSION_TABLE_COLUMNS,
                SYNC_STATUS_COLUMN + " = ?", new String[]{status.value()}, null, null, null);
        return readFormVersion(cursor);
    }

    public List<Map<String, String>> getAllFormWithSyncStatusAsMap(SyncStatus status) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(FORM_VERSION_TABLE_NAME, FORM_VERSION_TABLE_COLUMNS,
                SYNC_STATUS_COLUMN + " = ?", new String[]{status.value()}, null, null, null);
        return readFormVersionToMap(cursor);
    }

    public void addFormVersion(Map<String, String> dataJSON) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.insert(FORM_VERSION_TABLE_NAME, null, createValuesFormVersions(dataJSON));
    }

    public void addFormVersionFromObject(FormDefinitionVersion formDefinitionVersion) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.insert(FORM_VERSION_TABLE_NAME, null,
                createValuesFromObject(formDefinitionVersion));
    }

    public void deleteAll() {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.delete(FORM_VERSION_TABLE_NAME, null, null);
    }

    public void updateServerVersion(String formDirName, String serverVersion) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VERSION_COLUMN, serverVersion);
        database.update(FORM_VERSION_TABLE_NAME, values, FORM_DIR_NAME_COLUMN + " = ?",
                new String[]{formDirName});
    }

    public void updateFormName(String formDirName, String formName) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FORM_NAME_COLUMN, formName);
        database.update(FORM_VERSION_TABLE_NAME, values, FORM_DIR_NAME_COLUMN + " = ?",
                new String[]{formDirName});
    }

    public void updateSyncStatus(String formDirName, SyncStatus status) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SYNC_STATUS_COLUMN, status.value());
        database.update(FORM_VERSION_TABLE_NAME, values, FORM_DIR_NAME_COLUMN + " = ?",
                new String[]{formDirName});
    }

    public boolean formExists(String formDirName) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(FORM_VERSION_TABLE_NAME, new String[]{FORM_DIR_NAME_COLUMN},
                FORM_DIR_NAME_COLUMN + " = ?", new String[]{formDirName}, null, null, null);
        boolean isThere = cursor.moveToFirst();
        cursor.close();
        return isThere;
    }

    public ContentValues createValuesFormVersions(Map<String, String> params) {
        ContentValues values = new ContentValues();
        values.put(FORM_NAME_COLUMN, params.get(FORM_NAME_COLUMN));
        values.put(VERSION_COLUMN, params.get(VERSION_COLUMN));
        values.put(FORM_DIR_NAME_COLUMN, params.get(FORM_DIR_NAME_COLUMN));
        values.put(SYNC_STATUS_COLUMN, params.get(SYNC_STATUS_COLUMN));
        return values;
    }

    public ContentValues createValuesFromObject(FormDefinitionVersion formDefinitionVersion) {
        ContentValues values = new ContentValues();
        values.put(FORM_NAME_COLUMN, formDefinitionVersion.getFormName());
        values.put(VERSION_COLUMN, formDefinitionVersion.getVersion());
        values.put(FORM_DIR_NAME_COLUMN, formDefinitionVersion.getFormDirName());
        values.put(SYNC_STATUS_COLUMN, formDefinitionVersion.getSyncStatus().value());
        return values;
    }

    public long count() {
        return longForQuery(masterRepository().getReadableDatabase(),
                "SELECT COUNT(1) FROM " + FORM_VERSION_TABLE_NAME + " " + "WHERE "
                        + SYNC_STATUS_COLUMN + " = ?", new String[]{"SYNCED"});
    }

    private List<FormDefinitionVersion> readFormVersion(Cursor cursor) {
        cursor.moveToFirst();
        List<FormDefinitionVersion> submissions = new ArrayList<FormDefinitionVersion>();
        while (!cursor.isAfterLast()) {
            FormDefinitionVersion _formDefinitionVersion = new FormDefinitionVersion(
                    cursor.getString(cursor.getColumnIndex(FORM_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(FORM_DIR_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(VERSION_COLUMN)))
                    .withFormId(cursor.getString(cursor.getColumnIndex(ID_COLUMN)));
            String _syncStatus = cursor.getString(cursor.getColumnIndex(SYNC_STATUS_COLUMN));
            if (!_syncStatus.isEmpty()) {
                _formDefinitionVersion.withSyncStatus(SyncStatus.valueOf(_syncStatus));
            }
            submissions.add(_formDefinitionVersion);
            cursor.moveToNext();
        }
        cursor.close();
        return submissions;
    }

    private List<Map<String, String>> readFormVersionToMap(Cursor cursor) {
        cursor.moveToFirst();
        List<Map<String, String>> submissions = new ArrayList<Map<String, String>>();
        while (!cursor.isAfterLast()) {
            Map<String, String> _formDefinitionVersion = EasyMap.create(FORM_NAME_COLUMN,
                    cursor.getString(cursor.getColumnIndex(FORM_NAME_COLUMN)))
                    .put(FORM_DIR_NAME_COLUMN,
                            cursor.getString(cursor.getColumnIndex(FORM_DIR_NAME_COLUMN)))
                    .put(VERSION_COLUMN, cursor.getString(cursor.getColumnIndex(VERSION_COLUMN)))
                    .put(ID_COLUMN, cursor.getString(cursor.getColumnIndex(ID_COLUMN)))
                    .put(SYNC_STATUS_COLUMN,
                            cursor.getString(cursor.getColumnIndex(SYNC_STATUS_COLUMN))).map();

            submissions.add(_formDefinitionVersion);
            cursor.moveToNext();
        }
        cursor.close();
        return submissions;
    }

}