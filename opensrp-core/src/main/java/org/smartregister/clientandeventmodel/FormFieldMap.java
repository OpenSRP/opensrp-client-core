package org.smartregister.clientandeventmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormFieldMap {
    private String name;
    private List<String> values;
    private String source;
    private String bindPath;
    private String type;
    private Map<String, String> fieldAttributes;
    private Map<String, Map<String, String>> valuesCodes;

    public FormFieldMap(String name, List<String> values, String source, String bindPath, String
            type, Map<String, String> attributes, Map<String, Map<String, String>> valuesCodes) {
        this.name = name;
        this.values = values;
        this.source = source;
        this.bindPath = bindPath;
        this.type = type;
        this.fieldAttributes = attributes;
        this.valuesCodes = valuesCodes;
    }

    public FormFieldMap(String name, String value, String source, String bindPath, String type,
                        Map<String, String> attributes, Map<String, String> valueCodes) {
        this.name = name;
        addToValueList(value);
        this.source = source;
        this.bindPath = bindPath;
        this.type = type;
        this.fieldAttributes = attributes;
        addToValueCodeList(value, valueCodes);
    }

    public String name() {
        return name;
    }

    public String value() {
        if (values.size() > 1) {
            throw new RuntimeException("Multiset values can not be handled like single valued "
                    + "fields. Use function getValues");
        }

        if (values == null || values.size() == 0) {
            return null;
        }

        return values.get(0);
    }

    public List<String> values() {
        return values;
    }

    private void addToValueList(String value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);
    }

    private void addToValueCodeList(String value, Map<String, String> valueCodes) {
        if (valuesCodes == null) {
            valuesCodes = new HashMap<>();
        }

        valuesCodes.put(value, valueCodes);
    }

    public String source() {
        return source;
    }

    public String bindPath() {
        return bindPath;
    }

    public String type() {
        return type;
    }

    public Map<String, String> fieldAttributes() {
        return fieldAttributes;
    }

    public Map<String, Map<String, String>> valuesCodes() {
        return valuesCodes;
    }

    public Map<String, String> valueCodes(String value) {
        return valuesCodes.get(value);
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public String getSource() {
        return source;
    }

    public String getBindPath() {
        return bindPath;
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getFieldAttributes() {
        return fieldAttributes;
    }

    public Map<String, Map<String, String>> getValuesCodes() {
        return valuesCodes;
    }
}

