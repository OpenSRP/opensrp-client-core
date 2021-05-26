package org.smartregister.view.fragment;

import android.content.Intent;
import android.view.MenuItem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.service.UserService;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.controller.ANMController;

/**
 * Created by ndegwamartin on 2020-04-07.
 */
public class SecuredFragmentTest extends BaseUnitTest {

    private SecuredFragment securedFragment;

    @Mock
    private ANMController anmController;

    @Mock
    private Context context;

    @Mock
    private SecuredActivity activity;

    private DrishtiApplication application;

    @Mock
    private MenuItem menuItem;

    @Mock
    private UserService userService;

    @Captor
    private ArgumentCaptor<String> notificationMessageArgumentCaptor;

    @Captor
    private ArgumentCaptor<Intent> intentArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        securedFragment = Mockito.mock(SecuredFragment.class, Mockito.CALLS_REAL_METHODS);

        Mockito.doReturn(context).when(securedFragment).context();
        Mockito.doReturn(userService).when(context).userService();
        Mockito.doReturn(anmController).when(context).anmController();
        Mockito.doReturn(activity).when(securedFragment).getActivity();

        application = (DrishtiApplication) Mockito.spy(RuntimeEnvironment.application);
        Mockito.doReturn(application).when(activity).getApplication();
    }

    @Test
    public void assertSecuredFragmentInitsCorrectly() {

        Assert.assertNotNull(securedFragment);
    }

    /*
    @Test
    public void testOnCreateInitializesFragmentFields() {

        securedFragment.onCreate(bundle);

        Listener<Boolean> logoutListener = ReflectionHelpers.getField(securedFragment, "logoutListener");
        FormController formController = ReflectionHelpers.getField(securedFragment, "formController");
        ANMController anmController = ReflectionHelpers.getField(securedFragment, "anmController");
        NavigationController navigationController = ReflectionHelpers.getField(securedFragment, "navigationController");

        Assert.assertNotNull(logoutListener);
        Assert.assertNotNull(formController);
        Assert.assertNotNull(anmController);
        Assert.assertNotNull(navigationController);

    }

     */

    @Test
    public void assertOnResumeLogsOutCurrentUserIfContextIsUserLoggedOutIsTrue() {

        Mockito.doReturn(true).when(context).IsUserLoggedOut();

        securedFragment.onResume();

        Mockito.verify(application).logoutCurrentUser();
    }


    @Test
    public void assertOnResumeSetsOnPausedFlagToFalse() {
        ReflectionHelpers.setField(securedFragment, "isPaused", true);
        boolean onPauseFlag = securedFragment.isPaused();
        Assert.assertTrue(onPauseFlag);

        securedFragment.onResume();

        onPauseFlag = ReflectionHelpers.getField(securedFragment, "isPaused");
        Assert.assertFalse(onPauseFlag);
    }

    @Test
    public void assertOnPauseSetsOnPausedFlagToTrue() {
        ReflectionHelpers.setField(securedFragment, "isPaused", false);
        boolean onPauseFlag = ReflectionHelpers.getField(securedFragment, "isPaused");
        Assert.assertFalse(onPauseFlag);

        securedFragment.onPause();

        onPauseFlag = ReflectionHelpers.getField(securedFragment, "isPaused");
        Assert.assertTrue(onPauseFlag);
    }


    @Test
    public void onOptionsItemSelectedShowsCorrectToastMessageNotification() {

        Mockito.doReturn(R.id.switchLanguageMenuItem).when(menuItem).getItemId();
        Mockito.doReturn("Kyuk").when(userService).switchLanguagePreference();
        Mockito.doNothing().when(securedFragment).showToastNotification(ArgumentMatchers.anyString());

        securedFragment.onOptionsItemSelected(menuItem);

        Mockito.verify(securedFragment).showToastNotification(notificationMessageArgumentCaptor.capture());

        String capturedMessage = notificationMessageArgumentCaptor.getValue();

        Assert.assertEquals(R.string.language_change_prepend_message + " Kyuk. " + R.string.language_change_append_message + ".", capturedMessage);

    }

    @Test
    public void onOptionsItemSelectedDoesNotInvokeShowMessageNotificationIfLanguageNotSwitched() {

        Mockito.doReturn(0000).when(menuItem).getItemId(); //Random non existent id

        securedFragment.onOptionsItemSelected(menuItem);

        Mockito.verify(securedFragment, Mockito.times(0)).showToastNotification(ArgumentMatchers.anyString());

    }

    @Test
    public void assertLogoutUserInvokesUserserviceLogoutMethod() {

        Mockito.doNothing().when(securedFragment).startActivity(ArgumentMatchers.any(Intent.class));
        Mockito.doNothing().when(userService).logout();

        securedFragment.logoutUser();

        Mockito.verify(userService).logout();

    }

    @Test
    public void assertStartFormActivityInvokesNavigationToFormActivity() {

        Mockito.doNothing().when(securedFragment).startActivityForResult(ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt());

        securedFragment.startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, null);

        Mockito.verify(securedFragment).startActivityForResult(intentArgumentCaptor.capture(), integerArgumentCaptor.capture());

        Intent capturedIntent = intentArgumentCaptor.getValue();
        Assert.assertNotNull(capturedIntent);
        Assert.assertEquals("org.smartregister.view.activity.FormActivity", capturedIntent.getComponent().getClassName());

        Integer capturedInteger = integerArgumentCaptor.getValue();

        Assert.assertNotNull(capturedInteger);
        Assert.assertEquals(AllConstants.FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE, capturedInteger.intValue());

    }


    @Test
    public void assertStartMicroFormActivityInvokesNavigationToMicroFormActivity() {

        Mockito.doNothing().when(securedFragment).startActivityForResult(ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt());

        securedFragment.startMicroFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, null);

        Mockito.verify(securedFragment).startActivityForResult(intentArgumentCaptor.capture(), integerArgumentCaptor.capture());

        Intent capturedIntent = intentArgumentCaptor.getValue();
        Assert.assertNotNull(capturedIntent);
        Assert.assertEquals("org.smartregister.view.activity.MicroFormActivity", capturedIntent.getComponent().getClassName());

        Integer capturedInteger = integerArgumentCaptor.getValue();

        Assert.assertNotNull(capturedInteger);
        Assert.assertEquals(AllConstants.FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE, capturedInteger.intValue());

    }

    @Test
    public void testAddFieldOverridesIfExistAddsFieldOverrideParamsToRequestIntent() {

        String testMetadata = "{\"fieldOverrides\": \"metadata-override-values\"}";

        Mockito.doNothing().when(securedFragment).startActivityForResult(ArgumentMatchers.any(Intent.class), ArgumentMatchers.anyInt());

        securedFragment.startFormActivity(TEST_FORM_NAME, TEST_BASE_ENTITY_ID, testMetadata);

        Mockito.verify(securedFragment).startActivityForResult(intentArgumentCaptor.capture(), integerArgumentCaptor.capture());

        Intent capturedIntent = intentArgumentCaptor.getValue();
        Assert.assertNotNull(capturedIntent);
        Assert.assertEquals("metadata-override-values", capturedIntent.getStringExtra(AllConstants.FIELD_OVERRIDES_PARAM));


    }
}