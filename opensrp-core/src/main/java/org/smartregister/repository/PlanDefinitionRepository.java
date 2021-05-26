package org.smartregister.repository;

import android.content.ContentValues;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import timber.log.Timber;

import static org.smartregister.domain.PlanDefinition.PlanStatus.ACTIVE;

/**
 * Created by samuelgithengi on 5/7/19.
 */
public class PlanDefinitionRepository extends BaseRepository {

    public static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd HH:mm:ss.SSSZ"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .disableHtmlEscaping()
            .create();

    protected static final String ID = "_id";
    protected static final String JSON = "json";
    protected static final String NAME = "name";
    private static final String STATUS = "status";
    protected static final String DRAFT = "draft";

    private static final String PLAN_DEFINITION_TABLE = "plan_definition";

    private PlanDefinitionSearchRepository searchRepository;

    private static final String CREATE_PLAN_DEFINITION_TABLE =
            "CREATE TABLE " + PLAN_DEFINITION_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    JSON + " VARCHAR NOT NULL," +
                    STATUS + " VARCHAR NOT NULL)";


    public PlanDefinitionRepository() {
        searchRepository = new PlanDefinitionSearchRepository();
        searchRepository.setPlanDefinitionRepository(this);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_PLAN_DEFINITION_TABLE);
    }

    public void addOrUpdate(PlanDefinition planDefinition) {
        if (DRAFT.equalsIgnoreCase(planDefinition.getStatus().value()))
            return;
        try {
            getWritableDatabase().beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID, planDefinition.getIdentifier());

            contentValues.put(STATUS, planDefinition.getStatus().value());

            for (Jurisdiction jurisdiction : planDefinition.getJurisdiction()) {
                searchRepository.addOrUpdate(planDefinition, jurisdiction.getCode());
            }
            planDefinition.setJurisdiction(new ArrayList<>());
            contentValues.put(JSON, gson.toJson(planDefinition));
            getWritableDatabase().replace(PLAN_DEFINITION_TABLE, null, contentValues);

            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }

    }

    /**
     * Deletes plans which a user no longer has access to
     *
     * @param planIdentifiers the set of plan identifiers to delete
     */
    public void deletePlans(@NonNull Set<String> planIdentifiers) {
        getWritableDatabase().delete(PLAN_DEFINITION_TABLE,
                String.format("%s IN (%s)", ID, StringUtils.repeat("?", ",", planIdentifiers.size())),
                planIdentifiers.toArray(new String[]{}));
    }

    public PlanDefinition findPlanDefinitionById(String identifier) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT  " + JSON + " FROM " + PLAN_DEFINITION_TABLE +
                    " WHERE " + ID + " =?", new String[]{identifier});
            if (cursor.moveToFirst()) {
                return gson.fromJson(cursor.getString(0), PlanDefinition.class);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public Set<PlanDefinition> findPlanDefinitionByIds(Set<String> identifiers) {
        Cursor cursor = null;
        Set<PlanDefinition> planDefinitions = new HashSet<>();
        try {
            String query = String.format("SELECT %s  FROM %s  WHERE %s IN (%s)", JSON, PLAN_DEFINITION_TABLE, ID,
                    TextUtils.join(",", Collections.nCopies(identifiers.size(), "?")));
            cursor = getReadableDatabase().rawQuery(query, identifiers.toArray(new String[0]));
            while (cursor.moveToNext()) {
                planDefinitions.add(gson.fromJson(cursor.getString(0), PlanDefinition.class));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return planDefinitions;
    }


    public Set<PlanDefinition> findAllPlanDefinitions() {
        Cursor cursor = null;
        Set<PlanDefinition> planDefinitions = new TreeSet<>();
        try {
            String query = String.format("SELECT %s  FROM %s WHERE %s =?",
                    JSON, PLAN_DEFINITION_TABLE, STATUS);
            cursor = getReadableDatabase().rawQuery(query, new String[]{ACTIVE.value()});
            while (cursor.moveToNext()) {
                planDefinitions.add(gson.fromJson(cursor.getString(0), PlanDefinition.class));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return planDefinitions;
    }

    public Set<String> findAllPlanDefinitionIds() {
        Cursor cursor = null;
        Set<String> ids = new HashSet<>();
        try {
            String query = String.format("SELECT %s  FROM %s WHERE %s =?", ID, PLAN_DEFINITION_TABLE, STATUS);
            cursor = getReadableDatabase().rawQuery(query, new String[]{ACTIVE.value()});
            while (cursor.moveToNext()) {
                ids.add(cursor.getString(0));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return ids;
    }

}
