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
import org.smartregister.domain.EligibleCouple;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kaderchowdhury on 15/11/17.
 */

public class EligibleCoupleRepositoryTest extends BaseUnitTest {

    public static final String ID_COLUMN = "id";
    public static final String EC_NUMBER_COLUMN = "ecNumber";
    public static final String WIFE_NAME_COLUMN = "wifeName";
    public static final String HUSBAND_NAME_COLUMN = "husbandName";
    public static final String VILLAGE_NAME_COLUMN = "village";
    public static final String SUBCENTER_NAME_COLUMN = "subCenter";
    public static final String IS_OUT_OF_AREA_COLUMN = "isOutOfArea";
    public static final String DETAILS_COLUMN = "details";
    public static final String PHOTO_PATH_COLUMN = "photoPath";
    private static final String IS_CLOSED_COLUMN = "isClosed";

    private EligibleCoupleRepository eligibleCoupleRepository;
    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Mock
    private Repository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eligibleCoupleRepository = new EligibleCoupleRepository();
    }

    @Test
    public void assertConstrustorInitializationNotNull() {
        Assert.assertNotNull(new EligibleCoupleRepository());
    }

    @Test
    public void assertOnCreateCallDatabaseExecSql() {
        eligibleCoupleRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertUpdatePhotoPathCallsUpdate() {
        eligibleCoupleRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        eligibleCoupleRepository.updatePhotoPath("caseId", "photoPath");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertCloseCallsUpdate() {
        eligibleCoupleRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        eligibleCoupleRepository.close("0");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertAddECCallsDatabaseSqlInsert() {
        eligibleCoupleRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        eligibleCoupleRepository.add(getMockEligibleCouple());
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertUpdateDetails() {
        eligibleCoupleRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getECCursor());
        HashMap<String, String> details = new HashMap<String, String>();
        details.put("details", "1");
        eligibleCoupleRepository.updateDetails("0", details);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertAllEligibleCouples() {
        eligibleCoupleRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getECCursor());
        HashMap<String, String> details = new HashMap<String, String>();
        details.put("details", "1");
        eligibleCoupleRepository.allEligibleCouples();
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertAllVillagesReturnsVillageList() {
        eligibleCoupleRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyBoolean(), Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getECCursor());
        HashMap<String, String> details = new HashMap<String, String>();
        details.put("details", "1");
        Assert.assertNotNull(eligibleCoupleRepository.villages());
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyBoolean(), Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertFindCaseByIdsReturnsECList() {
        eligibleCoupleRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(getECCursor());
        Assert.assertNotNull(eligibleCoupleRepository.findByCaseIDs("0"));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).rawQuery(Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertMergeDetails() {
        eligibleCoupleRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getECCursor());
        HashMap<String, String> details = new HashMap<String, String>();
        details.put("details", "1");
        eligibleCoupleRepository.mergeDetails("0", details);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertGetfpCountReturnsLongCount() {
        eligibleCoupleRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        HashMap<String, String> details = new HashMap<String, String>();
        details.put("details", "1");
        details.put("details", "2");
        ArrayList<HashMap<String, String>> detailsList = new ArrayList<HashMap<String, String>>();
        detailsList.add(details);
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(getDetailsCursor());
        Assert.assertEquals(eligibleCoupleRepository.fpCount(), 0l);
    }

    public MatrixCursor getDetailsCursor() {
        String[] columns = {"details"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"{\"details\":\"1\"}"});
        cursor.addRow(new Object[]{"{\"details\":\"2\"}"});
        return cursor;
    }

    public MatrixCursor getECCursor() {

        String[] columns = {ID_COLUMN, WIFE_NAME_COLUMN, HUSBAND_NAME_COLUMN, EC_NUMBER_COLUMN, VILLAGE_NAME_COLUMN, SUBCENTER_NAME_COLUMN, IS_OUT_OF_AREA_COLUMN, DETAILS_COLUMN, IS_CLOSED_COLUMN, PHOTO_PATH_COLUMN};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"0", "WIFE", "HUSBAND", "3", "VILLAGE", "SUBCENTER", "0", "{\"details\":\"1\"}", "0", "photopath"});
        return cursor;
    }

    public EligibleCouple getMockEligibleCouple() {
        EligibleCouple ec = new EligibleCouple("caseId", "wifeName", "husbandName", "ecNumber", "village", "subcenter", new HashMap<String, String>());
        return ec;
    }
}
