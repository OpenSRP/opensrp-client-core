package org.smartregister.shadows;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.mockito.Mockito;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowActivity;
import org.smartregister.Context;
import org.smartregister.view.activity.SecuredActivity;

/**
 * Created by kaderchowdhury on 17/12/17.
 */
@Implements(SecuredActivity.class)
public class SecuredActivityShadow extends ShadowActivity {
    public SecuredActivityShadow() {

    }

    @Implementation
    protected void onCreate(Bundle savedInstanceState) {

    }

    @Implementation
    protected void onResume() {

    }

    @Implementation
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Implementation
    protected void attachLogoutMenuItem(Menu menu) {
    }

    @Implementation
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Implementation
    protected void onDestroy() {
    }

    @Implementation
    public void startFormActivity(String formName, String entityId, String metaData) {

    }

    @Implementation
    public void startMicroFormActivity(String formName, String entityId, String metaData) {

    }

    @Implementation
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Implementation
    public void replicationComplete() {

    }

    @Implementation
    public void replicationError() {

    }

    @Implementation
    public void showToast(String msg) {

    }

    @Implementation
    protected Context context() {
        return Mockito.mock(org.smartregister.Context.class);
    }

    @Implementation
    protected void onCreation() {

    }

    @Implementation
    protected void onResumption() {

    }
}
