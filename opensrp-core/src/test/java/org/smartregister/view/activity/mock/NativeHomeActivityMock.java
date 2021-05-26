package org.smartregister.view.activity.mock;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Menu;

import org.smartregister.R;
import org.smartregister.view.activity.NativeHomeActivity;

/**
 * Created by kaderchowdhury on 11/11/17.
 */

public class NativeHomeActivityMock extends NativeHomeActivity {

//    NativeECSmartRegisterActivity activity;

    @Override
    protected void onStart() {
        super.onStart();
        //   activity = new NativeECSmartRegisterActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_registers_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);

        return true;
    }


}
