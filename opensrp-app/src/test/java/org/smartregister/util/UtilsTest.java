package org.smartregister.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TableRow;

import androidx.test.core.app.ApplicationProvider;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncFilter;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.domain.jsonmapping.User;
import org.smartregister.domain.jsonmapping.util.Team;
import org.smartregister.domain.jsonmapping.util.TeamLocation;
import org.smartregister.domain.jsonmapping.util.TeamMember;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smartregister.TestUtils.getContext;
import static org.smartregister.util.Utils.getDefaultLocale;
import static org.smartregister.util.Utils.getUserDefaultTeamId;

/**
 * Created by kaderchowdhury on 12/11/17.
 */
public class UtilsTest extends BaseRobolectricUnitTest {

    @Before
    public void setUp() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()));
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSharedPreferences", allSharedPreferences);
    }

    @Test
    public void assertConvertDateFormatTestReturnsDate() throws Exception {
        assertEquals("20-10-2017", Utils.convertDateFormat("2017-10-20", true));
        assertEquals("", Utils.convertDateFormat("20171020", true));
    }

    @Test
    public void assertToDateReturnsDate() throws Exception {
        SimpleDateFormat DB_DF = new SimpleDateFormat("yyyy-MM-dd");
        Date date = DB_DF.parse("2017-10-20");
        org.junit.Assert.assertNotNull(Utils.toDate("2017-10-20", true));
        assertNull(Utils.toDate("20171020", true));
        assertEquals(date, Utils.toDate("2017-10-20", true));
    }

    @Test
    public void assertConvertDateFormat() throws Exception {
        assertEquals("20-10-2017", Utils.convertDateFormat("2017-10-20", "abcdxyz", true));
        assertEquals("abcdxyz", Utils.convertDateFormat("20171020", "abcdxyz", true));
        assertEquals("", Utils.convertDateFormat("20171020", "", true));
    }

    @Test
    public void assertConvertDateFormatReturnsDate() throws Exception {
        DateTime dateTime = new DateTime(0l);
        assertEquals("01-01-1970", Utils.convertDateFormat(dateTime));
    }

    @Test
    public void assertConvertDateTimeFormatReturnsDate() throws Exception {
        assertEquals("24-07-1985 00:00:00", Utils.convertDateTimeFormat("1985-07-24T00:00:00.000Z", true));
//      assertEquals("", Utils.convertDateTimeFormat("19850724", true));
    }

    @Test(expected = RuntimeException.class)
    public void assertConvertDateTimeFormatThrowsExceptionIfUnpasrsableDate() {
        Utils.convertDateTimeFormat("1985-07-24XXXYYY", false);
    }

    @Test
    public void assertFillValueFromHashMapReturnsValue() {
        android.widget.TextView view = new android.widget.TextView(RuntimeEnvironment.application);
        HashMap<String, String> map = new HashMap<String, String>();
        String field = "field";
        map.put(field, "2017-10-20");
        Utils.fillValue(view, map, field, true);
        assertEquals("2017-10-20", view.getText());
        map.put(field, "");
        Utils.fillValue(view, map, field, "default", true);
        assertEquals("default", view.getText());
    }

    @Test
    public void assertFillValueFromCommonPersonObjectClientReturnsValue() {
        android.widget.TextView view = new android.widget.TextView(RuntimeEnvironment.application);

        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        CommonPersonObjectClient cm = new CommonPersonObjectClient("", map, "NAME");
        Utils.fillValue(view, cm, field, true);
        assertEquals("2017-10-20", view.getText());
        map.put(field, "");
        cm.setDetails(map);
        Utils.fillValue(view, cm, field, "default", true);
        assertEquals("default", view.getText());
    }

    @Test
    public void assertFillValueTextFieldReturnsValue() throws Exception {
        android.widget.TextView view = new android.widget.TextView(RuntimeEnvironment.application);
        String value = "value";
        view.setText(value);
        Utils.fillValue(view, value);
        assertEquals(value, view.getText());
    }

    @Test
    public void assertFormatValueReturnsValue() {
        assertEquals("", Utils.formatValue(null, true));
        assertEquals("Abc-def", Utils.formatValue("abc-def", true));
        assertEquals("abc-def", Utils.formatValue("abc-def", false));
    }

    @Test
    public void assertFormatValueObjectReturnsValue() {
        assertEquals("", Utils.formatValue((Object) null, true));
        assertEquals("Abc-def", Utils.formatValue((Object) new String("abc-def"), true));
        assertEquals("abc-def", Utils.formatValue((Object) new String("abc-def"), false));
    }

    @Test
    public void assertGetValueReturnsValue() {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        CommonPersonObjectClient cm = new CommonPersonObjectClient("", map, "NAME");
        assertEquals(Utils.getValue(cm, field, "default", true), "2017-10-20");
        map.put(field, "");
        assertEquals(Utils.getValue(cm, field, "default", true), "default");
    }

    @Test
    public void assertGetValueMapReturnsValue() {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        assertEquals(Utils.getValue(map, field, "default", true), "2017-10-20");
        map.put(field, "");
        assertEquals(Utils.getValue(map, field, "default", true), "default");
    }

    @Test
    public void assertNotEmptyValueReturnsValue() {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "2017-10-20");
        assertEquals(Utils.nonEmptyValue(map, false, true, new String[]{field}), "2017-10-20");
        map.put(field, "");
        assertEquals(Utils.nonEmptyValue(map, false, true, new String[]{field}), "");
    }

    @Test
    public void assertHasEmptyValueReturnsBoolean() throws Exception {
        String field = "field";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(field, "");
        assertEquals(Utils.hasAnyEmptyValue(map, "", new String[]{field}), true);
        map.put(field, "2017-10-20");
        assertEquals(Utils.hasAnyEmptyValue(map, "x", new String[]{field}), false);
    }

    @Test
    public void assertAddToIntReturnsSum() {
        assertEquals(Utils.addAsInts(true, new String[]{""}), 0);
        assertEquals(Utils.addAsInts(true, new String[]{"1", "1", "1"}), 3);
    }

    @Test
    public void assertAddToRowReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "hello world", mockRow);
        assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void assertAddToRowWeihtReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "hello world", mockRow, 25);
        assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void assertAddToRowcompatReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "hello world", mockRow, true);
        assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void assertAddToRowWeightCompatReturnsTableRow() {
        TableRow mockRow = new TableRow(RuntimeEnvironment.application);
        TableRow row = Utils.addToRow(RuntimeEnvironment.application, "<b>hello world</b>", mockRow, true, 25);
        assertEquals(mockRow, row);
        android.widget.TextView view = (android.widget.TextView) row.getChildAt(0);
        assertEquals(view.getText().toString(), "hello world");
    }

    @Test
    public void testCompleteSyncIntent() throws Exception {
        Intent intent = new Intent();
        assertEquals(intent.getExtras(), null);
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, FetchStatus.fetched);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, true);

        Intent UtilIntent = Utils.completeSync(FetchStatus.fetched);

        Assert.assertSame(intent.getExtras().get("complete_status"), UtilIntent.getExtras().get("complete_status"));
        Assert.assertSame(intent.getExtras().get("fetch_status"), UtilIntent.getExtras().get("fetch_status"));
        assertEquals(FetchStatus.fetched, UtilIntent.getExtras().get("fetch_status"));
        Assert.assertSame(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS, UtilIntent.getAction());
        Assert.assertSame(0, Utils.completeSync(FetchStatus.fetched).getFlags());

    }

    @Test
    public void testKgStringSuffixReturnsCorrectValueIfParamFloat() {

        String result = Utils.kgStringSuffix(2.5f);
        Assert.assertNotNull(result);
        assertEquals("2.5 kg", result);
    }

    @Test
    public void testKgStringSuffixReturnsCorrectValueIfParamString() {

        String result = Utils.kgStringSuffix("3.4");
        Assert.assertNotNull(result);
        assertEquals("3.4 kg", result);
    }

    @Test
    public void testCmStringSuffixReturnsCorrectValueIfParamFloat() {

        String result = Utils.cmStringSuffix(1.5f);
        Assert.assertNotNull(result);
        assertEquals("1.5 cm", result);
    }

    @Test
    public void testCmStringSuffixReturnsCorrectValueIfParamString() {

        String result = Utils.cmStringSuffix("45.9");
        Assert.assertNotNull(result);
        assertEquals("45.9 cm", result);
    }


    @Test
    public void testConvertConvertsCommonPersonObjectToCommonPersonObjectClient() {

        Map<String, String> map = ImmutableMap.of("first_name", "Martin", "last_name", "Bull", "dob", "10-01-1970", "phone_number", "07246738839", "gender", "Male");
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("dfh45453483-34dfd893-394343cds3", map, "Marchello");

        Assert.assertNotNull(commonPersonObjectClient);
        Assert.assertNotNull(commonPersonObjectClient.getCaseId());
        Assert.assertNotNull(commonPersonObjectClient.getDetails());
        assertEquals(commonPersonObjectClient.getDetails().get("first_name"), "Martin");
    }

    @Test
    public void testGetVersionCodeShouldGetCorrectVersionCode() throws PackageManager.NameNotFoundException {
        Context context = getContext(40);
        assertEquals(Utils.getVersionCode(context), 40);
    }

    @Test
    public void testIs2xxSuccessfulShouldReturnCorrectStatus() {
        assertTrue(Utils.is2xxSuccessful(200));
        assertTrue(Utils.is2xxSuccessful(203));
        assertTrue(Utils.is2xxSuccessful(207));
        assertFalse(Utils.is2xxSuccessful(300));
    }

    @Test
    public void testDobStringToDateTimeShouldReturnCorrectDateTime() {
        assertEquals(Utils.dobStringToDateTime("2000-12-30").toString(), new DateTime("2000-12-30").toString());
    }

    @Test
    public void testDobStringToDateShouldReturnCorrectDate() {
        assertEquals(Utils.dobStringToDate("2000-12-30").toString(), Utils.dobStringToDateTime("2000-12-30").toDate().toString());
    }

    @Test
    public void testGetFilterValueShouldGetCorrectFilterValue() {
        LoginResponseData loginResponseData = getLoginResponseData();
        LoginResponse loginResponse = LoginResponse.SUCCESS.withPayload(loginResponseData);
        assertEquals(loginResponseData.team.team.uuid, Utils.getFilterValue(loginResponse, SyncFilter.TEAM));
        assertEquals(loginResponseData.team.team.uuid, Utils.getFilterValue(loginResponse, SyncFilter.TEAM_ID));
        assertEquals(loginResponseData.team.team.location.uuid, Utils.getFilterValue(loginResponse, SyncFilter.LOCATION));
        assertEquals(loginResponseData.user.getUsername(), Utils.getFilterValue(loginResponse, SyncFilter.PROVIDER));
    }

    @Test
    @Ignore
    public void testGetPropertiesShouldGetPropertyFile() {
        AppProperties appProperties = Utils.getProperties(RuntimeEnvironment.application);
        assertTrue(appProperties.getPropertyBoolean("property_4"));
        assertEquals("property_1", appProperties.getProperty("property_1"));
        assertEquals("property_2", appProperties.getProperty("property_2"));
        assertEquals("property_3", appProperties.getProperty("property_3"));
    }

    @Test
    public void testGetUserDefaultTeamIdShouldGetCorrectTeamId() {
        LoginResponseData loginResponseData = getLoginResponseData();
        assertEquals(loginResponseData.team.team.uuid, Utils.getUserDefaultTeamId(loginResponseData));
    }

    @Test
    public void testIsEmptyCollectionShouldReturnCorrectStatus() {
        List<String> list = new ArrayList<>();
        list.add("string");
        assertFalse(Utils.isEmptyCollection(list));
        list.clear();
        assertTrue(Utils.isEmptyCollection(list));
    }

    @Test
    public void testIsEmptyMapShouldReturnCorrectStatus() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "string");
        assertFalse(Utils.isEmptyMap(map));
        map.clear();
        assertTrue(Utils.isEmptyMap(map));
    }

    @Test
    public void testGetNameShouldGetCorrectlyFormattedName() {
        assertEquals("John Doe", Utils.getName("John", "Doe"));
        assertEquals("John", Utils.getName("John", ""));
        assertEquals("Doe", Utils.getName("", "Doe"));
    }

    private LoginResponseData getLoginResponseData() {
        Team team = new Team();
        team.uuid = "team_uuid";

        TeamLocation teamLocation = new TeamLocation();
        teamLocation.uuid = "location_uuid";

        TeamMember teamMember = new TeamMember();
        teamMember.team = team;
        teamMember.team.location = teamLocation;
        teamMember.team = team;

        User user = new User();
        user.setUsername("user_name");

        LoginResponseData loginResponseData = new LoginResponseData();
        loginResponseData.team = teamMember;
        loginResponseData.user = user;

        return loginResponseData;
    }

    @Test
    public void testGetAgeFromDateShouldGetCorrectAge() {
        String date = "2000-12-12";
        DateTime dateTime = DateTime.parse(date);
        assertEquals(Utils.getAgeFromDate(date), Years.yearsBetween(dateTime.toLocalDate(), LocalDate.now()).getYears());
    }

    @Test
    public void testGetDobShouldGetCorrectDob() {
        int age = 10;
        String datePattern = "YYYY-MM-dd";
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -age);
        assertEquals(cal.getWeekYear() + "-" + "01" + "-" + "01", Utils.getDob(age, datePattern));
        assertEquals("01" + "-" + "01" + "-" + cal.getWeekYear(), Utils.getDob(age));
    }

    @Test
    public void testGetAllSharedPreferencesShouldGetPreferences() {
        assertNotNull(Utils.getAllSharedPreferences());
    }

    @Test
    public void testGetPrefferedNameShouldGetCorrectPreferredName() {
        Utils.getAllSharedPreferences().updateANMUserName("provider");
        Utils.getAllSharedPreferences().updateANMPreferredName("provider", "prov");
        assertEquals("prov", Utils.getPrefferedName());
    }

    @Test
    public void testGetBuildDateShouldGetCorrectBuildDate() {
        Date date = new Date(CoreLibrary.getBuildTimeStamp());
        assertEquals(new SimpleDateFormat("dd MMM yyyy", getDefaultLocale()).format(date), Utils.getBuildDate(true));
        assertEquals(new SimpleDateFormat("dd MMMM yyyy", getDefaultLocale()).format(date), Utils.getBuildDate(false));
    }

    @Test
    public void testGetUserInitialsShouldGetCorrectInitials() {
        assertEquals("Me", Utils.getUserInitials());
        Utils.getAllSharedPreferences().updateANMUserName("provider");
        Utils.getAllSharedPreferences().updateANMPreferredName("provider", "provider name");
        assertEquals("pn", Utils.getUserInitials());
        Utils.getAllSharedPreferences().updateANMPreferredName("provider", "provider");
        assertEquals("p", Utils.getUserInitials());
    }

    @Test
    public void testGetAppIdShouldReturnAppId() {
        assertEquals("org.smartregister.test", Utils.getAppId(RuntimeEnvironment.application));
    }

    @Test
    public void testGetAppVersionShouldReturnAppVersion() {
        assertNull(Utils.getAppVersion(RuntimeEnvironment.application));
    }

    @Test
    public void testLogoutUserShouldInvokeRequiredMethods() {
        org.smartregister.Context opensrpContext = Mockito.mock(org.smartregister.Context.class);
        Context context = spy(RuntimeEnvironment.application);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        Mockito.doReturn("string").when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();

        Mockito.doReturn(context).when(opensrpContext).applicationContext();
        UserService mockUserService = Mockito.mock(UserService.class);
        Mockito.doReturn(mockUserService).when(opensrpContext).userService();

        Utils.logoutUser(opensrpContext, "logged out");
        verify(mockUserService, times(1)).forceRemoteLogin(anyString());
        verify(mockUserService, times(1)).logoutSession();
        verify(context, times(1)).startActivity(any(Intent.class));
    }

    @Test
    public void testHideKeyboardShouldInvokeRequireMethods() {
        Activity activity = Mockito.mock(Activity.class);

        View view = Mockito.mock(View.class);

        Mockito.doReturn(view).when(activity).getCurrentFocus();

        InputMethodManager keyboard = Mockito.mock(InputMethodManager.class);

        Mockito.doReturn(keyboard).when(activity).getSystemService(Context.INPUT_METHOD_SERVICE);

        Utils.hideKeyboard(activity);

        verify(keyboard, only()).hideSoftInputFromWindow(isNull(), eq(0));
    }

    @Test
    public void addToList() {
        String locationTag = "County";
        HashMap<String, String> locations = new HashMap<>();

        LinkedHashMap<String, TreeNode<String, Location>> locationMap = new LinkedHashMap<>();
        Location countryLocation = new Location("location-id-1", "Kenya", null, null);
        countryLocation.addTag("Country");

        Location countyLocation = new Location("location-id-2", "Nairobi", null, countryLocation);
        countyLocation.addTag(locationTag);

        TreeNode<String, Location> childTreeNode = new TreeNode<String, Location>("the-id-2", "Kk", countyLocation, null);
        LinkedHashMap<String, TreeNode<String, Location>> childLocationMap = new LinkedHashMap<>();
        childLocationMap.put(childTreeNode.getId(), childTreeNode);

        TreeNode<String, Location> countryTreeNode = new TreeNode<String, Location>("the-id", "Kenya", countryLocation, null, childLocationMap);
        locationMap.put(countryTreeNode.getId(), countryTreeNode);

        // call the method being tested
        Utils.addToList(locations, locationMap, locationTag);

        assertEquals("Nairobi", locations.get(locationTag));
    }

    @Test
    public void isConnectedToNetworkShouldReturnFalseWhenActiveNetworkInfoIsNotAvailable() {
        Context context = spy(RuntimeEnvironment.application);

        NetworkInfo networkInfo = mock(NetworkInfo.class);
        doReturn(false).when(networkInfo).isConnected();

        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        doReturn(connectivityManager).when(context).getSystemService(Context.CONNECTIVITY_SERVICE);
        doReturn(networkInfo).when(connectivityManager).getActiveNetworkInfo();

        // Call the method under test and assert false
        assertFalse(Utils.isConnectedToNetwork(context));
    }

    @Test
    public void isConnectedToNetworkShouldReturnTrueWhenActiveNetworkInfoIsNotAvailable() {
        Context context = spy(RuntimeEnvironment.application);

        NetworkInfo networkInfo = mock(NetworkInfo.class);
        doReturn(true).when(networkInfo).isConnected();

        ConnectivityManager connectivityManager = mock(ConnectivityManager.class);
        doReturn(connectivityManager).when(context).getSystemService(Context.CONNECTIVITY_SERVICE);
        doReturn(networkInfo).when(connectivityManager).getActiveNetworkInfo();

        // Call the method under test and assert false
        assertTrue(Utils.isConnectedToNetwork(context));
    }

    @Test
    public void getLongDateAwareGson() {
        long timeNow = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("iMillis", new JsonPrimitive(timeNow));
        Gson gson = Utils.getLongDateAwareGson();

        assertNotNull(gson);
        assertEquals(new DateTime(timeNow), gson.fromJson(jsonObject.toString(), DateTime.class));
    }

    @Test
    public void readAssetContents() {
        String contents = Utils.readAssetContents(RuntimeEnvironment.application, "test_file.txt");

        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation"
                , contents);
    }

    @Test
    public void startAsyncTaskShouldExecuteAsyncTaskOnThreadPoolExecutor() {
        AsyncTask<String, ?, ?> asyncTask = mock(AsyncTask.class);

        Utils.startAsyncTask(asyncTask, null);

        verify(asyncTask).executeOnExecutor(eq(AsyncTask.THREAD_POOL_EXECUTOR)
                , any(String[].class));
    }

    @Test
    public void dobToDateTime() {
        long expectedDobTime = System.currentTimeMillis();

        CommonPersonObjectClient client = new CommonPersonObjectClient("case-id", null, "John Doe");
        HashMap<String, String> columnMaps = new HashMap<>();
        client.setColumnmaps(columnMaps);

        columnMaps.put("dob", new DateTime(expectedDobTime).toString());

        DateTime actualDob = Utils.dobToDateTime(client);
        assertEquals(expectedDobTime, actualDob.getMillis());
    }

    @Test
    public void cleanUpHeader() {
        assertEquals("Full name"
                , ReflectionHelpers.callStaticMethod(Utils.class, "cleanUpHeader"
                        , ReflectionHelpers.ClassParameter.from(String.class, "\"Full name\"")));
    }

    @Test
    public void convert() {
        String name = "John";
        String caseId = "case-id-john-doe";
        String relationId = "relational-id";
        HashMap<String, String> details = new HashMap<>();
        details.put("first_name", name);

        CommonPersonObject client = new CommonPersonObject(caseId, relationId, details, null);
        client.setColumnmaps(details);

        CommonPersonObjectClient actualClient = Utils.convert(client);

        assertEquals(name, actualClient.getName());
        assertEquals(details.size(), actualClient.getDetails().size());
        assertEquals(client.getCaseId(), actualClient.getCaseId());
        assertEquals(details.size(), actualClient.getColumnmaps().size());
    }

    @Test
    public void getUserDefaultTeamIdShouldReturnNullWhenUserInfoDetailsAreNull() {
        assertNull(getUserDefaultTeamId(null));

        LoginResponseData loginData = new LoginResponseData();
        assertNull(getUserDefaultTeamId(loginData));

        loginData.team =  new TeamMember();
        assertNull(getUserDefaultTeamId(loginData));
    }

    @Ignore
    @Test
    public void getDurationShouldReturnValidDurationString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        Calendar calendar = Calendar.getInstance();
        calendar.add(1, Calendar.DAY_OF_MONTH);

        assertEquals("1d", Utils.getDuration(dateFormat.format(calendar.getTime())));

        calendar.add(5, Calendar.WEEK_OF_YEAR);
        assertEquals("5w 1d", Utils.getDuration(calendar.getTime().toString()));

        calendar.add(-1, Calendar.DAY_OF_MONTH);
        assertEquals("5w", Utils.getDuration(calendar.getTime().toString()));
    }


    @Test
    public void getDurationShouldReturnEmptyString() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(1, Calendar.DAY_OF_MONTH);

        assertEquals("", Utils.getDuration(""));
    }

    @Test
    public void dobStringToDateTimeShouldHandleNull() {
        assertNull(Utils.dobStringToDateTime(null));
    }

    @Test
    public void dobStringToDateTimeShouldHandleInvalidDateTimeStrings() {
        assertNull(Utils.dobStringToDateTime("opensrp"));
    }

    @Test
    public void getPropertiesShouldLoadPropertiesInPropertiesFile() {
        AppProperties appProperties = Utils.getProperties(RuntimeEnvironment.application);

        assertEquals(5, appProperties.size());
        assertEquals("", appProperties.getProperty("DRISHTI_BASE_URL"));
        assertEquals("false", appProperties.getProperty("SHOULD_VERIFY_CERTIFICATE"));
        assertEquals("false", appProperties.getProperty("system.toaster.centered"));
        assertEquals("10", appProperties.getProperty("SYNC_DOWNLOAD_BATCH_SIZE"));
    }

    @Test
    public void safeArrayToString() {
        assertEquals("OpenSRP", Utils.safeArrayToString(new char[]{'O', 'p', 'e', 'n', 'S', 'R', 'P'}));
    }

    @Test
    public void testComposeApiCallParamsStringWithNullValues() {
        assertEquals("", Utils.composeApiCallParamsString(null));
    }

    @Test
    public void testComposeApiCallParamsStringWithSingleParamValue() {
        List <Pair<String,String>> apiParams = Collections.singletonList(Pair.create("identifier", "global_configs"));
        assertEquals("&identifier=global_configs", Utils.composeApiCallParamsString(apiParams));
    }

    @Test
    public void testComposeApiCallParamsStringWithMultipleParamValues() {
        List <Pair<String,String>> apiParams = new ArrayList<>();
        apiParams.add(Pair.create("identifier", "global_configs"));
        apiParams.add(Pair.create("serverVersion", "21"));
        assertEquals("&identifier=global_configs&serverVersion=21", Utils.composeApiCallParamsString(apiParams));
    }
}

