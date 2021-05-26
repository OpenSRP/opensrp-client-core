package org.smartregister.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static org.smartregister.view.contract.AlertDTO.emptyAlert;
import static org.smartregister.view.contract.ServiceProvidedDTO.emptyService;

public class Visits {
    public ServiceProvidedDTO provided = emptyService;
    public AlertDTO toProvide = emptyAlert;

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
