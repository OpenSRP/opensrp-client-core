package org.smartregister.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Beneficiary {
    private String caseId;
    private String womanName;
    private String husbandName;
    private String thayiCardNumber;
    private String ecNumber;
    private String villageName;
    private boolean isHighRisk;

    public Beneficiary(String caseId, String womanName, String husbandName, String
            thayiCardNumber, String ecNumber, String villageName, boolean highRisk) {
        this.caseId = caseId;
        this.womanName = womanName;
        this.husbandName = husbandName;
        this.thayiCardNumber = thayiCardNumber;
        this.ecNumber = ecNumber;
        this.villageName = villageName;
        isHighRisk = highRisk;
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
