package org.smartregister.shadows;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 30-06-2020.
 */
@Implements(SQLiteDatabase.class)
public class ShadowSQLiteDatabase {

    @Implementation
    public static synchronized void loadLibs (Context context) {
        // Do nothing to prevent the exception thrown trying to load native libs
    }
}
