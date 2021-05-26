package org.smartregister.domain.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

public class SubForm {
    private String name;
    private String bind_type;
    private String default_bind_path;
    private List<FormField> fields;
    private List<Map<String, String>> instances;

    public SubForm(String name) {
        this.name = name;
    }

    public List<Map<String, String>> instances() {
        return instances;
    }

    public String name() {
        return name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBindType() {
        return bind_type;
    }

    public String getDefaultBindPath() {
        return default_bind_path;
    }

    public List<FormField> getFields() {
        return fields;
    }

    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }

    public List<Map<String, String>> getInstances() {
        return instances;
    }

    public void setInstances(List<Map<String, String>> instances) {
        this.instances = instances;
    }
}
