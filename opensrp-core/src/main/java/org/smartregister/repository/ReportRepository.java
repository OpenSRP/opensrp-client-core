package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.Report;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.repeat;

public class ReportRepository extends DrishtiRepository {
    private static final String REPORT_SQL =
            "CREATE TABLE report(indicator VARCHAR PRIMARY KEY, " + "" + ""
                    + "annualTarget VARCHAR, monthlySummaries VARCHAR)";
    private static final String REPORT_INDICATOR_INDEX_SQL =
            "CREATE INDEX " + "report_indicator_index" + " ON report(indicator);";
    private static final String REPORT_TABLE_NAME = "report";
    private static final String INDICATOR_COLUMN = "indicator";
    private static final String ANNUAL_TARGET_COLUMN = "annualTarget";
    private static final String MONTHLY_SUMMARIES_COLUMN = "monthlySummaries";
    private static final String[] REPORT_TABLE_COLUMNS = {INDICATOR_COLUMN, ANNUAL_TARGET_COLUMN,
            MONTHLY_SUMMARIES_COLUMN};

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(REPORT_SQL);
        database.execSQL(REPORT_INDICATOR_INDEX_SQL);
    }

    public void update(Report report) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.replace(REPORT_TABLE_NAME, null, createValuesFor(report));
    }

    public List<Report> allFor(String... indicators) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(
                String.format("SELECT * FROM %s WHERE %s IN (%s)", REPORT_TABLE_NAME,
                        INDICATOR_COLUMN, insertPlaceholdersForInClause(indicators.length)),
                indicators);
        return readAll(cursor);
    }

    public List<Report> all() {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database
                .query(REPORT_TABLE_NAME, REPORT_TABLE_COLUMNS, null, null, null, null, null);
        return readAll(cursor);
    }

    private ContentValues createValuesFor(Report report) {
        ContentValues values = new ContentValues();
        values.put(INDICATOR_COLUMN, report.indicator());
        values.put(ANNUAL_TARGET_COLUMN, report.annualTarget());
        values.put(MONTHLY_SUMMARIES_COLUMN, report.monthlySummariesJSON());
        return values;
    }

    private List<Report> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<Report> reports = new ArrayList<Report>();
        while (!cursor.isAfterLast()) {
            reports.add(read(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return reports;
    }

    private Report read(Cursor cursor) {
        return new Report(cursor.getString(0), cursor.getString(1), cursor.getString(2));
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }
}
