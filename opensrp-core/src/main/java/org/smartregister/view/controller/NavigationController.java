package org.smartregister.view.controller;

import android.app.Activity;
import android.content.Intent;

import org.smartregister.view.activity.NativeECSmartRegisterActivity;
import org.smartregister.view.activity.ReportsActivity;
import org.smartregister.view.activity.VideosActivity;

import static org.smartregister.view.controller.ProfileNavigationController.navigateToECProfile;

public class NavigationController {
    private Activity activity;
    private ANMController anmController;

    public NavigationController(Activity activity, ANMController anmController) {
        this.activity = activity;
        this.anmController = anmController;
    }

    public void startReports() {
        activity.startActivity(new Intent(activity, ReportsActivity.class));
    }

    public void startVideos() {
        activity.startActivity(new Intent(activity, VideosActivity.class));
    }

    public void startECSmartRegistry() {
        activity.startActivity(new Intent(activity, NativeECSmartRegisterActivity.class));
    }

    public void startFPSmartRegistry() {
//        activity.startActivity(new Intent(activity, NativeFPSmartRegisterActivity.class));
    }

    public void startANCSmartRegistry() {
//        activity.startActivity(new Intent(activity, NativeANCSmartRegisterActivity.class));
    }

    public void startPNCSmartRegistry() {
//        activity.startActivity(new Intent(activity, NativePNCSmartRegisterActivity.class));
    }

    public void startChildSmartRegistry() {
//        activity.startActivity(new Intent(activity, NativeChildSmartRegisterActivity.class));
    }

    public String get() {
        return anmController.get();
    }

    public void goBack() {
        activity.finish();
    }

    public void startEC(String entityId) {
        navigateToECProfile(activity, entityId);
    }

}
