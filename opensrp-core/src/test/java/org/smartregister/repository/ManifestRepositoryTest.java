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
import org.smartregister.domain.Manifest;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.repository.ManifestRepository.ACTIVE;
import static org.smartregister.repository.ManifestRepository.APP_VERSION;
import static org.smartregister.repository.ManifestRepository.CREATED_AT;
import static org.smartregister.repository.ManifestRepository.MANIFEST_TABLE;

/**
 * Created by ilakozejumanne on 2020-04-09.
 */

public class ManifestRepositoryTest extends BaseUnitTest {

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    private ManifestRepository manifestRepository;
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
    private String manifestJson = "{\"id\":\"1\",\"version\": \"0.0.67\",\"appVersion\":\"1.1.0\",\"formVersion\":\"1.0.0\",\"identifiers\":[\"en/child/enrollment.json\"],\"isNew\":true,\"active\":true,\"createdAt\":\"2020-04-09 00:00:00\"}";

    @Before
    public void setUp() {
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        manifestRepository = new ManifestRepository();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testAddOrUpdateShouldAdd() {

        Manifest manifest = gson.fromJson(manifestJson, Manifest.class);
        manifestRepository.addOrUpdate(manifest);

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());
        assertEquals(2, stringArgumentCaptor.getAllValues().size());

        Iterator<String> iterator = stringArgumentCaptor.getAllValues().iterator();
        assertEquals(MANIFEST_TABLE, iterator.next());
        assertNull(iterator.next());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(8, contentValues.size());

        assertEquals("1", contentValues.getAsString(ManifestRepository.ID));
        assertEquals("1.1.0", contentValues.getAsString(APP_VERSION));
        assertEquals("1.0.0", contentValues.getAsString(ManifestRepository.FORM_VERSION));
        assertEquals("[\"en/child/enrollment.json\"]", contentValues.getAsString(ManifestRepository.IDENTIFIERS));
        assertEquals(true, contentValues.getAsBoolean(ACTIVE));
        assertEquals(true, contentValues.getAsBoolean(ManifestRepository.IS_NEW));


    }


    @Test
    public void testGetAllManifests() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM " + MANIFEST_TABLE + " ORDER BY " + CREATED_AT + " DESC ", null)).thenReturn(getCursor());
        List<Manifest> manifests = manifestRepository.getAllManifests();
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM manifest ORDER BY created_at DESC ", stringArgumentCaptor.getValue());

        assertEquals(1, manifests.size());
        Manifest manifest = manifests.get(0);
        assertEquals("1", manifest.getId());

    }

    @Test
    public void testGetManifestByAppVersion() {
        String appVersion = "1.1.0";
        when(sqLiteDatabase.rawQuery("SELECT * FROM " + MANIFEST_TABLE +
                " WHERE " + APP_VERSION + " =?", new String[]{appVersion})).thenReturn(getCursor());
        List<Manifest> manifests = manifestRepository.getManifestByAppVersion(appVersion);
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM " + MANIFEST_TABLE + " WHERE " + APP_VERSION + " =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals(appVersion, argsCaptor.getValue()[0]);


        Manifest manifest = manifests.get(0);
        assertEquals("1", manifest.getId());

    }

    @Test
    public void testGetActiveManifest() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM " + MANIFEST_TABLE +
                " WHERE " + ACTIVE + " =?", new String[]{"1"})).thenReturn(getCursor());
        Manifest manifest = manifestRepository.getActiveManifest();
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM " + MANIFEST_TABLE + " WHERE " + ACTIVE + " =?", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals("1", argsCaptor.getValue()[0]);


        assertEquals(true, manifest.isActive());

    }


    public MatrixCursor getCursor() {
        MatrixCursor cursor = new MatrixCursor(ManifestRepository.COLUMNS);
        Manifest manifest = gson.fromJson(manifestJson, Manifest.class);
        cursor.addRow(new Object[]{manifest.getId(), manifest.getVersion(), manifest.getAppVersion(), manifest.getFormVersion(), new Gson().toJson(manifest.getIdentifiers()), manifest.isNew() ? 1 : 0, manifest.isActive() ? 1 : 0, manifest.getCreatedAt()});
        return cursor;
    }

}
