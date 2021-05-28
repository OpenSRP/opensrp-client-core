package org.smartregister.shadows;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by samuelgithengi on 7/7/20.
 */
@Implements(Snackbar.class)
public class ShadowSnackBar {

    private static Snackbar snackbar;

    @Implementation
    public static Snackbar make(@NonNull View view, @StringRes int resId, int duration) {
        snackbar = Snackbar.make(view, view.getResources().getText(resId), duration);
        return snackbar;
    }

    public static Snackbar getLatestSnackbar() {
        return snackbar;
    }
}
