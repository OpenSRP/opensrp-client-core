package org.smartregister.domain.db.mock;

import org.smartregister.domain.Obs;

import java.util.List;

/**
 * Created by kaderchowdhury on 21/11/17.
 */

public class ObsMock extends Obs {
    public ObsMock() {
        super();
    }

    public ObsMock(String fieldType, String fieldDataType, String fieldCode, String parentCode, List<Object> values, String comments, String formSubmissionField, List<Object> humanReadableValues) {
        super(fieldType, fieldDataType, fieldCode, parentCode, values, comments, formSubmissionField, humanReadableValues);
    }

    @Override
    public String getFieldType() {
        return super.getFieldType();
    }

    @Override
    public void setFieldType(String fieldType) {
        super.setFieldType(fieldType);
    }

    @Override
    public String getFieldDataType() {
        return super.getFieldDataType();
    }

    @Override
    public void setFieldDataType(String fieldDataType) {
        super.setFieldDataType(fieldDataType);
    }

    @Override
    public String getFieldCode() {
        return super.getFieldCode();
    }

    @Override
    public void setFieldCode(String fieldCode) {
        super.setFieldCode(fieldCode);
    }

    @Override
    public String getParentCode() {
        return super.getParentCode();
    }

    @Override
    public void setParentCode(String parentCode) {
        super.setParentCode(parentCode);
    }

    @Override
    public Object getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
    }

    @Override
    public List<Object> getValues() {
        return super.getValues();
    }

    @Override
    public void setValues(List<Object> values) {
        super.setValues(values);
    }

    @Override
    public String getComments() {
        return super.getComments();
    }

    @Override
    public void setComments(String comments) {
        super.setComments(comments);
    }

    @Override
    public String getFormSubmissionField() {
        return super.getFormSubmissionField();
    }

    @Override
    public void setFormSubmissionField(String formSubmissionField) {
        super.setFormSubmissionField(formSubmissionField);
    }

    @Override
    public Object getHumanReadableValue() {
        return super.getHumanReadableValue();
    }

    @Override
    public void setHumanReadableValue(Object humanReadableValue) {
        super.setHumanReadableValue(humanReadableValue);
    }

    @Override
    public List<Object> getHumanReadableValues() {
        return super.getHumanReadableValues();
    }

    @Override
    public void setHumanReadableValues(List<Object> humanReadableValues) {
        super.setHumanReadableValues(humanReadableValues);
    }

    @Override
    public Obs withFieldType(String fieldType) {
        return super.withFieldType(fieldType);
    }

    @Override
    public Obs withFieldDataType(String fieldDataType) {
        return super.withFieldDataType(fieldDataType);
    }

    @Override
    public Obs withFieldCode(String fieldCode) {
        return super.withFieldCode(fieldCode);
    }

    @Override
    public Obs withParentCode(String parentCode) {
        return super.withParentCode(parentCode);
    }

    @Override
    public Obs withValue(Object value) {
        return super.withValue(value);
    }

    @Override
    public Obs withValues(List<Object> values) {
        return super.withValues(values);
    }

    @Override
    public Obs addToValueList(Object value) {
        return super.addToValueList(value);
    }

    @Override
    public Obs addToHumanReadableValuesList(Object humanReadableValue) {
        return super.addToHumanReadableValuesList(humanReadableValue);
    }

    @Override
    public Obs withComments(String comments) {
        return super.withComments(comments);
    }

    @Override
    public Obs withFormSubmissionField(String formSubmissionField) {
        return super.withFormSubmissionField(formSubmissionField);
    }

    @Override
    public Obs withHumanReadableValue(Object humanReadableValue) {
        return super.withHumanReadableValue(humanReadableValue);
    }

    @Override
    public Obs withHumanReadableValues(List<Object> humanReadableValues) {
        return super.withHumanReadableValues(humanReadableValues);
    }

}
