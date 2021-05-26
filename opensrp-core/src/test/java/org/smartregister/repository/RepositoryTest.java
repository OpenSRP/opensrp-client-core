package org.smartregister.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.security.SecurityHelper;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;

/**
 * Created by kaderchowdhury on 19/11/17.
 */
@PrepareForTest({DrishtiApplication.class, SQLiteDatabase.class})
public class RepositoryTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private DrishtiApplication drishtiApplication;

    private Repository repository;

    @Mock
    private android.content.Context context;

    @Mock
    private SQLiteDatabase database;

    private String dbName;
    private char[] password;

    @Before
    public void setUp() {
        dbName = "drishti.db";
        password = "Android7832!".toCharArray();

        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);
        PowerMockito.when(drishtiApplication.getApplicationContext()).thenReturn(context);

        ReflectionHelpers.setField(drishtiApplication, "password", SecurityHelper.toBytes(password));
        PowerMockito.when(context.getDir("opensrp", android.content.Context.MODE_PRIVATE)).thenReturn(new File("/"));


        repository = Mockito.spy(Mockito.mock(Repository.class, Mockito.CALLS_REAL_METHODS));
        ReflectionHelpers.setField(repository, "context", context);
        ReflectionHelpers.setField(repository, "dbName", dbName);

        Mockito.doReturn(true).when(database).isOpen();
        ReflectionHelpers.setField(repository, "mDatabase", database);
    }

    @Test
    public void getReadableDatabaseShouldCallGetReadableDbAndPassword() {
        Mockito.doReturn(null).when(repository).getReadableDatabase(password);

        repository.getReadableDatabase();

        Mockito.verify(repository).getReadableDatabase(SecurityHelper.toBytes(password));
    }

    @Test(expected = RuntimeException.class)
    public void getReadableDatabaseShouldThrowRuntimeException() {
        Mockito.doReturn(null).when(drishtiApplication).getPassword();

        repository.getReadableDatabase();
    }

    @Test
    public void getWritableDatabaseShouldCallGetWritableDbAndPassword() {
        Mockito.doReturn(null).when(repository).getWritableDatabase(password);


        repository.getWritableDatabase();

        Mockito.verify(repository).getWritableDatabase(SecurityHelper.toBytes(password));
    }

    @Test(expected = RuntimeException.class)
    public void getWritableDatabaseShouldThrowRuntimeException() {
        Mockito.doReturn(null).when(drishtiApplication).getPassword();

        repository.getWritableDatabase();
    }


    @Test
    public void deleteRepositoryShouldReturnTrueAndCallDeleteDatabaseAndDeleteFile() {

        // Mock the file returned when getting the database path
        File dbPath = Mockito.mock(File.class);
        Mockito.doReturn(dbPath).when(context).getDatabasePath(dbName);
        Mockito.doReturn(true).when(dbPath).delete();

        // Run the method under test
        Assert.assertTrue(repository.deleteRepository());

        // Verify that the db is deleted & actual file is deleted
        Mockito.verify(context).deleteDatabase(dbName);
        Mockito.verify(context).getDatabasePath(dbName);
        Mockito.verify(dbPath).delete();
    }

}
