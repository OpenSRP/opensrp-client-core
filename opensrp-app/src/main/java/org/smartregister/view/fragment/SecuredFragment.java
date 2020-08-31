package org.smartregister.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.event.Event;
import org.smartregister.event.Listener;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.activity.FormActivity;
import org.smartregister.view.activity.LoginActivity;
import org.smartregister.view.activity.MicroFormActivity;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.controller.ANMController;
import org.smartregister.view.controller.FormController;
import org.smartregister.view.controller.NavigationController;

import java.util.Map;


/**
 * Created by koros on 10/12/15.
 */
public abstract class SecuredFragment extends Fragment {

    protected Listener<Boolean> logoutListener;
    protected FormController formController;
    protected ANMController anmController;
    protected NavigationController navigationController;
    private String metaData;
    private boolean isPaused;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logoutListener = new Listener<Boolean>() {
            public void onEvent(Boolean data) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    getActivity().finish();
                }
            }
        };
        Event.ON_LOGOUT.addListener(logoutListener);

        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication) this.getActivity()
                    .getApplication();
            application.logoutCurrentUser();
            return;
        }
        if (getActivity() instanceof SecuredActivity) {
            formController = new FormController((SecuredActivity) getActivity());
        }
        anmController = context().anmController();
        navigationController = new NavigationController(getActivity(), anmController);
        onCreation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication) this.getActivity()
                    .getApplication();
            application.logoutCurrentUser();
            return;
        }

        onResumption();
        isPaused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        if (i == R.id.switchLanguageMenuItem) {

            String newLanguagePreference = context().userService().switchLanguagePreference();
            showToastNotification(R.string.language_change_prepend_message + " " + newLanguagePreference + ". " + R.string.language_change_append_message + ".");

            return super.onOptionsItemSelected(item);
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void logoutUser() {
        context().userService().logout();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    protected abstract void onCreation();

    protected abstract void onResumption();

    public void startFormActivity(String formName, String entityId, String metaData) {
        launchForm(formName, entityId, metaData, FormActivity.class);
    }

    public void startMicroFormActivity(String formName, String entityId, String metaData) {
        launchForm(formName, entityId, metaData, MicroFormActivity.class);
    }

    private void launchForm(String formName, String entityId, String metaData, Class formType) {
        this.metaData = metaData;

        Intent intent = new Intent(getActivity(), formType);
        intent.putExtra(AllConstants.FORM_NAME_PARAM, formName);
        intent.putExtra(AllConstants.ENTITY_ID_PARAM, entityId);
        addFieldOverridesIfExist(intent);
        startActivityForResult(intent, AllConstants.FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE);
    }

    private void addFieldOverridesIfExist(Intent intent) {
        if (hasMetadata()) {
            Map<String, String> metaDataMap = new Gson()
                    .fromJson(this.metaData, new TypeToken<Map<String, String>>() {
                    }.getType());
            if (metaDataMap.containsKey(AllConstants.FIELD_OVERRIDES_PARAM)) {
                intent.putExtra(AllConstants.FIELD_OVERRIDES_PARAM, metaDataMap.get(AllConstants.FIELD_OVERRIDES_PARAM));
            }
        }
    }

    private boolean hasMetadata() {
        return this.metaData != null && !this.metaData.equalsIgnoreCase("undefined");
    }

    protected Context context() {
        return CoreLibrary.getInstance().context().updateApplicationContext(this.getActivity().getApplicationContext());
    }

    public boolean isPaused() {
        return isPaused;
    }

    @VisibleForTesting
    protected void showToastNotification(String message) {
        Utils.showShortToast(getActivity(), message);
    }
}
