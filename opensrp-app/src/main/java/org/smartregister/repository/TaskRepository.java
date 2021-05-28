package org.smartregister.repository;

import android.content.ContentValues;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.Client;
import org.smartregister.domain.Location;
import org.smartregister.domain.Note;
import org.smartregister.domain.Period;
import org.smartregister.domain.Task;
import org.smartregister.domain.Task.TaskStatus;
import org.smartregister.domain.TaskUpdate;
import org.smartregister.p2p.sync.data.JsonData;
import org.smartregister.sync.helper.TaskServiceHelper;
import org.smartregister.util.DatabaseMigrationUtils;
import org.smartregister.util.DateUtil;
import org.smartregister.util.P2PUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.AllConstants.DataTypes.INTEGER;
import static org.smartregister.AllConstants.ROWID;
import static org.smartregister.domain.Task.INACTIVE_TASK_STATUS;

/**
 * Created by samuelgithengi on 11/23/18.
 */
public class TaskRepository extends BaseRepository {

    private static final String ID = "_id";
    private static final String PLAN_ID = "plan_id";
    private static final String GROUP_ID = "group_id";
    private static final String STATUS = "status";
    private static final String BUSINESS_STATUS = "business_status";

    private static final String PRIORITY = "priority";
    private static final String CODE = "code";
    private static final String DESCRIPTION = "description";
    private static final String FOCUS = "focus";
    private static final String FOR = "for";

    private static final String START = "start";
    private static final String END = "end";

    private static final String AUTHORED_ON = "authored_on";
    private static final String LAST_MODIFIED = "last_modified";
    private static final String OWNER = "owner";
    private static final String SYNC_STATUS = "sync_status";
    private static final String SERVER_VERSION = "server_version";
    private static final String STRUCTURE_ID = "structure_id";
    private static final String REASON_REFERENCE = "reason_reference";
    private static final String LOCATION = "location";
    private static final String REQUESTER = "requester";
    private static final String RESTRICTION_REPEAT = "restriction_repeat";
    private static final String RESTRICTION_START = "restriction_start";
    private static final String RESTRICTION_END = "restriction_end";

    private final TaskNotesRepository taskNotesRepository;

    protected static final String[] COLUMNS = {ROWID, ID, PLAN_ID, GROUP_ID, STATUS, BUSINESS_STATUS, PRIORITY, CODE, DESCRIPTION, FOCUS, FOR, START, END, AUTHORED_ON, LAST_MODIFIED, OWNER, SYNC_STATUS, SERVER_VERSION, STRUCTURE_ID, REASON_REFERENCE, LOCATION, REQUESTER, RESTRICTION_REPEAT, RESTRICTION_START, RESTRICTION_END};

    protected static final String TASK_TABLE = "task";

    private static final String CREATE_TASK_TABLE =
            "CREATE TABLE " + TASK_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    PLAN_ID + " VARCHAR NOT NULL, " +
                    GROUP_ID + " VARCHAR NOT NULL, " +
                    STATUS + " VARCHAR  NOT NULL, " +
                    BUSINESS_STATUS + " VARCHAR,  " +
                    PRIORITY + " VARCHAR,  " +
                    CODE + " VARCHAR , " +
                    DESCRIPTION + " VARCHAR , " +
                    FOCUS + " VARCHAR , " +
                    FOR + " VARCHAR NOT NULL, " +
                    START + " INTEGER , " +
                    END + " INTEGER , " +
                    AUTHORED_ON + " INTEGER NOT NULL, " +
                    LAST_MODIFIED + " INTEGER NOT NULL, " +
                    OWNER + " VARCHAR NOT NULL, " +
                    SYNC_STATUS + " VARCHAR DEFAULT " + BaseRepository.TYPE_Synced + ", " +
                    SERVER_VERSION + " INTEGER, " +
                    STRUCTURE_ID + " VARCHAR, " +
                    REASON_REFERENCE + " VARCHAR, " +
                    LOCATION + " VARCHAR, " +
                    REQUESTER + " VARCHAR,  " +
                    RESTRICTION_REPEAT + " INTEGER,  " +
                    RESTRICTION_START + " INTEGER,  " +
                    RESTRICTION_END + " INTEGER )";

    private static final String CREATE_TASK_PLAN_GROUP_INDEX = "CREATE INDEX "
            + TASK_TABLE + "_plan_group_ind  ON " + TASK_TABLE + "(" + PLAN_ID + "," + GROUP_ID + "," + SYNC_STATUS + ")";

    public TaskRepository(TaskNotesRepository taskNotesRepository) {
        this.taskNotesRepository = taskNotesRepository;
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TASK_TABLE);
        database.execSQL(CREATE_TASK_PLAN_GROUP_INDEX);
    }

    /**
     * Migrate the older database by add restriction columns and changing of priority to enum
     *
     * @param database the database being upgraded
     */
    public static void updatePriorityToEnumAndAddRestrictions(@NonNull SQLiteDatabase database) {
        DatabaseMigrationUtils.addColumnIfNotExists(database, TASK_TABLE, RESTRICTION_REPEAT, INTEGER);
        DatabaseMigrationUtils.addColumnIfNotExists(database, TASK_TABLE, RESTRICTION_START, INTEGER);
        DatabaseMigrationUtils.addColumnIfNotExists(database, TASK_TABLE, RESTRICTION_END, INTEGER);
        DatabaseMigrationUtils.recreateSyncTableWithExistingColumnsOnly(database, TASK_TABLE, COLUMNS, CREATE_TASK_TABLE);
        database.execSQL(String.format("UPDATE %s SET %s=?", TASK_TABLE, PRIORITY), new Object[]{Task.TaskPriority.ROUTINE.name()});
    }

    public void addOrUpdate(Task task) {
        addOrUpdate(task, false);
    }

    public Task addOrUpdate(Task task, boolean updateOnly) {
        if (StringUtils.isBlank(task.getIdentifier())) {
            throw new IllegalArgumentException("identifier must be specified");
        }
        ContentValues contentValues = new ContentValues();

        Task existingTask = getTaskByIdentifier(task.getIdentifier());
        if (existingTask != null) {
            if (existingTask.getLastModified().isAfter(task.getLastModified())) {
                return task;
            }
            int maxRowId = P2PUtil.getMaxRowId(TASK_TABLE, getWritableDatabase());
            contentValues.put(ROWID, ++maxRowId);
        }

        contentValues.put(ID, task.getIdentifier());
        contentValues.put(PLAN_ID, task.getPlanIdentifier());
        contentValues.put(GROUP_ID, task.getGroupIdentifier());
        if (task.getStatus() != null) {
            contentValues.put(STATUS, task.getStatus().name());
        }
        contentValues.put(BUSINESS_STATUS, task.getBusinessStatus());
        if (task.getPriority() != null) {
            contentValues.put(PRIORITY, task.getPriority().name());
        } else {
            contentValues.put(PRIORITY, Task.TaskPriority.ROUTINE.name());
        }
        contentValues.put(CODE, task.getCode());
        contentValues.put(DESCRIPTION, task.getDescription());
        contentValues.put(FOCUS, task.getFocus());
        contentValues.put(FOR, task.getForEntity());
        if (task.getExecutionPeriod() != null) {
            contentValues.put(START, DateUtil.getMillis(task.getExecutionPeriod().getStart()));
            contentValues.put(END, DateUtil.getMillis(task.getExecutionPeriod().getEnd()));
        }
        contentValues.put(AUTHORED_ON, DateUtil.getMillis(task.getAuthoredOn()));
        contentValues.put(LAST_MODIFIED, DateUtil.getMillis(task.getLastModified()));
        contentValues.put(OWNER, task.getOwner());
        contentValues.put(SERVER_VERSION, task.getServerVersion());
        contentValues.put(SYNC_STATUS, task.getSyncStatus());
        contentValues.put(STRUCTURE_ID, task.getStructureId());
        contentValues.put(REASON_REFERENCE, task.getReasonReference());
        contentValues.put(LOCATION, task.getLocation());
        contentValues.put(REQUESTER, task.getRequester());
        if (task.getRestriction() != null) {
            contentValues.put(RESTRICTION_REPEAT, task.getRestriction().getRepetitions());
            if (task.getRestriction().getPeriod() != null) {
                contentValues.put(RESTRICTION_START, DateUtil.getMillis(task.getRestriction().getPeriod().getStart()));
                contentValues.put(RESTRICTION_END, DateUtil.getMillis(task.getRestriction().getPeriod().getEnd()));
            }
        }

        if (updateOnly) {
            getWritableDatabase().update(TASK_TABLE, contentValues, ID + " =?", new String[]{task.getIdentifier()});
        } else {
            getWritableDatabase().replace(TASK_TABLE, null, contentValues);
        }

        if (task.getNotes() != null) {
            for (Note note : task.getNotes())
                taskNotesRepository.addOrUpdate(note, task.getIdentifier());
        }

        return task;
    }

    public Map<String, Set<Task>> getTasksByPlanAndGroup(String planId, String groupId) {
        Cursor cursor = null;
        Map<String, Set<Task>> tasks = new HashMap<>();
        try {
            String[] params = new String[]{planId, groupId};
            cursor = getReadableDatabase().rawQuery(String.format("SELECT * FROM %s WHERE %s=? AND %s =? AND %s NOT IN (%s)",
                    TASK_TABLE, PLAN_ID, GROUP_ID, STATUS,
                    TextUtils.join(",", Collections.nCopies(INACTIVE_TASK_STATUS.length, "?"))),
                    ArrayUtils.addAll(params, INACTIVE_TASK_STATUS));
            while (cursor.moveToNext()) {
                Set<Task> taskSet;
                Task task = readCursor(cursor);
                if (tasks.containsKey(task.getStructureId()))
                    taskSet = tasks.get(task.getStructureId());
                else
                    taskSet = new HashSet<>();
                taskSet.add(task);
                tasks.put(task.getStructureId(), taskSet);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return tasks;
    }

    /**
     * Accepts a stream of tasks
     *
     * @param planId
     * @param groupId
     * @param consumer
     */
    public void readTasks(String planId, String groupId, String code, Consumer<Task> consumer) {
        Cursor cursor = null;
        try {
            String[] params = new String[]{planId, groupId, code};
            cursor = getReadableDatabase().rawQuery(String.format("SELECT * FROM %s WHERE %s=? AND %s =? AND %s =? AND %s NOT IN (%s)",
                    TASK_TABLE, PLAN_ID, GROUP_ID, CODE, STATUS,
                    TextUtils.join(",", Collections.nCopies(INACTIVE_TASK_STATUS.length, "?"))),
                    ArrayUtils.addAll(params, INACTIVE_TASK_STATUS));
            while (cursor.moveToNext()) {
                Task task = readCursor(cursor);
                consumer.accept(task);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }



    public Task getTaskByIdentifier(String identifier) {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TASK_TABLE +
                " WHERE " + ID + " =?", new String[]{identifier})) {
            if (cursor.moveToFirst()) {
                return readCursor(cursor);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private Set<Task> getTasks(String query, String[] params) {
        Cursor cursor = null;
        Set<Task> taskSet = new HashSet<>();
        try {
            cursor = getReadableDatabase().rawQuery(query, params);
            while (cursor.moveToNext()) {
                Task task = readCursor(cursor);
                taskSet.add(task);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return taskSet;
    }

    public Set<Task> getTasksByEntityAndCode(String planId, String groupId, String forEntity, String code) {
        return getTasks(String.format("SELECT * FROM %s WHERE %s=? AND %s =? AND %s =?  AND %s =? AND %s  NOT IN (%s)",
                TASK_TABLE, PLAN_ID, GROUP_ID, FOR, CODE, STATUS,
                TextUtils.join(",", Collections.nCopies(INACTIVE_TASK_STATUS.length, "?")))
                , ArrayUtils.addAll(new String[]{planId, groupId, forEntity, code}, INACTIVE_TASK_STATUS));
    }

    public Set<Task> getTasksByPlanAndEntity(String planId, String forEntity) {
        return getTasks(String.format("SELECT * FROM %s WHERE %s=? AND %s =? AND %s  NOT IN (%s)",
                TASK_TABLE, PLAN_ID, FOR, STATUS,
                TextUtils.join(",", Collections.nCopies(INACTIVE_TASK_STATUS.length, "?")))
                , ArrayUtils.addAll(new String[]{planId, forEntity}, INACTIVE_TASK_STATUS));
    }

    public Set<Task> getTasksByEntity(String forEntity) {
        return getTasks(String.format("SELECT * FROM %s WHERE %s =? AND %s  NOT IN (%s)",
                TASK_TABLE, FOR, STATUS,
                TextUtils.join(",", Collections.nCopies(INACTIVE_TASK_STATUS.length, "?")))
                , ArrayUtils.addAll(new String[]{forEntity}, INACTIVE_TASK_STATUS));
    }

    /**
     * Gets tasks for an entity using plan and task status
     *
     * @param planId     the plan id
     * @param forEntity  the entity id
     * @param taskStatus the task status {@link TaskStatus }
     * @return the set of tasks matching the above params
     */
    public Set<Task> getTasksByEntityAndStatus(String planId, String forEntity, TaskStatus taskStatus) {

        return getTasks(String.format("SELECT * FROM %s WHERE %s=? AND %s =? AND %s = ? ",
                TASK_TABLE, PLAN_ID, STATUS, FOR), new String[]{planId, taskStatus.name(), forEntity});
    }

    //Do not make private - used in deriving classes
    public Task readCursor(Cursor cursor) {
        Task task = new Task();
        task.setIdentifier(cursor.getString(cursor.getColumnIndex(ID)));
        task.setPlanIdentifier(cursor.getString(cursor.getColumnIndex(PLAN_ID)));
        task.setGroupIdentifier(cursor.getString(cursor.getColumnIndex(GROUP_ID)));
        if (cursor.getString(cursor.getColumnIndex(STATUS)) != null) {
            task.setStatus(TaskStatus.valueOf(cursor.getString(cursor.getColumnIndex(STATUS))));
        }
        task.setBusinessStatus(cursor.getString(cursor.getColumnIndex(BUSINESS_STATUS)));
        task.setPriority(Task.TaskPriority.valueOf(cursor.getString(cursor.getColumnIndex(PRIORITY))));
        task.setCode(cursor.getString(cursor.getColumnIndex(CODE)));
        task.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
        task.setFocus(cursor.getString(cursor.getColumnIndex(FOCUS)));
        task.setForEntity(cursor.getString(cursor.getColumnIndex(FOR)));
        Period period = new Period();
        period.setStart(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(START))));
        period.setEnd(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(END))));
        task.setExecutionPeriod(period);
        task.setAuthoredOn(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(AUTHORED_ON))));
        task.setLastModified(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(LAST_MODIFIED))));
        task.setOwner(cursor.getString(cursor.getColumnIndex(OWNER)));
        task.setSyncStatus(cursor.getString(cursor.getColumnIndex(SYNC_STATUS)));
        task.setServerVersion(cursor.getLong(cursor.getColumnIndex(SERVER_VERSION)));
        task.setStructureId(cursor.getString(cursor.getColumnIndex(STRUCTURE_ID)));
        task.setReasonReference(cursor.getString(cursor.getColumnIndex(REASON_REFERENCE)));
        task.setLocation(cursor.getString(cursor.getColumnIndex(LOCATION)));
        task.setRequester(cursor.getString(cursor.getColumnIndex(REQUESTER)));
        Period restrictionPeriod = new Period();
        restrictionPeriod.setStart(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(RESTRICTION_START))));
        restrictionPeriod.setEnd(DateUtil.getDateTimeFromMillis(cursor.getLong(cursor.getColumnIndex(RESTRICTION_END))));
        Task.Restriction restriction = new Task.Restriction(cursor.getInt(cursor.getColumnIndex(RESTRICTION_REPEAT)), restrictionPeriod);
        if (restriction.getRepetitions() != 0 || restrictionPeriod.getStart() != null && restrictionPeriod.getEnd() != null) {
            task.setRestriction(restriction);
        }
        return task;
    }

    public List<TaskUpdate> getUnSyncedTaskStatus() {
        Cursor cursor = null;
        List<TaskUpdate> taskUpdates = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(String.format("SELECT " + ID + "," + STATUS + "," + BUSINESS_STATUS + "," + SERVER_VERSION + "  FROM %s WHERE %s =?", TASK_TABLE, SYNC_STATUS), new String[]{BaseRepository.TYPE_Unsynced});
            while (cursor.moveToNext()) {
                taskUpdates.add(readUpdateCursor(cursor));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return taskUpdates;
    }

    public void markTaskAsSynced(String taskID) {
        try {
            ContentValues values = new ContentValues();
            values.put(TaskRepository.ID, taskID);
            values.put(TaskRepository.SYNC_STATUS, BaseRepository.TYPE_Synced);
            values.put(TaskRepository.SERVER_VERSION, 0);

            getWritableDatabase().update(TaskRepository.TASK_TABLE, values, TaskRepository.ID + " = ?",
                    new String[]{taskID});
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    //Do not make private - used in deriving classes
    protected TaskUpdate readUpdateCursor(Cursor cursor) {
        TaskUpdate taskUpdate = new TaskUpdate();
        taskUpdate.setIdentifier(cursor.getString(cursor.getColumnIndex(ID)));

        if (cursor.getString(cursor.getColumnIndex(STATUS)) != null) {
            taskUpdate.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));
        }
        if (cursor.getString(cursor.getColumnIndex(BUSINESS_STATUS)) != null) {
            taskUpdate.setBusinessStatus(cursor.getString(cursor.getColumnIndex(BUSINESS_STATUS)));
        }
        if (cursor.getString(cursor.getColumnIndex(SERVER_VERSION)) != null) {
            taskUpdate.setServerVersion(cursor.getString(cursor.getColumnIndex(SERVER_VERSION)));
        }
        return taskUpdate;
    }

    public List<Task> getAllUnsynchedCreatedTasks() {
        return new ArrayList<>(getTasks(String.format("SELECT *  FROM %s WHERE %s =? OR %s IS NULL OR %s = 0", TASK_TABLE, SYNC_STATUS, SERVER_VERSION, SERVER_VERSION), new String[]{BaseRepository.TYPE_Created}));
    }

    /**
     * This method updates the task structureId field with a value from  the
     * residence attribute field of a given client
     * <p>
     * This is only done for tasks whose for field refers to a client's residence attribute field
     *
     * @param clients A list of clients
     * @return bolean indicating whether the update was successful or not
     */
    public boolean updateTaskStructureIdFromClient(List<Client> clients, String attribute) {
        if (clients == null || clients.isEmpty()) {
            return false;
        }

        SQLiteStatement updateStatement = null;
        try {
            getWritableDatabase().beginTransaction();

            String updateTaskSructureIdQuery = String.format("UPDATE %s  SET %s = ? WHERE %s = ?  AND %s IS NULL",
                    TASK_TABLE, STRUCTURE_ID, FOR, STRUCTURE_ID);
            updateStatement = getWritableDatabase().compileStatement(updateTaskSructureIdQuery);

            for (Client client : clients) {
                String taskFor = client.getBaseEntityId();
                if (client.getAttribute(attribute) == null) {
                    continue;
                }
                String structureId = client.getAttribute(attribute).toString();

                updateStatement.bindString(1, structureId);
                updateStatement.bindString(2, taskFor);
                updateStatement.executeUpdateDelete();

            }

            getWritableDatabase().setTransactionSuccessful();
            getWritableDatabase().endTransaction();
            return true;
        } catch (SQLException e) {
            Timber.e(e);
            getWritableDatabase().endTransaction();
            return false;
        } finally {
            if (updateStatement != null)
                updateStatement.close();
        }
    }

    /**
     * This method updates the task structureId field with a value from  the
     * location's id field of a given structure
     * <p>
     * This is only done for tasks whose for field refers to a structure's  id field
     *
     * @param locations A list of locations (structures)
     * @return bolean indicating whether the update was successful or not
     */
    public boolean updateTaskStructureIdFromStructure(List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return false;
        }

        SQLiteStatement updateStatement = null;
        try {
            getWritableDatabase().beginTransaction();
            String updateTaskSructureIdQuery = String.format("UPDATE %s  SET %s = ? WHERE %s = ?   AND %s IS NULL",
                    TASK_TABLE, STRUCTURE_ID, FOR, STRUCTURE_ID);
            updateStatement = getWritableDatabase().compileStatement(updateTaskSructureIdQuery);

            for (Location location : locations) {

                updateStatement.bindString(1, location.getId());
                updateStatement.bindString(2, location.getId());
                updateStatement.executeUpdateDelete();
            }

            getWritableDatabase().setTransactionSuccessful();
            getWritableDatabase().endTransaction();
            return true;

        } catch (SQLException e) {
            Timber.e(e);
            getWritableDatabase().endTransaction();
            return false;
        } finally {
            if (updateStatement != null)
                updateStatement.close();
        }
    }

    /**
     * This method updates the task.structure_id field with
     * ids of existing structures where the structure._id equals
     * the task.for value.
     *
     * <p>
     * This is only done for tasks that have a null structure_id field
     *
     * @return bolean indicating whether the update was successful
     */
    public boolean updateTaskStructureIdsFromExistingStructures() {

        try {
            getReadableDatabase().execSQL(String.format("UPDATE %s SET %s =(SELECT %s FROM structure WHERE %s = %s) WHERE %s IS NULL",
                    TASK_TABLE, STRUCTURE_ID, ID, ID, FOR, STRUCTURE_ID));
            return true;
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }
    }

    /**
     * This method updates the task.structure_id field with
     * structure_ids of existing clients where the base_entity_id equals
     * the task.for value.
     *
     * <p>
     * This is only done for tasks that have a null structure_id field
     *
     * @return bolean indicating whether the update was successful
     */
    public boolean updateTaskStructureIdsFromExistingClients(String clientTable) {

        try {
            getReadableDatabase().execSQL(String.format("UPDATE %s SET %s =(SELECT %s FROM %s WHERE base_entity_id = %s) WHERE %s IS NULL",
                    TASK_TABLE, STRUCTURE_ID, STRUCTURE_ID, clientTable, FOR, STRUCTURE_ID));
            return true;
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }
    }

    public boolean batchInsertTasks(JSONArray array) {
        if (array == null || array.length() == 0) {
            return false;
        }

        try {
            getWritableDatabase().beginTransaction();

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                Task task = TaskServiceHelper.taskGson.fromJson(jsonObject.toString(), Task.class);
                addOrUpdate(task);
            }

            getWritableDatabase().setTransactionSuccessful();
            getWritableDatabase().endTransaction();
            return true;
        } catch (Exception e) {
            Timber.e(e, "EXCEPTION %s", e.toString());
            getWritableDatabase().endTransaction();
            return false;
        }
    }

    /**
     * Fetches {@link Task}s whose rowid > #lastRowId up to the #limit provided.
     *
     * @param lastRowId
     * @param jurisdictionId
     * @return JsonData which contains a {@link JSONArray} and the maximum row id in the array
     * of {@link Task}s returned or {@code null} if no records match the conditions or an exception occurred.
     * This enables this method to be called again for the consequent batches
     */
    @Nullable
    public JsonData getTasks(long lastRowId, int limit, String jurisdictionId) {
        JsonData jsonData = null;
        long maxRowId = 0;
        String locationFilter = jurisdictionId != null ? String.format(" %s =? AND ", GROUP_ID) : "";
        String query = "SELECT "
                + ROWID
                + ",* FROM "
                + TASK_TABLE
                + " WHERE "
                + locationFilter
                + ROWID
                + " > ? "
                + " ORDER BY " + ROWID + " ASC LIMIT ?";

        Cursor cursor = null;
        JSONArray jsonArray = new JSONArray();

        try {
            cursor = getWritableDatabase().rawQuery(query, jurisdictionId != null ? new Object[]{jurisdictionId, lastRowId, limit} : new Object[]{lastRowId, limit});

            while (cursor.moveToNext()) {
                long rowId = cursor.getLong(0);

                Task task = readCursor(cursor);
                task.setRowid(cursor.getLong(0));

                String taskString = TaskServiceHelper.taskGson.toJson(task);
                JSONObject taskObject = new JSONObject(taskString);

                jsonArray.put(taskObject);

                if (rowId > maxRowId) {
                    maxRowId = rowId;
                }
            }
        } catch (Exception e) {
            Timber.e(e, "EXCEPTION %s", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (jsonArray.length() > 0) {
            jsonData = new JsonData(jsonArray, maxRowId);
        }
        return jsonData;
    }

    /**
     * Cancels tasks for an entity
     * Cancels all tasks that have status ready @{@link TaskStatus}
     *
     * @param entityId id of the entity whose tasks are being cancelled
     */
    public void cancelTasksForEntity(@NonNull String entityId) {
        if (StringUtils.isBlank(entityId))
            return;
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, TaskStatus.CANCELLED.name());
        contentValues.put(SYNC_STATUS, BaseRepository.TYPE_Unsynced);
        getWritableDatabase().update(TASK_TABLE, contentValues, String.format("%s = ? AND %s =?", FOR, STATUS), new String[]{entityId, TaskStatus.READY.name()});
    }

    /**
     * Cancels a particular task
     * <p>
     * Cancels the task if it has a status ready @{@link TaskStatus}
     *
     * @param identifier id of the task to be cancelled
     */
    public void cancelTaskByIdentifier(@NonNull String identifier) {
        if (StringUtils.isBlank(identifier))
            return;
        Task task = getTaskByIdentifier(identifier);
        if (task == null || !TaskStatus.READY.equals(task.getStatus()))
            return;
        task.setStatus(TaskStatus.CANCELLED);
        // update task sync status to unsynced if it was already synced,
        // ignore if task status is created so that it will be created on server
        if (!BaseRepository.TYPE_Created.equals(task.getSyncStatus())) {
            task.setSyncStatus(BaseRepository.TYPE_Unsynced);
        }
        task.setLastModified(DateTime.now());
        addOrUpdate(task, true);
    }


    /**
     * Archive tasks for an entity
     * Archives all tasks that have status different from ready @{@link TaskStatus}
     *
     * @param entityId id of the entity whose tasks are being archived
     */
    public void archiveTasksForEntity(@NonNull String entityId) {
        if (StringUtils.isBlank(entityId))
            return;
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, TaskStatus.ARCHIVED.name());
        contentValues.put(SYNC_STATUS, BaseRepository.TYPE_Unsynced);
        getWritableDatabase().update(TASK_TABLE, contentValues, String.format("%s = ? AND %s NOT IN (?,?)", FOR, STATUS), new String[]{entityId, TaskStatus.READY.name(), TaskStatus.CANCELLED.name()});
    }

    public int getUnsyncedCreatedTasksAndTaskStatusCount() {
        Cursor cursor = null;
        int unsyncedRecordsCount = 0;
        try {
            cursor = getReadableDatabase().rawQuery(String.format("SELECT count(*) FROM %s WHERE %s =? OR %s IS NULL OR %s = ?"
                    , TASK_TABLE, SYNC_STATUS, SERVER_VERSION, SYNC_STATUS)
                    , new String[]{BaseRepository.TYPE_Created, BaseRepository.TYPE_Unsynced});
            if (cursor.moveToNext()) {
                unsyncedRecordsCount = cursor.getInt(0);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return unsyncedRecordsCount;
    }

    @NonNull
    public Set<Task> getTasksByJurisdictionAndPlan(@NonNull String jurisdictionId, String planIdentifier) {
        String query = TextUtils.join(" ",
                new String[]{"SELECT * FROM", TASK_TABLE, "WHERE", GROUP_ID, "=?", "AND", PLAN_ID, "=?"});
        return getTasks(query, new String[]{jurisdictionId, planIdentifier});
    }

    @NonNull
    public Set<Task> getTasksByJurisdiction(@NonNull String jurisdictionId) {
        String query = TextUtils.join(" ",
                new String[]{"SELECT * FROM", TASK_TABLE, "WHERE", GROUP_ID, "=?"});
        return getTasks(query, new String[]{jurisdictionId});
    }

    public List<String> getEntityIdsWithDuplicateTasks() {
        List<String> entityIds = new ArrayList<>();
        android.database.Cursor cursor = null;
        String query = "SELECT " +
                "    DISTINCT(for) " +
                "FROM " +
                "    task " +
                "WHERE status IN (?, ?) " +
                "GROUP BY " +
                "    plan_id, for, code " +
                "HAVING " +
                "    COUNT(*) > 1";

        try {
            cursor = getReadableDatabase().rawQuery(query, new String[]{TaskStatus.READY.name(), TaskStatus.COMPLETED.name()});

            while (cursor.moveToNext()) {
                entityIds.add(cursor.getString(0));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return entityIds;
    }

    public Set<Task> getDuplicateTasksForEntity(String entityId) {
        String query = "SELECT t1.* " +
                "FROM task t1 " +
                "JOIN (SELECT " +
                "plan_id, for, code, COUNT(*) as count " +
                "FROM " +
                "    task " +
                "WHERE for = ? " +
                "GROUP BY " +
                "    plan_id, for, code " +
                "HAVING  " +
                "    COUNT(*) > 1) t2 " +
                "ON t1.plan_id = t2.plan_id " +
                "AND t1.for = t2.for " +
                "AND t1.code = t2.code " +
                "AND t1.for = ? " +
                "ORDER BY t1.for";
        return getTasks(query, new String[]{entityId, entityId});
    }

    public void deleteTasksByIds(List<String> taskIds) {
        String taskIdsBinding = StringUtils.repeat("? ", ", ", taskIds.size());
        getWritableDatabase().delete(TASK_TABLE, String.format("_id in (%s)", taskIdsBinding), taskIds.toArray(new String[0]));
    }
}
