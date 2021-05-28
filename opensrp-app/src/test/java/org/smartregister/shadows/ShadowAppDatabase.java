package org.smartregister.shadows;

import android.content.Context;
import androidx.annotation.NonNull;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.p2p.model.AppDatabase;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-05-31
 */

@Implements(AppDatabase.class)
public class ShadowAppDatabase {

    private static AppDatabase appDatabase;

    @Implementation
    public static AppDatabase getInstance(@NonNull Context context, @NonNull String passphrase) {
        // Do nothing
        return appDatabase;
    }

    public static void setDb(@NonNull AppDatabase appDatabase) {
        ShadowAppDatabase.appDatabase = appDatabase;
    }
}
