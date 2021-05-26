package org.smartregister.repository;

import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.TimelineEvent;

/**
 * Created by kaderchowdhury on 19/11/17.
 */

public class TimelineEventRepositoryTest extends BaseUnitTest {

    private TimelineEventRepository timelineEventRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Mock
    private Repository repository;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        timelineEventRepository = new TimelineEventRepository();
        timelineEventRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void assertConstructorInitializationNotNull() {
        Assert.assertNotNull(timelineEventRepository);
    }

    @Test
    public void assertOnCreateCallsDatabaseExec() {
        Mockito.doNothing().when(sqLiteDatabase).execSQL(Mockito.anyString());
        timelineEventRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(2)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertAddCallsDatabaseInsert() {
        TimelineEvent timelineEvent = new TimelineEvent("", "", new LocalDate(), "", "", "");
        timelineEventRepository.add(timelineEvent);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertAllFor() {
        String[] columns = {"id", "name", "date", "a", "b", "c"};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"", "", "2017-10-10", "", "", ""});
        cursor.addRow(new Object[]{"", "", "2017-10-10", "", "", ""});
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(cursor);
        timelineEventRepository.allFor("0");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class));
    }

    @Test
    public void assertDeleteAllTimelineEventsForEntity() {
        timelineEventRepository.deleteAllTimelineEventsForEntity("");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).delete(Mockito.anyString(), Mockito.anyString(), Mockito.any(String[].class));
    }
}
