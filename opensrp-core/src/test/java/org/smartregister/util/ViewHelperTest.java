package org.smartregister.util;

import android.app.Activity;
import android.content.res.Resources;
import android.util.LayoutDirection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;

/**
 * Created by ndegwamartin on 2020-03-10.
 */
public class ViewHelperTest extends BaseUnitTest {

    private float TEST_DIMENSION = 1985.0f;

    @Mock
    private Activity activity;

    @Mock
    private LayoutInflater layoutInflater;

    @Spy
    private ViewGroup paginationView;

    @Mock
    private ListView clientsView;

    @Mock
    private Button nextPageButton;

    @Mock
    private Button prevPageButton;

    @Mock
    private TextView infoTextView;

    @Mock
    private View.OnClickListener clickListener;

    @Mock
    private Resources resources;

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetPaginationViewInflatesCorrectLayout() {

        Mockito.doReturn(layoutInflater).when(activity).getLayoutInflater();
        Mockito.doReturn(paginationView).when(layoutInflater).inflate(R.layout.smart_register_pagination, null);

        ViewHelper.getPaginationView(activity);

        Mockito.verify(layoutInflater).inflate(R.layout.smart_register_pagination, null);

    }

    @Test
    public void testAddPaginationCoreCreatesPaginationHolderWithCorrectValues() {

        Mockito.doReturn(layoutInflater).when(activity).getLayoutInflater();
        Mockito.doReturn(paginationView).when(layoutInflater).inflate(R.layout.smart_register_pagination, null);
        Mockito.doReturn(activity).when(clientsView).getContext();

        Mockito.doReturn(nextPageButton).when(paginationView).findViewById(R.id.btn_next_page);
        Mockito.doReturn(prevPageButton).when(paginationView).findViewById(R.id.btn_previous_page);
        Mockito.doReturn(infoTextView).when(paginationView).findViewById(R.id.txt_page_info);

        Mockito.doReturn(LayoutDirection.LTR).when(paginationView).getLayoutDirection();

        Mockito.doReturn(resources).when(activity).getResources();
        Mockito.doReturn(1985.0f).when(resources).getDimension(ArgumentMatchers.anyInt());

        PaginationHolder paginationHolder = ViewHelper.addPaginationCore(clickListener, clientsView);

        Assert.assertNotNull(paginationHolder);
        Assert.assertEquals(nextPageButton, paginationHolder.getNextPageView());
        Assert.assertEquals(prevPageButton, paginationHolder.getPreviousPageView());
        Assert.assertEquals(infoTextView, paginationHolder.getPageInfoView());

        Mockito.verify(nextPageButton).setOnClickListener(clickListener);
        Mockito.verify(prevPageButton).setOnClickListener(clickListener);

    }

    @Test
    public void testAddPaginationCoreCreatesFooterViewCorrectly() {


        Mockito.doReturn(layoutInflater).when(activity).getLayoutInflater();
        Mockito.doReturn(paginationView).when(layoutInflater).inflate(R.layout.smart_register_pagination, null);
        Mockito.doReturn(activity).when(clientsView).getContext();

        Mockito.doReturn(nextPageButton).when(paginationView).findViewById(R.id.btn_next_page);
        Mockito.doReturn(prevPageButton).when(paginationView).findViewById(R.id.btn_previous_page);
        Mockito.doReturn(infoTextView).when(paginationView).findViewById(R.id.txt_page_info);

        Mockito.doReturn(LayoutDirection.LTR).when(paginationView).getLayoutDirection();

        Mockito.doReturn(resources).when(activity).getResources();
        Mockito.doReturn(TEST_DIMENSION).when(resources).getDimension(ArgumentMatchers.anyInt());

        PaginationHolder paginationHolder = ViewHelper.addPaginationCore(clickListener, clientsView);

        Assert.assertNotNull(paginationHolder);

        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) paginationView.getLayoutParams();

        Assert.assertNotNull(layoutParams);
        Assert.assertEquals(-1, layoutParams.width);
        Assert.assertEquals(1985.0f, layoutParams.height, 0);
        Mockito.verify(clientsView).addFooterView(paginationView);

    }

}
