package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.domain.db.Column;
import org.smartregister.domain.db.ColumnAttribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class Hia2ReportRepository extends BaseRepository {

    public enum report_column implements Column {
        creator(ColumnAttribute.Type.text, false, false),
        dateCreated(ColumnAttribute.Type.date, false, true),
        editor(ColumnAttribute.Type.text, false, false),
        dateEdited(ColumnAttribute.Type.date, false, false),
        voided(ColumnAttribute.Type.bool, false, false),
        dateVoided(ColumnAttribute.Type.date, false, false),
        voider(ColumnAttribute.Type.text, false, false),
        voidReason(ColumnAttribute.Type.text, false, false),

        reportId(ColumnAttribute.Type.text, true, true),
        syncStatus(ColumnAttribute.Type.text, false, true),
        validationStatus(ColumnAttribute.Type.text, false, true),
        json(ColumnAttribute.Type.text, false, false),
        locationId(ColumnAttribute.Type.text, false, false),
        childLocationId(ColumnAttribute.Type.text, false, false),
        reportDate(ColumnAttribute.Type.date, false, true),
        reportType(ColumnAttribute.Type.text, false, true),
        formSubmissionId(ColumnAttribute.Type.text, false, false),
        providerId(ColumnAttribute.Type.text, false, false),
        entityType(ColumnAttribute.Type.text, false, false),
        version(ColumnAttribute.Type.text, false, false),
        updatedAt(ColumnAttribute.Type.date, false, true),
        serverVersion(ColumnAttribute.Type.longnum, false, true);

        private ColumnAttribute column;

        report_column(ColumnAttribute.Type type, boolean pk, boolean index) {
            this.column = new ColumnAttribute(type, pk, index);
        }

        public ColumnAttribute column() {
            return column;
        }
    }

    // Definitions
    public enum Table implements BaseTable {
        hia2_report(report_column.values());
        private Column[] columns;

        public Column[] columns() {
            return columns;
        }

        Table(Column[] columns) {
            this.columns = columns;
        }
    }


    public List<JSONObject> getUnSyncedReports(int limit) {
        List<JSONObject> reports = new ArrayList<JSONObject>();

        String query = "select "
                + report_column.json
                + ","
                + report_column.syncStatus
                + " from "
                + Table.hia2_report.name()
                + " where "
                + report_column.syncStatus
                + " = ?  and length("
                + report_column.json
                + ")>2 order by "
                + report_column.updatedAt
                + " asc limit "
                + limit;
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(query, new String[]{BaseRepository.TYPE_Unsynced});

            while (cursor.moveToNext()) {
                String jsonEventStr = (cursor.getString(0));
                if (StringUtils.isBlank(jsonEventStr)
                        || "{}".equals(jsonEventStr)) { // Skip blank/empty json string
                    continue;
                }
                jsonEventStr = jsonEventStr.replaceAll("'", "");
                JSONObject jsonObectEvent = new JSONObject(jsonEventStr);
                reports.add(jsonObectEvent);

            }

        } catch (Exception e) {
            Timber.e(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return reports;
    }

    public List<String> getUnValidatedReportFormSubmissionIds(int limit) {
        List<String> ids = new ArrayList<String>();

        final String validateFilter = " where "
                + report_column.syncStatus + " = ? "
                + " AND ( " + report_column.validationStatus + " is NULL or "
                + report_column.validationStatus + " != ? ) ";

        String query = "select "
                + report_column.formSubmissionId
                + " from "
                + Table.hia2_report.name()
                + validateFilter
                + ORDER_BY
                + report_column.updatedAt
                + " asc limit "
                + limit;

        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(query, new String[]{BaseRepository.TYPE_Synced, BaseRepository.TYPE_Valid});
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

    public void addReport(JSONObject jsonObject) {
        try {

            ContentValues values = new ContentValues();
            values.put(report_column.json.name(), jsonObject.toString());
            values.put(report_column.reportType.name(),
                    jsonObject.has(report_column.reportType.name()) ? jsonObject.getString(
                            report_column.reportType.name()) : "");
            values.put(report_column.updatedAt.name(), dateFormat.format(new Date()));
            values.put(report_column.syncStatus.name(), BaseRepository.TYPE_Unsynced);
            //update existing event if eventid present
            if (jsonObject.has(report_column.formSubmissionId.name())
                    && jsonObject.getString(report_column.formSubmissionId.name()) != null) {
                //sanity check
                if (checkIfExistsByFormSubmissionId(Table.hia2_report,
                        jsonObject.getString(report_column
                                .formSubmissionId
                                .name()))) {
                    getWritableDatabase().update(Table.hia2_report.name(),
                            values,
                            report_column.formSubmissionId.name() + "=?",
                            new String[]{jsonObject.getString(
                                    report_column.formSubmissionId.name())});
                } else {
                    //that odd case
                    values.put(report_column.formSubmissionId.name(),
                            jsonObject.getString(report_column.formSubmissionId.name()));

                    getWritableDatabase().insert(Table.hia2_report.name(), null, values);

                }
            } else {
// a case here would be if an event comes from openmrs
                getWritableDatabase().insert(Table.hia2_report.name(), null, values);
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void markReportAsSynced(String formSubmissionId) {
        try {

            ContentValues values = new ContentValues();
            values.put(report_column.formSubmissionId.name(), formSubmissionId);
            values.put(report_column.syncStatus.name(), BaseRepository.TYPE_Synced);

            getWritableDatabase().update(Table.hia2_report.name(),
                    values,
                    report_column.formSubmissionId.name() + " = ?",
                    new String[]{formSubmissionId});

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void markReportValidationStatus(String formSubmissionId, boolean valid) {
        try {
            ContentValues values = new ContentValues();
            values.put(report_column.formSubmissionId.name(), formSubmissionId);
            values.put(report_column.validationStatus.name(), valid ? TYPE_Valid : TYPE_InValid);
            if (!valid) {
                values.put(report_column.syncStatus.name(), TYPE_Unsynced);
            }

            getWritableDatabase().update(Table.hia2_report.name(),
                    values,
                    report_column.formSubmissionId.name() + " = ?",
                    new String[]{formSubmissionId});

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void markReportsAsSynced(List<JSONObject> syncedReports) {
        try {

            if (syncedReports != null && !syncedReports.isEmpty()) {
                for (JSONObject report : syncedReports) {
                    String formSubmissionId = report.getString(report_column.formSubmissionId
                            .name());
                    markReportAsSynced(formSubmissionId);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    public Boolean checkIfExistsByFormSubmissionId(Table table, String formSubmissionId) {
        Cursor mCursor = null;
        try {
            String query = "SELECT "
                    + report_column.formSubmissionId
                    + " FROM "
                    + table.name()
                    + " WHERE "
                    + report_column.formSubmissionId
                    + " =?";
            mCursor = getWritableDatabase().rawQuery(query, new String[]{formSubmissionId});
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


}
