package org.smartregister.repository;

import android.content.ContentValues;
import android.database.SQLException;

import androidx.test.core.app.ApplicationProvider;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.UniqueId;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Date;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by ndegwamartin on 2019-12-02.
 */
public class UniqueIdRepositoryTest extends BaseRobolectricUnitTest {
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

    private UniqueIdRepository uniqueIdRepository;

    private static final String testUsername = "testUser1";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()));
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSharedPreferences", allSharedPreferences);

        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);

        uniqueIdRepository = new UniqueIdRepository();
    }

    @After
    public void tearDown() {
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", (Repository) null);
    }

    @Test
    public void testUpdateOpenMRSIdentifierStatusInvokesDatabaseUpdateMethodCorrectlyWithNoHyphen() {
        String openMrsId = "329893823";

        uniqueIdRepository.close(openMrsId);
        verify(sqLiteDatabase, times(2)).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());
    }

    @Test
    public void testUpdateOpenMRSIdentifierStatusInvokesDatabaseUpdateMethodCorrectlyWithHyphen() {
        String openMrsId = "32989382-3";

        uniqueIdRepository.close(openMrsId);
        verify(sqLiteDatabase, times(2)).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());
    }

    @Test
    public void testUpdateOpenMRSIdentifierStatusInvokesDatabaseUpdateMethodOnceIfRowUpdated() {
        CoreLibrary.getInstance().context().allSharedPreferences().updateANMUserName(testUsername);
        String openMrsId = "3298938-2";

        doReturn(1).when(sqLiteDatabase).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());

        uniqueIdRepository.close(openMrsId);
        verify(sqLiteDatabase).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());

        Assert.assertNotNull(stringArgumentCaptor.getValue());
        assertEquals("unique_ids", stringArgumentCaptor.getAllValues().get(2));
        assertEquals(openMrsId, argsCaptor.getValue()[0]);

        ContentValues values = contentValuesArgumentCaptor.getValue();
        assertEquals(testUsername, values.getAsString("used_by"));
    }

    @Test
    public void testBulkInsertOpenMrsIds() {
        CoreLibrary.getInstance().context().allSharedPreferences().updateANMUserName(testUsername);
        String openMrsId = "3298938-2";
        List<String> openMrsIds = Collections.singletonList(openMrsId);

        uniqueIdRepository.bulkInsertOpenmrsIds(openMrsIds);

        verify(sqLiteDatabase).beginTransaction();
        verify(sqLiteDatabase).setTransactionSuccessful();
        verify(sqLiteDatabase).endTransaction();
        verify(sqLiteDatabase).insert(stringArgumentCaptor.capture(), eq(null), contentValuesArgumentCaptor.capture());

        assertEquals("unique_ids", stringArgumentCaptor.getValue());
        ContentValues values = contentValuesArgumentCaptor.getValue();
        assertEquals(openMrsId, values.getAsString("openmrs_id"));
        assertEquals("not_used", values.getAsString("status"));
        assertEquals(testUsername, values.getAsString("synced_by"));
        assertNotNull(values.getAsString("created_at"));
    }

    @Test
    public void testBulkInsertOpenMrsIdsWithEmptyParamList() {
        CoreLibrary.getInstance().context().allSharedPreferences().updateANMUserName(testUsername);
        uniqueIdRepository.bulkInsertOpenmrsIds(null);

        verifyNoMoreInteractions(sqLiteDatabase);
    }

    @Test
    public void testBulkInsertOpenMrsIdsWithExceptionThrown() {
        CoreLibrary.getInstance().context().allSharedPreferences().updateANMUserName(testUsername);
        String openMrsId = "3298938-2";
        List<String> openMrsIds = Collections.singletonList(openMrsId);
        uniqueIdRepository = spy(uniqueIdRepository);
        doThrow(new SQLException()).when(sqLiteDatabase).insert(any(), any(), any());

        uniqueIdRepository.bulkInsertOpenmrsIds(openMrsIds);

        verify(sqLiteDatabase).beginTransaction();
        verify(sqLiteDatabase, never()).setTransactionSuccessful();
        verify(sqLiteDatabase).endTransaction();
        verify(sqLiteDatabase).insert(stringArgumentCaptor.capture(), eq(null), contentValuesArgumentCaptor.capture());

        assertEquals("unique_ids", stringArgumentCaptor.getValue());
        ContentValues values = contentValuesArgumentCaptor.getValue();
        assertEquals(openMrsId, values.getAsString("openmrs_id"));
        assertEquals("not_used", values.getAsString("status"));
        assertEquals(testUsername, values.getAsString("synced_by"));
        assertNotNull(values.getAsString("created_at"));
    }

    @Test
    public void testCountUnusedIds() {
        when(sqLiteDatabase.rawQuery(anyString(), any())).thenReturn(getCountCursor());
        long actualCount = uniqueIdRepository.countUnUsedIds();

        assertEquals(12, actualCount);
        verify(sqLiteDatabase).rawQuery("SELECT COUNT (*) FROM unique_ids WHERE status=?", new String[]{"not_used"});
    }

    @Test
    public void testGetNextUniqueId() {
        when(sqLiteDatabase.query(any(), any(), any(), any(), any(), any(), anyString(), anyString())).thenReturn(getUniqueIdCursor());
        UniqueId actualUniqueId = uniqueIdRepository.getNextUniqueId();

        assertEquals("12", actualUniqueId.getId());
        assertEquals("openrs-id1", actualUniqueId.getOpenmrsId());
        assertEquals("test-owner", actualUniqueId.getUsedBy());
        assertEquals(new Date(1583830167).toString(), actualUniqueId.getCreatedAt().toString());
    }

    @Test
    public void testCreateValuesFor() {
        UniqueId expectedUniqueId = new UniqueId("12", "openrs-id1", "not_used", "test-owner", new Date());
        uniqueIdRepository.add(expectedUniqueId);

        verify(sqLiteDatabase).insert(stringArgumentCaptor.capture(), eq(null), contentValuesArgumentCaptor.capture());
        assertEquals("unique_ids", stringArgumentCaptor.getValue());
        ContentValues values = contentValuesArgumentCaptor.getValue();
        assertEquals("12", values.getAsString("_id"));
        assertEquals("openrs-id1", values.getAsString("openmrs_id"));
        assertEquals("not_used", values.getAsString("status"));
        assertEquals("test-owner", values.getAsString("used_by"));
        assertNotNull(values.getAsString("created_at"));
    }

    @Test
    public void testReleaseReserveIds() {
        uniqueIdRepository.releaseReservedIds();

        verify(sqLiteDatabase).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());

        Assert.assertNotNull(stringArgumentCaptor.getValue());
        assertEquals("unique_ids", stringArgumentCaptor.getAllValues().get(0));
        assertEquals("status = ?", stringArgumentCaptor.getAllValues().get(1));
        assertEquals("reserved", argsCaptor.getValue()[0]);

        ContentValues values = contentValuesArgumentCaptor.getValue();
        assertEquals("not_used", values.getAsString("status"));
        assertEquals("", values.getAsString("used_by"));
    }

    @Test
    public void testOpen() {
        String openMrsId = "3298938-2";

        uniqueIdRepository.open(openMrsId);

        verify(sqLiteDatabase, times(2)).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor.capture(), argsCaptor.capture());
        Assert.assertNotNull(stringArgumentCaptor.getValue());
        assertEquals("unique_ids", stringArgumentCaptor.getAllValues().get(0));
        assertEquals("openmrs_id = ?", stringArgumentCaptor.getAllValues().get(1));
        assertEquals("32989382", argsCaptor.getValue()[0]);
        ContentValues values = contentValuesArgumentCaptor.getValue();
        assertEquals("not_used", values.getAsString("status"));
        assertEquals("", values.getAsString("used_by"));
    }

    public MatrixCursor getCountCursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{"count(*)"});
        cursor.addRow(new Object[]{"12"});
        return cursor;
    }

    public MatrixCursor getUniqueIdCursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{"id", "openmrs_id", "status", "used_by", "date_created"});
        cursor.addRow(new Object[]{"12", "openrs-id1", "not-used", "test-owner", 1583830167});
        return cursor;
    }
}
