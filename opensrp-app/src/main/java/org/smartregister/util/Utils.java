/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartregister.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncFilter;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.LoginResponse;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static org.smartregister.util.Log.logError;


/**
 * @author Maimoona
 * Class containing some static utility methods.
 */
public class Utils {
    private static final SimpleDateFormat UI_DF = new SimpleDateFormat("dd-MM-yyyy", Utils.getDefaultLocale());
    private static final SimpleDateFormat UI_DTF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Utils.getDefaultLocale());

    private static final SimpleDateFormat DB_DF = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static final SimpleDateFormat DB_DTF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private static String KG_FORMAT = "%s kg";
    private static String CM_FORMAT = "%s cm";
    public static final String APP_PROPERTIES_FILE = "app.properties";

    public static String convertDateFormat(String date, boolean suppressException) {
        try {
            return UI_DF.format(DB_DF.parse(date));
        } catch (ParseException e) {
            if (!suppressException) throw new RuntimeException(e);
        }
        return "";
    }

    public static Date toDate(String date, boolean suppressException) {
        try {
            return DB_DF.parse(date);
        } catch (ParseException e) {
            if (!suppressException) throw new RuntimeException(e);
        }
        return null;
    }

    public static String convertDateFormat(String date, String defaultV, boolean suppressException) {
        try {
            return UI_DF.format(DB_DF.parse(date));
        } catch (ParseException e) {
            if (!suppressException) throw new RuntimeException(e);
        }
        return StringUtils.isNotBlank(defaultV) ? defaultV : "";
    }

    public static String convertDateFormat(DateTime date) {
        return UI_DF.format(date.toDate());
    }

    public static String convertDateTimeFormat(String date, boolean suppressException) {
        try {
            return UI_DTF.format(DB_DTF.parse(date.replace("T", " ")));
        } catch (ParseException e) {
            e.printStackTrace();
            if (!suppressException) throw new RuntimeException(e);
        }
        return "";
    }

    public static void fillValue(TextView v, Map<String, String> cm, String field, boolean humanize) {
        v.setText(getValue(cm, field, humanize));
    }

    public static void fillValue(TextView v, Map<String, String> cm, String field, String defaultV, boolean humanize) {
        String val = getValue(cm, field, humanize);
        if (StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)) {
            val = defaultV;
        }
        v.setText(val);
    }

    public static void fillValue(TextView v, CommonPersonObjectClient pc, String field, boolean humanize) {
        v.setText(getValue(pc, field, humanize));
    }

    public static void fillValue(TextView v, CommonPersonObjectClient pc, String field, String defaultV, boolean humanize) {
        String val = getValue(pc, field, humanize);
        if (StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)) {
            val = defaultV;
        }
        v.setText(val);
    }

    public static void addToList(Map<String, String> locations, Map<String, TreeNode<String, Location>> locationMap,
                                 String locationTag) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {
            boolean tagFound = false;
            if (entry.getValue() != null) {
                Location l = entry.getValue().getNode();

                if (l.getTags() != null) {
                    for (String s : l.getTags()) {
                        if (s.equalsIgnoreCase(locationTag)) {
                            locations.put(locationTag, l.getName());
                            tagFound = true;
                        }
                    }
                }
            }
            if (!tagFound) {
                if (entry.getValue().getChildren() != null) {
                    addToList(locations, entry.getValue().getChildren(), locationTag);
                }
            }
        }
    }

    public static void fillValue(TextView v, String value) {
        v.setText(value);
    }

    public static String formatValue(String value, boolean humanize) {
        if (value == null) {
            value = "";
        }
        return humanize ? WordUtils.capitalize(StringUtil.humanize(value)) : value;
    }

    public static String formatValue(Object value, boolean humanize) {
        if (value == null) {
            value = "";
        }
        return humanize ? WordUtils.capitalize(StringUtil.humanize(value.toString())) : value.toString();
    }

    public static String getValue(CommonPersonObjectClient pc, String field, boolean humanize) {
        return formatValue(pc.getDetails().get(field), humanize);
    }

    public static String getValue(CommonPersonObjectClient pc, String field, String defaultV, boolean humanize) {
        String val = formatValue(pc.getDetails().get(field), humanize);
        if (StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)) {
            val = defaultV;
        }
        return val;
    }

    public static String getValue(Map<String, String> cm, String field, String defaultV, boolean humanize) {
        String val = formatValue(cm.get(field), humanize);
        if (StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)) {
            val = defaultV;
        }
        return val;
    }

    public static String getValue(Map<String, String> cm, String field, boolean humanize) {
        return formatValue(cm.get(field), humanize);
    }

    public static String nonEmptyValue(Map<String, String> cm, boolean asc, boolean humanize, String... fields) {
        List<String> l = Arrays.asList(fields);
        if (!asc) {
            Collections.reverse(l);
        }
        for (String f : l) {
            String v = getValue(cm, f, humanize);
            if (v != "") {
                return v;
            }
        }
        return "";
    }

    public static boolean hasAnyEmptyValue(Map<String, String> cm, String postFix, String... fields) {
        List<String> l = Arrays.asList(fields);
        for (String f : l) {
            String v = getValue(cm, f, false);
            if (v == "" && (StringUtils.isBlank(postFix) || StringUtils.isBlank(getValue(cm, f + postFix, false)))) {
                return true;
            }
        }
        return false;
    }


    public static int addAsInts(boolean ignoreEmpty, String... vals) {
        int i = 0;
        for (String v : vals) {
            i += ignoreEmpty && StringUtils.isBlank(v) ? 0 : Integer.parseInt(v);
        }
        return i;
    }

    public static TableRow addToRow(Context context, String value, TableRow row) {
        return addToRow(context, value, row, false, 1);
    }

    public static TableRow addToRow(Context context, String value, TableRow row, int weight) {
        return addToRow(context, value, row, false, weight);
    }

    public static TableRow addToRow(Context context, String value, TableRow row, boolean compact) {
        return addToRow(context, value, row, compact, 1);
    }

    public static TableRow addToRow(Context context, String value, TableRow row, boolean compact, int weight) {
        return addToRow(context, Html.fromHtml(value), row, compact, weight);
    }

    public static TableRow addToRow(Context context, Spanned value, TableRow row, boolean compact, int weight) {
        TextView v = new TextView(context);
        v.setText(value);
        if (compact) {
            v.setPadding(15, 4, 1, 1);
        } else {
            v.setPadding(2, 15, 2, 15);
        }
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT, weight
        );
        params.setMargins(0, 0, 1, 0);
        v.setLayoutParams(params);
        v.setTextColor(Color.BLACK);
        v.setTextSize(14);
        v.setBackgroundColor(Color.WHITE);
        row.addView(v);

        return row;
    }

    public static Gson getLongDateAwareGson() {
        Gson g = new GsonBuilder().registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
            @Override
            public DateTime deserialize(JsonElement e, Type t, JsonDeserializationContext jd) throws JsonParseException {
                if (e.isJsonNull()) {
                    return null;
                } else if (e.isJsonObject()) {
                    JsonObject je = e.getAsJsonObject();
                    return new DateTime(je.get("iMillis").getAsLong());
                } else if (e.isJsonPrimitive()) {
                    return new DateTime(e.getAsString());
                } else return null;
            }

        }).create();
        return g;
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    public static String getName(@NonNull String firstName, @NonNull String lastName) {
        return (firstName.trim() + " " + lastName.trim()).trim();
    }

    public static String readAssetContents(Context context, String path) {
        String fileContents = null;
        try {
            InputStream is = context.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Timber.e(ex);
        }

        return fileContents;
    }

    public static InputStream getAssetFileInputStream(Context context, String path) {
        InputStream is = null;
        try {
            is = context.getAssets().open(path);

        } catch (IOException ex) {
            Timber.e(ex);
        }

        return is;
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static <T> void startAsyncTask(AsyncTask<T, ?, ?> asyncTask, T[] params) {
        if (params == null) {
            @SuppressWarnings("unchecked")
            T[] arr = (T[]) new Void[0];
            params = arr;
        }
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            asyncTask.execute(params);
        }
    }

    public static DateTime dobToDateTime(CommonPersonObjectClient childDetails) {
        DateTime birthDateTime = null;
        String dobString = Utils.getValue(childDetails.getColumnmaps(), "dob", false);
        if (!TextUtils.isEmpty(dobString)) {
            birthDateTime = new DateTime(dobString);
        }
        return birthDateTime;
    }

    /**
     * This method is only intended to be used for processing Zambia-EIR-DataDictionaryReporting-HIA2.csv
     *
     * @param csvFileName
     * @param columns     this map has the db column name as value and the csv column no as the key
     * @return each map is db row with key as the column name and value as the value from the csv file
     */
    public static List<Map<String, String>> populateTableFromCSV(Context context, String csvFileName,
                                                                 Map<Integer, String> columns) {
        List<Map<String, String>> result = new ArrayList<>();

        try {
            InputStream is = getAssetFileInputStream(context, csvFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            try {
                String line;
                String category = "";
                while ((line = reader.readLine()) != null) {
                    Map<String, String> csvValues = new HashMap<>();
                    String[] rowData = line.split(",");
                    if (!TextUtils.isDigitsOnly(rowData[0])) {
                        category = cleanUpHeader(line);
                        continue;
                    }
                    for (Integer key : columns.keySet()) {
                        if (key != 999) {
                            String value = rowData[key];
                            csvValues.put(columns.get(key), value);
                            csvValues.put(columns.get(999), category);
                        }
                    }
                    result.add(csvValues);
                }
            } catch (IOException e) {
                Timber.e(e, "populateTableFromCSV: error reading csv file ");

            } finally {
                try {
                    is.close();
                    reader.close();
                } catch (Exception e) {
                    Timber.e(e, "populateTableFromCSV: unable to close inputstream/bufferedreader ");
                }
            }

        } catch (Exception e) {
            Timber.e(e, "populateTableFromCSV ");
        }
        return result;
    }

    private static String cleanUpHeader(String header) {

        try {
            header = header.contains("\"") ? header.replaceAll("\"", "") : header;
            int length = header.length() - 1;
            for (int i = length; i > 0; i--) {
                if (header.charAt(i) != ',') {
                    header = header.substring(0, i + 1);
                    break;
                }
            }
            return header;
        } catch (Exception ex) {
            return header;
        }
    }

    public static String kgStringSuffix(Float weight) {
        return String.format(KG_FORMAT, weight);
    }

    public static String kgStringSuffix(String weight) {
        return String.format(KG_FORMAT, weight);
    }

    public static String cmStringSuffix(Float height) {
        return String.format(CM_FORMAT, height);
    }

    public static String cmStringSuffix(String height) {
        return String.format(CM_FORMAT, height);
    }

    public static CommonPersonObjectClient convert(CommonPersonObject commonPersonObject) {
        CommonPersonObjectClient pc = new CommonPersonObjectClient(commonPersonObject.getCaseId(),
                commonPersonObject.getDetails(), commonPersonObject.getColumnmaps().get("first_name"));
        pc.setColumnmaps(commonPersonObject.getColumnmaps());
        return pc;
    }

    public static boolean getBooleanProperty(String key) {

        return CoreLibrary.getInstance().context().getAppProperties().hasProperty(key) ? CoreLibrary.getInstance().context().getAppProperties().getPropertyBoolean(key) : false;

    }

    public static void showToast(Context context, String message) {
        showToastCore(context, message, Toast.LENGTH_LONG);
    }

    public static void showShortToast(Context context, String message) {
        showToastCore(context, message, Toast.LENGTH_SHORT);

    }

    public static void showToastCore(Context context, String message, int duration) {
        Toast toast = Toast.makeText(context, message, duration);

        if (getBooleanProperty(AllConstants.PROPERTY.SYSTEM_TOASTER_CENTERED)) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }

        toast.show();
    }

    public static void hideKeyboard(Context context, View view) {
        try {

            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            logError("Error encountered while hiding keyboard " + e);
        }
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            hideKeyboard(activity, view);
        }
    }

    public static boolean isEmptyCollection(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmptyMap(Map map) {
        return map == null || map.isEmpty();
    }

    public static String getVersion(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionName;
    }

    public static long getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionCode;
    }

    public static String getBuildDate(Boolean isShortMonth) {
        String simpleDateFormat;
        if (isShortMonth) {
            simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", getDefaultLocale()).format(new Date(CoreLibrary.getBuildTimeStamp()));
        } else {
            simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", getDefaultLocale()).format(new Date(CoreLibrary.getBuildTimeStamp()));
        }
        return simpleDateFormat;
    }

    public static String getUserDefaultTeamId(LoginResponseData userInfo) {
        try {
            if (userInfo != null && userInfo.team != null && userInfo.team.team != null) {
                return userInfo.team.team.uuid;
            }
        } catch (Exception e) {
            Log.v("Error : ", e.getMessage());
        }

        return null;
    }

    public static String getPrefferedName() {
        if (getAllSharedPreferences() == null) {
            return null;
        }

        return getAllSharedPreferences().getANMPreferredName(getAllSharedPreferences().fetchRegisteredANM());
    }

    public String getName() {
        return getPrefferedName();
    }

    public static String getUserInitials() {
        String initials = "Me";
        String preferredName = getPrefferedName();

        if (StringUtils.isNotBlank(preferredName)) {
            String[] preferredNameArray = preferredName.split(" ");
            initials = "";
            if (preferredNameArray.length > 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0)) + String.valueOf(preferredNameArray[1].charAt(0));
            } else if (preferredNameArray.length == 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0));
            }
        }
        return initials;
    }

    public static AllSharedPreferences getAllSharedPreferences() {
        return CoreLibrary.getInstance().context().allSharedPreferences();
    }

    public static String getDuration(String date) {
        DateTime duration;
        if (StringUtils.isNotBlank(date)) {
            try {
                duration = new DateTime(date);
                return DateUtil.getDuration(duration);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return "";
    }


    public static String getDob(int age) {
        return getDob(age, DateUtil.DATE_FORMAT_FOR_TIMELINE_EVENT);
    }

    public static String getDob(int age, String dateFormatPattern) {
        String pattern = dateFormatPattern;
        if (StringUtils.isBlank(dateFormatPattern)) {
            pattern = DateUtil.DATE_FORMAT_FOR_TIMELINE_EVENT;
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -age);
        cal.set(Calendar.DAY_OF_MONTH, 1);


        cal.set(Calendar.MONTH, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(cal.getTime());
    }

    public static int getAgeFromDate(String dateOfBirth) {
        DateTime date = DateTime.parse(dateOfBirth);
        Years age = Years.yearsBetween(date.toLocalDate(), LocalDate.now());
        return age.getYears();
    }

    public static Date dobStringToDate(String dobString) {
        DateTime dateTime = dobStringToDateTime(dobString);
        if (dateTime != null) {
            return dateTime.toDate();
        }
        return null;
    }

    public static DateTime dobStringToDateTime(String dobString) {
        try {
            if (StringUtils.isBlank(dobString)) {
                return null;
            }
            return new DateTime(dobString);

        } catch (Exception e) {
            return null;
        }
    }

    public static Intent completeSync(FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, true);
        return intent;
    }

    public static boolean is2xxSuccessful(int httpStatus) {
        return httpStatus >= HttpStatus.SC_OK && httpStatus <= HttpStatus.SC_MULTI_STATUS;
    }

    public static String getFilterValue(LoginResponse loginResponse, SyncFilter syncFilterParam) {
        String filterValue = "";
        LoginResponseData response = loginResponse.payload();
        switch (syncFilterParam) {

            case TEAM:
            case TEAM_ID:

                filterValue = response.team.team.uuid;
                break;

            case LOCATION:

                filterValue = response.team.team.location.uuid;
                break;
            case PROVIDER:

                filterValue = response.user.getUsername();
                break;


            default:
                break;
        }

        return filterValue;
    }

    public static AppProperties getProperties(Context context) {

        AppProperties properties = new AppProperties();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(APP_PROPERTIES_FILE);
            properties.load(inputStream);
        } catch (Exception e) {
            Timber.e(e);
        }
        return properties;

    }

    /**
     * This util does a look up from the strings file without specifying an android resource id , rather the identifier(key) of the resource
     *
     * @param key a string identifier that corresponds to a key in the strings.xml file e.g. for R.string.key key is the identifier
     * @return String value from resource file
     */
    public static String getTranslatedIdentifier(String key) {

        String myKey;
        try {
            myKey = CoreLibrary.getInstance().context().applicationContext().getString(CoreLibrary.getInstance().context().applicationContext().getResources().getIdentifier(key.toLowerCase(), "string", CoreLibrary.getInstance().context().applicationContext().getPackageName()));

        } catch (Resources.NotFoundException resourceNotFoundException) {
            myKey = key;
        }
        return myKey;
    }

    /**
     * copyDatabase function moves database file created by the app is the apps private folder to Downloads folder. From Downloads folder, it's easy to share
     *
     * @param dbName     name of the database that was created by the app e.g. drishti.db
     * @param copyDbName name of the database file once copied to Downloads folder e.g. reveal.db
     * @param context    application context when calling the function
     */
    public static void copyDatabase(String dbName, String copyDbName, Context context) {
        try {
            final String inFileName = context.getDatabasePath(dbName).getPath();
            final String outFileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + copyDbName;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            OutputStream output = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();

        } catch (Exception e) {
            Timber.e("copyDatabase: backup error " + e.toString());
        }
    }

    public static Locale getDefaultLocale() {
        return Locale.getDefault().toString().startsWith("ar") ? Locale.ENGLISH : Locale.getDefault();
    }

    public static boolean deleteRoomDb(@NonNull Context context, @NonNull String databaseName) {
        boolean operationSuccessful;
        File databasesFolder = new File(context.getApplicationInfo().dataDir + "/databases");
        File db = new File(databasesFolder, databaseName);
        if (!db.exists()) {
            Timber.i("Room database %s does not exist", databaseName);
            return false;
        }

        if (db.delete()) {
            Timber.i("Room database %s deleted", databaseName);
        } else {
            Timber.i("Failed to delete database %s", databaseName);
            return false;
        }

        // Delete the journal file
        operationSuccessful = deleteDbJournal(databasesFolder, databaseName);

        // Delete the db shm & wal file
        return operationSuccessful && deleteDbTemporaryFiles(databaseName, databasesFolder);
    }

    /**
     * Deletes the wal & shm temporary files for a DB
     *
     * @param databaseName
     * @param databasesFolder
     * @return
     */
    protected static boolean deleteDbTemporaryFiles(@NonNull String databaseName, File databasesFolder) {
        boolean operationSuccessful = true;
        File walFile = new File(databasesFolder, databaseName + "-wal");
        if (walFile.exists()) {
            if (walFile.delete()) {
                Timber.i("Database %s-wal deleted", databaseName);
            } else {
                Timber.e("Failed to delete database %s-wal", databaseName);
                operationSuccessful = false;
            }
        }

        File shmFile = new File(databasesFolder, databaseName + "-shm");
        if (shmFile.exists()) {
            if (shmFile.delete()) {
                Timber.i("Database %s-shm deleted", databaseName);
            } else {
                Timber.e("Failed to delete database %s-shm", databaseName);
                return false;
            }
        }

        return operationSuccessful;
    }

    protected static boolean deleteDbJournal(File databases, @NonNull String databaseName) {
        File journal = new File(databases, databaseName + "-journal");
        if (journal.exists()) {
            if (journal.delete()) {
                Timber.i("Database %s journal deleted", databaseName);
            } else {
                Timber.e("Failed to delete database %s journal", databaseName);
                return false;
            }
        }

        return true;
    }

    @Nullable
    public static String getAppId(@NonNull Context context) {
        PackageInfo packageInfo = getPackageInfo(context);

        return packageInfo != null? packageInfo.packageName : null;
    }

    @Nullable
    public static String getAppVersion(@NonNull Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        return packageInfo != null? packageInfo.versionName : null;
    }

    @Nullable
    private static PackageInfo getPackageInfo(@NonNull Context context) {

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
            return null;
        }
    }

    public static Long tryParseLong(String value, long defaultValue) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int calculatePercentage(long totalCount, long partialCount){
        if (totalCount < 1) {
            return 100;
        } else if (partialCount < 1) {
            return 0;
        } else {
           return  Math.round(( partialCount * 100f) /  totalCount);
        }
    }

}