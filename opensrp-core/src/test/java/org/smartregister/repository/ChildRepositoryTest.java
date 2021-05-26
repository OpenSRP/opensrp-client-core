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
import org.smartregister.domain.Child;

import java.util.HashMap;

/**
 * Created by kaderchowdhury on 15/11/17.
 */
public class ChildRepositoryTest extends BaseUnitTest {

    private static final String ID_COLUMN = "id";
    private static final String MOTHER_ID_COLUMN = "motherCaseId";
    private static final String THAYI_CARD_COLUMN = "thayiCardNumber";
    private static final String DATE_OF_BIRTH_COLUMN = "dateOfBirth";
    private static final String GENDER_COLUMN = "gender";
    private static final String DETAILS_COLUMN = "details";
    private static final String IS_CLOSED_COLUMN = "isClosed";
    public static final String PHOTO_PATH_COLUMN = "photoPath";

    private ChildRepository childRepository;
    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Mock
    private Repository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childRepository = new ChildRepository();
    }

    @Test
    public void assertConstrustorInitializationNotNull() {
        Assert.assertNotNull(new ChildRepository());
    }

    @Test
    public void assertOnCreateCallDatabaseExecSql() {
        childRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertAddChildCallsDatabaseSqlInsert() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        childRepository.add(getMockChild());
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertUpdateChildCallsDatabaseSqlUpdate() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        childRepository.update(getMockChild());
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertAllChildReturnsListOfChilds() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getChildCursor());
        Assert.assertNotNull(childRepository.all());
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertFindChildReturnsListOfChilds() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getChildCursor());
        Assert.assertNotNull(childRepository.find("0"));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertFindChildByMotherReturnsListOfChilds() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getChildCursor());
        Assert.assertNotNull(childRepository.findByMotherCaseId("0"));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertFindChildSReturnsListOfChilds() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(getChildCursor());
        Assert.assertNotNull(childRepository.findChildrenByCaseIds("0"));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).rawQuery(Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertUpdateDetailsCallsDatabaseUpdate() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        childRepository.updateDetails("", new HashMap<String, String>());
    }

    @Test
    public void assertCloseCallsDatabaseUpdate() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        childRepository.close("0");
    }

    @Test(expected = Exception.class)
    public void assertAllChildrenWithMotherAndEC() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        //throws Exception for cursor being null
        Assert.assertNull(childRepository.allChildrenWithMotherAndEC());
    }

    @Test
    public void assertAllChildrenWithMotherAndECJoinTableCursor() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(getJoinCursor());
        //throws Exception for cursor being null
        Assert.assertNotNull(childRepository.allChildrenWithMotherAndEC());
    }

    @Test
    public void assertFindAllChildrenWithMotherAndECJoinTableCursor() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(getJoinCursor());
        //throws Exception for cursor being null
        Assert.assertNotNull(childRepository.findAllChildrenByECId("0"));
    }

    @Test
    public void assertUpdatePhotoPathCallsDatabaseUpdate() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        childRepository.updatePhotoPath("/home/real", "/home/kaderchowdhury");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertDeleteCallsDatabaseDelete() {
        childRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.delete(Mockito.anyString(), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        childRepository.delete("");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).delete(Mockito.anyString(), Mockito.anyString(), Mockito.any(String[].class));
    }

    public MatrixCursor getJoinCursor() {
        String[] columns = {"childid", "childmotherCaseId", "childthayiCardNumber", "childdateOfBirth", "childgender", "childdetails", "childisClosed", "childphotoPath", "motherid", "motherecCaseId", "motherthayiCardNumber", "mothertype", "motherreferenceDate", "motherdetails", "motherisClosed", "eligible_coupleid", "eligible_couplewifeName", "eligible_couplehusbandName", "eligible_coupleecNumber", "eligible_couplevillage", "eligible_couplesubCenter", "eligible_coupleisOutOfArea", "eligible_coupledetails", "eligible_coupleisClosed", "eligible_couplephotoPath"};
        String[] row = {"childid", "childmotherCaseId", "childthayiCardNumber", "childdateOfBirth", "childgender", "{\"details\":\"1\"}", "childisClosed", "childphotoPath", "motherid", "motherecCaseId", "motherthayiCardNumber", "mothertype", "motherreferenceDate", "{\"details\":\"1\"}", "motherisClosed", "eligible_coupleid", "eligible_couplewifeName", "eligible_couplehusbandName", "eligible_coupleecNumber", "eligible_couplevillage", "eligible_couplesubCenter", "eligible_coupleisOutOfArea", "{\"details\":\"1\"}", "eligible_coupleisClosed", "eligible_couplephotoPath"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(row);
        return cursor;
    }

    public MatrixCursor getChildCursor() {

        String[] columns = {ID_COLUMN, MOTHER_ID_COLUMN, THAYI_CARD_COLUMN, DATE_OF_BIRTH_COLUMN, GENDER_COLUMN, DETAILS_COLUMN, IS_CLOSED_COLUMN, PHOTO_PATH_COLUMN};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"0", "1", "2", "2017-10-10", "M", "{\"details\":\"1\"}", "0", "/home/real/"});
        return cursor;
    }

    public Child getMockChild() {
        Child child = new Child("caseId", "motherCaseId", "thayiCardNumber", "dateOfBirth", "gender", new HashMap<String, String>());
        return child;
    }
}
