package org.smartregister.repository;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationTag;
import org.smartregister.domain.LocationTest;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.domain.LocationTest.stripTimezone;
import static org.smartregister.repository.BaseRepository.TYPE_Unsynced;
import static org.smartregister.repository.LocationRepository.LOCATION_TABLE;

/**
 * Created by samuelgithengi on 11/26/18.
 */

public class LocationRepositoryTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private LocationRepository locationRepository;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Captor
    private ArgumentCaptor<ContentValues> contentValuesArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<String[]> argsCaptor;

    private String locationJson = LocationTest.parentJson;

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HHmm")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

    @Before
    public void setUp() {
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        locationRepository = new LocationRepository();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testAddOrUpdateShouldAdd() {
        Location location = gson.fromJson(locationJson, Location.class);
        locationRepository.addOrUpdate(location);

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());
        assertEquals(2, stringArgumentCaptor.getAllValues().size());

        Iterator<String> iterator = stringArgumentCaptor.getAllValues().iterator();
        assertEquals(LOCATION_TABLE, iterator.next());
        assertNull(iterator.next());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(6, contentValues.size());

        assertEquals("3734", contentValues.getAsString("_id"));
        assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", contentValues.getAsString("uuid"));
        assertEquals("21", contentValues.getAsString("parent_id"));
        assertEquals(locationJson, stripTimezone(contentValues.getAsString("geojson")));
        assertTrue(contentValues.containsKey("sync_status"));
    }

    @Test
    public void testAddOrUpdateWithSyncStatus() {
        Location location = gson.fromJson(locationJson, Location.class);
        location.setSyncStatus(TYPE_Unsynced);
        locationRepository.addOrUpdate(location);

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());
        assertEquals(2, stringArgumentCaptor.getAllValues().size());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(6, contentValues.size());

        assertEquals(TYPE_Unsynced, contentValues.getAsString("sync_status"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOrUpdateShouldThrowException() {
        Location location = new Location();
        locationRepository.addOrUpdate(location);
    }

    @Test
    public void tesGetAllLocations() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM location", null)).thenReturn(getCursor());
        List<Location> allLocations = locationRepository.getAllLocations();
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM location", stringArgumentCaptor.getValue());

        assertEquals(1, allLocations.size());
        Location location = allLocations.get(0);
        assertEquals(locationJson, stripTimezone(gson.toJson(location)));
    }

    @Test
    public void tesGetAllLocationIds() {
        when(sqLiteDatabase.rawQuery("SELECT _id FROM location", null)).thenReturn(getCursor());
        List<String> allLocationIds = locationRepository.getAllLocationIds();
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT _id FROM location", stringArgumentCaptor.getValue());
        assertEquals(1, allLocationIds.size());
        assertEquals("3734", allLocationIds.get(0));

        when(sqLiteDatabase.rawQuery("SELECT * FROM location", null)).thenReturn(getCursor());
        List<Location> allLocations = locationRepository.getAllLocations();
        assertEquals(1, allLocations.size());
        assertEquals(allLocationIds.get(0), allLocations.get(0).getId());
    }

    @Test
    public void tesGetLocationsByParentId() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM location WHERE parent_id =?", new String[]{"21"})).thenReturn(getCursor());
        List<Location> allLocations = locationRepository.getLocationsByParentId("21");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM location WHERE parent_id =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("21", argsCaptor.getValue()[0]);

        assertEquals(1, allLocations.size());
        Location location = allLocations.get(0);
        assertEquals(locationJson, stripTimezone(gson.toJson(location)));
    }

    @Test
    public void tesGetLocationById() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM location WHERE _id =?", new String[]{"3734"})).thenReturn(getCursor());
        Location location = locationRepository.getLocationById("3734");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM location WHERE _id =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("3734", argsCaptor.getValue()[0]);

        assertEquals(locationJson, stripTimezone(gson.toJson(location)));
    }

    @Test
    public void tesGetLocationByUUID() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM location WHERE uuid =?",
                new String[]{"41587456-b7c8-4c4e-b433-23a786f742fc"})).thenReturn(getCursor());
        Location location = locationRepository.getLocationByUUId("41587456-b7c8-4c4e-b433-23a786f742fc");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM location WHERE uuid =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", argsCaptor.getValue()[0]);

        assertEquals(locationJson, stripTimezone(gson.toJson(location)));
    }

    @Test
    public void tesGetLocationByName() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM location WHERE name =?",
                new String[]{"01_5"})).thenReturn(getCursor());
        Location location = locationRepository.getLocationByName("01_5");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM location WHERE name =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("01_5", argsCaptor.getValue()[0]);

        assertEquals(locationJson, stripTimezone(gson.toJson(location)));
    }

    @Test
    public void tesGetLocationsByIds() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM location WHERE _id IN (?)",
                new String[]{"3734"})).thenReturn(getCursor());
        List<Location> locations = locationRepository.getLocationsByIds(Collections.singletonList("3734"));
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM location WHERE _id IN (?)", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("3734", argsCaptor.getValue()[0]);

        assertNotNull(locations);
        assertEquals(locationJson, stripTimezone(gson.toJson(locations.get(0))));
    }

    @Test
    public void tesGetLocationsByIdsExclusive() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM location WHERE _id NOT IN (?)",
                new Object[]{"3734"})).thenReturn(getCursor());
        List<Location> locations = locationRepository.getLocationsByIds(Collections.singletonList("3734"), false);
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM location WHERE _id NOT IN (?)", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("3734", argsCaptor.getValue()[0]);

        assertEquals(0, locations.size());
    }

    @Test
    public void testGetLocationsByTagName() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM location_tag WHERE name =?", new String[]{"Facility"})).thenReturn(getLocationTagsCursor());
        when(sqLiteDatabase.rawQuery("SELECT * FROM location WHERE _id IN (?)", new String[]{"1"})).thenReturn(getCursor());

        List<Location> tags = locationRepository.getLocationsByTagName("Facility");
        assertNotNull(tags);
        assertEquals(1, tags.size());
    }

    public MatrixCursor getCursor() {
        MatrixCursor cursor = new MatrixCursor(LocationRepository.COLUMNS);
        Location location = gson.fromJson(locationJson, Location.class);
        cursor.addRow(new Object[]{location.getId(), location.getProperties().getUid(),
                location.getProperties().getParentId(), location.getProperties().getName(), locationJson});
        return cursor;
    }

    public MatrixCursor getLocationTagsCursor() {
        String locationTagJson = "{\"name\":\"Facility\",\"locationId\":\"1\"}";
        MatrixCursor cursor = new MatrixCursor(LocationTagRepository.COLUMNS);
        LocationTag locationTag = gson.fromJson(locationTagJson, LocationTag.class);
        cursor.addRow(new Object[]{locationTag.getName(), locationTag.getLocationId()});
        return cursor;
    }
}
