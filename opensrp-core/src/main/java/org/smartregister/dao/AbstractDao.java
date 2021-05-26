package org.smartregister.dao;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class AbstractDao {

    private static SimpleDateFormat DOB_DATE_FORMAT;
    private static SimpleDateFormat NATIVE_FORMS_DATE_FORMAT;
    private static Repository repository;

    public static SimpleDateFormat getDobDateFormat() {
        if (DOB_DATE_FORMAT == null)
            DOB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        return DOB_DATE_FORMAT;
    }

    public static SimpleDateFormat getNativeFormsDateFormat() {
        if (NATIVE_FORMS_DATE_FORMAT == null)
            NATIVE_FORMS_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        return NATIVE_FORMS_DATE_FORMAT;
    }

    protected static Repository getRepository() {
        if (repository == null)
            repository = DrishtiApplication.getInstance().getRepository();

        return repository;
    }

    protected static void setRepository(Repository repository) {
        AbstractDao.repository = repository;
    }

    protected static void updateDB(String sql) {
        try {
            SQLiteDatabase db = getRepository().getWritableDatabase();
            db.rawExecSQL(sql);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Returns a mapped pojo by reading the sqlite adapter
     * handles iteration and cursor disposable
     *
     * @param query
     * @param dataMap
     * @param <T>
     * @return
     */
    protected static <T> List<T> readData(String query, DataMap<T> dataMap) {
        SQLiteDatabase db = getRepository().getReadableDatabase();
        return readData(query, dataMap, db);
    }

    @Nullable
    protected static <T> T readSingleValue(String query, DataMap<T> dataMap, SQLiteDatabase db) {
        List<T> tList = readData(query, dataMap, db);
        if (tList == null || tList.size() == 0)
            return null;

        return tList.get(0);
    }

    protected static <T> T readSingleValue(String query, DataMap<T> dataMap, SQLiteDatabase db, T defaultValue) {
        T res = readSingleValue(query, dataMap, db);
        return res == null ? defaultValue : res;
    }

    @Nullable
    protected static <T> T readSingleValue(String query, DataMap<T> dataMap) {
        return readSingleValue(query, dataMap, getRepository().getReadableDatabase());
    }

    protected static <T> T readSingleValue(String query, DataMap<T> dataMap, T defaultValue) {
        T res = readSingleValue(query, dataMap, getRepository().getReadableDatabase());
        return res == null ? defaultValue : res;
    }

    protected @Nullable
    static <T> List<T> readData(String query, DataMap<T> dataMap, SQLiteDatabase db) {
        Cursor cursor = null;
        List<T> list = new ArrayList<>();
        try {
            cursor = db.rawQuery(query, new String[]{});
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    list.add(dataMap.readCursor(cursor));
                }
            }
        } catch (Exception e) {
            Timber.e(e);
            list = null;
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    /**
     * Reads the results of a raw query and returns a list object representing all the table
     * rows and a map representing the columns
     *
     * @param query
     * @return
     */
    public static @Nullable List<Map<String, Object>> readData(String query, String[] selectionArgs) {
        List<Map<String, Object>> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getRepository().getReadableDatabase().rawQuery(query, selectionArgs);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Map<String, Object> res = new HashMap<>();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        Object result;
                        switch (cursor.getType(i)) {
                            case Cursor.FIELD_TYPE_NULL:
                                result = null;
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                result = getCursorLongValue(cursor, cursor.getColumnName(i));
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                result = getCursorIntValue(cursor, cursor.getColumnName(i));
                                break;
                            default:
                                result = getCursorValue(cursor, i);
                                break;
                        }

                        res.put(cursor.getColumnName(i), result);
                    }
                    list.add(res);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
            list = null;
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    @Nullable
    protected static String getCursorValue(Cursor c, int column_index) {
        return c.getType(column_index) == Cursor.FIELD_TYPE_NULL ? null : c.getString(column_index);
    }

    @Nullable
    protected static String getCursorValue(Cursor c, String column_name) {
        int column_index = c.getColumnIndex(column_name);
        if (column_index < 0)
            return null;

        return c.getType(column_index) == Cursor.FIELD_TYPE_NULL ? null : c.getString(column_index);
    }

    protected static String getCursorValue(Cursor c, String column_name, String defaultValue) {
        int column_index = c.getColumnIndex(column_name);
        if (column_index < 0)
            return defaultValue;

        return c.getType(column_index) == Cursor.FIELD_TYPE_NULL ? defaultValue : c.getString(column_index);
    }

    @Nullable
    protected static Long getCursorLongValue(Cursor c, String column_name) {
        int column_index = c.getColumnIndex(column_name);
        if (column_index < 0)
            return null;

        return c.getType(column_index) == Cursor.FIELD_TYPE_NULL ? null : c.getLong(column_index);
    }

    @Nullable
    protected static Integer getCursorIntValue(Cursor c, String column_name) {
        int column_index = c.getColumnIndex(column_name);
        if (column_index < 0)
            return null;

        return c.getType(column_index) == Cursor.FIELD_TYPE_NULL ? null : c.getInt(column_index);
    }

    protected static Integer getCursorIntValue(Cursor c, String column_name, int defaultValue) {
        Integer res = getCursorIntValue(c, column_name);
        return res == null ? defaultValue : res;
    }

    @Nullable
    protected static Date getCursorValueAsDate(Cursor c, String column_name, SimpleDateFormat formatter) {
        String value = getCursorValue(c, column_name);
        if (StringUtils.isBlank(value))
            return null;

        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            Timber.e(e);
            return null;
        }
    }

    @Nullable
    protected static Date getCursorValueAsDate(Cursor c, String column_name) {
        String value = getCursorValue(c, column_name);
        if (StringUtils.isBlank(value))
            return null;

        return new Date(Long.parseLong(value));
    }

    public interface DataMap<T> {
        T readCursor(Cursor cursor);
    }
}
