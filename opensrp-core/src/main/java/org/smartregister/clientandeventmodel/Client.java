package org.smartregister.clientandeventmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client extends BaseEntity {
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String middleName;
    @JsonProperty
    private String lastName;
    @JsonProperty
    private Date birthdate;
    @JsonProperty
    private Date deathdate;
    @JsonProperty
    private Boolean birthdateApprox;
    @JsonProperty
    private Boolean deathdateApprox;
    @JsonProperty
    private String gender;
    @JsonProperty
    private Map<String, List<String>> relationships;

    //This is an id field used to link a client to other clients or parent
    @JsonProperty
    private String relationalBaseEntityId;
    @JsonProperty
    private String clientType;

    @JsonProperty
    private String locationId;

    @JsonProperty
    private String teamId;

    @JsonProperty
    private String syncStatus;

    protected Client() {

    }

    public Client(String baseEntityId) {
        super(baseEntityId);
    }

    public Client(String baseEntityId, String firstName, String middleName, String lastName, Date
            birthdate, Date deathdate, Boolean birthdateApprox, Boolean deathdateApprox, String
                          gender) {
        super(baseEntityId);
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.deathdate = deathdate;
        this.birthdateApprox = birthdateApprox;
        this.deathdateApprox = deathdateApprox;
        this.gender = gender;
    }

    public Client(String baseEntityId, String firstName, String middleName, String lastName, Date
            birthdate, Date deathdate, Boolean birthdateApprox, Boolean deathdateApprox, String
                          gender, String identifierType, String identifier) {
        super(baseEntityId);
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.deathdate = deathdate;
        this.birthdateApprox = birthdateApprox;
        this.deathdateApprox = deathdateApprox;
        this.gender = gender;
        addIdentifier(identifierType, identifier);
    }

    public Client(String baseEntityId, String firstName, String middleName, String lastName, Date
            birthdate, Date deathdate, Boolean birthdateApprox, Boolean deathdateApprox, String
                          gender, List<Address> addresses, Map<String, String> identifiers, Map<String, Object>
                          attributes) {
        super(baseEntityId);
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.deathdate = deathdate;
        this.birthdateApprox = birthdateApprox;
        this.deathdateApprox = deathdateApprox;
        this.gender = gender;
        setIdentifiers(identifiers);
        setAddresses(addresses);
        setAttributes(attributes);
    }

    public Client(String baseEntityId, String firstName, String middleName, String lastName, Date
            birthdate, Date deathdate, Boolean birthdateApprox, Boolean deathdateApprox, String
                          gender, String clientType) {
        this(baseEntityId, firstName, middleName, lastName, birthdate, deathdate, birthdateApprox, deathdateApprox, gender);
        setClientType(clientType);
    }

    public Client(String baseEntityId, String firstName, String middleName, String lastName, Date
            birthdate, Date deathdate, Boolean birthdateApprox, Boolean deathdateApprox, String
                          gender, String identifierType, String identifier, String clientType) {
        this(baseEntityId, firstName, middleName, lastName, birthdate, deathdate, birthdateApprox, deathdateApprox, gender, identifierType, identifier);
        setClientType(clientType);
    }

    public Client(String baseEntityId, String firstName, String middleName, String lastName, Date
            birthdate, Date deathdate, Boolean birthdateApprox, Boolean deathdateApprox, String
                          gender, List<Address> addresses, Map<String, String> identifiers, Map<String, Object>
                          attributes, String clientType) {
        this(baseEntityId, firstName, middleName, lastName, birthdate, deathdate, birthdateApprox, deathdateApprox, gender, addresses, identifiers, attributes);
        setClientType(clientType);
    }

    public String getRelationalBaseEntityId() {
        return relationalBaseEntityId;
    }

    public void setRelationalBaseEntityId(String relationalBaseEntityId) {
        this.relationalBaseEntityId = relationalBaseEntityId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Date getDeathdate() {
        return deathdate;
    }

    public void setDeathdate(Date deathdate) {
        this.deathdate = deathdate;
    }

    public Boolean getBirthdateApprox() {
        return birthdateApprox;
    }

    public void setBirthdateApprox(Boolean birthdateApprox) {
        this.birthdateApprox = birthdateApprox;
    }

    public Boolean getDeathdateApprox() {
        return deathdateApprox;
    }

    public void setDeathdateApprox(Boolean deathdateApprox) {
        this.deathdateApprox = deathdateApprox;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Map<String, List<String>> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, List<String>> relationships) {
        this.relationships = relationships;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public Client withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public Client withMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public Client withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Client withName(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        return this;
    }

    public Client withBirthdate(Date birthdate, Boolean isApproximate) {
        this.birthdate = birthdate;
        this.birthdateApprox = isApproximate;
        return this;
    }

    public Client withDeathdate(Date deathdate, Boolean isApproximate) {
        this.deathdate = deathdate;
        this.deathdateApprox = isApproximate;
        return this;
    }

    public Client withGender(String gender) {
        this.gender = gender;
        return this;
    }

    public Client withGender(Gender gender) {
        this.gender = gender.name();
        return this;
    }

    public Client withClientType(String clientType) {
        this.clientType = clientType;
        return this;
    }

    /**
     * Overrides the existing data
     */
    public Client withRelationships(Map<String, List<String>> relationships) {
        this.relationships = relationships;
        return this;
    }

    public List<String> findRelatives(String relationshipType) {
        if (relationships == null) {
            relationships = new HashMap<>();
        }

        return relationships.get(relationshipType);
    }

    public void addRelationship(String relationType, String relativeEntityId) {
        if (relationships == null) {
            relationships = new HashMap<>();
        }

        List<String> relatives = findRelatives(relationType);
        if (relatives == null) {
            relatives = new ArrayList<>();
        }
        relatives.add(relativeEntityId);
        relationships.put(relationType, relatives);
    }

    public List<String> getRelationships(String relativeEntityId) {
        List<String> relations = new ArrayList<String>();
        for (Map.Entry<String, List<String>> rl : relationships.entrySet()) {
            if (rl.getValue().toString().equalsIgnoreCase(relativeEntityId)) {
                relations.add(rl.getKey());
            }
        }
        return relations;
    }


    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Client withSyncStatus(String syncStatus) {
        setSyncStatus(syncStatus);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "_id", "_rev");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "_id", "_rev");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

