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
import org.smartregister.domain.Report;

/**
 * Created by kaderchowdhury on 19/11/17.
 */

public class ReportRepositoryTest extends BaseUnitTest {

    private ReportRepository reportRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Mock
    private Repository repository;

    private static final String REPORT_TABLE_NAME = "report";
    private static final String INDICATOR_COLUMN = "indicator";
    private static final String ANNUAL_TARGET_COLUMN = "annualTarget";
    private static final String MONTHLY_SUMMARIES_COLUMN = "monthlySummaries";
    private static final String[] REPORT_TABLE_COLUMNS = {INDICATOR_COLUMN, ANNUAL_TARGET_COLUMN,
            MONTHLY_SUMMARIES_COLUMN};

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        reportRepository = new ReportRepository();
        reportRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(reportRepository);
    }

    @Test
    public void onCreateCallsDatabaseExec() {
        reportRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(2)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertUpdateCallsDatabaseUpdate() {
        Report report = new Report("", "", "");
        reportRepository.update(report);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).replace(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertAllForReturnsReportList() {
        String[] columns = {"a", "b", "c"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"", "", ""});
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(cursor);
        Assert.assertNotNull(reportRepository.allFor("", "", ""));
    }

    @Test
    public void assertAllReturnsAllReports() {
        String[] columns = {"a", "b", "c"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"", "", ""});
        Mockito.when(sqLiteDatabase.query(REPORT_TABLE_NAME, REPORT_TABLE_COLUMNS, null, null, null, null, null)).thenReturn(cursor);
        Assert.assertNotNull(reportRepository.all());
    }

}
