package org.smartregister.repository;

import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.FormDefinitionVersion;
import org.smartregister.domain.SyncStatus;

import java.util.HashMap;

/**
 * Created by kaderchowdhury on 19/11/17.
 */

public class FormsVersionRepositoryTest extends BaseUnitTest {

    private FormsVersionRepository formsVersionRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Mock
    private Repository repository;
    private static final String ID_COLUMN = "id";
    public static final String FORM_NAME_COLUMN = "formName";
    public static final String VERSION_COLUMN = "formDataDefinitionVersion";
    public static final String FORM_DIR_NAME_COLUMN = "formDirName";
    public static final String SYNC_STATUS_COLUMN = "syncStatus";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        formsVersionRepository = new FormsVersionRepository();
        formsVersionRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getCursor());
        Mockito.when(sqLiteDatabase.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
    }

    public MatrixCursor getCursor() {
        String[] columns = {ID_COLUMN,
                FORM_NAME_COLUMN, FORM_DIR_NAME_COLUMN, VERSION_COLUMN, SYNC_STATUS_COLUMN};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"1", "name", "dir", "1.0", SyncStatus.PENDING});
        return cursor;
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(formsVersionRepository);
    }

    @Test
    public void assertOnCreateCallsDatabaseExec() {
        formsVersionRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertFetchVersionByFormName() {
        Assert.assertNotNull(formsVersionRepository.fetchVersionByFormName(""));
    }

    @Test
    public void assertFetchVersionByFormDirName() {
        Assert.assertNotNull(formsVersionRepository.fetchVersionByFormDirName(""));
    }

    @Test
    public void assertGetVersion() {
        Assert.assertNotNull(formsVersionRepository.getVersion(""));
    }

    @Test
    public void assertgetAllFormWithSyncStatus() {
        Assert.assertNotNull(formsVersionRepository.getAllFormWithSyncStatus(SyncStatus.PENDING));
    }

    @Test
    public void assertgetAllFormWithSyncStatusAsMap() {
        Assert.assertNotNull(formsVersionRepository.getAllFormWithSyncStatusAsMap(SyncStatus.PENDING));
    }

    @Test
    public void assertgetFormByFormDirName() {
        Assert.assertNotNull(formsVersionRepository.getFormByFormDirName(""));
    }

    @Test
    public void assertAddFormVersion() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(FORM_NAME_COLUMN, "form_name");
        data.put(VERSION_COLUMN, "1.0");
        data.put(FORM_DIR_NAME_COLUMN, "dir");
        data.put(SYNC_STATUS_COLUMN, SyncStatus.PENDING.value());
        formsVersionRepository.addFormVersion(data);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertAddFormVersionFromObject() {
        FormDefinitionVersion fd = new FormDefinitionVersion("", "", "");
        fd.setSyncStatus(SyncStatus.PENDING);
        formsVersionRepository.addFormVersionFromObject(fd);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertformExistsReturnsTrue() {
        Assert.assertEquals(formsVersionRepository.formExists(""), true);
    }

    @Test
    public void assertDeleteAllCallsDatabaseDelete() {
        formsVersionRepository.deleteAll();
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).delete(Mockito.anyString(), Mockito.isNull(String.class), Mockito.isNull(String[].class));
    }

    @Test
    public void assertupdateServerVersion() {
        formsVersionRepository.updateServerVersion("", "");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertupdateFormName() {
        formsVersionRepository.updateFormName("", "");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertupdateSyncStatus() {
        formsVersionRepository.updateSyncStatus("", SyncStatus.PENDING);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }
}
