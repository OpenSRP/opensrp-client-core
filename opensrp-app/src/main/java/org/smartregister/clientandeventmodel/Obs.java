package org.smartregister.clientandeventmodel;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Obs {

    @JsonProperty
    private String fieldType;
    @JsonProperty
    private String fieldDataType;
    @JsonProperty
    private String fieldCode;
    @JsonProperty
    private String parentCode;
    @JsonProperty
    private List<Object> values;
    @JsonProperty
    private Map<String, Object> keyValPairs;
    @JsonProperty
    private List<Object> humanReadableValues;
    @JsonProperty
    private String comments;
    @JsonProperty
    private String formSubmissionField;
    @JsonProperty
    private boolean saveObsAsArray;

    public Obs() {
    }

    public Obs(String fieldType, String fieldDataType, String fieldCode, String parentCode,
               List<Object> values, List<Object> humanReadableValues, String comments, String
                       formSubmissionField) {
        this.setFieldType(fieldType);
        this.fieldDataType = fieldDataType;
        this.fieldCode = fieldCode;
        this.parentCode = parentCode;
        this.values = values;
        this.humanReadableValues = humanReadableValues;
        this.comments = comments;
        this.formSubmissionField = formSubmissionField;
    }

    public Obs(String fieldType, String fieldDataType, String fieldCode, String parentCode,
               List<Object> values, List<Object> humanReadableValues, String comments,
               String formSubmissionField, boolean saveObsAsArray) {
        this(fieldType, fieldDataType, fieldCode, parentCode, values, humanReadableValues, comments, formSubmissionField);
        setSaveObsAsArray(saveObsAsArray);
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldDataType() {
        return fieldDataType;
    }

    public void setFieldDataType(String fieldDataType) {
        this.fieldDataType = fieldDataType;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public boolean isSaveObsAsArray() {
        return saveObsAsArray;
    }

    public void setSaveObsAsArray(boolean saveObsAsArray) {
        this.saveObsAsArray = saveObsAsArray;
    }

    @JsonIgnore
    public Object getValue() {
        if (values.size() > 1) {
//            throw new RuntimeException("Multiset values can not be handled like single valued
// fields. Use function getValues");
            return getValues();
        }
        if (values == null || values.size() == 0) {
            return null;
        }

        return values.get(0);
    }

    @JsonIgnore
    public void setValue(Object value) {
        addToValueList(value);
    }

    public List<Object> getHumanReadableValues() {
        return humanReadableValues;
    }

    public void setHumanReadableValues(List<Object> humanReadableValues) {
        this.humanReadableValues = humanReadableValues;
    }

    public Map<String, Object>  getKeyValPairs() {
        return keyValPairs;
    }

    public void setKeyValPairs(Map<String, Object> keyValPairs) {
        this.keyValPairs = keyValPairs;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFormSubmissionField() {
        return formSubmissionField;
    }

    public void setFormSubmissionField(String formSubmissionField) {
        this.formSubmissionField = formSubmissionField;
    }

    public Obs withFieldType(String fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public Obs withFieldDataType(String fieldDataType) {
        this.fieldDataType = fieldDataType;
        return this;
    }

    public Obs withFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
        return this;
    }

    public Obs withParentCode(String parentCode) {
        this.parentCode = parentCode;
        return this;
    }

    public Obs withValue(Object value) {
        return addToValueList(value);
    }

    public Obs withValues(List<Object> values) {
        this.values = values;
        return this;
    }
    public Obs withKeyValPairs(Map<String, Object> keyValPairs) {
        setKeyValPairs(keyValPairs);
        return this;
    }

    public Obs addToValueList(Object value) {
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);
        return this;
    }

    public Obs withComments(String comments) {
        this.comments = comments;
        return this;
    }

    public Obs withFormSubmissionField(String formSubmissionField) {
        this.formSubmissionField = formSubmissionField;
        return this;
    }

    public Obs withHumanReadableValues(List<Object> humanReadableValues) {
        this.humanReadableValues = humanReadableValues;
        return this;
    }

    public Obs withsaveObsAsArray(boolean saveObsAsArray) {
        setSaveObsAsArray(saveObsAsArray);
        return this;
    }
}

