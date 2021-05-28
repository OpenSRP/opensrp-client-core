package org.smartregister.util.mock;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.widget.LinearLayout;

import org.smartregister.R;
import org.smartregister.util.OpenSRPImageLoader;

/**
 * Created by kaderchowdhury on 14/11/17.
 */

public class OpenSRPImageLoaderTestActivity extends FragmentActivity {

    private OpenSRPImageLoader openSRPImageLoader;

    @Override
    public void onCreate(Bundle bundle) {
        setTheme(R.style.AppTheme); //we need this here
        super.onCreate(bundle);
        LinearLayout linearLayout;
        linearLayout = new LinearLayout(this);
        openSRPImageLoader = new OpenSRPImageLoader(this);
        setContentView(linearLayout);
    }

    public OpenSRPImageLoaderTestActivity getInstance() {
        return this;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
