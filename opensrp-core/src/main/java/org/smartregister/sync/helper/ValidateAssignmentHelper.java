package org.smartregister.sync.helper;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;

import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.Response;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.dto.UserAssignmentDTO;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.service.UserService;
import org.smartregister.util.SyncUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.controller.ANMLocationController;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.account.AccountHelper.CONFIGURATION_CONSTANTS.IS_KEYCLOAK_CONFIGURED;
import static org.smartregister.sync.helper.LocationServiceHelper.LOCATION_LAST_SYNC_DATE;
import static org.smartregister.sync.helper.LocationServiceHelper.STRUCTURES_LAST_SYNC_DATE;
import static org.smartregister.sync.helper.PlanIntentServiceHelper.PLAN_LAST_SYNC_DATE;
import static org.smartregister.sync.helper.TaskServiceHelper.TASK_LAST_SYNC_DATE;

/**
 * Created by samuelgithengi on 9/16/20.
 */
public class ValidateAssignmentHelper extends BaseHelper {

    private static final String USER_ASSIGNMENT_URL = "/rest/organization/user-assignment";

    public static final String ACTION_ASSIGNMENT_REMOVED = "action_assignment_removed";

    public static final String ASSIGNMENTS_REMOVED = "assignments_removed";

    public static final Gson gson = new Gson();

    private final SyncUtils syncUtils;

    private final UserService userService;

    private final PlanDefinitionRepository planDefinitionRepository;

    private final LocationRepository locationRepository;

    private final AllSettings settingsRepository;

    private final AllSharedPreferences allSharedPreferences;

    private final ANMLocationController anmLocationController;

    private final HTTPAgent httpAgent;

    public ValidateAssignmentHelper(SyncUtils syncUtils) {
        this.syncUtils = syncUtils;
        userService = CoreLibrary.getInstance().context().userService();
        planDefinitionRepository = CoreLibrary.getInstance().context().getPlanDefinitionRepository();
        locationRepository = CoreLibrary.getInstance().context().getLocationRepository();
        settingsRepository = CoreLibrary.getInstance().context().allSettings();
        allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        anmLocationController = CoreLibrary.getInstance().context().anmLocationController();
        httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
    }

    protected Set<String> getExistingJurisdictions() {
        return new HashSet<>(locationRepository.getAllLocationIds());
    }

    public void validateUserAssignment() {
        boolean keycloakConfigured = allSharedPreferences.getBooleanPreference(IS_KEYCLOAK_CONFIGURED);
        if (!keycloakConfigured) {
            return;
        }
        try {
            String assignment = getUserAssignment();
            if (StringUtils.isNotBlank(assignment)) {
                UserAssignmentDTO currentUserAssignment = gson.fromJson(assignment, UserAssignmentDTO.class);
                Set<Long> existingOrganizations = userService.fetchOrganizations();
                Set<String> existingJurisdictions = getExistingJurisdictions();
                Set<String> existingPlans = planDefinitionRepository.findAllPlanDefinitionIds();
                boolean newAssignments = hasNewAssignments(currentUserAssignment, existingOrganizations, existingJurisdictions);
                UserAssignmentDTO removedAssignments = processRemovedAssignments(currentUserAssignment, existingOrganizations, existingJurisdictions, existingPlans);
                if (newAssignments) {
                    logoff(R.string.account_new_assignment_logged_off);
                    resetSync();
                } else if (removedAssignments.isRemoved()) {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_ASSIGNMENT_REMOVED);
                    intent.putExtra(ASSIGNMENTS_REMOVED, removedAssignments);
                    CoreLibrary.getInstance().context().applicationContext().sendBroadcast(intent);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void resetSync() {
        allSharedPreferences.savePreference(LOCATION_LAST_SYNC_DATE, "0");
        allSharedPreferences.savePreference(STRUCTURES_LAST_SYNC_DATE, "0");
        allSharedPreferences.savePreference(PLAN_LAST_SYNC_DATE, "0");
        allSharedPreferences.savePreference(TASK_LAST_SYNC_DATE, "0");
        allSharedPreferences.saveLastSyncDate(0);
    }

    private void logoff(@StringRes int message) throws AuthenticatorException, OperationCanceledException, IOException {
        //skip logoff if user is already logged off
        if (!userService.hasSessionExpired()) {
            Timber.i("Logging out user");
            syncUtils.logoutUser(message);
        } else {
            Timber.i("User already logged out");
        }
    }


    private UserAssignmentDTO processRemovedAssignments(UserAssignmentDTO currentUserAssignment, Set<Long> existingOrganizations, Set<String> existingJurisdictions, Set<String> existingPlans) throws AuthenticatorException, OperationCanceledException, IOException {
        Set<Long> ids = new HashSet<>(existingOrganizations);
        Set<String> prefsIds = new HashSet<>(existingJurisdictions);
        existingJurisdictions.removeAll(currentUserAssignment.getJurisdictions());
        existingOrganizations.removeAll(currentUserAssignment.getOrganizationIds());
        existingPlans.removeAll(currentUserAssignment.getPlans());

        LocationTree locationTree = gson.fromJson(settingsRepository.fetchANMLocation(), LocationTree.class);
        boolean removed = false;
        UserAssignmentDTO removedAssignments = UserAssignmentDTO.builder().jurisdictions(existingJurisdictions).organizationIds(existingOrganizations).plans(existingPlans).build();

        if (!Utils.isEmptyCollection(removedAssignments.getPlans())) {
            planDefinitionRepository.deletePlans(removedAssignments.getPlans());
            removed = true;
        }

        if (!Utils.isEmptyCollection(removedAssignments.getOrganizationIds())) {
            ids.removeAll(removedAssignments.getOrganizationIds());
            userService.saveOrganizations(new ArrayList<>(ids));
            removed = true;
        }
        if (!Utils.isEmptyCollection(removedAssignments.getJurisdictions())) {
            locationRepository.deleteLocations(removedAssignments.getJurisdictions());
            removeLocationsFromHierarchy(locationTree, removedAssignments.getJurisdictions());
            prefsIds.removeAll(removedAssignments.getJurisdictions());
            userService.saveJurisdictionIds(prefsIds);
            removed = true;
        }
        return removedAssignments.toBuilder().isRemoved(removed).build();

    }

    @VisibleForTesting
    protected void removeLocationsFromHierarchy(LocationTree locationTree, Set<String> removedAssignments) throws AuthenticatorException, OperationCanceledException, IOException {
        for (String removedAssignment : removedAssignments) {
            locationTree.deleteLocation(removedAssignment);
        }
        settingsRepository.saveANMLocation(gson.toJson(locationTree));
        anmLocationController.evict();
        String defaultLocationUuid = allSharedPreferences.fetchDefaultLocalityId(allSharedPreferences.fetchRegisteredANM());
        if (StringUtils.isNotBlank(defaultLocationUuid) && removedAssignments.contains(defaultLocationUuid)) {
            logoff(R.string.default_location_revoked_logged_off);
        }
    }


    private boolean hasNewAssignments(UserAssignmentDTO currentUserAssignment, Set<Long> existingOrganizations, Set<String> existingJurisdictions) {
        if (existingJurisdictions.isEmpty()) {
            LocationTree locationTree = gson.fromJson(settingsRepository.fetchANMLocation(), LocationTree.class);
            for (String location : currentUserAssignment.getJurisdictions()) {
                if (!locationTree.hasLocation(location)) return true;
            }
            return false;
        }

        return !existingOrganizations.containsAll(currentUserAssignment.getOrganizationIds()) || !existingJurisdictions.containsAll(currentUserAssignment.getJurisdictions());
    }

    private String getUserAssignment() throws NoHttpResponseException {
        if (httpAgent == null) {
            throw new IllegalArgumentException(USER_ASSIGNMENT_URL + " http agent is null");
        }
        String baseUrl = getFormattedBaseUrl();
        Response<String> resp = httpAgent.fetch(MessageFormat.format("{0}{1}", baseUrl, USER_ASSIGNMENT_URL));
        if (resp.isFailure()) {
            throw new NoHttpResponseException(USER_ASSIGNMENT_URL + " not returned data");
        }
        return resp.payload();
    }
}
