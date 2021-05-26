package org.smartregister.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;
import org.smartregister.BaseUnitTest;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.FormDefinitionVersion;
import org.smartregister.domain.Response;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.FormsVersionRepository;
import org.smartregister.util.EasyMap;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.smartregister.domain.FetchStatus.fetched;
import static org.smartregister.domain.FetchStatus.nothingFetched;
import static org.smartregister.domain.ResponseStatus.success;

/**
 * Created by Dimas Ciputra on 3/31/15.
 */
@Config(manifest = Config.NONE)
public class AllFormVersionSyncServiceTest extends BaseUnitTest {

    @Mock
    private HTTPAgent httpAgent;
    @Mock
    private DristhiConfiguration configuration;
    @Mock
    private FormsVersionRepository formsVersionRepository;
    @Mock
    private FormPathService formPathService;
    @Mock
    private File file;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private AllFormVersionSyncService service;
    private List<FormDefinitionVersion> expectedFormDefinitionVersion;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = Mockito.spy(new AllFormVersionSyncService(httpAgent,
                configuration,
                formsVersionRepository));
        expectedFormDefinitionVersion = Arrays.asList(new FormDefinitionVersion("form_ec", "ec_dir", "2"));
        Mockito.when(configuration.dristhiBaseURL()).thenReturn("http://opensrp_base_url");
    }

    @Test
    public void shouldNotDownloadIfThereIsNoPendingForms() throws Exception {
        Mockito.when(formsVersionRepository.getAllFormWithSyncStatus(SyncStatus.PENDING)).thenReturn(
                Collections.<FormDefinitionVersion>emptyList());

        DownloadStatus status = service.downloadAllPendingFormFromServer();

        Assert.assertEquals(status, DownloadStatus.nothingDownloaded);
        Mockito.verify(formsVersionRepository).getAllFormWithSyncStatus(SyncStatus.PENDING);
        Mockito.verifyNoMoreInteractions(formsVersionRepository);
        Mockito.verifyZeroInteractions(httpAgent);
    }

    @Test
    public void shouldDownloadIfThereIsAPendingForms() throws Exception {
        Mockito.when(formsVersionRepository.getAllFormWithSyncStatus(SyncStatus.PENDING)).thenReturn(
                this.expectedFormDefinitionVersion);
        Mockito.when(httpAgent.downloadFromUrl("http://opensrp_base_url/form/form-files?formDirName=ec_dir",
                "ec_dir.zip")).thenReturn(DownloadStatus.downloaded);

        DownloadStatus status = service.downloadAllPendingFormFromServer();

        Assert.assertEquals(status, DownloadStatus.downloaded);
        Mockito.verify(formsVersionRepository).getAllFormWithSyncStatus(SyncStatus.PENDING);
        Mockito.verify(httpAgent).downloadFromUrl(
                "http://opensrp_base_url/form/form-files?formDirName=ec_dir",
                "ec_dir.zip");
    }

    @Test
    public void shouldReturnWhenThereIsNoFileInFolder() throws Exception {
        Mockito.when(service.listFormFiles()).thenReturn(null);

        service.verifyFormsInFolder();

        Mockito.verify(formsVersionRepository).deleteAll();
        Mockito.verifyNoMoreInteractions(formsVersionRepository);
        Mockito.verifyNoMoreInteractions(file);
    }

    @Test
    public void shouldUpdateRepoWhenThereIsNoFileInFolder() throws Exception {
        final File file1 = temporaryFolder.newFile("ec_registration");

        File[] formFiles = new File[]{file1};

        Map<String, String> repoFile1 = EasyMap.create("formName", "ec_registration")
                .put("formDirName", "ec_registration")
                .put("formDataDefinitionVersion", "1")
                .put("id", "1")
                .put("syncStatus", "SYNCED")
                .map();

        Map<String, String> repoFile2 = EasyMap.create("formName", "anc_registration")
                .put("formDirName", "anc_registration")
                .put("formDataDefinitionVersion", "1")
                .put("id", "1")
                .put("syncStatus", "SYNCED")
                .map();

        Mockito.when(service.listFormFiles()).thenReturn(formFiles);
        Mockito.when(formsVersionRepository.getAllFormWithSyncStatusAsMap(SyncStatus.SYNCED)).thenReturn(
                Arrays.asList(repoFile1, repoFile2));

        service.verifyFormsInFolder();

        Mockito.verify(formsVersionRepository).updateSyncStatus("anc_registration", SyncStatus.PENDING);
    }

    @Test
    public void shouldAddToRepoWhenThereIsFilesInFolder() throws Exception {
        File file1 = temporaryFolder.newFile("ec_registration");
        File file2 = temporaryFolder.newFile("anc_registration");

        File[] formFiles = new File[]{file1, file2};

        FormDefinitionVersion f1 = new FormDefinitionVersion("ec_registration",
                "ec_registration",
                "1");
        FormDefinitionVersion f2 = new FormDefinitionVersion("anc_registration",
                "anc_registration",
                "1");

        Mockito.when(service.listFormFiles()).thenReturn(formFiles);
        Mockito.doReturn(f1).when(service).getFormDefinitionFromFile(file1);
        Mockito.doReturn(f2).when(service).getFormDefinitionFromFile(file2);

        Mockito.when(formsVersionRepository.formExists("ec_registration")).thenReturn(false);
        Mockito.when(formsVersionRepository.formExists("anc_registration")).thenReturn(false);

        service.verifyFormsInFolder();

        Mockito.verify(formsVersionRepository).formExists("ec_registration");
        Mockito.verify(formsVersionRepository).formExists("anc_registration");
        Mockito.verify(formsVersionRepository).addFormVersionFromObject(f1);
        Mockito.verify(formsVersionRepository).addFormVersionFromObject(f2);
    }

    @Test
    public void shouldUpdateVersionIfThereIsNewerVersion() throws Exception {
        String jsonObject = "{\"formVersions\" : [{\"formName\": \"EC_ENGKAN\", \"formDirName\": "
                + "\"ec_dir\", \"formDataDefinitionVersion\": \"3\"}] }";
        Mockito.when(httpAgent.fetch("http://opensrp_base_url/form/latest-form-versions")).thenReturn(
                new Response<String>(
                        success,
                        jsonObject));

        List<FormDefinitionVersion> repoForm = Arrays.asList(new FormDefinitionVersion("form_ec",
                "ec_dir",
                "1"));

        Mockito.when(formsVersionRepository.formExists("ec_dir")).thenReturn(true);
        Mockito.when(formsVersionRepository.getAllFormWithSyncStatus(SyncStatus.PENDING)).thenReturn(
                repoForm);
        Mockito.when(formsVersionRepository.getFormByFormDirName("ec_dir")).thenReturn(
                new FormDefinitionVersion(
                        "EC_ENGAN",
                        "ec_dir",
                        "1"));
        Mockito.when(formsVersionRepository.getVersion("ec_dir")).thenReturn("1");

        FetchStatus fetchStatus = service.pullFormDefinitionFromServer();

        Assert.assertEquals(fetched, fetchStatus);

        Mockito.verify(httpAgent).fetch("http://opensrp_base_url/form/latest-form-versions");
        Mockito.verify(formsVersionRepository).updateFormName("ec_dir", "EC_ENGKAN");
        Mockito.verify(formsVersionRepository).formExists("ec_dir");
        Mockito.verify(formsVersionRepository).updateServerVersion("ec_dir", "3");
        Mockito.verify(formsVersionRepository).updateSyncStatus("ec_dir", SyncStatus.PENDING);
    }

    @Test
    public void shouldNotUpdateIfLocalFormIsTheLatestVersion() throws Exception {
        String jsonObject = "{\"formVersions\" : [{\"formName\": \"EC_ENGKAN\", \"formDirName\":"
                + " \"ec_dir\", \"formDataDefinitionVersion\": \"2\"}] }";
        Mockito.when(httpAgent.fetch("http://opensrp_base_url/form/latest-form-versions")).thenReturn(
                new Response<String>(
                        success,
                        jsonObject));

        List<FormDefinitionVersion> repoForm = Arrays.asList(new FormDefinitionVersion("form_ec",
                "ec_dir",
                "3"));

        Mockito.when(formsVersionRepository.formExists("ec_dir")).thenReturn(true);
        Mockito.when(formsVersionRepository.getAllFormWithSyncStatus(SyncStatus.PENDING)).thenReturn(
                repoForm);
        Mockito.when(formsVersionRepository.getFormByFormDirName("ec_dir")).thenReturn(
                new FormDefinitionVersion(
                        "EC_ENGAN",
                        "ec_dir",
                        "3"));
        Mockito.when(formsVersionRepository.getVersion("ec_dir")).thenReturn("3");

        FetchStatus fetchStatus = service.pullFormDefinitionFromServer();

        Assert.assertEquals(nothingFetched, fetchStatus);
    }
}
