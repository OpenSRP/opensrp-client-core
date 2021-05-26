package org.smartregister.view.activity.mock;

import android.os.Bundle;
import androidx.annotation.Nullable;

import org.smartregister.R;
import org.smartregister.view.activity.BarcodeScanActivity;

public class BarcodeScanActivityMock extends BarcodeScanActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); //we need this here
        super.onCreate(savedInstanceState);
    }
}
