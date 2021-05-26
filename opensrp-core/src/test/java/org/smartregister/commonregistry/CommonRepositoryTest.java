package org.smartregister.commonregistry;

import android.content.ContentValues;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.ColumnDetails;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by onaio on 29/08/2017.
 */

public class CommonRepositoryTest extends BaseUnitTest {

    public static final String ADDITIONALCOLUMN = "ADDITIONALCOLUMN";
    public static final String CUSTOMRELATIONALID = "CUSTOMRELATIONALID";


    private CommonRepository commonRepository;

    @Mock
    private Repository repository;

    @Mock
    private CommonFtsObject commonFtsObject;
    @Mock
    private SQLiteDatabase sqliteDatabase;

    private String tablename;

    private String[] tableColumns;


    @Before
    public void setUp() throws Exception {

        tablename = "ec_client";
        tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCall() throws Exception {
        String tablename = "";
        ColumnDetails[] tableColumns = new ColumnDetails[0];
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        CommonRepository commonRepository = new CommonRepository(tablename, tableColumns);
        Assert.assertNotNull(commonRepository);
        Assert.assertNotNull(new CommonRepository(commonFtsObject, tablename, tableColumns));
    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCallWithAdditionalColumns() throws Exception {
        String tablename = "";
        ColumnDetails[] tableColumns = new ColumnDetails[]{
                ColumnDetails.builder().name(CommonRepository.BASE_ENTITY_ID_COLUMN).build(),
                ColumnDetails.builder().name(CommonRepository.Relational_Underscore_ID).build(),
                ColumnDetails.builder().name(ADDITIONALCOLUMN).build(),
                ColumnDetails.builder().name(CUSTOMRELATIONALID).defaultValue("0").build()
        };
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        Mockito.when(commonFtsObject.getCustomRelationalId(Mockito.anyString())).thenReturn(CUSTOMRELATIONALID);
        Assert.assertNotNull(new CommonRepository(commonFtsObject, tablename, tableColumns));
    }

    @Test
    public void addCallsDatabaseInsert1times() throws Exception {
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        commonRepository.add(new CommonPersonObject("", "", new HashMap<String, String>(), ""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void findByCaseIDCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findByCaseID(""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class));

    }

    @Test
    public void findByBaseEntityIdCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findByBaseEntityId(""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class));

    }

    @Test
    public void findByGOBHHIDCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findHHByGOBHHID(""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class));

    }

    @Test
    public void allcommonCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.allcommon());
        Mockito.verify(sqliteDatabase, Mockito.times(1)).query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class));

    }

    @Test
    public void findByCaseIDsCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findByCaseIDs("caseID1", "caseID2"));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void findByRelationalIDsCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findByRelationalIDs("relationalID", "relationalID2"));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void findByRelational_IDsCallsDatabaseQuery1times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.findByRelational_IDs("relationalID", "relationalID2"));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void customQueryReturnsNotNUll() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.customQuery("", new String[]{}, ""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void customQueryForCompleteRowReturnsNotNUll() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.customQueryForCompleteRow("", new String[]{}, ""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void rawCustomQueryForAdapterRowReturnsNotNUll() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(commonRepository.rawCustomQueryForAdapter(""));
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String[].class));

    }

    @Test
    public void readAllcommonforCursorAdapterReturnsNotNUll() throws Exception {
        String[] columns = new String[]{"_id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        matrixCursor.moveToFirst();
        Assert.assertNotNull(commonRepository.readAllcommonforCursorAdapter(matrixCursor));

    }

    @Test
    public void readAllcommonforFieldReturnsNotNUll() throws Exception {
        String[] columns = new String[]{"_id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{0, 0, 0, 0});
        matrixCursor.addRow(new Object[]{1, 1, 1, 1});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        matrixCursor.moveToFirst();
        Assert.assertNotNull(commonRepository.readAllcommonForField(matrixCursor, ""));

    }

    @Test
    public void updateDetailsCallsDatabaseUpdate1Times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        commonRepository.updateDetails("caseID", new HashMap<String, String>());
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void mergeDetailsCallsDatabaseUpdate1Times() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor matrixCursor = new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        matrixCursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        commonRepository.mergeDetails("caseID", new HashMap<String, String>());
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void assertOnCreateCallsDatabaseExec() {
        String tablename = "";
        ColumnDetails[] tableColumns = new ColumnDetails[]{
                ColumnDetails.builder().name(CommonRepository.BASE_ENTITY_ID_COLUMN).build(),
                ColumnDetails.builder().name(CommonRepository.Relational_Underscore_ID).build(),
                ColumnDetails.builder().name(ADDITIONALCOLUMN).build(),
                ColumnDetails.builder().name(CUSTOMRELATIONALID).defaultValue("0").build()
        };
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        Mockito.when(commonFtsObject.getCustomRelationalId(Mockito.anyString())).thenReturn(CUSTOMRELATIONALID);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        Assert.assertNotNull(commonRepository);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.onCreate(sqliteDatabase);
        Mockito.verify(sqliteDatabase, Mockito.times(6)).execSQL(Mockito.anyString());
    }


    @Test
    public void assertExecuteInsertStatementReturnsId() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        cursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "table";
        ColumnDetails[] tableColumns = new ColumnDetails[]{
                ColumnDetails.builder().name(CommonRepository.BASE_ENTITY_ID_COLUMN).build(),
                ColumnDetails.builder().name(CommonRepository.Relational_Underscore_ID).build(),
                ColumnDetails.builder().name(ADDITIONALCOLUMN).build(),
                ColumnDetails.builder().name(CUSTOMRELATIONALID).defaultValue("0").build()
        };
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        Mockito.when(commonFtsObject.getCustomRelationalId(Mockito.anyString())).thenReturn(CUSTOMRELATIONALID);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        Assert.assertNotNull(commonRepository);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        String baseEntityId = "1";
        String query =
                "SELECT  * FROM " + tablename + " WHERE base_entity_id = ?";
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(sqliteDatabase.rawQuery(query, new String[]{baseEntityId})).thenReturn(cursor);
        ContentValues cv = new ContentValues();
        cv.put("base_entity_id", "1");
        commonRepository.updateMasterRepository(repository);
        Long id = 0l;
        Assert.assertEquals(commonRepository.executeInsertStatement(cv, tablename), id);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insertWithOnConflict(Mockito.anyString(), Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyInt());
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(query, new String[]{"1"});
    }

    @Test
    public void assertQueryTableReturnsCursor() throws Exception {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        cursor.addRow(new Object[]{"caseID2", "relationalID2", new HashMap<String, String>(), 0});
        String tablename = "table";
        ColumnDetails[] tableColumns = new ColumnDetails[]{
                ColumnDetails.builder().name(CommonRepository.BASE_ENTITY_ID_COLUMN).build(),
                ColumnDetails.builder().name(CommonRepository.Relational_Underscore_ID).build(),
                ColumnDetails.builder().name(ADDITIONALCOLUMN).build(),
                ColumnDetails.builder().name(CUSTOMRELATIONALID).defaultValue("0").build()
        };
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        Mockito.when(commonFtsObject.getCustomRelationalId(Mockito.anyString())).thenReturn(CUSTOMRELATIONALID);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        Assert.assertNotNull(commonRepository);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        String baseEntityId = "1";
        String query =
                "SELECT  * FROM " + tablename + " WHERE base_entity_id = '" + baseEntityId + "'";
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(cursor);
        commonRepository.updateMasterRepository(repository);
        org.junit.Assert.assertEquals(commonRepository.queryTable(query), cursor);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).rawQuery(query, null);
    }

    @Test
    public void assertCloseCaseCallsDatabaseExec() {
        String tablename = "table";
        String baseEntityId = "1";
        ColumnDetails[] tableColumns = new ColumnDetails[]{
                ColumnDetails.builder().name(CommonRepository.BASE_ENTITY_ID_COLUMN).build(),
                ColumnDetails.builder().name(CommonRepository.Relational_Underscore_ID).build(),
                ColumnDetails.builder().name(ADDITIONALCOLUMN).build(),
                ColumnDetails.builder().name(CUSTOMRELATIONALID).defaultValue("0").build()
        };
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.closeCase(baseEntityId, tablename);
        commonRepository.updateMasterRepository(repository);
        commonRepository.closeCase(baseEntityId, tablename);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertDeleteCaseCallsDatabaseExec() {
        String tablename = "table";
        String baseEntityId = "1";
        ColumnDetails[] tableColumns = new ColumnDetails[]{
                ColumnDetails.builder().name(CommonRepository.BASE_ENTITY_ID_COLUMN).build(),
                ColumnDetails.builder().name(CommonRepository.Relational_Underscore_ID).build(),
                ColumnDetails.builder().name(ADDITIONALCOLUMN).build(),
                ColumnDetails.builder().name(CUSTOMRELATIONALID).defaultValue("0").build()
        };
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Assert.assertEquals(commonRepository.deleteCase(baseEntityId, tablename), false);
        Mockito.when(sqliteDatabase.delete(Mockito.anyString(), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        commonRepository.updateMasterRepository(repository);
        Assert.assertEquals(commonRepository.deleteCase(baseEntityId, tablename), true);

    }

    @Test
    public void assertRawQueryReturnsMap() {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"caseID", "relationalID", "dd1", "0"});
        cursor.addRow(new Object[]{"caseID2", "relationalID2", "dd2", "0"});

        String tablename = "table";
        ColumnDetails[] tableColumns = new ColumnDetails[]{
                ColumnDetails.builder().name(CommonRepository.BASE_ENTITY_ID_COLUMN).build(),
                ColumnDetails.builder().name(CommonRepository.Relational_Underscore_ID).build(),
                ColumnDetails.builder().name(ADDITIONALCOLUMN).build(),
                ColumnDetails.builder().name(CUSTOMRELATIONALID).defaultValue("0").build()
        };
        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        Mockito.when(commonFtsObject.getCustomRelationalId(Mockito.anyString())).thenReturn(CUSTOMRELATIONALID);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tablename, tableColumns);
        Assert.assertNotNull(commonRepository);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        repository = Mockito.mock(Repository.class);
        String baseEntityId = "1";
        String query =
                "SELECT  * FROM " + tablename + " WHERE base_entity_id = ?";
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(cursor);
        commonRepository.updateMasterRepository(repository);
        Assert.assertNotNull(commonRepository.rawQuery(query, new String[]{baseEntityId}));
    }

    @Test
    public void assertPopulateSearchValuesByCaseIdReturnsContentValue() {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tableName = "common";
        ColumnDetails[] tableColumns = new ColumnDetails[0];
        String[] tables = {"common", "common2"};
        String[] mainConditions = {"details"};
        String[] shortFields = {"id", "alerts.relationalid", "alerts.details", "is_closed"};

        String[] columns2 = new String[]{"id", "name"};
        MatrixCursor cursor2 = new MatrixCursor(columns2);
        cursor2.addRow(new Object[]{"caseID", "details"});


        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tableName, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);

        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(cursor);
        Mockito.when(commonFtsObject.getSearchFields(Mockito.anyString())).thenReturn(columns);
        Mockito.when(commonFtsObject.getTables()).thenReturn(tables);
        Mockito.when(commonFtsObject.getMainConditions(Mockito.anyString())).thenReturn(mainConditions);
        Mockito.when(commonFtsObject.getSortFields(Mockito.anyString())).thenReturn(shortFields);
        tableName = "common2";
        String query = "PRAGMA table_info(" + tableName + ")";
        Mockito.when(sqliteDatabase.rawQuery(query, null)).thenReturn(cursor2);
        Assert.assertNotNull(commonRepository.populateSearchValues("caseID"));
    }

    @Test
    public void assertPopulateSearchValuesReturnsContentBoolean() {
        String[] columns = new String[]{"id", "relationalid", "details", "is_closed"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"caseID", "relationalID", new HashMap<String, String>(), 0});
        String tableName = "common";
        ColumnDetails[] tableColumns = new ColumnDetails[0];
        String[] tables = {"common"};
        String[] mainConditions = {"details"};
        String[] shortFields = {"id", "alerts.relationalid", "alerts.details", "is_closed"};

        String[] columns2 = new String[]{"id", "name", CommonFtsObject.phraseColumn};
        MatrixCursor cursor2 = new MatrixCursor(columns2);
        cursor2.addRow(new Object[]{"caseID", "details", "| hello| world"});


        commonFtsObject = Mockito.mock(CommonFtsObject.class);
        CommonRepository commonRepository = new CommonRepository(commonFtsObject, tableName, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);

        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(cursor);
        Mockito.when(commonFtsObject.getSearchFields(Mockito.anyString())).thenReturn(columns);
        Mockito.when(commonFtsObject.getTables()).thenReturn(tables);
        Mockito.when(commonFtsObject.getMainConditions(Mockito.anyString())).thenReturn(mainConditions);
        Mockito.when(commonFtsObject.getSortFields(Mockito.anyString())).thenReturn(shortFields);

        String query = "SELECT object_id, field FROM common_search WHERE  object_id MATCH ?";
        Mockito.when(sqliteDatabase.rawQuery(query, new String[]{"caseID"})).thenReturn(cursor2);
        Assert.assertEquals(commonRepository.populateSearchValues("caseID", "field", "value", new String[]{"details"}), false);
        Mockito.when(sqliteDatabase.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        query = "SELECT object_id, phrase FROM common_search WHERE  object_id MATCH ?";
        Mockito.when(sqliteDatabase.rawQuery(query, new String[]{"caseID"})).thenReturn(cursor2);
        Assert.assertEquals(commonRepository.populateSearchValues("caseID", CommonFtsObject.phraseColumn, "hello_world", new String[]{"hello"}), true);
    }

    @Test
    public void deleteSearchRecordShouldReturnTrueAndCommitTransaction() {
        String tablename = "ec_client";
        String[] tableColumns = new String[]{};
        commonRepository = new CommonRepository(tablename, tableColumns);
        repository = Mockito.mock(Repository.class);
        sqliteDatabase = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        commonRepository.updateMasterRepository(repository);

        ArgumentCaptor<String[]> caseIdCaptor = ArgumentCaptor.forClass(String[].class);
        Mockito.doReturn(2).when(sqliteDatabase).delete(Mockito.eq("ec_client_search"), Mockito.eq("object_id MATCH ?"), caseIdCaptor.capture());

        String caseId = "my-case-id";
        Assert.assertTrue(commonRepository.deleteSearchRecord(caseId));
        Assert.assertEquals(caseId, caseIdCaptor.getValue()[0]);

        Mockito.verify(sqliteDatabase).beginTransaction();
        Mockito.verify(sqliteDatabase).setTransactionSuccessful();
        Mockito.verify(sqliteDatabase).endTransaction();
    }

    @Test
    public void deleteSearchRecordShouldReturnFalseAndEndTransactionWhenExceptionOccurs() {
        ArgumentCaptor<String[]> caseIdCaptor = ArgumentCaptor.forClass(String[].class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new Exception("An error occurred");
            }
        }).when(sqliteDatabase).delete(Mockito.eq("ec_client_search"), Mockito.eq("object_id MATCH ?"), caseIdCaptor.capture());

        String caseId = "my-case-id";
        Assert.assertFalse(commonRepository.deleteSearchRecord(caseId));
        Assert.assertEquals(caseId, caseIdCaptor.getValue()[0]);

        Mockito.verify(sqliteDatabase).beginTransaction();
        Mockito.verify(sqliteDatabase, Mockito.times(0)).setTransactionSuccessful();
        Mockito.verify(sqliteDatabase).endTransaction();
    }

    @Test
    public void searchBatchInsertsShouldReturnFalse() {
        HashMap<String, ContentValues> searchMap = new HashMap<>();
        ContentValues contentValues = new ContentValues();
        searchMap.put("sample-case-id", contentValues);

        Mockito.doThrow(new SQLException("Some exception")).when(sqliteDatabase).insert(Mockito.eq(tablename + "_search"), Mockito.nullable(String.class), Mockito.eq(contentValues));

        Assert.assertFalse(commonRepository.searchBatchInserts(searchMap));
        Mockito.verify(sqliteDatabase).endTransaction();
        Mockito.verify(sqliteDatabase, Mockito.times(0)).setTransactionSuccessful();
    }

    @Test
    public void searchBatchInsertsShouldReturnTrueAndCallSqliteDbInsert() {
        HashMap<String, ContentValues> searchMap = new HashMap<>();
        ContentValues contentValues = new ContentValues();
        searchMap.put("sample-case-id", contentValues);

        Assert.assertTrue(commonRepository.searchBatchInserts(searchMap));
        Mockito.verify(sqliteDatabase).insert(Mockito.eq(tablename + "_search"), Mockito.nullable(String.class), Mockito.eq(contentValues));
        Mockito.verify(sqliteDatabase).endTransaction();
        Mockito.verify(sqliteDatabase).setTransactionSuccessful();
    }

    @Test
    public void searchBatchInsertsShouldReturnTrueAndCallSqliteDbUpdate() {
        commonRepository = Mockito.spy(commonRepository);
        HashMap<String, ContentValues> searchMap = new HashMap<>();
        ContentValues contentValues = new ContentValues();
        String caseId = "sample-case-id";
        searchMap.put(caseId, contentValues);

        ArrayList<HashMap<String, String>> mapList = new ArrayList<>();
        mapList.add(new HashMap<>());

        Mockito.doReturn(mapList).when(commonRepository).rawQuery(Mockito.eq("SELECT object_id FROM " + tablename + "_search WHERE object_id MATCH ?"), Mockito.any(String[].class));

        ArgumentCaptor<String[]> caseIdArgumentCaptor = ArgumentCaptor.forClass(String[].class);

        Assert.assertTrue(commonRepository.searchBatchInserts(searchMap));
        Mockito.verify(sqliteDatabase).update(Mockito.eq(tablename + "_search"), Mockito.eq(contentValues), Mockito.eq("object_id MATCH ?"), caseIdArgumentCaptor.capture());
        Assert.assertEquals(caseId, caseIdArgumentCaptor.getValue()[0]);

        Mockito.verify(sqliteDatabase).endTransaction();
        Mockito.verify(sqliteDatabase).setTransactionSuccessful();
    }


    @Test
    public void findSearchIdsShouldReturnListOfIds() {
        String query = "SELECT object_id FROM ec_client_search";

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"object_id"});
        matrixCursor.addRow(new Object[]{"id-1"});
        matrixCursor.addRow(new Object[]{"id-2"});
        matrixCursor.addRow(new Object[]{"id-3"});

        Mockito.doReturn(matrixCursor).when(sqliteDatabase).rawQuery(Mockito.eq(query), Mockito.nullable(String[].class));
        List<String> ids = commonRepository.findSearchIds(query);
        Assert.assertEquals(3, ids.size());
        Assert.assertEquals("id-3", ids.get(2));

        Assert.assertTrue(matrixCursor.isClosed());
    }


    @Test
    public void countSearchIdsShouldReturnListOfIds() {
        String query = "SELECT count(object_id) FROM ec_client_search";
        int count = 23;

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"count(object_id)"});
        matrixCursor.addRow(new Object[]{count});

        Mockito.doReturn(matrixCursor).when(sqliteDatabase).rawQuery(Mockito.eq(query), Mockito.nullable(String[].class));
        int resultCount = commonRepository.countSearchIds(query);
        Assert.assertEquals(count, resultCount);

        Assert.assertTrue(matrixCursor.isClosed());
    }

}
