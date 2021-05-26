package org.smartregister.clientandeventmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubFormData {
    @JsonProperty
    private String name;
    @JsonProperty
    private String bind_type;
    @JsonProperty
    private String default_bind_path;
    @JsonProperty
    private List<FormField> fields;
    @JsonProperty
    private List<Map<String, String>> instances;

    public SubFormData() {
        this.instances = new ArrayList<>();
        this.name = "";
    }

    public SubFormData(String name, List<Map<String, String>> instances) {
        this.instances = instances;
        this.name = name;
    }

    public String name() {
        return name;
    }

    public String defaultBindPath() {
        return default_bind_path;
    }

    public String bindType() {
        return bind_type;
    }

    public List<FormField> fields() {
        return fields;
    }

    public List<Map<String, String>> instances() {
        return instances;
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

    public String getBind_type() {
        return bind_type;
    }

    public void setBind_type(String bind_type) {
        this.bind_type = bind_type;
    }

    public String getDefault_bind_path() {
        return default_bind_path;
    }

    public void setDefault_bind_path(String default_bind_path) {
        this.default_bind_path = default_bind_path;
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

