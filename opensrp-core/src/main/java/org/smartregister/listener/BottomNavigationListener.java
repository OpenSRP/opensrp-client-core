package org.smartregister.listener;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

import org.smartregister.R;
import org.smartregister.view.activity.BaseRegisterActivity;

public class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Activity context;

    public BottomNavigationListener(Activity context) {
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (context == null) {
            return false;
        }

        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) context;

        if (item.getItemId() == R.id.action_clients) {
            baseRegisterActivity.switchToBaseFragment();
        } else if (item.getItemId() == R.id.action_search) {
            baseRegisterActivity.switchToFragment(BaseRegisterActivity.ADVANCED_SEARCH_POSITION);
        } else if (item.getItemId() == R.id.action_register) {
            baseRegisterActivity.startRegistration();
        } else if (item.getItemId() == R.id.action_library) {
            baseRegisterActivity.switchToFragment(BaseRegisterActivity.LIBRARY_POSITION);
        } else if (item.getItemId() == R.string.action_me) {
            baseRegisterActivity.switchToFragment(BaseRegisterActivity.ME_POSITION);
        }

        return true;
    }
}
