package org.smartregister.view.activity.mock;

import android.os.Bundle;
import androidx.annotation.Nullable;

import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.view.activity.ReportsActivity;

/**
 * Created by Raihan Ahmed on 11/11/17.
 */

public class ReportsActivityMock extends ReportsActivity {

    static Context mockactivitycontext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); //we need this here
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Context context() {
        return mockactivitycontext;
    }

    public static void setContext(Context context) {
        mockactivitycontext = context;
    }


}
