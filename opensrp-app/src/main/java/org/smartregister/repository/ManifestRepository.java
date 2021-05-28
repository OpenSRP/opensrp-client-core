package org.smartregister.repository;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.Manifest;
import org.smartregister.util.DatabaseMigrationUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by cozej4 on 2020-04-06.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class ManifestRepository extends BaseRepository {

    protected static final String ID = "id";
    protected static final String VERSION = "version";
    protected static final String APP_VERSION = "app_version";
    protected static final String FORM_VERSION = "form_version";
    protected static final String IDENTIFIERS = "identifiers";
    protected static final String IS_NEW = "is_new";
    protected static final String ACTIVE = "active";
    protected static final String CREATED_AT = "created_at";


    protected static final String MANIFEST_TABLE = "manifest";

    protected static final String[] COLUMNS = new String[]{ID, VERSION, APP_VERSION, FORM_VERSION, IDENTIFIERS, IS_NEW, ACTIVE, CREATED_AT};
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private static final String CREATE_MANIFEST_TABLE =
            "CREATE TABLE " + MANIFEST_TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VERSION + " VARCHAR," +
                    APP_VERSION + " VARCHAR , " +
                    FORM_VERSION + " VARCHAR , " +
                    IDENTIFIERS + " VARCHAR , " +
                    IS_NEW + " INTEGER , " +
                    ACTIVE + " INTEGER , " +
                    CREATED_AT + " VARCHAR NOT NULL ) ";

    private static final String CREATE_MANIFEST_APP_VERSION_INDEX = "CREATE INDEX "
            + MANIFEST_TABLE + "_" + APP_VERSION + "_ind ON " + MANIFEST_TABLE + "(" + APP_VERSION + ")";
    private static final String CREATE_MANIFEST_IS_ACTIVE_INDEX = "CREATE INDEX "
            + MANIFEST_TABLE + "_" + ACTIVE + "_ind ON " + MANIFEST_TABLE + "(" + ACTIVE + ")";

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_MANIFEST_TABLE);
        database.execSQL(CREATE_MANIFEST_APP_VERSION_INDEX);
        database.execSQL(CREATE_MANIFEST_IS_ACTIVE_INDEX);
    }

    public static void addVersionColumn(@NonNull SQLiteDatabase database) {
        database.execSQL(String.format("ALTER TABLE %s ADD %s VARCHAR", MANIFEST_TABLE, VERSION));
    }

    public static boolean isVersionColumnExist(@NonNull SQLiteDatabase database) {
        return DatabaseMigrationUtils.isColumnExists(database, MANIFEST_TABLE, VERSION);
    }

    protected String getManifestTableName() {
        return MANIFEST_TABLE;
    }

    public void addOrUpdate(Manifest manifest) {
        ContentValues contentValues = new ContentValues();

        if (manifest.getId() != null)
            contentValues.put(ID, manifest.getId());
        contentValues.put(VERSION, manifest.getVersion());
        contentValues.put(APP_VERSION, manifest.getAppVersion());
        contentValues.put(FORM_VERSION, manifest.getFormVersion());
        contentValues.put(IDENTIFIERS, new Gson().toJson(manifest.getIdentifiers()));
        contentValues.put(IS_NEW, manifest.isNew());
        contentValues.put(ACTIVE, manifest.isActive());
        contentValues.put(CREATED_AT, DATE_FORMAT.format(manifest.getCreatedAt()));

        getWritableDatabase().replace(getManifestTableName(), null, contentValues);
    }

    protected Manifest readCursor(@NonNull Cursor cursor) {
        Manifest manifest = new Manifest();
        manifest.setId(cursor.getString(cursor.getColumnIndex(ID)));
        manifest.setVersion(cursor.getString(cursor.getColumnIndex(VERSION)));
        manifest.setAppVersion(cursor.getString(cursor.getColumnIndex(APP_VERSION)));
        manifest.setFormVersion(cursor.getString(cursor.getColumnIndex(FORM_VERSION)));
        manifest.setNew(cursor.getInt(cursor.getColumnIndex(IS_NEW)) == 1);
        manifest.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE)) == 1);
        manifest.setIdentifiers(new Gson().fromJson(cursor.getString(cursor.getColumnIndex(IDENTIFIERS)),
                new TypeToken<List<String>>() {}.getType()));
        try {
            manifest.setCreatedAt(DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(CREATED_AT))));
        } catch (ParseException e) {
            Timber.e(e);
        }

        return manifest;
    }

    /**
     * Get a list of  of all Manifests in the repository
     *
     * @return a list of all Manifests in the repository
     */
    public List<Manifest> getAllManifests() {
        List<Manifest> manifests = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getManifestTableName() +
                " ORDER BY " + CREATED_AT + " DESC ", null)) {
            while (cursor.moveToNext()) {
                manifests.add(readCursor(cursor));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return manifests;

    }

    /**
     * Get a list of Manifest for the passed appVersion
     *
     * @param appVersion of a the client form
     * @return a list of Manifest for the passed appVersion
     */
    public List<Manifest> getManifestByAppVersion(String appVersion) {
        List<Manifest> manifests = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getManifestTableName() +
                " WHERE " + APP_VERSION + " =?", new String[]{appVersion})) {
            while (cursor.moveToNext()) {
                manifests.add(readCursor(cursor));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return manifests;

    }


    /**
     * Get the active of Manifest
     *
     * @return the manifest tagged as active
     */
    public Manifest getActiveManifest() {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getManifestTableName() +
                " WHERE " + ACTIVE + " =?", new String[]{"1"})) {
            if (cursor.moveToFirst()) {
                return readCursor(cursor);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;

    }

    /**
     * Delete manifest by id
     *
     * @param manifestId
     */
    public void delete(String manifestId) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(MANIFEST_TABLE, ID + "= ?", new String[]{manifestId});
    }
}
