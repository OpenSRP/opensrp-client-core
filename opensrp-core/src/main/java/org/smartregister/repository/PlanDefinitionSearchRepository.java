package org.smartregister.repository;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.PlanDefinitionSearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 5/7/19.
 */
public class PlanDefinitionSearchRepository extends BaseRepository {

    protected static final String PLAN_ID = "plan_id";
    protected static final String JURISDICTION_ID = "jurisdiction_id";
    protected static final String NAME = "name";
    protected static final String STATUS = "status";
    protected static final String START = "start";
    protected static final String END = "end";


    protected static final String[] COLUMNS = new String[]{PLAN_ID, JURISDICTION_ID, NAME, STATUS, START, END};
    protected static final String ACTIVE = "active";

    protected static final String PLAN_DEFINITION_SEARCH_TABLE = "plan_definition_search";

    private PlanDefinitionRepository planDefinitionRepository;

    private static final String CREATE_PLAN_DEFINITION_TABLE =
            "CREATE TABLE " + PLAN_DEFINITION_SEARCH_TABLE + " (" +
                    PLAN_ID + " VARCHAR NOT NULL," +
                    JURISDICTION_ID + " VARCHAR NOT NULL," +
                    NAME + " VARCHAR NOT NULL," +
                    STATUS + " VARCHAR NOT NULL," +
                    START + " INTEGER NOT NULL," +
                    END + " INTEGER NOT NULL, PRIMARY KEY (" +
                    PLAN_ID + "," + JURISDICTION_ID + "))";

    private static final String CREATE_PLAN_DEFINITION_STATUS_INDEX = "CREATE INDEX "
            + PLAN_DEFINITION_SEARCH_TABLE + "_status_ind  ON " + PLAN_DEFINITION_SEARCH_TABLE + "(" + STATUS + ")";

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_PLAN_DEFINITION_TABLE);
        database.execSQL(CREATE_PLAN_DEFINITION_STATUS_INDEX);
    }


    public void addOrUpdate(PlanDefinition planDefinition, String jurisdiction) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLAN_ID, planDefinition.getIdentifier());
        contentValues.put(JURISDICTION_ID, jurisdiction);
        contentValues.put(NAME, planDefinition.getName());
        contentValues.put(STATUS, planDefinition.getStatus().value());
        contentValues.put(START, planDefinition.getEffectivePeriod().getStart().toDate().getTime());
        contentValues.put(END, planDefinition.getEffectivePeriod().getEnd().toDate().getTime());
        contentValues.put(JURISDICTION_ID, jurisdiction);
        getWritableDatabase().replace(PLAN_DEFINITION_SEARCH_TABLE, null, contentValues);
    }

    public Set<PlanDefinition> findActivePlansByJurisdiction(String jurisdiction) {

        Set<String> planIds = new HashSet<>();
        Cursor cursor = null;
        try {
            String query = String.format("SELECT %s FROM %s " +
                            "WHERE %s=? AND %s=?  AND %s  >=? ", PLAN_ID,
                    PLAN_DEFINITION_SEARCH_TABLE, JURISDICTION_ID, STATUS, END);
            cursor = getReadableDatabase().rawQuery(query, new String[]{jurisdiction, ACTIVE,
                    String.valueOf(LocalDate.now().toDate().getTime())});
            while (cursor.moveToNext()) {
                planIds.add(cursor.getString(0));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return getPlanDefinitionRepository().findPlanDefinitionByIds(planIds);
    }

    public List<PlanDefinitionSearch> findPlanDefinitionSearchByPlanId(@NonNull String planId) {
        List<PlanDefinitionSearch> planDefinitionSearchList = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE %s=? ",
                PLAN_DEFINITION_SEARCH_TABLE, PLAN_ID);
        try (Cursor cursor = getReadableDatabase().rawQuery(query, new String[]{planId})) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    planDefinitionSearchList.add(readCursor(cursor));
                }
            }
        } catch (SQLiteException e) {
            Timber.e(e);
        }
        return planDefinitionSearchList;
    }

    public List<PlanDefinitionSearch> findPlanDefinitionSearchByPlanStatus(@NonNull PlanDefinition.PlanStatus status) {
        List<PlanDefinitionSearch> planDefinitionSearchList = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE %s=? ",
                PLAN_DEFINITION_SEARCH_TABLE, STATUS);
        try (Cursor cursor = getReadableDatabase().rawQuery(query, new String[]{status.value()})) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    planDefinitionSearchList.add(readCursor(cursor));
                }
            }
        } catch (SQLiteException e) {
            Timber.e(e);
        }
        return planDefinitionSearchList;
    }

    private PlanDefinitionSearch readCursor(@NonNull Cursor cursor) {
        PlanDefinitionSearch planDefinitionSearch = new PlanDefinitionSearch();
        planDefinitionSearch.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        planDefinitionSearch.setPlanId(cursor.getString(cursor.getColumnIndex(PLAN_ID)));
        planDefinitionSearch.setJurisdictionId(cursor.getString(cursor.getColumnIndex(JURISDICTION_ID)));
        planDefinitionSearch.setStart(new DateTime(cursor.getLong(cursor.getColumnIndex(START))));
        planDefinitionSearch.setEnd(new DateTime(cursor.getLong(cursor.getColumnIndex(END))));
        planDefinitionSearch.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));
        return planDefinitionSearch;
    }

    public boolean planExists(String planId, String jurisdictionId) {

        Cursor cursor = null;
        try {
            String query = String.format("SELECT %s FROM %s " +
                            "WHERE %s=? AND %s=? AND %s=?  AND %s  >=? ", PLAN_ID,
                    PLAN_DEFINITION_SEARCH_TABLE, PLAN_ID, JURISDICTION_ID, STATUS, END);
            cursor = getReadableDatabase().rawQuery(query, new String[]{planId, jurisdictionId, ACTIVE,
                    String.valueOf(LocalDate.now().toDate().getTime())});
            return cursor.moveToFirst();
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
    }

    public void setPlanDefinitionRepository(PlanDefinitionRepository planDefinitionRepository) {
        this.planDefinitionRepository = planDefinitionRepository;

    }

    public PlanDefinitionRepository getPlanDefinitionRepository() {
        if (planDefinitionRepository == null) {
            planDefinitionRepository = new PlanDefinitionRepository();
        }
        return planDefinitionRepository;
    }
}
