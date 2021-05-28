package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.domain.AlertStatus.normal;
import static org.smartregister.domain.AlertStatus.upcoming;

/**
 * Created by onaio on 29/08/2017.
 */

public class AlertRepositoryTest extends BaseUnitTest {

    public static final String ADDITIONALCOLUMN = "ADDITIONALCOLUMN";
    public static final String CUSTOMRELATIONALID = "CUSTOMRELATIONALID";
    public static final String[] alertColumns = {"caseID", "scheduleName", "visitCode", "status", "startDate", "expiryDate", "completionDate", "offline"};
    public static final Object[] alertRow = {"caseID", "scheduleName", "visitCode", "urgent", "startDate", "expiryDate", "completionDate", 1};

    @InjectMocks
    private AlertRepository alertRepository;

    @Mock
    private Repository repository;

    @Mock
    private CommonFtsObject commonFtsObject;

    @Mock
    private AlertService alertService;


    @Mock
    private Context context;

    @Mock
    private SQLiteDatabase sqliteDatabase;

    @Before
    public void setUp() {

        initMocks(this);

    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCall() throws Exception {
        Assert.assertNotNull(new AlertRepository());
    }

    @Test
    public void assertOnCreateCallsDatabaseExecSql() {
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.onCreate(sqliteDatabase);
        Mockito.verify(sqliteDatabase, Mockito.times(5)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertAllAlertsReturnNotNUll() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.allAlerts());
    }

    @Test
    public void createAlertsCallsInsert1TimeForNewALerts() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
//        matrixCursor.addRow(alertRow);

        Cursor cursor = Mockito.mock(MatrixCursor.class);
        Mockito.doReturn(0).when(cursor).getInt(0);
        Mockito.doReturn(cursor).when(sqliteDatabase).rawQuery(ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class));
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Alert alert = new Alert("caseID", "scheduleName", "visitCode", AlertStatus.urgent, "startDate", "expiryDate", true);
        alertRepository.createAlert(alert);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).insert(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.any(ContentValues.class));

    }

    @Test
    public void createAlertsCallsUpdate1TimeForOldALerts() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        Cursor cursor = Mockito.mock(MatrixCursor.class);
        Mockito.doReturn(1).when(cursor).getInt(0);
        Mockito.doReturn(cursor).when(sqliteDatabase).rawQuery(ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class));
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Alert alert = new Alert("caseID", "scheduleName", "visitCode", AlertStatus.urgent, "startDate", "expiryDate", true);
        alertRepository.createAlert(alert);
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void changeAlertStatusToInProcess1CallsUpdate1Time() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.changeAlertStatusToInProcess("caseID", "scheduleName");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void changeAlertStatusToCompleteCallsUpdate1Time() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.changeAlertStatusToComplete("caseID", "scheduleName");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void markAlertAsClosedCallsUpdate1Time() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.markAlertAsClosed("caseID", "scheduleName", "completionDate");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).update(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(ContentValues.class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void filterActiveAlertsReturnsNotNull() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        LocalDate today = LocalDate.now();
        today = today.plusDays(5);
        Object[] alertRowForActiveALerts = {"caseID", "scheduleName", "visitCode", "urgent", "", today.toString(), "", 1};

        matrixCursor.addRow(alertRowForActiveALerts);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.allActiveAlertsForCase("caseID"));

    }

    @Test
    public void deleteAllAlertsForEntityCallsDelete1Times() throws Exception {
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.deleteAllAlertsForEntity("caseID");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void deleteAllOfflineAlertsForEntityCallsDelete1Times() throws Exception {
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.deleteOfflineAlertsForEntity("caseID");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void deleteAllOfflineAlertsForEntityAndNameCallsDelete1Times() throws Exception {
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.deleteOfflineAlertsForEntity("caseID", "name1", "name2");
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class));

    }

    @Test
    public void deleteAllAlertsCallsDelete1Times() throws Exception {
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        alertRepository.deleteAllAlerts();
        Mockito.verify(sqliteDatabase, Mockito.times(1)).delete(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String[].class));

    }

    @Test
    public void findByEntityIDReturnNotNUll() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.findByEntityId("caseID"));
    }

    @Test
    public void findByEntityIdAndAlertNamesReturnNotNUll() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.findByEntityIdAndAlertNames("caseID", "names1", "names2"));
    }

    @Test
    public void findOfflineByEntityIdAndAlertNamesReturnNotNUll() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.rawQuery(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.findOfflineByEntityIdAndName("caseID", "names1", "names2"));
    }

    @Test
    public void findByEntityIdAndScheduleNameReturnNotNUll() throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);
        Mockito.when(sqliteDatabase.query(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class), org.mockito.ArgumentMatchers.isNull(String.class))).thenReturn(matrixCursor);
        Assert.assertNotNull(alertRepository.findByEntityIdAndScheduleName("caseID", "Schedulenames"));
    }

    @Test
    public void testCreateAlertsPersistsNewAlertsInTransactions() throws Exception {
        List<Alert> alerts = new ArrayList<>();
        alerts.add(new Alert("Case X", "bcg", "bcg1", normal, "2021-01-01", "2022-01-11", true).withCompletionDate("2021-02-02"));
        alerts.add(new Alert("Case Y", "opv", "opv1", normal, "2021-02-01", "2022-01-11", false));
        alerts.add(new Alert("Case Z", "mr", "mr1", normal, "2021-03-01", "2022-01-11", true));

        Cursor cursor = Mockito.mock(MatrixCursor.class);
        Mockito.doReturn(0).when(cursor).getInt(0);
        Mockito.doReturn(cursor).when(sqliteDatabase).rawQuery(ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class));

        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> stringArgumentCaptor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ContentValues> contentValuesArgumentCaptor = ArgumentCaptor.forClass(ContentValues.class);

        alertRepository.createAlerts(alerts);

        Mockito.verify(sqliteDatabase, Mockito.times(3)).insert(stringArgumentCaptor.capture(), stringArgumentCaptor2.capture(), contentValuesArgumentCaptor.capture());

        Assert.assertEquals(AlertRepository.ALERTS_TABLE_NAME, stringArgumentCaptor.getValue());
        Assert.assertNull(stringArgumentCaptor2.getValue());

        List<ContentValues> capturedContentValues = contentValuesArgumentCaptor.getAllValues();
        Assert.assertNotNull(capturedContentValues);
        Assert.assertEquals(3, capturedContentValues.size());

        ContentValues contentValues = capturedContentValues.get(0);//First item

        Assert.assertNotNull(contentValues);
        Assert.assertEquals("Case X", contentValues.get(AlertRepository.ALERTS_CASEID_COLUMN));
        Assert.assertEquals("bcg", contentValues.get(AlertRepository.ALERTS_SCHEDULE_NAME_COLUMN));
        Assert.assertEquals("bcg1", contentValues.get(AlertRepository.ALERTS_VISIT_CODE_COLUMN));
        Assert.assertEquals("normal", contentValues.get(AlertRepository.ALERTS_STATUS_COLUMN));
        Assert.assertEquals("2021-01-01", contentValues.get(AlertRepository.ALERTS_STARTDATE_COLUMN));
        Assert.assertEquals("2022-01-11", contentValues.get(AlertRepository.ALERTS_EXPIRYDATE_COLUMN));
        Assert.assertEquals("2021-02-02", contentValues.get(AlertRepository.ALERTS_COMPLETIONDATE_COLUMN));
        Assert.assertEquals(Integer.valueOf("1"), contentValues.get(AlertRepository.ALERTS_OFFLINE_COLUMN));

        Mockito.verify(sqliteDatabase).beginTransaction();
        Mockito.verify(sqliteDatabase).setTransactionSuccessful();
        Mockito.verify(sqliteDatabase).endTransaction();
    }

    @Test
    public void testCreateAlertsPersistsUpdatedAlertsInTransactions() throws Exception {
        List<Alert> alerts = new ArrayList<>();
        alerts.add(new Alert("Case W", "penta", "penta1", upcoming, "2021-04-04", "2022-02-21", false));
        alerts.add(new Alert("Case X", "bcg", "bcg1", normal, "2021-01-01", "2022-01-11", true).withCompletionDate("2021-02-02"));

        Cursor cursor = Mockito.mock(MatrixCursor.class);
        Mockito.doReturn(1).when(cursor).getInt(0);
        Mockito.doReturn(cursor).when(sqliteDatabase).rawQuery(ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class));

        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> stringArgumentCaptor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String[]> arrayStringArgumentCaptor = ArgumentCaptor.forClass(String[].class);
        ArgumentCaptor<ContentValues> contentValuesArgumentCaptor = ArgumentCaptor.forClass(ContentValues.class);

        alertRepository.createAlerts(alerts);

        Mockito.verify(sqliteDatabase, Mockito.times(alerts.size())).update(stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture(), stringArgumentCaptor2.capture(), arrayStringArgumentCaptor.capture());

        Assert.assertEquals(AlertRepository.ALERTS_TABLE_NAME, stringArgumentCaptor.getValue());
        Assert.assertEquals("caseID = ?  COLLATE NOCASE AND scheduleName = ?", stringArgumentCaptor2.getValue());

        List<String[]> arrayArguments = arrayStringArgumentCaptor.getAllValues();
        Assert.assertNotNull(arrayArguments);
        Assert.assertEquals(alerts.size(), arrayArguments.size());
        Assert.assertEquals("Case W", arrayArguments.get(0)[0]);
        Assert.assertEquals("penta", arrayArguments.get(0)[1]);
        Assert.assertEquals("Case X", arrayArguments.get(1)[0]);
        Assert.assertEquals("bcg", arrayArguments.get(1)[1]);

        List<ContentValues> capturedContentValues = contentValuesArgumentCaptor.getAllValues();
        Assert.assertNotNull(capturedContentValues);
        Assert.assertEquals(alerts.size(), capturedContentValues.size());

        ContentValues contentValues = capturedContentValues.get(0);//First item

        Assert.assertNotNull(contentValues);
        Assert.assertEquals("Case W", contentValues.get(AlertRepository.ALERTS_CASEID_COLUMN));
        Assert.assertEquals("penta", contentValues.get(AlertRepository.ALERTS_SCHEDULE_NAME_COLUMN));
        Assert.assertEquals("penta1", contentValues.get(AlertRepository.ALERTS_VISIT_CODE_COLUMN));
        Assert.assertEquals("upcoming", contentValues.get(AlertRepository.ALERTS_STATUS_COLUMN));
        Assert.assertEquals("2021-04-04", contentValues.get(AlertRepository.ALERTS_STARTDATE_COLUMN));
        Assert.assertEquals("2022-02-21", contentValues.get(AlertRepository.ALERTS_EXPIRYDATE_COLUMN));
        Assert.assertEquals(Integer.valueOf("0"), contentValues.get(AlertRepository.ALERTS_OFFLINE_COLUMN));

        Mockito.verify(sqliteDatabase).beginTransaction();
        Mockito.verify(sqliteDatabase).setTransactionSuccessful();
        Mockito.verify(sqliteDatabase).endTransaction();

    }

    @Test
    public void testGetAlertsCountEvaluatesCorrectCountQuery() throws Exception {

        Cursor cursor = Mockito.mock(MatrixCursor.class);
        Mockito.doReturn(1).when(cursor).getInt(0);
        Mockito.doReturn(cursor).when(sqliteDatabase).rawQuery(ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class));

        MatrixCursor matrixCursor = new MatrixCursor(alertColumns);
        matrixCursor.addRow(alertRow);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        alertRepository.updateMasterRepository(repository);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String[]> arrayStringArgumentCaptor = ArgumentCaptor.forClass(String[].class);

        alertRepository.getAlertsCount(sqliteDatabase, "Case U", "pcv1");

        Mockito.verify(sqliteDatabase).rawQuery(stringArgumentCaptor.capture(), arrayStringArgumentCaptor.capture());

        Assert.assertEquals("SELECT COUNT(caseID) FROM alerts WHERE caseID = ?  COLLATE NOCASE AND scheduleName = ?", stringArgumentCaptor.getValue());
        String[] arrayArguments = arrayStringArgumentCaptor.getValue();
        Assert.assertNotNull(arrayArguments);
        Assert.assertEquals(2, arrayArguments.length);
        Assert.assertEquals("Case U", arrayArguments[0]);
        Assert.assertEquals("pcv1", arrayArguments[1]);

    }

}