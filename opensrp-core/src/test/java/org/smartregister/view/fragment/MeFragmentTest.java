package org.smartregister.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;
import org.smartregister.util.Utils;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.contract.MeContract;

/**
 * Created by ndegwamartin on 2020-03-24.
 */

@PrepareForTest({Utils.class})
public class MeFragmentTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private View view;

    @Mock
    private FragmentActivity activity;

    @Mock
    private Bundle bundle;

    @Mock
    private RelativeLayout meLocationSection;

    @Mock
    private RelativeLayout settingSection;

    @Mock
    private RelativeLayout logoutSection;

    @Mock
    private LocationPickerView facilitySelection;

    public static final String TEST_SEARCH_HINT = "Test Search Hint";

    public static final String MY_TEST_SEARCH_TEXT = "My Testing Search Text";

    private static final String GENERIC_TEXT_PHRASE = "Move the Earth";

    private MeFragment meFragment;

    @Mock
    protected MeContract.Presenter presenter;

    private LayoutInflater layoutInflater;

    @Mock
    private ViewGroup viewGroup;

    @Before
    public void setUp() throws Exception {

        org.mockito.MockitoAnnotations.initMocks(this);

        meFragment = Mockito.mock(MeFragment.class, Mockito.CALLS_REAL_METHODS);

        ReflectionHelpers.setField(meFragment, "presenter", presenter);

        layoutInflater = LayoutInflater.from(RuntimeEnvironment.application);

        Mockito.doReturn(meLocationSection).when(view).findViewById(R.id.me_location_section);
        Mockito.doReturn(settingSection).when(view).findViewById(R.id.setting_section);
        Mockito.doReturn(logoutSection).when(view).findViewById(R.id.logout_section);
        Mockito.doReturn(facilitySelection).when(view).findViewById(R.id.facility_selection);

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getBooleanProperty(AllConstants.PROPERTY.DISABLE_LOCATION_PICKER_VIEW)).thenReturn(true);

        Mockito.doReturn(RuntimeEnvironment.application.getResources()).when(activity).getResources();

    }

    @Test
    public void assertFragmentInstantiatesSuccessufully() {

        Assert.assertNotNull(meFragment);
        Assert.assertNotNull(meFragment.presenter);
    }

    /*
    @Test
    public void testOnCreateInvokesInitializePresenterMethod() {

        meFragment.onCreate(bundle);
        Mockito.verify(meFragment).initializePresenter();
    }

     */

    @Test
    @Ignore
    public void testOnCreateViewReturnsCorrectLayoutView() {

        View layoutView = meFragment.onCreateView(layoutInflater, viewGroup, bundle);
        Assert.assertNotNull(layoutView);
        Mockito.verify(layoutInflater).inflate(R.layout.fragment_me, viewGroup, false);
    }

    @Test
    public void testOnViewCreatedInvokesRequiredSetupMethods() {

        meFragment.onViewCreated(view, bundle);

        Mockito.verify(meFragment).setUpViews(view);
        Mockito.verify(meFragment).setClickListeners();

        Mockito.verify(presenter).updateInitials();
        Mockito.verify(presenter).updateName();
    }


    @After
    public void tearDown() {
        meFragment = null;
        layoutInflater = null;
    }
}
