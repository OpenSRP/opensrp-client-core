package org.smartregister.sync.helper;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.firebase.perf.metrics.Trace;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.LocationTag;
import org.smartregister.domain.Response;
import org.smartregister.domain.SyncEntity;
import org.smartregister.domain.SyncProgress;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.LocationTagRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.PropertiesConverter;
import org.smartregister.util.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.AllConstants.COUNT;
import static org.smartregister.AllConstants.JURISDICTION_IDS;
import static org.smartregister.AllConstants.LocationConstants.DISPLAY;
import static org.smartregister.AllConstants.LocationConstants.LOCATION;
import static org.smartregister.AllConstants.LocationConstants.LOCATIONS;
import static org.smartregister.AllConstants.LocationConstants.SPECIAL_TAG_FOR_OPENMRS_TEAM_MEMBERS;
import static org.smartregister.AllConstants.LocationConstants.TEAM;
import static org.smartregister.AllConstants.LocationConstants.UUID;
import static org.smartregister.AllConstants.OPERATIONAL_AREAS;
import static org.smartregister.AllConstants.PerformanceMonitoring.ACTION;
import static org.smartregister.AllConstants.PerformanceMonitoring.FETCH;
import static org.smartregister.AllConstants.PerformanceMonitoring.LOCATION_SYNC;
import static org.smartregister.AllConstants.PerformanceMonitoring.PUSH;
import static org.smartregister.AllConstants.PerformanceMonitoring.STRUCTURE;
import static org.smartregister.AllConstants.RETURN_COUNT;
import static org.smartregister.AllConstants.TYPE;
import static org.smartregister.util.PerformanceMonitoringUtils.addAttribute;
import static org.smartregister.util.PerformanceMonitoringUtils.clearTraceAttributes;
import static org.smartregister.util.PerformanceMonitoringUtils.initTrace;
import static org.smartregister.util.PerformanceMonitoringUtils.startTrace;
import static org.smartregister.util.PerformanceMonitoringUtils.stopTrace;

public class LocationServiceHelper extends BaseHelper {

    public static final String LOCATION_STRUCTURE_URL = "/rest/location/sync";
    public static final String CREATE_STRUCTURE_URL = "/rest/location/add";
    public static final String COMMON_LOCATIONS_SERVICE_URL = "/location/by-level-and-tags";
    public static final String OPENMRS_LOCATION_BY_TEAM_IDS = "/location/by-team-ids";
    public static final String LOCATION_HIERARCHY_URL = "/rest/location/hierarchy/";
    public static final String ALL_LOCATIONS_URL = "/rest/location/getAll";
    public static final String STRUCTURES_LAST_SYNC_DATE = "STRUCTURES_LAST_SYNC_DATE";
    public static final String LOCATION_LAST_SYNC_DATE = "LOCATION_LAST_SYNC_DATE";
    private static final String LOCATIONS_NOT_PROCESSED = "Locations with Ids not processed: ";
    private static final String LOCATION_IDS = "location_ids";
    private static final String IS_JURISDICTION = "is_jurisdiction";
    private static final String LOCATION_NAMES = "location_names";
    private static final String PARENT_ID = "parent_id";
    private static final String LIMIT = "limit";
    private static final int RECORD_COUNT = 2000;

    public static Gson locationGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HHmm")
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();
    protected static LocationServiceHelper instance;
    protected final Context context;
    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
    private LocationRepository locationRepository;
    private LocationTagRepository locationTagRepository;
    private StructureRepository structureRepository;
    private Trace locationSyncTrace;
    private String team;
    private long totalRecords;
    private SyncProgress syncProgress;

    public LocationServiceHelper(LocationRepository locationRepository, LocationTagRepository locationTagRepository, StructureRepository structureRepository) {
        this.context = CoreLibrary.getInstance().context().applicationContext();
        this.locationRepository = locationRepository;
        this.locationTagRepository = locationTagRepository;
        this.structureRepository = structureRepository;
        this.locationSyncTrace = initTrace(LOCATION_SYNC);
        String providerId = allSharedPreferences.fetchRegisteredANM();
        team = allSharedPreferences.fetchDefaultTeam(providerId);
    }

    public static LocationServiceHelper getInstance() {
        if (instance == null) {
            instance = new LocationServiceHelper(CoreLibrary.getInstance().context().getLocationRepository(), CoreLibrary.getInstance().context().getLocationTagRepository(), CoreLibrary.getInstance().context().getStructureRepository());
        }
        return instance;
    }

    protected List<Location> syncLocationsStructures(boolean isJurisdiction) {
        syncProgress = new SyncProgress();
        if (isJurisdiction) {
            syncProgress.setSyncEntity(SyncEntity.LOCATIONS);
        } else {
            syncProgress.setSyncEntity(SyncEntity.STRUCTURES);
        }
        syncProgress.setTotalRecords(totalRecords);

        List<Location> locationStructures = batchSyncLocationsStructures(isJurisdiction, new ArrayList<>(), true);
        syncProgress.setPercentageSynced(Utils.calculatePercentage(totalRecords, locationStructures.size()));
        sendSyncProgressBroadcast(syncProgress, context);
        return locationStructures;
    }

    private List<Location> batchSyncLocationsStructures(boolean isJurisdiction, List<Location> batchLocationStructures, boolean returnCount) {
        long serverVersion = 0;
        String currentServerVersion = allSharedPreferences.getPreference(isJurisdiction ? LOCATION_LAST_SYNC_DATE : STRUCTURES_LAST_SYNC_DATE);
        try {
            serverVersion = (StringUtils.isEmpty(currentServerVersion) ? 0 : Long.parseLong(currentServerVersion));
        } catch (NumberFormatException e) {
            Timber.e(e, "EXCEPTION %s", e.toString());
        }
        if (serverVersion > 0) serverVersion += 1;
        try {
            List<String> parentIds = locationRepository.getAllLocationIds();

            if (isJurisdiction) {
                startLocationTrace(FETCH, AllConstants.PerformanceMonitoring.LOCATION, 0);
            } else {
                startLocationTrace(FETCH, STRUCTURE, 0);
            }
            startTrace(locationSyncTrace);
            String featureResponse = fetchLocationsOrStructures(isJurisdiction, serverVersion, TextUtils.join(",", parentIds), returnCount);
            List<Location> locations = locationGson.fromJson(featureResponse, new TypeToken<List<Location>>() {
            }.getType());

            addAttribute(locationSyncTrace, COUNT, String.valueOf(locations.size()));
            stopTrace(locationSyncTrace);

            for (Location location : locations) {
                try {
                    location.setSyncStatus(BaseRepository.TYPE_Synced);
                    if (isJurisdiction)
                        locationRepository.addOrUpdate(location);
                    else {
                        structureRepository.addOrUpdate(location);
                    }
                    location.setGeometry(null);
                } catch (Exception e) {
                    Timber.e(e, "EXCEPTION %s", e.toString());
                }
            }
            if (!Utils.isEmptyCollection(locations)) {
                String maxServerVersion = getMaxServerVersion(locations);
                String updateKey = isJurisdiction ? LOCATION_LAST_SYNC_DATE : STRUCTURES_LAST_SYNC_DATE;
                allSharedPreferences.savePreference(updateKey, maxServerVersion);

                // retry fetch since there were items synced from the server
                locations.addAll(batchLocationStructures);
                syncProgress.setPercentageSynced(Utils.calculatePercentage(totalRecords, locations.size()));
                sendSyncProgressBroadcast(syncProgress, context);
                return batchSyncLocationsStructures(isJurisdiction, locations, false);

            }
        } catch (Exception e) {
            Timber.e(e, "EXCEPTION %s", e.toString());
        }
        return batchLocationStructures;
    }

    private String fetchLocationsOrStructures(boolean isJurisdiction, Long serverVersion, String locationFilterValue, boolean returnCount) throws Exception {

        HTTPAgent httpAgent = getHttpAgent();
        if (httpAgent == null) {
            throw new IllegalArgumentException(LOCATION_STRUCTURE_URL + " http agent is null");
        }

        String baseUrl = getFormattedBaseUrl();

        Response<String> resp;

        JSONObject request = new JSONObject();
        request.put(IS_JURISDICTION, isJurisdiction);
        request.put(RETURN_COUNT, returnCount);
        if (isJurisdiction) {
            String preferenceLocationNames = allSharedPreferences.getPreference(OPERATIONAL_AREAS);
            request.put(LOCATION_NAMES, new JSONArray(Arrays.asList(preferenceLocationNames.split(","))));

            String preferenceLocationIds = allSharedPreferences.getPreference(JURISDICTION_IDS);
            if (StringUtils.isNotBlank(preferenceLocationIds)) {
                request.put(LOCATION_IDS, new JSONArray(Arrays.asList(preferenceLocationIds.split(","))));
            }
        } else {
            request.put(PARENT_ID, new JSONArray(Arrays.asList(locationFilterValue.split(","))));
        }
        request.put(AllConstants.SERVER_VERSION, serverVersion);

        resp = httpAgent.post(MessageFormat.format("{0}{1}", baseUrl, LOCATION_STRUCTURE_URL),
                request.toString());

        if (resp.isFailure()) {
            throw new NoHttpResponseException(LOCATION_STRUCTURE_URL + " not returned data");
        }

        if (returnCount) {
            totalRecords = resp.getTotalRecords();
        }

        return resp.payload();
    }

    private String getMaxServerVersion(List<Location> locations) {
        long maxServerVersion = 0;
        for (Location location : locations) {
            long serverVersion = location.getServerVersion();
            if (serverVersion > maxServerVersion) {
                maxServerVersion = serverVersion;
            }
        }
        return String.valueOf(maxServerVersion);
    }

    public List<Location> fetchLocationsStructures() {
        syncLocationsStructures(true);
        List<Location> locations = syncLocationsStructures(false);
        syncCreatedStructureToServer();
        syncUpdatedLocationsToServer();
        return locations;
    }

    public void fetchLocationsByLevelAndTags() throws Exception {

        HTTPAgent httpAgent = getHttpAgent();

        if (httpAgent == null) {
            throw new IllegalArgumentException(COMMON_LOCATIONS_SERVICE_URL + " http agent is null");
        }

        String baseUrl = getFormattedBaseUrl();

        SyncConfiguration configs = getSyncConfiguration();

        JSONObject requestPayload = new JSONObject();
        requestPayload.put("locationUUID", allSharedPreferences.fetchDefaultLocalityId(allSharedPreferences.fetchRegisteredANM()));
        requestPayload.put("locationTopLevel", configs.getTopAllowedLocationLevel());
        requestPayload.put("locationTagsQueried", new JSONArray(new Gson().toJson(configs.getSynchronizedLocationTags())));

        Response<String> resp = httpAgent.post(
                MessageFormat.format("{0}{1}",
                        baseUrl,
                        COMMON_LOCATIONS_SERVICE_URL),
                requestPayload.toString());

        if (resp.isFailure()) {
            throw new NoHttpResponseException(COMMON_LOCATIONS_SERVICE_URL + " not returned data");
        }

        List<org.smartregister.domain.jsonmapping.Location> receivedOpenMrsLocations =
                new Gson().fromJson(resp.payload(),
                        new TypeToken<List<org.smartregister.domain.jsonmapping.Location>>() {
                        }.getType());

        for (org.smartregister.domain.jsonmapping.Location openMrsLocation : receivedOpenMrsLocations) {
            Location location = new Location();
            location.setId(openMrsLocation.getLocationId());
            LocationProperty property = new LocationProperty();
            property.setUid(openMrsLocation.getLocationId());
            property.setParentId(openMrsLocation.getParentLocation().getLocationId());
            property.setName(openMrsLocation.getName());
            location.setProperties(property);

            locationRepository.addOrUpdate(location);


            for (String tagName : openMrsLocation.getTags()) {
                LocationTag locationTag = new LocationTag();
                locationTag.setLocationId(openMrsLocation.getLocationId());
                locationTag.setName(tagName);

                locationTagRepository.addOrUpdate(locationTag);
            }
        }
    }

    public SyncConfiguration getSyncConfiguration() {
        return CoreLibrary.getInstance().getSyncConfiguration();
    }

    public void syncCreatedStructureToServer() {
        List<Location> locations = structureRepository.getAllUnsynchedCreatedStructures();
        if (!locations.isEmpty()) {
            String jsonPayload = locationGson.toJson(locations);
            startLocationTrace(PUSH, STRUCTURE, locations.size());
            String baseUrl = getFormattedBaseUrl();
            Response<String> response = getHttpAgent().postWithJsonResponse(
                    MessageFormat.format("{0}/{1}",
                            baseUrl,
                            CREATE_STRUCTURE_URL),
                    jsonPayload);
            if (response.isFailure()) {
                Timber.e("Failed to create new locations on server: %s", response.payload());
                return;
            }
            stopTrace(locationSyncTrace);
            Set<String> unprocessedIds = new HashSet<>();
            if (StringUtils.isNotBlank(response.payload())) {
                if (response.payload().startsWith(LOCATIONS_NOT_PROCESSED)) {
                    unprocessedIds.addAll(Arrays.asList(response.payload().substring(LOCATIONS_NOT_PROCESSED.length()).split(",")));
                }
                for (Location location : locations) {
                    if (!unprocessedIds.contains(location.getId()))
                        structureRepository.markStructuresAsSynced(location.getId());
                }
            }
        }
    }

    private void startLocationTrace(String action, String type, int count) {
        clearTraceAttributes(locationSyncTrace);
        addAttribute(locationSyncTrace, AllConstants.PerformanceMonitoring.TEAM, team);
        addAttribute(locationSyncTrace, ACTION, action);
        addAttribute(locationSyncTrace, TYPE, type);
        addAttribute(locationSyncTrace, COUNT, String.valueOf(count));
        startTrace(locationSyncTrace);
    }

    public void syncUpdatedLocationsToServer() {
        HTTPAgent httpAgent = getHttpAgent();
        List<Location> locations = locationRepository.getAllUnsynchedLocation();
        if (!locations.isEmpty()) {
            String jsonPayload = locationGson.toJson(locations);
            String baseUrl = getFormattedBaseUrl();

            String isJurisdictionParam = "?" + IS_JURISDICTION + "=true";
            Response<String> response = httpAgent.postWithJsonResponse(
                    MessageFormat.format("{0}{1}{2}",
                            baseUrl,
                            CREATE_STRUCTURE_URL,
                            isJurisdictionParam),
                    jsonPayload);
            if (response.isFailure()) {
                Timber.e("Failed to create new locations on server: %s", response.payload());
                return;
            }

            Set<String> unprocessedIds = new HashSet<>();
            if (StringUtils.isNotBlank(response.payload())) {
                if (response.payload().startsWith(LOCATIONS_NOT_PROCESSED)) {
                    unprocessedIds.addAll(Arrays.asList(response.payload().substring(LOCATIONS_NOT_PROCESSED.length()).split(",")));
                }
                for (Location location : locations) {
                    if (!unprocessedIds.contains(location.getId()))
                        locationRepository.markLocationsAsSynced(location.getId());
                }
            }
        }
    }

    public void fetchOpenMrsLocationsByTeamIds() throws NoHttpResponseException, JSONException {
        HTTPAgent httpAgent = getHttpAgent();
        if (httpAgent == null) {
            throw new IllegalArgumentException(OPENMRS_LOCATION_BY_TEAM_IDS + " http agent is null");
        }
        String baseUrl = getFormattedBaseUrl();

        Response resp = httpAgent.post(
                MessageFormat.format("{0}{1}", baseUrl, OPENMRS_LOCATION_BY_TEAM_IDS),
                new JSONArray().put(allSharedPreferences.fetchDefaultLocalityId(
                        allSharedPreferences.fetchRegisteredANM())).toString());

        if (resp.isFailure()) {
            throw new NoHttpResponseException(OPENMRS_LOCATION_BY_TEAM_IDS + " not returned data");
        }

        Timber.i(resp.payload().toString());
        JSONArray teamLocations = new JSONArray(resp.payload().toString());

        for (int index = 0; index < teamLocations.length(); index++) {
            JSONObject openMrsLocation = teamLocations.getJSONObject(index);
            if (openMrsLocation.has(LOCATIONS) && openMrsLocation.has(TEAM)) {
                JSONArray actualLocations = openMrsLocation.getJSONArray(LOCATIONS);
                saveOpenMrsTeamLocation(openMrsLocation, actualLocations);
            }
        }
    }

    private void saveOpenMrsTeamLocation(JSONObject openMrsLocation, JSONArray actualLocations) throws JSONException {
        for (int currentIndex = 0; currentIndex < actualLocations.length(); currentIndex++) {
            JSONObject actualLocation = actualLocations.getJSONObject(currentIndex);
            if (actualLocation.has(DISPLAY) && actualLocation.has(UUID)) {
                Location location = new Location();
                location.setId(actualLocation.getString(UUID));
                LocationProperty property = new LocationProperty();
                property.setUid(actualLocation.getString(UUID));
                property.setParentId(openMrsLocation.getJSONObject(TEAM).getJSONObject(LOCATION).getString(UUID));
                property.setName(actualLocation.getString(DISPLAY));
                location.setProperties(property);
                locationRepository.addOrUpdate(location);

                //Save tag with a special keyword for team members on the location tags table.
                LocationTag locationTag = new LocationTag();
                locationTag.setLocationId(location.getId());
                locationTag.setName(SPECIAL_TAG_FOR_OPENMRS_TEAM_MEMBERS);
                locationTagRepository.addOrUpdate(locationTag);
            }
        }
    }

    public HTTPAgent getHttpAgent() {
        return CoreLibrary.getInstance().context().getHttpAgent();
    }

    @NotNull
    public String getFormattedBaseUrl() {
        String baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        return baseUrl;
    }

    public void fetchAllLocations() {
        fetchAllLocations(RECORD_COUNT);
    }

    public void fetchAllLocations(int recordCount) {
        try {
            HTTPAgent httpAgent = getHttpAgent();
            if (httpAgent == null) {
                throw new IllegalArgumentException(ALL_LOCATIONS_URL + " http agent is null");
            }

            String baseUrl = getFormattedBaseUrl();

            String urlParams = "?" + IS_JURISDICTION + "=" + true;
            urlParams += "&" + AllConstants.SERVER_VERSION + "=" + 0;
            urlParams += "&" + LIMIT + "=" + recordCount;

            Response<String> resp = httpAgent.fetch(MessageFormat.format("{0}{1}{2}", baseUrl, ALL_LOCATIONS_URL, urlParams));

            if (resp.isFailure()) {
                throw new NoHttpResponseException(ALL_LOCATIONS_URL + " not returned data");
            }

            List<Location> locations = locationGson.fromJson(
                    resp.payload(),
                    new TypeToken<List<Location>>() {
                    }.getType()
            );

            for (Location location : locations) {
                try {
                    location.setSyncStatus(BaseRepository.TYPE_Synced);

                    locationRepository.addOrUpdate(location);

                    for (LocationTag tag : location.getLocationTags()) {
                        LocationTag locationTag = new LocationTag();
                        locationTag.setLocationId(location.getId());
                        locationTag.setName(tag.getName());

                        locationTagRepository.addOrUpdate(locationTag);
                    }
                } catch (Exception e) {
                    Timber.e(e, "EXCEPTION %s", e.toString());
                }
            }
        } catch (Exception e) {
            Timber.e(e, "EXCEPTION %s", e.toString());
        }
    }

    @Nullable
    public LocationTree getLocationHierarchy(String locationId) {
        LocationTree locationTree = null;
        Response<String> resp = getHttpAgent().fetch(MessageFormat.format("{0}{1}{2}",
                getFormattedBaseUrl(), LOCATION_HIERARCHY_URL, locationId));

        if (resp.isFailure()) {
            Timber.e("Location hierarchy sync failed");
            return locationTree;
        }

        return AssetHandler.jsonStringToJava(resp.payload(), LocationTree.class);
    }
}

