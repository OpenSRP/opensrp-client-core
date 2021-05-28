package org.smartregister.repository;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.Geometry;
import org.smartregister.domain.Location;
import org.smartregister.p2p.sync.data.JsonData;
import org.smartregister.repository.helper.MappingHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.smartregister.AllConstants.ROWID;

import org.smartregister.sync.helper.LocationServiceHelper;
import org.smartregister.util.P2PUtil;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 11/23/18.
 */
public class StructureRepository extends LocationRepository {

    public static String STRUCTURE_TABLE = "structure";
    protected static final String SYNC_STATUS = "sync_status";

    protected static final String LATITUDE = "latitude";
    protected static final String LONGITUDE = "longitude";

    private static final String CREATE_LOCATION_TABLE =
            "CREATE TABLE " + STRUCTURE_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    UUID + " VARCHAR , " +
                    PARENT_ID + " VARCHAR , " +
                    NAME + " VARCHAR , " +
                    SYNC_STATUS + " VARCHAR DEFAULT " + BaseRepository.TYPE_Synced + ", " +
                    LATITUDE + " FLOAT , " +
                    LONGITUDE + " FLOAT , " +
                    GEOJSON + " VARCHAR NOT NULL ) ";

    private static final String CREATE_LOCATION_PARENT_INDEX = "CREATE INDEX "
            + STRUCTURE_TABLE + "_" + PARENT_ID + "_ind ON " + STRUCTURE_TABLE + "(" + PARENT_ID + ")";

    private MappingHelper helper;

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_LOCATION_TABLE);
        database.execSQL(CREATE_LOCATION_PARENT_INDEX);
    }

    @Override
    public void deleteLocations(@NonNull Set<String> locationIdentifiers) {
        throw new UnsupportedOperationException("deleteLocations not supported for Structures");
    }

    @Override
    public List<Location> getAllLocations() {
        throw new UnsupportedOperationException("getAllLocations not supported for Structures");
    }

    @Override
    protected String getLocationTableName() {
        return STRUCTURE_TABLE;
    }

    public List<Location> getAllUnsynchedCreatedStructures() {
        Cursor cursor = null;
        List<Location> structures = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(String.format("SELECT *  FROM %s WHERE %s =?", STRUCTURE_TABLE, SYNC_STATUS), new String[]{BaseRepository.TYPE_Created});
            while (cursor.moveToNext()) {
                structures.add(readCursor(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            Timber.e(e, "EXCEPTION %s", e.toString());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return structures;
    }

    public void markStructuresAsSynced(String structureId) {
        try {
            ContentValues values = new ContentValues();
            values.put(ID, structureId);
            values.put(SYNC_STATUS, BaseRepository.TYPE_Synced);

            getWritableDatabase().update(STRUCTURE_TABLE, values, ID + " = ?", new String[]{structureId});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addOrUpdate(Location location) {
        if (StringUtils.isBlank(location.getId()))
            throw new IllegalArgumentException("id not provided");
        ContentValues contentValues = new ContentValues();

        if (P2PUtil.checkIfExistsById(STRUCTURE_TABLE, location.getId(), getWritableDatabase())) {
            int maxRowId = P2PUtil.getMaxRowId(STRUCTURE_TABLE, getWritableDatabase());
            contentValues.put(ROWID, ++maxRowId);
        }

        contentValues.put(ID, location.getId());
        contentValues.put(UUID, location.getProperties().getUid());
        contentValues.put(PARENT_ID, location.getProperties().getParentId());
        contentValues.put(NAME, location.getProperties().getName());
        contentValues.put(SYNC_STATUS, location.getSyncStatus());
        contentValues.put(GEOJSON, gson.toJson(location));
        if (location.getGeometry().getType().equals(Geometry.GeometryType.POINT)) {
            contentValues.put(LONGITUDE, location.getGeometry().getCoordinates().get(0).getAsFloat());
            contentValues.put(LATITUDE, location.getGeometry().getCoordinates().get(1).getAsFloat());
        } else if (helper != null) {
            android.location.Location center = helper.getCenter(gson.toJson(location.getGeometry()));
            contentValues.put(LATITUDE, center.getLatitude());
            contentValues.put(LONGITUDE, center.getLongitude());
        }
        getWritableDatabase().replace(getLocationTableName(), null, contentValues);

    }

    public void setHelper(MappingHelper helper) {
        this.helper = helper;
    }

    public boolean batchInsertStructures(JSONArray array) {
        if (array == null || array.length() == 0) {
            return false;
        }

        try {
            getWritableDatabase().beginTransaction();

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                Location structure = LocationServiceHelper.locationGson.fromJson(jsonObject.toString(), Location.class);
                addOrUpdate(structure);
            }

            getWritableDatabase().setTransactionSuccessful();
            getWritableDatabase().endTransaction();
            return true;
        } catch (Exception e) {
            Timber.e(e, "EXCEPTION %s", e.toString());
            getWritableDatabase().endTransaction();
            return false;
        }
    }

    /**
     * Fetches {@link Location}s whose rowid > #lastRowId up to the #limit provided.
     *
     * @param lastRowId
     * @param parentLocationId
     * @return JsonData which contains a {@link JSONArray} and the maximum row id in the array
     * of {@link Location}s returned or {@code null} if no records match the conditions or an exception occurred.
     * This enables this method to be called again for the consequent batches
     */
    @Nullable
    public JsonData getStructures(long lastRowId, int limit, String parentLocationId) {
        JsonData jsonData = null;
        long maxRowId = 0;
        String locationFilter = parentLocationId != null ? String.format(" %s =? AND ", PARENT_ID) : "";
        String query = "SELECT "
                + ROWID
                + ",* FROM "
                + STRUCTURE_TABLE
                + " WHERE "
                + locationFilter
                + ROWID
                + " > ? "
                + " ORDER BY " + ROWID + " ASC LIMIT ?";

        Cursor cursor = null;
        JSONArray jsonArray = new JSONArray();

        try {
            cursor = getWritableDatabase().rawQuery(query, parentLocationId != null ? new Object[]{parentLocationId, lastRowId, limit} : new Object[]{lastRowId, limit});


            while (cursor.moveToNext()) {
                long rowId = cursor.getLong(0);

                Location location = readCursor(cursor);
                location.setRowid(cursor.getLong(0));

                JSONObject structureObject = new JSONObject(LocationServiceHelper.locationGson.toJson(location));
                jsonArray.put(structureObject);

                if (rowId > maxRowId) {
                    maxRowId = rowId;
                }
            }
        } catch (Exception e) {
            Timber.e(e, "EXCEPTION %s", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (jsonArray.length() > 0) {
            jsonData = new JsonData(jsonArray, maxRowId);
        }
        return jsonData;
    }

    public int getUnsyncedStructuresCount() {
        Cursor cursor = null;
        int structuresCount = 0;
        try {
            cursor = getReadableDatabase().rawQuery(String.format("SELECT count(*) FROM %s WHERE %s = ?", STRUCTURE_TABLE, SYNC_STATUS), new String[]{BaseRepository.TYPE_Created});
            if (cursor.moveToNext()) {
                structuresCount = cursor.getInt(0);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return structuresCount;
    }
}
