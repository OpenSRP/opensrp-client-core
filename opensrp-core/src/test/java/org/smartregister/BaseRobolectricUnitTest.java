package org.smartregister;

import android.os.Build;

import com.evernote.android.job.ShadowJobManager;

import org.junit.Rule;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.annotation.Config;
import org.smartregister.customshadows.FontTextViewShadow;
import org.smartregister.customshadows.ShadowLocalBroadcastManager;
import org.smartregister.shadows.ShadowAppDatabase;
import org.smartregister.shadows.ShadowDrawableResourcesImpl;
import org.smartregister.shadows.ShadowSQLiteDatabase;
import org.smartregister.view.UnitTest;

/**
 * Created by onaio on 29/08/2017.
 */

@Config(shadows = {ShadowLocalBroadcastManager.class, FontTextViewShadow.class, ShadowDrawableResourcesImpl.class, ShadowAppDatabase.class, ShadowJobManager.class, ShadowSQLiteDatabase.class}, sdk = Build.VERSION_CODES.O_MR1)
public abstract class BaseRobolectricUnitTest extends UnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    public static final int ASYNC_TIMEOUT = 2000;

    public void initCoreLibrary() {
        TestApplication.getInstance().initCoreLibrary();
    }
}
