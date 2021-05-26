package org.smartregister.clientandeventmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class FormInstance {
    @JsonProperty
    private String form_data_definition_version;

    @JsonProperty
    private FormData form;

    public FormInstance() {
    }

    public FormInstance(FormData form) {
        this.form = form;
    }

    public FormData form() {
        return form;
    }

    public String getField(String name) {
        return form.getField(name);
    }

    public String bindType() {
        return form.bindType();
    }

    public String defaultBindPath() {
        return form.defaultBindPath();
    }

    public SubFormData getSubFormByName(String name) {
        return form.getSubFormByName(name);
    }

    public List<SubFormData> subForms() {
        return form.subForms();
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

    public void setForm_data_definition_version(String form_data_definition_version) {
        this.form_data_definition_version = form_data_definition_version;
    }

    public FormData getForm() {
        return form;
    }

    public void setForm(FormData form) {
        this.form = form;
    }

}
