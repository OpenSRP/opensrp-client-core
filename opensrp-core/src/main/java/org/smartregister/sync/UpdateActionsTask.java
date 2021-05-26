package org.smartregister.sync;

import android.content.Context;

import org.smartregister.CoreLibrary;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.FetchStatus;
import org.smartregister.service.ActionService;
import org.smartregister.service.AllFormVersionSyncService;
import org.smartregister.service.FormSubmissionSyncService;
import org.smartregister.util.Utils;
import org.smartregister.view.BackgroundAction;
import org.smartregister.view.LockingBackgroundTask;
import org.smartregister.view.ProgressIndicator;

import static org.smartregister.domain.FetchStatus.fetched;
import static org.smartregister.domain.FetchStatus.nothingFetched;
import static org.smartregister.util.Log.logInfo;

public class UpdateActionsTask {
    private final LockingBackgroundTask task;
    private ActionService actionService;
    private Context context;
    private FormSubmissionSyncService formSubmissionSyncService;
    private AllFormVersionSyncService allFormVersionSyncService;
    private AdditionalSyncService additionalSyncService;

    public UpdateActionsTask(Context context, ActionService actionService,
                             FormSubmissionSyncService formSubmissionSyncService,
                             ProgressIndicator progressIndicator, AllFormVersionSyncService
                                     allFormVersionSyncService) {
        this.actionService = actionService;
        this.context = context;
        this.formSubmissionSyncService = formSubmissionSyncService;
        this.allFormVersionSyncService = allFormVersionSyncService;
        this.additionalSyncService = null;
        task = new LockingBackgroundTask(progressIndicator);
    }

    public void setAdditionalSyncService(AdditionalSyncService additionalSyncService) {
        this.additionalSyncService = additionalSyncService;
    }

    public void updateFromServer(final AfterFetchListener afterFetchListener) {
        if (CoreLibrary.getInstance().context().IsUserLoggedOut()) {
            logInfo("Not updating from server as user is not logged in.");
            return;
        }

        task.doActionInBackground(new BackgroundAction<FetchStatus>() {
            public FetchStatus actionToDoInBackgroundThread() {

                FetchStatus fetchStatusForForms = formSubmissionSyncService.sync();
                FetchStatus fetchStatusForActions = CoreLibrary.getInstance().getSyncConfiguration().disableActionService() ? nothingFetched : actionService.fetchNewActions();
                FetchStatus fetchStatusAdditional = additionalSyncService == null ? nothingFetched
                        : additionalSyncService.sync();

                if (CoreLibrary.getInstance().context().configuration().shouldSyncForm()) {

                    allFormVersionSyncService.verifyFormsInFolder();
                    FetchStatus fetchVersionStatus = allFormVersionSyncService
                            .pullFormDefinitionFromServer();
                    DownloadStatus downloadStatus = allFormVersionSyncService
                            .downloadAllPendingFormFromServer();

                    if (downloadStatus == DownloadStatus.downloaded) {
                        allFormVersionSyncService.unzipAllDownloadedFormFile();
                    }

                    if (fetchVersionStatus == fetched
                            || downloadStatus == DownloadStatus.downloaded) {
                        return fetched;
                    }
                }

                if (fetchStatusForActions == fetched || fetchStatusForForms == fetched
                        || fetchStatusAdditional == fetched) {
                    return fetched;
                }

                return fetchStatusForForms;
            }

            public void postExecuteInUIThread(FetchStatus result) {
                if (result != null && context != null && result != nothingFetched) {
                    Utils.showShortToast(context, result.displayValue());
                }
                afterFetchListener.afterFetch(result);
            }
        });
    }
}