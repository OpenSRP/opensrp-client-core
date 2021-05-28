package org.smartregister.sync.helper;

import android.content.Context;
import androidx.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;


/**
 * Created by ndegwamartin on 15/03/2018.
 */

public class ECSyncHelper {

    protected final EventClientRepository eventClientRepository;
    protected final Context context;

    protected AllSharedPreferences allSharedPreferences;

    protected static ECSyncHelper instance;

    public static ECSyncHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ECSyncHelper(context, CoreLibrary.getInstance().context().getEventClientRepository());
            instance.allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        }
        return instance;
    }

    @VisibleForTesting
    protected ECSyncHelper(Context context, EventClientRepository eventClientRepository) {
        this.context = context;
        this.eventClientRepository = eventClientRepository;
    }

    public boolean saveAllClientsAndEvents(JSONObject jsonObject) {
        try {
            if (jsonObject == null) {
                return false;
            }

            JSONArray events = jsonObject.has("events") ? jsonObject.getJSONArray("events") : new JSONArray();
            JSONArray clients = jsonObject.has("clients") ? jsonObject.getJSONArray("clients") : new JSONArray();

            batchSave(events, clients);


            return true;
        } catch (Exception e) {
            Timber.e(e);
            return false;
        }
    }

    public List<EventClient> allEventClients(long startSyncTimeStamp, long lastSyncTimeStamp) {
        try {
            return eventClientRepository.fetchEventClients(startSyncTimeStamp, lastSyncTimeStamp);
        } catch (Exception e) {
            Timber.e(e);
        }
        return new ArrayList<>();
    }

    public long getLastSyncTimeStamp() {
        return allSharedPreferences.fetchLastSyncDate(0);
    }

    public void updateLastSyncTimeStamp(long lastSyncTimeStamp) {
        allSharedPreferences.saveLastSyncDate(lastSyncTimeStamp);
    }

    public void updateLastCheckTimeStamp(long lastCheckTimeStamp) {
        allSharedPreferences.updateLastCheckTimeStamp(lastCheckTimeStamp);
    }

    public long getLastCheckTimeStamp() {
        return allSharedPreferences.fetchLastCheckTimeStamp();
    }

    public void batchSave(JSONArray events, JSONArray clients) {
        eventClientRepository.batchInsertClients(clients);
        eventClientRepository.batchInsertEvents(events, getLastSyncTimeStamp());
    }


    public List<EventClient> getEvents(Date lastSyncDate, String syncStatus) {
        try {
            return eventClientRepository.fetchEventClients(lastSyncDate, syncStatus);
        } catch (Exception e) {
            Timber.e(e);
        }
        return new ArrayList<>();
    }

    public List<EventClient> getEvents(List<String> formSubmissionIds) {
        try {
            return eventClientRepository.fetchEventClients(formSubmissionIds);
        } catch (Exception e) {
            Timber.e(e);
        }
        return new ArrayList<>();
    }

    public JSONObject getClient(String baseEntityId) {
        try {
            return eventClientRepository.getClientByBaseEntityId(baseEntityId);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public void addClient(String baseEntityId, JSONObject jsonObject) {
        try {
            eventClientRepository.addorUpdateClient(baseEntityId, jsonObject);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void addEvent(String baseEntityId, JSONObject jsonObject) {
        try {
            eventClientRepository.addEvent(baseEntityId, jsonObject);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void addEvent(String baseEntityId, JSONObject jsonObject, String syncStatus) {
        try {
            eventClientRepository.addEvent(baseEntityId, jsonObject, syncStatus);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public List<EventClient> allEvents(long startSyncTimeStamp, long lastSyncTimeStamp) {
        try {
            return eventClientRepository.fetchEventClients(startSyncTimeStamp, lastSyncTimeStamp);
        } catch (Exception e) {
            Timber.e(e);
        }
        return new ArrayList<>();
    }

    public void batchInsertClients(JSONArray clients) {
        eventClientRepository.batchInsertClients(clients);
    }

    public void batchInsertEvents(JSONArray events) {
        eventClientRepository.batchInsertEvents(events, getLastSyncTimeStamp());
    }

    public <T> T convert(JSONObject jo, Class<T> t) {
        return eventClientRepository.convert(jo, t);
    }

    public JSONObject convertToJson(Object object) {
        return eventClientRepository.convertToJson(object);
    }

    public boolean deleteClient(String baseEntityId) {
        return eventClientRepository.deleteClient(baseEntityId);
    }

}

