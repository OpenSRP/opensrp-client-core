package org.smartregister.repository;

import android.content.ContentValues;

import org.junit.Assert;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Setting;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.smartregister.repository.SettingsRepository.SETTINGS_KEY_COLUMN;
import static org.smartregister.repository.SettingsRepository.SETTINGS_SYNC_STATUS_COLUMN;
import static org.smartregister.repository.SettingsRepository.SETTINGS_TABLE_NAME;
import static org.smartregister.repository.SettingsRepository.SETTINGS_TYPE_COLUMN;
import static org.smartregister.repository.SettingsRepository.SETTINGS_VALUE_COLUMN;
import static org.smartregister.repository.SettingsRepository.SETTINGS_VERSION_COLUMN;

/**
 * Created by kaderchowdhury on 21/11/17.
 */

public class SettingsRepositoryTest extends BaseUnitTest {

    SettingsRepository settingsRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Mock
    private Repository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        settingsRepository = new SettingsRepository();
        settingsRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(settingsRepository);
    }

    @Test
    public void assertOnCreate() {
        settingsRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).execSQL(anyString());
    }

    @Test
    public void testOnUpgradeExecutesCorrectSQLStatement() {
        settingsRepository.onUpgrade(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(3)).execSQL(anyString());
    }

    @Test
    public void assertupdateSetting() {
        settingsRepository.updateSetting("KEY", "VALUE");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).replace(anyString(), Mockito.isNull(String.class), any(ContentValues.class));
    }

    @Test
    public void assertupdateBLOB() {
        settingsRepository.updateBLOB("", new byte[]{});
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).replace(anyString(), Mockito.isNull(String.class), any(ContentValues.class));
    }

    @Test
    public void assertquerySetting() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{SETTINGS_KEY_COLUMN, SETTINGS_VALUE_COLUMN});
        matrixCursor.addRow(new String[]{"KEY", "VALUE"});
        Mockito.when(sqLiteDatabase.query(anyString(), any(String[].class), anyString(), any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), anyString())).thenReturn(matrixCursor);
        Assert.assertEquals(settingsRepository.querySetting("", ""), "KEY");
    }

    @Test
    public void assertqueryBlob() {
        Mockito.when(sqLiteDatabase.query(anyString(), any(String[].class), anyString(), any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), anyString())).thenReturn(getCursor());
        Assert.assertEquals(settingsRepository.queryBLOB(""), null);
    }

    public MatrixCursor getCursor() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{SETTINGS_KEY_COLUMN, SETTINGS_VALUE_COLUMN});
        matrixCursor.addRow(new Object[]{new byte[]{}, new byte[]{}});
        return matrixCursor;
    }

    @Test
    public void testUpdateSetting() {
        Setting s = new Setting();
        s.setKey("test"); s.setValue("testValue"); s.setVersion("test"); s.setType("test"); s.setSyncStatus("test");

        ArgumentCaptor<ContentValues> contentValuesArgumentCaptor = ArgumentCaptor.forClass(ContentValues.class);
        Mockito.doReturn(1L).when(sqLiteDatabase).replace(Mockito.eq(SETTINGS_TABLE_NAME), Mockito.nullable(String.class), contentValuesArgumentCaptor.capture());

        settingsRepository.updateSetting(s);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).replace(anyString(), Mockito.isNull(String.class), any(ContentValues.class));

        Assert.assertEquals(contentValuesArgumentCaptor.getValue().get(SETTINGS_KEY_COLUMN), "test");
    }

    @Test
    public void testQuerySetting() {
        Mockito.doReturn(getMatrixCursor()).when(sqLiteDatabase).query(any(), any(), any(), any(), nullable(String.class), nullable(String.class), nullable(String.class), anyString());

        Setting s = settingsRepository.querySetting("");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query("settings", new String[]{"key", "value", "type", "version", "sync_status"},
                "key = ?", new String[]{""}, null, null, null, "1");

        Assert.assertEquals(s.getKey(), "testKey");
    }

    @Test
    public void testQuerySettingsByType() {
        Mockito.doReturn(getMatrixCursor()).when(sqLiteDatabase).query(any(), any(), any(), any(), nullable(String.class), nullable(String.class), nullable(String.class), nullable(String.class));

        List<Setting> lst = settingsRepository.querySettingsByType("");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query("settings", new String[]{"key", "value", "type", "version", "sync_status"},
                "type = ?", new String[]{""}, null, null, null, null);

        Assert.assertEquals(lst.get(0).getKey(), "testKey");
    }

    @Test
    public void testQueryUnsyncedSettings(){
        Mockito.doReturn(getMatrixCursor()).when(sqLiteDatabase).query(any(), any(), any(), any(), nullable(String.class), nullable(String.class), nullable(String.class), nullable(String.class));

        List<Setting> lst = settingsRepository.queryUnsyncedSettings();
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query("settings", new String[]{"key", "value", "type", "version", "sync_status"},
                "sync_status = ?", new String[]{"PENDING"}, null, null, null, null);

        Assert.assertEquals(lst.get(0).getKey(), "testKey");
    }

    @Test
    public void testQueryCore() {
        MatrixCursor cursor = getMatrixCursor();
        cursor.moveToFirst();
        Setting s = settingsRepository.queryCore(cursor);
        Assert.assertEquals(s.getKey(), "testKey");
    }

    public MatrixCursor getMatrixCursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{SETTINGS_KEY_COLUMN, SETTINGS_VALUE_COLUMN, SETTINGS_TYPE_COLUMN, SETTINGS_VERSION_COLUMN, SETTINGS_SYNC_STATUS_COLUMN});
        cursor.addRow(new Object[]{"testKey", "testValue", "testType", "testVersion", "PENDING"});
        return cursor;
    }
}
