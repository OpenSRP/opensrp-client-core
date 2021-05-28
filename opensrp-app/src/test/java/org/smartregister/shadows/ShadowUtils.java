package org.smartregister.shadows;

import android.content.Context;
import android.content.pm.PackageInfo;

import androidx.annotation.NonNull;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.BuildConfig;
import org.smartregister.util.Utils;

/**
 * Created by samuelgithengi on 11/10/20.
 */
@Implements(Utils.class)
public class ShadowUtils {

    @Implementation
    private static PackageInfo getPackageInfo(@NonNull Context context) {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.versionName = BuildConfig.VERSION_NAME;
        packageInfo.packageName = BuildConfig.LIBRARY_PACKAGE_NAME;
        return packageInfo;
    }
}
