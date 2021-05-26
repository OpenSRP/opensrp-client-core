package org.smartregister.clientandeventmodel.processor.model;

import java.util.Map;

/**
 * Created by raihan on 3/15/16.
 */
public class Event {
    String baseEntityID;
    Map<String, String> attributesDetailsMap;
    Map<String, String> attributesColumnsMap;
    Map<String, String> ObsColumnsMap;
    Map<String, String> ObsDetailsMap;

    public Event(String baseEntityID, Map<String, String> attributesDetailsMap, Map<String,
            String> attributesColumnsMap, Map<String, String> obsColumnsMap, Map<String, String>
                         obsDetailsMap) {
        this.baseEntityID = baseEntityID;
        this.attributesDetailsMap = attributesDetailsMap;
        this.attributesColumnsMap = attributesColumnsMap;
        ObsColumnsMap = obsColumnsMap;
        ObsDetailsMap = obsDetailsMap;
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

    public Map<String, String> getObsColumnsMap() {
        return ObsColumnsMap;
    }

    public void setObsColumnsMap(Map<String, String> obsColumnsMap) {
        ObsColumnsMap = obsColumnsMap;
    }

    public Map<String, String> getObsDetailsMap() {
        return ObsDetailsMap;
    }

    public void setObsDetailsMap(Map<String, String> obsDetailsMap) {
        ObsDetailsMap = obsDetailsMap;
    }
}
