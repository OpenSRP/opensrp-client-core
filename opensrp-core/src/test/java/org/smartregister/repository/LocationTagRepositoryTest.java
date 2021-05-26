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
import org.smartregister.domain.LocationTag;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.domain.LocationTest.stripTimezone;
import static org.smartregister.repository.LocationTagRepository.LOCATION_ID;
import static org.smartregister.repository.LocationTagRepository.LOCATION_TAG_TABLE;
import static org.smartregister.repository.LocationTagRepository.NAME;

/**
 * Created by ilakozejumanne on 02/20/20.
 */

public class LocationTagRepositoryTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private LocationTagRepository locationTagRepository;

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

    private String locationTagJson = "{\"name\":\"Facility\",\"locationId\":\"1\"}";

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HHmm")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

    @Before
    public void setUp() {
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        locationTagRepository = new LocationTagRepository();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testAddOrUpdateShouldAdd() {
        LocationTag locationTag = gson.fromJson(locationTagJson, LocationTag.class);
        locationTagRepository.addOrUpdate(locationTag);

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());
        assertEquals(2, stringArgumentCaptor.getAllValues().size());

        Iterator<String> iterator = stringArgumentCaptor.getAllValues().iterator();
        assertEquals(LOCATION_TAG_TABLE, iterator.next());
        assertNull(iterator.next());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(2, contentValues.size());

        assertEquals("Facility", contentValues.getAsString("name"));
        assertEquals("1", contentValues.getAsString("location_id"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOrUpdateShouldThrowException() {
        LocationTag locationTag = new LocationTag();
        locationTagRepository.addOrUpdate(locationTag);
    }

    @Test
    public void tesGetAllLocationTags() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM " + LOCATION_TAG_TABLE, null)).thenReturn(getCursor());
        List<LocationTag> allLocationsTags = locationTagRepository.getAllLocationTags();
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM location_tag", stringArgumentCaptor.getValue());

        assertEquals(1, allLocationsTags.size());
        LocationTag locationTag = allLocationsTags.get(0);
        assertEquals(locationTagJson, stripTimezone(gson.toJson(locationTag)));
    }

    @Test
    public void testGetLocationTagsById() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM " + LOCATION_TAG_TABLE + " WHERE " + LOCATION_ID + " =?", new String[]{"1"})).thenReturn(getCursor());
        List<LocationTag> locationTags = locationTagRepository.getLocationTagByLocationId("1");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM " + LOCATION_TAG_TABLE + " WHERE " + LOCATION_ID + " =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("1", argsCaptor.getValue()[0]);

        assertEquals(locationTagJson, stripTimezone(gson.toJson(locationTags.get(0))));
    }

    @Test
    public void testGetLocationTagsByTagName() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM " + LOCATION_TAG_TABLE + " WHERE " + NAME + " =?", new String[]{"Facility"})).thenReturn(getCursor());
        List<LocationTag> locationTags = locationTagRepository.getLocationTagsByTagName("Facility");
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM " + LOCATION_TAG_TABLE + " WHERE " + NAME + " =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("Facility", argsCaptor.getValue()[0]);
        assertEquals(locationTagJson, stripTimezone(gson.toJson(locationTags.get(0))));
    }

    public MatrixCursor getCursor() {
        MatrixCursor cursor = new MatrixCursor(LocationTagRepository.COLUMNS);
        LocationTag locationTag = gson.fromJson(locationTagJson, LocationTag.class);
        cursor.addRow(new Object[]{locationTag.getName(), locationTag.getLocationId()});
        return cursor;
    }
}
