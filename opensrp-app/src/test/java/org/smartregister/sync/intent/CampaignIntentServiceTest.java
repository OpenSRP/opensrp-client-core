package org.smartregister.sync.intent;

import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Campaign;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.CampaignRepository;
import org.smartregister.service.HTTPAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vincent Karuri on 02/03/2021
 */
public class CampaignIntentServiceTest extends BaseUnitTest {

    @Captor
    private ArgumentCaptor<Intent> intentArgumentCaptor;
    @Captor
    private ArgumentCaptor<Campaign> campaignArgumentCaptor;

    private CampaignIntentService campaignIntentService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        campaignIntentService = Mockito.spy(new CampaignIntentService());
        Mockito.doNothing().when(campaignIntentService).sendBroadcast(ArgumentMatchers.any(Intent.class));
    }

    @Test
    public void testOnHandleIntentShouldShowErrorMessageIfFetchFails() throws Exception {
        Context openSRPContext = CoreLibrary.getInstance().context();
        openSRPContext.allSharedPreferences().savePreference(AllConstants.DRISHTI_BASE_URL, "http//:dummy-url");

        HTTPAgent httpAgent = Mockito.mock(HTTPAgent.class);
        Mockito.doReturn(new Response<String>(ResponseStatus.failure, null)).when(httpAgent).fetch(ArgumentMatchers.anyString());
        ReflectionHelpers.setField(openSRPContext, "httpAgent", httpAgent);

        Whitebox.invokeMethod(campaignIntentService, "onHandleIntent", null);

        Mockito.verify(campaignIntentService).sendBroadcast(intentArgumentCaptor.capture());
        Assert.assertEquals(FetchStatus.nothingFetched,
                intentArgumentCaptor.getValue()
                        .getSerializableExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS));
    }


    @Test
    public void testOnHandleIntentShouldSaveCampaigns() throws Exception {
        final String CAMPAIGN1 = "campaign1";
        final String CAMPAIGN2 = "campaign2";

        List<Campaign> expectedCampaigns = new ArrayList<>();
        Campaign campaign = new Campaign();
        campaign.setIdentifier(CAMPAIGN1);
        expectedCampaigns.add(campaign);
        campaign = new Campaign();
        campaign.setIdentifier(CAMPAIGN2);
        expectedCampaigns.add(campaign);
        campaign = new Campaign();
        campaign.setIdentifier("campaign3");
        expectedCampaigns.add(campaign);

        String payload = CampaignIntentService.gson.toJson(expectedCampaigns, new TypeToken<List<Campaign>>() {
        }.getType());

        Context openSRPContext = CoreLibrary.getInstance().context();
        AllSharedPreferences allSharedPreferences = openSRPContext.allSharedPreferences();
        allSharedPreferences.savePreference(AllConstants.DRISHTI_BASE_URL, "http//:dummy-url");
        allSharedPreferences.savePreference(AllConstants.CAMPAIGNS, TextUtils.join(",", Arrays.asList(CAMPAIGN1, CAMPAIGN2)));

        HTTPAgent httpAgent = Mockito.mock(HTTPAgent.class);
        Mockito.doReturn(new Response<>(ResponseStatus.success, payload)).when(httpAgent).fetch(ArgumentMatchers.anyString());
        ReflectionHelpers.setField(openSRPContext, "httpAgent", httpAgent);

        CampaignRepository campaignRepository = Mockito.mock(CampaignRepository.class);
        ReflectionHelpers.setField(campaignIntentService, "campaignRepository", campaignRepository);

        Whitebox.invokeMethod(campaignIntentService, "onHandleIntent", null);

        Mockito.verify(campaignRepository, Mockito.times(2)).addOrUpdate(campaignArgumentCaptor.capture());

        List<Campaign> actualCampaigns = campaignArgumentCaptor.getAllValues();
        Assert.assertEquals(2, actualCampaigns.size());

        Set<String> identifiers = new HashSet<String>() {{
            add(CAMPAIGN1);
            add(CAMPAIGN2);
        }};
        Assert.assertTrue(identifiers.contains(CAMPAIGN1));
        Assert.assertTrue(identifiers.contains(CAMPAIGN2));
    }
}