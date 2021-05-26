package org.smartregister.domain;

/**
 * Created by ndegwamartin on 13/08/2018.
 */
public class ServerSetting {
    private String key;
    private boolean value;
    private String label;
    private String description;

    public ServerSetting() {

    }

    /**
     * @param key         The key
     * @param value
     * @param label       the label
     * @param description
     */
    public ServerSetting(String key, boolean value, String label, String description) {
        this.key = key;
        this.value = value;
        this.label = label;
        this.description = description;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
