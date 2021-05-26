package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Note;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.repository.TaskNotesRepository.TASK_NOTES_TABLE;

/**
 * Created by samuelgithengi on 11/26/18.
 */

public class TaskNotesRepositoryTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private TaskNotesRepository taskNotesRepository;

    @Mock
    private static Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Captor
    private ArgumentCaptor<ContentValues> contentValuesArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Before
    public void setUp() {
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        taskNotesRepository = new TaskNotesRepository();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testAddOrUpdateShouldAdd() {
        Note note = new Note();
        note.setText("Should be completed by End of November");
        Long now = System.currentTimeMillis();
        note.setTime(new DateTime(now));
        note.setAuthorString("jdoe");
        taskNotesRepository.addOrUpdate(note, "task22132");

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());
        assertEquals(2, stringArgumentCaptor.getAllValues().size());

        Iterator<String> iterator = stringArgumentCaptor.getAllValues().iterator();
        assertEquals(TASK_NOTES_TABLE, iterator.next());
        assertNull(iterator.next());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(4, contentValues.size());

        assertEquals("task22132", contentValues.getAsString("task_id"));
        assertEquals("Should be completed by End of November", contentValues.getAsString("text"));
        assertEquals("jdoe", contentValues.getAsString("author"));
        assertEquals(now, contentValues.getAsLong("time"));


    }


    @Test
    public void tesGetTasksAllTasks() {
        when(sqLiteDatabase.rawQuery("SELECT * FROM task_note WHERE task_id =?",
                new String[]{"task22132"})).thenReturn(getCursor());
        List<Note> notes = taskNotesRepository.getNotesByTask("task22132");
        verify(sqLiteDatabase).rawQuery("SELECT * FROM task_note WHERE task_id =?", new String[]{"task22132"});
        assertEquals(1, notes.size());
        assertEquals("Should be completed by End of November", notes.get(0).getText());
        assertEquals("jdoe", notes.get(0).getAuthorString());
        assertEquals(1543232476345l, notes.get(0).getTime().getMillis());


    }


    public MatrixCursor getCursor() {
        MatrixCursor cursor = new MatrixCursor(TaskNotesRepository.COLUMNS);
        cursor.addRow(new Object[]{"task22132", "jdoe", "1543232476345", "Should be completed by End of November"});
        return cursor;
    }


}
