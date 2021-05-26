package org.smartregister.sync.mock;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.smartregister.R;

/**
 * Created by kaderchowdhury on 13/11/17.
 */

public class MockActivity extends Activity {

    @Override
    public void onCreate(Bundle bundle) {
        setTheme(R.style.AppTheme); //we need this here
        super.onCreate(bundle);
        LinearLayout linearLayout;
        linearLayout = new LinearLayout(this);
        setContentView(linearLayout);
    }

    public MockActivity getInstance() {
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
