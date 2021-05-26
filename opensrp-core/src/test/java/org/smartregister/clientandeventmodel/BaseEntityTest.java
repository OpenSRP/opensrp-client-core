package org.smartregister.clientandeventmodel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.clientandeventmodel.mock.BaseEntityMock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaderchowdhury on 21/11/17.
 */

public class BaseEntityTest {

    private String baseEntityId;

    private Map<String, String> identifiers = new HashMap<>();

    private List<Address> addresses = new ArrayList<>();

    private Map<String, Object> attributes = new HashMap<String, Object>();
    private BaseEntityMock baseEntity;
    private String name = "name";

    private String identifierType = "type";
    private String identifier = "identifier";

    @Before
    public void setUp() {
        baseEntity = new BaseEntityMock(baseEntityId, identifiers);
    }

    @Test
    public void assertConstructors() {
        Assert.assertNotNull(new BaseEntityMock(baseEntityId, identifiers));
        Assert.assertNotNull(new BaseEntityMock(baseEntityId, identifiers, attributes));
        Assert.assertNotNull(new BaseEntityMock(baseEntityId, identifiers, attributes, addresses));
    }

    public String getBaseEntityId() {
        return baseEntity.getBaseEntityId();
    }

    @Test
    public void assertsetBaseEntityId() {
        baseEntity.setBaseEntityId(baseEntityId);
        Assert.assertEquals(getBaseEntityId(), baseEntityId);
    }

    public List<Address> getAddresses() {
        return baseEntity.getAddresses();
    }

    public Address getAddress(String addressType) {
        return baseEntity.getAddress(addressType);
    }

    @Test
    public void assertsetAddresses() {
        baseEntity.setAddresses(addresses);
        Assert.assertEquals(getAddresses(), addresses);
    }

    @Test
    public void assertaddAddress() {
        Address address = new Address();
        address.setAddressType(identifierType);
        List<Address> addressesses = new ArrayList<>();
        addressesses.add(address);
        baseEntity.setAddresses(addressesses);
        baseEntity.addAddress(address);
        Assert.assertEquals(getAddresses(), addressesses);
        Assert.assertEquals(getAddress(identifierType), address);
        Assert.assertEquals(getAddress("NULL"), null);
    }

    public Map<String, Object> getAttributes() {
        return baseEntity.getAttributes();
    }

    public Object getAttribute(String name) {
        return baseEntity.getAttribute(name);
    }

    @Test
    public void assertsetAttributes() {
        baseEntity.setAttributes(attributes);
        Assert.assertEquals(getAttributes(), attributes);
    }

    @Test
    public void addAttribute() {
        Object value = new Object();
        baseEntity.setAttributes(null);
        baseEntity.addAttribute(name, value);
        baseEntity.setAttributes(null);
        Assert.assertEquals(getAttribute(""), null);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(name, value);
        baseEntity.setAttributes(attributes);
        Assert.assertEquals(getAttribute(""), null);
        baseEntity.addAttribute(name, value);
        Assert.assertEquals(getAttribute(name), value);
        baseEntity.removeAttribute(name); //attribute successfully added and removed
        Assert.assertEquals(getAttribute(name), null);
    }

    public void removeAttribute(String name) {
        baseEntity.removeAttribute(name);
    }

    public Map<String, String> getIdentifiers() {
        return baseEntity.getIdentifiers();
    }

    public String getIdentifier(String identifierType) {
        return baseEntity.getIdentifier(identifierType);
    }

    @Test
    public void assertsetIdentifiers() {
        baseEntity.setIdentifiers(identifiers);
        Assert.assertEquals(getIdentifiers(), identifiers);
    }

    @Test
    public void assertaddIdentifier() {
        baseEntity.setIdentifiers(identifiers);
        baseEntity.addIdentifier(identifierType, identifier);
        Assert.assertEquals(getIdentifier(identifierType), identifier);
    }

    @Test
    public void assertremoveIdentifier() {
        identifiers.put(identifierType, identifier);
        baseEntity.setIdentifiers(identifiers);
        baseEntity.removeIdentifier(identifierType);
        Assert.assertEquals(getIdentifiers(), identifiers);
    }

    @Test
    public void assertwithBaseEntityId() {
        Assert.assertNotNull(baseEntity.withBaseEntityId(baseEntityId));
    }

    @Test
    public void assertwithIdentifiers() {
        Assert.assertNotNull(baseEntity.withIdentifiers(identifiers));
    }

    @Test
    public void assertwithIdentifier() {
        Assert.assertNotNull(baseEntity.withIdentifier("", ""));
    }

    @Test
    public void assertwithAddresses() {
        Assert.assertNotNull(baseEntity.withAddresses(addresses));
    }

    @Test
    public void assertwithAddress() {
        Address address = new Address();
        Assert.assertNotNull(baseEntity.withAddress(address));
    }

    @Test
    public void assertwithAttributes() {
        Assert.assertNotNull(baseEntity.withAttributes(attributes));
    }

    @Test
    public void assertwithAttribute() {

        Object value = new Object();
        Assert.assertNotNull(baseEntity.withAttribute(name, value));
    }

    @Test
    public void assertequals() {
        Object o = new BaseEntity();
        Assert.assertEquals(baseEntity.equals(o), false);
    }

    @Test
    public void asserthashCode() {
        Assert.assertNotNull(baseEntity.hashCode());
    }

    @Test
    public void asserttoString() {
        Assert.assertNotNull(baseEntity.toString());
    }
}
