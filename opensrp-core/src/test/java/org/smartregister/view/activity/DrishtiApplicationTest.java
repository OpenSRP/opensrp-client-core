package org.smartregister.view.activity;

import android.os.Build;

import com.evernote.android.job.ShadowJobManager;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.TestSyncConfiguration;
import org.smartregister.customshadows.FontTextViewShadow;
import org.smartregister.repository.Repository;
import org.smartregister.shadows.ShadowAppDatabase;
import org.smartregister.shadows.ShadowDrawableResourcesImpl;
import org.smartregister.shadows.ShadowSQLiteDatabase;
import org.smartregister.util.CredentialsHelper;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 30-06-2020.
 */

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {FontTextViewShadow.class
        , ShadowDrawableResourcesImpl.class, ShadowAppDatabase.class, ShadowJobManager.class, ShadowSQLiteDatabase.class}
        , sdk = Build.VERSION_CODES.O_MR1)
public class DrishtiApplicationTest {

    private DrishtiApplication drishtiApplication;

    @Before
    public void setUp() throws Exception {
        drishtiApplication = Mockito.spy(new Application());
        Mockito.doReturn(RuntimeEnvironment.application.getFilesDir()).when(drishtiApplication).getFilesDir();
        Mockito.doReturn(RuntimeEnvironment.application).when(drishtiApplication).getApplicationContext();
        Mockito.doReturn(RuntimeEnvironment.application.getResources()).when(drishtiApplication).getResources();
    }

    @Test
    public void getCachedImageLoaderInstance() {
        drishtiApplication.onCreate();
        Assert.assertNull(ReflectionHelpers.getStaticField(DrishtiApplication.class, "cachedImageLoader"));
        Assert.assertNotNull(DrishtiApplication.getCachedImageLoaderInstance());
    }

    @Test
    public void onCreateShouldRegisterSingletonAndInitialiseCrashReporting() {
        drishtiApplication.onCreate();

        Mockito.verify(drishtiApplication).initializeCrashLyticsTree();
        DrishtiApplication singleton = ReflectionHelpers.getStaticField(DrishtiApplication.class, "mInstance");
        Assert.assertEquals(drishtiApplication, singleton);
    }

    @Test
    public void getRepository() {
        drishtiApplication.onCreate();

        Assert.assertNull(ReflectionHelpers.getField(drishtiApplication, "repository"));
        Assert.assertNotNull(drishtiApplication.getRepository());
    }

    @Test
    public void getPassword() {
        byte[] password = "pwd".getBytes();

        drishtiApplication.onCreate();

        Assert.assertNull(ReflectionHelpers.getField(drishtiApplication, "password"));
        CredentialsHelper credentialsProvider = Mockito.spy(new CredentialsHelper(Mockito.mock(Context.class)));
        Mockito.doReturn(password).when(credentialsProvider).getCredentials(ArgumentMatchers.anyString(), ArgumentMatchers.eq(CredentialsHelper.CREDENTIALS_TYPE.DB_AUTH));

        ReflectionHelpers.setField(drishtiApplication, "credentialsHelper", credentialsProvider);

        Assert.assertEquals(password, drishtiApplication.getPassword());
    }

    @Test
    public void onTerminateShouldClosePendingTransactions() {
        drishtiApplication.onCreate();
        Repository repository = Mockito.mock(Repository.class);
        SQLiteDatabase sqLiteDatabase = Mockito.mock(SQLiteDatabase.class);
        ReflectionHelpers.setField(drishtiApplication, "repository", repository);
        /*
        Context context = Mockito.spy(drishtiApplication.getContext());
        ReflectionHelpers.setField(drishtiApplication, "context", context);*/

        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
        Mockito.doReturn(true).when(sqLiteDatabase).isOpen();
        Mockito.doReturn(true).when(sqLiteDatabase).inTransaction();

        drishtiApplication.onTerminate();
        Mockito.verify(sqLiteDatabase).endTransaction();
        Assert.assertTrue(drishtiApplication.getContext().allSharedPreferences().fetchTransactionsKilledFlag());
    }

    public static class Application extends DrishtiApplication {

        @Override
        public void logoutCurrentUser() {
            // Implement and do nothing
        }

        @Override
        public void onCreate() {
            super.onCreate();

            context = Context.getInstance();
            context.updateApplicationContext(getApplicationContext());
            CoreLibrary.init(context, new TestSyncConfiguration(), 1588062490000l);
        }
    }
}