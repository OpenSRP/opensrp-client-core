package org.smartregister.shadows;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by samuelgithengi on 7/7/20.
 */
@Implements(ContextCompat.class)
public class ShadowContextCompat {

    @Implementation
    public static int checkSelfPermission(@NonNull Context context, @NonNull String permission) {
        return PackageManager.PERMISSION_GRANTED;
    }
}
