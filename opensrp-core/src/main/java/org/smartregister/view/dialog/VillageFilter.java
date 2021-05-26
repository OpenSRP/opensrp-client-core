package org.smartregister.view.dialog;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.smartregister.view.contract.SmartRegisterClient;

public class VillageFilter implements FilterOption {
    private final String filter;

    public VillageFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public String name() {
        return filter;
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return client.village().equalsIgnoreCase(filter);
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
}
