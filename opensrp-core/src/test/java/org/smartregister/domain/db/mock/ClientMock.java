package org.smartregister.domain.db.mock;

import org.joda.time.DateTime;
import org.smartregister.domain.Address;
import org.smartregister.domain.Client;

import java.util.List;
import java.util.Map;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class ClientMock extends Client {
    protected ClientMock() {
        super();
    }

    public ClientMock(String baseEntityId) {
        super(baseEntityId);
    }

    public ClientMock(String baseEntityId, String firstName, String middleName, String lastName, DateTime birthdate, DateTime deathdate, Boolean birthdateApprox, Boolean deathdateApprox, String gender) {
        super(baseEntityId, firstName, middleName, lastName, birthdate, deathdate, birthdateApprox, deathdateApprox, gender);
    }

    public ClientMock(String baseEntityId, String firstName, String middleName, String lastName, DateTime birthdate, DateTime deathdate, Boolean birthdateApprox, Boolean deathdateApprox, String gender, String identifierType, String identifier) {
        super(baseEntityId, firstName, middleName, lastName, birthdate, deathdate, birthdateApprox, deathdateApprox, gender, identifierType, identifier);
    }

    public ClientMock(String baseEntityId, String firstName, String middleName, String lastName, DateTime birthdate, DateTime deathdate, Boolean birthdateApprox, Boolean deathdateApprox, String gender, List<Address> addresses, Map<String, String> identifiers, Map<String, Object> attributes) {
        super(baseEntityId, firstName, middleName, lastName, birthdate, deathdate, birthdateApprox, deathdateApprox, gender, addresses, identifiers, attributes);
    }

    @Override
    public String getFirstName() {
        return super.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        super.setFirstName(firstName);
    }

    @Override
    public String getMiddleName() {
        return super.getMiddleName();
    }

    @Override
    public void setMiddleName(String middleName) {
        super.setMiddleName(middleName);
    }

    @Override
    public String getLastName() {
        return super.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        super.setLastName(lastName);
    }

    @Override
    public DateTime getBirthdate() {
        return super.getBirthdate();
    }

    @Override
    public void setBirthdate(DateTime birthdate) {
        super.setBirthdate(birthdate);
    }

    @Override
    public DateTime getDeathdate() {
        return super.getDeathdate();
    }

    @Override
    public void setDeathdate(DateTime deathdate) {
        super.setDeathdate(deathdate);
    }

    @Override
    public Boolean getBirthdateApprox() {
        return super.getBirthdateApprox();
    }

    @Override
    public void setBirthdateApprox(Boolean birthdateApprox) {
        super.setBirthdateApprox(birthdateApprox);
    }

    @Override
    public Boolean getDeathdateApprox() {
        return super.getDeathdateApprox();
    }

    @Override
    public void setDeathdateApprox(Boolean deathdateApprox) {
        super.setDeathdateApprox(deathdateApprox);
    }

    @Override
    public String getGender() {
        return super.getGender();
    }

    @Override
    public void setGender(String gender) {
        super.setGender(gender);
    }

    @Override
    public Map<String, List<String>> getRelationships() {
        return super.getRelationships();
    }

    @Override
    public void setRelationships(Map<String, List<String>> relationships) {
        super.setRelationships(relationships);
    }

    @Override
    public Client withFirstName(String firstName) {
        return super.withFirstName(firstName);
    }

    @Override
    public Client withMiddleName(String middleName) {
        return super.withMiddleName(middleName);
    }

    @Override
    public Client withLastName(String lastName) {
        return super.withLastName(lastName);
    }

    @Override
    public Client withName(String firstName, String middleName, String lastName) {
        return super.withName(firstName, middleName, lastName);
    }

    @Override
    public Client withBirthdate(DateTime birthdate, Boolean isApproximate) {
        return super.withBirthdate(birthdate, isApproximate);
    }

    @Override
    public Client withDeathdate(DateTime deathdate, Boolean isApproximate) {
        return super.withDeathdate(deathdate, isApproximate);
    }

    @Override
    public Client withGender(String gender) {
        return super.withGender(gender);
    }

    @Override
    public Client withRelationships(Map<String, List<String>> relationships) {
        return super.withRelationships(relationships);
    }

    @Override
    public List<String> findRelatives(String relationshipType) {
        return super.findRelatives(relationshipType);
    }

    @Override
    public void addRelationship(String relationType, String relativeEntityId) {
        super.addRelationship(relationType, relativeEntityId);
    }

    @Override
    public List<String> getRelationships(String relativeEntityId) {
        return super.getRelationships(relativeEntityId);
    }
}
