package org.smartregister.view.contract;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CoupleDetails {
    private final String ecNumber;
    private final boolean isInArea;
    private String wifeName;
    private String husbandName;
    private String caste;
    private String economicStatus;
    private String photo_path;

    public CoupleDetails(String wifeName, String husbandName, String ecNumber, boolean outOfArea) {
        this.wifeName = wifeName;
        this.husbandName = husbandName;
        this.ecNumber = ecNumber;
        this.isInArea = !outOfArea;
    }

    public CoupleDetails withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public CoupleDetails withEconomicStatus(String economicStatus) {
        this.economicStatus = economicStatus;
        return this;
    }

    public CoupleDetails withPhotoPath(String photoPath) {
        this.photo_path = photoPath;
        return this;
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
