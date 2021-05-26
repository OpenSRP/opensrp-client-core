package org.smartregister.repository;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import org.smartregister.util.Session;

import java.util.Arrays;
import java.util.Date;

public class SettingsRepositoryTest extends AndroidTestCase {
    private SettingsRepository settingsRepository;

    @Override
    protected void setUp() throws Exception {
        settingsRepository = new SettingsRepository();
        Session session = new Session().setPassword("password").setRepositoryName("drishti.db" + new Date().getTime());
        new Repository(new RenamingDelegatingContext(getContext(), "test_"), session, settingsRepository);
    }

    public void testShouldGetDefaultValueIfNothingHasBeenSet() throws Exception {
        assertEquals("someDefaultValue", settingsRepository.querySetting("NOT-SET", "someDefaultValue"));
    }

    public void testSettingsFetchAndSave() throws Exception {
        settingsRepository.updateSetting("abc", "def");

        assertEquals("def", settingsRepository.querySetting("abc", "someDefaultValue"));
    }

    public void testSettingsFetchAndSaveAsBLOB() throws Exception {
        byte[] expected = new byte[]{1, 2, 3};
        settingsRepository.updateBLOB("abc", expected);

        byte[] actual = settingsRepository.queryBLOB("abc");

        assertTrue(Arrays.equals(expected, actual));
    }

    public void testShouldGiveDefaultValueIfThereHasBeenNoSetValue() throws Exception {
        assertEquals("someDefaultValue", settingsRepository.querySetting("SOMETHING_WHICH_DOES_NOT_EXIST", "someDefaultValue"));
    }

    public void testShouldOverwriteExistingValueWhenUpdating() throws Exception {
        settingsRepository.updateSetting("abc", "def");
        settingsRepository.updateSetting("abc", "ghi");

        assertEquals("ghi", settingsRepository.querySetting("abc", "someDefaultValue"));
    }
}
