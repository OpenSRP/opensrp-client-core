package org.smartregister.clientandeventmodel.mock;

import org.smartregister.clientandeventmodel.FormInstance;
import org.smartregister.clientandeventmodel.FormSubmission;
import org.smartregister.clientandeventmodel.SubFormData;

import java.util.List;
import java.util.Map;

/**
 * Created by kaderchowdhury on 28/11/17.
 */

public class FormSubmissionMock extends FormSubmission {
    public FormSubmissionMock() {
        super();
    }

    public FormSubmissionMock(String anmId, String instanceId, String formName, String entityId, long clientVersion, String formDataDefinitionVersion, FormInstance formInstance, long serverVersion) {
        super(anmId, instanceId, formName, entityId, clientVersion, formDataDefinitionVersion, formInstance, serverVersion);
    }

    public FormSubmissionMock(String anmId, String instanceId, String formName, String entityId, String formDataDefinitionVersion, long clientVersion, FormInstance formInstance) {
        super(anmId, instanceId, formName, entityId, formDataDefinitionVersion, clientVersion, formInstance);
    }

    @Override
    public String anmId() {
        return super.anmId();
    }

    @Override
    public String instanceId() {
        return super.instanceId();
    }

    @Override
    public String entityId() {
        return super.entityId();
    }

    @Override
    public String formName() {
        return super.formName();
    }

    @Override
    public String bindType() {
        return super.bindType();
    }

    @Override
    public String defaultBindPath() {
        return super.defaultBindPath();
    }

    @Override
    public FormInstance instance() {
        return super.instance();
    }

    @Override
    public long clientVersion() {
        return super.clientVersion();
    }

    @Override
    public String formDataDefinitionVersion() {
        return super.formDataDefinitionVersion();
    }

    @Override
    public long serverVersion() {
        return super.serverVersion();
    }

    @Override
    public void setServerVersion(long serverVersion) {
        super.setServerVersion(serverVersion);
    }

    @Override
    public String getField(String name) {
        return super.getField(name);
    }

    @Override
    public Map<String, String> getFields(List<String> fieldNames) {
        return super.getFields(fieldNames);
    }

    @Override
    public String getInstanceId() {
        return super.getInstanceId();
    }

    @Override
    public SubFormData getSubFormByName(String name) {
        return super.getSubFormByName(name);
    }

    @Override
    public List<SubFormData> subForms() {
        return super.subForms();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return super.getMetadata();
    }

    @Override
    public Object getMetadata(String key) {
        return super.getMetadata(key);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
