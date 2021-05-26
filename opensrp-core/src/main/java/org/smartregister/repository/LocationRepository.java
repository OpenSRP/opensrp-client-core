package org.smartregister.repository;

import android.content.ContentValues;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.LocationTag;
import org.smartregister.domain.PhysicalLocation;
import org.smartregister.pathevaluator.dao.LocationDao;
import org.smartregister.util.PropertiesConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 11/23/18.
 */
public class LocationRepository extends BaseRepository {

    protected static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HHmm")
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();

    protected static final String ID = "_id";
    protected static final String UUID = "uuid";
    protected static final String PARENT_ID = "parent_id";
    protected static final String NAME = "name";
    protected static final String GEOJSON = "geojson";
    protected static final String SYNC_STATUS = "sync_status";

    protected static final String LOCATION_TABLE = "location";

    protected static final String[] COLUMNS = new String[]{ID, UUID, PARENT_ID, NAME, GEOJSON};

    private static final String CREATE_LOCATION_TABLE =
            "CREATE TABLE " + LOCATION_TABLE + " (" +
                    ID + " VARCHAR NOT NULL PRIMARY KEY," +
                    UUID + " VARCHAR , " +
                    PARENT_ID + " VARCHAR , " +
                    NAME + " VARCHAR , " +
                    SYNC_STATUS + " VARCHAR DEFAULT " + BaseRepository.TYPE_Synced + ", " +
                    GEOJSON + " VARCHAR NOT NULL ) ";

    private static final String CREATE_LOCATION_NAME_INDEX = "CREATE INDEX "
            + LOCATION_TABLE + "_" + NAME + "_ind ON " + LOCATION_TABLE + "(" + NAME + ")";

    protected String getLocationTableName() {
        return LOCATION_TABLE;
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_LOCATION_TABLE);
        database.execSQL(CREATE_LOCATION_NAME_INDEX);
    }

    public void addOrUpdate(Location location) {
        if (StringUtils.isBlank(location.getId()))
            throw new IllegalArgumentException("id not provided");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, location.getId());
        contentValues.put(UUID, location.getProperties().getUid());
        contentValues.put(PARENT_ID, location.getProperties().getParentId());
        contentValues.put(NAME, location.getProperties().getName());
        contentValues.put(GEOJSON, gson.toJson(location));
        contentValues.put(SYNC_STATUS, location.getSyncStatus());
        getWritableDatabase().replace(getLocationTableName(), null, contentValues);
    }

    /**
     * Deletes jurisdiction locations which a user no longer has access to
     *
     * @param locationIdentifiers the set of jurisdiction identifiers to delete
     */
    public void deleteLocations(@NonNull Set<String> locationIdentifiers) {
        getWritableDatabase().delete(LOCATION_TABLE,
                String.format("%s IN (%s)", ID, StringUtils.repeat("?", ",", locationIdentifiers.size())),
                locationIdentifiers.toArray(new String[]{}));
    }

    public List<Location> getAllLocations() {
        Cursor cursor = null;
        List<Location> locations = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getLocationTableName(), null);
            while (cursor.moveToNext()) {
                locations.add(readCursor(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locations;
    }

    public List<String> getAllLocationIds() {
        Cursor cursor = null;
        List<String> locationIds = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery("SELECT " + ID + " FROM " + getLocationTableName(), null);
            while (cursor.moveToNext()) {
                locationIds.add(cursor.getString(0));
            }
            cursor.close();
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locationIds;
    }

    public Location getLocationById(String id) {
        return getLocationById(id, getLocationTableName());
    }

    public Location getLocationById(String id, String tableName) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + tableName +
                    " WHERE " + ID + " =?", new String[]{id});
            if (cursor.moveToFirst()) {
                return readCursor(cursor);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;

    }

    public Location getLocationByUUId(String uuid) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getLocationTableName() +
                    " WHERE " + UUID + " =?", new String[]{uuid});
            if (cursor.moveToFirst()) {
                return readCursor(cursor);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;

    }

    public List<Location> getLocationsByParentId(String parentId) {
        return getLocationsByParentId(parentId, getLocationTableName());
    }

    public List<Location> getLocationsByParentId(String parentId, String tableName) {
        Cursor cursor = null;
        List<Location> locations = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + tableName +
                    " WHERE " + PARENT_ID + " =?", new String[]{parentId});
            while (cursor.moveToNext()) {
                locations.add(readCursor(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locations;
    }

    public Location getLocationByName(String name) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getLocationTableName() +
                    " WHERE " + NAME + " =?", new String[]{name});
            if (cursor.moveToFirst()) {
                return readCursor(cursor);
            }
            cursor.close();
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Get a List of locations that match the list of ids provided
     *
     * @param ids list of location ids
     * @return the list of locations that match the params provided
     */
    public List<Location> getLocationsByIds(List<String> ids) {
        return getLocationsByIds(ids, true);
    }

    /**
     * Get a List of locations that either match or don't match the list of ids provided
     * depending on value of the inclusive flag
     *
     * @param ids       list of location ids
     * @param inclusive flag that determines whether the list of locations returned
     *                  should include the locations whose ids match the params provided
     *                  or exclude them
     * @return
     */
    public List<Location> getLocationsByIds(List<String> ids, Boolean inclusive) {
        Cursor cursor = null;
        List<Location> locations = new ArrayList<>();
        int idCount = ids != null ? ids.size() : 0;
        String[] idsArray = ids != null ? ids.toArray(new String[0]) : null;

        String operator = inclusive != null && inclusive ? "IN" : "NOT IN";

        String selectSql = String.format("SELECT * FROM " + getLocationTableName() +
                " WHERE " + ID + " " + operator + " (%s)", insertPlaceholdersForInClause(idCount));

        try {
            cursor = getReadableDatabase().rawQuery(selectSql, idsArray);
            while (cursor.moveToNext()) {
                locations.add(readCursor(cursor));
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locations;
    }

    protected Location readCursor(Cursor cursor) {
        String geoJson = cursor.getString(cursor.getColumnIndex(GEOJSON));
        return gson.fromJson(geoJson, Location.class);
    }

    public List<Location> getAllUnsynchedLocation() {
        Cursor cursor = null;
        List<Location> locations = new ArrayList<>();
        try {
            cursor = getReadableDatabase().rawQuery(String.format("SELECT *  FROM %s WHERE %s =?", LOCATION_TABLE, SYNC_STATUS), new String[]{BaseRepository.TYPE_Unsynced});
            while (cursor.moveToNext()) {
                locations.add(readCursor(cursor));
            }
            cursor.close();
        } catch (Exception e) {
            Timber.e(e, "EXCEPTION %s", e.toString());
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return locations;
    }

    public void markLocationsAsSynced(String locationId) {
        try {
            ContentValues values = new ContentValues();
            values.put(ID, locationId);
            values.put(SYNC_STATUS, BaseRepository.TYPE_Synced);

            getWritableDatabase().update(LOCATION_TABLE, values, ID + " = ?", new String[]{locationId});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a List of locations that match provided tag name
     *
     * @param tagName Tag name
     * @return List of locations
     */
    public List<Location> getLocationsByTagName(String tagName) {
        LocationTagRepository locationTagRepository = new LocationTagRepository();
        List<LocationTag> locationTags = locationTagRepository.getLocationTagsByTagName(tagName);

        List<String> locationIds = locationTags.stream()
                .map(LocationTag::getLocationId)
                .collect(Collectors.toList());

        return getLocationsByIds(locationIds);
    }

}
