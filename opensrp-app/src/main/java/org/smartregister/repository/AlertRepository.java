package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.LocalDate;
import org.smartregister.domain.Alert;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.apache.commons.lang3.ArrayUtils.addAll;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.smartregister.domain.AlertStatus.complete;
import static org.smartregister.domain.AlertStatus.from;
import static org.smartregister.domain.AlertStatus.inProcess;

public class AlertRepository extends DrishtiRepository {
    public static final String ALERTS_TABLE_NAME = "alerts";
    public static final String ALERTS_CASEID_COLUMN = "caseID";
    public static final String ALERTS_SCHEDULE_NAME_COLUMN = "scheduleName";
    public static final String ALERTS_VISIT_CODE_COLUMN = "visitCode";
    public static final String ALERTS_STATUS_COLUMN = "status";
    public static final String ALERTS_STARTDATE_COLUMN = "startDate";
    public static final String ALERTS_EXPIRYDATE_COLUMN = "expiryDate";
    public static final String ALERTS_COMPLETIONDATE_COLUMN = "completionDate";
    public static final String ALERTS_OFFLINE_COLUMN = "offline";
    public static final String CASE_AND_VISIT_CODE_COLUMN_SELECTIONS =
            ALERTS_CASEID_COLUMN + " " + "=" + " ? AND " + ALERTS_VISIT_CODE_COLUMN + " = ?";
    public static final String OFFLINE_INDEX =
            "CREATE INDEX " + ALERTS_TABLE_NAME + "_" + ALERTS_OFFLINE_COLUMN + "_index ON "
                    + ALERTS_TABLE_NAME + "(" + ALERTS_OFFLINE_COLUMN + " COLLATE NOCASE);";
    public static final String ALTER_ADD_OFFLINE_COLUMN =
            "ALTER TABLE " + ALERTS_TABLE_NAME + " " + "" + "" + "ADD COLUMN "
                    + ALERTS_OFFLINE_COLUMN + " INTEGER NOT NULL DEFAULT 0";
    private static final String ALERTS_SQL = "CREATE TABLE alerts (caseID VARCHAR, scheduleName "
            + "VARCHAR, visitCode VARCHAR, status VARCHAR, startDate VARCHAR, expiryDate VARCHAR,"
            + "" + " " + "completionDate VARCHAR)";
    private static final String[] ALERTS_TABLE_COLUMNS = new String[]{ALERTS_CASEID_COLUMN,
            ALERTS_SCHEDULE_NAME_COLUMN, ALERTS_VISIT_CODE_COLUMN, ALERTS_STATUS_COLUMN,
            ALERTS_STARTDATE_COLUMN, ALERTS_EXPIRYDATE_COLUMN, ALERTS_COMPLETIONDATE_COLUMN,
            ALERTS_OFFLINE_COLUMN};
    private static final String CASE_ID_INDEX =
            "CREATE INDEX " + ALERTS_TABLE_NAME + "_" + ALERTS_CASEID_COLUMN + "_index ON "
                    + ALERTS_TABLE_NAME + "(" + ALERTS_CASEID_COLUMN + " COLLATE NOCASE);";
    private static final String SCHEDULE_NAME_INDEX =
            "CREATE INDEX " + ALERTS_TABLE_NAME + "_" + ALERTS_SCHEDULE_NAME_COLUMN + "_index ON "
                    + ALERTS_TABLE_NAME + "(" + ALERTS_SCHEDULE_NAME_COLUMN + " COLLATE NOCASE);";
    private static final String STATUS_NAME_INDEX =
            "CREATE INDEX " + ALERTS_TABLE_NAME + "_" + ALERTS_STATUS_COLUMN + "_index ON "
                    + ALERTS_TABLE_NAME + "(" + ALERTS_STATUS_COLUMN + " COLLATE NOCASE);";
    private static final String VISIT_CODE_INDEX =
            "CREATE INDEX " + ALERTS_TABLE_NAME + "_" + ALERTS_VISIT_CODE_COLUMN + "_index ON "
                    + ALERTS_TABLE_NAME + "(" + ALERTS_VISIT_CODE_COLUMN + " COLLATE NOCASE);";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(ALERTS_SQL);
        database.execSQL(CASE_ID_INDEX);
        database.execSQL(SCHEDULE_NAME_INDEX);
        database.execSQL(STATUS_NAME_INDEX);
        database.execSQL(VISIT_CODE_INDEX);
    }

    public List<Alert> allAlerts() {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database
                    .query(ALERTS_TABLE_NAME, ALERTS_TABLE_COLUMNS, null, null, null, null, null, null);
            return readAllAlerts(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<Alert> allActiveAlertsForCase(String caseId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.query(ALERTS_TABLE_NAME, ALERTS_TABLE_COLUMNS,
                    ALERTS_CASEID_COLUMN + " = ?  " + "COLLATE NOCASE ", new String[]{caseId}, null,
                    null, null, null);
            return filterActiveAlerts(readAllAlerts(cursor));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Creates a single alert in the db. This is inefficient if you have multiple alerts to create.
     * For efficiency when processing multiple alerts, it is recommended to use {@link #createAlerts(List<Alert>) method} instead
     *
     * @param alert the alert to process
     */
    public void createAlert(Alert alert) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        createAlertCore(database, alert);
    }

    /**
     * Creates alerts in the db as a transaction.
     * For efficiency it is recommended to use this method instead of the {@link #createAlert(Alert alert) method} which only processes one alert at a time
     *
     * @param alertList A list of alerts to persist in the DB
     */
    public void createAlerts(List<Alert> alertList) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.beginTransaction();

        try {

            for (Alert alert : alertList) {

                createAlertCore(database, alert);
            }

            database.setTransactionSuccessful();

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            database.endTransaction();
        }
    }

    /**
     * A more efficient way to check for the prescence of Alerts in the db corresponding to a specific entity id.
     *
     * @param database     a reference to the db helper
     * @param caseId       the entity id
     * @param scheduleName the name of the vaccine(schedule name) eg. bcg, opv0, penta1 e.t.c
     */
    public int getAlertsCount(SQLiteDatabase database, String caseId, String scheduleName) {

        String[] caseAndScheduleNameColumnValues = {caseId, scheduleName};
        String caseAndScheduleNameColumnSelections = ALERTS_CASEID_COLUMN + " = ?  COLLATE NOCASE" + " AND " + ALERTS_SCHEDULE_NAME_COLUMN + " = ?";

        Cursor cursor = null;
        int count;

        try {
            cursor = database.rawQuery(String.format("SELECT COUNT(%s) FROM %s WHERE %s", ALERTS_CASEID_COLUMN, ALERTS_TABLE_NAME, caseAndScheduleNameColumnSelections), caseAndScheduleNameColumnValues);
            cursor.moveToFirst();
            count = cursor.getInt(0);

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }

    private void createAlertCore(SQLiteDatabase database, Alert alert) {

        String caseAndScheduleNameColumnSelections = ALERTS_CASEID_COLUMN + " = ?  COLLATE NOCASE" + " AND " + ALERTS_SCHEDULE_NAME_COLUMN + " = ?";
        boolean alertsExist = getAlertsCount(database, alert.caseId(), alert.scheduleName()) > 0;

        ContentValues values = createValuesFor(alert);
        if (!alertsExist) {
            database.insert(ALERTS_TABLE_NAME, null, values);
        } else {
            database.update(ALERTS_TABLE_NAME, values, caseAndScheduleNameColumnSelections, new String[]{alert.caseId(), alert.scheduleName()});
        }
    }

    public void markAlertAsClosed(String caseId, String visitCode, String completionDate) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        String[] caseAndVisitCodeColumnValues = {caseId, visitCode};

        ContentValues valuesToBeUpdated = new ContentValues();
        valuesToBeUpdated.put(ALERTS_STATUS_COLUMN, complete.value());
        valuesToBeUpdated.put(ALERTS_COMPLETIONDATE_COLUMN, completionDate);
        database.update(ALERTS_TABLE_NAME, valuesToBeUpdated, CASE_AND_VISIT_CODE_COLUMN_SELECTIONS,
                caseAndVisitCodeColumnValues);
    }

    public void deleteVaccineAlertForEntity(String caseId, String visitCode) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.delete(ALERTS_TABLE_NAME, ALERTS_CASEID_COLUMN + " = ? AND " + ALERTS_VISIT_CODE_COLUMN + " = ? "
                , new String[]{caseId, visitCode});
    }

    public void deleteAllAlertsForEntity(String caseId) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.delete(ALERTS_TABLE_NAME, ALERTS_CASEID_COLUMN + "= ?  COLLATE NOCASE ",
                new String[]{caseId});
    }

    public void deleteOfflineAlertsForEntity(String caseId) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.delete(ALERTS_TABLE_NAME,
                ALERTS_CASEID_COLUMN + "= ?  COLLATE NOCASE AND " + ALERTS_OFFLINE_COLUMN + " = 1",
                new String[]{caseId});
    }

    public void deleteOfflineAlertsForEntity(String caseId, String... names) {
        String whereClause = String.format(" %s = ? COLLATE NOCASE AND %s = 1 AND %s IN (%s)",
                ALERTS_CASEID_COLUMN, ALERTS_OFFLINE_COLUMN, ALERTS_VISIT_CODE_COLUMN,
                insertPlaceholdersForInClause(names.length));
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.delete(ALERTS_TABLE_NAME, whereClause, addAll(new String[]{caseId}, names));
    }

    public void deleteAllAlerts() {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.delete(ALERTS_TABLE_NAME, null, null);
    }

    private List<Alert> readAllAlerts(Cursor cursor) {
        cursor.moveToFirst();
        List<Alert> alerts = new ArrayList<Alert>();
        while (!cursor.isAfterLast()) {
            alerts.add(new Alert(cursor.getString(cursor.getColumnIndex(ALERTS_CASEID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(ALERTS_SCHEDULE_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(ALERTS_VISIT_CODE_COLUMN)),
                    from(cursor.getString(cursor.getColumnIndex(ALERTS_STATUS_COLUMN))),
                    cursor.getString(cursor.getColumnIndex(ALERTS_STARTDATE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(ALERTS_EXPIRYDATE_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(ALERTS_OFFLINE_COLUMN)) == 1)
                    .withCompletionDate(
                            cursor.getString(cursor.getColumnIndex(ALERTS_COMPLETIONDATE_COLUMN))));
            cursor.moveToNext();
        }
        return alerts;
    }

    private List<Alert> filterActiveAlerts(List<Alert> alerts) {
        List<Alert> activeAlerts = new ArrayList<Alert>();
        for (Alert alert : alerts) {
            LocalDate today = LocalDate.now();
            if (LocalDate.parse(alert.expiryDate()).isAfter(today) || (
                    complete.equals(alert.status()) && LocalDate.parse(alert.completionDate())
                            .isAfter(today.minusDays(3)))) {
                activeAlerts.add(alert);
            }
        }
        return activeAlerts;
    }

    private ContentValues createValuesFor(Alert alert) {
        ContentValues values = new ContentValues();
        values.put(ALERTS_CASEID_COLUMN, alert.caseId());
        values.put(ALERTS_SCHEDULE_NAME_COLUMN, alert.scheduleName());
        values.put(ALERTS_VISIT_CODE_COLUMN, alert.visitCode());
        values.put(ALERTS_STATUS_COLUMN, alert.status().value());
        values.put(ALERTS_STARTDATE_COLUMN, alert.startDate());
        values.put(ALERTS_EXPIRYDATE_COLUMN, alert.expiryDate());
        values.put(ALERTS_COMPLETIONDATE_COLUMN, alert.completionDate());
        values.put(ALERTS_OFFLINE_COLUMN, alert.offline() ? 1 : 0);
        return values;
    }

    public List<Alert> findByEntityId(String entityId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    String.format("SELECT * FROM %s WHERE %s = ? COLLATE NOCASE " + "ORDER BY " + "DATE"
                            + "(%s)", ALERTS_TABLE_NAME, ALERTS_CASEID_COLUMN, ALERTS_STARTDATE_COLUMN),
                    new String[]{entityId});
            return readAllAlerts(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<Alert> findByEntityIdAndAlertNames(String entityId, String... names) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    String.format("SELECT * FROM %s WHERE %s = ? COLLATE NOCASE " + "AND %s IN "
                                    + "(%s) ORDER BY DATE(%s)", ALERTS_TABLE_NAME, ALERTS_CASEID_COLUMN,
                            ALERTS_VISIT_CODE_COLUMN, insertPlaceholdersForInClause(names.length),
                            ALERTS_STARTDATE_COLUMN), addAll(new String[]{entityId}, names));
            return readAllAlerts(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<Alert> findOfflineByEntityIdAndName(String entityId, String... names) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    String.format("SELECT * FROM %s WHERE %s = ? COLLATE NOCASE " + "AND %s = 1 "
                                    + "AND %s IN (%s) ORDER BY DATE(%s)", ALERTS_TABLE_NAME,
                            ALERTS_CASEID_COLUMN, ALERTS_OFFLINE_COLUMN, ALERTS_VISIT_CODE_COLUMN,
                            insertPlaceholdersForInClause(names.length), ALERTS_STARTDATE_COLUMN),
                    addAll(new String[]{entityId}, names));
            return readAllAlerts(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Alert findByEntityIdAndScheduleName(String entityId, String scheduleName) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        String[] caseAndScheduleNameColumnValues = {entityId, scheduleName};

        String caseAndScheduleNameColumnSelections =
                ALERTS_CASEID_COLUMN + " = ? COLLATE NOCASE " + "" + "" + "AND "
                        + ALERTS_SCHEDULE_NAME_COLUMN + " = ? ";

        Cursor cursor = null;
        try {
            cursor = database
                    .query(ALERTS_TABLE_NAME, ALERTS_TABLE_COLUMNS, caseAndScheduleNameColumnSelections,
                            caseAndScheduleNameColumnValues, null, null, null, null);
            List<Alert> alertList = readAllAlerts(cursor);
            if (!alertList.isEmpty()) {
                return alertList.get(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }

    public void changeAlertStatusToInProcess(String entityId, String alertName) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        String[] caseAndVisitCodeColumnValues = {entityId, alertName};

        ContentValues valuesToBeUpdated = new ContentValues();
        valuesToBeUpdated.put(ALERTS_STATUS_COLUMN, inProcess.value());
        database.update(ALERTS_TABLE_NAME, valuesToBeUpdated, CASE_AND_VISIT_CODE_COLUMN_SELECTIONS,
                caseAndVisitCodeColumnValues);
    }

    public void changeAlertStatusToComplete(String entityId, String alertName) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        String[] caseAndVisitCodeColumnValues = {entityId, alertName};

        ContentValues valuesToBeUpdated = new ContentValues();
        valuesToBeUpdated.put(ALERTS_STATUS_COLUMN, complete.value());
        database.update(ALERTS_TABLE_NAME, valuesToBeUpdated, CASE_AND_VISIT_CODE_COLUMN_SELECTIONS,
                caseAndVisitCodeColumnValues);
    }

}
