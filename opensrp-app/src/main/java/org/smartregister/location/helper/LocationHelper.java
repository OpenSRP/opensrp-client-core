package org.smartregister.location.helper;

import android.support.annotation.VisibleForTesting;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

import static org.smartregister.AllConstants.CAMPAIGNS;
import static org.smartregister.AllConstants.OPERATIONAL_AREAS;


/**
 * Created by ndegwamartin on 09/04/2018.
 */

public class LocationHelper {

    private static LocationHelper instance;
    private String childLocationId;
    private String parentLocationId;
    private List<String> locationIds;
    private List<String> locationNames;
    private List<String> locationNameHierarchy;
    private Map<String, Pair<String, String>> childAndParentLocationIds;
    private String defaultLocation;

    private List<String> ALLOWED_LEVELS;
    private String DEFAULT_LOCATION_LEVEL;
    private List<String> allCampaigns = new ArrayList<>();
    private List<String> allOperationalArea = new ArrayList<>();
    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

    private LocationHelper(List<String> allowedLevels, String defaultLocationLevel) {

        childAndParentLocationIds = new HashMap<>();
        setParentAndChildLocationIds(getDefaultLocation());
        this.ALLOWED_LEVELS = allowedLevels;
        this.DEFAULT_LOCATION_LEVEL = defaultLocationLevel;
    }

    public static void init(List<String> allowedLevels, String defaultLocationLevel) {
        if (instance == null && StringUtils.isNotEmpty(defaultLocationLevel) && allowedLevels != null && allowedLevels.contains(defaultLocationLevel)) {
            instance = new LocationHelper(allowedLevels, defaultLocationLevel);
        }
    }

    public static LocationHelper getInstance() {
        return instance;
    }

    public String locationIdsFromHierarchy() {
        if (Utils.isEmptyCollection(locationIds)) {
            locationIds = locationsFromHierarchy(true, null);
        }

        if (!Utils.isEmptyCollection(locationIds)) {
            return StringUtils.join(locationIds, ",");
        }
        return null;
    }

    public List<String> locationNamesFromHierarchy(String defaultLocation) {
        if (Utils.isEmptyCollection(locationNames)) {
            locationNames = locationsFromHierarchy(false, defaultLocation);
        }
        return locationNames;
    }

    public List<String> locationsFromHierarchy(boolean fetchLocationIds, String defaultLocation) {
        List<String> locations = new ArrayList<>();
        try {
            LinkedHashMap<String, TreeNode<String, Location>> map = map();
            if (!Utils.isEmptyMap(map)) {
                for (Map.Entry<String, TreeNode<String, Location>> entry : map.entrySet()) {
                    List<String> foundLocations = extractLocations(entry.getValue(), fetchLocationIds, defaultLocation);

                    if (!Utils.isEmptyCollection(foundLocations)) {
                        locations.addAll(foundLocations);
                    }
                }

                if (ALLOWED_LEVELS.contains("reveal")) {
                    if (allCampaigns != null && !allCampaigns.isEmpty()) {
                        allSharedPreferences.savePreference(CAMPAIGNS, android.text.TextUtils.join(",", allCampaigns));
                    }
                    if (allOperationalArea != null && !allOperationalArea.isEmpty()) {
                        allSharedPreferences.savePreference(OPERATIONAL_AREAS, android.text.TextUtils.join(",", allOperationalArea));
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return locations;
    }

    public String getDefaultLocation() {
        if (StringUtils.isBlank(defaultLocation)) {
            List<String> rawDefaultLocation = generateDefaultLocationHierarchy(ALLOWED_LEVELS);

            if (!Utils.isEmptyCollection(rawDefaultLocation)) {
                defaultLocation = rawDefaultLocation.get(rawDefaultLocation.size() - 1);
            }

        }
        return defaultLocation;
    }

    public String getOpenMrsLocationId(String locationName) {
        if (StringUtils.isBlank(locationName)) {
            return null;
        }

        String response = locationName;
        try {
            LinkedHashMap<String, TreeNode<String, Location>> map = map();
            if (!Utils.isEmptyMap(map)) {
                for (Map.Entry<String, TreeNode<String, Location>> entry : map.entrySet()) {
                    String curResult = getOpenMrsLocationId(locationName, entry.getValue());
                    if (StringUtils.isNotBlank(curResult)) {
                        response = curResult;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return response;
    }

    public String getOpenMrsLocationName(String locationId) {
        if (StringUtils.isBlank(locationId)) {
            Timber.e("Location id is null");
            return null;
        }

        String response = locationId;
        try {
            LinkedHashMap<String, TreeNode<String, Location>> map = map();
            if (!Utils.isEmptyMap(map)) {
                for (Map.Entry<String, TreeNode<String, Location>> entry : map.entrySet()) {
                    String curResult = getOpenMrsLocationName(locationId, entry.getValue());
                    if (StringUtils.isNotBlank(curResult)) {
                        response = curResult;
                        break;
                    }
                }
            } else {
                Timber.e("locationData doesn't have locationHierarchy");
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return response;
    }

    /**
     * This method returns the name hierarchy of a location given it's id
     *
     * @param locationId        The ID for the location we want the hierarchy for
     * @param onlyAllowedLevels Restrict the location name hierarchy to only the allowed levels
     * @return The name hierarchy (starting with the top-most parent) for the location or {@code NULL} if location id is not found
     */
    public List<String> getOpenMrsLocationHierarchy(String locationId, boolean onlyAllowedLevels) {
        if (StringUtils.isBlank(locationId)) {
            Timber.e("Location id is null");
            return new ArrayList<>();
        }
        List<String> response = null;

        try {
            LinkedHashMap<String, TreeNode<String, Location>> map = map();
            if (!Utils.isEmptyMap(map)) {
                for (Map.Entry<String, TreeNode<String, Location>> entry : map.entrySet()) {
                    List<String> curResult = getOpenMrsLocationHierarchy(locationId, entry.getValue(), new ArrayList<>(), onlyAllowedLevels);
                    if (!Utils.isEmptyCollection(curResult)) {
                        response = curResult;
                        break;
                    }
                }
            } else {
                Timber.e("locationData doesn't have locationHierarchy");
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return response;
    }


    public List<String> generateDefaultLocationHierarchy(List<String> allowedLevels) {
        if (Utils.isEmptyCollection(allowedLevels)) {
            return new ArrayList<>();
        }

        try {
            String defaultLocationUuid = allSharedPreferences.fetchDefaultLocalityId(allSharedPreferences.fetchRegisteredANM());

            LinkedHashMap<String, TreeNode<String, Location>> map = map();
            if (!Utils.isEmptyMap(map)) {
                for (Map.Entry<String, TreeNode<String, Location>> entry : map.entrySet()) {
                    List<String> curResult = getDefaultLocationHierarchy(defaultLocationUuid, entry.getValue(), new ArrayList<>(), allowedLevels);
                    if (!Utils.isEmptyCollection(curResult)) {
                        return curResult;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public List<FormLocation> generateLocationHierarchyTree(boolean withOtherOption, List<String> allowedLevels) {
        LinkedHashMap<String, TreeNode<String, Location>> map = map();
        return generateLocationHierarchyTree(withOtherOption, allowedLevels, map);
    }

    public List<FormLocation> generateLocationHierarchyTree(boolean withOtherOption, List<String> allowedLevels, Map<String, TreeNode<String, Location>> map) {
        if (Utils.isEmptyCollection(allowedLevels)) {
            return new ArrayList<>();
        }

        List<FormLocation> formLocationList = new ArrayList<>();
        try {
            if (!Utils.isEmptyMap(map)) {
                for (Map.Entry<String, TreeNode<String, Location>> entry : map.entrySet()) {
                    List<FormLocation> foundLocationList = getFormJsonData(entry.getValue(), allowedLevels);
                    if (!Utils.isEmptyCollection(foundLocationList)) {
                        formLocationList.addAll(foundLocationList);
                    }
                }
            }

            formLocationList = sortTreeViewQuestionOptions(formLocationList);
        } catch (Exception e) {
            Timber.e(e);
        }

        if (withOtherOption) {
            FormLocation other = new FormLocation();
            other.name = "Other";
            other.key = "Other";
            other.level = "";
            formLocationList.add(other);
        }
        return formLocationList;
    }

    public String getOpenMrsReadableName(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }

        String readableName = name;

        try {
            Pattern prefixPattern = Pattern.compile("^[a-z]{2} (.*)$");
            Matcher prefixMatcher = prefixPattern.matcher(readableName);
            if (prefixMatcher.find()) {
                readableName = prefixMatcher.group(1);
            }

            if (readableName.contains(":")) {
                String[] splitName = readableName.split(":");
                readableName = splitName[splitName.length - 1].trim();
            }

        } catch (Exception e) {
            Timber.e(e);
        }
        return readableName;
    }

    // Private methods
    private List<String> extractLocations(TreeNode<String, Location> rawLocationData, boolean fetchLocationIds, String defaultLocation) {

        List<String> locationList = new ArrayList<>();
        try {
            if (rawLocationData == null) {
                return null;
            }
            Location node = rawLocationData.getNode();
            if (node == null) {
                return null;
            }
            String value = fetchLocationIds ? node.getLocationId() : node.getName();
            Set<String> levels = node.getTags();

            if (!Utils.isEmptyCollection(levels)) {
                String teamUID = allSharedPreferences.fetchDefaultTeamId(allSharedPreferences.fetchRegisteredANM());
                for (String level : levels) {
                    if (ALLOWED_LEVELS.contains(level)) {

                        if (node.getAttribute("campaign_id") != null) {

                            allCampaigns.add(node.getAttribute("campaign_id").toString());
                        }
                        if (node.getAttribute("team_id") != null && node.getAttribute("team_id").toString().equals(teamUID)) {
                            allOperationalArea.add(node.getName());
                        }
                        if (!fetchLocationIds && DEFAULT_LOCATION_LEVEL.equals(level) && defaultLocation != null && !defaultLocation.equals(value)) {
                            return locationList;
                        }

                        locationList.add(value);
                    }
                }

            }

            LinkedHashMap<String, TreeNode<String, Location>> childMap = childMap(rawLocationData);
            if (!Utils.isEmptyMap(childMap)) {
                for (Map.Entry<String, TreeNode<String, Location>> childEntry : childMap.entrySet()) {
                    List<String> childLocations = extractLocations(childEntry.getValue(), fetchLocationIds, defaultLocation);
                    if (!Utils.isEmptyCollection(childLocations)) {
                        locationList.addAll(childLocations);
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return locationList;
    }

    private String getOpenMrsLocationId(String locationName, TreeNode<String, Location> openMrsLocations) {
        try {
            if (openMrsLocations == null) {
                return null;
            }

            Location node = openMrsLocations.getNode();
            if (node == null) {
                return null;
            }
            String name = node.getName();
            if (locationName.equals(name)) {
                return node.getLocationId();
            }

            LinkedHashMap<String, TreeNode<String, Location>> childMap = childMap(openMrsLocations);
            if (!Utils.isEmptyMap(childMap)) {
                for (Map.Entry<String, TreeNode<String, Location>> childEntry : childMap.entrySet()) {
                    String curResult = getOpenMrsLocationId(locationName, childEntry.getValue());
                    if (StringUtils.isNotBlank(curResult)) {
                        return curResult;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private String getOpenMrsLocationName(String locationId, TreeNode<String, Location> openMrsLocations) {
        try {
            if (openMrsLocations == null) {
                return null;
            }

            Location node = openMrsLocations.getNode();
            if (node == null) {
                return null;
            }
            String id = node.getLocationId();
            Timber.d("Current location id is %s", id);
            if (locationId.equals(id)) {
                return node.getName();
            }

            LinkedHashMap<String, TreeNode<String, Location>> childMap = childMap(openMrsLocations);
            if (!Utils.isEmptyMap(childMap)) {
                for (Map.Entry<String, TreeNode<String, Location>> childEntry : childMap.entrySet()) {
                    String curResult = getOpenMrsLocationName(locationId, childEntry.getValue());
                    if (StringUtils.isNotBlank(curResult)) {
                        return curResult;
                    }
                }
            } else {
                Timber.d(id + " does not have children");
            }

        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public List<String> getDefaultLocationHierarchy(String defaultLocationUuid, TreeNode<String,
            Location> openMrsLocationData, List<String> parents, List<String> allowedLevels) {
        try {
            List<String> hierarchy = new ArrayList<>(parents);
            if (openMrsLocationData == null) {
                return null;
            }

            Location node = openMrsLocationData.getNode();
            if (node == null) {
                return null;
            }

            Set<String> levels = node.getTags();
            if (!Utils.isEmptyCollection(levels)) {
                for (String level : levels) {
                    if (allowedLevels.contains(level)) {
                        hierarchy.add(node.getName());
                    }
                }
            }

            if (defaultLocationUuid.equals(node.getLocationId())) {
                return hierarchy;
            }

            LinkedHashMap<String, TreeNode<String, Location>> childMap = childMap(openMrsLocationData);
            if (!Utils.isEmptyMap(childMap)) {
                for (Map.Entry<String, TreeNode<String, Location>> childEntry : childMap.entrySet()) {
                    List<String> curResult = getDefaultLocationHierarchy(defaultLocationUuid, childEntry.getValue(), hierarchy, allowedLevels);
                    if (!Utils.isEmptyCollection(curResult)) {
                        return curResult;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private List<FormLocation> getFormJsonData(TreeNode<String, Location> openMrsLocationData,
                                               List<String> allowedLevels) {
        List<FormLocation> allLocationData = new ArrayList<>();
        try {
            FormLocation formLocation = new FormLocation();

            if (openMrsLocationData == null) {
                return null;
            }

            Location node = openMrsLocationData.getNode();
            if (node == null) {
                return null;
            }

            String name = node.getName();
            formLocation.name = getOpenMrsReadableName(name);
            formLocation.key = name;

            Set<String> levels = node.getTags();
            formLocation.level = isLocationTagsShownEnabled() && levels != null && !levels.isEmpty() ? levels.iterator().next() : "";


            LinkedHashMap<String, TreeNode<String, Location>> childMap = childMap(openMrsLocationData);
            if (!Utils.isEmptyMap(childMap)) {
                List<FormLocation> children = new ArrayList<>();
                for (Map.Entry<String, TreeNode<String, Location>> childEntry : childMap.entrySet()) {
                    List<FormLocation> childFormLocations = getFormJsonData(childEntry.getValue(), allowedLevels);
                    if (!Utils.isEmptyCollection(childFormLocations)) {
                        children.addAll(childFormLocations);
                    }
                }

                boolean allowed = false;
                for (String level : levels) {
                    if (allowedLevels.contains(level)) {
                        formLocation.nodes = children;
                        allowed = true;
                    }
                }

                if (!allowed) {
                    for (FormLocation childLocation : children) {
                        allLocationData.add(childLocation);
                    }
                }
            }

            for (String level : levels) {
                if (allowedLevels.contains(level)) {
                    allLocationData.add(formLocation);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return allLocationData;
    }

    @VisibleForTesting
    protected boolean isLocationTagsShownEnabled() {
        return Utils.getBooleanProperty(AllConstants.PROPERTY.LOCATION_PICKER_TAG_SHOWN);
    }

    /**
     * This method sorts the options provided for a native form tree view question
     *
     * @return The sorted options
     */
    private List<FormLocation> sortTreeViewQuestionOptions
    (List<FormLocation> treeViewOptions) {
        if (Utils.isEmptyCollection(treeViewOptions)) {
            return treeViewOptions;
        }

        List<FormLocation> sortedTree = new ArrayList<>();

        HashMap<String, FormLocation> sortMap = new HashMap<>();
        for (FormLocation formLocation : treeViewOptions) {
            sortMap.put(formLocation.name, formLocation);
        }

        List<String> sortedKeys = new ArrayList<>(sortMap.keySet());
        Collections.sort(sortedKeys);

        for (String curOptionName : sortedKeys) {
            FormLocation curOption = sortMap.get(curOptionName);
            if (!Utils.isEmptyCollection(curOption.nodes)) {
                curOption.nodes = sortTreeViewQuestionOptions(curOption.nodes);
            }
            sortedTree.add(curOption);
        }

        return sortedTree;
    }

    /**
     * Maps location name {@param currLocation} to both its parent and child (self) OpenMRS location ids
     * <p>
     * The child location is taken to be the {@param currLocation} and the method attempts to find
     * its location id and that of its parent
     *
     * @return a  {@link Pair} representing the parent and child OpenMRS location ids
     */
    private Pair<String, String> getParentAndChildLocationIds(String currLocation) {

        if (childAndParentLocationIds.containsKey(currLocation)) {
            return childAndParentLocationIds.get(currLocation);
        }

        String currLocationId = getOpenMrsLocationId(currLocation);
        Pair<String, String> result;
        if (currLocationId == null) {
            result = new Pair<>(getDefaultLocation(), getDefaultLocation());
        } else {
            locationNameHierarchy = getOpenMrsLocationHierarchy(currLocationId, true);

            String childLocationName = locationNameHierarchy.get(locationNameHierarchy.size() - 1);
            String parentLocationName = locationNameHierarchy.get(locationNameHierarchy.size() - 1);
            if (locationNameHierarchy.size() > 1) {
                parentLocationName = locationNameHierarchy.get(locationNameHierarchy.size() - 2);
            }

            childLocationId = getOpenMrsLocationId(childLocationName);
            parentLocationId = getOpenMrsLocationId(parentLocationName);

            result = new Pair<>(parentLocationId, childLocationId);
            childAndParentLocationIds.put(currLocation, result);
        }
        return result;
    }

    public void setParentAndChildLocationIds(String currLocation) {

        Pair<String, String> parentAndChildLocationIds = getParentAndChildLocationIds(currLocation);
        setParentLocationId(parentAndChildLocationIds.first);
        setChildLocationId(parentAndChildLocationIds.second);
    }


    public String getParentLocationId() {

        return parentLocationId;
    }

    public String getChildLocationId() {

        return childLocationId;
    }

    public void setParentLocationId(String parentId) {

        parentLocationId = parentId;
    }

    public void setChildLocationId(String childId) {

        childLocationId = childId;
    }

    private List<String> getOpenMrsLocationHierarchy(String locationId,
                                                     TreeNode<String, Location> openMrsLocation,
                                                     List<String> parents, boolean onlyAllowedLevels) {
        List<String> hierarchy = new ArrayList<>(parents);
        if (openMrsLocation == null) {
            return null;
        }

        Location node = openMrsLocation.getNode();
        if (node == null) {
            return null;
        }

        if (onlyAllowedLevels) {
            Set<String> levels = node.getTags();
            if (!Utils.isEmptyCollection(levels)) {
                for (String level : levels) {
                    if (ALLOWED_LEVELS.contains(level)) {
                        hierarchy.add(node.getName());
                    }
                }
            }
        } else {
            hierarchy.add(node.getName());
        }

        String id = node.getLocationId();
        Timber.d("Current location id is %s", id);
        if (locationId.equals(id)) {
            return hierarchy;
        }

        LinkedHashMap<String, TreeNode<String, Location>> childMap = childMap(openMrsLocation);
        if (!Utils.isEmptyMap(childMap)) {
            for (Map.Entry<String, TreeNode<String, Location>> childEntry : childMap.entrySet()) {
                List<String> curResult = getOpenMrsLocationHierarchy(locationId, childEntry.getValue(), hierarchy, onlyAllowedLevels);
                if (!Utils.isEmptyCollection(curResult)) {
                    return curResult;
                }
            }
        } else {
            Timber.d(id + " does not have children");
        }

        return null;
    }


    public LinkedHashMap<String, TreeNode<String, Location>> map() {
        String locationData = CoreLibrary.getInstance().context().anmLocationController().get();
        LocationTree locationTree = AssetHandler.jsonStringToJava(locationData, LocationTree.class);
        if (locationTree != null) {
            return locationTree.getLocationsHierarchy();
        }

        return null;
    }


    private LinkedHashMap<String, TreeNode<String, Location>> childMap
            (TreeNode<String, Location> treeNode) {
        if (treeNode.getChildren() != null) {
            return treeNode.getChildren();
        }
        return null;
    }

    public List<String> getAllowedLevels() {
        return ALLOWED_LEVELS;
    }

    public String getDefaultLocationLevel() {
        return DEFAULT_LOCATION_LEVEL;
    }

}