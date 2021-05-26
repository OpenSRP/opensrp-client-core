package org.smartregister.domain.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FormInstance {
    private String form_data_definition_version;
    private FormData form;

    public FormInstance(FormData form, String formDataDefinitionVersion) {
        this.form = form;
        this.form_data_definition_version = formDataDefinitionVersion;
    }

    public FormData form() {
        return form;
    }

    public String getFieldValue(String name) {
        return form.getFieldValue(name);
    }

    public SubForm getSubFormByName(String name) {
        return form.getSubFormByName(name);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public FormData getForm() {
        return form;
    }

    public void setForm(FormData form) {
        this.form = form;
    }

    public String getForm_data_definition_version() {
        return form_data_definition_version;
    }

    public void setForm_data_definition_version(String form_data_definition_version) {
        this.form_data_definition_version = form_data_definition_version;
    }
}
