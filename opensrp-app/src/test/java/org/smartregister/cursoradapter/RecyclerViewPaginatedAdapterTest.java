package org.smartregister.cursoradapter;

import android.content.Context;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.LinearLayout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by Richard Kareko on 6/16/20.
 */

public class RecyclerViewPaginatedAdapterTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private RecyclerViewProvider<RecyclerView.ViewHolder> listItemProvider;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private Cursor mCursor;

    @Mock
    private RecyclerView.ViewHolder mockViewHolder;

    @Captor
    private ArgumentCaptor<Cursor> cursorArgumentCaptor;

    @Captor
    private ArgumentCaptor<CommonPersonObjectClient> personObjectClientArgumentCaptor;

    @Captor
    private ArgumentCaptor<RecyclerView.ViewHolder> viewHolderArgumentCaptor;

    private RecyclerViewPaginatedAdapter adapter;

    private Context context = RuntimeEnvironment.application;

    @Before
    public void setUp() {
        adapter = new RecyclerViewPaginatedAdapter(mCursor, listItemProvider, commonRepository);
    }

    @Test
    public void testConstructor() {
        assertNotNull(adapter);
        assertEquals(mCursor, adapter.getCursor());
        assertEquals(listItemProvider, Whitebox.getInternalState(adapter, "listItemProvider"));
        assertEquals(commonRepository, Whitebox.getInternalState(adapter, "commonRepository"));
    }

    @Test
    public void testOnCreateViewHolder() {
        LinearLayout vg = new LinearLayout(context);
        when(listItemProvider.createViewHolder(any())).thenReturn(mockViewHolder);
        RecyclerView.ViewHolder actualViewHolder = adapter.onCreateViewHolder(vg,
                RecyclerViewCursorAdapter.Type.ITEM.ordinal());
        assertNotNull(actualViewHolder);
        verify(listItemProvider).createViewHolder(any());
    }

    @Test
    public void testOnCreateFooterHolder() {
        LinearLayout vg = new LinearLayout(context);
        when(listItemProvider.createFooterHolder(any())).thenReturn(mockViewHolder);
        RecyclerView.ViewHolder actualViewHolder = adapter.onCreateViewHolder(vg,
                RecyclerViewCursorAdapter.Type.FOOTER.ordinal());
        assertNotNull(actualViewHolder);
        verify(listItemProvider).createFooterHolder(any());
    }

    @Test
    public void testOnBindViewFootHolder() {
        adapter.setTotalcount(20);
        when(listItemProvider.isFooterViewHolder(mockViewHolder)).thenReturn(true);
        adapter.onBindViewHolder(mockViewHolder, mCursor);
        verify(listItemProvider).getFooterView(mockViewHolder, 1, 1, false, false);

    }

    @Test
    public void testOnBindViewHolder() {
        String name = "John";
        Map<String, String> details = new HashMap<>();
        details.put("FWHOHFNAME", name);
        Map<String, String> columnmaps = new HashMap<>();
        String idColumn = "baseEntityId";
        columnmaps.put("id", idColumn);
        String caseId = "case 1";
        String relationId = "identifier 123";
        String type = "bindtype";
        CommonPersonObject personInList = new CommonPersonObject(caseId, relationId, details,type);
        personInList.setColumnmaps(columnmaps);
        when(commonRepository.readAllcommonforCursorAdapter(mCursor)).thenReturn(personInList);

        adapter.onBindViewHolder(mockViewHolder, mCursor);
        verify(listItemProvider).getView(cursorArgumentCaptor.capture(), personObjectClientArgumentCaptor.capture(), viewHolderArgumentCaptor.capture());
        assertEquals(mockViewHolder, viewHolderArgumentCaptor.getValue());
        assertEquals(mCursor, cursorArgumentCaptor.getValue());
        assertEquals(name, personObjectClientArgumentCaptor.getValue().getName());
        assertEquals(caseId, personObjectClientArgumentCaptor.getValue().getCaseId());
        assertEquals(idColumn, personObjectClientArgumentCaptor.getValue().getColumnmaps().get("id"));

    }

    @Test
    public void testGetCurrentPageCountWithNoOffset() throws Exception {
        int currentPageCount = Whitebox.invokeMethod(adapter, "getCurrentPageCount");
        assertEquals(1, currentPageCount);
    }

    @Test
    public void testGetCurrentPageCountWithLimitGreaterThanOffset() throws Exception {
        adapter.setCurrentoffset(10);
        adapter.setCurrentlimit(20);
        int currentPageCount = Whitebox.invokeMethod(adapter, "getCurrentPageCount");
        assertEquals(1, currentPageCount);
    }

    @Test
    public void testGetCurrentPageCountWithOffsetEqualLimit() throws Exception {
        adapter.setCurrentoffset(10);
        adapter.setCurrentlimit(10);
        int currentPageCount = Whitebox.invokeMethod(adapter, "getCurrentPageCount");
        assertEquals(2, currentPageCount);
    }

    @Test
    public void testGetTotalPageCountWhenTotalCountEqualsCurrentLimit() throws Exception {
        adapter.setTotalcount(10);
        adapter.setCurrentlimit(10);
        int totalPageCount = Whitebox.invokeMethod(adapter, "getTotalPageCount");
        assertEquals(1, totalPageCount);
    }

    @Test
    public void testGetTotalPageCountWhenTotalCountNotEqualToCurrentLimit() throws Exception {
        adapter.setTotalcount(30);
        adapter.setCurrentlimit(10);
        int totalPageCount = Whitebox.invokeMethod(adapter, "getTotalPageCount");
        assertEquals(3, totalPageCount);
    }

}
