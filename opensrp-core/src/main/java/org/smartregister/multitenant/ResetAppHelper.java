package org.smartregister.multitenant;

import android.content.SharedPreferences;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.evernote.android.job.JobManager;

import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.R;
import org.smartregister.exception.AppResetException;
import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.listener.OnCompleteClearDataCallback;
import org.smartregister.multitenant.check.EventClientSyncedCheck;
import org.smartregister.multitenant.check.PreResetAppCheck;
import org.smartregister.multitenant.check.SettingsSyncedCheck;
import org.smartregister.multitenant.check.StructureSyncedCheck;
import org.smartregister.multitenant.check.TaskSyncedCheck;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.p2p.model.AppDatabase;
import org.smartregister.util.AppExecutors;
import org.smartregister.util.NetworkUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.dialog.ResetAppDialog;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-04-2020.
 */
public class ResetAppHelper {

    private DrishtiApplication application;
    private AppExecutors appExecutors;
    private ArrayList<PreResetAppCheck> preResetAppChecks = new ArrayList<>();
    private ResetAppDialog resetAppDialog;
    private boolean resetCancelled = false;

    public ResetAppHelper(@NonNull DrishtiApplication drishtiApplication) {
        this.application = drishtiApplication;
        appExecutors = new AppExecutors();
        preResetAppChecks.add(new EventClientSyncedCheck());
        preResetAppChecks.add(new SettingsSyncedCheck());
        preResetAppChecks.add(new StructureSyncedCheck());
        preResetAppChecks.add(new TaskSyncedCheck());
    }

    public void startResetProcess(@Nullable AppCompatActivity activity, @Nullable OnCompleteClearDataCallback onCompleteClearDataCallback) {

        resetCancelled = false;
        // Show some UI here to display the reset progress

        if (activity != null) {
            resetAppDialog = ResetAppDialog.newInstance();
            resetAppDialog.show(activity.getSupportFragmentManager(), "rest-app-dialog");
            resetAppDialog.setOnCancelListener((dialogInterface) -> {
                showProgressText(activity.getString(R.string.cancelling));
                resetCancelled = true;
            });

            resetAppDialog.showText(activity.getString(R.string.stopping_services));
        }

        JobManager.create(application).cancelAll();

        if (resetAppDialog != null) {
            resetAppDialog.showText(activity.getString(R.string.performing_data_checks));
        }

        if (!resetCancelled) {
            performPreResetChecksAndResetProcess(onCompleteClearDataCallback);
        }
    }

    public void performPreResetChecksAndResetProcess(@Nullable OnCompleteClearDataCallback onCompleteClearDataCallback) {
        // Should be handled in the background
        appExecutors.diskIO()
                .execute(() -> {
                    try {
                        for (PreResetAppCheck preResetAppCheck : preResetAppChecks) {
                            if (!preResetAppCheck.isCheckOk(application)) {
                                if (resetCancelled) {
                                    dismissDialog();
                                    return;
                                }

                                if (!NetworkUtils.isNetworkAvailable()) {
                                    dismissDialog();
                                    appExecutors.mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(application, "No Internet Connection Available", Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    });

                                    resetCancelled = true;
                                    return;
                                }

                                performPreResetOperations(preResetAppCheck);
                            }
                        }

                        if (resetCancelled) {
                            dismissDialog();
                            return;
                        }

                        showProgressText(application.getString(R.string.clearing_application_data));

                        if (resetCancelled) {
                            dismissDialog();
                            return;
                        }

                        Timber.w("User %s has completely reset the app", application.getUsername());
                        performResetOperations();
                        appExecutors.mainThread()
                                .execute(() -> {
                                    dismissDialog();

                                    if (onCompleteClearDataCallback != null) {
                                        onCompleteClearDataCallback.onComplete();
                                    }
                                });

                    } catch (PreResetAppOperationException | AppResetException e) {
                        Timber.e(e);
                    }
                });
    }

    public void performResetOperations() throws AppResetException {
        clearSqCipherDb();
        clearP2PDb();
        clearAllPrivateKeyEntries();
        clearSharedPreferences();
    }

    protected void clearP2PDb() {
        P2POptions p2POptions = CoreLibrary.getInstance().getP2POptions();
        if (p2POptions != null && p2POptions.isEnableP2PLibrary()) {
            p2POptions.setAuthorizationService(null);
            AppDatabase roomP2PDb = P2PLibrary.getInstance().getDb();
            roomP2PDb.clearAllTables();
            Utils.deleteRoomDb(application.getApplicationContext(), roomP2PDb.getOpenHelper().getDatabaseName());
        }
    }

    public void performPreResetOperations(@NonNull PreResetAppCheck preResetAppCheck) throws PreResetAppOperationException {
        preResetAppCheck.performPreResetAppOperations(application);
    }

    protected boolean clearSharedPreferences() {
        SharedPreferences sharedPreferences = application.getContext().allSharedPreferences().getPreferences();
        if (sharedPreferences != null) {
            return sharedPreferences.edit().clear().commit();
        }

        return false;
    }

    protected boolean clearSqCipherDb() {
        return application.getRepository().deleteRepository();
    }

    protected void clearAllPrivateKeyEntries() throws AppResetException {
        KeyStore keyStore = application.getContext().userService().getKeyStore();
        if (keyStore != null) {
            try {
                Enumeration<String> keystoreEnumeration = keyStore.aliases();
                while (keystoreEnumeration.hasMoreElements()) {
                    String keyStoreAlias = keystoreEnumeration.nextElement();
                    keyStore.deleteEntry(keyStoreAlias);
                }
            } catch (KeyStoreException ex) {
                Timber.e(ex);
                throw new AppResetException(ex.getMessage(), ex);
            }
        }
    }

    public boolean addPreResetAppCheck(@NonNull PreResetAppCheck preResetAppCheck) {
        if (!preResetAppChecks.contains(preResetAppCheck)) {
            return preResetAppChecks.add(preResetAppCheck);
        }

        return false;
    }

    public boolean removePreResetAppCheck(@NonNull PreResetAppCheck preResetAppCheck) {
        return preResetAppChecks.remove(preResetAppCheck);
    }

    @Nullable
    public PreResetAppCheck removePreResetAppCheck(@NonNull String checkName) {
        for (PreResetAppCheck preResetAppCheck : preResetAppChecks) {
            if (checkName.equals(preResetAppCheck.getUniqueName())) {
                if (removePreResetAppCheck(preResetAppCheck)) {
                    return preResetAppCheck;
                }

                break;
            }
        }

        return null;
    }

    protected void dismissDialog() {
        if (resetAppDialog != null) {
            resetAppDialog.dismiss();
        }
    }


    @AnyThread
    public void showProgressText(@NonNull String progressText) {
        if (resetCancelled) {
            return;
        }

        appExecutors.mainThread()
                .execute(() -> {
                    if (resetAppDialog != null) {
                        resetAppDialog.showText(progressText);
                    }
                });

    }

}
