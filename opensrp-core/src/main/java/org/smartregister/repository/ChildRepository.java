package org.smartregister.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Child;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.smartregister.repository.EligibleCoupleRepository.EC_TABLE_COLUMNS;
import static org.smartregister.repository.EligibleCoupleRepository.EC_TABLE_NAME;
import static org.smartregister.repository.MotherRepository.MOTHER_TABLE_COLUMNS;
import static org.smartregister.repository.MotherRepository.MOTHER_TABLE_NAME;

public class ChildRepository extends DrishtiRepository {
    public static final String CHILD_TABLE_NAME = "child";
    public static final String PHOTO_PATH_COLUMN = "photoPath";
    public static final String NOT_CLOSED = "false";
    private static final String CHILD_SQL = "CREATE TABLE child(id VARCHAR PRIMARY KEY, "
            + "motherCaseId VARCHAR, thayiCardNumber VARCHAR, dateOfBirth VARCHAR, gender VARCHAR, "
            + "" + "" + "details VARCHAR, isClosed VARCHAR, photoPath VARCHAR)";
    private static final String ID_COLUMN = "id";
    private static final String MOTHER_ID_COLUMN = "motherCaseId";
    private static final String THAYI_CARD_COLUMN = "thayiCardNumber";
    private static final String DATE_OF_BIRTH_COLUMN = "dateOfBirth";
    private static final String GENDER_COLUMN = "gender";
    private static final String DETAILS_COLUMN = "details";
    private static final String IS_CLOSED_COLUMN = "isClosed";
    public static final String[] CHILD_TABLE_COLUMNS = {ID_COLUMN, MOTHER_ID_COLUMN,
            THAYI_CARD_COLUMN, DATE_OF_BIRTH_COLUMN, GENDER_COLUMN, DETAILS_COLUMN,
            IS_CLOSED_COLUMN, PHOTO_PATH_COLUMN};
    private static final int CASE_ID_IDX = 0;
    private static final int MOTHER_CASE_ID_IDX = 1;
    private static final int THAYI_CARD_NUMBER_IDX = 2;
    private static final int DATE_OF_BIRTH_IDX = 3;
    private static final int GENDER_IDX = 4;
    private static final int DETAILS_IDX = 5;

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(CHILD_SQL);
    }

    public void add(Child child) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.insert(CHILD_TABLE_NAME, null, createValuesFor(child));
    }

    public void update(Child child) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.update(CHILD_TABLE_NAME, createValuesFor(child), ID_COLUMN + " = ?",
                new String[]{child.caseId()});
    }

    public List<Child> all() {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database
                .query(CHILD_TABLE_NAME, CHILD_TABLE_COLUMNS, IS_CLOSED_COLUMN + " = ?",
                        new String[]{NOT_CLOSED}, null, null, null, null);
        return readAll(cursor);
    }

    public Child find(String caseId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.query(CHILD_TABLE_NAME, CHILD_TABLE_COLUMNS, ID_COLUMN + " = ?",
                new String[]{caseId}, null, null, null, null);
        List<Child> children = readAll(cursor);

        if (children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }

    public List<Child> findChildrenByCaseIds(String... caseIds) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(
                String.format("SELECT * FROM %s WHERE %s IN (%s)", CHILD_TABLE_NAME, ID_COLUMN,
                        insertPlaceholdersForInClause(caseIds.length)), caseIds);
        return readAll(cursor);
    }

    public void updateDetails(String caseId, Map<String, String> details) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DETAILS_COLUMN, new Gson().toJson(details));
        database.update(CHILD_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public List<Child> findByMotherCaseId(String caseId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database
                .query(CHILD_TABLE_NAME, CHILD_TABLE_COLUMNS, MOTHER_ID_COLUMN + " = ?",
                        new String[]{caseId}, null, null, null, null);
        return readAll(cursor);
    }

    public long count() {
        return longForQuery(masterRepository().getReadableDatabase(),
                "SELECT COUNT(1) FROM " + CHILD_TABLE_NAME + " WHERE " + IS_CLOSED_COLUMN + " = ?",
                new String[]{NOT_CLOSED});
    }

    public void close(String caseId) {
        markAsClosed(caseId);
    }

    public List<Child> allChildrenWithMotherAndEC() {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT " + tableColumnsForQuery(CHILD_TABLE_NAME, CHILD_TABLE_COLUMNS) + ", "
                        + tableColumnsForQuery(MOTHER_TABLE_NAME, MOTHER_TABLE_COLUMNS) + ", "
                        + tableColumnsForQuery(EC_TABLE_NAME, EC_TABLE_COLUMNS) + " FROM "
                        + CHILD_TABLE_NAME + ", " + MOTHER_TABLE_NAME + ", " + EC_TABLE_NAME + " "
                        + "WHERE " + CHILD_TABLE_NAME + "." + IS_CLOSED_COLUMN + "= ? AND "
                        + CHILD_TABLE_NAME + "." + MOTHER_ID_COLUMN + " = "
                        + MOTHER_TABLE_NAME + "." + MotherRepository.ID_COLUMN + " AND "
                        + MOTHER_TABLE_NAME + "." + MotherRepository.EC_CASEID_COLUMN + " = "
                        + EC_TABLE_NAME + "." + EligibleCoupleRepository.ID_COLUMN, new String[]{NOT_CLOSED});
        return readAllChildrenWithMotherAndEC(cursor);
    }

    private String tableColumnsForQuery(String tableName, String[] tableColumns) {
        return StringUtils.join(prepend(tableColumns, tableName), ", ");
    }

    private String[] prepend(String[] input, String tableName) {
        int length = input.length;
        String[] output = new String[length];
        for (int index = 0; index < length; index++) {
            output[index] = tableName + "." + input[index] + " as " + tableName + input[index];
        }
        return output;
    }

    private void markAsClosed(String caseId) {
        ContentValues values = new ContentValues();
        values.put(IS_CLOSED_COLUMN, TRUE.toString());
        masterRepository().getWritableDatabase()
                .update(CHILD_TABLE_NAME, values, ID_COLUMN + " = " + "?", new String[]{caseId});
    }

    private ContentValues createValuesFor(Child child) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, child.caseId());
        values.put(MOTHER_ID_COLUMN, child.motherCaseId());
        values.put(THAYI_CARD_COLUMN, child.thayiCardNumber());
        values.put(DATE_OF_BIRTH_COLUMN, child.dateOfBirth());
        values.put(GENDER_COLUMN, child.gender());
        values.put(DETAILS_COLUMN, new Gson().toJson(child.details()));
        values.put(IS_CLOSED_COLUMN, Boolean.toString(child.isClosed()));
        values.put(PHOTO_PATH_COLUMN, child.photoPath());
        return values;
    }

    private List<Child> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<Child> children = new ArrayList<Child>();
        while (!cursor.isAfterLast()) {
            children.add(new Child(cursor.getString(CASE_ID_IDX),
                    cursor.getString(MOTHER_CASE_ID_IDX),
                    cursor.getString(THAYI_CARD_NUMBER_IDX),
                    cursor.getString(DATE_OF_BIRTH_IDX),
                    cursor.getString(GENDER_IDX),
                    new Gson().<Map<String, String>>fromJson(cursor.getString(
                            DETAILS_IDX), new TypeToken<Map<String, String>>() {
                    }.getType())).setIsClosed(Boolean.valueOf(cursor.getString(6)))
                    .withPhotoPath(cursor.getString(
                            cursor.getColumnIndex(PHOTO_PATH_COLUMN))));
            cursor.moveToNext();
        }
        cursor.close();
        return children;
    }

    private List<Child> readAllChildrenWithMotherAndEC(Cursor cursor) {
        cursor.moveToFirst();
        List<Child> children = new ArrayList<Child>();
        while (!cursor.isAfterLast()) {
            children.add(childFromCursor(cursor).withMother(motherFromCursor(cursor))
                    .withEC(ecFromCursor(cursor)));
            cursor.moveToNext();
        }
        cursor.close();
        return children;
    }

    private EligibleCouple ecFromCursor(Cursor cursor) {
        return new EligibleCouple(
                getColumnValueByAlias(cursor, EC_TABLE_NAME, EligibleCoupleRepository.ID_COLUMN),
                getColumnValueByAlias(cursor, EC_TABLE_NAME,
                        EligibleCoupleRepository.WIFE_NAME_COLUMN),
                getColumnValueByAlias(cursor, EC_TABLE_NAME,
                        EligibleCoupleRepository.HUSBAND_NAME_COLUMN),
                getColumnValueByAlias(cursor, EC_TABLE_NAME,
                        EligibleCoupleRepository.EC_NUMBER_COLUMN),
                getColumnValueByAlias(cursor, EC_TABLE_NAME,
                        EligibleCoupleRepository.VILLAGE_NAME_COLUMN),
                getColumnValueByAlias(cursor, EC_TABLE_NAME,
                        EligibleCoupleRepository.SUBCENTER_NAME_COLUMN),
                new Gson().<Map<String, String>>fromJson(
                        getColumnValueByAlias(cursor, EC_TABLE_NAME,
                                EligibleCoupleRepository.DETAILS_COLUMN),
                        new TypeToken<Map<String, String>>() {
                        }.getType())).withPhotoPath(getColumnValueByAlias(cursor, EC_TABLE_NAME,
                EligibleCoupleRepository.PHOTO_PATH_COLUMN)).withOutOfArea(
                getColumnValueByAlias(cursor, EC_TABLE_NAME,
                        EligibleCoupleRepository.IS_OUT_OF_AREA_COLUMN));
    }

    private Mother motherFromCursor(Cursor cursor) {
        return new Mother(
                getColumnValueByAlias(cursor, MOTHER_TABLE_NAME, MotherRepository.ID_COLUMN),
                getColumnValueByAlias(cursor, MOTHER_TABLE_NAME, MotherRepository.EC_CASEID_COLUMN),
                getColumnValueByAlias(cursor, MOTHER_TABLE_NAME,
                        MotherRepository.THAYI_CARD_NUMBER_COLUMN),
                getColumnValueByAlias(cursor, MOTHER_TABLE_NAME, MotherRepository.REF_DATE_COLUMN))
                .withDetails(new Gson().<Map<String, String>>fromJson(
                        getColumnValueByAlias(cursor, MOTHER_TABLE_NAME,
                                MotherRepository.DETAILS_COLUMN),
                        new TypeToken<Map<String, String>>() {
                        }.getType()));
    }

    private List<Child> readAllChildren(Cursor cursor) {
        cursor.moveToFirst();
        List<Child> children = new ArrayList<Child>();
        while (!cursor.isAfterLast()) {
            children.add(childFromCursor(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return children;
    }

    private Child childFromCursor(Cursor cursor) {
        return new Child(getColumnValueByAlias(cursor, CHILD_TABLE_NAME, ID_COLUMN),
                getColumnValueByAlias(cursor, CHILD_TABLE_NAME, MOTHER_ID_COLUMN),
                getColumnValueByAlias(cursor, CHILD_TABLE_NAME, THAYI_CARD_COLUMN),
                getColumnValueByAlias(cursor, CHILD_TABLE_NAME, DATE_OF_BIRTH_COLUMN),
                getColumnValueByAlias(cursor, CHILD_TABLE_NAME, GENDER_COLUMN),
                new Gson().<Map<String, String>>fromJson(
                        getColumnValueByAlias(cursor, CHILD_TABLE_NAME, DETAILS_COLUMN),
                        new TypeToken<Map<String, String>>() {
                        }.getType())).setIsClosed(
                Boolean.valueOf(getColumnValueByAlias(cursor, CHILD_TABLE_NAME, IS_CLOSED_COLUMN)))
                .withPhotoPath(getColumnValueByAlias(cursor, CHILD_TABLE_NAME, PHOTO_PATH_COLUMN));
    }

    private String getColumnValueByAlias(Cursor cursor, String table, String column) {
        return cursor.getString(cursor.getColumnIndex(table + column));
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }

    public void updatePhotoPath(String caseId, String imagePath) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PHOTO_PATH_COLUMN, imagePath);
        database.update(CHILD_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public List<Child> findAllChildrenByECId(String ecId) {
        SQLiteDatabase database = masterRepository().getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT " + tableColumnsForQuery(CHILD_TABLE_NAME, CHILD_TABLE_COLUMNS) + " FROM "
                        + CHILD_TABLE_NAME + ", " + MOTHER_TABLE_NAME + ", " + EC_TABLE_NAME
                        + " WHERE " + CHILD_TABLE_NAME + "." + IS_CLOSED_COLUMN + "=? AND "
                        + CHILD_TABLE_NAME + "." + MOTHER_ID_COLUMN + " = "
                        + MOTHER_TABLE_NAME + "." + MotherRepository.ID_COLUMN + " AND "
                        + MOTHER_TABLE_NAME + "." + MotherRepository.EC_CASEID_COLUMN + " = "
                        + EC_TABLE_NAME + "." + EligibleCoupleRepository.ID_COLUMN + " AND "
                        + EC_TABLE_NAME + "." + EligibleCoupleRepository.ID_COLUMN + "= ? ",
                new String[]{NOT_CLOSED, ecId});
        return readAllChildren(cursor);
    }

    public void delete(String childId) {
        SQLiteDatabase database = masterRepository().getWritableDatabase();
        database.delete(CHILD_TABLE_NAME, ID_COLUMN + "= ?", new String[]{childId});
    }
}
