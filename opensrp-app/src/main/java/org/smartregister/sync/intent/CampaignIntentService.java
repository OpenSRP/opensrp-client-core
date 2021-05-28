package org.smartregister.sync.intent;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Campaign;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.CampaignRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;
import org.smartregister.util.Utils;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.AllConstants.CAMPAIGNS;

public class CampaignIntentService extends BaseSyncIntentService {
    public static final String CAMPAIGN_URL = "/rest/campaign/";
    private static final String TAG = "CampaignIntentService";
    private CampaignRepository campaignRepository;
    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
    public static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter()).create();

    public CampaignIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        syncCampaigns();
    }

    protected void syncCampaigns() {
        try {
            String campaignsResponse = fetchCampaigns();
            List<String> allowedCampaigns = Arrays.asList(allSharedPreferences.getPreference(CAMPAIGNS).split(","));

            List<Campaign> campaigns = gson.fromJson(campaignsResponse, new TypeToken<List<Campaign>>() {
            }.getType());

            for (Campaign campaign : campaigns) {
                try {
                    if (campaign.getIdentifier() != null && allowedCampaigns.contains(campaign.getIdentifier())) {
                        campaignRepository.addOrUpdate(campaign);
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String fetchCampaigns() throws NoHttpResponseException {
        Context opensrpContext = CoreLibrary.getInstance().context();

        HTTPAgent httpAgent = opensrpContext.getHttpAgent();
        if (httpAgent == null) {
            sendBroadcast(Utils.completeSync(FetchStatus.noConnection));
            throw new IllegalArgumentException(CAMPAIGN_URL + " http agent is null");
        }

        String baseUrl = opensrpContext.configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        String url = baseUrl + CAMPAIGN_URL;

        Response resp = httpAgent.fetch(url);
        if (resp.isFailure()) {
            sendBroadcast(Utils.completeSync(FetchStatus.nothingFetched));
            throw new NoHttpResponseException(CAMPAIGN_URL + " not returned data");
        }

        return resp.payload().toString();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        campaignRepository = CoreLibrary.getInstance().context().getCampaignRepository();
        return super.onStartCommand(intent, flags, startId);
    }
}
