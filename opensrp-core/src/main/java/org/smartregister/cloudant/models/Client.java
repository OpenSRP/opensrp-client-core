package org.smartregister.cloudant.models;

import com.cloudant.sync.datastore.DocumentRevision;

import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.clientandeventmodel.User;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by koros on 3/16/16.
 */
public class Client extends org.smartregister.clientandeventmodel.Client {

    public static final String type_key = "type";
    public static final String addresses_key = "addresses";
    public static final String attributes_key = "attributes";
    public static final String base_entity_id_key = "baseEntityId";
    public static final String birth_date_key = "birthdate";
    public static final String birth_date_approx_key = "birthdateApprox";
    public static final String creator_key = "creator";
    public static final String date_created_key = "dateCreated";
    public static final String date_voided_key = "dateVoided";
    public static final String date_edited_key = "dateEdited";
    public static final String death_date_key = "deathdate";
    public static final String firstname_key = "firstName";
    public static final String identifiers_key = "identifiers";
    public static final String gender_key = "gender";
    public static final String lastname_key = "lastName";
    public static final String middlename_key = "middleName";
    public static final String voider_key = "voider";
    public static final String void_reason_key = "voidReason";
    public static final String editor_key = "editor";
    public static final String death_date_approx_key = "deathdateApprox";
    public static final String voided_key = "voided";
    public static final String relationships_key = "relationships";
    static final String DOC_TYPE = "Client";
    // this is the revision in the database representing this task
    private DocumentRevision rev;

    public Client() {
    }

    public Client(org.smartregister.clientandeventmodel.Client client) {

        setAddresses(client.getAddresses());
        setAttributes(client.getAttributes());
        setBaseEntityId(client.getBaseEntityId());
        setBirthdate(client.getBirthdate());
        setBirthdateApprox(client.getBirthdateApprox());
        setCreator(client.getCreator());
        setDateCreated(client.getDateCreated());
        setDeathdateApprox(client.getDeathdateApprox());
        setDateVoided(client.getDateVoided());
        setDateEdited(client.getDateEdited());
        setDeathdate(client.getDeathdate());
        setFirstName(client.getFirstName());
        setIdentifiers(client.getIdentifiers());
        setGender(client.getGender());
        setLastName(client.getLastName());
        setMiddleName(client.getMiddleName());
        setVoider(client.getVoider());
        setVoidReason(client.getVoidReason());
        setVoided(client.getVoided());
        setEditor(client.getEditor());
        setRelationalBaseEntityId(client.getRelationalBaseEntityId());
        setRelationships(client.getRelationships());
        setType(DOC_TYPE);
    }

    public static Client fromRevision(DocumentRevision rev) throws ParseException {
        Client client = new Client();
        client.rev = rev;
        // this could also be done by a fancy object mapper
        Map<String, Object> map = rev.asMap();
        if (map.containsKey(type_key) && map.get(type_key).equals(Client.DOC_TYPE)) {
            //client.setType((String) map.get(type_key));
            if (map.get(addresses_key) != null) {
                client.setAddresses((List<Address>) map.get(addresses_key));
            }
            if (map.get(attributes_key) != null) {
                client.setAttributes((Map<String, Object>) map.get(attributes_key));
            }

            if (map.get(base_entity_id_key) != null) {
                client.setBaseEntityId((String) map.get(base_entity_id_key));
            }
            //the date is being saved as long
            if (map.get(birth_date_key) != null) {
                Date birthDate = DateUtil.toDate(map.get(birth_date_key));
                if (birthDate != null) {
                    client.setBirthdate(birthDate);
                }
            }
            if (map.get(birth_date_approx_key) != null) {
                client.setBirthdateApprox((Boolean) map.get(birth_date_approx_key));
            }
            if (map.get(creator_key) != null) {
                client.setCreator((User) map.get(creator_key));
            }
            if (map.get(date_created_key) != null) {
                Date dateCreated = DateUtil.toDate(map.get(date_created_key));
                if (dateCreated != null) {
                    client.setDateCreated(dateCreated);
                }
            }
            if (map.get(date_voided_key) != null) {
                Date dateVoided = DateUtil.toDate(map.get(date_voided_key));
                if (dateVoided != null) {
                    client.setDateVoided(dateVoided);
                }
            }
            if (map.get(date_edited_key) != null) {
                Date dateEdited = DateUtil.toDate(map.get(date_edited_key));
                if (dateEdited != null) {
                    client.setDateEdited(dateEdited);
                }
            }
            if (map.get(death_date_key) != null) {
                Date deathDate = DateUtil.toDate(map.get(death_date_key));
                if (deathDate != null) {
                    client.setDeathdate(deathDate);
                }
            }
            if (map.get(firstname_key) != null) {
                client.setFirstName((String) map.get(firstname_key));
            }
            if (map.get(identifiers_key) != null) {
                client.setIdentifiers((Map<String, String>) map.get(identifiers_key));
            }
            if (map.get(gender_key) != null) {
                client.setGender((String) map.get(gender_key));
            }
            if (map.get(lastname_key) != null) {
                client.setLastName((String) map.get(lastname_key));
            }
            if (map.get(middlename_key) != null) {
                client.setMiddleName((String) map.get(middlename_key));
            }
            if (map.get(voider_key) != null) {
                client.setVoider((User) map.get(voider_key));
            }
            if (map.get(void_reason_key) != null) {
                client.setVoidReason((String) map.get(void_reason_key));
            }
            if (map.get(editor_key) != null) {
                client.setEditor((User) map.get(editor_key));
            }
            if (map.get(death_date_approx_key) != null) {
                client.setDeathdateApprox((Boolean) map.get(death_date_approx_key));
            }
            if (map.get(voided_key) != null) {
                client.setVoided((Boolean) map.get(voided_key));
            }
            if (map.get(relationships_key) != null) {
                client.setRelationships((Map<String, List<String>>) map.get(relationships_key));
            }
            return client;
        }
        return null;
    }

    public void setRev(DocumentRevision rev) {
        this.rev = rev;
    }

    public DocumentRevision getDocumentRevision() {
        return rev;
    }

    public Map<String, Object> asMap() {
        // this could also be done by a fancy object mapper
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(type_key, type);
        if (getAddresses() != null) {
            map.put(addresses_key, getAddresses());
        }
        if (getAttributes() != null) {
            map.put(attributes_key, getAttributes());
        }
        if (getBaseEntityId() != null) {
            map.put(base_entity_id_key, getBaseEntityId());
        }
        if (getBirthdate() != null) {
            map.put(birth_date_key, DateUtil.fromDate(getBirthdate()));
        }
        if (getBirthdateApprox() != null) {
            map.put(birth_date_approx_key, getBirthdateApprox());
        }
        if (getCreator() != null) {
            map.put(creator_key, getCreator());
        }
        if (getDateCreated() != null) {
            map.put(date_created_key, DateUtil.fromDate(getDateCreated()));
        }
        if (getDateVoided() != null) {
            map.put(date_voided_key, DateUtil.fromDate(getDateVoided()));
        }
        if (getDateEdited() != null) {
            map.put(date_edited_key, DateUtil.fromDate(getDateEdited()));
        }
        if (getDeathdate() != null) {
            map.put(death_date_key, DateUtil.fromDate(getDeathdate()));
        }
        if (getFirstName() != null) {
            map.put(firstname_key, getFirstName());
        }
        if (getIdentifiers() != null) {
            map.put(identifiers_key, getIdentifiers());
        }
        if (getGender() != null) {
            map.put(gender_key, getGender());
        }
        if (getLastName() != null) {
            map.put(lastname_key, getLastName());
        }
        if (getMiddleName() != null) {
            map.put(middlename_key, getMiddleName());
        }
        if (getVoider() != null) {
            map.put(voider_key, getVoider());
        }
        if (getVoidReason() != null) {
            map.put(void_reason_key, getVoidReason());
        }
        if (getEditor() != null) {
            map.put(editor_key, getEditor());
        }
        if (getDeathdateApprox() != null) {
            map.put(death_date_approx_key, getDeathdateApprox());
        }
        if (getVoided() != null) {
            map.put(voided_key, getVoided());
        }
        if (getRelationships() != null) {
            map.put(relationships_key, getRelationships());
        }
        return map;
    }

}
