package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.smartregister.repository.EligibleCoupleRepository.EC_TABLE_COLUMNS;
import static org.smartregister.repository.EligibleCoupleRepository.EC_TABLE_NAME;
import static org.smartregister.repository.EligibleCoupleRepository.IS_OUT_OF_AREA_COLUMN;

public class MotherRepository extends DrishtiRepository {
    public static final String MOTHER_TABLE_NAME = "mother";
    public static final String ID_COLUMN = "id";
    public static final String EC_CASEID_COLUMN = "ecCaseId";
    public static final String THAYI_CARD_NUMBER_COLUMN = "thayiCardNumber";
    public static final String REF_DATE_COLUMN = "referenceDate";
    public static final String DETAILS_COLUMN = "details";
    public static final String TYPE_ANC = "ANC";
    public static final String TYPE_PNC = "PNC";
    private static final String MOTHER_SQL = "CREATE TABLE mother(id VARCHAR PRIMARY KEY, "
            + "ecCaseId VARCHAR, thayiCardNumber VARCHAR, type VARCHAR, referenceDate VARCHAR, "
            + "details VARCHAR, isClosed VARCHAR)";
    private static final String MOTHER_TYPE_INDEX_SQL =
            "CREATE INDEX mother_type_index ON " + "mother" + "(type);";
    private static final String MOTHER_REFERENCE_DATE_INDEX_SQL =
            "CREATE INDEX " + "mother_referenceDate_index ON mother(referenceDate);";
    private static final String TYPE_COLUMN = "type";
    private static final String IS_CLOSED_COLUMN = "isClosed";
    public static final String[] MOTHER_TABLE_COLUMNS = {ID_COLUMN, EC_CASEID_COLUMN,
            THAYI_CARD_NUMBER_COLUMN, TYPE_COLUMN, REF_DATE_COLUMN, DETAILS_COLUMN,
            IS_CLOSED_COLUMN};
    private static final String NOT_CLOSED = "false";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(MOTHER_SQL);
        database.execSQL(MOTHER_TYPE_INDEX_SQL);
        database.execSQL(MOTHER_REFERENCE_DATE_INDEX_SQL);
    }

    public void add(Mother mother) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.insert(MOTHER_TABLE_NAME, null, createValuesFor(mother, TYPE_ANC));
    }

    public void switchToPNC(String caseId) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();

        ContentValues motherValuesToBeUpdated = new ContentValues();
        motherValuesToBeUpdated.put(TYPE_COLUMN, TYPE_PNC);

        database.update(MOTHER_TABLE_NAME, motherValuesToBeUpdated, ID_COLUMN + " = ?",
                new String[]{caseId});
    }

    public List<Mother> allANCs() {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(MOTHER_TABLE_NAME, MOTHER_TABLE_COLUMNS,
                TYPE_COLUMN + " = ? AND " + IS_CLOSED_COLUMN + " = ?",
                new String[]{TYPE_ANC, NOT_CLOSED}, null, null, null, null);
        return readAll(cursor);
    }

    public Mother findById(String entityId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database
                .query(MOTHER_TABLE_NAME, MOTHER_TABLE_COLUMNS, ID_COLUMN + " = ?",
                        new String[]{entityId}, null, null, null, null);
        return readAll(cursor).get(0);
    }

    public List<Mother> allPNCs() {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(MOTHER_TABLE_NAME, MOTHER_TABLE_COLUMNS,
                TYPE_COLUMN + " =" + " ? AND " + IS_CLOSED_COLUMN + " = ?",
                new String[]{TYPE_PNC, NOT_CLOSED}, null, null, null, null);
        return readAll(cursor);
    }

    public long ancCount() {
        return longForQuery(masterRepository().getReadableDatabase(),
                "SELECT COUNT(1) FROM " + MOTHER_TABLE_NAME + " WHERE " + TYPE_COLUMN + " = ? AND "
                        + IS_CLOSED_COLUMN + " " + "= ?", new String[]{TYPE_ANC, NOT_CLOSED});
    }

    public long pncCount() {
        return longForQuery(masterRepository().getReadableDatabase(),
                "SELECT COUNT(1) FROM " + MOTHER_TABLE_NAME + " WHERE " + TYPE_COLUMN + " = ? AND "
                        + IS_CLOSED_COLUMN + "= ?", new String[]{TYPE_PNC, NOT_CLOSED});
    }

    public Mother findOpenCaseByCaseID(String caseId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(MOTHER_TABLE_NAME, MOTHER_TABLE_COLUMNS,
                ID_COLUMN + " = ? AND " + IS_CLOSED_COLUMN + " = ?",
                new String[]{caseId, NOT_CLOSED}, null, null, null, null);
        List<Mother> mothers = readAll(cursor);

        if (mothers.isEmpty()) {
            return null;
        }
        return mothers.get(0);
    }

    public List<Mother> findAllCasesForEC(String ecCaseId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database
                .query(MOTHER_TABLE_NAME, MOTHER_TABLE_COLUMNS, EC_CASEID_COLUMN + " = ?",
                        new String[]{ecCaseId}, null, null, null, null);
        return readAll(cursor);
    }

    public List<Mother> findByCaseIds(String... caseIds) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(
                String.format("SELECT * FROM %s WHERE %s IN (%s)", MOTHER_TABLE_NAME, ID_COLUMN,
                        insertPlaceholdersForInClause(caseIds.length)), caseIds);
        return readAll(cursor);
    }

    public List<Pair<Mother, EligibleCouple>> allMothersOfATypeWithEC(String type) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT " + tableColumnsForQuery(MOTHER_TABLE_NAME, MOTHER_TABLE_COLUMNS) + ", "
                        + tableColumnsForQuery(EC_TABLE_NAME, EC_TABLE_COLUMNS) + " FROM "
                        + MOTHER_TABLE_NAME + ", " + EC_TABLE_NAME + " WHERE" + " " + TYPE_COLUMN
                        + "=? AND " + MOTHER_TABLE_NAME + "." + IS_CLOSED_COLUMN
                        + "= ? AND " + MOTHER_TABLE_NAME + "." + EC_CASEID_COLUMN
                        + " = " + EC_TABLE_NAME + "." + EligibleCoupleRepository.ID_COLUMN, new String[]{type,NOT_CLOSED});
        return readAllMothersWithEC(cursor);
    }

    public void closeAllCasesForEC(String ecCaseId) {
        List<Mother> mothers = findAllCasesForEC(ecCaseId);
        for (Mother mother : mothers) {
            close(mother.caseId());
        }
    }

    public void close(String caseId) {
        ContentValues values = new ContentValues();
        values.put(IS_CLOSED_COLUMN, TRUE.toString());
        masterRepository().getWritableDatabase()
                .update(MOTHER_TABLE_NAME, values, ID_COLUMN + " = " + "?", new String[]{caseId});
    }

    private ContentValues createValuesFor(Mother mother, String type) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, mother.caseId());
        values.put(EC_CASEID_COLUMN, mother.ecCaseId());
        values.put(THAYI_CARD_NUMBER_COLUMN, mother.thayiCardNumber());
        values.put(TYPE_COLUMN, type);
        values.put(REF_DATE_COLUMN, mother.referenceDate());
        values.put(DETAILS_COLUMN, new Gson().toJson(mother.details()));
        values.put(IS_CLOSED_COLUMN, Boolean.toString(mother.isClosed()));
        return values;
    }

    private List<Mother> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<Mother> mothers = new ArrayList<Mother>();
        while (!cursor.isAfterLast()) {
            Map<String, String> details = new Gson()
                    .fromJson(cursor.getString(5), new TypeToken<Map<String, String>>() {
                    }.getType());

            mothers.add(new Mother(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(4)).withDetails(details)
                    .setIsClosed(Boolean.valueOf(cursor.getString(6)))
                    .withType(cursor.getString(cursor.getColumnIndex(TYPE_COLUMN))));
            cursor.moveToNext();
        }
        cursor.close();
        return mothers;
    }

    private List<Pair<Mother, EligibleCouple>> readAllMothersWithEC(Cursor cursor) {
        cursor.moveToFirst();
        List<Pair<Mother, EligibleCouple>> ancsWithEC = new ArrayList<Pair<Mother,
                EligibleCouple>>();
        while (!cursor.isAfterLast()) {
            Mother mother = new Mother(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(4))
                    .withType(cursor.getString(cursor.getColumnIndex(TYPE_COLUMN))).withDetails(
                            new Gson().<Map<String, String>>fromJson(cursor.getString(5),
                                    new TypeToken<Map<String, String>>() {
                                    }.getType()));
            EligibleCouple eligibleCouple = new EligibleCouple(cursor.getString(7),
                    cursor.getString(8), cursor.getString(9), cursor.getString(10),
                    cursor.getString(11), cursor.getString(12),
                    new Gson().<Map<String, String>>fromJson(cursor.getString(14),
                            new TypeToken<Map<String, String>>() {
                            }.getType())).withPhotoPath(cursor.getString(
                    cursor.getColumnIndex(EligibleCoupleRepository.PHOTO_PATH_COLUMN)));
            if (Boolean.valueOf(cursor.getString(cursor.getColumnIndex(IS_OUT_OF_AREA_COLUMN)))) {
                eligibleCouple.asOutOfArea();
            }

            ancsWithEC.add(Pair.of(mother, eligibleCouple));
            cursor.moveToNext();
        }
        cursor.close();
        return ancsWithEC;
    }

    private String tableColumnsForQuery(String tableName, String[] tableColumns) {
        return join(prepend(tableColumns, tableName + "."), ", ");
    }

    private String[] prepend(String[] input, String textToPrepend) {
        int length = input.length;
        String[] output = new String[length];
        for (int index = 0; index < length; index++) {
            output[index] = textToPrepend + input[index];
        }
        return output;
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }

    public Mother findMotherWithOpenStatusByECId(String ecId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(MOTHER_TABLE_NAME, MOTHER_TABLE_COLUMNS,
                EC_CASEID_COLUMN + " = ? AND " + IS_CLOSED_COLUMN + " = ?",
                new String[]{ecId, NOT_CLOSED}, null, null, null, null);
        List<Mother> mothers = readAll(cursor);
        return mothers.isEmpty() ? null : mothers.get(0);
    }

    public boolean isPregnant(String ecId) {
        return longForQuery(masterRepository().getReadableDatabase(),
                "SELECT COUNT(1) FROM " + MOTHER_TABLE_NAME + " WHERE " + EC_CASEID_COLUMN
                        + " = ? AND " + IS_CLOSED_COLUMN + " = ? AND " + TYPE_COLUMN + "" + " = ?",
                new String[]{ecId, NOT_CLOSED, TYPE_ANC}) > 0;
    }

    public void update(Mother mother) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.update(MOTHER_TABLE_NAME, createValuesFor(mother, TYPE_ANC), ID_COLUMN + " = ?",
                new String[]{mother.caseId()});
    }
}
