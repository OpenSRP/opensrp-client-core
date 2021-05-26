package org.smartregister.sync.helper;

import org.json.JSONException;
import org.junit.Test;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncFilter;

import static org.junit.Assert.assertEquals;

/**
 * Created by Richard Kareko on 11/17/20.
 */

public class SyncableJSONObjectTest extends BaseRobolectricUnitTest {

    @Test
    public void testConstructor() throws JSONException {
        String username = "testuser";
        CoreLibrary.getInstance().context().allSharedPreferences().updateANMUserName(username);
        CoreLibrary.getInstance().context().allSharedPreferences().saveDefaultLocalityId(username, "location-id1");
        CoreLibrary.getInstance().context().allSharedPreferences().saveDefaultTeam(username, "testteam");
        CoreLibrary.getInstance().context().allSharedPreferences().saveDefaultTeamId(username, "test-team-id");
        SyncableJSONObject syncableJSONObject = new SyncableJSONObject("{\"name\" : \"testObject\"}");

        assertEquals("testObject", syncableJSONObject.getString("name"));
        assertEquals("testuser", syncableJSONObject.getString(SyncFilter.PROVIDER.value()));
        assertEquals("location-id1", syncableJSONObject.getString(SyncFilter.LOCATION.value()));
        assertEquals("testteam", syncableJSONObject.getString(SyncFilter.TEAM.value()));
        assertEquals("test-team-id", syncableJSONObject.getString(SyncFilter.TEAM_ID.value()));
    }
}
