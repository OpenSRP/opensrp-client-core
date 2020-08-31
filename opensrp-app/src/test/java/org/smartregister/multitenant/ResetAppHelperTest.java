package org.smartregister.multitenant;

import androidx.sqlite.db.SupportSQLiteOpenHelper;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.P2POptions;
import org.smartregister.exception.AppResetException;
import org.smartregister.exception.PreResetAppOperationException;
import org.smartregister.listener.OnCompleteClearDataCallback;
import org.smartregister.multitenant.check.PreResetAppCheck;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;
import org.smartregister.p2p.model.AppDatabase;
import org.smartregister.p2p.model.dao.ReceiverTransferDao;
import org.smartregister.p2p.model.dao.SenderTransferDao;
import org.smartregister.repository.Repository;
import org.smartregister.service.ZiggyService;
import org.smartregister.shadows.ShadowAppDatabase;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.activity.mock.ReportsActivityMock;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 14-04-2020.
 */

public class ResetAppHelperTest extends BaseRobolectricUnitTest {

    private ResetAppHelper resetAppHelper;

    @Before
    public void setUp() throws Exception {
        resetAppHelper = Mockito.spy(new ResetAppHelper(DrishtiApplication.getInstance()));
    }

    @Test
    public void startResetProcess() {
        Context context = Mockito.spy(CoreLibrary.getInstance().context());
        Mockito.doReturn(Mockito.mock(ZiggyService.class)).when(context).ziggyService();
        ReportsActivityMock.setContext(context);
        ReportsActivityMock formActivity = Robolectric.buildActivity(ReportsActivityMock.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();

        resetAppHelper.startResetProcess(formActivity, null);
        Mockito.verify(resetAppHelper).performPreResetChecksAndResetProcess(Mockito.nullable(OnCompleteClearDataCallback.class));
    }

    @Test
    public void performPreResetChecksShouldPerformChecksOnAllComponents() throws PreResetAppOperationException {
        AppExecutors appExecutors = ReflectionHelpers.getField(resetAppHelper, "appExecutors");
        Executor diskIoExceutor = Mockito.spy((Executor) ReflectionHelpers.getField(appExecutors, "diskIO"));
        Executor networkIoExceutor = Mockito.spy((Executor) ReflectionHelpers.getField(appExecutors, "networkIO"));
        Executor mainThreadExceutor = Mockito.spy((Executor) ReflectionHelpers.getField(appExecutors, "mainThread"));

        Mockito.doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(diskIoExceutor).execute(Mockito.any(Runnable.class));

        Mockito.doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(networkIoExceutor).execute(Mockito.any(Runnable.class));

        Mockito.doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(mainThreadExceutor).execute(Mockito.any(Runnable.class));

        ReflectionHelpers.setField(appExecutors, "diskIO", diskIoExceutor);
        ReflectionHelpers.setField(appExecutors, "networkIO", networkIoExceutor);
        ReflectionHelpers.setField(appExecutors, "mainThread", mainThreadExceutor);

        ArrayList<PreResetAppCheck> preResetAppChecks = ReflectionHelpers.getField(resetAppHelper, "preResetAppChecks");
        ArrayList<PreResetAppCheck> mockedPreResetAppChecks = new ArrayList<>();

        for (PreResetAppCheck preResetAppCheck: preResetAppChecks) {
            preResetAppCheck = Mockito.spy(preResetAppCheck);
            Mockito.doReturn(false).when(preResetAppCheck).isCheckOk(Mockito.eq(DrishtiApplication.getInstance()));
            Mockito.doNothing().when(preResetAppCheck).performPreResetAppOperations(Mockito.eq(DrishtiApplication.getInstance()));
            mockedPreResetAppChecks.add(preResetAppCheck);
        }

        ReflectionHelpers.setField(resetAppHelper, "preResetAppChecks", mockedPreResetAppChecks);

        resetAppHelper.performPreResetChecksAndResetProcess(null);

        assertEquals(4, preResetAppChecks.size());
        for (PreResetAppCheck preResetAppCheck: mockedPreResetAppChecks) {
            Mockito.verify(preResetAppCheck).isCheckOk(Mockito.eq(DrishtiApplication.getInstance()));
        }

        Mockito.verify(resetAppHelper).dismissDialog();
    }

    @Test
    public void performResetOperations() throws AppResetException {
        Mockito.doNothing().when(resetAppHelper).clearP2PDb();


        resetAppHelper.performResetOperations();
        Mockito.verify(resetAppHelper).clearAllPrivateKeyEntries();
        Mockito.verify(resetAppHelper).clearSqCipherDb();
        Mockito.verify(resetAppHelper).clearP2PDb();
        Mockito.verify(resetAppHelper).clearSharedPreferences();
    }

    @Test
    public void clearP2PDb() {
        P2POptions p2POptions = new P2POptions(true);

        P2PLibrary.Options p2PLibraryOptions = new P2PLibrary.Options(RuntimeEnvironment.application, "team-id", "username", Mockito.mock(P2PAuthorizationService.class), Mockito.mock(ReceiverTransferDao.class), Mockito.mock(SenderTransferDao.class));
        P2PLibrary.init(p2PLibraryOptions);
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "p2POptions", p2POptions);

        AppDatabase appDatabase = Mockito.mock(AppDatabase.class);
        SupportSQLiteOpenHelper supportSQLiteOpenHelper = Mockito.mock(SupportSQLiteOpenHelper.class);


        ShadowAppDatabase.setDb(appDatabase);
        Mockito.doReturn(supportSQLiteOpenHelper).when(appDatabase).getOpenHelper();
        Mockito.doReturn("dummy_path").when(supportSQLiteOpenHelper).getDatabaseName();

        resetAppHelper.clearP2PDb();

        Mockito.verify(appDatabase).clearAllTables();
        Mockito.verify(supportSQLiteOpenHelper).getDatabaseName();
    }

    @Test
    public void performPreResetOperations() throws PreResetAppOperationException {
        PreResetAppCheck preResetAppCheck = Mockito.mock(PreResetAppCheck.class);
        resetAppHelper.performPreResetOperations(preResetAppCheck);

        Mockito.verify(preResetAppCheck).performPreResetAppOperations(Mockito.any());
    }

    @Test
    public void clearSharedPreferences() {
        SharedPreferences sharedPreferences = DrishtiApplication.getInstance().getContext().allSharedPreferences().getPreferences();
        sharedPreferences.edit().putString("1", "value");
        sharedPreferences.edit().putString("2", "value2");
        sharedPreferences.edit().putString("3", "value3");
        sharedPreferences.edit().putString("4", "value4");

        resetAppHelper.clearSharedPreferences();

        assertNull(sharedPreferences.getString("1", null));
        assertNull(sharedPreferences.getString("2", null));
        assertNull(sharedPreferences.getString("3", null));
        assertNull(sharedPreferences.getString("4", null));
    }

    @Test
    public void clearSqCipherDb() {
        Repository repository = DrishtiApplication.getInstance().getRepository();

        Mockito.doReturn(true).when(repository).deleteRepository();

        assertTrue(resetAppHelper.clearSqCipherDb());

        Mockito.verify(repository).deleteRepository();
    }

    @Test
    public void addPreResetAppCheck() {
        PreResetAppCheck appCheck = Mockito.mock(PreResetAppCheck.class);

        assertFalse(resetAppHelper.removePreResetAppCheck(appCheck));
        assertTrue(resetAppHelper.addPreResetAppCheck(appCheck));
        assertTrue(resetAppHelper.removePreResetAppCheck(appCheck));
    }

    @Test
    public void testRemovePreResetAppCheck() {
        String appCheckName = "GIVE_UP";
        PreResetAppCheck appCheck = Mockito.mock(PreResetAppCheck.class);
        Mockito.doReturn(appCheckName).when(appCheck).getUniqueName();

        assertNull(resetAppHelper.removePreResetAppCheck(appCheckName));
        assertTrue(resetAppHelper.addPreResetAppCheck(appCheck));
        assertEquals(appCheck, resetAppHelper.removePreResetAppCheck(appCheckName));
    }
}