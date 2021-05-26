package org.smartregister.repository;

import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Mother;


/**
 * Created by kaderchowdhury on 15/11/17.
 */

public class MotherRepositoryTest extends BaseUnitTest {

    private static final String ID_COLUMN = "id";
    private static final String EC_CASEID_COLUMN = "ecCaseId";
    private static final String THAYI_CARD_NUMBER_COLUMN = "thayiCardNumber";
    private static final String REF_DATE_COLUMN = "referenceDate";
    private static final String DETAILS_COLUMN = "details";
    private static final String TYPE_COLUMN = "type";
    private static final String IS_CLOSED_COLUMN = "isClosed";

    private MotherRepository motherRepository;
    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Mock
    private Repository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        motherRepository = new MotherRepository();
    }

    @Test
    public void assertConstrustorInitializationNotNull() {
        Assert.assertNotNull(new MotherRepository());
    }

    @Test
    public void assertOnCreateCallDatabaseExecSql() {
        motherRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(3)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertAddMotherCallsDatabaseSqlInsert() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        motherRepository.add(getMockMother());
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertSwitchToPNCCallsDatabaseUpdate() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        motherRepository.switchToPNC("0");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertUpdateMotherCallsDatabaseUpdate() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        motherRepository.update(getMockMother());
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertAllANCsCallsDatabaseQueryAndReturnsListOfANCs() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getMotherCursor());
        org.junit.Assert.assertNotNull(motherRepository.allANCs());
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertAllPNCsCallsDatabaseQueryAndReturnsListOfANCs() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getMotherCursor());
        org.junit.Assert.assertNotNull(motherRepository.allPNCs());
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertAllECCallsDatabaseQueryAndReturnsListOfANCs() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getMotherCursor());
        org.junit.Assert.assertNotNull(motherRepository.findAllCasesForEC("0"));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertFindMotherByIdReturnsMother() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getMotherCursor());
        Assert.assertNotNull(motherRepository.findById("0"));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertFindMotherWithOpenStatusByECIdReturnsMother() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getMotherCursor());
        Assert.assertNotNull(motherRepository.findMotherWithOpenStatusByECId("0"));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertFindCaseByIdReturnsMother() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getMotherCursor());
        Assert.assertNotNull(motherRepository.findOpenCaseByCaseID("0"));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertFindCaseByIdsReturnsMotherList() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(getMotherCursor());
        Assert.assertNotNull(motherRepository.findByCaseIds("0"));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).rawQuery(Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test(expected = Exception.class)
    public void assertAllMothersOfATypeWithECReturnsNull() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Assert.assertNull(motherRepository.allMothersOfATypeWithEC("type"));
    }

    @Test
    public void assertAllMothersOfATypeWithECReturnsAllMother() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(getJoinCursor());
        Assert.assertNotNull(motherRepository.allMothersOfATypeWithEC("type"));
    }

    @Test
    public void assertCloseAllCasesForECCallsDatabaseUpdate() {
        motherRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getMotherCursor());
        motherRepository.closeAllCasesForEC("0");
    }

    public Mother getMockMother() {
        Mother mother = new Mother("caseId", "ecCaseId", "thayiCardNumber", "referenceDate");
        return mother;
    }


    public MatrixCursor getJoinCursor() {
        String[] columns = {"mother.id", "mother.ecCaseId", "mother.thayiCardNumber", "mother.type", "mother.referenceDate", "mother.details", "mother.isClosed", "eligible_couple.id", "eligible_couple.wifeName", "eligible_couple.husbandName", "eligible_couple.ecNumber", "eligible_couple.village", "eligible_couple.subCenter", "eligible_couple.isOutOfArea", "eligible_couple.details", "eligible_couple.isClosed", "eligible_couple.photoPath", "type", "photoPath", "isOutOfArea"};
        String[] row = {"mother.id", "mother.ecCaseId", "mother.thayiCardNumber", "mother.type", "mother.referenceDate", "{\"details\":\"1\"}", "mother.isClosed", "eligible_couple.id", "eligible_couple.wifeName", "eligible_couple.husbandName", "eligible_couple.ecNumber", "eligible_couple.village", "eligible_couple.subCenter", "eligible_couple.isOutOfArea", "{\"details\":\"1\"}", "eligible_couple.isClosed", "eligible_couple.photoPath", "type", "path", "0"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(row);
        return cursor;
    }

    public MatrixCursor getMotherCursor() {

        String[] columns = {ID_COLUMN, EC_CASEID_COLUMN, THAYI_CARD_NUMBER_COLUMN, TYPE_COLUMN, REF_DATE_COLUMN, DETAILS_COLUMN, IS_CLOSED_COLUMN};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"0", "1", "2", "3", "2017-10-10", "{\"details\":\"1\"}", "0"});
        return cursor;
    }

}
