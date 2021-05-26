package org.smartregister.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Child {
    private String caseId;
    private String motherName;
    private String fatherName;
    private String thayiCardNumber;
    private String ecNumber;
    private String villageName;
    private boolean isHighRisk;

    public Child(String caseId, String thayiCardNumber, String motherName, String fatherName,
                 String ecNumber, String villageName, boolean highRisk) {
        this.caseId = caseId;
        this.motherName = motherName;
        this.fatherName = fatherName;
        this.thayiCardNumber = thayiCardNumber;
        this.ecNumber = ecNumber;
        this.villageName = villageName;
        isHighRisk = highRisk;
    }

    public String motherName() {
        return motherName;
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
