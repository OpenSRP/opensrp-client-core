package org.smartregister.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.security.SecurityHelper;
import org.smartregister.util.Session;

import java.io.File;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 28-07-2020.
 */
public class RepositoryRobolectricTest extends BaseRobolectricUnitTest {


    @Test
    public void constructor1ShouldCallUpdateMasterRepository() {
        Session session = Mockito.mock(Session.class);
        Mockito.doReturn(AllConstants.DATABASE_NAME).when(session).repositoryName();

        DrishtiRepository drishtiRepository1 = Mockito.mock(DrishtiRepository.class);
        DrishtiRepository drishtiRepository2 = Mockito.mock(DrishtiRepository.class);

        // Mock fetching the database path
        Context context = Mockito.spy(RuntimeEnvironment.application);
        Mockito.doReturn(Mockito.mock(File.class)).when(context).getDatabasePath("drishti.db");

        // Execute the method under test
        Repository repository = new Repository(context, session, drishtiRepository1, drishtiRepository2);


        // Verify
        Mockito.verify(drishtiRepository1).updateMasterRepository(repository);
        Mockito.verify(drishtiRepository2).updateMasterRepository(repository);
    }

    @Test
    public void constructor2ShouldCallUpdateMasterRepository() {
        DrishtiRepository drishtiRepository1 = Mockito.mock(DrishtiRepository.class);
        DrishtiRepository drishtiRepository2 = Mockito.mock(DrishtiRepository.class);

        // Mock fetching the database path
        Context context = Mockito.spy(RuntimeEnvironment.application);
        Mockito.doReturn(Mockito.mock(File.class)).when(context).getDatabasePath("drishti.db");

        // Execute the method under test
        Repository repository = new Repository(context, AllConstants.DATABASE_NAME, 1, Mockito.mock(Session.class), Mockito.mock(CommonFtsObject.class), drishtiRepository1, drishtiRepository2);


        // Verify
        Mockito.verify(drishtiRepository1).updateMasterRepository(repository);
        Mockito.verify(drishtiRepository2).updateMasterRepository(repository);
    }

    @Test
    public void onCreateShouldCallDrishtiRepositoryOnCreate() {
        Repository repository = Mockito.mock(Repository.class, Mockito.CALLS_REAL_METHODS);

        DrishtiRepository drishtiRepository1 = Mockito.mock(DrishtiRepository.class);
        DrishtiRepository drishtiRepository2 = Mockito.mock(DrishtiRepository.class);

        DrishtiRepository[] drishtiRepositories = new DrishtiRepository[2];
        drishtiRepositories[0] = drishtiRepository1;
        drishtiRepositories[1] = drishtiRepository2;

        ReflectionHelpers.setField(repository, "repositories", drishtiRepositories);

        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);

        // Call the method under test
        repository.onCreate(database);

        // Verify calls
        Mockito.verify(drishtiRepository1).onCreate(database);
        Mockito.verify(drishtiRepository2).onCreate(database);
    }

    @Test
    public void onCreateShouldCreateFtsTables() {
        Repository repository = Mockito.mock(Repository.class, Mockito.CALLS_REAL_METHODS);

        String[] tables = new String[]{"ec_client", "vaccine"};
        CommonFtsObject commonFtsObject = new CommonFtsObject(tables);
        commonFtsObject.updateMainConditions("ec_client", new String[]{"opensrp_id", "created_at"});
        commonFtsObject.updateMainConditions("vaccine", new String[]{"vaccine", "no"});

        ReflectionHelpers.setField(repository, "repositories", new DrishtiRepository[0]);
        ReflectionHelpers.setField(repository, "commonFtsObject", commonFtsObject);

        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);

        // Call the method under test
        repository.onCreate(database);


        // Verify calls
        Mockito.verify(database).execSQL(Mockito.contains("create virtual table ec_client_search using fts4 (object_id,object_relational_id,phrase,is_closed TINYINT DEFAULT 0,opensrp_id,created_at)"));
        Mockito.verify(database).execSQL(Mockito.contains("create virtual table vaccine_search using fts4 (object_id,object_relational_id,phrase,is_closed TINYINT DEFAULT 0,vaccine,no)"));
    }


    @Test
    public void onCreateShouldCreateFtsTablesWithSortFieldIncluded() {
        Repository repository = Mockito.mock(Repository.class, Mockito.CALLS_REAL_METHODS);

        String[] tables = new String[]{"ec_client", "vaccine"};
        CommonFtsObject commonFtsObject = new CommonFtsObject(tables);
        commonFtsObject.updateMainConditions("ec_client", new String[]{"opensrp_id", "created_at"});
        commonFtsObject.updateMainConditions("vaccine", new String[]{"vaccine", "no"});
        commonFtsObject.updateSortFields("vaccine", new String[]{"created_at", "alerts.priority"});

        ReflectionHelpers.setField(repository, "repositories", new DrishtiRepository[0]);
        ReflectionHelpers.setField(repository, "commonFtsObject", commonFtsObject);

        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);

        // Call the method under test
        repository.onCreate(database);


        // Verify calls
        Mockito.verify(database).execSQL(Mockito.contains("create virtual table ec_client_search using fts4 (object_id,object_relational_id,phrase,is_closed TINYINT DEFAULT 0,opensrp_id,created_at)"));
        Mockito.verify(database).execSQL(Mockito.contains("create virtual table vaccine_search using fts4 (object_id,object_relational_id,phrase,is_closed TINYINT DEFAULT 0,vaccine,no,created_at,priority)"));
    }

    @Test
    public void canUseThisPasswordShouldCallIsDatabaseWritableAndReturnTrue() {
        Repository repository = Mockito.mock(Repository.class, Mockito.CALLS_REAL_METHODS);
        byte[] password = SecurityHelper.toBytes("mypwd".toCharArray());

        Mockito.doReturn(true).when(repository).isDatabaseWritable(password);
        Assert.assertTrue(repository.canUseThisPassword(password));
    }
}
