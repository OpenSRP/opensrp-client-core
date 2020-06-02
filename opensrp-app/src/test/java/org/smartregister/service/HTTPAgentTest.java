package org.smartregister.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.common.io.BaseEncoding;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.SyncConfiguration;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.account.AccountConfiguration;
import org.smartregister.account.AccountHelper;
import org.smartregister.account.AccountResponse;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.LoginResponseTestData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Base64.class, File.class, FileInputStream.class, Context.class, AccountHelper.class, CoreLibrary.class, IOUtils.class})
public class HTTPAgentTest {
    @Mock
    private android.content.Context context;

    @Mock
    private Context openSrpContext;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private DristhiConfiguration dristhiConfiguration;

    @Mock
    private ProfileImage profileImage;

    @Mock
    private AccountAuthenticatorXml accountAuthenticatorXml;

    @Mock
    private Account account;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private AccountManager accountManager;

    @Mock
    private SyncConfiguration syncConfiguration;

    @Mock
    private HttpURLConnection httpURLConnection;

    @Mock
    private HttpsURLConnection httpsURLConnection;

    @Mock
    private OutputStream outputStream;

    @Mock
    private InputStream inputStream;

    @Mock
    private InputStream errorStream;

    @Rule
    private TemporaryFolder folder = new TemporaryFolder();

    private HTTPAgent httpAgent;
    private static final String TEST_USERNAME = "demo";
    private static final String TEST_PASSWORD = "password";
    public static final String TEST_BASE_URL = "https://my-server.com/";
    private static final String TEST_TOKEN_ENDPOINT = "https://my-server.com/oauth/token";
    private static final String SECURE_RESOURCE_ENDPOINT = "https://my-server.com/my/secure/resource";
    private static final String KEYClOAK_CONFIGURATION_ENDPOINT = "https://my-server.com/rest/config/keycloak";
    private static final String USER_DETAILS_ENDPOINT = "https://my-server.com/opensrp/security/authenticate";

    private final String SAMPLE_TEST_TOKEN = "sample-test-token";
    private final String SAMPLE_REFRESH_TOKEN = "sample-refresh-token";
    private static final String TEST_CLIENT_ID = "my-client-id";
    private static final String TEST_CLIENT_SECRET = "my-client-secret";
    private static final String TOKEN_REQUEST_SERVER_RESPONSE = "{\r\naccess_token:\"1r9A8zi5E3r@Zz\",\r\ntoken_type: \"bearer\",\r\nrefresh_token: \"text_token\",\r\nexpires_in: 3600,\r\nrefresh_expires_in: 36000,\r\nscope: \"read write trust\"\r\n\r\n}";
    private static final String TOKEN_BAD_REQUEST_SERVER_RESPONSE = "{status_code:400,\"error\":\"invalid_grant\",\"error_description\":\"Code not valid\"}";
    private static final String TOKEN_INTERNAL_SERVER_RESPONSE = "{status_code:500,\"error\":\"internal server error\",\"error_description\":\"Oops, something went wrong\"}";
    private static final String OAUTH_CONFIGURATION_SERVER_RESPONSE = "{\"issuer\":\"https://my-server.com/oauth/issuer\",\r\n\"authorization_endpoint\": \"https://my-server.com/oauth/auth\",\r\n\"token_endpoint\": \"https://my-server.com/oauth/token\",\r\n\"grant_types_supported\":[\"authorization code\",\"implicit\",\"password\"]\r\n}";
    private static final String FETCH_DATA_REQUEST_SERVER_RESPONSE = "{status:{\"response_status\":\"success\"},payload: \"My secure resources from the server\"\r\n\r\n}";
    private static final String SAMPLE_POST_REQUEST_PAYLOAD = "{\"payload\":\"My POST Payload\"}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(Context.class);
        PowerMockito.when(Context.getInstance()).thenReturn(openSrpContext);
        Mockito.doReturn(context).when(context).getApplicationContext();

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        Mockito.doReturn(accountManager).when(coreLibrary).getAccountManager();
        Mockito.doReturn(accountAuthenticatorXml).when(coreLibrary).getAccountAuthenticatorXml();

        Mockito.doReturn(accountManager).when(coreLibrary).getAccountManager();
        Mockito.doReturn(syncConfiguration).when(coreLibrary).getSyncConfiguration();
        Mockito.doReturn(1).when(syncConfiguration).getMaxAuthenticationRetries();

        PowerMockito.mockStatic(AccountHelper.class);
        PowerMockito.when(AccountHelper.getOauthAccountByType(accountAuthenticatorXml.getAccountType())).thenReturn(account);

        httpAgent = new HTTPAgent(context, allSharedPreferences, dristhiConfiguration);
        httpAgent.setConnectTimeout(60000);
        httpAgent.setReadTimeout(60000);
    }

    @Test
    public void testFetchFailsGivenWrongUrl() {
        Response<String> resp = httpAgent.fetch("wrong.url");
        Assert.assertEquals(ResponseStatus.failure, resp.status());
    }

    @Test
    public void testFetchPassesGivenCorrectUrl() {
        PowerMockito.mockStatic(Base64.class);
        Response<String> resp = httpAgent.fetch("https://google.com");
        Assert.assertEquals(ResponseStatus.success, resp.status());
    }

    @Test
    public void testPostFailsGivenWrongUrl() {
        HashMap<String, String> map = new HashMap<>();
        map.put("title", "OpenSRP Testing Tuesdays");
        JSONObject jObject = new JSONObject(map);
        Response<String> resp = httpAgent.post("wrong.url", jObject.toString());
        Assert.assertEquals(ResponseStatus.failure, resp.status());
    }

    @Test
    public void testPostPassesGivenCorrectUrl() {
        PowerMockito.mockStatic(Base64.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("title", "OpenSRP Testing Tuesdays");
        JSONObject jObject = new JSONObject(map);
        Response<String> resp = httpAgent.post("http://www.mocky.io/v2/5e54d9333100006300eb33a8", jObject.toString());
        Assert.assertEquals(ResponseStatus.success, resp.status());
    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentials() {
        PowerMockito.mockStatic(Base64.class);
        LoginResponse resp = httpAgent.urlCanBeAccessWithGivenCredentials("http://www.mocky.io/v2/5e54de89310000d559eb33d9", "", "");
        Assert.assertEquals(LoginResponse.SUCCESS.message(), resp.message());
    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsGivenWrongUrl() {
        PowerMockito.mockStatic(Base64.class);
        LoginResponse resp = httpAgent.urlCanBeAccessWithGivenCredentials("wrong.url", "", "");
        Assert.assertEquals(LoginResponse.MALFORMED_URL.message(), resp.message());
    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsGivenEmptyResp() {
        PowerMockito.mockStatic(Base64.class);
        LoginResponse resp = httpAgent.urlCanBeAccessWithGivenCredentials("http://mockbin.org/bin/e42f7256-18b2-40b9-a20c-40fdc564d06f", "", "");
        Assert.assertEquals(LoginResponse.SUCCESS_WITH_EMPTY_RESPONSE.message(), resp.message());
    }

    @Test
    public void testfetchWithCredentialsFailsGivenWrongUrl() {
        Response<String> resp = httpAgent.fetchWithCredentials("wrong.url", SAMPLE_TEST_TOKEN);
        Assert.assertEquals(ResponseStatus.failure, resp.status());
    }

    @Test
    public void testfetchWithCredentialsPassesGivenCorrectUrl() {
        PowerMockito.mockStatic(Base64.class);
        Response<String> resp = httpAgent.fetchWithCredentials("https://google.com", SAMPLE_TEST_TOKEN);
        Assert.assertEquals(ResponseStatus.success, resp.status());
    }

    @Test
    public void testHttpImagePostGivenWrongUrl() {
        String resp = httpAgent.httpImagePost("wrong.url", profileImage);
        Assert.assertEquals("", resp);
    }

    @Test
    public void testHttpImagePostTimeout() {
        PowerMockito.mockStatic(Base64.class);
        PowerMockito.mockStatic(File.class);
        PowerMockito.mockStatic(FileInputStream.class);

        ProfileImage profileImage2 = new ProfileImage();
        profileImage2.setFilepath("test");

        String resp = httpAgent.httpImagePost("http://www.mocky.io/v2/5e54de89310000d559eb33d9?mocky-delay=60000ms", profileImage2);
        Assert.assertEquals("", resp);
    }

    @Test
    public void testPostWithJsonResponse() {
        PowerMockito.mockStatic(Base64.class);
        HashMap<String, String> map = new HashMap<>();
        map.put("title", "OpenSRP Testing Tuesdays");
        JSONObject jObject = new JSONObject(map);
        Response<String> resp = httpAgent.postWithJsonResponse("http://www.mocky.io/v2/5e54d9333100006300eb33a8", jObject.toString());
        Assert.assertEquals(ResponseStatus.success, resp.status());
    }


    @Test
    public void testOauth2authenticateCreatesUrlConnectionWithCorrectParameters() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);
        Mockito.doReturn(TEST_CLIENT_ID).when(syncConfiguration).getOauthClientId();
        Mockito.doReturn(TEST_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

        Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
        Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();
        Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpURLConnection).getResponseCode();

        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toString(inputStream)).thenReturn(TOKEN_REQUEST_SERVER_RESPONSE);


        AccountResponse accountResponse = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);

        Assert.assertNotNull(accountResponse);
        Assert.assertEquals(200, accountResponse.getStatus());
        Assert.assertEquals("1r9A8zi5E3r@Zz", accountResponse.getAccessToken());
        Assert.assertEquals("bearer", accountResponse.getTokenType());
        Assert.assertEquals("text_token", accountResponse.getRefreshToken());
        Assert.assertEquals(Integer.valueOf("3600"), accountResponse.getExpiresIn());
        Assert.assertEquals(Integer.valueOf("36000"), accountResponse.getRefreshExpiresIn());
        Assert.assertEquals("read write trust", accountResponse.getScope());


        Mockito.verify(httpURLConnection).setConnectTimeout(60000);
        Mockito.verify(httpURLConnection).setReadTimeout(60000);

        String requestParams = "&grant_type=" + AccountHelper.OAUTH.GRANT_TYPE.PASSWORD + "&username=" + TEST_USERNAME + "&password=" + TEST_PASSWORD + "&client_id=" + TEST_CLIENT_ID + "&client_secret=" + TEST_CLIENT_SECRET;

        Mockito.verify(httpURLConnection).setFixedLengthStreamingMode(requestParams.getBytes().length);
        Mockito.verify(httpURLConnection).setDoOutput(true);
        Mockito.verify(httpURLConnection).setInstanceFollowRedirects(false);
        Mockito.verify(httpURLConnection).setRequestMethod("POST");
        Mockito.verify(httpURLConnection).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        Mockito.verify(httpURLConnection).setRequestProperty("charset", "utf-8");
        Mockito.verify(httpURLConnection).setRequestProperty(ArgumentMatchers.eq("Content-Length"), ArgumentMatchers.anyString());
        Mockito.verify(httpURLConnection).setUseCaches(false);
        final String base64Auth = BaseEncoding.base64().encode(new String(TEST_CLIENT_ID + ":" + TEST_CLIENT_SECRET).getBytes());
        Mockito.verify(httpURLConnection).setRequestProperty(AllConstants.HTTP_REQUEST_HEADERS.AUTHORIZATION, AllConstants.HTTP_REQUEST_AUTH_TOKEN_TYPE.BASIC + " " + base64Auth);
        Mockito.verify(httpURLConnection).setInstanceFollowRedirects(false);

    }

    @Test
    public void testOauth2authenticateReturnsCorrectResponseForBadRequest() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);
        Mockito.doReturn(TEST_CLIENT_ID).when(syncConfiguration).getOauthClientId();
        Mockito.doReturn(TEST_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

        Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
        Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();
        Mockito.doReturn(errorStream).when(httpURLConnection).getErrorStream();
        Mockito.doReturn(HttpURLConnection.HTTP_BAD_REQUEST).when(httpURLConnection).getResponseCode();

        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toString(errorStream)).thenReturn(TOKEN_BAD_REQUEST_SERVER_RESPONSE);

        AccountResponse accountResponse = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);

        Assert.assertNotNull(accountResponse);
        Assert.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, accountResponse.getStatus());
        Assert.assertNotNull(accountResponse.getAccountError());
        Assert.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, accountResponse.getAccountError().getStatusCode());
        Assert.assertEquals("Code not valid", accountResponse.getAccountError().getErrorDescription());
        Assert.assertEquals("invalid_grant", accountResponse.getAccountError().getError());

    }

    @Test
    public void testOauth2authenticateReturnsCorrectAccountErrorResponseForMalformedURL() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);
        Mockito.doReturn(TEST_CLIENT_ID).when(syncConfiguration).getOauthClientId();
        Mockito.doReturn(TEST_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

        Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
        Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();

        Mockito.doThrow(new MalformedURLException()).when(httpURLConnection).getResponseCode();

        AccountResponse response = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getAccountError());
        Assert.assertEquals(0, response.getAccountError().getStatusCode());
        Assert.assertEquals(LoginResponse.MALFORMED_URL.name(), response.getAccountError().getError());

    }

    @Test
    public void testOauth2authenticateReturnsCorrectAccountErrorResponseForSocketTimeout() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);
        Mockito.doReturn(TEST_CLIENT_ID).when(syncConfiguration).getOauthClientId();
        Mockito.doReturn(TEST_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

        Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
        Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();

        Mockito.doThrow(new SocketTimeoutException()).when(httpURLConnection).getResponseCode();

        AccountResponse response = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getAccountError());
        Assert.assertEquals(0, response.getAccountError().getStatusCode());
        Assert.assertEquals(LoginResponse.TIMEOUT.name(), response.getAccountError().getError());

    }

    @Test
    public void testOauth2authenticateReturnsCorrectAccountErrorResponseForIOException() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);
        Mockito.doReturn(TEST_CLIENT_ID).when(syncConfiguration).getOauthClientId();
        Mockito.doReturn(TEST_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

        Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();
        Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();

        Mockito.doThrow(new IOException()).when(httpURLConnection).getResponseCode();

        AccountResponse response = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getAccountError());
        Assert.assertEquals(0, response.getAccountError().getStatusCode());
        Assert.assertEquals(LoginResponse.NO_INTERNET_CONNECTIVITY.name(), response.getAccountError().getError());

    }

    @Test
    public void testOauth2authenticateReturnsNonNullAccountErrorResponseForRandomException() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(TEST_TOKEN_ENDPOINT);
        Mockito.doReturn(TEST_CLIENT_ID).when(syncConfiguration).getOauthClientId();
        Mockito.doReturn(TEST_CLIENT_SECRET).when(syncConfiguration).getOauthClientSecret();

        Mockito.doReturn(errorStream).when(httpURLConnection).getErrorStream();
        Mockito.doReturn(outputStream).when(httpURLConnection).getOutputStream();

        Mockito.doReturn(HttpURLConnection.HTTP_INTERNAL_ERROR).when(httpURLConnection).getResponseCode();

        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toString(errorStream)).thenReturn(TOKEN_INTERNAL_SERVER_RESPONSE);

        AccountResponse response = httpAgentSpy.oauth2authenticate(TEST_USERNAME, TEST_PASSWORD, AccountHelper.OAUTH.GRANT_TYPE.PASSWORD, TEST_TOKEN_ENDPOINT);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getAccountError());
        Assert.assertEquals(HttpURLConnection.HTTP_INTERNAL_ERROR, response.getAccountError().getStatusCode());
        Assert.assertNotNull(response.getAccountError().getError());
        Assert.assertEquals("Oops, something went wrong", response.getAccountError().getErrorDescription());
        Assert.assertEquals("internal server error", response.getAccountError().getError());

    }

    @Test
    public void testFetchOAuthConfigurationProcessesConfigurationResponseCorrectly() throws Exception {
        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(KEYClOAK_CONFIGURATION_ENDPOINT);

        Mockito.doReturn(inputStream).when(httpURLConnection).getInputStream();
        Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpURLConnection).getResponseCode();

        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toString(inputStream)).thenReturn(OAUTH_CONFIGURATION_SERVER_RESPONSE);

        AccountConfiguration accountConfiguration = httpAgentSpy.fetchOAuthConfiguration();
        Assert.assertNotNull(accountConfiguration);
        Assert.assertEquals("https://my-server.com/oauth/auth", accountConfiguration.getAuthorizationEndpoint());
        Assert.assertEquals("https://my-server.com/oauth/issuer", accountConfiguration.getIssuerEndpoint());
        Assert.assertEquals(TEST_TOKEN_ENDPOINT, accountConfiguration.getTokenEndpoint());

        List<String> grantTypes = accountConfiguration.getGrantTypesSupported();
        Assert.assertNotNull(grantTypes);
        Assert.assertEquals("authorization code", grantTypes.get(0));
        Assert.assertEquals("implicit", grantTypes.get(1));
        Assert.assertEquals("password", grantTypes.get(2));
    }

    @Test
    public void testFetchInvalidatesCacheIfUnauthorizedAndReturnsCorrectResponse() throws Exception {

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(SECURE_RESOURCE_ENDPOINT);
        Mockito.doReturn(errorStream).when(httpURLConnection).getErrorStream();
        Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpURLConnection).getResponseCode();

        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toString(errorStream)).thenReturn(FETCH_DATA_REQUEST_SERVER_RESPONSE);

        PowerMockito.mockStatic(AccountHelper.class);
        PowerMockito.when(AccountHelper.getCachedOAuthToken(accountAuthenticatorXml.getAccountType(), AccountHelper.TOKEN_TYPE.PROVIDER)).thenReturn(SAMPLE_TEST_TOKEN);

        Response<String> response = httpAgentSpy.fetch(SECURE_RESOURCE_ENDPOINT);
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseStatus.valueOf("success"), response.status());
        Assert.assertEquals(FETCH_DATA_REQUEST_SERVER_RESPONSE, response.payload());

        PowerMockito.verifyStatic(AccountHelper.class);
        AccountHelper.invalidateAuthToken(accountAuthenticatorXml.getAccountType(), SAMPLE_TEST_TOKEN);

    }

    @Test
    public void testPostInvokesInvalidateCacheIfUnauthorizedOnFirstAttempt() throws Exception {

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(SECURE_RESOURCE_ENDPOINT);
        Mockito.doReturn(errorStream).when(httpURLConnection).getErrorStream();
        Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpURLConnection).getResponseCode();

        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toString(errorStream)).thenReturn(FETCH_DATA_REQUEST_SERVER_RESPONSE);

        PowerMockito.mockStatic(AccountHelper.class);
        PowerMockito.when(AccountHelper.getCachedOAuthToken(accountAuthenticatorXml.getAccountType(), AccountHelper.TOKEN_TYPE.PROVIDER)).thenReturn(SAMPLE_TEST_TOKEN);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).generatePostRequest(SECURE_RESOURCE_ENDPOINT, SAMPLE_POST_REQUEST_PAYLOAD);

        httpAgentSpy.post(SECURE_RESOURCE_ENDPOINT, SAMPLE_POST_REQUEST_PAYLOAD);

        Mockito.verify(httpAgentSpy).invalidateExpiredCachedAccessToken();

    }

    @Test
    public void testFetchWithCredentialsInvokesInvalidateCacheIfUnauthorizedOnFirstAttempt() throws Exception {

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(SECURE_RESOURCE_ENDPOINT);
        Mockito.doReturn(errorStream).when(httpURLConnection).getErrorStream();
        Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpURLConnection).getResponseCode();

        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toString(errorStream)).thenReturn(FETCH_DATA_REQUEST_SERVER_RESPONSE);

        PowerMockito.mockStatic(AccountHelper.class);
        PowerMockito.when(AccountHelper.getCachedOAuthToken(accountAuthenticatorXml.getAccountType(), AccountHelper.TOKEN_TYPE.PROVIDER)).thenReturn(SAMPLE_TEST_TOKEN);

        Response<String> response = httpAgentSpy.fetchWithCredentials(SECURE_RESOURCE_ENDPOINT, SAMPLE_TEST_TOKEN);
        Assert.assertNotNull(response);

        PowerMockito.verifyStatic(AccountHelper.class);
        AccountHelper.invalidateAuthToken(accountAuthenticatorXml.getAccountType(), SAMPLE_TEST_TOKEN);

    }

    @Test
    public void testOauth2authenticateRefreshTokenInvokesOauth2authenticateCoreWithCorrectParams() throws Exception {

        Mockito.doReturn(sharedPreferences).when(allSharedPreferences).getPreferences();
        Mockito.doReturn(TEST_TOKEN_ENDPOINT).when(sharedPreferences).getString(AccountHelper.CONFIGURATION_CONSTANTS.TOKEN_ENDPOINT_URL, "");

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        AccountResponse accountResponse = Mockito.mock(AccountResponse.class);

        Mockito.doReturn(accountResponse).when(httpAgentSpy).oauth2authenticateCore(ArgumentMatchers.any(StringBuilder.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        ArgumentCaptor<StringBuilder> requestParamStringBuilder = ArgumentCaptor.forClass(StringBuilder.class);
        ArgumentCaptor<String> grantType = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> tokenEndPoint = ArgumentCaptor.forClass(String.class);

        httpAgentSpy.oauth2authenticateRefreshToken(SAMPLE_REFRESH_TOKEN);

        Mockito.verify(httpAgentSpy).oauth2authenticateCore(requestParamStringBuilder.capture(), grantType.capture(), tokenEndPoint.capture());

        String capturedRefreshTokenRequestValue = requestParamStringBuilder.getValue().toString();
        String capturedGrantTypeValue = grantType.getValue();
        String capturedTokenEndpointValue = tokenEndPoint.getValue();

        Assert.assertEquals("&refresh_token=" + SAMPLE_REFRESH_TOKEN, capturedRefreshTokenRequestValue);

        Assert.assertEquals(AccountHelper.OAUTH.GRANT_TYPE.REFRESH_TOKEN, capturedGrantTypeValue);
        Assert.assertEquals(TEST_TOKEN_ENDPOINT, capturedTokenEndpointValue);


    }

    @Test
    public void testFetchUserDetailsConstructsCorrectResponse() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);
        Mockito.doReturn(inputStream).when(httpsURLConnection).getInputStream();
        Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpsURLConnection).getResponseCode();

        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toString(inputStream)).thenReturn(LoginResponseTestData.USER_DETAILS_REQUEST_SERVER_RESPONSE);

        LoginResponse loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("Login successful.", loginResponse.message());
        Assert.assertNotNull(loginResponse.payload());

        Assert.assertNotNull(loginResponse.payload().user);
        Assert.assertEquals("demo", loginResponse.payload().user.getUsername());
        Assert.assertEquals("Demo User", loginResponse.payload().user.getPreferredName());
        Assert.assertEquals("93c6526-6667-3333-a611112-f3b309999999", loginResponse.payload().user.getBaseEntityId());

        Assert.assertNotNull(loginResponse.payload().time);
        Assert.assertEquals("2020-06-02 08:21:40", loginResponse.payload().time.getTime());
        Assert.assertEquals("Africa/Nairobi", loginResponse.payload().time.getTimeZone());

        Assert.assertNotNull(loginResponse.payload().locations);
        Assert.assertNotNull(loginResponse.payload().locations.getLocationsHierarchy());

        Assert.assertNotNull(loginResponse.payload().jurisdictions);
        Assert.assertEquals(1, loginResponse.payload().jurisdictions.size());
        Assert.assertNotNull("Health Team Kasarani", loginResponse.payload().jurisdictions.get(0));

        Assert.assertNotNull(loginResponse.payload().team);
        Assert.assertEquals("93c6526-6667-3333-a611112-f3b309999999", loginResponse.payload().team.identifier);
        Assert.assertEquals("93c6526-6667-3333-a611112-f3b309999999", loginResponse.payload().team.uuid);

        Assert.assertEquals("SUCCESS", loginResponse.name());

        ArgumentCaptor<String> headerKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValue = ArgumentCaptor.forClass(String.class);

        Mockito.verify(httpsURLConnection).setRequestProperty(headerKey.capture(), headerValue.capture());
        String capturedKey = headerKey.getValue();
        String capturedValue = headerValue.getValue();

        Assert.assertEquals(capturedKey, AllConstants.HTTP_REQUEST_HEADERS.AUTHORIZATION);
        Assert.assertEquals(capturedValue, AllConstants.HTTP_REQUEST_AUTH_TOKEN_TYPE.BEARER + " " + SAMPLE_TEST_TOKEN);

    }

    @Test
    public void testFetchUserDetailsConstructsCorrectResponseForUnauthorizedRequests() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);
        Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpsURLConnection).getResponseCode();

        LoginResponse loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("Please check the credentials", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("UNAUTHORIZED", loginResponse.name());

    }

    @Test
    public void testFetchUserDetailsConstructsCorrectResponseForRandomServerError() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

        Mockito.doReturn(errorStream).when(httpsURLConnection).getInputStream();
        Mockito.doReturn(HttpURLConnection.HTTP_INTERNAL_ERROR).when(httpsURLConnection).getResponseCode();

        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toString(errorStream)).thenReturn("<html><p><b>message</b> Oops, something went wrong </u></p></html>");

        LoginResponse loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("Dristhi login failed. Try later", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("UNKNOWN_RESPONSE", loginResponse.name());

    }


    @Test
    public void testFetchUserDetailsConstructsCorrectResponseForMalformedURLRequests() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);
        Mockito.doThrow(new MalformedURLException()).when(httpsURLConnection).getResponseCode();

        LoginResponse loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("Incorrect url", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("MALFORMED_URL", loginResponse.name());

    }

    @Test
    public void testFetchUserDetailsConstructsCorrectResponseForConnectionTimedOutRequests() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);
        Mockito.doThrow(new SocketTimeoutException()).when(httpsURLConnection).getResponseCode();

        LoginResponse loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("The server could not be reached. Try again", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("TIMEOUT", loginResponse.name());

    }


    @Test
    public void testFetchUserDetailsConstructsCorrectResponseForRequestsWithNetworkConnectivity() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);
        Mockito.doThrow(new IOException()).when(httpsURLConnection).getResponseCode();

        LoginResponse loginResponse = httpAgentSpy.fetchUserDetails(USER_DETAILS_ENDPOINT, SAMPLE_TEST_TOKEN);

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("No internet connection. Please ensure data connectivity", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("NO_INTERNET_CONNECTIVITY", loginResponse.name());

    }


    @Test
    public void testVerifyAuthorizationReturnsTrueForAuthorizedResponse() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(KEYClOAK_CONFIGURATION_ENDPOINT);

        Mockito.doReturn(HttpURLConnection.HTTP_OK).when(httpURLConnection).getResponseCode();

        boolean isVerified = httpAgentSpy.verifyAuthorization();
        Assert.assertTrue(isVerified);

    }

    @Test
    public void testVerifyAuthorizationReturnsFalseForUnauthorizedResponse() throws Exception {

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
        Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection("https://my-server.com/user-details?anm-id=" + TEST_USERNAME);

        Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpURLConnection).getResponseCode();

        boolean isVerified = httpAgentSpy.verifyAuthorization();
        Assert.assertFalse(isVerified);

    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsReturnsUnauthorizedResponse() throws Exception {

        PowerMockito.mockStatic(Base64.class);

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
        Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

        Mockito.doReturn(HttpURLConnection.HTTP_UNAUTHORIZED).when(httpURLConnection).getResponseCode();

        LoginResponse response = httpAgentSpy.urlCanBeAccessWithGivenCredentials(USER_DETAILS_ENDPOINT, TEST_USERNAME, TEST_PASSWORD);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.message());
        Assert.assertNull(response.payload());
        Assert.assertEquals("Please check the credentials", response.message());

    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsReturnsErrorResponseForMalformedURL() throws Exception {

        PowerMockito.mockStatic(Base64.class);

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
        Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

        Mockito.doThrow(new MalformedURLException()).when(httpURLConnection).getResponseCode();

        LoginResponse response = httpAgentSpy.urlCanBeAccessWithGivenCredentials(USER_DETAILS_ENDPOINT, TEST_USERNAME, TEST_PASSWORD);
        Assert.assertNotNull(response);
        Assert.assertNull(response.payload());
        Assert.assertNotNull(response.message());
        Assert.assertEquals(LoginResponse.MALFORMED_URL.name(), response.name());

    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsReturnsCorrectErrorResponseForSocketTimeout() throws Exception {

        PowerMockito.mockStatic(Base64.class);

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
        Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

        Mockito.doThrow(new MalformedURLException()).when(httpURLConnection).getResponseCode();


        LoginResponse response = httpAgentSpy.urlCanBeAccessWithGivenCredentials(USER_DETAILS_ENDPOINT, TEST_USERNAME, TEST_PASSWORD);
        Assert.assertNotNull(response);
        Assert.assertNull(response.payload());
        Assert.assertNotNull(response.message());
        Assert.assertEquals(LoginResponse.MALFORMED_URL.name(), response.name());

    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsReturnsCorrectErrorResponseErrorResponseForIOException() throws Exception {

        PowerMockito.mockStatic(Base64.class);

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(TEST_BASE_URL).when(dristhiConfiguration).dristhiBaseURL();
        Mockito.doReturn(TEST_USERNAME).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(httpURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

        Mockito.doThrow(new IOException()).when(httpURLConnection).getResponseCode();


        LoginResponse response = httpAgentSpy.urlCanBeAccessWithGivenCredentials(USER_DETAILS_ENDPOINT, TEST_USERNAME, TEST_PASSWORD);
        Assert.assertNotNull(response);
        Assert.assertNull(response.payload());
        Assert.assertNotNull(response.message());
        Assert.assertEquals(LoginResponse.NO_INTERNET_CONNECTIVITY.name(), response.name());

    }

    @Test
    public void testUrlCanBeAccessWithGivenCredentialsReturnsCorrectResponseForRandomServerError() throws Exception {

        PowerMockito.mockStatic(Base64.class);

        URL url = PowerMockito.mock(URL.class);
        Assert.assertNotNull(url);

        HTTPAgent httpAgentSpy = Mockito.spy(httpAgent);

        Mockito.doReturn(httpsURLConnection).when(httpAgentSpy).getHttpURLConnection(USER_DETAILS_ENDPOINT);

        Mockito.doReturn(errorStream).when(httpsURLConnection).getErrorStream();
        Mockito.doReturn(HttpURLConnection.HTTP_INTERNAL_ERROR).when(httpsURLConnection).getResponseCode();

        PowerMockito.mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toString(errorStream)).thenReturn("<html><p><b>message</b> Oops, something went wrong </u></p></html>");

        LoginResponse loginResponse = httpAgentSpy.urlCanBeAccessWithGivenCredentials(USER_DETAILS_ENDPOINT, TEST_USERNAME, TEST_PASSWORD);

        Assert.assertNotNull(loginResponse);
        Assert.assertNotNull(loginResponse.message());
        Assert.assertEquals("Oops, something went wrong", loginResponse.message());
        Assert.assertNull(loginResponse.payload());

        Assert.assertEquals("CUSTOM_SERVER_RESPONSE", loginResponse.name());

    }
}
