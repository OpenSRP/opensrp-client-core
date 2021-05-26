package org.smartregister.clientandeventmodel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.clientandeventmodel.mock.ClientMock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaderchowdhury on 20/11/17.
 */

public class ClientTest extends BaseUnitTest {

    private ClientMock client;
    private String baseEntityId = "baseEntityId";
    private String firstName = "firstName";
    private String middleName = "middleName";
    private String lastName = "LastName";
    private Date birthdate = new Date(0l);
    private Date deathdate = new Date(0l);
    private Boolean birthdateApprox = Boolean.FALSE;
    private Boolean deathdateApprox = Boolean.TRUE;
    private String gender = "X";
    private String identifierType = "type";
    private String identifier = "identifier";
    private List<Address> addresses = new ArrayList<>();
    private Map<String, String> identifiers = new HashMap<>();
    private Map<String, Object> attributes = new HashMap<>();

    private Map<String, List<String>> relationships = new HashMap<String, List<String>>();

    @Before
    public void setUp() {
        client = new ClientMock(baseEntityId);
        Assert.assertNotNull(new ClientMock());
    }

    @Test
    public void assertconstructor1() {
        client = new ClientMock(baseEntityId);
        Assert.assertNotNull(client);
    }

    @Test
    public void assertconstructor2() {
        client = new ClientMock(baseEntityId, firstName, middleName, lastName, birthdate, deathdate, birthdateApprox, deathdateApprox, gender);
        Assert.assertNotNull(client);
    }

    @Test
    public void assertconstructor3() {
        client = new ClientMock(baseEntityId, firstName, middleName, lastName, birthdate, deathdate, birthdateApprox, deathdateApprox, gender, identifierType, identifier);
        Assert.assertNotNull(client);
    }

    @Test
    public void assertconstructor4() {
        client = new ClientMock(baseEntityId, firstName, middleName, lastName, birthdate, deathdate, birthdateApprox, deathdateApprox, gender, addresses, identifiers, attributes);
        Assert.assertNotNull(client);
    }

    public String getFirstName() {
        return client.getFirstName();
    }

    @Test
    public void assertSetFirstName() {
        client.setFirstName(firstName);
        Assert.assertEquals(getFirstName(), firstName);
    }

    public String getMiddleName() {
        return client.getMiddleName();
    }

    @Test
    public void assertSetMiddleName() {
        client.setMiddleName(middleName);
        Assert.assertEquals(getMiddleName(), middleName);
    }

    public String getLastName() {
        return client.getLastName();
    }

    @Test
    public void assertSetLastName() {
        client.setLastName(lastName);
        Assert.assertEquals(getLastName(), lastName);
    }

    public Date getBirthdate() {
        return client.getBirthdate();
    }

    @Test
    public void assertSetBirthdate() {
        client.setBirthdate(birthdate);
        Assert.assertEquals(getBirthdate(), birthdate);
    }

    public Date getDeathdate() {
        return client.getDeathdate();
    }

    @Test
    public void assertSetDeathdate() {
        client.setDeathdate(deathdate);
        Assert.assertEquals(getDeathdate(), deathdate);
    }

    public Boolean getBirthdateApprox() {
        return client.getBirthdateApprox();
    }

    @Test
    public void assertSetBirthdateApprox() {
        client.setBirthdateApprox(birthdateApprox);
        Assert.assertEquals(getBirthdateApprox(), birthdateApprox);
    }

    public Boolean getDeathdateApprox() {
        return client.getDeathdateApprox();
    }

    @Test
    public void assertSetDeathdateApprox() {
        client.setDeathdateApprox(deathdateApprox);
        Assert.assertEquals(getDeathdateApprox(), deathdateApprox);
    }

    public String getGender() {
        return client.getGender();
    }

    @Test
    public void assertSetGender() {
        client.setGender(gender);
        Assert.assertEquals(getGender(), gender);
    }

    public Map<String, List<String>> getRelationships() {
        return client.getRelationships();
    }

    @Test
    public void assertSetRelationships() {
        client.setRelationships(relationships);
        Assert.assertEquals(getRelationships(), relationships);
    }

    @Test
    public void assertwithFirstName() {
        Assert.assertNotNull(client.withFirstName(firstName));
    }

    @Test
    public void assertwithMiddleName() {
        Assert.assertNotNull(client.withMiddleName(middleName));
    }

    @Test
    public void assertwithLastName() {
        Assert.assertNotNull(client.withLastName(lastName));
    }

    @Test
    public void assertwithName() {
        Assert.assertNotNull(client.withName(firstName, middleName, lastName));
    }

    @Test
    public void assertwithBirthdate() {
        Assert.assertNotNull(client.withBirthdate(birthdate, Boolean.TRUE));
    }

    @Test
    public void assertwithDeathdate() {
        Assert.assertNotNull(client.withDeathdate(deathdate, Boolean.TRUE));
    }

    @Test
    public void assertthatwithGender() {
        Assert.assertNotNull(client.withGender(gender));
    }

    @Test
    public void assertwithRelationships() {
        Assert.assertNotNull(client.withRelationships(relationships));
    }

    @Test
    public void assertfindRelatives() {
        client.setRelationships(null);
        Assert.assertNull(client.findRelatives(null));
        relationships.put(identifierType, new ArrayList<String>());
        client.setRelationships(relationships);
        Assert.assertNotNull(client.findRelatives(identifierType));
    }

    @Test
    public void assertaddRelationship() {
        client.setRelationships(null);
        client.addRelationship(identifierType, baseEntityId);
        Assert.assertNotNull(client.findRelatives(identifierType));
    }

    @Test
    public void assertgetRelationshipsReturnsList() {
        relationships.put(baseEntityId, new ArrayList<String>());
        client.setRelationships(relationships);
        Assert.assertNotNull(client.getRelationships(baseEntityId));
    }

    public String getRelationalBaseEntityId() {
        return client.getRelationalBaseEntityId();
    }

    @Test
    public void assertSetRelationalBaseEntityId() {
        String relationalBaseEntityId = "0";
        client.setRelationalBaseEntityId(relationalBaseEntityId);
        Assert.assertEquals(getRelationalBaseEntityId(), relationalBaseEntityId);
    }

    @Test
    public void assertwithGender() throws Exception {
        Assert.assertNotNull(client.withGender(Gender.FEMALE));
    }

    @Test
    public void assertequals() {
        Object o = new Client();
        Assert.assertEquals(client.equals(o), false);
    }

    @Test
    public void asserthashCode() {
        Assert.assertNotNull(client.hashCode());
    }

    @Test
    public void asserttoString() {
        Assert.assertNotNull(client.toString());
    }
}
