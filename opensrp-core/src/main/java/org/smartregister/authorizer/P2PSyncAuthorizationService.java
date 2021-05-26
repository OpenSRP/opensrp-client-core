package org.smartregister.authorizer;

import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 22/03/2019
 */

public class P2PSyncAuthorizationService implements P2PAuthorizationService {

    private Map<String, Object> authorizationDetails = new HashMap<>();

    public P2PSyncAuthorizationService(@NonNull String teamId) {
        authorizationDetails.put(AllConstants.PeerToPeer.KEY_TEAM_ID, teamId);
    }

    @Override
    public void authorizeConnection(@NonNull final Map<String, Object> peerDeviceMap, @NonNull final AuthorizationCallback authorizationCallback) {
        getAuthorizationDetails(new OnAuthorizationDetailsProvidedCallback() {
            @Override
            public void onAuthorizationDetailsProvided(@NonNull Map<String, Object> map) {
                Object peerDeviceTeamId = peerDeviceMap.get(AllConstants.PeerToPeer.KEY_TEAM_ID);
                if (peerDeviceTeamId != null && peerDeviceTeamId instanceof String
                        && ((String) peerDeviceTeamId).equals(map.get(AllConstants.PeerToPeer.KEY_TEAM_ID))) {
                    authorizationCallback.onConnectionAuthorized();
                } else {
                    authorizationCallback.onConnectionAuthorizationRejected("Incorrect authorization details provided");
                }
            }
        });
    }

    @Override
    public void getAuthorizationDetails(@NonNull OnAuthorizationDetailsProvidedCallback onAuthorizationDetailsProvidedCallback) {
            // Todo: Add the authorization details here
            onAuthorizationDetailsProvidedCallback.onAuthorizationDetailsProvided(authorizationDetails);
    }
}
