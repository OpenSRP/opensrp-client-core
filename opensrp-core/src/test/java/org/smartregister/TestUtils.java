package org.smartregister;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by Vincent Karuri on 25/03/2020
 */
public class TestUtils {

    public static android.content.Context getContext(int appVersion) throws PackageManager.NameNotFoundException {
        android.content.Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.versionCode = appVersion;
        doReturn(packageManager).when(context).getPackageManager();
        doReturn(packageInfo).when(packageManager).getPackageInfo(anyString(), anyInt());
        doReturn("").when(context).getPackageName();

        return context;
    }
}
