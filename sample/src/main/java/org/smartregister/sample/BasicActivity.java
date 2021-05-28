package org.smartregister.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.smartregister.sample.fragment.ReportFragment;
import org.smartregister.view.activity.MultiLanguageActivity;

public class BasicActivity extends MultiLanguageActivity {
    public static final String DISPLAY_FRAGMENT = "DISPLAY_FRAGMENT";

    public static void startFragment(Activity activity, String fragmentName) {
        Intent intent = new Intent(activity, BasicActivity.class);
        intent.putExtra(DISPLAY_FRAGMENT, fragmentName);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_activity);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String fragmentName = bundle.getString(DISPLAY_FRAGMENT);
            Fragment fragment = getDestinationFragment(fragmentName);
            if (fragment != null)
                switchToFragment(fragment);

        }
    }

    private void switchToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, fragment)
                .commit();
    }

    @Nullable
    private Fragment getDestinationFragment(@Nullable String destinationFragment) {
        if (destinationFragment == null)
            return null;

        switch (destinationFragment) {
            case ReportFragment
                    .TAG:
                return new ReportFragment();
            default:
                return null;
        }
    }
}
