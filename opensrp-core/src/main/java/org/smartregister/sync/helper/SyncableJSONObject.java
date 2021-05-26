package org.smartregister.sync.helper;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncFilter;
import org.smartregister.repository.AllSharedPreferences;

/**
 * Created by ndegwamartin on 20/09/2018.
 */
public class SyncableJSONObject extends JSONObject {

    public SyncableJSONObject(String json) throws JSONException {
        super(json);

        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        String providerId = allSharedPreferences.fetchRegisteredANM();

        put(SyncFilter.PROVIDER.value(), providerId);
        put(SyncFilter.LOCATION.value(), allSharedPreferences.fetchDefaultLocalityId(providerId));
        put(SyncFilter.TEAM.value(), allSharedPreferences.fetchDefaultTeam(providerId));
        put(SyncFilter.TEAM_ID.value(), allSharedPreferences.fetchDefaultTeamId(providerId));

    }
}
