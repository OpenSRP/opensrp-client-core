package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Hia2ReportRepositoryTest extends BaseUnitTest {
    private String baseEntityId = "baseEntityId";
    private String syncStatus = "syncStatus";

    @InjectMocks
    private Hia2ReportRepository hia2ReportRepository;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqliteDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(DrishtiApplication.getInstance(),"repository",repository);
        when(repository.getReadableDatabase()).thenReturn(sqliteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqliteDatabase);
        hia2ReportRepository = new Hia2ReportRepository();
    }

    @Test
    public void assertGetUnSyncedReportsReturnsList() {
        when(sqliteDatabase.rawQuery(anyString(), any(String[].class))).thenReturn(getCursorSyncStatus());
        assertNotNull(hia2ReportRepository.getUnSyncedReports(1));
    }

    @Test
    public void assertGetUnValidatedReportFormSubmissionIdsReturnsList() {
        when(sqliteDatabase.rawQuery(anyString(), any(String[].class))).thenReturn(getCursorSyncStatus());
        assertNotNull(hia2ReportRepository.getUnValidatedReportFormSubmissionIds(1));
    }

    @Test
    public void assertAddReportCallsDatabaseInsertAndUpdate() throws Exception {
        String jsonReport = "{\"reportType\":\"reportType\", \"formSubmissionId\":\"formSubmissionId\"}";
        when(sqliteDatabase.rawQuery(anyString(), any(String[].class))).thenReturn(getCursorSyncStatus());
        hia2ReportRepository.addReport(new JSONObject(jsonReport));
        verify(sqliteDatabase, Mockito.times(1)).update(anyString(), any(ContentValues.class), anyString(), any(String[].class));
        when(sqliteDatabase.rawQuery(anyString(), any(String[].class))).thenReturn(null);
        hia2ReportRepository.addReport(new JSONObject(jsonReport));
        verify(sqliteDatabase, Mockito.times(1)).insert(anyString(), isNull(String.class), any(ContentValues.class));
    }

    @Test
    public void assertmarkReportsAsSyncedCallsDatabaseUpdate() throws Exception {
        String jsonReport = "{\"reportType\":\"reportType\", \"formSubmissionId\":\"formSubmissionId\"}";
        List<JSONObject> reports = new ArrayList<>();
        reports.add(new JSONObject(jsonReport));
        hia2ReportRepository.markReportsAsSynced(reports);
        verify(sqliteDatabase, Mockito.times(1)).update(anyString(), any(ContentValues.class), anyString(), any(String[].class));
    }

    public MatrixCursor getCursorSyncStatus() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{baseEntityId, syncStatus});
        matrixCursor.addRow(new String[]{"{\"json\":\"data\"}", syncStatus});
        return matrixCursor;
    }

}
