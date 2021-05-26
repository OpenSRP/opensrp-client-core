package org.smartregister.util;

import android.database.Cursor;
import androidx.core.util.Pair;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.view.activity.DrishtiApplication;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.gson;

/**
 * Created by samuelgithengi on 12/30/19.
 */
public class RecreateECUtil {

    private EventClientRepository eventClientRepository = new EventClientRepository();

    private ClientProcessorForJava clientProcessor = DrishtiApplication.getInstance().getClientProcessor();

    public Pair<List<Event>, List<Client>> createEventAndClients(SQLiteDatabase database, String tablename, String query, String[] params, String eventType, String entityType, FormTag formTag) {

        Table table = clientProcessor.getColumnMappings(tablename);
        if (table == null) {
            return null;
        }
        List<Event> events = new ArrayList<>();
        List<Client> clients = new ArrayList<>();
        List<Map<String, String>> savedEventClients = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, params);
            int columncount = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                Map<String, String> details = new HashMap<>();
                for (int i = 0; i < columncount; i++) {
                    details.put(cursor.getColumnName(i), cursor.getString(i));
                }
                savedEventClients.add(details);
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        for (Map<String, String> details : savedEventClients) {
            String entityId = details.get("base_entity_id");
            Event event = new Event();
            event.withBaseEntityId(entityId)
                    .withEventType(eventType)
                    .withEntityType(entityType)
                    .withFormSubmissionId(UUID.randomUUID().toString())
                    .withSyncStatus(BaseRepository.TYPE_Unsynced)
                    .withDateCreated(new Date());

            Client client = new Client(entityId).withSyncStatus(BaseRepository.TYPE_Unsynced);
            boolean eventChanged = false;
            boolean clientChanged = false;
            for (Column column : table.columns) {
                String value = details.get(column.column_name);
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                if (EventClientRepository.Table.event.name().equalsIgnoreCase(column.type)) {
                    populateEvent(column, value, event);
                    eventChanged = true;
                } else {
                    populateClient(column, value, client);
                    clientChanged = true;
                }
            }
            if (eventChanged) {
                tagEvent(event, formTag);
                events.add(event);
            }
            if (clientChanged) {
                clients.add(client);
            }

        }
        return new Pair<>(events, clients);
    }

    private void tagEvent(Event event, FormTag formTag) {
        event.setProviderId(formTag.providerId);
        event.setLocationId(formTag.locationId);
        event.setChildLocationId(formTag.locationId);
        event.setTeam(formTag.team);
        event.setTeamId(formTag.teamId);
        event.setClientApplicationVersion(formTag.appVersion);
        event.setClientApplicationVersionName(formTag.appVersionName);
        event.setClientDatabaseVersion(formTag.databaseVersion);
    }

    public void saveEventAndClients(Pair<List<Event>, List<Client>> eventClients, SQLiteDatabase sqLiteDatabase) {
        if (eventClients == null) {
            return;
        }
        if (eventClients.first != null) {
            JSONArray events;
            try {
                events = new JSONArray(gson.toJson(eventClients.first));
                Timber.d("saving %d events, %s ", eventClients.first.size(), events);
                eventClientRepository.batchInsertEvents(events, 0, sqLiteDatabase);
            } catch (JSONException e) {
                Timber.e(e);
            }

        }
        if (eventClients.second != null) {
            JSONArray clients;
            try {
                clients = new JSONArray(gson.toJson(eventClients.second));
                Timber.d("saving %d clients, %s", eventClients.second.size(), clients);
                eventClientRepository.batchInsertClients(clients, sqLiteDatabase);
            } catch (JSONException e) {
                Timber.e(e);
            }

        }

    }

    private void populateEvent(Column column, String value, Event event) {
        String field = column.json_mapping.field;
        if (field.equalsIgnoreCase("obs.fieldCode")) {
            event.addObs(new Obs().withFieldType(getFieldValue(column.json_mapping.formSubmissionField, column.json_mapping.concept))
                    .withFieldDataType(getFieldValue(column.dataType, "text"))
                    .withFieldCode(column.json_mapping.concept)
                    .withFormSubmissionField(column.json_mapping.formSubmissionField)
                    .withValue(value));

        } else if (field.startsWith("details.")) {
            event.addDetails(field.substring(field.indexOf(".") + 1), value);
        } else if (field.startsWith("identifiers.")) {
            event.addIdentifier(field.substring(field.indexOf(".") + 1), value);
        } else {
            setFieldValue(event, field, value);
        }

    }

    private void populateClient(Column column, String value, Client client) {
        String field = column.json_mapping.field;
        if (field.startsWith("attributes.")) {
            client.addAttribute(field.substring(field.indexOf(".") + 1), value);
        } else if (field.startsWith("identifiers.")) {
            client.addIdentifier(field.substring(field.indexOf(".") + 1), value);
        } else if (field.startsWith("relationships.")) {
            client.addRelationship(field.substring(field.indexOf(".") + 1), value);
        } else {
            setFieldValue(client, column.json_mapping.field, value);
        }

    }


    private void setFieldValue(Object instance, String fieldName, String value) {
        if (instance == null || StringUtils.isBlank(fieldName)) {
            return;
        }
        try {
            Field field = getField(instance.getClass(), fieldName);
            if (field != null) {

                field.setAccessible(true);
                if (field.getType().equals(Date.class)) {
                    Date date = new DateTime(value).toDate();
                    field.set(instance, date);
                } else if (field.getType().equals(Long.class)) {
                    field.set(instance, Long.valueOf(value));
                } else {
                    field.set(instance, value);
                }
            }
        } catch (IllegalAccessException e) {
            Timber.w(e);
        }
    }

    private Field getField(Class clazz, String fieldName) {
        if (clazz == null || StringUtils.isBlank(fieldName)) {
            return null;
        }

        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // No need to log this, log will be to big
        }
        if (field != null) {
            return field;
        }

        return getField(clazz.getSuperclass(), fieldName);
    }

    private String getFieldValue(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }


}
