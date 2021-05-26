


package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.domain.Client;
import org.smartregister.domain.ClientRelationship;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.Column;
import org.smartregister.domain.db.ColumnAttribute;
import org.smartregister.domain.db.EventClient;
import org.smartregister.p2p.sync.data.JsonData;
import org.smartregister.sync.intent.P2pProcessRecordsService;
import org.smartregister.util.DatabaseMigrationUtils;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.Utils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.AllConstants.ROWID;

/**
 * Created by keyman on 27/07/2017.
 */
public class EventClientRepository extends BaseRepository {

    private static final String EVENT_ID = "id";

    private static final String _ID = "_id";

    public static final String VARCHAR = "VARCHAR";

    protected Table clientTable;
    protected Table eventTable;

    protected int FORM_SUBMISSION_IDS_PAGE_SIZE = 250;

    public EventClientRepository() {
        this.clientTable = Table.client;
        this.eventTable = Table.event;
    }

    public EventClientRepository(Table clientTable, Table eventTable) {
        this.clientTable = clientTable;
        this.eventTable = eventTable;
    }

    public static String getCreateTableColumn(Column col) {
        ColumnAttribute c = col.column();
        return "`" + col.name() + "` " + getSqliteType(c.type()) + (c.pk() ? " PRIMARY KEY " : "");
    }

    public static String removeEndingComma(String str) {
        if (str.trim().endsWith(",")) {
            return str.substring(0, str.lastIndexOf(","));
        }
        return str;
    }

    public static void createTable(SQLiteDatabase db, BaseTable table, Column[] columns) {
        try {
            String cl = "";
            for (Column cc : columns) {
                cl += getCreateTableColumn(cc) + ",";
            }
            cl = removeEndingComma(cl);
            String create_tb = "CREATE TABLE " + table.name() + " ( " + cl + " )";

            db.execSQL(create_tb);

            createIndex(db, table, columns);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void createIndex(SQLiteDatabase db, BaseTable table, Column[] columns) {
        try {
            for (Column cc : columns) {
                if (cc.column().index()) {
                    String create_id = "CREATE INDEX IF NOT EXISTS "
                            + table.name() + "_" + cc.name()
                            + "_index ON "
                            + table.name()
                            + " ("
                            + cc.name()
                            + "); ";
                    db.execSQL(create_id);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * add columns to event and client tables to support Plan evaluation engine
     *
     * @param db          the database being upgraded
     * @param eventTable  event table columns are being added to
     * @param clientTable event client columns are being added to
     */
    public static void createAdditionalColumns(SQLiteDatabase db, String eventTable, String clientTable) {
        DatabaseMigrationUtils.addColumnIfNotExists(db, eventTable, event_column.planId.name(), VARCHAR);
        DatabaseMigrationUtils.addColumnIfNotExists(db, clientTable, client_column.locationId.name(), VARCHAR);
        DatabaseMigrationUtils.addColumnIfNotExists(db, clientTable, client_column.clientType.name(), VARCHAR);
        DatabaseMigrationUtils.addColumnIfNotExists(db, clientTable, client_column.residence.name(), VARCHAR);
        DatabaseMigrationUtils.addIndexIfNotExists(db, clientTable, client_column.residence.name());
        DatabaseMigrationUtils.addIndexIfNotExists(db, clientTable, client_column.locationId.name());
    }

    /**
     * add columns to event and client tables to support Plan evaluation engine
     *
     * @param db the database being upgraded
     */
    public static void createAdditionalColumns(SQLiteDatabase db) {
        createAdditionalColumns(db, Table.event.name(), Table.client.name());
    }

    /**
     * add locationId  column on event table
     *
     * @param db the database being upgraded
     */
    public static void addEventLocationId(SQLiteDatabase db) {
        DatabaseMigrationUtils.addColumnIfNotExists(db, Table.event.name(), event_column.locationId.name(), VARCHAR);
        DatabaseMigrationUtils.addIndexIfNotExists(db, Table.event.name(), event_column.locationId.name());
        db.execSQL(String.format("UPDATE %s set %s = %s WHERE %s >0", Table.event.name(), event_column.locationId.name(), "substr(json,instr(json, '\"locationId\":')+14,36)", "instr(json, '\"locationId\":')"));
    }

    /**
     * add taskId  column on event table
     *
     * @param db the database being upgraded
     */
    public static void addEventTaskId(SQLiteDatabase db) {
        DatabaseMigrationUtils.addColumnIfNotExists(db, Table.event.name(), event_column.taskId.name(), VARCHAR);
        DatabaseMigrationUtils.addIndexIfNotExists(db, Table.event.name(), event_column.taskId.name());
    }

    public static void dropIndexes(SQLiteDatabase db, BaseTable table) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = ?"
                    + " AND sql is not null AND tbl_name = ?", new String[]{"index", table.name()});
            while (cursor.moveToNext()) {
                db.execSQL("DROP INDEX " + cursor.getString(0));
            }
        } catch (Exception e) {
            Timber.e(EventClientRepository.class.getName(), "SQLException", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public static String getSqliteType(ColumnAttribute.Type type) {
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.text.name())) {
            return "varchar";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.bool.name())) {
            return "boolean";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.date.name())) {
            return "datetime";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.list.name())) {
            return "varchar";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.map.name())) {
            return "varchar";
        }
        if (type.name().equalsIgnoreCase(ColumnAttribute.Type.longnum.name())) {
            return "integer";
        }
        return null;
    }

    public Table getClientTable() {
        return clientTable;
    }

    public Table getEventTable() {
        return eventTable;
    }

    private void populateAdditionalColumns(ContentValues values, Column[] columns, JSONObject jsonObject) {
        for (Column column : columns) {
            try {
                if (values.containsKey(column.name()))//column already added
                    continue;
                if (jsonObject.has(column.name())) {
                    Object value = jsonObject.get(column.name());
                    if (column.column().type().equals(ColumnAttribute.Type.date)) {
                        values.put(column.name(), dateFormat.format(new DateTime(value).toDate()));
                    } else if (column.column().type().equals(ColumnAttribute.Type.longnum)) {
                        values.put(column.name(), Long.valueOf(value.toString()));
                    } else {
                        values.put(column.name(), value.toString());
                    }
                }
            } catch (Exception e) {
                Timber.e(e, "Error updating column %s for event %s", column.name(), jsonObject);
            }
        }

    }


    public Boolean checkIfExists(Table table, String baseEntityId) {
        return checkIfExists(table, baseEntityId, getWritableDatabase());
    }

    public Boolean checkIfExists(Table table, String baseEntityId, SQLiteDatabase sqLiteDatabase) {
        Cursor mCursor = null;
        try {
            String query = "SELECT "
                    + event_column.baseEntityId
                    + " FROM "
                    + table.name()
                    + " WHERE "
                    + event_column.baseEntityId
                    + " = ?";
            mCursor = sqLiteDatabase.rawQuery(query, new String[]{baseEntityId});
            if (mCursor != null && mCursor.moveToFirst()) {

                return true;
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return false;
    }

    private List<String> getFormSubmissionIdsFromJsonArray(JSONArray array) {
        if (array != null && array.length() != 0) {
            List<String> stringList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.optJSONObject(i);
                if (jsonObject != null) {
                    String formSubmissionId = jsonObject.optString(event_column.formSubmissionId.name());
                    if (StringUtils.isNotBlank(formSubmissionId)) {
                        stringList.add(formSubmissionId);
                    }
                }
            }
            return stringList;
        }
        return new ArrayList<>();
    }

    protected void populateFormSubmissionIds(@NonNull List<String> formSubmissionIdsList,
                                             Set<String> formSubmissionIds) {
        int tempPageSize = FORM_SUBMISSION_IDS_PAGE_SIZE;

        List<String> tempList;

        boolean shouldEnd = false;

        if (formSubmissionIdsList.size() <= tempPageSize) {
            tempList = formSubmissionIdsList;
            shouldEnd = true;
        } else {
            tempList = formSubmissionIdsList.subList(0, tempPageSize);
        }

        String query = "SELECT " + EventClientRepository.event_column.formSubmissionId + " FROM event " +
                "WHERE " + EventClientRepository.event_column.formSubmissionId + " IN ( " + StringUtils.repeat("?", ", ", tempList.size()) + ")";

        try (Cursor mCursor = getReadableDatabase().rawQuery(query, tempList.toArray(new String[0]))) {
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    formSubmissionIds.add(mCursor.getString(0));
                }
            }
        } catch (SQLException e) {
            Timber.e(e);
        }

        if (shouldEnd) {
            return;
        }

        populateFormSubmissionIds(formSubmissionIdsList.subList(tempPageSize, formSubmissionIdsList.size()), formSubmissionIds);
    }

    public Boolean checkIfExistsByFormSubmissionId(Table table, String formSubmissionId) {
        return checkIfExistsByFormSubmissionId(table, formSubmissionId, getReadableDatabase());
    }

    public Boolean checkIfExistsByFormSubmissionId(Table table, String
            formSubmissionId, SQLiteDatabase sqLiteDatabase) {
        Cursor mCursor = null;
        try {
            String query = "SELECT "
                    + event_column.formSubmissionId
                    + " FROM "
                    + table.name()
                    + " WHERE "
                    + event_column.formSubmissionId
                    + " =?";
            mCursor = sqLiteDatabase.rawQuery(query, new String[]{formSubmissionId});
            if (mCursor != null && mCursor.moveToFirst()) {

                return true;
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return false;
    }

    private boolean populateStatement(SQLiteStatement statement, Table table, JSONObject
            jsonObject, Map<String, Integer> columnOrder) {
        if (statement == null)
            return false;
        statement.clearBindings();
        List columns;
        try {
            if (table.equals(clientTable)) {
                columns = Arrays.asList(client_column.values());
                createClientBindings(statement, jsonObject, columnOrder);
            } else if (table.equals(eventTable)) {
                columns = Arrays.asList(event_column.values());
                createEventBindings(statement, jsonObject, columnOrder);
            } else {
                return false;
            }

            List<? extends Column> otherColumns = new ArrayList(columns);
            if (!otherColumns.isEmpty()) {
                otherColumns.removeAll(Arrays.asList(client_column.json, client_column.updatedAt, client_column.syncStatus, client_column.validationStatus, client_column.baseEntityId,
                        client_column.residence, client_column.locationId, client_column.clientType,
                        event_column.json, event_column.updatedAt, event_column.syncStatus, event_column.validationStatus, event_column.baseEntityId, event_column.eventId
                        , event_column.planId, event_column.taskId));
            }

            for (Column column : otherColumns) {
                if (jsonObject.has(column.name())) {
                    Object value = jsonObject.get(column.name());
                    if (column.column().type().equals(ColumnAttribute.Type.date)) {
                        statement.bindString(columnOrder.get(column.name()), dateFormat.format(new DateTime(value).toDate()));
                    } else if (column.column().type().equals(ColumnAttribute.Type.longnum)) {
                        statement.bindLong(columnOrder.get(column.name()), Long.valueOf(value.toString()));
                    } else {
                        statement.bindString(columnOrder.get(column.name()), value.toString());
                    }
                } else {
                    statement.bindNull(columnOrder.get(column.name()));
                }
            }
            return true;
        } catch (JSONException e) {
            Timber.e(e);
            return false;
        }
    }

    private void createEventBindings(SQLiteStatement statement, JSONObject jsonObject, Map<String, Integer> columnOrder) throws JSONException {
        String syncStatus = jsonObject.has(client_column.syncStatus.name()) ? jsonObject.getString(client_column.syncStatus.name()) : BaseRepository.TYPE_Synced;
        jsonObject.remove(client_column.syncStatus.name());
        statement.bindString(columnOrder.get(event_column.json.name()), jsonObject.toString());
        statement.bindString(columnOrder.get(event_column.updatedAt.name()), dateFormat.format(new Date()));
        statement.bindString(columnOrder.get(event_column.syncStatus.name()), syncStatus);
        statement.bindString(columnOrder.get(event_column.validationStatus.name()), BaseRepository.TYPE_Valid);
        statement.bindString(columnOrder.get(event_column.baseEntityId.name()), jsonObject.getString(event_column.baseEntityId.name()));
        statement.bindString(columnOrder.get(event_column.locationId.name()), jsonObject.optString(event_column.locationId.name()));
        if (jsonObject.has(EVENT_ID))
            statement.bindString(columnOrder.get(event_column.eventId.name()), jsonObject.getString(EVENT_ID));
        else if (jsonObject.has(_ID))
            statement.bindString(columnOrder.get(event_column.eventId.name()), jsonObject.getString(_ID));
        JSONObject details = jsonObject.optJSONObject(AllConstants.DETAILS);
        if (details != null) {
            bindString(statement, columnOrder.get(event_column.planId.name()), details.optString(AllConstants.PLAN_IDENTIFIER));
            bindString(statement, columnOrder.get(event_column.taskId.name()), details.optString(AllConstants.TASK_IDENTIFIER));
        }
    }

    private void createClientBindings(SQLiteStatement statement, JSONObject jsonObject, Map<String, Integer> columnOrder) throws JSONException {
        String syncStatus = jsonObject.has(client_column.syncStatus.name()) ? jsonObject.getString(client_column.syncStatus.name()) : BaseRepository.TYPE_Synced;
        jsonObject.remove(client_column.syncStatus.name());
        statement.bindString(columnOrder.get(client_column.json.name()), jsonObject.toString());
        statement.bindString(columnOrder.get(client_column.updatedAt.name()), dateFormat.format(new Date()));
        statement.bindString(columnOrder.get(client_column.syncStatus.name()), syncStatus);
        statement.bindString(columnOrder.get(client_column.validationStatus.name()), BaseRepository.TYPE_Valid);
        statement.bindString(columnOrder.get(client_column.baseEntityId.name()), jsonObject.getString(client_column.baseEntityId.name()));

        bindString(statement, columnOrder.get(client_column.locationId.name()), jsonObject.optString(AllConstants.LOCATION_ID));
        bindString(statement, columnOrder.get(client_column.clientType.name()), jsonObject.optString(AllConstants.CLIENT_TYPE));
        JSONObject attributes = jsonObject.optJSONObject(AllConstants.ATTRIBUTES);
        if (attributes != null) {
            bindString(statement, columnOrder.get(client_column.residence.name()), attributes.optString(AllConstants.RESIDENCE));
        }
    }


    private void bindString(SQLiteStatement statement, int index, String value) {
        if (StringUtils.isNotBlank(value))
            statement.bindString(index, value);
        else
            statement.bindNull(index);
    }

    private QueryWrapper generateInsertQuery(Table table) {

        QueryWrapper queryWrapper = new QueryWrapper();
        Map<String, Integer> columnOrder = new HashMap();

        StringBuilder queryBuilder = new StringBuilder("INSERT  INTO ");
        queryBuilder.append(table.name());
        queryBuilder.append(" (");
        StringBuilder params = new StringBuilder(" VALUES( ");

        for (int i = 0; i < table.columns().length; i++) {

            queryBuilder.append(table.columns()[i].name() + ",");
            params.append("?,");
            columnOrder.put(table.columns()[i].name(), i + 1);
        }

        queryBuilder.setLength(queryBuilder.length() - 1);
        params.setLength(params.length() - 1);
        queryBuilder.append(")");
        queryBuilder.append(params);
        queryBuilder.append(")");

        queryWrapper.sqlQuery = queryBuilder.toString();
        queryWrapper.columnOrder = columnOrder;

        return queryWrapper;
    }

    private QueryWrapper generateUpdateQuery(Table table) {
        QueryWrapper queryWrapper = new QueryWrapper();
        Map<String, Integer> columnOrder = new HashMap();

        Column filterColumn;

        if (table.equals(clientTable))
            filterColumn = client_column.baseEntityId;
        else if (table.equals(eventTable))
            filterColumn = event_column.formSubmissionId;
        else return null;
        StringBuilder queryBuilder = new StringBuilder("UPDATE ");
        queryBuilder.append(table.name());
        queryBuilder.append(" SET ");

        for (int i = 0; i < table.columns().length; i++) {
            if (table.columns()[i].equals(filterColumn))
                continue;
            queryBuilder.append(table.columns()[i].name() + "=?,");
            columnOrder.put(table.columns()[i].name(), columnOrder.size() + 1);
        }

        // Add the rowid column
        queryBuilder.append(ROWID + "=?");
        columnOrder.put(ROWID, columnOrder.size() + 1);

        queryBuilder.append(" WHERE ");
        queryBuilder.append(filterColumn.name() + "=?");
        columnOrder.put(filterColumn.name(), columnOrder.size() + 1);

        queryWrapper.sqlQuery = queryBuilder.toString();
        queryWrapper.columnOrder = columnOrder;

        return queryWrapper;
    }

    public boolean batchInsertClients(JSONArray array) {
        return batchInsertClients(array, getWritableDatabase());
    }

    public boolean batchInsertClients(JSONArray array, SQLiteDatabase sqLiteDatabase) {
        if (array == null || array.length() == 0) {
            return false;
        }
        SQLiteStatement insertStatement = null;
        SQLiteStatement updateStatement = null;
        try {
            sqLiteDatabase.beginTransaction();

            int maxRowId = 0;
            QueryWrapper insertQueryWrapper = generateInsertQuery(clientTable);

            QueryWrapper updateQueryWrapper = generateUpdateQuery(clientTable);

            insertStatement = sqLiteDatabase.compileStatement(insertQueryWrapper.sqlQuery);

            updateStatement = sqLiteDatabase.compileStatement(updateQueryWrapper.sqlQuery);
            Set<ClientRelationship> clientRelationships = new HashSet<>();

            for (int i = 0; i < array.length(); i++) {
                try {
                    if (array.isNull(i)) {
                        continue;
                    }
                    JSONObject jsonObject = array.getJSONObject(i);
                    String baseEntityId = jsonObject.getString(client_column.baseEntityId.name());

                    if (maxRowId == 0) {
                        maxRowId = getMaxRowId(clientTable, sqLiteDatabase);
                    }

                    maxRowId++;
                    if (checkIfExists(clientTable, baseEntityId, sqLiteDatabase)) {
                        if (populateStatement(updateStatement, clientTable, jsonObject, updateQueryWrapper.columnOrder)) {
                            updateStatement.bindLong(updateQueryWrapper.columnOrder.get(ROWID), (long) maxRowId);
                            updateStatement.executeUpdateDelete();
                            clientRelationships.add(getClientRelationShip(baseEntityId, jsonObject));
                        } else {
                            Timber.w("Unable to update client with baseEntityId: %s", baseEntityId);
                        }

                    } else {
                        if (populateStatement(insertStatement, clientTable, jsonObject, insertQueryWrapper.columnOrder)) {
                            insertStatement.executeInsert();
                            clientRelationships.add(getClientRelationShip(baseEntityId, jsonObject));
                        } else
                            Timber.w("Unable to add client with baseEntityId: %s", baseEntityId);
                    }
                } catch (JSONException e) {
                    Timber.e(e, "JSONException");
                }
            }
            if (CoreLibrary.getInstance().getSyncConfiguration().runPlanEvaluationOnClientProcessing()) {
                CoreLibrary.getInstance().context().getClientRelationshipRepository().saveRelationship(clientRelationships.toArray(new ClientRelationship[0]));
            }
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
            return true;
        } catch (Exception e) {
            if (sqLiteDatabase.isDbLockedByCurrentThread())
                sqLiteDatabase.endTransaction();
            Timber.e(e);
            return false;
        } finally {
            if (insertStatement != null)
                insertStatement.close();
            if (updateStatement != null)
                updateStatement.close();
        }
    }

    public int getMaxRowId(@NonNull Table table) {
        return getMaxRowId(table, getWritableDatabase());
    }

    public int getMaxRowId(@NonNull Table table, SQLiteDatabase sqLiteDatabase) {
        Cursor cursor = null;
        int rowId = 0;
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT max(" + ROWID + ") AS max_row_id FROM " + table.name(), null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    rowId = cursor.getInt(cursor.getColumnIndex("max_row_id"));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return rowId;
    }

    public boolean batchInsertEvents(JSONArray array, long serverVersion) {
        return batchInsertEvents(array, serverVersion, getWritableDatabase());
    }

    public boolean batchInsertEvents(JSONArray array, long serverVersion, SQLiteDatabase
            sqLiteDatabase) {
        if (array == null || array.length() == 0) {
            return false;
        }

        SQLiteStatement insertStatement = null;
        SQLiteStatement updateStatement = null;

        List<String> formSubmissionIdsList = getFormSubmissionIdsFromJsonArray(array);

        Set<String> formSubmissionIds = new HashSet<>();

        populateFormSubmissionIds(formSubmissionIdsList, formSubmissionIds);

        try {

            sqLiteDatabase.beginTransaction();
            int maxRowId = 0;

            QueryWrapper insertQueryWrapper = generateInsertQuery(eventTable);

            QueryWrapper updateQueryWrapper = generateUpdateQuery(eventTable);

            insertStatement = sqLiteDatabase.compileStatement(insertQueryWrapper.sqlQuery);

            updateStatement = sqLiteDatabase.compileStatement(updateQueryWrapper.sqlQuery);
            for (int i = 0; i < array.length(); i++) {
                if (array.isNull(i)) {
                    continue;
                }
                JSONObject jsonObject = array.getJSONObject(i);
                String formSubmissionId = jsonObject.getString(event_column.formSubmissionId.name());

                if (maxRowId == 0) {
                    maxRowId = getMaxRowId(eventTable, sqLiteDatabase);
                }

                maxRowId++;
                if (formSubmissionIds.contains(formSubmissionId)) {
                    if (populateStatement(updateStatement, eventTable, jsonObject, updateQueryWrapper.columnOrder)) {
                        updateStatement.bindLong(updateQueryWrapper.columnOrder.get(ROWID), (long) maxRowId);
                        updateStatement.executeUpdateDelete();
                    } else {
                        Timber.w("Unable to update event with formSubmissionId: %s ", formSubmissionId);
                    }
                } else {
                    if (populateStatement(insertStatement, eventTable, jsonObject, insertQueryWrapper.columnOrder))
                        insertStatement.executeInsert();
                    else
                        Timber.w("Unable to update event with formSubmissionId: %s", formSubmissionId);
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
            sqLiteDatabase.endTransaction();
            return true;
        } catch (Exception e) {
            Timber.e(e);
            sqLiteDatabase.endTransaction();
            return false;
        } finally {
            if (insertStatement != null)
                insertStatement.close();
            if (updateStatement != null)
                updateStatement.close();
        }
    }

    public <T> T convert(JSONObject jo, Class<T> t) {
        if (jo == null) {
            return null;
        }
        return convert(jo.toString(), t);
    }

    public <T> T convert(String jsonString, Class<T> t) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return JsonFormUtils.gson.fromJson(jsonString, t);
        } catch (Exception e) {
            Timber.e(e, "Unable to convert: %s", jsonString);
            return null;
        }
    }

    public JSONObject convertToJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return new JSONObject(JsonFormUtils.gson.toJson(object));
        } catch (Exception e) {
            Timber.e(e, "Unable to convert to json %s ", object);
            return null;
        }
    }

    public Pair<Long, Long> getMinMaxServerVersions(JSONObject jsonObject) {
        final String EVENTS = AllConstants.KEY.EVENTS;
        try {
            if (jsonObject != null && jsonObject.has(EVENTS)) {
                JSONArray events = jsonObject.getJSONArray(EVENTS);
                Type listType = new TypeToken<List<Event>>() {
                }.getType();
                List<Event> eventList = JsonFormUtils.gson.fromJson(events.toString(), listType);

                long maxServerVersion = Long.MIN_VALUE;
                long minServerVersion = Long.MAX_VALUE;

                for (Event event : eventList) {
                    long serverVersion = event.getServerVersion();
                    if (serverVersion > maxServerVersion) {
                        maxServerVersion = serverVersion;
                    }

                    if (serverVersion < minServerVersion) {
                        minServerVersion = serverVersion;
                    }
                }
                return Pair.create(minServerVersion, maxServerVersion);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return Pair.create(0L, 0L);
    }

    public List<JSONObject> getEvents(long startServerVersion, long lastServerVersion) {
        List<JSONObject> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT json FROM "
                            + eventTable.name()
                            + " WHERE "
                            + event_column.serverVersion.name()
                            + " > "
                            + startServerVersion
                            + " AND "
                            + event_column.serverVersion.name()
                            + " <= "
                            + lastServerVersion
                            + " ORDER BY "
                            + event_column.serverVersion.name(),
                    null);
            while (cursor.moveToNext()) {
                String jsonEventStr = cursor.getString(0);

                jsonEventStr = jsonEventStr.replaceAll("'", "");

                JSONObject ev = new JSONObject(jsonEventStr);

                if (ev.has(event_column.baseEntityId.name())) {
                    String baseEntityId = ev.getString(event_column.baseEntityId.name());
                    JSONObject cl = getClient(getWritableDatabase(), baseEntityId);
                    ev.put("client", cl);
                }
                list.add(ev);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<EventClient> fetchEventClientsCore(String query, String[] params) {

        List<EventClient> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(query, params);
            while (cursor.moveToNext()) {
                if (processEventClientCursor(list, cursor)) continue;
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public List<EventClient> fetchEventClients(List<String> formSubmissionIds) {

        return fetchEventClientsCore("SELECT json FROM "
                        + eventTable.name()
                        + " WHERE "
                        + event_column.formSubmissionId.name()
                        + " IN (" + (getPlaceHolders(formSubmissionIds.size())) + ")"
                        + " ORDER BY "
                        + event_column.serverVersion.name(),
                formSubmissionIds.toArray(new String[formSubmissionIds.size()]));
    }

    private String getPlaceHolders(int size) {
        String placeholders = "";

        for (int i = 0; i < size; i++) {
            placeholders += "?";
            if (i != (size - 1)) {
                placeholders += ",";
            }
        }
        return placeholders;
    }

    private boolean processEventClientCursor(List<EventClient> list, Cursor cursor) {
        String jsonEventStr = cursor.getString(0);
        if (StringUtils.isBlank(jsonEventStr)
                || "{}".equals(jsonEventStr)) { // Skip blank/empty json string
            return true;
        }
        jsonEventStr = jsonEventStr.replaceAll("'", "");

        Event event = convert(jsonEventStr, Event.class);

        String baseEntityId = event.getBaseEntityId();
        Client client = fetchClientByBaseEntityId(baseEntityId);

        EventClient eventClient = new EventClient(event, client);
        list.add(eventClient);
        return false;
    }

    public List<EventClient> fetchEventClients(long startServerVersion, long lastServerVersion) {


        return fetchEventClientsCore("SELECT json FROM "
                        + eventTable.name()
                        + " WHERE "
                        + event_column.serverVersion.name()
                        + " > ? AND "
                        + event_column.serverVersion.name()
                        + " <= ?  ORDER BY "
                        + event_column.serverVersion.name(),
                new String[]{String.valueOf(startServerVersion), String.valueOf(lastServerVersion)});
    }

    public P2pProcessRecordsService.EventClientQueryResult fetchEventClientsByRowId(
            long lastProcessedRowId) {
        List<EventClient> list = new ArrayList<>();
        Cursor cursor = null;
        int maxRowId = 0;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT " + ROWID + ",json FROM "
                            + eventTable.name()
                            + " WHERE "
                            + ROWID
                            + " > ?"
                            + "ORDER BY " + ROWID + " ASC LIMIT 100",
                    new Object[]{lastProcessedRowId});
            while (cursor.moveToNext()) {
                String jsonEventStr = cursor.getString(cursor.getColumnIndex("json"));
                int rowId = cursor.getInt(cursor.getColumnIndex(ROWID));

                if (rowId > maxRowId) {
                    maxRowId = rowId;
                }

                if (StringUtils.isBlank(jsonEventStr)
                        || "{}".equals(jsonEventStr)) { // Skip blank/empty json string
                    continue;
                }

                jsonEventStr = jsonEventStr.replaceAll("'", "");

                Event event = convert(jsonEventStr, Event.class);

                String baseEntityId = event.getBaseEntityId();
                Client client = fetchClientByBaseEntityId(baseEntityId);

                EventClient eventClient = new EventClient(event, client);
                list.add(eventClient);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return new P2pProcessRecordsService.EventClientQueryResult(maxRowId, list);
    }

    /**
     * Get a list of events and client for a list of event types
     *
     * @param eventTypes the list of event types
     * @return a list of events and clients
     */
    public List<EventClient> fetchEventClientsByEventTypes(List<String> eventTypes) {

        if (eventTypes == null)
            return null;

        String eventTypeString = TextUtils.join(",", Collections.nCopies(eventTypes.size(), "?"));

        return fetchEventClientsCore(String.format("SELECT json FROM "
                        + eventTable.name()
                        + " WHERE " + event_column.eventType.name() + " IN (%s)  "
                        + " ORDER BY " + event_column.serverVersion.name(), eventTypeString),
                eventTypes.toArray(new String[]{}));

    }

    public List<JSONObject> getEvents(Date lastSyncDate) {

        String lastSyncString = DateUtil.yyyyMMddHHmmss.format(lastSyncDate);

        List<JSONObject> eventAndAlerts = new ArrayList<>();

        String query = "select "
                + event_column.json
                + ","
                + event_column.updatedAt
                + " from "
                + eventTable.name()
                + " where "
                + event_column.updatedAt
                + " > ? and length("
                + event_column.json
                + ")>2 order by "
                + event_column.serverVersion
                + " asc ";
        Cursor cursor = null;

        try {
            cursor = getWritableDatabase().rawQuery(query, new String[]{lastSyncString});
            while (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                // String jsonEventStr = new String(json, "UTF-8");
                if (StringUtils.isBlank(jsonEventStr)
                        || "{}".equals(jsonEventStr)) { // Skip blank/empty json string
                    continue;
                }

                JSONObject jsonObectEventOrAlert = new JSONObject(jsonEventStr);
                String type =
                        jsonObectEventOrAlert.has("type") ? jsonObectEventOrAlert.getString("type")
                                : null;
                if (StringUtils.isBlank(type)) { // Skip blank types
                    continue;
                }

                if (!"Event".equals(type)
                        && !"Action".equals(type)) { // Skip type that isn't Event or Action
                    continue;
                }
                if (jsonObectEventOrAlert.has(event_column.baseEntityId.name())) {
                    String baseEntityId = jsonObectEventOrAlert.getString(event_column
                            .baseEntityId
                            .name());
                    JSONObject cl = getClientByBaseEntityId(baseEntityId);
                    jsonObectEventOrAlert.put("client", cl);
                }

                eventAndAlerts.add(jsonObectEventOrAlert);
                try {
                    lastSyncDate.setTime(DateUtil.yyyyMMddHHmmss.parse(cursor.getString(1))
                            .getTime());
                } catch (ParseException e) {
                    Timber.e(e);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return eventAndAlerts;
    }

    public List<JSONObject> getEvents(Date lastSyncDate, String syncStatus) {

        String lastSyncString = DateUtil.yyyyMMddHHmmss.format(lastSyncDate);

        List<JSONObject> eventAndAlerts = new ArrayList<>();

        String query = "select "
                + event_column.json
                + ","
                + event_column.updatedAt
                + " from "
                + eventTable.name()
                + " where "
                + event_column.syncStatus
                + " = ? and "
                + event_column.updatedAt
                + " > ? and length("
                + event_column.json
                + ")>2 order by "
                + event_column.serverVersion
                + " asc ";
        Cursor cursor = null;

        try {
            cursor = getWritableDatabase().rawQuery(query, new String[]{syncStatus, lastSyncString});
            while (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                // String jsonEventStr = new String(json, "UTF-8");
                if (StringUtils.isBlank(jsonEventStr)
                        || "{}".equals(jsonEventStr)) { // Skip blank/empty json string
                    continue;
                }

                JSONObject jsonObectEventOrAlert = new JSONObject(jsonEventStr);
                String type =
                        jsonObectEventOrAlert.has("type") ? jsonObectEventOrAlert.getString("type")
                                : null;
                if (StringUtils.isBlank(type)) { // Skip blank types
                    continue;
                }

                if (!"Event".equals(type)
                        && !"Action".equals(type)) { // Skip type that isn't Event or Action
                    continue;
                }
                if (jsonObectEventOrAlert.has(event_column.baseEntityId.name())) {
                    String baseEntityId = jsonObectEventOrAlert.getString(event_column
                            .baseEntityId
                            .name());
                    JSONObject cl = getClientByBaseEntityId(baseEntityId);
                    jsonObectEventOrAlert.put("client", cl);
                }

                eventAndAlerts.add(jsonObectEventOrAlert);
                try {
                    lastSyncDate.setTime(DateUtil.yyyyMMddHHmmss.parse(cursor.getString(1))
                            .getTime());
                } catch (ParseException e) {
                    Timber.e(e);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return eventAndAlerts;
    }

    public List<EventClient> fetchEventClients(Date lastSyncDate, String syncStatus) {

        String lastSyncString = DateUtil.yyyyMMddHHmmss.format(lastSyncDate);
        String query = "select "
                + event_column.json
                + ","
                + event_column.updatedAt
                + " from "
                + eventTable.name()
                + " where "
                + event_column.syncStatus
                + " = ? and "
                + event_column.updatedAt
                + " > ? ORDER BY "
                + event_column.serverVersion.name();


        return fetchEventClientsCore(query, new String[]{syncStatus, lastSyncString});
    }

    public Map<String, Object> getUnSyncedEvents(int limit) {
        Map<String, Object> result = new HashMap<>();
        List<JSONObject> clients = new ArrayList<>();
        List<JSONObject> events = new ArrayList<>();

        String query = "select "
                + event_column.json
                + ","
                + event_column.syncStatus
                + " from "
                + eventTable.name()
                + " where "
                + event_column.syncStatus
                + " in (? , ?)  and length("
                + event_column.json
                + ")>2 order by "
                + event_column.updatedAt
                + " asc limit "
                + limit;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(query, new String[]{BaseRepository.TYPE_Unsynced, BaseRepository.TYPE_Unprocessed});

            while (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                if (StringUtils.isBlank(jsonEventStr)
                        || jsonEventStr.equals("{}")) { // Skip blank/empty json string
                    continue;
                }
                jsonEventStr = jsonEventStr.replaceAll("'", "");
                JSONObject jsonObectEvent = new JSONObject(jsonEventStr);
                events.add(jsonObectEvent);
                if (jsonObectEvent.has(event_column.baseEntityId.name())) {
                    String baseEntityId = jsonObectEvent.getString(event_column.baseEntityId.name
                            ());
                    JSONObject cl = getUnSyncedClientByBaseEntityId(baseEntityId);
                    if (cl != null) {
                        clients.add(cl);
                    }
                }

            }

            if (clients.isEmpty() && events.isEmpty()) {
                clients = getUnSyncedClients(limit);
            } else if (!events.isEmpty()) {
                result.put(AllConstants.KEY.EVENTS, events);
            }
            if (!clients.isEmpty()) {
                result.put(AllConstants.KEY.CLIENTS, clients);
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    public int getUnSyncedEventsCount() {
        int count = 0;
        String query = "SELECT count("
                + event_column.json
                + ") FROM "
                + eventTable.name()
                + " WHERE "
                + event_column.syncStatus
                + " IN (?, ?)  AND length("
                + event_column.json
                + ")>2";
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(query, new String[]{BaseRepository.TYPE_Unsynced, BaseRepository.TYPE_Unprocessed});
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Timber.e(e);
            return -1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }

    public List<String> getUnValidatedEventFormSubmissionIds(int limit) {
        List<String> ids = new ArrayList<>();

        final String validateFilter = " where "
                + event_column.syncStatus + " = ? "
                + " AND ( " + event_column.validationStatus + " is NULL or "
                + event_column.validationStatus + " != ? ) ";

        String query = "select "
                + event_column.formSubmissionId
                + " from "
                + eventTable.name()
                + validateFilter
                + ORDER_BY
                + event_column.updatedAt
                + " asc limit "
                + limit;

        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(query, new String[]{BaseRepository.TYPE_Synced, BaseRepository.TYPE_Valid});
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(0);
                    ids.add(id);

                    cursor.moveToNext();
                }
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

    public List<String> getUnValidatedClientBaseEntityIds(int limit) {
        List<String> ids = new ArrayList<>();

        final String validateFilter = " where "
                + client_column.syncStatus + " = ? "
                + " AND ( " + client_column.validationStatus + " is NULL or "
                + client_column.validationStatus + " != ? ) ";

        String query = "select "
                + client_column.baseEntityId
                + " from "
                + clientTable.name()
                + validateFilter
                + ORDER_BY
                + client_column.updatedAt
                + " asc limit "
                + limit;

        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(query, new String[]{BaseRepository.TYPE_Synced, BaseRepository.TYPE_Valid});
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(0);
                    ids.add(id);

                    cursor.moveToNext();
                }
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

    public void markAllAsUnSynced() {

        String events = "select "
                + event_column.baseEntityId
                + ","
                + event_column.syncStatus
                + " from "
                + eventTable.name();
        String clients = "select "
                + client_column.baseEntityId
                + ","
                + client_column.syncStatus
                + " from "
                + clientTable.name();
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(clients, null);
            int maxRowId = getMaxRowId(clientTable);

            while (cursor.moveToNext()) {
                String beid = (cursor.getString(0));
                if (StringUtils.isBlank(beid)
                        || "{}".equals(beid)) { // Skip blank/empty json string
                    continue;
                }

                ContentValues values = new ContentValues();
                values.put(client_column.baseEntityId.name(), beid);
                values.put(client_column.syncStatus.name(), BaseRepository.TYPE_Unsynced);
                values.put(ROWID, maxRowId++);

                getWritableDatabase().update(clientTable.name(),
                        values,
                        client_column.baseEntityId.name() + " = ?",
                        new String[]{beid});

            }

            cursor.close();
            cursor = getWritableDatabase().rawQuery(events, null);

            maxRowId = getMaxRowId(eventTable);

            while (cursor.moveToNext()) {
                String beid = (cursor.getString(0));
                if (StringUtils.isBlank(beid)
                        || "{}".equals(beid)) { // Skip blank/empty json string
                    continue;
                }

                ContentValues values = new ContentValues();
                values.put(event_column.baseEntityId.name(), beid);
                values.put(event_column.syncStatus.name(), BaseRepository.TYPE_Unsynced);
                values.put(ROWID, maxRowId++);

                getWritableDatabase().update(eventTable.name(),
                        values,
                        event_column.baseEntityId.name() + " = ?",
                        new String[]{beid});

            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    public JSONObject getClient(SQLiteDatabase db, String baseEntityId) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT json FROM "
                    + clientTable.name()
                    + " WHERE "
                    + client_column.baseEntityId.name()
                    + "= ? ", new String[]{baseEntityId});
            if (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                jsonEventStr = jsonEventStr.replaceAll("'", "");
                JSONObject cl = new JSONObject(jsonEventStr);

                return cl;
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public JSONObject getEventsByBaseEntityId(String baseEntityId) {
        JSONObject events = new JSONObject();
        JSONArray list = new JSONArray();
        if (StringUtils.isBlank(baseEntityId)) {
            return events;
        }

        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT json FROM "
                    + eventTable.name()
                    + " WHERE "
                    + event_column.baseEntityId.name()
                    + "= ? ", new String[]{baseEntityId});
            while (cursor.moveToNext()) {
                String jsonEventStr = cursor.getString(0);

                jsonEventStr = jsonEventStr.replaceAll("'", "");

                JSONObject ev = new JSONObject(jsonEventStr);


                list.put(ev);
            }
            JSONObject cl = getClient(getWritableDatabase(), baseEntityId);
            events.put("client", cl);
            events.put("events", list);

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return events;
    }

    public JSONObject getEventsByEventId(String eventId) {
        if (StringUtils.isBlank(eventId)) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT json FROM "
                    + eventTable.name()
                    + " WHERE "
                    + event_column.eventId.name()
                    + "= ? ", new String[]{eventId});
            if (cursor.moveToNext()) {
                String jsonEventStr = cursor.getString(0);

                jsonEventStr = jsonEventStr.replaceAll("'", "");

                return new JSONObject(jsonEventStr);

            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public List<Event> getEventsByEventIds(Set<String> eventIds) {
        return fetchEvents("SELECT json FROM "
                + eventTable.name()
                + " WHERE "
                + event_column.eventId.name()
                + " IN (" + StringUtils.repeat("?", ",", eventIds.size()) + ")", eventIds.toArray(new String[0]));
    }

    public JSONObject getEventsByFormSubmissionId(String formSubmissionId) {
        if (StringUtils.isBlank(formSubmissionId)) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT json FROM "
                    + eventTable.name()
                    + " WHERE "
                    + event_column.formSubmissionId.name()
                    + "= ? ", new String[]{formSubmissionId});
            if (cursor.moveToNext()) {
                String jsonEventStr = cursor.getString(0);

                jsonEventStr = jsonEventStr.replaceAll("'", "");

                return new JSONObject(jsonEventStr);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public JSONObject getClientByBaseEntityId(String baseEntityId) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT "
                    + client_column.json
                    + " FROM "
                    + clientTable.name()
                    + " WHERE "
                    + client_column.baseEntityId.name()
                    + " = ? ", new String[]{baseEntityId});
            if (cursor.moveToNext()) {
                String jsonString = cursor.getString(0);
                jsonString = jsonString.replaceAll("'", "");
                return new JSONObject(jsonString);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public Client fetchClientByBaseEntityId(String baseEntityId) {
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("SELECT "
                    + client_column.json
                    + " FROM "
                    + clientTable.name()
                    + " WHERE "
                    + client_column.baseEntityId.name()
                    + " = ? ", new String[]{baseEntityId});
            if (cursor.moveToNext()) {
                String jsonString = cursor.getString(0);
                jsonString = jsonString.replaceAll("'", "");
                return convert(jsonString, Client.class);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    public List<Client> fetchClientByBaseEntityIds(Set<String> baseEntityIds) {
        return fetchClients("SELECT "
                + client_column.json
                + " FROM "
                + clientTable.name()
                + " WHERE "
                + client_column.baseEntityId.name()
                + " in  (" + StringUtils.repeat("?", baseEntityIds.size()) + ")", baseEntityIds.toArray(new String[0]));
    }

    public JSONObject getUnSyncedClientByBaseEntityId(String baseEntityId) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT "
                    + client_column.json
                    + " FROM "
                    + clientTable.name()
                    + " WHERE "
                    + client_column.syncStatus.name()
                    + " = ? and "
                    + client_column.baseEntityId.name()
                    + " = ? ", new String[]{BaseRepository.TYPE_Unsynced, baseEntityId});
            if (cursor.moveToNext()) {
                String json = cursor.getString(0);
                json = json.replaceAll("'", "");
                return new JSONObject(json);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    public List<JSONObject> getUnSyncedClients(int limit) {
        List<JSONObject> clients = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT "
                        + client_column.json
                        + " FROM "
                        + clientTable.name()
                        + " WHERE "
                        + client_column.syncStatus.name()
                        + " = ? limit "
                        + limit
                , new String[]{BaseRepository.TYPE_Unsynced})) {
            while (cursor.moveToNext()) {
                String json = cursor.getString(0);
                json = json.replaceAll("'", "");
                clients.add(new JSONObject(json));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return clients;
    }

    public JSONObject getEventsByBaseEntityIdAndEventType(String baseEntityId, String eventType) {
        if (StringUtils.isBlank(baseEntityId)) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT json FROM "
                    + eventTable.name()
                    + " WHERE "
                    + event_column.baseEntityId.name()
                    + "= ? AND " + event_column.eventType.name() + "= ? ", new String[]{baseEntityId, eventType});
            if (cursor.moveToNext()) {
                String jsonEventStr = cursor.getString(0);

                jsonEventStr = jsonEventStr.replaceAll("'", "");

                return new JSONObject(jsonEventStr);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public List<EventClient> getEventsByBaseEntityIdsAndSyncStatus(String syncStatus, List<String> baseEntityIds) {
        List<EventClient> list = new ArrayList<>();
        if (Utils.isEmptyCollection(baseEntityIds))
            return list;
        Cursor cursor = null;
        try {
            int len = baseEntityIds.size();
            String query = String.format("SELECT json FROM "
                            + eventTable.name()
                            + " WHERE " + event_column.baseEntityId.name() + " IN (%s) "
                            + " AND " + event_column.syncStatus.name() + "= ? "
                            + " ORDER BY " + event_column.serverVersion.name(),
                    TextUtils.join(",", Collections.nCopies(len, "?")));
            String[] params = baseEntityIds.toArray(new String[len + 1]);
            params[len] = syncStatus;
            cursor = getReadableDatabase().rawQuery(query, params);

            eventCursorParser(list, cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Returns a list of {@link Event}s wrapped in an {@link EventClient} object.
     *
     * @param baseEntityIds The entity identifiers of the client
     * @param syncStatus    The sync status of the event e.g. BaseRepository.TYPE_Unsynced, BaseRepository.TYPE_Unprocessed
     * @param eventTypes    The type of event
     */
    public List<EventClient> getEvents(@NonNull List<String> baseEntityIds, @NonNull List<String> syncStatus, @NonNull List<String> eventTypes) {
        List<EventClient> list = new ArrayList<>();
        if (Utils.isEmptyCollection(baseEntityIds) || Utils.isEmptyCollection(syncStatus) || Utils.isEmptyCollection(eventTypes))
            return list;

        Cursor cursor = null;
        List<String> paramsList = new ArrayList<>();
        paramsList.addAll(baseEntityIds);
        paramsList.addAll(syncStatus);
        paramsList.addAll(eventTypes);

        try {
            String query = String.format("SELECT json FROM "
                            + eventTable.name()
                            + " WHERE " + event_column.baseEntityId.name() + " IN (%s) "
                            + " AND " + event_column.syncStatus.name() + " IN (%s) "
                            + " AND " + event_column.eventType.name() + " IN (%s) "
                            + " ORDER BY " + event_column.serverVersion.name(),
                    TextUtils.join(",", Collections.nCopies(baseEntityIds.size(), "?")), TextUtils.join(",", Collections.nCopies(syncStatus.size(), "?")), TextUtils.join(",", Collections.nCopies(eventTypes.size(), "?")));
            String[] params = paramsList.toArray(new String[paramsList.size()]);
            cursor = getReadableDatabase().rawQuery(query, params);

            eventCursorParser(list, cursor);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    private void eventCursorParser(List<EventClient> list, Cursor cursor) {
        while (cursor.moveToNext()) {
            String jsonEventStr = cursor.getString(0);

            jsonEventStr = jsonEventStr.replaceAll("'", "");
            Event event = convert(jsonEventStr, Event.class);
            list.add(new EventClient(event));
        }
    }

    /**
     * Fetches {@link Event}s whose rowid > #lastRowId up to the #limit provided. Each jsonObject contains the
     * default properties as the one fetched from the DB with an additional property that holds the {@code syncStatus}
     * and {@code rowid} which are used for peer-to-peer sync.
     *
     * @param lastRowId  the last rowId sent
     * @param locationId optional locationId filter
     * @return JsonData which contains a {@link JSONArray} and the maximum row id in the array
     * of {@link Event}s returned. This enables this method to be called again for the consequent batches
     */
    @Nullable
    public JsonData getEvents(long lastRowId, int limit, @Nullable String locationId) {
        JsonData jsonData = null;
        JSONArray jsonArray = new JSONArray();
        long maxRowId = 0;
        String locationFilter = locationId != null ? String.format(" %s =? AND ", event_column.locationId.name()) : "";

        String query = "SELECT "
                + event_column.json
                + ","
                + event_column.syncStatus
                + ","
                + ROWID
                + " FROM "
                + eventTable.name()
                + " WHERE "
                + locationFilter
                + ROWID
                + " > ? "
                + " ORDER BY " + ROWID + " ASC LIMIT ?";
        Cursor cursor = null;

        try {
            cursor = getWritableDatabase().rawQuery(query, locationId != null ? new Object[]{locationId, lastRowId, limit} : new Object[]{lastRowId, limit});

            while (cursor.moveToNext()) {
                long rowId = cursor.getLong(2);
                JSONObject eventObject = getEventObject(cursor, rowId);
                if (eventObject == null) continue;

                jsonArray.put(eventObject);

                if (rowId > maxRowId) {
                    maxRowId = rowId;
                }

            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (jsonArray.length() > 0) {
                jsonData = new JsonData(jsonArray, maxRowId);
            }
        }

        return jsonData;
    }

    @Nullable
    private JSONObject getEventObject(Cursor cursor, long rowId) throws JSONException {
        String jsonEventStr = (cursor.getString(0));
        String syncStatus = cursor.getString(1);

        if (StringUtils.isBlank(jsonEventStr)
                || jsonEventStr.equals("{}")) { // Skip blank/empty json string
            return null;
        }

        jsonEventStr = jsonEventStr.replaceAll("'", "");

        JSONObject eventObject = new JSONObject(jsonEventStr);
        eventObject.put(event_column.syncStatus.name(), syncStatus);
        eventObject.put(ROWID, rowId);
        return eventObject;
    }

    /**
     * Fetches {@link Client}s whose rowid > #lastRowId up to the #limit provided.
     *
     * @param lastRowId
     * @return JsonData which contains a {@link JSONArray} and the maximum row id in the array
     * of {@link Client}s returned or {@code null} if no records match the conditions or an exception occurred.
     * This enables this method to be called again for the consequent batches
     * <p>
     * Also adds a locationID to the client by selecting the last location id from the events table
     */
    @Nullable
    public JsonData getClientsWithLastLocationID(long lastRowId, int limit) {
        JsonData jsonData = null;
        JSONArray jsonArray = new JSONArray();
        long maxRowId = 0;

        String eventJson = "(select event.json from event where event.baseEntityId = client.baseEntityId \n" +
                " ORDER by event.eventDate desc , event.updatedAt desc , event.dateEdited desc , event.serverVersion desc limit 1) eventJson";

        String query = "SELECT "
                + event_column.json
                + ","
                + event_column.syncStatus
                + ","
                + ROWID
                + ","
                + eventJson
                + " FROM "
                + clientTable.name()
                + " WHERE "
                + ROWID
                + " > ? "
                + " ORDER BY " + ROWID + " ASC LIMIT ?";
        Cursor cursor = null;

        try {
            cursor = getWritableDatabase().rawQuery(query, new Object[]{lastRowId, limit});

            while (cursor.moveToNext()) {
                long rowId = cursor.getLong(2);
                String jsonEventStr = (cursor.getString(3));
                if (StringUtils.isBlank(jsonEventStr)
                        || jsonEventStr.equals("{}")) { // Skip blank/empty json string
                    return null;
                }
                jsonEventStr = jsonEventStr.replaceAll("'", "");
                JSONObject eventJsonObject = new JSONObject(jsonEventStr);

                String locationId = eventJsonObject.getString("locationId");
                JSONObject eventObject = getEventObject(cursor, rowId);
                if (eventObject == null) continue;
                eventObject.put("locationId", locationId);
                jsonArray.put(eventObject);

                if (rowId > maxRowId) {
                    maxRowId = rowId;
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (jsonArray.length() > 0) {
                jsonData = new JsonData(jsonArray, maxRowId);
            }
        }

        return jsonData;
    }

    /**
     * Fetches {@link Client}s whose rowid > #lastRowId up to the #limit provided.
     *
     * @param lastRowId  the last row Id queries
     * @param limit      the number of rows to the pulled
     * @param locationId an optional locationId filter for getting the data
     * @return JsonData which contains a {@link JSONArray} and the maximum row id in the array
     * of {@link Client}s returned or {@code null} if no records match the conditions or an exception occurred.
     * This enables this method to be called again for the consequent batches
     */
    @Nullable
    public JsonData getClients(long lastRowId, int limit, @Nullable String locationId) {
        JsonData jsonData = null;
        JSONArray jsonArray = new JSONArray();
        long maxRowId = 0;

        String locationFilter = locationId != null ? String.format(" %s =? AND ", client_column.locationId.name()) : "";
        String query = "SELECT "
                + client_column.json
                + ","
                + client_column.syncStatus
                + ","
                + ROWID
                + " FROM "
                + clientTable.name()
                + " WHERE "
                + locationFilter
                + ROWID
                + " > ? "
                + " ORDER BY " + ROWID + " ASC LIMIT ?";
        Cursor cursor = null;

        try {
            Object[] params;
            if (locationId == null) {
                params = new Object[]{lastRowId, limit};
            } else {
                params = new Object[]{locationId, lastRowId, limit};
            }
            cursor = getWritableDatabase().rawQuery(query, params);

            while (cursor.moveToNext()) {
                long rowId = cursor.getLong(2);
                JSONObject eventObject = getEventObject(cursor, rowId);
                if (eventObject == null) continue;

                jsonArray.put(eventObject);

                if (rowId > maxRowId) {
                    maxRowId = rowId;
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (jsonArray.length() > 0) {
                jsonData = new JsonData(jsonArray, maxRowId);
            }
        }

        return jsonData;
    }


    public void addorUpdateClient(String baseEntityId, JSONObject jsonObject) {
        addorUpdateClient(baseEntityId, jsonObject, BaseRepository.TYPE_Unsynced);
    }

    private ClientRelationship getClientRelationShip(String baseEntityId, JSONObject jsonObject) {
        try {
            JSONObject relationShips = jsonObject.optJSONObject(AllConstants.RELATIONSHIPS);
            if (relationShips != null) {
                Iterator<String> keys = relationShips.keys();
                while (keys.hasNext()) {
                    String relationshipName = keys.next();
                    JSONArray relationalIds = relationShips.optJSONArray(relationshipName);
                    if (relationalIds != null && relationalIds.length() > 0) {
                        return ClientRelationship.builder()
                                .baseEntityId(baseEntityId)
                                .relationship(relationshipName)
                                .relationalId(relationalIds.optString(0))
                                .build();
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e, "Error saving relationship for %s", baseEntityId);
        }
        return null;
    }

    public void addorUpdateClient(String baseEntityId, JSONObject jsonObject, String syncStatus) {
        try {
            ContentValues values = new ContentValues();
            values.put(client_column.json.name(), jsonObject.toString());
            values.put(client_column.updatedAt.name(), dateFormat.format(new Date()));
            values.put(client_column.syncStatus.name(), syncStatus);
            values.put(client_column.baseEntityId.name(), baseEntityId);

            values.put(client_column.locationId.name(), jsonObject.optString(AllConstants.LOCATION_ID));
            values.put(client_column.clientType.name(), jsonObject.optString(AllConstants.CLIENT_TYPE));
            JSONObject attributes = jsonObject.optJSONObject(AllConstants.ATTRIBUTES);
            if (attributes != null) {
                values.put(client_column.residence.name(), attributes.optString(AllConstants.RESIDENCE));
            }
            populateAdditionalColumns(values, client_column.values(), jsonObject);
            getClientRelationShip(baseEntityId, jsonObject);
            long affected;
            if (checkIfExists(clientTable, baseEntityId)) {
                values.put(ROWID, getMaxRowId(clientTable) + 1);

                affected = getWritableDatabase().update(clientTable.name(),
                        values,
                        client_column.baseEntityId.name() + " = ?",
                        new String[]{baseEntityId});
            } else {
                affected = getWritableDatabase().insert(clientTable.name(), null, values);
            }
            if (affected < 1)
                Timber.e("Client %s not saved: %s", baseEntityId, jsonObject);
            if (CoreLibrary.getInstance().getSyncConfiguration().runPlanEvaluationOnClientProcessing()) {
                CoreLibrary.getInstance().context().getClientRelationshipRepository().saveRelationship(getClientRelationShip(baseEntityId, jsonObject));
            }

        } catch (Exception e) {
            Timber.e(e, "Error saving client %s", jsonObject);
        }
    }

    public void addEvent(String baseEntityId, JSONObject jsonObject) {//Backward compatibility
        addEvent(baseEntityId, jsonObject, BaseRepository.TYPE_Unprocessed);
    }

    public void addEvent(String baseEntityId, JSONObject jsonObject, String syncStatus) {
        try {
            final String EVENT_TYPE = "eventType";
            ContentValues values = new ContentValues();
            values.put(event_column.json.name(), jsonObject.toString());
            values.put(event_column.eventType.name(), jsonObject.has(EVENT_TYPE) ? jsonObject.getString(EVENT_TYPE) : "");
            values.put(event_column.updatedAt.name(), dateFormat.format(new Date()));
            values.put(event_column.baseEntityId.name(), baseEntityId);
            values.put(event_column.syncStatus.name(), syncStatus);
            values.put(event_column.locationId.name(), jsonObject.optString(event_column.locationId.name()));
            JSONObject details = jsonObject.optJSONObject(AllConstants.DETAILS);
            if (details != null) {
                values.put(event_column.planId.name(), details.optString(AllConstants.PLAN_IDENTIFIER));
                values.put(event_column.taskId.name(), details.optString(AllConstants.TASK_IDENTIFIER));
            }
            if (jsonObject.has(EVENT_ID)) {
                values.put(event_column.eventId.name(), jsonObject.getString(EVENT_ID));
            } else if (jsonObject.has(_ID)) {
                values.put(event_column.eventId.name(), jsonObject.getString(_ID));
            }
            populateAdditionalColumns(values, event_column.values(), jsonObject);
            long affected;
            //update existing event if eventid present
            if (jsonObject.has(event_column.formSubmissionId.name())
                    && jsonObject.getString(event_column.formSubmissionId.name()) != null) {
                //sanity check
                if (checkIfExistsByFormSubmissionId(eventTable,
                        jsonObject.getString(event_column
                                .formSubmissionId
                                .name()))) {

                    values.put(ROWID, getMaxRowId(eventTable) + 1);
                    affected = getWritableDatabase().update(eventTable.name(),
                            values,
                            event_column.formSubmissionId.name() + "=?",
                            new String[]{jsonObject.getString(
                                    event_column.formSubmissionId.name())});
                } else {
                    //that odd case
                    values.put(event_column.formSubmissionId.name(),
                            jsonObject.getString(event_column.formSubmissionId.name()));

                    affected = getWritableDatabase().insert(eventTable.name(), null, values);

                }
            } else {
// a case here would be if an event comes from openmrs
                affected = getWritableDatabase().insert(eventTable.name(), null, values);
            }

            if (affected < 1)
                Timber.e("event for %s not created or updated: %s", baseEntityId, jsonObject);

        } catch (Exception e) {
            Timber.e(e, "Error saving event %s", jsonObject);
        }
    }

    /**
     * Flag an event as locally processed.
     * This method only updates locally created and processed events and prevents reprocessing locally
     *
     * @param formSubmissionId
     */
    public void markEventAsProcessed(String formSubmissionId) {
        try {

            ContentValues values = new ContentValues();
            values.put(event_column.syncStatus.name(), BaseRepository.TYPE_Unsynced);
            values.put(ROWID, getMaxRowId(eventTable) + 1);

            getWritableDatabase().update(eventTable.name(),
                    values,
                    event_column.formSubmissionId.name() + " = ? and " + event_column.syncStatus.name() + " = ? ",
                    new String[]{formSubmissionId, BaseRepository.TYPE_Unprocessed});

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void markEventAsSynced(String formSubmissionId) {
        try {

            ContentValues values = new ContentValues();
            values.put(event_column.syncStatus.name(), BaseRepository.TYPE_Synced);
            values.put(ROWID, getMaxRowId(eventTable) + 1);

            getWritableDatabase().update(eventTable.name(),
                    values,
                    event_column.formSubmissionId.name() + " = ?",
                    new String[]{formSubmissionId});

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void markClientAsSynced(String baseEntityId) {
        try {

            ContentValues values = new ContentValues();
            values.put(client_column.baseEntityId.name(), baseEntityId);
            values.put(client_column.syncStatus.name(), BaseRepository.TYPE_Synced);
            values.put(ROWID, getMaxRowId(clientTable) + 1);

            getWritableDatabase().update(clientTable.name(),
                    values,
                    client_column.baseEntityId.name() + " = ?",
                    new String[]{baseEntityId});

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void markEventValidationStatus(String formSubmissionId, boolean valid) {
        try {
            ContentValues values = new ContentValues();
            values.put(event_column.formSubmissionId.name(), formSubmissionId);
            values.put(event_column.validationStatus.name(), valid ? TYPE_Valid : TYPE_InValid);
            if (!valid) {
                values.put(event_column.syncStatus.name(), TYPE_Unsynced);
            }
            values.put(ROWID, getMaxRowId(eventTable) + 1);

            getWritableDatabase().update(eventTable.name(),
                    values,
                    event_column.formSubmissionId.name() + " = ?",
                    new String[]{formSubmissionId});

        } catch (Exception e) {
            Timber.e(e);
        }
    }


    public void markClientValidationStatus(String baseEntityId, boolean valid) {
        try {
            ContentValues values = new ContentValues();
            values.put(client_column.baseEntityId.name(), baseEntityId);
            values.put(client_column.validationStatus.name(), valid ? TYPE_Valid : TYPE_InValid);
            if (!valid) {
                values.put(client_column.syncStatus.name(), TYPE_Unsynced);
            }

            values.put(ROWID, getMaxRowId(clientTable) + 1);

            getWritableDatabase().update(clientTable.name(),
                    values,
                    client_column.baseEntityId.name() + " = ?",
                    new String[]{baseEntityId});

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void markEventAsTaskUnprocessed(String formSubmissionId) {
        try {
            ContentValues values = new ContentValues();
            values.put(client_column.syncStatus.name(), TYPE_Task_Unprocessed);
            values.put(ROWID, getMaxRowId(eventTable) + 1);

            getWritableDatabase().update(eventTable.name(),
                    values,
                    event_column.formSubmissionId.name() + " = ?",
                    new String[]{formSubmissionId});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void markEventsAsSynced(Map<String, Object> syncedEvents) {
        try {
            List<JSONObject> clients =
                    syncedEvents.containsKey(AllConstants.KEY.CLIENTS) ? (List<JSONObject>) syncedEvents.get(
                            AllConstants.KEY.CLIENTS) : null;
            List<JSONObject> events =
                    syncedEvents.containsKey(AllConstants.KEY.EVENTS) ? (List<JSONObject>) syncedEvents.get(
                            AllConstants.KEY.EVENTS) : null;

            if (clients != null && !clients.isEmpty()) {
                for (JSONObject client : clients) {
                    String baseEntityId = client.getString(client_column.baseEntityId.name());
                    markClientAsSynced(baseEntityId);
                }
            }
            if (events != null && !events.isEmpty()) {
                for (JSONObject event : events) {
                    String formSubmissionId = event.getString(event_column.formSubmissionId.name());
                    markEventAsSynced(formSubmissionId);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    protected List<Client> fetchClients(String query, String[] params) {
        Cursor cursor = null;
        List<Client> clients = new ArrayList<>();
        try {
            cursor = getWritableDatabase().rawQuery(query, params);
            while (cursor.moveToNext()) {
                String jsonString = cursor.getString(0);
                jsonString = jsonString.replaceAll("'", "");
                clients.add(convert(jsonString, Client.class));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return clients;
    }

    protected List<Event> fetchEvents(String query, String[] params) {
        Cursor cursor = null;
        List<Event> events = new ArrayList<>();
        try {
            cursor = getWritableDatabase().rawQuery(query, params);
            while (cursor.moveToNext()) {
                String jsonString = cursor.getString(0);
                jsonString = jsonString.replaceAll("'", "");
                events.add(convert(jsonString, Event.class));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return events;
    }

    // Definitions
    public enum Table implements BaseTable {
        client(client_column.values()),
        event(event_column.values()),
        foreignEvent(event_column.values()),
        foreignClient(client_column.values());

        private Column[] columns;

        public Column[] columns() {
            return columns;
        }

        Table(Column[] columns) {
            this.columns = columns;
        }
    }

    public enum client_column implements Column {

        baseEntityId(ColumnAttribute.Type.text, true, true),
        syncStatus(ColumnAttribute.Type.text, false, true),
        validationStatus(ColumnAttribute.Type.text, false, true),
        json(ColumnAttribute.Type.text, false, false),
        updatedAt(ColumnAttribute.Type.date, false, true),
        locationId(ColumnAttribute.Type.text, false, true),
        clientType(ColumnAttribute.Type.text, false, true),
        residence(ColumnAttribute.Type.text, false, true);

        private ColumnAttribute column;

        client_column(ColumnAttribute.Type type, boolean pk, boolean index) {
            this.column = new ColumnAttribute(type, pk, index);
        }

        public ColumnAttribute column() {
            return column;
        }
    }

    public enum event_column implements Column {
        dateCreated(ColumnAttribute.Type.date, false, true),
        dateEdited(ColumnAttribute.Type.date, false, false),

        eventId(ColumnAttribute.Type.text, true, true),
        baseEntityId(ColumnAttribute.Type.text, false, true),
        syncStatus(ColumnAttribute.Type.text, false, true),
        validationStatus(ColumnAttribute.Type.text, false, true),
        json(ColumnAttribute.Type.text, false, false),
        eventDate(ColumnAttribute.Type.date, false, true),
        eventType(ColumnAttribute.Type.text, false, true),
        formSubmissionId(ColumnAttribute.Type.text, false, true),
        updatedAt(ColumnAttribute.Type.date, false, true),
        serverVersion(ColumnAttribute.Type.longnum, false, true),
        planId(ColumnAttribute.Type.text, false, true),
        locationId(ColumnAttribute.Type.text, false, true),
        taskId(ColumnAttribute.Type.text, false, true);

        private ColumnAttribute column;

        event_column(ColumnAttribute.Type type, boolean pk, boolean index) {
            this.column = new ColumnAttribute(type, pk, index);
        }

        public ColumnAttribute column() {
            return column;
        }
    }

    public boolean deleteClient(String baseEntityId) {
        try {
            int rowsAffected = getWritableDatabase().delete(clientTable.name(),
                    client_column.baseEntityId.name()
                            + " = ?",
                    new String[]{baseEntityId});
            if (rowsAffected > 0) {
                return true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return false;
    }

    public boolean deleteEventsByBaseEntityId(String baseEntityId, String eventType) {

        try {
            int rowsAffected = getWritableDatabase().delete(eventTable.name(),
                    event_column.baseEntityId.name()
                            + " = ? AND "
                            + event_column.eventType.name()
                            + " != ?",
                    new String[]{baseEntityId, eventType});
            if (rowsAffected > 0) {
                return true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return false;
    }

    static class QueryWrapper {
        public String sqlQuery;
        public Map<String, Integer> columnOrder;
    }

    public List<Event> getEventsByTaskIds(Set<String> taskIds) {
        return fetchEvents("SELECT json FROM "
                + eventTable.name()
                + " WHERE "
                + event_column.taskId.name()
                + " IN (" + StringUtils.repeat("?", ",", taskIds.size()) + ")", taskIds.toArray(new String[0]));
    }
}
