package org.smartregister.cloudant.models;

import com.cloudant.sync.datastore.DocumentRevision;

import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.clientandeventmodel.User;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by koros on 3/16/16.
 */
public class Event extends org.smartregister.clientandeventmodel.Event {

    public static final String type_key = "type";
    public static final String date_created_key = "dateCreated";
    public static final String voided_key = "voided";
    public static final String base_entity_id_key = "baseEntityId";
    public static final String identifiers_key = "identifiers";
    public static final String editor_key = "editor";
    public static final String creator_key = "creator";
    public static final String date_edited_key = "dateEdited";
    public static final String voider_key = "voider";
    public static final String date_voided_key = "dateVoided";
    public static final String void_reason_key = "voidReason";
    public static final String details_key = "voidReason";
    public static final String entity_type_key = "entityType";
    public static final String form_submission_id_key = "formSubmissionId";
    public static final String location_id_key = "locationId";
    public static final String event_date_key = "eventDate";
    public static final String event_type_key = "eventType";
    public static final String event_id_key = "eventId";
    public static final String obs_key = "obs";
    public static final String provider_key = "providerId";
    public static final String version_key = "version";
    static final String DOC_TYPE = "Event";
    // this is the revision in the database representing this task
    private DocumentRevision rev;
    private transient String type = DOC_TYPE;

    public Event() {
    }

    public Event(org.smartregister.clientandeventmodel.Event event) {
        setType(type);
        setDateCreated(event.getDateCreated());
        setVoided(event.getVoided());
        setBaseEntityId(event.getBaseEntityId());
        setIdentifiers(event.getIdentifiers());
        setEditor(event.getEditor());
        setCreator(event.getCreator());
        setDateEdited(event.getDateEdited());
        setVoider(event.getVoider());
        setDateVoided(event.getDateVoided());
        setVoidReason(event.getVoidReason());
        setDetails(event.getDetails());
        setEntityType(event.getEntityType());
        setFormSubmissionId(event.getFormSubmissionId());
        setLocationId(event.getLocationId());
        setEventDate(event.getEventDate());
        setEventType(event.getEventType());
        setEventId(event.getEventId());
        setObs(event.getObs());
        setProviderId(event.getProviderId());
        setVersion(event.getVersion());
    }

    public static Event fromRevision(DocumentRevision rev) throws ParseException {
        Event event = new Event();
        event.rev = rev;
        // this could also be done by a fancy object mapper
        Map<String, Object> map = rev.asMap();
        if (map.containsKey(type_key) && map.get(type_key).equals(Event.DOC_TYPE)) {
            // event.setType((String) map.get(type_key));
            if (map.get(date_created_key) != null) {
                Date dateCreated = DateUtil.toDate(map.get(date_created_key));
                if (dateCreated != null) {
                    event.setDateCreated(dateCreated);
                }
            }
            if (map.get(voided_key) != null) {
                event.setVoided((Boolean) map.get(voided_key));
            }
            if (map.get(base_entity_id_key) != null) {
                event.setBaseEntityId((String) map.get(base_entity_id_key));
            }
            if (map.get(editor_key) != null) {
                event.setEditor((User) map.get(editor_key));
            }
            if (map.get(creator_key) != null) {
                event.setCreator((User) map.get(creator_key));
            }
            if (map.get(date_edited_key) != null) {
                Date dateEdited = DateUtil.toDate(map.get(date_edited_key));
                if (dateEdited != null) {
                    event.setDateEdited(dateEdited);
                }
            }
            if (map.get(voider_key) != null) {
                event.setVoider((User) map.get(voider_key));
            }
            if (map.get(date_voided_key) != null) {
                Date dateVoided = DateUtil.toDate(map.get(date_voided_key));
                if (dateVoided != null) {
                    event.setDateVoided(dateVoided);
                }
            }
            if (map.get(void_reason_key) != null) {
                event.setVoidReason((String) map.get(void_reason_key));
            }
            if (map.get(details_key) != null) {
                event.setDetails((Map<String, String>) map.get(details_key));
            }
            if (map.get(entity_type_key) != null) {
                event.setEntityType((String) map.get(entity_type_key));
            }
            if (map.get(form_submission_id_key) != null) {
                event.setFormSubmissionId((String) map.get(form_submission_id_key));
            }
            if (map.get(location_id_key) != null) {
                event.setLocationId((String) map.get(location_id_key));
            }
            if (map.get(event_type_key) != null)
            // event.setEventDate((Date) map.get(event_date_key));
            {
                event.setEventType((String) map.get(event_type_key));
            }
            if (map.get(event_id_key) != null) {
                event.setEventId((String) map.get(event_id_key));
            }
            if (map.get(obs_key) != null) {
                event.setObs((List<Obs>) map.get(map.get(obs_key)));
            }
            if (map.get(provider_key) != null) {
                event.setProviderId((String) map.get(provider_key));
            }
            //event.setVersion((String) map.get(version_key));
            return event;
        }
        return null;
    }

    public DocumentRevision getDocumentRevision() {
        return rev;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> asMap() {
        // this could also be done by a fancy object mapper

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(type_key, type);
        if (getDateCreated() != null) {
            map.put(date_created_key, DateUtil.fromDate(getDateCreated()));
        }
        if (getVoided() != null) {
            map.put(voided_key, getVoided());
        }
        if (getBaseEntityId() != null) {
            map.put(base_entity_id_key, getBaseEntityId());
        }
        if (getIdentifiers() != null) {
            map.put(identifiers_key, getIdentifiers());
        }
        if (getEditor() != null) {
            map.put(editor_key, getEditor());
        }
        if (getCreator() != null) {
            map.put(creator_key, getCreator());
        }
        if (getDateEdited() != null) {
            map.put(date_edited_key, DateUtil.fromDate(getDateEdited()));
        }
        if (getVoider() != null) {
            map.put(voider_key, getVoider());
        }
        if (getDateVoided() != null) {
            map.put(date_voided_key, DateUtil.fromDate(getDateVoided()));
        }
        if (getVoidReason() != null) {
            map.put(void_reason_key, getVoidReason());
        }
        if (getDetails() != null) {
            map.put(details_key, getDetails());
        }
        if (getEntityType() != null) {
            map.put(entity_type_key, getEntityType());
        }
        if (getFormSubmissionId() != null) {
            map.put(form_submission_id_key, getFormSubmissionId());
        }
        if (getLocationId() != null) {
            map.put(location_id_key, getLocationId());
        }
        if (getEventDate() != null) {
            map.put(event_date_key, DateUtil.fromDate(getEventDate()));
        }
        if (getEventType() != null) {
            map.put(event_type_key, getEventType());
        }
        if (getEventId() != null) {
            map.put(event_id_key, getEventId());
        }
        if (getObs() != null) {
            map.put(obs_key, getObs());
        }
        if (getProviderId() != null) {
            map.put(provider_key, getProviderId());
        }
        map.put(version_key, getVersion());
        return map;
    }
}
