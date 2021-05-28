package org.smartregister.sync.intent;

import android.content.Context;
import android.content.Intent;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.Response;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import timber.log.Timber;

/**
 * Created by keyman on 11/10/2017.
 */
public class ValidateIntentService extends BaseSyncIntentService {

    private Context context;
    private HTTPAgent httpAgent;
    private static final int FETCH_LIMIT = 100;
    private static final String VALIDATE_SYNC_PATH = "rest/validate/sync";
    private org.smartregister.Context openSRPContext = CoreLibrary.getInstance().context();

    private EventClientRepository eventClientRepository = getOpenSRPContext().getEventClientRepository();

    public ValidateIntentService() {
        super("ValidateIntentService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
        httpAgent = getOpenSRPContext().getHttpAgent();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            super.onHandleIntent(intent);
            int fetchLimit = FETCH_LIMIT;

            List<String> clientIds = eventClientRepository.getUnValidatedClientBaseEntityIds(fetchLimit);
            if (!clientIds.isEmpty()) {
                fetchLimit -= clientIds.size();
            }

            List<String> eventIds = new ArrayList<>();
            if (fetchLimit > 0) {
                eventIds = eventClientRepository.getUnValidatedEventFormSubmissionIds(fetchLimit);
            }

            JSONObject request = request(clientIds, eventIds);
            if (request == null) {
                return;
            }

            String baseUrl = getOpenSRPContext().configuration().dristhiBaseURL();
            if (baseUrl.endsWith(context.getString(R.string.url_separator))) {
                baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(context.getString(R.string.url_separator)));
            }

            String jsonPayload = request.toString();
            Response<String> response = httpAgent.postWithJsonResponse(
                    MessageFormat.format("{0}/{1}",
                            baseUrl,
                            VALIDATE_SYNC_PATH),
                    jsonPayload);
            if (response.isFailure() || StringUtils.isBlank(response.payload())) {
                Timber.e("Validation sync failed.");
                return;
            }

            JSONObject results = new JSONObject(response.payload());

            if (results.has(AllConstants.KEY.CLIENTS)) {
                JSONArray inValidClients = results.getJSONArray(AllConstants.KEY.CLIENTS);
                Set<String> invalidClientIds = filterArchivedClients(extractIds(inValidClients));
                for (String id : invalidClientIds) {
                    clientIds.remove(id);
                    eventClientRepository.markClientValidationStatus(id, false);
                }

                for (String clientId : clientIds) {
                    eventClientRepository.markClientValidationStatus(clientId, true);
                }
            }

            if (results.has(AllConstants.KEY.EVENTS)) {
                JSONArray inValidEvents = results.getJSONArray(AllConstants.KEY.EVENTS);
                Set<String> inValidEventIds = filterArchivedEvents(extractIds(inValidEvents));
                for (String inValidEventId : inValidEventIds) {
                    eventIds.remove(inValidEventId);
                    eventClientRepository.markEventValidationStatus(inValidEventId, false);
                }

                for (String eventId : eventIds) {
                    eventClientRepository.markEventValidationStatus(eventId, true);
                }
            }

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private Set<String> extractIds(JSONArray inValidClients) {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < inValidClients.length(); i++) {
            ids.add(inValidClients.optString(i));
        }
        return ids;
    }

    private Set<String> filterArchivedClients(Set<String> ids) {
        return eventClientRepository.fetchClientByBaseEntityIds(ids)
                .stream()
                .filter(c -> c.getDateVoided() == null)
                .map(Client::getBaseEntityId)
                .collect(Collectors.toSet());
    }

    private Set<String> filterArchivedEvents(Set<String> ids) {
        return eventClientRepository.getEventsByEventIds(ids)
                .stream()
                .filter(e -> e.getDateVoided() == null)
                .map(Event::getEventId)
                .collect(Collectors.toSet());
    }

    private JSONObject request(List<String> clientIds, List<String> eventIds) {
        try {

            JSONArray clientIdArray = null;
            if (!clientIds.isEmpty()) {
                clientIdArray = new JSONArray(clientIds);
            }

            JSONArray eventIdArray = null;
            if (!eventIds.isEmpty()) {
                eventIdArray = new JSONArray(eventIds);
            }

            if (clientIdArray != null || eventIdArray != null) {
                JSONObject request = new JSONObject();
                if (clientIdArray != null) {
                    request.put(AllConstants.KEY.CLIENTS, clientIdArray);
                }

                if (eventIdArray != null) {
                    request.put(AllConstants.KEY.EVENTS, eventIdArray);
                }

                return request;

            }

        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private org.smartregister.Context getOpenSRPContext() {
        return openSRPContext;
    }
}
