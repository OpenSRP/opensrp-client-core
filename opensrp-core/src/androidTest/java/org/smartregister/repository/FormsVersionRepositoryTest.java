package org.smartregister.repository;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import org.smartregister.domain.FormDefinitionVersion;
import org.smartregister.util.Session;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.smartregister.domain.SyncStatus.PENDING;
import static org.smartregister.domain.SyncStatus.SYNCED;
import static org.smartregister.util.EasyMap.create;

/**
 * Created by Dimas Ciputra on 3/22/15.
 */
public class FormsVersionRepositoryTest extends AndroidTestCase {

    private FormsVersionRepository repository;

    @Override
    protected void setUp() throws Exception {
        repository = new FormsVersionRepository();
        Session session = new Session().setPassword("password").setRepositoryName("drishti.db" + new Date().getTime());
        new Repository(new RenamingDelegatingContext(getContext(), "test_"), session, repository);
    }

    public void testShouldCheckFormExistence() throws Exception {
        Map<String, String> jsonData = create("id", "1")
                .put("formDirName", "ec_dir_name")
                .put("formName", "ec")
                .put("version", "1")
                .put("syncStatus", "SYNCED").map();

        repository.addFormVersion(jsonData);

        assertTrue(repository.formExists("ec_dir_name"));
        assertFalse(repository.formExists("ec"));
    }

    public void testShouldSaveFormVersion() throws Exception {
        Map<String, String> params = create("formName", "ec")
                .put("formDirName", "ec_dir_name")
                .put("syncStatus", SYNCED.value())
                .put("formDataDefinitionVersion", "1").map();
        repository.addFormVersion(params);

        FormDefinitionVersion actualFormsVersion = repository.fetchVersionByFormDirName("ec_dir_name");
        assertNotNull(actualFormsVersion);
        assertEquals(actualFormsVersion.getVersion(), params.get("formDataDefinitionVersion"));
    }

    public void testShouldAutoGenerateIdKeyWhenAddNewForm() throws Exception {
        Map<String, String> params = create("formName", "ec")
                .put("formDirName", "ec_dir_name")
                .put("syncStatus", SYNCED.value())
                .put("formDataDefinitionVersion", "1").map();

        repository.addFormVersion(params);

        FormDefinitionVersion actualFormsVersion = repository.fetchVersionByFormDirName("ec_dir_name");
        assertNotNull(actualFormsVersion);
        assertEquals(actualFormsVersion.getVersion(), params.get("formDataDefinitionVersion"));
        assertNotNull(actualFormsVersion.getEntityId());
    }

    public void testShouldUpdateVersionFormIfFormExist() throws Exception {
        Map<String, String> data1 = create("formName", "ec")
                .put("formDirName", "ec_dir_name")
                .put("formDataDefinitionVersion", "1")
                .put("syncStatus", SYNCED.value()).map();

        repository.addFormVersion(data1);

        FormDefinitionVersion actualData1 = repository.fetchVersionByFormDirName("ec_dir_name");
        assertEquals("1", actualData1.getVersion());

        if (repository.formExists("ec_dir_name")) {
            repository.updateServerVersion("ec_dir_name", "2");
        }

        FormDefinitionVersion actualData2 = repository.fetchVersionByFormDirName("ec_dir_name");
        assertEquals("2", actualData2.getVersion());
    }

    public void testShouldUpdateSyncStatus() throws Exception {
        Map<String, String> firstForm = create("id", "1")
                .put("formName", "ec")
                .put("formDirName", "ec_dir_name")
                .put("formDataDefinitionVersion", "1")
                .put("syncStatus", PENDING.value()).map();

        repository.addFormVersion(firstForm);
        FormDefinitionVersion actualData = repository.fetchVersionByFormDirName("ec_dir_name");

        assertEquals(PENDING.value(), actualData.getSyncStatus().toString());

        repository.updateSyncStatus("ec_dir_name", SYNCED);
        actualData = repository.fetchVersionByFormDirName("ec_dir_name");

        assertEquals(SYNCED.value(), actualData.getSyncStatus().toString());
    }

    public void testShouldGetAllPendingForms() throws Exception {

        Map<String, String> firstForm = create("entityId", "1")
                .put("formName", "ec")
                .put("formDirName", "ec_dir_name")
                .put("formDataDefinitionVersion", "1")
                .put("syncStatus", PENDING.value()).map();
        Map<String, String> secondForm = create("entityId", "2")
                .put("formName", "anc")
                .put("formDirName", "anc_dir_name")
                .put("formDataDefinitionVersion", "1")
                .put("syncStatus", PENDING.value()).map();
        Map<String, String> thirdForm = create("entityId", "3")
                .put("formName", "pnc")
                .put("formDirName", "pnc_dir_name")
                .put("formDataDefinitionVersion", "1")
                .put("syncStatus", SYNCED.value()).map();

        repository.addFormVersion(firstForm);
        repository.addFormVersion(secondForm);
        repository.addFormVersion(thirdForm);

        List<FormDefinitionVersion> allPendingForms = repository.getAllFormWithSyncStatus(PENDING);
        List<FormDefinitionVersion> allSyncedForms = repository.getAllFormWithSyncStatus(SYNCED);

        assertEquals(2, allPendingForms.size());
        assertEquals(1, allSyncedForms.size());
    }

    public void testShouldGetVersionByFormName() throws Exception {
        Map<String, String> firstForm = create("id", "1")
                .put("formName", "ec")
                .put("formDirName", "ec_dir_name")
                .put("formDataDefinitionVersion", "1")
                .put("syncStatus", PENDING.value()).map();

        repository.addFormVersion(firstForm);

        String version = repository.getVersion("ec_dir_name");

        assertEquals("1", version);
    }
}
