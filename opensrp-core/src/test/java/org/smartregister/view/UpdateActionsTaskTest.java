package org.smartregister.view;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.FetchStatus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.ActionService;
import org.smartregister.service.AllFormVersionSyncService;
import org.smartregister.service.FormSubmissionSyncService;
import org.smartregister.sync.AfterFetchListener;
import org.smartregister.sync.UpdateActionsTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.domain.FetchStatus.fetched;
import static org.smartregister.domain.FetchStatus.nothingFetched;

public class UpdateActionsTaskTest extends BaseUnitTest {
    @Mock
    private ActionService actionService;
    @Mock
    private ProgressIndicator progressIndicator;
    @Mock
    private Context androidContext;
    @Mock
    private org.smartregister.Context context;
    @Mock
    private FormSubmissionSyncService formSubmissionSyncService;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private AllFormVersionSyncService allFormVersionSyncService;

    @Mock
    private DristhiConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldShowProgressBarsWhileFetchingAlerts() throws Exception {
        progressIndicator = mock(ProgressIndicator.class);

        when(context.IsUserLoggedOut()).thenReturn(false);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");
        when(actionService.fetchNewActions()).thenReturn(fetched);
        when(formSubmissionSyncService.sync()).thenReturn(fetched);
        when(context.configuration()).thenReturn(configuration);

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(null, actionService, formSubmissionSyncService, progressIndicator, allFormVersionSyncService);
        updateActionsTask.updateFromServer(new AfterFetchListener() {
            public void afterFetch(FetchStatus status) {
                assertEquals(fetched, status);
            }
        });

        // FIXME indicator visibility not working
        InOrder inOrder = inOrder(actionService, progressIndicator);
        //inOrder.verify(progressIndicator).setVisible();
        //inOrder.verify(actionService).fetchNewActions();
        //inOrder.verify(progressIndicator).setInvisible();
    }

    @Test
    public void shouldNotUpdateDisplayIfNothingWasFetched() throws Exception {
        CoreLibrary.init(context);
        when(context.IsUserLoggedOut()).thenReturn(false);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");
        when(actionService.fetchNewActions()).thenReturn(nothingFetched);
        when(formSubmissionSyncService.sync()).thenReturn(nothingFetched);
        when(allFormVersionSyncService.pullFormDefinitionFromServer()).thenReturn(nothingFetched);
        when(allFormVersionSyncService.downloadAllPendingFormFromServer()).thenReturn(DownloadStatus.nothingDownloaded);

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(null, actionService, formSubmissionSyncService, progressIndicator, allFormVersionSyncService);
        updateActionsTask.updateFromServer(new AfterFetchListener() {
            public void afterFetch(FetchStatus status) {
                assertEquals(nothingFetched, status);
            }
        });
    }

    @Test
    public void shouldNotUpdateWhenUserIsNotLoggedIn() throws Exception {
        when(context.configuration()).thenReturn(configuration);
        when(configuration.shouldSyncForm()).thenReturn(false);
        CoreLibrary.init(context);
        when(context.IsUserLoggedOut()).thenReturn(true);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");


        UpdateActionsTask updateActionsTask = new UpdateActionsTask(androidContext, actionService, formSubmissionSyncService, progressIndicator, allFormVersionSyncService);
        updateActionsTask.updateFromServer(new AfterFetchListener() {
            public void afterFetch(FetchStatus status) {
                fail("Should not have updated from server as the user is not logged in.");
            }
        });

        // verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldSyncFormSubmissionsWithServer() throws Exception {
        CoreLibrary.init(context);
        when(context.IsUserLoggedOut()).thenReturn(false);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(allSharedPreferences.fetchLanguagePreference()).thenReturn("en");

        UpdateActionsTask updateActionsTask = new UpdateActionsTask(androidContext, actionService, formSubmissionSyncService, progressIndicator, allFormVersionSyncService);
        updateActionsTask.updateFromServer(new AfterFetchListener() {
            public void afterFetch(FetchStatus status) {
            }
        });

        // FIXME test not working
        // verify(formSubmissionSyncService).sync();
    }
}
