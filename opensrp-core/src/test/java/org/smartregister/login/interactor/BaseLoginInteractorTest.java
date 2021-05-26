package org.smartregister.login.interactor;

import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.SyncConfiguration;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.account.AccountConfiguration;
import org.smartregister.account.AccountError;
import org.smartregister.account.AccountHelper;
import org.smartregister.account.AccountResponse;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.Setting;
import org.smartregister.domain.TimeStatus;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.domain.jsonmapping.Time;
import org.smartregister.domain.jsonmapping.User;
import org.smartregister.listener.OnCompleteClearDataCallback;
import org.smartregister.multitenant.ResetAppHelper;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.service.UserService;
import org.smartregister.shadows.LoginInteractorShadow;
import org.smartregister.shadows.ShadowNetworkUtils;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.smartregister.domain.LoginResponse.SUCCESS_WITH_EMPTY_RESPONSE;
import static org.smartregister.domain.LoginResponse.UNAUTHORIZED;
import static org.smartregister.domain.LoginResponse.UNKNOWN_RESPONSE;

/**
 * Created by samuelgithengi on 8/4/20.
 */

public class BaseLoginInteractorTest extends BaseRobolectricUnitTest {

    @InjectMocks
    private LoginInteractorShadow interactor;

    @Mock
    private BaseLoginContract.Presenter presenter;

    @Mock
    private BaseLoginContract.View view;

    @Mock
    private Context context;

    @Mock
    private UserService userService;

    @Mock
    private Bundle userDataBundle;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private SyncConfiguration syncConfiguration;

    @Captor
    private ArgumentCaptor<DialogInterface.OnClickListener> dialogCaptor;

    @Mock
    private DialogInterface dialogInterface;

    @Mock
    private ResetAppHelper resetAppHelper;

    @Captor
    private ArgumentCaptor<OnCompleteClearDataCallback> onCompleteClearDataCaptor;

    @Mock
    private UniqueIdRepository uniqueIdRepository;

    @Mock
    private AllSettings allSettings;

    @Captor
    private ArgumentCaptor<Setting> settingCaptor;

    private AppCompatActivity activity;

    private LoginResponseData loginResponseData;

    @Mock
    private AccountConfiguration accountConfiguration;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private HTTPAgent httpAgent;

    @Mock
    private AccountManager mAccountManager;

    @Mock
    private SharedPreferences.Editor sharePrefEditor;

    @Mock
    private AccountAuthenticatorXml accountAuthenticatorXml;

    @Mock
    private AccountResponse accountResponse;

    private String username = "johndoe";
    private char[] qwertyPassword = "qwerty".toCharArray();
    private char[] password = "password".toCharArray();

    @Before
    public void setUp() {
        when(presenter.getOpenSRPContext()).thenReturn(context);
        when(sharedPreferences.edit()).thenReturn(sharePrefEditor);
        when(allSharedPreferences.getPreferences()).thenReturn(sharedPreferences);
        when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        when(context.userService()).thenReturn(userService);
        when(presenter.getLoginView()).thenReturn(view);
        when(presenter.getPassword()).thenReturn(qwertyPassword);

        activity = Robolectric.buildActivity(AppCompatActivity.class).create().get();

        when(view.getActivityContext()).thenReturn(activity);

        loginResponseData = new LoginResponseData();
        loginResponseData.user = new User().withUsername(username).withRole("READ_ROLE").withRole("WRITE_ROLE").withRole("PROVIDER_ROLE");
        loginResponseData.time = new Time(new Date(), TimeZone.getTimeZone("Africa/Nairobi"));

        when(context.getHttpAgent()).thenReturn(httpAgent);
        when(context.allSettings()).thenReturn(allSettings);
        when(accountConfiguration.getAuthorizationEndpoint()).thenReturn("https://my-server.com/oauth/auth");
        when(accountConfiguration.getTokenEndpoint()).thenReturn("https://my-server.com/");
        when(accountConfiguration.getIssuerEndpoint()).thenReturn("https://my-server.com/oauth/issuer");
        when(accountConfiguration.getGrantTypesSupported()).thenReturn(new LinkedList<>(Arrays.asList(new String[]{AccountHelper.OAUTH.GRANT_TYPE.PASSWORD})));

        when(httpAgent.fetchOAuthConfiguration()).thenReturn(accountConfiguration);
        when(accountResponse.getAccessToken()).thenReturn("a-random-access-token");

        when(httpAgent.oauth2authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.any(char[].class), ArgumentMatchers.eq(AccountHelper.OAUTH.GRANT_TYPE.PASSWORD), ArgumentMatchers.eq("https://my-server.com/"))).thenReturn(accountResponse);

        when(accountAuthenticatorXml.getAccountType()).thenReturn("org.smartregister.core.testapp");

        Whitebox.setInternalState(CoreLibrary.getInstance(), "context", context);
        Whitebox.setInternalState(CoreLibrary.getInstance(), "accountManager", mAccountManager);
        Whitebox.setInternalState(CoreLibrary.getInstance(), "authenticatorXml", accountAuthenticatorXml);


    }

    @After
    public void tearDown() {
        initCoreLibrary();
    }

    @Test
    public void testOnDestroyShouldSetPresenterNull() {
        assertNotNull(Whitebox.getInternalState(interactor, "mLoginPresenter"));
        interactor.onDestroy(false);
        assertNull(Whitebox.getInternalState(interactor, "mLoginPresenter"));
    }

    @Test
    public void testLoginAttemptsRemoteLoginAndErrorsWithBaseURLIsMissing() {
        when(allSharedPreferences.fetchBaseURL("")).thenReturn("");
        interactor.login(new WeakReference<>(view), "johndoe", password);
        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(allSharedPreferences).savePreference("DRISHTI_BASE_URL", activity.getString(R.string.opensrp_url));
        verify(view).enableLoginButton(true);
        verify(view).showErrorDialog(activity.getString(R.string.remote_login_base_url_missing_error));
        verify(view, never()).goToHome(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testLoginAttemptsRemoteLoginAndErrorsWithGenericError() {
        interactor.login(new WeakReference<>(view), "johndoe", password);
        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(allSharedPreferences, never()).savePreference("DRISHTI_BASE_URL", activity.getString(R.string.opensrp_url));
        verify(view, never()).enableLoginButton(true);
        verify(view).showErrorDialog(activity.getString(R.string.remote_login_generic_error));
        verify(view, never()).goToHome(ArgumentMatchers.anyBoolean());
    }


    @Test
    public void testLoginAttemptsRemoteLoginAndErrorsWhenNoInternetConnectivity() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);

        when(httpAgent.oauth2authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.any(char[].class), ArgumentMatchers.eq(AccountHelper.OAUTH.GRANT_TYPE.PASSWORD), ArgumentMatchers.eq("https://my-server.com/"))).thenReturn(new AccountResponse(0, new AccountError(0, NO_INTERNET_CONNECTIVITY.name())));

        when(allSharedPreferences.fetchBaseURL("")).thenReturn(activity.getString(R.string.opensrp_url));

        interactor.login(new WeakReference<>(view), username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view).showErrorDialog(activity.getString(R.string.no_internet_connectivity));
        verify(view, never()).goToHome(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testLoginAttemptsRemoteLoginAndErrorsWhenNullLoginResponse() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);

        when(httpAgent.oauth2authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.any(char[].class), ArgumentMatchers.eq(AccountHelper.OAUTH.GRANT_TYPE.PASSWORD), ArgumentMatchers.eq("https://my-server.com/"))).thenReturn(new AccountResponse(0, new AccountError(0, null)));

        when(allSharedPreferences.fetchBaseURL("")).thenReturn(activity.getString(R.string.opensrp_url));

        interactor.login(new WeakReference<>(view), username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view).showErrorDialog(activity.getString(R.string.remote_login_generic_error));
        verify(view, never()).goToHome(ArgumentMatchers.anyBoolean());
    }


    @Test
    public void testLoginAttemptsRemoteLoginAndErrorsWhenResponseUnknown() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);
        when(httpAgent.oauth2authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.any(char[].class), ArgumentMatchers.eq(AccountHelper.OAUTH.GRANT_TYPE.PASSWORD), ArgumentMatchers.eq("https://my-server.com/"))).thenReturn(new AccountResponse(0, new AccountError(0, UNKNOWN_RESPONSE.name())));
        when(allSharedPreferences.fetchBaseURL("")).thenReturn(activity.getString(R.string.opensrp_url));

        interactor.login(new WeakReference<>(view), username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view).showErrorDialog(activity.getString(R.string.unknown_response));
        verify(view, never()).goToHome(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testLoginAttemptsRemoteLoginAndErrorsWhenUnauthorized() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);
        when(httpAgent.oauth2authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.any(char[].class), ArgumentMatchers.eq(AccountHelper.OAUTH.GRANT_TYPE.PASSWORD), ArgumentMatchers.eq("https://my-server.com/"))).thenReturn(new AccountResponse(0, new AccountError(0, UNAUTHORIZED.name())));
        when(allSharedPreferences.fetchBaseURL("")).thenReturn(activity.getString(R.string.opensrp_url));

        interactor.login(new WeakReference<>(view), username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view).showErrorDialog(activity.getString(R.string.unauthorized));
        verify(view, never()).goToHome(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testLoginAttemptsRemoteLoginAndErrorsWhenTimeIsWrong() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);

        when(httpAgent.oauth2authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.any(char[].class), ArgumentMatchers.eq(AccountHelper.OAUTH.GRANT_TYPE.PASSWORD), ArgumentMatchers.eq("https://my-server.com/"))).thenReturn(accountResponse);
        when(userService.fetchUserDetails(ArgumentMatchers.anyString())).thenReturn(LoginResponse.SUCCESS.withPayload(loginResponseData));
        when(userService.saveUserCredentials(username, qwertyPassword, loginResponseData)).thenReturn(userDataBundle);

        when(userService.validateDeviceTime(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenReturn(TimeStatus.TIME_MISMATCH);
        when(userService.isUserInPioneerGroup(username)).thenReturn(true);
        when(allSharedPreferences.fetchBaseURL("")).thenReturn(activity.getString(R.string.opensrp_url));
        Whitebox.setInternalState(AllConstants.class, "TIME_CHECK", true);

        interactor.login(new WeakReference<>(view), username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view).showErrorDialog(activity.getString(TimeStatus.TIME_MISMATCH.getMessage()));
        verify(view, never()).goToHome(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testLoginAttemptsRemoteLoginAndErrorsWhenTimeZoneIsWrong() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);
        when(userService.fetchUserDetails(ArgumentMatchers.anyString())).thenReturn(LoginResponse.SUCCESS.withPayload(loginResponseData));
        when(userService.saveUserCredentials(username, qwertyPassword, loginResponseData)).thenReturn(userDataBundle);
        when(userService.validateDeviceTime(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenReturn(TimeStatus.TIMEZONE_MISMATCH);
        when(userService.isUserInPioneerGroup(username)).thenReturn(true);
        when(allSharedPreferences.fetchBaseURL("")).thenReturn(activity.getString(R.string.opensrp_url));
        Whitebox.setInternalState(AllConstants.class, "TIME_CHECK", true);

        interactor.login(new WeakReference<>(view), username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view).showErrorDialog(activity.getString(TimeStatus.TIMEZONE_MISMATCH.getMessage(), TimeZone.getTimeZone("Africa/Nairobi").getDisplayName()));
        verify(view, never()).goToHome(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testLoginAttemptsRemoteLoginAndErrorsWithErrorFromEnum() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);
        when(httpAgent.oauth2authenticate(ArgumentMatchers.anyString(), ArgumentMatchers.any(char[].class), ArgumentMatchers.eq(AccountHelper.OAUTH.GRANT_TYPE.PASSWORD), ArgumentMatchers.eq("https://my-server.com/"))).thenReturn(new AccountResponse(0, new AccountError(0, SUCCESS_WITH_EMPTY_RESPONSE.name())));

        when(userService.validateDeviceTime(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenReturn(TimeStatus.TIMEZONE_MISMATCH);
        when(userService.isUserInPioneerGroup(username)).thenReturn(true);
        when(allSharedPreferences.fetchBaseURL("")).thenReturn(activity.getString(R.string.opensrp_url));
        Whitebox.setInternalState(AllConstants.class, "TIME_CHECK", true);

        interactor.login(new WeakReference<>(view), username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view).showErrorDialog(LoginResponse.SUCCESS_WITH_EMPTY_RESPONSE.message());
        verify(view, never()).goToHome(ArgumentMatchers.anyBoolean());
    }

    @Test
    @Config(shadows = {ShadowNetworkUtils.class})
    public void testLoginAttemptsRemoteLoginAndNavigatesToHome() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);
        when(userService.fetchUserDetails(ArgumentMatchers.anyString())).thenReturn(LoginResponse.SUCCESS.withPayload(loginResponseData));
        when(userService.validateDeviceTime(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenReturn(TimeStatus.TIMEZONE_MISMATCH);
        when(userService.isUserInPioneerGroup(username)).thenReturn(true);
        when(userService.saveUserCredentials(username, qwertyPassword, loginResponseData)).thenReturn(userDataBundle);
        when(allSharedPreferences.fetchBaseURL("")).thenReturn(activity.getString(R.string.opensrp_url));

        interactor.login(new WeakReference<>(view), username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view).goToHome(true);
    }


    @Test
    public void testLoginWithLocalFlagShouldAttemptRemoteLoginAndResetAppForNewUserAndStartsLogin() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);
        Whitebox.setInternalState(CoreLibrary.getInstance(), "syncConfiguration", syncConfiguration);
        Whitebox.setInternalState(interactor, "resetAppHelper", resetAppHelper);
        when(view.getAppCompatActivity()).thenReturn(activity);
        when(syncConfiguration.clearDataOnNewTeamLogin()).thenReturn(true);
        when(userService.fetchUserDetails(ArgumentMatchers.anyString())).thenReturn(LoginResponse.SUCCESS.withPayload(loginResponseData));
        when(userService.saveUserCredentials(username, qwertyPassword, loginResponseData)).thenReturn(userDataBundle);
        when(userService.isUserInPioneerGroup(username)).thenReturn(false);
        when(allSharedPreferences.fetchBaseURL("")).thenReturn(activity.getString(R.string.opensrp_url));

        interactor = spy(interactor);
        interactor.loginWithLocalFlag(new WeakReference<>(view), false, username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view).showClearDataDialog(dialogCaptor.capture());
        dialogCaptor.getValue().onClick(dialogInterface, DialogInterface.BUTTON_POSITIVE);
        verify(dialogInterface).dismiss();
        verify(resetAppHelper).startResetProcess(ArgumentMatchers.eq(activity), onCompleteClearDataCaptor.capture());
        onCompleteClearDataCaptor.getValue().onComplete();
        verify(interactor).login(ArgumentMatchers.any(), ArgumentMatchers.eq(username), ArgumentMatchers.eq(qwertyPassword));

    }


    @Test
    public void testLoginWithLocalFlagShouldFailsForDifferentTeam() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);
        Whitebox.setInternalState(interactor, "resetAppHelper", resetAppHelper);
        Whitebox.setInternalState(CoreLibrary.getInstance(), "syncConfiguration", syncConfiguration);
        when(syncConfiguration.clearDataOnNewTeamLogin()).thenReturn(false);
        when(view.getAppCompatActivity()).thenReturn(activity);
        when(userService.fetchUserDetails(ArgumentMatchers.anyString())).thenReturn(LoginResponse.SUCCESS.withPayload(loginResponseData));
        when(userService.saveUserCredentials(username, qwertyPassword, loginResponseData)).thenReturn(userDataBundle);
        when(userService.isUserInPioneerGroup(username)).thenReturn(false);
        when(allSharedPreferences.fetchBaseURL("")).thenReturn(activity.getString(R.string.opensrp_url));

        interactor.loginWithLocalFlag(new WeakReference<>(view), false, username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view, never()).goToHome(true);
        verify(view, never()).showClearDataDialog(dialogCaptor.capture());
        verify(view).showErrorDialog(activity.getString(R.string.unauthorized_group));


    }


    @Test
    public void testLocalLoginShouldShowErrorWhenNotAuthenticated() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);
        when(allSharedPreferences.fetchForceRemoteLogin(username)).thenReturn(false);
        when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        when(allSharedPreferences.isRegisteredANM(username)).thenReturn(true);
        when(userService.isUserInValidGroup(username, qwertyPassword)).thenReturn(false);

        interactor.login(new WeakReference<>(view), username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view).showErrorDialog(activity.getString(R.string.unauthorized));
        verify(view, never()).goToHome(ArgumentMatchers.anyBoolean());
    }

    @Test
    @Config(shadows = {ShadowNetworkUtils.class})
    public void testLocalLoginShouldShowNavigateToHomeAndReleaseIds() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);
        when(context.getUniqueIdRepository()).thenReturn(uniqueIdRepository);
        when(allSharedPreferences.fetchForceRemoteLogin(username)).thenReturn(false);
        when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        when(allSharedPreferences.isRegisteredANM(username)).thenReturn(true);
        when(userService.isUserInValidGroup(username, qwertyPassword)).thenReturn(true);
        when(userService.fetchUserDetails(ArgumentMatchers.anyString())).thenReturn(LoginResponse.SUCCESS.withPayload(loginResponseData));
        when(userService.saveUserCredentials(username, qwertyPassword, loginResponseData)).thenReturn(userDataBundle);

        interactor.login(new WeakReference<>(view), username, qwertyPassword);

        verify(view).hideKeyboard();
        verify(view).enableLoginButton(false);
        verify(view).enableLoginButton(true);
        verify(view, never()).showErrorDialog(ArgumentMatchers.anyString());
        verify(view).goToHome(false);
        verify(userService).localLoginWith(username);
        verify(uniqueIdRepository, timeout(ASYNC_TIMEOUT)).releaseReservedIds();
    }

    @Test
    public void testLocalLoginShouldInitiateRemoteLoginIfTimeCheckEnabled() {
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "userService", userService);
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "uniqueIdRepository", uniqueIdRepository);
        when(allSharedPreferences.fetchForceRemoteLogin(username)).thenReturn(false);
        when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        when(userService.isUserInValidGroup(username, qwertyPassword)).thenReturn(true);
        Whitebox.setInternalState(AllConstants.class, "TIME_CHECK", true);
        when(userService.validateDeviceTime(ArgumentMatchers.any(), ArgumentMatchers.anyLong())).thenReturn(TimeStatus.TIME_MISMATCH);
        interactor = spy(interactor);

        interactor.login(new WeakReference<>(view), username, qwertyPassword);

        verify(view, never()).goToHome(ArgumentMatchers.anyBoolean());
        verify(userService, never()).localLoginWith(username);
        verify(interactor).loginWithLocalFlag(ArgumentMatchers.any(), ArgumentMatchers.eq(false), ArgumentMatchers.eq(username), ArgumentMatchers.eq(qwertyPassword));
    }


    @Test
    public void testGetFlexValueShouldReturnCorrectFlex() {
        assertEquals(5, interactor.getFlexValue(2));
        assertEquals(20, interactor.getFlexValue(60));
    }

    @Test
    public void testProcessServerSettingsShouldSaveSettings() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AllConstants.PREF_KEY.SETTINGS, new JSONArray("[{\"identifier\":\"login\",\"serverVersion\":1212121}]"));
        LoginResponse loginResponse = LoginResponse.SUCCESS.withPayload(loginResponseData);
        loginResponse.setRawData(jsonObject);

        interactor.processServerSettings(loginResponse);

        verify(allSettings).put(AllSharedPreferences.LAST_SETTINGS_SYNC_TIMESTAMP, "1212121");
        verify(allSettings).putSetting(settingCaptor.capture());
        assertEquals("login", settingCaptor.getValue().getKey());
        assertEquals("1212121", settingCaptor.getValue().getVersion());
    }


}
