package org.smartregister.authorizer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.AllConstants;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;

import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class P2PSyncAuthorizationServiceTest {

    private P2PSyncAuthorizationService p2PSyncAuthorizationService;
    private String myDeviceTeamId = "90392-232532-dsfsdf";

    @Before
    public void setUp() throws Exception {
        p2PSyncAuthorizationService = Mockito.spy(new P2PSyncAuthorizationService(myDeviceTeamId));
    }

    @Test
    public void authorizeConnectionShouldCallOnConnectionAuthorizedWhenPeerDeviceTeamIdIsEqualsCurrentDeviceTeamId() {
        P2PAuthorizationService.AuthorizationCallback authorizationCallback = Mockito.mock(P2PAuthorizationService.AuthorizationCallback.class);

        HashMap<String, Object> peerDeviceMap = new HashMap<>();
        peerDeviceMap.put(AllConstants.PeerToPeer.KEY_TEAM_ID, "90392-232532-dsfsdf");

        p2PSyncAuthorizationService.authorizeConnection(peerDeviceMap, authorizationCallback);
        Mockito.verify(authorizationCallback, Mockito.times(1)).onConnectionAuthorized();
    }

    @Test
    public void authorizeConnectionShouldCallOnConnectionAuthorizationRejectedWhenPeerDeviceTeamIdsAreNotEqual() {
        P2PAuthorizationService.AuthorizationCallback authorizationCallback = Mockito.mock(P2PAuthorizationService.AuthorizationCallback.class);

        HashMap<String, Object> peerDeviceMap = new HashMap<>();
        peerDeviceMap.put(AllConstants.PeerToPeer.KEY_TEAM_ID, "different-team_id");

        p2PSyncAuthorizationService.authorizeConnection(peerDeviceMap, authorizationCallback);
        Mockito.verify(authorizationCallback, Mockito.times(1)).onConnectionAuthorizationRejected(ArgumentMatchers.eq("Incorrect authorization details provided"));
    }
}