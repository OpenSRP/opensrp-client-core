package org.smartregister.service;

import com.google.gson.Gson;

import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.domain.SyncStatus;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.FormDataRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FormSubmissionSyncServiceTest extends BaseUnitTest {
    @Mock
    private FormDataRepository repository;
    @Mock
    private HTTPAgent httpAgent;
    @Mock
    private AllSettings allSettings;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private FormSubmissionService formSubmissionService;
    @Mock
    private DristhiConfiguration configuration;

    private FormSubmissionSyncService service;
    private List<FormSubmissionDTO> expectedFormSubmissionsDto;
    private List<FormSubmission> submissions;
    private String formInstanceJSON;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new FormSubmissionSyncService(formSubmissionService, httpAgent, repository, allSettings, allSharedPreferences, configuration);

        formInstanceJSON = "{form:{bind_type: 'ec'}}";
        submissions = Arrays.asList(new FormSubmission("id 1", "entity id 1", "form name", formInstanceJSON, "123", SyncStatus.PENDING, "1"));
        expectedFormSubmissionsDto = Arrays.asList(new FormSubmissionDTO(
                "anm id 1", "id 1", "entity id 1", "form name", formInstanceJSON, "123", "1"));
        Mockito.when(configuration.dristhiBaseURL()).thenReturn("http://dristhi_base_url");
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("anm id 1");
        Mockito.when(repository.getPendingFormSubmissions()).thenReturn(submissions);
    }

    @Test
    public void shouldPushPendingFormSubmissionsToServerAndMarkThemAsSynced() throws Exception {
        Mockito.when(httpAgent.post("http://dristhi_base_url" + "/form-submissions", new Gson().toJson(expectedFormSubmissionsDto)))
                .thenReturn(new Response<String>(ResponseStatus.success, null));

        service.pushToServer();

        Mockito.inOrder(allSettings, httpAgent, repository);
        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
        Mockito.verify(httpAgent).post("http://dristhi_base_url" + "/form-submissions", new Gson().toJson(expectedFormSubmissionsDto));
        Mockito.verify(repository).markFormSubmissionsAsSynced(submissions);
    }

    @Test
    public void shouldNotMarkPendingSubmissionsAsSyncedIfPostFails() throws Exception {
        Mockito.when(httpAgent.post("http://dristhi_base_url" + "/form-submissions", new Gson().toJson(expectedFormSubmissionsDto)))
                .thenReturn(new Response<String>(ResponseStatus.failure, null));

        service.pushToServer();

        Mockito.verify(repository).getPendingFormSubmissions();
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    public void shouldNotPushIfThereAreNoPendingSubmissions() throws Exception {
        Mockito.when(repository.getPendingFormSubmissions()).thenReturn(Collections.<FormSubmission>emptyList());

        service.pushToServer();

        Mockito.verify(repository).getPendingFormSubmissions();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyZeroInteractions(allSettings);
        Mockito.verifyZeroInteractions(httpAgent);
    }

    @Test
    public void shouldPullFormSubmissionsFromServerInBatchesAndDelegateToProcessing() throws Exception {
        this.expectedFormSubmissionsDto = Arrays.asList(new FormSubmissionDTO(
                "anm id 1", "id 1", "entity id 1", "form name", formInstanceJSON, "123", "1"));
        List<FormSubmission> expectedFormSubmissions = Arrays.asList(new FormSubmission("id 1", "entity id 1", "form name",
                formInstanceJSON, "123", SyncStatus.SYNCED, "1"));
        Mockito.when(configuration.syncDownloadBatchSize()).thenReturn(1);
        Mockito.when(allSettings.fetchPreviousFormSyncIndex()).thenReturn("122");
        Mockito.when(httpAgent.fetch("http://dristhi_base_url/form-submissions?anm-id=anm id 1&timestamp=122&batch-size=1"))
                .thenReturn(new Response<String>(ResponseStatus.success, new Gson().toJson(this.expectedFormSubmissionsDto)),
                        new Response<String>(ResponseStatus.success, new Gson().toJson(Collections.emptyList())));

        FetchStatus fetchStatus = service.pullFromServer();

        Assert.assertEquals(FetchStatus.fetched, fetchStatus);
        Mockito.verify(httpAgent, Mockito.times(2))
                .fetch("http://dristhi_base_url/form-submissions?anm-id=anm id 1&timestamp=122&batch-size=1");
        Mockito.verify(formSubmissionService).processSubmissions(expectedFormSubmissions);
    }

    @Test
    public void shouldReturnNothingFetchedStatusWhenNoFormSubmissionsAreGotFromServer() throws Exception {
        Mockito.when(configuration.syncDownloadBatchSize()).thenReturn(1);
        Mockito.when(allSettings.fetchPreviousFormSyncIndex()).thenReturn("122");
        Mockito.when(httpAgent.fetch("http://dristhi_base_url/form-submissions?anm-id=anm id 1&timestamp=122&batch-size=1"))
                .thenReturn(new Response<String>(ResponseStatus.success, new Gson().toJson(Collections.emptyList())));

        FetchStatus fetchStatus = service.pullFromServer();

        Assert.assertEquals(FetchStatus.nothingFetched, fetchStatus);
        Mockito.verify(httpAgent).fetch("http://dristhi_base_url/form-submissions?anm-id=anm id 1&timestamp=122&batch-size=1");
        Mockito.verifyZeroInteractions(formSubmissionService);
    }

    @Test
    public void shouldNotDelegateToProcessingIfPullFails() throws Exception {
        Mockito.when(configuration.syncDownloadBatchSize()).thenReturn(1);
        Mockito.when(allSettings.fetchPreviousFormSyncIndex()).thenReturn("122");
        Mockito.when(httpAgent.fetch("http://dristhi_base_url/form-submissions?anm-id=anm id 1&timestamp=122&batch-size=1"))
                .thenReturn(new Response<String>(ResponseStatus.failure, null));

        FetchStatus fetchStatus = service.pullFromServer();

        Assert.assertEquals(FetchStatus.fetchedFailed, fetchStatus);
        Mockito.verifyZeroInteractions(formSubmissionService);
    }
}
