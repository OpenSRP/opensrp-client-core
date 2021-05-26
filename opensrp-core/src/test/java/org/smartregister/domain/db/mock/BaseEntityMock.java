package org.smartregister.domain.db.mock;

import org.smartregister.domain.Address;
import org.smartregister.domain.BaseEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by kaderchowdhury on 21/11/17.
 */

public class BaseEntityMock extends BaseEntity {

    protected BaseEntityMock() {
        super();
    }

    public BaseEntityMock(String baseEntityId) {
        super(baseEntityId);
    }

    public BaseEntityMock(String baseEntityId, Map<String, String> identifiers) {
        super(baseEntityId, identifiers);
    }

    public BaseEntityMock(String baseEntityId, Map<String, String> identifiers, Map<String, Object> attributes) {
        super(baseEntityId, identifiers, attributes);
    }

    public BaseEntityMock(String baseEntityId, Map<String, String> identifiers, Map<String, Object> attributes, List<Address> addresses) {
        super(baseEntityId, identifiers, attributes, addresses);
    }

    @Override
    public String getBaseEntityId() {
        return super.getBaseEntityId();
    }

    @Override
    public void setBaseEntityId(String baseEntityId) {
        super.setBaseEntityId(baseEntityId);
    }

    @Override
    public List<Address> getAddresses() {
        return super.getAddresses();
    }

    @Override
    public Address getAddress(String addressType) {
        return super.getAddress(addressType);
    }

    @Override
    public void setAddresses(List<Address> addresses) {
        super.setAddresses(addresses);
    }

    @Override
    public void addAddress(Address address) {
        super.addAddress(address);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return super.getAttributes();
    }

    @Override
    public Object getAttribute(String name) {
        return super.getAttribute(name);
    }

    @Override
    public void setAttributes(Map<String, Object> attributes) {
        super.setAttributes(attributes);
    }

    @Override
    public void addAttribute(String name, Object value) {
        super.addAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        super.removeAttribute(name);
    }

    @Override
    public Map<String, String> getIdentifiers() {
        return super.getIdentifiers();
    }

    @Override
    public String getIdentifier(String identifierType) {
        return super.getIdentifier(identifierType);
    }

    @Override
    public void setIdentifiers(Map<String, String> identifiers) {
        super.setIdentifiers(identifiers);
    }

    @Override
    public void addIdentifier(String identifierType, String identifier) {
        super.addIdentifier(identifierType, identifier);
    }

    @Override
    public void removeIdentifier(String identifierType) {
        super.removeIdentifier(identifierType);
    }

    @Override
    public BaseEntity withBaseEntityId(String baseEntityId) {
        return super.withBaseEntityId(baseEntityId);
    }

    @Override
    public BaseEntity withIdentifiers(Map<String, String> identifiers) {
        return super.withIdentifiers(identifiers);
    }

    @Override
    public BaseEntity withIdentifier(String identifierType, String identifier) {
        return super.withIdentifier(identifierType, identifier);
    }

    @Override
    public BaseEntity withAddresses(List<Address> addresses) {
        return super.withAddresses(addresses);
    }

    @Override
    public BaseEntity withAddress(Address address) {
        return super.withAddress(address);
    }

    @Override
    public BaseEntity withAttributes(Map<String, Object> attributes) {
        return super.withAttributes(attributes);
    }

    @Override
    public BaseEntity withAttribute(String name, Object value) {
        return super.withAttribute(name, value);
    }
}
