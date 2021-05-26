package org.smartregister.view.fragment;

import android.graphics.drawable.Drawable;
import androidx.fragment.app.FragmentActivity;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;
import org.smartregister.adapter.SmartRegisterPaginatedAdapter;
import org.smartregister.customshadows.FontTextViewShadow;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.dialog.ECSearchOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.dialog.VillageFilter;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*", "javax.management.*", "com.sun.org.apache.xerces.*", "javax.xml.*",
        "org.xml.*", "org.w3c.dom.*", "com.sun.org.apache.xalan.*", "javax.activation.*"})
@Config(shadows = {FontTextViewShadow.class})
public class SecuredNativeSmartRegisterFragmentTest extends BaseUnitTest {

    @Mock
    private View searchCancelButton;

    @Mock
    private EditText searchView;

    @Mock
    private SmartRegisterPaginatedAdapter clientsAdapter;

    @Mock
    private TextView serviceModeView;

    private SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragment;

    @Mock
    private SecuredNativeSmartRegisterActivity.NavBarOptionsProvider navBarOptionsProvider;

    @Mock
    private SecuredNativeSmartRegisterActivity.DefaultOptionsProvider defaultOptionsProvider;

    @Mock
    private SecuredNativeSmartRegisterFragment.SearchCancelHandler searchCancelHandler;

    @Mock
    private ServiceModeOption serviceModeOption;

    @Mock
    private SortOption sortOption;

    @Mock
    private FilterOption filterOption;

    @Mock
    private View view;

    @Mock
    private LinearLayout linearLayout;

    @Mock
    private Button buttonBackHome;

    @Mock
    private SecuredNativeSmartRegisterActivity.ClientsHeaderProvider headerProvider;

    @Mock
    private FragmentActivity activity;

    @Mock
    private CustomFontTextView customFontTextView;

    @Mock
    private TextView appliedVillageFilterView;

    @Mock
    private TextView appliedSortView;

    @Mock
    private SecuredNativeSmartRegisterFragment.NavBarActionsHandler navBarActionsHandler;

    @Mock
    private SecuredNativeSmartRegisterFragment.PaginationViewHandler paginationViewHandler;

    @Spy
    private ListView clientsView;

    @Captor
    private ArgumentCaptor<ECSearchOption> ecSearchOptionArgumentCaptor;

    @Captor
    private ArgumentCaptor<VillageFilter> villageFilterArgumentCaptor;

    @Captor
    private ArgumentCaptor<ServiceModeOption> serviceModeOptionArgumentCaptor;

    @Captor
    private ArgumentCaptor<SortOption> sortOptionArgumentCaptor;

    private Drawable drawable = null;

    public static final String TEST_SEARCH_HINT = "Test Search Hint";

    public static final String MY_TEST_SEARCH_TEXT = "My Testing Search Text";

    private static final String GENERIC_TEXT_PHRASE = "Move the Earth";


    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);

        securedNativeSmartRegisterFragment = Mockito.mock(SecuredNativeSmartRegisterFragment.class, Mockito.CALLS_REAL_METHODS);

        ReflectionHelpers.setField(securedNativeSmartRegisterFragment, "searchCancelHandler", searchCancelHandler);
        ReflectionHelpers.setField(securedNativeSmartRegisterFragment, "navBarActionsHandler", navBarActionsHandler);
        ReflectionHelpers.setField(securedNativeSmartRegisterFragment, "paginationViewHandler", paginationViewHandler);

        Mockito.doReturn(navBarOptionsProvider).when(securedNativeSmartRegisterFragment).getNavBarOptionsProvider();
        Mockito.doReturn(defaultOptionsProvider).when(securedNativeSmartRegisterFragment).getDefaultOptionsProvider();

        Mockito.doReturn(serviceModeOption).when(defaultOptionsProvider).serviceMode();
        Mockito.doReturn(filterOption).when(defaultOptionsProvider).villageFilter();
        Mockito.doReturn(sortOption).when(defaultOptionsProvider).sortOption();

        Mockito.doReturn(GENERIC_TEXT_PHRASE).when(defaultOptionsProvider).nameInShortFormForTitle();

        Mockito.doReturn(headerProvider).when(serviceModeOption).getHeaderProvider();
        Mockito.doReturn(3).when(headerProvider).count();
        Mockito.doReturn(new int[]{3, 6, 7}).when(headerProvider).weights();
        Mockito.doReturn(new int[]{121982, 193726, 191917}).when(headerProvider).headerTextResourceIds();

        Mockito.doReturn(TEST_SEARCH_HINT).when(navBarOptionsProvider).searchHint();

        Mockito.doReturn(activity).when(clientsView).getContext();
        ReflectionHelpers.setField(securedNativeSmartRegisterFragment, "clientsView", clientsView);

        Mockito.doReturn(RuntimeEnvironment.application.getResources()).when(activity).getResources();
        Mockito.doReturn(RuntimeEnvironment.application.getResources()).when(securedNativeSmartRegisterFragment).getResources();

        Mockito.doNothing().when(clientsView).addFooterView(ArgumentMatchers.any(View.class));
    }

    @Test
    public void testActivityShouldNotBeNull() {

        Assert.assertNotNull(securedNativeSmartRegisterFragment);
    }

    @After
    public void tearDown() {
        securedNativeSmartRegisterFragment = null;
    }

    @Test
    public void testSetupSearchView() {

        View parent = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.smart_register_activity, null, false);

        View parentSpy = Mockito.spy(parent);

        Mockito.doReturn(searchView).when(parentSpy).findViewById(R.id.edt_search);
        Mockito.doReturn(searchCancelButton).when(parentSpy).findViewById(R.id.btn_search_cancel);

        securedNativeSmartRegisterFragment.setupSearchView(parentSpy);

        Assert.assertNotNull(securedNativeSmartRegisterFragment.getSearchView());
        Assert.assertNotNull(securedNativeSmartRegisterFragment.getSearchCancelView());

        Mockito.verify(parentSpy).findViewById(R.id.edt_search);
        Mockito.verify(parentSpy).findViewById(R.id.btn_search_cancel);

        Mockito.verify(searchView).addTextChangedListener(ArgumentMatchers.any(TextWatcher.class));
        Mockito.verify(searchCancelButton).setOnClickListener(ArgumentMatchers.any(SecuredNativeSmartRegisterFragment.SearchCancelHandler.class));

    }


    @Test
    public void testServiceModeViewSetsServiceModeViewWithCorrectCompoundDrawableValues() {


        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "serviceModeView", serviceModeView);

        securedNativeSmartRegisterFragment.setServiceModeViewDrawableRight(drawable);

        Mockito.verify(serviceModeView).setCompoundDrawables(null, null, drawable, null);

    }

    @Test
    public void testSetupSearchViewOnTextChangeListenerRefreshesClientsAdapterList() {
        securedNativeSmartRegisterFragment.setClientsAdapter(clientsAdapter);

        View parent = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.smart_register_activity, null, false);
        View parentSpy = Mockito.spy(parent);

        Mockito.doReturn(searchCancelButton).when(parentSpy).findViewById(R.id.btn_search_cancel);

        securedNativeSmartRegisterFragment.setupSearchView(parentSpy);
        securedNativeSmartRegisterFragment.getSearchView().setText(MY_TEST_SEARCH_TEXT);

        Mockito.verify(clientsAdapter).refreshList(villageFilterArgumentCaptor.capture(), serviceModeOptionArgumentCaptor.capture(), ecSearchOptionArgumentCaptor.capture(), sortOptionArgumentCaptor.capture());

        Assert.assertNotNull(ecSearchOptionArgumentCaptor.getValue());
        Assert.assertEquals(1, ecSearchOptionArgumentCaptor.getAllValues().size());

        Mockito.verify(searchCancelButton).setVisibility(ArgumentMatchers.anyInt());

    }

    @Test
    public void testSetupSearchViewOnTextChangeListenerSetsCorrectStateForSearchCancelButton() {
        securedNativeSmartRegisterFragment.setClientsAdapter(clientsAdapter);

        View parent = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.smart_register_activity, null, false);
        View parentSpy = Mockito.spy(parent);

        Mockito.doReturn(searchCancelButton).when(parentSpy).findViewById(R.id.btn_search_cancel);

        securedNativeSmartRegisterFragment.setupSearchView(parentSpy);
        securedNativeSmartRegisterFragment.getSearchView().setText(MY_TEST_SEARCH_TEXT);

        Mockito.verify(searchCancelButton).setVisibility(View.VISIBLE);

        securedNativeSmartRegisterFragment.getSearchView().setText("");

        Mockito.verify(searchCancelButton).setVisibility(View.INVISIBLE);

    }

    @Test
    public void testGotoNextPageInvokesRequiredMethods() {

        Assert.assertNotEquals(clientsAdapter, securedNativeSmartRegisterFragment.getClientsAdapter());
        securedNativeSmartRegisterFragment.setClientsAdapter(clientsAdapter);

        Assert.assertEquals(clientsAdapter, securedNativeSmartRegisterFragment.getClientsAdapter());

        securedNativeSmartRegisterFragment.gotoNextPage();

        Mockito.verify(clientsAdapter).nextPage();
        Mockito.verify(clientsAdapter).notifyDataSetChanged();

    }

    @Test
    public void testGoBackToPreviousPageInvokesRequiredMethods() {

        Assert.assertNotEquals(clientsAdapter, securedNativeSmartRegisterFragment.getClientsAdapter());
        securedNativeSmartRegisterFragment.setClientsAdapter(clientsAdapter);

        Assert.assertEquals(clientsAdapter, securedNativeSmartRegisterFragment.getClientsAdapter());

        securedNativeSmartRegisterFragment.goBackToPreviousPage();
        Mockito.verify(clientsAdapter).previousPage();
        Mockito.verify(clientsAdapter).notifyDataSetChanged();

    }

    @Test
    public void testOnServiceModeSelectionUpdatesServiceModeViewWithCorrectValue() {

        ServiceModeOption serviceModeOptionLocal = securedNativeSmartRegisterFragment.getCurrentServiceModeOption();
        Assert.assertNull(serviceModeOptionLocal);

        Mockito.doReturn(GENERIC_TEXT_PHRASE).when(serviceModeOption).name();
        Mockito.doReturn(linearLayout).when(view).findViewById(R.id.clients_header_layout);

        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "serviceModeView", serviceModeView);
        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "clientsAdapter", clientsAdapter);

        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragmentSpy = Mockito.spy(securedNativeSmartRegisterFragment);
        Mockito.doReturn(customFontTextView).when(securedNativeSmartRegisterFragmentSpy).getCustomFontTextViewHeader();
        Mockito.doReturn(RuntimeEnvironment.application.getResources()).when(securedNativeSmartRegisterFragmentSpy).getResources();

        securedNativeSmartRegisterFragmentSpy.onServiceModeSelection(serviceModeOption, view);
        Assert.assertNotNull(serviceModeOption);

        Mockito.verify(serviceModeView).setText(GENERIC_TEXT_PHRASE);

    }

    @Test
    public void testOnServiceModeSelectionRefreshesClientsAdapterWithCorrectServiceModeOptionValue() {

        Mockito.doReturn(GENERIC_TEXT_PHRASE).when(serviceModeOption).name();
        Mockito.doReturn(linearLayout).when(view).findViewById(R.id.clients_header_layout);

        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "serviceModeView", serviceModeView);
        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "clientsAdapter", clientsAdapter);


        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragmentSpy = Mockito.spy(securedNativeSmartRegisterFragment);
        Mockito.doReturn(customFontTextView).when(securedNativeSmartRegisterFragmentSpy).getCustomFontTextViewHeader();
        Mockito.doReturn(RuntimeEnvironment.application.getResources()).when(securedNativeSmartRegisterFragmentSpy).getResources();

        securedNativeSmartRegisterFragmentSpy.onServiceModeSelection(serviceModeOption, view);

        Mockito.verify(clientsAdapter).refreshList(villageFilterArgumentCaptor.capture(), serviceModeOptionArgumentCaptor.capture(), ecSearchOptionArgumentCaptor.capture(), sortOptionArgumentCaptor.capture());

        ServiceModeOption serviceModeName = serviceModeOptionArgumentCaptor.getValue();

        Assert.assertEquals(GENERIC_TEXT_PHRASE, serviceModeName.name());

    }

    @Test
    public void testOnSortSelectionRefreshesClientsAdapterWithCorrectSortOptionValue() {


        Mockito.doReturn(GENERIC_TEXT_PHRASE).when(sortOption).name();
        Mockito.doReturn(linearLayout).when(view).findViewById(R.id.clients_header_layout);

        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "clientsAdapter", clientsAdapter);
        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "currentSortOption", sortOption);
        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "appliedSortView", appliedSortView);

        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragmentSpy = Mockito.spy(securedNativeSmartRegisterFragment);
        Mockito.doReturn(customFontTextView).when(securedNativeSmartRegisterFragmentSpy).getCustomFontTextViewHeader();
        Mockito.doReturn(RuntimeEnvironment.application.getResources()).when(securedNativeSmartRegisterFragmentSpy).getResources();

        securedNativeSmartRegisterFragmentSpy.onSortSelection(sortOption);

        Mockito.verify(clientsAdapter).refreshList(villageFilterArgumentCaptor.capture(), serviceModeOptionArgumentCaptor.capture(), ecSearchOptionArgumentCaptor.capture(), sortOptionArgumentCaptor.capture());

        SortOption sortName = sortOptionArgumentCaptor.getValue();

        Assert.assertEquals(GENERIC_TEXT_PHRASE, sortName.name());


    }

    @Test
    public void testOnFilterSelectionRefreshesClientsAdapterWithCorrectFilterOptionValue() {

        Mockito.doReturn(GENERIC_TEXT_PHRASE).when(filterOption).name();
        Mockito.doReturn(linearLayout).when(view).findViewById(R.id.clients_header_layout);


        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "clientsAdapter", clientsAdapter);
        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "currentVillageFilter", filterOption);
        Whitebox.setInternalState(securedNativeSmartRegisterFragment, "appliedVillageFilterView", appliedVillageFilterView);

        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragmentSpy = Mockito.spy(securedNativeSmartRegisterFragment);
        Mockito.doReturn(customFontTextView).when(securedNativeSmartRegisterFragmentSpy).getCustomFontTextViewHeader();
        Mockito.doReturn(RuntimeEnvironment.application.getResources()).when(securedNativeSmartRegisterFragmentSpy).getResources();

        securedNativeSmartRegisterFragmentSpy.onFilterSelection(filterOption);

        Mockito.verify(clientsAdapter).refreshList(villageFilterArgumentCaptor.capture(), serviceModeOptionArgumentCaptor.capture(), ecSearchOptionArgumentCaptor.capture(), sortOptionArgumentCaptor.capture());

        FilterOption filterOption = villageFilterArgumentCaptor.getValue();

        Assert.assertEquals(GENERIC_TEXT_PHRASE, filterOption.name());


    }

    @Test
    public void refreshListView() {

        Mockito.doNothing().when(securedNativeSmartRegisterFragment).onResumption();

        securedNativeSmartRegisterFragment.refreshListView();

        Mockito.verify(securedNativeSmartRegisterFragment).setRefreshList(true);
        Mockito.verify(securedNativeSmartRegisterFragment).onResumption();
        Mockito.verify(securedNativeSmartRegisterFragment).setRefreshList(false);
    }


    @Test
    public void testSetupNavBarViewsInitsViewCorrectly() {

        View parent = LayoutInflater.from(RuntimeEnvironment.application.getApplicationContext()).inflate(R.layout.smart_register_activity, null, false);

        View parentSpy = Mockito.spy(parent);

        Mockito.doReturn(buttonBackHome).when(parentSpy).findViewById(R.id.btn_back_to_home);
        Mockito.doReturn(customFontTextView).when(securedNativeSmartRegisterFragment).getCustomFontTextViewHeader();

        Mockito.doReturn(LayoutInflater.from(RuntimeEnvironment.application.getApplicationContext())).when(activity).getLayoutInflater();
        Mockito.doReturn(activity).when(clientsView).getContext();
        Mockito.doReturn(clientsView).when(parentSpy).findViewById(R.id.list);

        SecuredNativeSmartRegisterFragment securedNativeSmartRegisterFragmentSpy = Mockito.spy(securedNativeSmartRegisterFragment);
        Mockito.doReturn(defaultOptionsProvider).when(securedNativeSmartRegisterFragmentSpy).getDefaultOptionsProvider();
        Mockito.doReturn(navBarOptionsProvider).when(securedNativeSmartRegisterFragmentSpy).getNavBarOptionsProvider();

        Mockito.doNothing().when(securedNativeSmartRegisterFragmentSpy).onResumption();

        securedNativeSmartRegisterFragment.setupViews(parentSpy);


        Mockito.verify(filterOption).name();
        Mockito.verify(sortOption).name();
        Mockito.verify(serviceModeOption).name();
        Mockito.verify(defaultOptionsProvider).nameInShortFormForTitle();

        TextView textView = Whitebox.getInternalState(securedNativeSmartRegisterFragment, "titleLabelView");
        Assert.assertNotNull(textView);

        Assert.assertEquals(GENERIC_TEXT_PHRASE, textView.getText());
    }

}
