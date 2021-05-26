package org.smartregister.clientandeventmodel.processor.model;

import java.util.Map;

/**
 * Created by raihan on 3/15/16.
 */
public class Client {
    private String baseEntityID;
    private Map<String, String> attributesDetailsMap;
    private Map<String, String> attributesColumnsMap;
    private Map<String, String> propertyColumnMap;
    private Map<String, String> propertyDetailsMap;

    public Client(String baseEntityID, Map<String, String> attributesDetailsMap, Map<String,
            String> attributesColumnsMap, Map<String, String> propertyColumnsMap, Map<String,
            String> propertyDetailMap) {
        this.baseEntityID = baseEntityID;
        this.attributesDetailsMap = attributesDetailsMap;
        this.attributesColumnsMap = attributesColumnsMap;
        propertyColumnMap = propertyColumnsMap;
        propertyDetailsMap = propertyDetailMap;
    }

    public String getBaseEntityID() {
        return baseEntityID;
    }

    public void setBaseEntityID(String baseEntityID) {
        this.baseEntityID = baseEntityID;
    }

    public Map<String, String> getAttributesDetailsMap() {
        return attributesDetailsMap;
    }

    public void setAttributesDetailsMap(Map<String, String> attributesDetailsMap) {
        this.attributesDetailsMap = attributesDetailsMap;
    }

    public Map<String, String> getAttributesColumnsMap() {
        return attributesColumnsMap;
    }

    public void setAttributesColumnsMap(Map<String, String> attributesColumnsMap) {
        this.attributesColumnsMap = attributesColumnsMap;
    }

    public Map<String, String> getPropertyColumnMap() {
        return propertyColumnMap;
    }

    public void setPropertyColumnMap(Map<String, String> propertyColumnMap) {
        this.propertyColumnMap = propertyColumnMap;
    }

    public Map<String, String> getPropertyDetailsMap() {
        return propertyDetailsMap;
    }

    public void setPropertyDetailsMap(Map<String, String> propertyDetailsMap) {
        this.propertyDetailsMap = propertyDetailsMap;
    }
}
