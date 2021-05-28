package org.smartregister.commonregistry;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.ColumnDetails;
import org.smartregister.repository.DrishtiRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * Created by Raihan Ahmed on 4/15/15.
 */
public class CommonRepository extends DrishtiRepository {
    public static final String ID_COLUMN = "id";
    public static final String Relational_ID = "relationalid";
    public static final String Relational_Underscore_ID = "relational_id";
    public static final String DETAILS_COLUMN = "details";
    public static final String IS_CLOSED_COLUMN = "is_closed";
    public static final String BASE_ENTITY_ID_COLUMN = "base_entity_id";
    public String TABLE_NAME = "common";
    public ColumnDetails[] common_TABLE_COLUMNS = new ColumnDetails[]{
            ColumnDetails.builder().name(ID_COLUMN).dataType("VARCHAR").build(),
            ColumnDetails.builder().name(Relational_ID).dataType("VARCHAR").build(),
            ColumnDetails.builder().name(DETAILS_COLUMN).dataType("VARCHAR").build(),
            ColumnDetails.builder().name(IS_CLOSED_COLUMN).dataType("TINYINT").defaultValue("0").build()
    };
    public final int initialColumnCount = common_TABLE_COLUMNS.length;
    public ColumnDetails[] additionalcolumns;
    private String common_SQL = "CREATE TABLE common(id VARCHAR PRIMARY KEY,details VARCHAR)";
    private String common_ID_INDEX_SQL =
            "CREATE INDEX common_id_index ON common(id COLLATE " + "NOCASE) ;";
    private String common_Relational_ID_INDEX_SQL = null;
    private String common_Relational_Underscore_ID_INDEX_SQL = null;
    private String common_Base_Entity_ID_INDEX_SQL = null;
    private String common_Custom_Relational_ID_INDEX_SQL = null;
    private CommonFtsObject commonFtsObject;
    private String[] columns = null;

    // Legacy Support
    public CommonRepository(String tablename, String[] columns) {
        ColumnDetails[] details = new ColumnDetails[columns.length];
        int x = 0;
        while (x < columns.length) {
            details[x] = ColumnDetails.builder().name(columns[x]).dataType("VARCHAR").build();
            x++;
        }

        initialize(tablename, details);
    }

    public CommonRepository(String tablename, ColumnDetails[] columns) {
        super();
        initialize(tablename, columns);

    }

    public CommonRepository(CommonFtsObject commonFtsObject, String tablename, ColumnDetails[] columns) {
        this(tablename, columns);
        this.commonFtsObject = commonFtsObject;
        if (this.commonFtsObject.getCustomRelationalId(TABLE_NAME) != null) {
            String customRelationalId = this.commonFtsObject.getCustomRelationalId(TABLE_NAME);

            Map<String, ColumnDetails> additionalColumns = new HashMap<>();
            if (this.additionalcolumns != null)
                for (ColumnDetails details : this.additionalcolumns)
                    additionalColumns.put(details.getName(), details);

            if (additionalColumns.containsKey(customRelationalId)) {
                common_Custom_Relational_ID_INDEX_SQL =
                        "CREATE INDEX " + TABLE_NAME + "_" + customRelationalId + "_index ON "
                                + TABLE_NAME + "(" + customRelationalId + " COLLATE NOCASE);";
            }
        }
    }

    private String getCommonSqlString(ColumnDetails[] columns) {

        StringBuilder builder = new StringBuilder("CREATE TABLE ").append(TABLE_NAME)
                .append("(id VARCHAR PRIMARY KEY,relationalid ")
                .append("VARCHAR, details VARCHAR, is_closed TINYINT DEFAULT 0");

        for (int i = 0; i < columns.length; i++) {
            if (i == 0) builder.append(", ");

            builder.append(columns[i].getName()).append(" ").append(columns[i].getDataType());
            if (StringUtils.isNotBlank(columns[i].getLength()))
                builder.append(" (").append(columns[i].getLength()).append(") ");

            if (StringUtils.isNotBlank(columns[i].getDefaultValue()))
                builder.append(" DEFAULT ").append(columns[i].getDefaultValue()).append(" ");

            if (i != columns.length - 1) {
                builder.append(",");
            } else {
                builder.append(" ");
            }
        }
        builder.append(")");

        return builder.toString();
    }

    private void initialize(String tablename, ColumnDetails[] columns) {
        additionalcolumns = columns;
        common_TABLE_COLUMNS = ArrayUtils.addAll(common_TABLE_COLUMNS, columns);
        TABLE_NAME = tablename;

        common_SQL = getCommonSqlString(columns);
        common_ID_INDEX_SQL =
                "CREATE INDEX " + TABLE_NAME + "_" + ID_COLUMN + "_index ON " + TABLE_NAME + "("
                        + ID_COLUMN + " COLLATE NOCASE);";
        common_Relational_ID_INDEX_SQL =
                "CREATE INDEX " + TABLE_NAME + "_" + Relational_ID + "_index ON " + TABLE_NAME + "("
                        + Relational_ID + " COLLATE NOCASE);";

        Map<String, ColumnDetails> additionalColumns = new HashMap<>();
        if (this.additionalcolumns != null)
            for (ColumnDetails details : this.additionalcolumns)
                additionalColumns.put(details.getName(), details);

        if (additionalColumns.containsKey(Relational_Underscore_ID)) {
            common_Relational_Underscore_ID_INDEX_SQL =
                    "CREATE INDEX " + TABLE_NAME + "_" + Relational_Underscore_ID + "_index ON "
                            + TABLE_NAME + "(" + Relational_Underscore_ID + " COLLATE NOCASE);";
        }
        if (additionalColumns.containsKey(BASE_ENTITY_ID_COLUMN)) {
            common_Base_Entity_ID_INDEX_SQL =
                    "CREATE INDEX " + TABLE_NAME + "_" + BASE_ENTITY_ID_COLUMN + "_index ON "
                            + TABLE_NAME + "(" + BASE_ENTITY_ID_COLUMN + " COLLATE NOCASE);";
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(common_SQL);
        if (StringUtils.isNotBlank(common_ID_INDEX_SQL)) {
            database.execSQL(common_ID_INDEX_SQL);
        }
        if (StringUtils.isNotBlank(common_Relational_ID_INDEX_SQL)) {
            database.execSQL(common_Relational_ID_INDEX_SQL);
        }
        if (StringUtils.isNotBlank(common_Relational_Underscore_ID_INDEX_SQL)) {
            database.execSQL(common_Relational_Underscore_ID_INDEX_SQL);
        }
        if (StringUtils.isNotBlank(common_Base_Entity_ID_INDEX_SQL)) {
            database.execSQL(common_Base_Entity_ID_INDEX_SQL);
        }
        if (StringUtils.isNotBlank(common_Custom_Relational_ID_INDEX_SQL)) {
            database.execSQL(common_Custom_Relational_ID_INDEX_SQL);
        }
    }

    public void add(CommonPersonObject common) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.insert(TABLE_NAME, null, createValuesFor(common));
    }

    public void updateDetails(String caseId, Map<String, String> details) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();

        CommonPersonObject common = findByCaseID(caseId);
        if (common == null) {
            return;
        }

        ContentValues valuesToUpdate = new ContentValues();
        valuesToUpdate.put(DETAILS_COLUMN, new Gson().toJson(details));
        database.update(TABLE_NAME, valuesToUpdate, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public void mergeDetails(String caseId, Map<String, String> details) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();

        CommonPersonObject common = findByCaseID(caseId);
        if (common == null) {
            return;
        }

        Map<String, String> mergedDetails = new HashMap<String, String>(common.getDetails());
        mergedDetails.putAll(details);
        ContentValues valuesToUpdate = new ContentValues();
        valuesToUpdate.put(DETAILS_COLUMN, new Gson().toJson(mergedDetails));
        database.update(TABLE_NAME, valuesToUpdate, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public String[] getTableColumns() {
        if (columns != null) return columns;

        columns = new String[common_TABLE_COLUMNS.length];

        int x = 0;
        while (x < common_TABLE_COLUMNS.length) {
            columns[x] = common_TABLE_COLUMNS[x].getName();
            x++;
        }

        return columns;
    }

    public List<CommonPersonObject> allcommon() {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database
                    .query(TABLE_NAME, getTableColumns(), null, null, null, null, null, null);
            return readAllcommon(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<CommonPersonObject> findByCaseIDs(String... caseIds) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    String.format("SELECT * FROM %s WHERE %s IN (%s)", TABLE_NAME, ID_COLUMN,
                            insertPlaceholdersForInClause(caseIds.length)), caseIds);
            return readAllcommon(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<CommonPersonObject> findByRelationalIDs(String... caseIds) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    String.format("SELECT * FROM %s WHERE %s COLLATE NOCASE" + " IN (%s)", TABLE_NAME,
                            Relational_ID, insertPlaceholdersForInClause(caseIds.length)), caseIds);
            return readAllcommon(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<CommonPersonObject> findByRelational_IDs(String... caseIds) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    String.format("SELECT * FROM %s WHERE %s COLLATE NOCASE" + " IN (%s)", TABLE_NAME,
                            Relational_Underscore_ID, insertPlaceholdersForInClause(caseIds.length)),
                    caseIds);
            return readAllcommon(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public CommonPersonObject findByCaseID(String caseId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database
                    .query(TABLE_NAME, getTableColumns(), ID_COLUMN + " = ?", new String[]{caseId},
                            null, null, null, null);
            List<CommonPersonObject> commons = readAllcommon(cursor);
            if (commons.isEmpty()) {
                return null;
            }
            return commons.get(0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public CommonPersonObject findByBaseEntityId(String baseEntityId) {
        Cursor cursor = null;
        try {
            cursor = masterRepository().getReadableDatabase().query(TABLE_NAME, getTableColumns(),
                    BASE_ENTITY_ID_COLUMN + " = ? " + "COLLATE NOCASE ", new String[]{baseEntityId},
                    null, null, null, null);
            List<CommonPersonObject> commons = readAllcommon(cursor);
            if (commons.isEmpty()) {
                return null;
            }
            return commons.get(0);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public CommonPersonObject findHHByGOBHHID(String caseId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database
                    .query(TABLE_NAME, getTableColumns(), "FWGOBHHID" + " = ?", new String[]{caseId},
                            null, null, null, null);
            List<CommonPersonObject> commons = readAllcommon(cursor);
            if (commons.isEmpty()) {
                return null;
            }
            return commons.get(0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public long count() {
        return longForQuery(masterRepository().getReadableDatabase(),
                "SELECT COUNT(1) FROM " + TABLE_NAME, new String[0]);
    }

    public void close(String caseId) {
//        ContentValues values = new ContentValues();
//        masterRepository().getWritableDatabase().update(EC_TABLE_NAME, values, ID_COLUMN + " =
// ?", new String[]{caseId});
    }

    private ContentValues createValuesFor(CommonPersonObject common) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, common.getCaseId());
        values.put(Relational_ID, common.getRelationalId());
        values.put(DETAILS_COLUMN, new Gson().toJson(common.getDetails()));
        return values;
    }

    private List<CommonPersonObject> readAllcommon(Cursor cursor) {
        cursor.moveToFirst();
        List<CommonPersonObject> commons = new ArrayList<CommonPersonObject>();
        while (!cursor.isAfterLast()) {
            int columncount = cursor.getColumnCount();
            HashMap<String, String> columns = new HashMap<String, String>();
            for (int i = initialColumnCount; i < columncount; i++) {
                columns.put(additionalcolumns[i - initialColumnCount].getName(), cursor.getString(i));
            }
            CommonPersonObject common = new CommonPersonObject(cursor.getString(0),
                    cursor.getString(1),
                    new Gson().<Map<String, String>>fromJson(cursor.getString(2),
                            new TypeToken<Map<String, String>>() {
                            }.getType()), TABLE_NAME);
            common.setClosed(cursor.getShort(cursor.getColumnIndex(IS_CLOSED_COLUMN)));
            common.setColumnmaps(columns);

            commons.add(common);
            cursor.moveToNext();
        }
        return commons;
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }

    private List<Map<String, String>> readDetailsList(Cursor cursor) {
        try {
            cursor.moveToFirst();
            List<Map<String, String>> detailsList = new ArrayList<Map<String, String>>();
            while (!cursor.isAfterLast()) {
                String detailsJSON = cursor.getString(0);
                detailsList.add(new Gson().<Map<String, String>>fromJson(detailsJSON,
                        new TypeToken<HashMap<String, String>>() {
                        }.getType()));
                cursor.moveToNext();
            }
            return detailsList;
        } finally {
            cursor.close();
        }
    }

    public void updateColumn(String tableName, ContentValues contentValues, String caseId) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.update(tableName, contentValues, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public List<CommonPersonObject> customQuery(String sql, String[] selections, String tableName) {

        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, selections);
        // database.
        return readAllcommonForField(cursor, tableName);
    }

    public List<CommonPersonObject> readAllcommonForField(Cursor cursor, String tableName) {
        List<CommonPersonObject> commons = new ArrayList<CommonPersonObject>();
        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int columncount = cursor.getColumnCount();
                HashMap<String, String> columns = new HashMap<String, String>();
                for (int i = 0; i < columncount; i++) {
                    columns.put(cursor.getColumnName(i), String.valueOf(cursor.getInt(i)));
                }
                CommonPersonObject common = new CommonPersonObject("1", "0", null, tableName);
                common.setClosed((short) 0);
                common.setColumnmaps(columns);

                commons.add(common);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            cursor.close();
        }

        return commons;
    }

    public List<CommonPersonObject> customQueryForCompleteRow(String sql, String[] selections,
                                                              String tableName) {

        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, selections);
        // database.
        return readAllcommonFor(cursor, tableName);
    }

    private List<CommonPersonObject> readAllcommonFor(Cursor cursor, String tableName) {
        List<CommonPersonObject> commons = new ArrayList<CommonPersonObject>();
        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                int columncount = cursor.getColumnCount();
                HashMap<String, String> columns = new HashMap<String, String>();
                for (int i = initialColumnCount; i < columncount; i++) {
                    columns.put(additionalcolumns[i - initialColumnCount].getName(), cursor.getString(i));
                }
                CommonPersonObject common = new CommonPersonObject("1", "0",
                        new Gson().<Map<String, String>>fromJson(
                                cursor.getString(cursor.getColumnIndex("details")),
                                new TypeToken<Map<String, String>>() {
                                }.getType()), tableName);
                common.setClosed((short) 0);
                common.setColumnmaps(columns);

                commons.add(common);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            cursor.close();
        }

        return commons;
    }

    public Cursor rawCustomQueryForAdapter(String query) {
        Timber.i(query);
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        return cursor;
    }

    public CommonPersonObject readAllcommonforCursorAdapter(Cursor cursor) {

        int columncount = cursor.getColumnCount();
        HashMap<String, String> columns = new HashMap<String, String>();
        for (int i = 0; i < columncount; i++) {
            String columnName = cursor.getColumnName(i);
            String value = cursor.getString(cursor.getColumnIndex(columnName));
            columns.put(columnName, value);
        }
        //CommonPersonObject common = new CommonPersonObject(cursor.getString(0),cursor.getString
        // (1),new Gson().<Map<String, String>>fromJson(cursor.getString(2), new
        // TypeToken<Map<String, String>>() {
        //}.getType()),TABLE_NAME);
        CommonPersonObject common = getCommonPersonObjectFromCursor(cursor);
        common.setColumnmaps(columns);

        return common;
    }

    public CommonPersonObject getCommonPersonObjectFromCursor(Cursor cursor) {
        CommonPersonObject commonPersonObject = null;
        String caseId = cursor.getString(cursor.getColumnIndex("_id"));
        String relationalid = cursor.getString(cursor.getColumnIndex("relationalid"));
        Map<String, String> details = sqliteRowToMap(cursor);
        commonPersonObject = new CommonPersonObject(caseId, relationalid, details, TABLE_NAME);
        return commonPersonObject;
    }

    /**
     * Insert the a new record to the database and returns its id
     **/
    public Long executeInsertStatement(ContentValues values, String tableName) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        String baseEntityId = values.getAsString("base_entity_id");
        values = addMissingContentValuesForRecordId(baseEntityId, tableName, values);
        //hack the id above is not set to be autogenerated so we'll reuse the base entity id
        values.put("id", baseEntityId);
        Long id = database.insertWithOnConflict(tableName, BaseColumns._ID, values,
                android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE);
        return id;
    }

    private ContentValues addMissingContentValuesForRecordId(String baseEntityId, String
            tableName, ContentValues cv) {
        Map<String, String> dbValues = new HashMap<String, String>();
        SQLiteDatabase db = masterRepository().getWritableDatabase();
        String query =
                "SELECT  * FROM " + tableName + " WHERE base_entity_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{baseEntityId});

        if (cursor != null && cursor.moveToFirst()) {
            dbValues = sqliteRowToMap(cursor);

            for (String key : dbValues.keySet()) {
                if (!cv.containsKey(key)) {
                    cv.put(key, dbValues.get(key));
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return cv;
    }

    public Map<String, String> sqliteRowToMap(Cursor cursor) {
        int totalColumn = cursor.getColumnCount();
        Map<String, String> rowObject = new HashMap<String, String>();
        if (cursor != null) {
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

    public Cursor queryTable(String query) {
        SQLiteDatabase db = masterRepository().getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    /**
     * Closes a case with the given baseEntityId
     *
     * @param baseEntityId
     */
    public void closeCase(String baseEntityId, String tableName) {
        try {
            SQLiteDatabase db = masterRepository().getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(IS_CLOSED_COLUMN, 1);
            db.update(tableName, cv, BASE_ENTITY_ID_COLUMN + "=?", new String[]{baseEntityId});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Deletes a case with the given baseEntityId
     *
     * @param baseEntityId
     */
    public boolean deleteCase(String baseEntityId, String tableName) {
        try {
            SQLiteDatabase db = masterRepository().getWritableDatabase();
            int afftectedRows = db
                    .delete(tableName, BASE_ENTITY_ID_COLUMN + " = ? COLLATE NOCASE" + " ",
                            new String[]{baseEntityId});
            if (afftectedRows > 0) {
                return true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return false;
    }

    public ArrayList<HashMap<String, String>> rawQuery(String sql, String[] selectionArgs) {
        ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
        Cursor cursor = null;
        try {
            SQLiteDatabase database = masterRepository().getReadableDatabase();
            cursor = database.rawQuery(sql, selectionArgs);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                // looping through all rows and adding to list
                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        map.put(cursor.getColumnName(i), cursor.getString(i));
                    }

                    maplist.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Timber.e(e);

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // return contact list
        return maplist;
    }

    public ContentValues populateSearchValues(String caseId) {
        CommonPersonObject commonPersonObject = findByCaseID(caseId);
        if (commonPersonObject == null) {
            return null;
        }

        if (commonFtsObject == null) {
            return null;
        }

        try {
            ContentValues searchValues = new ContentValues();

            Map<String, ColumnDetails> additionalColumns = new HashMap<>();
            if (this.additionalcolumns != null)
                for (ColumnDetails details : this.additionalcolumns)
                    additionalColumns.put(details.getName(), details);

            List<String> ftsSearchColumns = new ArrayList<String>();

            // Update Search Fields
            String[] ftsSearchFields = commonFtsObject.getSearchFields(TABLE_NAME);
            for (String ftsSearchField : ftsSearchFields) {
                if (!ftsSearchField.startsWith("alerts.")) {
                    String ftsSearchValue = getSearchFieldValue(commonPersonObject, ftsSearchField);
                    String ftsSearchColumn = withSub(ftsSearchValue);
                    ftsSearchColumns.add(ftsSearchColumn);
                }
            }

            String phraseSeparator = " | ";
            String phrase = StringUtils.join(ftsSearchColumns, phraseSeparator);

            searchValues.put(CommonFtsObject.phraseColumn, phrase);

            // Update Main Condition Fields
            String[] ftsMainConditionFields = commonFtsObject.getMainConditions(TABLE_NAME);
            if (ftsMainConditionFields != null) {
                for (String ftsMainConditionField : ftsMainConditionFields) {
                    String value = null;
                    if (ftsMainConditionField.equals("details")) {
                        Map<String, String> details = commonPersonObject.getDetails();
                        if (details != null && !details.isEmpty()) {
                            value = new Gson().toJson(details);
                        }
                    } else {
                        value = getSearchFieldValue(commonPersonObject, ftsMainConditionField);
                    }

                    searchValues.put(ftsMainConditionField, value);
                }
            }

            // Update Sort Fields
            String[] ftsSortFields = commonFtsObject.getSortFields(TABLE_NAME);
            if (ftsSortFields != null) {
                for (String ftsSortField : ftsSortFields) {
                    if (!ftsSortField.startsWith("alerts.")) {
                        String ftsSortValue = getSearchFieldValue(commonPersonObject, ftsSortField);
                        searchValues.put(ftsSortField, ftsSortValue);
                    }
                }
            }

            // Update Common Fields
            searchValues.put(CommonFtsObject.idColumn, caseId);

            if (additionalColumns.containsKey(Relational_Underscore_ID)) {
                searchValues.put(CommonFtsObject.relationalIdColumn,
                        commonPersonObject.getColumnmaps().get(Relational_Underscore_ID));
            } else if (commonPersonObject.getRelationalId() != null) {
                searchValues.put(CommonFtsObject.relationalIdColumn,
                        commonPersonObject.getRelationalId());
            }

            searchValues.put(CommonFtsObject.isClosedColumnName, commonPersonObject.getClosed());
            return searchValues;
        } catch (Exception e) {
            Timber.e(e, "Update Search Error");
            return null;
        }
    }

    public boolean populateSearchValues(String caseId, String field, String value, String[]
            listToRemove) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();

        CommonPersonObject commonPersonObject = findByCaseID(caseId);
        if (commonPersonObject == null) {
            return false;
        }

        if (commonFtsObject == null) {
            return false;
        }

        ContentValues searchValues = new ContentValues();

        String ftsSearchTable = CommonFtsObject.searchTableName(TABLE_NAME);

        String selectSql =
                "SELECT " + CommonFtsObject.idColumn + ", " + CommonFtsObject.phraseColumn
                        + " FROM " + ftsSearchTable + " WHERE  " + CommonFtsObject.idColumn
                        + " MATCH ?";
        if (!field.equals(CommonFtsObject.phraseColumn)) {
            selectSql =
                    "SELECT " + CommonFtsObject.idColumn + ", " + field + " FROM " + ftsSearchTable
                            + " WHERE  " + CommonFtsObject.idColumn + " MATCH ?";
        }

        ArrayList<HashMap<String, String>> mapList = rawQuery(selectSql, new String[]{caseId});

        if (mapList.isEmpty()) {
            return false;
        }

        if (field.equals(CommonFtsObject.phraseColumn)) {
            HashMap<String, String> map = mapList.get(0);
            String oldSearchValue = map.get(CommonFtsObject.phraseColumn);

            if (listToRemove != null && listToRemove.length > 0) {
                for (String s : listToRemove) {
                    if (oldSearchValue.contains(s)) {
                        oldSearchValue = oldSearchValue.replace("| " + s, "");
                    }
                }
            }

            // Underscore does not work well in fts search
            if (value.contains("_")) {
                value = value.replace("_", "");
            }

            List<String> ftsSearchColumns = new ArrayList<String>();
            ftsSearchColumns.add(oldSearchValue);
            ftsSearchColumns.add(value);

            String phraseSeparator = " | ";
            String phrase = StringUtils.join(ftsSearchColumns, phraseSeparator);

            searchValues.put(CommonFtsObject.phraseColumn, phrase);

        } else {
            HashMap<String, String> map = mapList.get(0);
            String fieldValue = map.get(field);
            // If field value is complete it should not be changed
            if (fieldValue != null && fieldValue.equals(AlertStatus.complete.value())) {
                return false;
            }

            searchValues.put(field, value);
        }

        try {
            int rowsAffected = database
                    .update(ftsSearchTable, searchValues, CommonFtsObject.idColumn + " MATCH ?",
                            new String[]{caseId});
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean searchBatchInserts(Map<String, ContentValues> searchMap) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();

        database.beginTransaction();
        String ftsSearchTable = CommonFtsObject.searchTableName(TABLE_NAME);
        try {
            for (String caseId : searchMap.keySet()) {
                ContentValues searchValues = searchMap.get(caseId);
                ArrayList<HashMap<String, String>> mapList = rawQuery(
                        "SELECT " + CommonFtsObject.idColumn + " FROM " + ftsSearchTable
                                + " WHERE " + CommonFtsObject.idColumn + " MATCH ?", new String[]{caseId});
                if (!mapList.isEmpty()) {
                    int updated = database.update(ftsSearchTable, searchValues,
                            CommonFtsObject.idColumn + " MATCH ?", new String[]{caseId});
                    Timber.i("Fts Row Updated: %s", String.valueOf(updated));

                } else {
                    long rowId = database.insert(ftsSearchTable, null, searchValues);
                    Timber.i("Details Row Inserted : %s", String.valueOf(rowId));
                }
            }
            database.setTransactionSuccessful();
            database.endTransaction();

            return true;
        } catch (Exception e) {
            Timber.e(e, "Update Search Error");
            database.endTransaction();
            return false;
        }
    }

    public boolean deleteSearchRecord(String caseId) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();

        database.beginTransaction();
        String ftsSearchTable = CommonFtsObject.searchTableName(TABLE_NAME);
        try {

            int afftectedRows = database
                    .delete(ftsSearchTable, CommonFtsObject.idColumn + " MATCH ?",
                            new String[]{caseId});

            database.setTransactionSuccessful();
            database.endTransaction();

            if (afftectedRows > 0) {
                return true;
            }
        } catch (Exception e) {
            Timber.e(e, "Update Search Error");
            database.endTransaction();
        }

        return false;
    }

    public List<String> findSearchIds(String query) {

        List<String> ids = new ArrayList<String>();
        Cursor cursor = null;
        try {
            SQLiteDatabase database = masterRepository().getReadableDatabase();

            Timber.i(query);
            cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    ids.add(id);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ids;
    }

    public int countSearchIds(String query) {

        int count = 0;
        Cursor cursor = null;
        try {
            SQLiteDatabase database = masterRepository().getReadableDatabase();

            Timber.i(query);
            cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    public boolean isFts() {
        return commonFtsObject != null;
    }

    private String withSub(String s) {
        String withSub = "";
        if (s == null || s.isEmpty()) {
            return withSub;
        }
        int length = s.length();

        for (int i = 0; i < length; i++) {
            withSub += s.substring(i) + " ";
        }
        return withSub.trim();
    }

    private String getSearchFieldValue(CommonPersonObject commonPersonObject, String field) {
        if (field.equals(ID_COLUMN) || field.equals(Relational_ID) || field
                .equals(IS_CLOSED_COLUMN)) {
            return null;
        }

        Map<String, ColumnDetails> additionalColumns = new HashMap<>();
        if (this.additionalcolumns != null)
            for (ColumnDetails details : this.additionalcolumns)
                additionalColumns.put(details.getName(), details);

        if (!additionalColumns.containsKey(field)) {
            if (additionalColumns.containsKey(Relational_Underscore_ID)) { // Try getting the field
                // by the relational_id
                return getFieldValueFromRelatedTable(field,
                        commonPersonObject.getColumnmaps().get(Relational_Underscore_ID));
            } else if (commonFtsObject.getCustomRelationalId(TABLE_NAME) != null
                    && additionalColumns.containsKey(commonFtsObject
                    .getCustomRelationalId(TABLE_NAME))) {  // Try getting the field by a
                // pre-defined custom relational id
                return getFieldValueFromRelatedTable(field, commonPersonObject.getColumnmaps()
                        .get(commonFtsObject.getCustomRelationalId(TABLE_NAME)));
            } else { // Try getting the field by the case Id
                return getFieldValueFromRelatedTable(field, commonPersonObject.getCaseId());
            }
        } else {
            return commonPersonObject.getColumnmaps().get(field);
        }
    }

    private String getFieldValueFromRelatedTable(String fieldName, String relationId) {
        if (StringUtils.isBlank(relationId)) {
            return null;
        }

        for (String table : commonFtsObject.getTables()) {
            if (!table.equals(TABLE_NAME) && isFieldExist(table, fieldName)) {
                ArrayList<HashMap<String, String>> list = rawQuery(
                        " SELECT " + fieldName + " FROM " + table + " WHERE " + ID_COLUMN
                                + " = ?", new String[]{relationId});
                if (!list.isEmpty()) {
                    return list.get(0).get(fieldName);
                }
            }
        }
        return null;
    }

    private boolean isFieldExist(String tableName, String fieldName) {
        boolean isExist = false;
        SQLiteDatabase db = masterRepository().getWritableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        int index = cursor.getColumnIndex("name");
        if (cursor.moveToFirst()) {
            do {
                String columnName = cursor.getString(index);
                if (columnName.equals(fieldName)) {
                    isExist = true;
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return isExist;
    }
}
