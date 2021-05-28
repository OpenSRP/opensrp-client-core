package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.LocalDate;
import org.smartregister.domain.TimelineEvent;

import java.util.ArrayList;
import java.util.List;

public class TimelineEventRepository extends DrishtiRepository {
    private static final String TIMELINEEVENT_SQL = "CREATE TABLE timelineEvent(caseID VARCHAR, "
            + "type VARCHAR, referenceDate VARCHAR, title VARCHAR, detail1 VARCHAR, detail2 "
            + "VARCHAR)";
    private static final String TIMELINEVENT_CASEID_INDEX_SQL =
            "CREATE INDEX " + "timelineEvent_caseID_index ON timelineEvent(caseID);";
    private static final String TIMELINEEVENT_TABLE_NAME = "timelineEvent";
    private static final String CASEID_COLUMN = "caseId";
    private static final String TYPE_COLUMN = "type";
    private static final String REF_DATE_COLUMN = "referenceDate";
    private static final String TITLE_COLUMN = "title";
    private static final String DETAIL1_COLUMN = "detail1";
    private static final String DETAIL2_COLUMN = "detail2";
    private static final String[] TIMELINEEVENT_TABLE_COLUMNS = {CASEID_COLUMN, TYPE_COLUMN,
            REF_DATE_COLUMN, TITLE_COLUMN, DETAIL1_COLUMN, DETAIL2_COLUMN};

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(TIMELINEEVENT_SQL);
        database.execSQL(TIMELINEVENT_CASEID_INDEX_SQL);
    }

    public void add(TimelineEvent timelineEvent) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.insert(TIMELINEEVENT_TABLE_NAME, null, createValuesFor(timelineEvent));
    }

    public List<TimelineEvent> allFor(String caseId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(TIMELINEEVENT_TABLE_NAME, TIMELINEEVENT_TABLE_COLUMNS,
                CASEID_COLUMN + "= ?", new String[]{caseId}, null, null, null);

        return readAllTimelineEvents(cursor);
    }

    public void deleteAllTimelineEventsForEntity(String caseId) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.delete(TIMELINEEVENT_TABLE_NAME, CASEID_COLUMN + " = ?", new String[]{caseId});
    }

    private List<TimelineEvent> readAllTimelineEvents(Cursor cursor) {
        cursor.moveToFirst();
        List<TimelineEvent> timelineEvents = new ArrayList<TimelineEvent>();
        while (!cursor.isAfterLast()) {
            timelineEvents.add(new TimelineEvent(cursor.getString(0), cursor.getString(1),
                    LocalDate.parse(cursor.getString(2)), cursor.getString(3), cursor.getString(4),
                    cursor.getString(5)));
            cursor.moveToNext();
        }
        cursor.close();

        return timelineEvents;
    }

    private ContentValues createValuesFor(TimelineEvent timelineEvent) {
        ContentValues values = new ContentValues();
        values.put(CASEID_COLUMN, timelineEvent.caseId());
        values.put(TYPE_COLUMN, timelineEvent.type());
        values.put(REF_DATE_COLUMN, timelineEvent.referenceDate().toString());
        values.put(TITLE_COLUMN, timelineEvent.title());
        values.put(DETAIL1_COLUMN, timelineEvent.detail1());
        values.put(DETAIL2_COLUMN, timelineEvent.detail2());

        return values;
    }
}
