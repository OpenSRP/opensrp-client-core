package org.smartregister.cursoradapter;

import androidx.recyclerview.widget.RecyclerView;

import net.sqlcipher.Cursor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.cursoradapter.RecyclerViewCursorAdapter.NotifyingDataSetObserver;
import org.smartregister.cursoradapter.mock.RecyclerViewCursorAdapterMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 10/27/20.
 */
public class RecyclerViewCursorAdapterTest extends BaseRobolectricUnitTest {

    @InjectMocks
    private RecyclerViewCursorAdapterMock recyclerViewCursorAdapter;

    @Mock
    private Cursor cursor;

    @Mock
    private RecyclerView.ViewHolder viewHolder;

    @Before
    public void setUp() {
        when(cursor.getColumnIndex("_id")).thenReturn(0);
    }

    @Test
    public void testGetCursorShouldReturnCursor() {
        assertEquals(cursor, recyclerViewCursorAdapter.getCursor());
    }

    @Test
    public void testGetItemCountShouldReturnCountFromCursor() {
        when(cursor.getCount()).thenReturn(2);
        assertEquals(3, recyclerViewCursorAdapter.getItemCount());
        verify(cursor).getCount();
    }

    @Test
    public void testGetItemCountShouldReturnZero() {
        ReflectionHelpers.setField(recyclerViewCursorAdapter, "mDataValid", false);
        assertEquals(0, recyclerViewCursorAdapter.getItemCount());
        verify(cursor, never()).getCount();
    }

    @Test
    public void testGetItemIdShouldReadFromCursor() {
        when(cursor.moveToPosition(12)).thenReturn(true);
        when(cursor.getLong(0)).thenReturn(200L);
        assertEquals(200, recyclerViewCursorAdapter.getItemId(12));
        verify(cursor).getLong(0);
        verify(cursor).moveToPosition(12);
    }

    @Test
    public void testGetItemIdShouldReturnZeroIfCursorIsBlank() {
        when(cursor.moveToPosition(1)).thenReturn(false);
        assertEquals(0, recyclerViewCursorAdapter.getItemId(1));
        verify(cursor).moveToPosition(1);
        verify(cursor, never()).getLong(anyInt());
    }

    @Test
    public void testSetHasStableIdsShouldPassTrueToSuper() {
        recyclerViewCursorAdapter.setHasStableIds(false);
        assertTrue( ReflectionHelpers.getField(recyclerViewCursorAdapter, "mHasStableIds"));

        recyclerViewCursorAdapter.setHasStableIds(true);
        assertTrue( ReflectionHelpers.getField(recyclerViewCursorAdapter, "mHasStableIds"));
    }

    @Test
    public void testGetItemViewTypeShouldReturnFooter() {
        when(cursor.getCount()).thenReturn(3);
        assertEquals(RecyclerViewCursorAdapter.Type.FOOTER.ordinal(), recyclerViewCursorAdapter.getItemViewType(3));
    }

    @Test
    public void testGetItemViewTypeShouldReturnItem() {
        when(cursor.getCount()).thenReturn(5);
        assertEquals(RecyclerViewCursorAdapter.Type.ITEM.ordinal(), recyclerViewCursorAdapter.getItemViewType(3));
    }

    @Test(expected = IllegalStateException.class)
    public void testOnBindViewHolderShouldThrowExceptionIfCursorNotValid() {
        ReflectionHelpers.setField(recyclerViewCursorAdapter, "mDataValid", false);
        recyclerViewCursorAdapter.onBindViewHolder(viewHolder, 2);
    }

    @Test(expected = IllegalStateException.class)
    public void testOnBindViewHolderShouldThrowExceptionIfCursorDoesNotHaveItem() {
        when(cursor.moveToPosition(2)).thenReturn(false);
        when(cursor.getCount()).thenReturn(3);
        recyclerViewCursorAdapter.onBindViewHolder(viewHolder, 2);
    }

    @Test
    public void testOnBindViewHolderShouldInvokeBindViewHolder() {
        when(cursor.moveToPosition(2)).thenReturn(true);
        recyclerViewCursorAdapter = spy(recyclerViewCursorAdapter);
        recyclerViewCursorAdapter.onBindViewHolder(viewHolder, 2);
        verify(recyclerViewCursorAdapter).onBindViewHolder(viewHolder, cursor);
    }

    @Test
    public void testChangeCursorShouldCloseCursor() {
        Cursor newCursor = mock(Cursor.class);
        recyclerViewCursorAdapter.changeCursor(newCursor);
        verify(cursor).close();
    }

    @Test
    public void testSwapCursorShouldReturnNullIfNewCursorIsnull() {
        assertNull(recyclerViewCursorAdapter.swapCursor(null));
    }

    @Test
    public void testSwapCursorShouldProcessCorrectly() {
        recyclerViewCursorAdapter = spy(recyclerViewCursorAdapter);
        NotifyingDataSetObserver mDataSetObserver = ReflectionHelpers.getField(recyclerViewCursorAdapter, "mDataSetObserver");
        Cursor newCursor = mock(Cursor.class);
        android.database.Cursor old = recyclerViewCursorAdapter.swapCursor(newCursor);
        assertNotNull(old);
        verify(cursor).unregisterDataSetObserver(mDataSetObserver);
        verify(newCursor).registerDataSetObserver(mDataSetObserver);
        verify(recyclerViewCursorAdapter).notifyDataSetChanged();
    }

    @Test
    public void testNotifyingDataSetObserverOnchange() {
        NotifyingDataSetObserver mDataSetObserver = ReflectionHelpers.getField(recyclerViewCursorAdapter, "mDataSetObserver");
        mDataSetObserver.onChanged();
        assertTrue(ReflectionHelpers.getField(recyclerViewCursorAdapter, "mDataValid"));
    }

    @Test
    public void testNotifyingDataSetObserverOnInvalidated() {
        NotifyingDataSetObserver mDataSetObserver = ReflectionHelpers.getField(recyclerViewCursorAdapter, "mDataSetObserver");
        mDataSetObserver.onInvalidated();
        assertFalse(ReflectionHelpers.getField(recyclerViewCursorAdapter, "mDataValid"));
    }
}
