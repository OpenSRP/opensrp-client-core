package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.SyncStatus;
import org.smartregister.domain.form.FormSubmission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.smartregister.AllConstants.APP_NAME_INDONESIA;
import static org.smartregister.AllConstants.ENTITY_ID_FIELD_NAME;
import static org.smartregister.AllConstants.ENTITY_ID_PARAM;
import static org.smartregister.AllConstants.INSTANCE_ID_PARAM;
import static org.smartregister.AllConstants.SYNC_STATUS;
import static org.smartregister.domain.SyncStatus.PENDING;
import static org.smartregister.domain.SyncStatus.SYNCED;

public class FormDataRepository extends DrishtiRepository {
    public static final String INSTANCE_ID_COLUMN = "instanceId";
    public static final String ENTITY_ID_COLUMN = "entityId";
    public static final String ID_COLUMN = "id";
    private static final String FORM_SUBMISSION_SQL =
            "CREATE TABLE form_submission(instanceId " + "VARCHAR PRIMARY KEY, entityId VARCHAR, "
                    + "formName VARCHAR, instance VARCHAR, "
                    + "version VARCHAR, serverVersion VARCHAR, formDataDefinitionVersion VARCHAR, "
                    + "syncStatus VARCHAR)";
    private static final String FORM_NAME_COLUMN = "formName";
    private static final String INSTANCE_COLUMN = "instance";
    private static final String VERSION_COLUMN = "version";
    private static final String SERVER_VERSION_COLUMN = "serverVersion";
    private static final String SYNC_STATUS_COLUMN = "syncStatus";
    private static final String FORM_DATA_DEFINITION_VERSION_COLUMN = "formDataDefinitionVersion";
    public static final String[] FORM_SUBMISSION_TABLE_COLUMNS = new String[]{INSTANCE_ID_COLUMN,
            ENTITY_ID_COLUMN, FORM_NAME_COLUMN, INSTANCE_COLUMN, VERSION_COLUMN,
            SERVER_VERSION_COLUMN, FORM_DATA_DEFINITION_VERSION_COLUMN, SYNC_STATUS_COLUMN};
    private static final String FORM_SUBMISSION_TABLE_NAME = "form_submission";
    private static final String DETAILS_COLUMN_NAME = "details";
    private static final String FORM_NAME_PARAM = "formName";
    private Map<String, String[]> TABLE_COLUMN_MAP;

    public FormDataRepository() {
        TABLE_COLUMN_MAP = new HashMap<String, String[]>();

        TABLE_COLUMN_MAP.put(EligibleCoupleRepository.EC_TABLE_NAME,
                EligibleCoupleRepository.EC_TABLE_COLUMNS);
        TABLE_COLUMN_MAP
                .put(MotherRepository.MOTHER_TABLE_NAME, MotherRepository.MOTHER_TABLE_COLUMNS);
        TABLE_COLUMN_MAP.put(ChildRepository.CHILD_TABLE_NAME, ChildRepository.CHILD_TABLE_COLUMNS);

        if (APP_NAME_INDONESIA.equals(CoreLibrary.getInstance().context().configuration().appName())) {
            return;
        }

        for (int i = 0; i < Context.bindtypes.size(); i++) {
            TABLE_COLUMN_MAP.put(Context.bindtypes.get(i).getBindtypename(), CoreLibrary.getInstance().context()
                    .commonrepository(
                            Context.bindtypes.get(i).getBindtypename()).getTableColumns());
        }
    }

    public void addTableColumnMap(String key, String[] val) {
        TABLE_COLUMN_MAP.put(key, val);
    }

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(FORM_SUBMISSION_SQL);
    }

    @JavascriptInterface
    public String queryUniqueResult(String sql, String[] selectionArgs) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, selectionArgs);

        cursor.moveToFirst();
        Map<String, String> result = readARow(cursor);
        cursor.close();

        return new Gson().toJson(result);
    }

    @JavascriptInterface
    public String queryList(String sql, String[] selectionArgs) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, selectionArgs);
        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            results.add(readARow(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return new Gson().toJson(results);
    }

    @JavascriptInterface
    public String saveFormSubmission(String paramsJSON, String data, String
            formDataDefinitionVersion) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        Map<String, String> params = new Gson()
                .fromJson(paramsJSON, new TypeToken<Map<String, String>>() {
                }.getType());
        database.insert(FORM_SUBMISSION_TABLE_NAME, null,
                createValuesForFormSubmission(params, data, formDataDefinitionVersion));
        return params.get(INSTANCE_ID_PARAM);
    }

    @JavascriptInterface
    public void saveFormSubmission(FormSubmission formSubmission) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.insert(FORM_SUBMISSION_TABLE_NAME, null,
                createValuesForFormSubmission(formSubmission));
    }

    public FormSubmission fetchFromSubmission(String instanceId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(FORM_SUBMISSION_TABLE_NAME, FORM_SUBMISSION_TABLE_COLUMNS,
                INSTANCE_ID_COLUMN + " = ?", new String[]{instanceId}, null, null, null);
        return readFormSubmission(cursor).get(0);
    }

    public List<FormSubmission> getPendingFormSubmissions() {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(FORM_SUBMISSION_TABLE_NAME, FORM_SUBMISSION_TABLE_COLUMNS,
                SYNC_STATUS_COLUMN + " = ?", new String[]{PENDING.value()}, null, null, null);
        return readFormSubmission(cursor);
    }

    public long getPendingFormSubmissionsCount() {
        return longForQuery(masterRepository().getReadableDatabase(),
                "SELECT COUNT(1) FROM " + FORM_SUBMISSION_TABLE_NAME + " " + "" + "WHERE "
                        + SYNC_STATUS_COLUMN + " = ? ", new String[]{PENDING.value()});
    }

    public void markFormSubmissionsAsSynced(List<FormSubmission> formSubmissions) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        for (FormSubmission submission : formSubmissions) {
            FormSubmission updatedSubmission = new FormSubmission(submission.instanceId(),
                    submission.entityId(), submission.formName(), submission.instance(),
                    submission.version(), SYNCED, "1");
            database.update(FORM_SUBMISSION_TABLE_NAME,
                    createValuesForFormSubmission(updatedSubmission), INSTANCE_ID_COLUMN + " = ?",
                    new String[]{updatedSubmission.instanceId()});
        }
    }

    public void updateServerVersion(String instanceId, String serverVersion) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SERVER_VERSION_COLUMN, serverVersion);
        database.update(FORM_SUBMISSION_TABLE_NAME, values, INSTANCE_ID_COLUMN + " = ?",
                new String[]{instanceId});
    }

    public boolean submissionExists(String instanceId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(FORM_SUBMISSION_TABLE_NAME, new String[]{INSTANCE_ID_COLUMN},
                INSTANCE_ID_COLUMN + " = ?", new String[]{instanceId}, null, null, null);
        boolean isThere = cursor.moveToFirst();
        cursor.close();
        return isThere;
    }

    @JavascriptInterface
    public String saveEntity(String entityType, String fields) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        Map<String, String> updatedFieldsMap = new Gson()
                .fromJson(fields, new TypeToken<Map<String, String>>() {
                }.getType());

        String entityId = updatedFieldsMap.get(ENTITY_ID_FIELD_NAME);
        Map<String, String> entityMap = loadEntityMap(entityType, database, entityId);

        ContentValues contentValues = getContentValues(updatedFieldsMap, entityType, entityMap);
        database.replace(entityType, null, contentValues);
        return entityId;
    }

    private ContentValues createValuesForFormSubmission(FormSubmission submission) {
        ContentValues values = new ContentValues();
        values.put(INSTANCE_ID_COLUMN, submission.instanceId());
        values.put(ENTITY_ID_COLUMN, submission.entityId());
        values.put(FORM_NAME_COLUMN, submission.formName());
        values.put(INSTANCE_COLUMN, submission.instance());
        values.put(VERSION_COLUMN, submission.version());
        values.put(SERVER_VERSION_COLUMN, submission.serverVersion());
        values.put(FORM_DATA_DEFINITION_VERSION_COLUMN, submission.formDataDefinitionVersion());
        values.put(SYNC_STATUS_COLUMN, submission.syncStatus().value());
        return values;
    }

    private ContentValues createValuesForFormSubmission(Map<String, String> params, String data,
                                                        String formDataDefinitionVersion) {
        ContentValues values = new ContentValues();
        values.put(INSTANCE_ID_COLUMN, params.get(INSTANCE_ID_PARAM));
        values.put(ENTITY_ID_COLUMN, params.get(ENTITY_ID_PARAM));
        values.put(FORM_NAME_COLUMN, params.get(FORM_NAME_PARAM));
        values.put(INSTANCE_COLUMN, data);
        values.put(VERSION_COLUMN, currentTimeMillis());
        values.put(FORM_DATA_DEFINITION_VERSION_COLUMN, formDataDefinitionVersion);
        String syncStatus = PENDING.value();
        if (params.containsKey(SYNC_STATUS)) {
            syncStatus = params.get(SYNC_STATUS);
        }
        values.put(SYNC_STATUS_COLUMN, syncStatus);
        return values;
    }

    private List<FormSubmission> readFormSubmission(Cursor cursor) {
        cursor.moveToFirst();
        List<FormSubmission> submissions = new ArrayList<FormSubmission>();
        while (!cursor.isAfterLast()) {
            submissions.add(new FormSubmission(
                    cursor.getString(cursor.getColumnIndex(INSTANCE_ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(ENTITY_ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(FORM_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(INSTANCE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(VERSION_COLUMN)),
                    SyncStatus.valueOf(cursor.getString(cursor.getColumnIndex(SYNC_STATUS_COLUMN))),
                    cursor.getString(cursor.getColumnIndex(FORM_DATA_DEFINITION_VERSION_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SERVER_VERSION_COLUMN))));
            cursor.moveToNext();
        }
        cursor.close();
        return submissions;
    }

    private Map<String, String> readARow(Cursor cursor) {
        Map<String, String> columnValues = new HashMap<String, String>();
        if (cursor.isAfterLast()) {
            return columnValues;
        }
        String[] columns = cursor.getColumnNames();
        int numberOfColumns = columns.length;
        for (int index = 0; index < numberOfColumns; index++) {
            String columnValue = cursor.getString(index);
            if (DETAILS_COLUMN_NAME.equalsIgnoreCase(columns[index]) && columnValue != null
                    && !columnValue.isEmpty()) {
                Map<String, String> details = new Gson()
                        .fromJson(columnValue, new TypeToken<Map<String, String>>() {
                        }.getType());
                columnValues.putAll(details);
            } else {
                columnValues.put(columns[index], columnValue);
            }
        }
        return columnValues;
    }

    private ContentValues getContentValues(Map<String, String> updatedFieldsMap, String
            entityType, Map<String, String> entityMap) {
        List<String> columns = asList(TABLE_COLUMN_MAP.get(entityType));
        ContentValues contentValues = initializeContentValuesBasedExistingValues(entityMap);
        Map<String, String> details = initializeDetailsBasedOnExistingValues(entityMap);

        for (String fieldName : updatedFieldsMap.keySet()) {
            if (columns.contains(fieldName)) {
                contentValues.put(fieldName, updatedFieldsMap.get(fieldName));
            } else {
                details.put(fieldName, updatedFieldsMap.get(fieldName));
            }
        }
        contentValues.put(DETAILS_COLUMN_NAME, new Gson().toJson(details));

        return contentValues;
    }

    private Map<String, String> initializeDetailsBasedOnExistingValues(Map<String, String>
                                                                               entityMap) {
        Map<String, String> details;
        String detailsJSON = entityMap.get(DETAILS_COLUMN_NAME);
        if (detailsJSON == null) {
            details = new HashMap<String, String>();
        } else {
            details = new Gson().fromJson(detailsJSON, new TypeToken<Map<String, String>>() {
            }.getType());
        }
        return details;
    }

    private ContentValues initializeContentValuesBasedExistingValues(Map<String, String>
                                                                             entityMap) {
        ContentValues contentValues = new ContentValues();
        for (String column : entityMap.keySet()) {
            contentValues.put(column, entityMap.get(column));
        }
        return contentValues;
    }

    private Map<String, String> loadEntityMap(String entityType, SQLiteDatabase database, String
            entityId) {
        Map<String, String> entityMap = new HashMap<String, String>();
        Cursor cursor = database
                .query(entityType, TABLE_COLUMN_MAP.get(entityType), ID_COLUMN + " =?",
                        new String[]{entityId}, null, null, null);
        if (!cursor.isAfterLast()) {
            cursor.moveToFirst();
            for (String column : cursor.getColumnNames()) {
                entityMap.put(column, cursor.getString(cursor.getColumnIndex(column)));
            }
        }
        cursor.close();
        return entityMap;
    }

    @JavascriptInterface
    public String generateIdFor(String entityType) {
        return randomUUID().toString();
    }

    public Map<String, String> getMapFromSQLQuery(String sql, String[] selectionArgs) {
        Map<String, String> map = new HashMap<String, String>();
        Cursor cursor = null;
        try {
            SQLiteDatabase database = masterRepository().getReadableDatabase();
            cursor = database.rawQuery(sql, selectionArgs);
            map = sqliteRowToMap(cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return map;
    }

    public Map<String, String> sqliteRowToMap(Cursor cursor) {
        int totalColumn = cursor.getColumnCount();
        Map<String, String> rowObject = new HashMap<String, String>();
        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                    } catch (Exception e) {
                        Timber.d(e.getMessage());
                    }
                }
            }
        }
        return rowObject;
    }
}
