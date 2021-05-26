package org.smartregister.view.activity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.event.Listener;
import org.smartregister.service.PendingFormSubmissionService;
import org.smartregister.sync.SyncAfterFetchListener;
import org.smartregister.sync.SyncProgressIndicator;
import org.smartregister.sync.UpdateActionsTask;
import org.smartregister.view.contract.HomeContext;
import org.smartregister.view.controller.NativeAfterANMDetailsFetchListener;
import org.smartregister.view.controller.NativeUpdateANMDetailsTask;

import static java.lang.String.valueOf;
import static org.smartregister.event.Event.ACTION_HANDLED;
import static org.smartregister.event.Event.FORM_SUBMITTED;
import static org.smartregister.event.Event.SYNC_COMPLETED;
import static org.smartregister.event.Event.SYNC_STARTED;

public class NativeHomeActivity extends SecuredActivity {
    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private PendingFormSubmissionService pendingFormSubmissionService;

    private Listener<Boolean> onSyncStartListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(R.layout.progress);
            }
        }
    };
    private TextView ecRegisterClientCountView;
    private TextView ancRegisterClientCountView;
    private TextView pncRegisterClientCountView;
    private TextView fpRegisterClientCountView;
    private TextView childRegisterClientCountView;
    private Listener<Boolean> onSyncCompleteListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            //#TODO: RemainingFormsToSyncCount cannot be updated from a back ground thread!!
            updateRemainingFormsToSyncCount();
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(null);
            }
            updateRegisterCounts();
        }
    };
    private Listener<String> onFormSubmittedListener = new Listener<String>() {
        @Override
        public void onEvent(String instanceId) {
            updateRegisterCounts();
        }
    };
    private Listener<String> updateANMDetailsListener = new Listener<String>() {
        @Override
        public void onEvent(String data) {
            updateRegisterCounts();
        }
    };
    private View.OnClickListener onRegisterStartListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.btn_ec_register) {
                navigationController.startECSmartRegistry();

            } else if (i == R.id.btn_anc_register) {
                navigationController.startANCSmartRegistry();

            } else if (i == R.id.btn_pnc_register) {
                navigationController.startPNCSmartRegistry();

            } else if (i == R.id.btn_child_register) {
                navigationController.startChildSmartRegistry();

            } else if (i == R.id.btn_fp_register) {
                navigationController.startFPSmartRegistry();

            }
        }
    };
    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.btn_reporting) {
                navigationController.startReports();

            } else if (i == R.id.btn_videos) {
                navigationController.startVideos();

            }
        }
    };

    @Override
    protected void onCreation() {
        setContentView(R.layout.smart_registers_home);
        setupViews();
        initialize();
    }

    private void setupViews() {
        findViewById(R.id.btn_ec_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_pnc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_anc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_fp_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_child_register).setOnClickListener(onRegisterStartListener);

        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_videos).setOnClickListener(onButtonsClickListener);

        ecRegisterClientCountView = (TextView) findViewById(R.id.txt_ec_register_client_count);
        pncRegisterClientCountView = (TextView) findViewById(R.id.txt_pnc_register_client_count);
        ancRegisterClientCountView = (TextView) findViewById(R.id.txt_anc_register_client_count);
        fpRegisterClientCountView = (TextView) findViewById(R.id.txt_fp_register_client_count);
        childRegisterClientCountView = (TextView) findViewById(
                R.id.txt_child_register_client_count);
    }

    private void initialize() {
        pendingFormSubmissionService = context().pendingFormSubmissionService();
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);
    }

    @Override
    protected void onResumption() {
        updateRegisterCounts();
        updateSyncIndicator();
        updateRemainingFormsToSyncCount();
    }

    private void updateRegisterCounts() {
        NativeUpdateANMDetailsTask task = new NativeUpdateANMDetailsTask(
                CoreLibrary.getInstance().context().anmController());
        task.fetch(new NativeAfterANMDetailsFetchListener() {
            @Override
            public void afterFetch(HomeContext anmDetails) {
                updateRegisterCounts(anmDetails);
            }
        });
    }

    private void updateRegisterCounts(HomeContext homeContext) {
        ecRegisterClientCountView.setText(valueOf(homeContext.eligibleCoupleCount()));
        ancRegisterClientCountView.setText(valueOf(homeContext.ancCount()));
        pncRegisterClientCountView.setText(valueOf(homeContext.pncCount()));
        fpRegisterClientCountView.setText(valueOf(homeContext.fpCount()));
        childRegisterClientCountView.setText(valueOf(homeContext.childCount()));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateMenuItem = menu.findItem(R.id.updateMenuItem);
        remainingFormsToSyncMenuItem = menu.findItem(R.id.remainingFormsToSyncMenuItem);

        updateSyncIndicator();
        updateRemainingFormsToSyncCount();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.updateMenuItem) {
            updateFromServer();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void updateFromServer() {
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(this, context().actionService(),
                context().formSubmissionSyncService(), new SyncProgressIndicator(),
                context().allFormVersionSyncService());
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SYNC_STARTED.removeListener(onSyncStartListener);
        SYNC_COMPLETED.removeListener(onSyncCompleteListener);
        FORM_SUBMITTED.removeListener(onFormSubmittedListener);
        ACTION_HANDLED.removeListener(updateANMDetailsListener);
    }

    private void updateSyncIndicator() {
        if (updateMenuItem != null) {
            if (context().allSharedPreferences().fetchIsSyncInProgress()) {
                updateMenuItem.setActionView(R.layout.progress);
            } else {
                updateMenuItem.setActionView(null);
            }
        }
    }

    private void updateRemainingFormsToSyncCount() {
        if (remainingFormsToSyncMenuItem == null) {
            return;
        }

        long size = pendingFormSubmissionService.pendingFormSubmissionCount();
        if (size > 0) {
            remainingFormsToSyncMenuItem.setTitle(
                    valueOf(size) + " " + getString(R.string.unsynced_forms_count_message));
            remainingFormsToSyncMenuItem.setVisible(true);
        } else {
            remainingFormsToSyncMenuItem.setVisible(false);
        }
    }
}
