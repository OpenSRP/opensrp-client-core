package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.domain.EligibleCouple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;
import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.repeat;

public class EligibleCoupleRepository extends DrishtiRepository {
    public static final String ID_COLUMN = "id";
    public static final String EC_NUMBER_COLUMN = "ecNumber";
    public static final String WIFE_NAME_COLUMN = "wifeName";
    public static final String HUSBAND_NAME_COLUMN = "husbandName";
    public static final String VILLAGE_NAME_COLUMN = "village";
    public static final String SUBCENTER_NAME_COLUMN = "subCenter";
    public static final String IS_OUT_OF_AREA_COLUMN = "isOutOfArea";
    public static final String DETAILS_COLUMN = "details";
    public static final String PHOTO_PATH_COLUMN = "photoPath";
    public static final String EC_TABLE_NAME = "eligible_couple";
    public static final String NOT_CLOSED = "false";
    private static final String EC_SQL = "CREATE TABLE eligible_couple(id VARCHAR PRIMARY KEY, "
            + "wifeName VARCHAR, husbandName VARCHAR, " + "ecNumber VARCHAR, village VARCHAR, "
            + "subCenter VARCHAR, isOutOfArea VARCHAR, details VARCHAR, isClosed VARCHAR, "
            + "photoPath" + " VARCHAR)";
    private static final String IS_CLOSED_COLUMN = "isClosed";
    public static final String[] EC_TABLE_COLUMNS = new String[]{ID_COLUMN, WIFE_NAME_COLUMN,
            HUSBAND_NAME_COLUMN, EC_NUMBER_COLUMN, VILLAGE_NAME_COLUMN, SUBCENTER_NAME_COLUMN,
            IS_OUT_OF_AREA_COLUMN, DETAILS_COLUMN, IS_CLOSED_COLUMN, PHOTO_PATH_COLUMN};
    private static final String IN_AREA = "false";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(EC_SQL);
    }

    public void add(EligibleCouple eligibleCouple) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.insert(EC_TABLE_NAME, null, createValuesFor(eligibleCouple));
    }

    public void updateDetails(String caseId, Map<String, String> details) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();

        EligibleCouple couple = findByCaseID(caseId);
        if (couple == null) {
            return;
        }

        ContentValues valuesToUpdate = new ContentValues();
        valuesToUpdate.put(DETAILS_COLUMN, new Gson().toJson(details));
        database.update(EC_TABLE_NAME, valuesToUpdate, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public void mergeDetails(String caseId, Map<String, String> details) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();

        EligibleCouple couple = findByCaseID(caseId);
        if (couple == null) {
            return;
        }

        Map<String, String> mergedDetails = new HashMap<String, String>(couple.details());
        mergedDetails.putAll(details);
        ContentValues valuesToUpdate = new ContentValues();
        valuesToUpdate.put(DETAILS_COLUMN, new Gson().toJson(mergedDetails));
        database.update(EC_TABLE_NAME, valuesToUpdate, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public List<EligibleCouple> allEligibleCouples() {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(EC_TABLE_NAME, EC_TABLE_COLUMNS,
                IS_OUT_OF_AREA_COLUMN + "" + " = ? AND " + IS_CLOSED_COLUMN + " = ?",
                new String[]{IN_AREA, NOT_CLOSED}, null, null, null, null);
        return readAllEligibleCouples(cursor);
    }

    public List<EligibleCouple> findByCaseIDs(String... caseIds) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(
                String.format("SELECT * FROM %s WHERE %s IN (%s)", EC_TABLE_NAME, ID_COLUMN,
                        insertPlaceholdersForInClause(caseIds.length)), caseIds);
        return readAllEligibleCouples(cursor);
    }

    public EligibleCouple findByCaseID(String caseId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database
                .query(EC_TABLE_NAME, EC_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId},
                        null, null, null, null);
        List<EligibleCouple> couples = readAllEligibleCouples(cursor);
        if (couples.isEmpty()) {
            return null;
        }
        return couples.get(0);
    }

    public long count() {
        return longForQuery(masterRepository().getReadableDatabase(),
                "SELECT COUNT(1) FROM " + EC_TABLE_NAME + " WHERE " + IS_OUT_OF_AREA_COLUMN + " = ?"
                        + " and " + IS_CLOSED_COLUMN + " = ?",
                new String[]{IN_AREA, NOT_CLOSED});
    }

    public List<String> villages() {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(true, EC_TABLE_NAME, new String[]{VILLAGE_NAME_COLUMN},
                IS_OUT_OF_AREA_COLUMN + " = ? AND " + IS_CLOSED_COLUMN + " = ?",
                new String[]{IN_AREA, NOT_CLOSED}, null, null, null, null);
        cursor.moveToFirst();
        List<String> villages = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            villages.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return villages;
    }

    public void updatePhotoPath(String caseId, String imagePath) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHOTO_PATH_COLUMN, imagePath);
        database.update(EC_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public void close(String caseId) {
        ContentValues values = new ContentValues();
        values.put(IS_CLOSED_COLUMN, TRUE.toString());
        masterRepository().getWritableDatabase()
                .update(EC_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    private ContentValues createValuesFor(EligibleCouple eligibleCouple) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, eligibleCouple.caseId());
        values.put(WIFE_NAME_COLUMN, eligibleCouple.wifeName());
        values.put(HUSBAND_NAME_COLUMN, eligibleCouple.husbandName());
        values.put(EC_NUMBER_COLUMN, eligibleCouple.ecNumber());
        values.put(VILLAGE_NAME_COLUMN, eligibleCouple.village());
        values.put(SUBCENTER_NAME_COLUMN, eligibleCouple.subCenter());
        values.put(IS_OUT_OF_AREA_COLUMN, Boolean.toString(eligibleCouple.isOutOfArea()));
        values.put(DETAILS_COLUMN, new Gson().toJson(eligibleCouple.details()));
        values.put(IS_CLOSED_COLUMN, Boolean.toString(eligibleCouple.isClosed()));
        values.put(PHOTO_PATH_COLUMN, eligibleCouple.photoPath());
        return values;
    }

    private List<EligibleCouple> readAllEligibleCouples(Cursor cursor) {
        cursor.moveToFirst();
        List<EligibleCouple> eligibleCouples = new ArrayList<EligibleCouple>();
        while (!cursor.isAfterLast()) {
            EligibleCouple eligibleCouple = new EligibleCouple(cursor.getString(0),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5),
                    new Gson().<Map<String, String>>fromJson(cursor.getString(7),
                            new TypeToken<Map<String, String>>() {
                            }.getType()));
            eligibleCouple.setIsClosed(Boolean.valueOf(cursor.getString(8)));
            if (Boolean.valueOf(cursor.getString(6))) {
                eligibleCouple.asOutOfArea();
            }
            eligibleCouple.withPhotoPath(cursor.getString(9));
            eligibleCouples.add(eligibleCouple);
            cursor.moveToNext();
        }
        cursor.close();
        return eligibleCouples;
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }

    public long fpCount() {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(
                format("SELECT details FROM {0} WHERE {1} = ? and" + " {2} = ?",
                        EC_TABLE_NAME, IS_OUT_OF_AREA_COLUMN, IS_CLOSED_COLUMN)
                , new String[]{IN_AREA, NOT_CLOSED});
        List<Map<String, String>> detailsList = readDetailsList(cursor);
        return getECsUsingFPMethod(detailsList);
    }

    private long getECsUsingFPMethod(List<Map<String, String>> detailsList) {
        long fpCount = 0;
        for (Map<String, String> details : detailsList) {
            if (!(isBlank(details.get(AllConstants.ECRegistrationFields.CURRENT_FP_METHOD))
                    || "none".equalsIgnoreCase(
                    details.get(AllConstants.ECRegistrationFields.CURRENT_FP_METHOD)))) {
                fpCount++;
            }
        }
        return fpCount;
    }

    private List<Map<String, String>> readDetailsList(Cursor cursor) {
        cursor.moveToFirst();
        List<Map<String, String>> detailsList = new ArrayList<Map<String, String>>();
        while (!cursor.isAfterLast()) {
            String detailsJSON = cursor.getString(0);
            detailsList.add(new Gson().<Map<String, String>>fromJson(detailsJSON,
                    new TypeToken<HashMap<String, String>>() {
                    }.getType()));
            cursor.moveToNext();
        }
        cursor.close();
        return detailsList;
    }
}
