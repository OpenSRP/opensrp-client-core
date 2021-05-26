package org.smartregister.domain.form;

import com.google.gson.Gson;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.smartregister.domain.SyncStatus;

public class FormSubmission {
    private String instanceId;
    private String entityId;
    private String formName;
    private String instance;
    private String clientVersion;
    private String formDataDefinitionVersion;
    private String serverVersion;
    private SyncStatus syncStatus;
    private FormInstance formInstance;

    public FormSubmission(String instanceId, String entityId, String formName, String instance,
                          String clientVersion, SyncStatus syncStatus, String
                                  formDataDefinitionVersion) {
        this(instanceId, entityId, formName, instance, clientVersion, syncStatus,
                formDataDefinitionVersion, null);
    }

    public FormSubmission(String instanceId, String entityId, String formName, String instance,
                          String clientVersion, SyncStatus syncStatus, String
                                  formDataDefinitionVersion, String serverVersion) {
        this.instanceId = instanceId;
        this.entityId = entityId;
        this.formName = formName;
        this.instance = instance;
        this.clientVersion = clientVersion;
        this.syncStatus = syncStatus;
        this.formDataDefinitionVersion = formDataDefinitionVersion;
        this.serverVersion = serverVersion;
        this.formInstance = new Gson().fromJson(instance, FormInstance.class);
    }

    public String instanceId() {
        return instanceId;
    }

    public String entityId() {
        return entityId;
    }

    public String formName() {
        return formName;
    }

    public String instance() {
        return instance;
    }

    public String version() {
        return clientVersion;
    }

    public String serverVersion() {
        return serverVersion;
    }

    public SyncStatus syncStatus() {
        return syncStatus;
    }

    public String formDataDefinitionVersion() {
        return formDataDefinitionVersion;
    }

    public FormSubmission setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
        return this;
    }

    public FormSubmission setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
        return this;
    }

    public String getFieldValue(String fieldName) {
        return formInstance.getFieldValue(fieldName);
    }

    public SubForm getSubFormByName(String name) {
        return formInstance.getSubFormByName(name);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "clientVersion");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "clientVersion");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public FormInstance getFormInstance() {
        return formInstance;
    }

    public void setFormInstance(FormInstance formInstance) {
        this.formInstance = formInstance;
    }

    public FormData getForm() {
        return formInstance.form();
    }

}
