package org.smartregister.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.smartregister.AllConstants.BOOLEAN_TRUE;
import static org.smartregister.AllConstants.ECRegistrationFields.CURRENT_FP_METHOD;
import static org.smartregister.AllConstants.ECRegistrationFields.HIGH_PRIORITY_REASON;
import static org.smartregister.AllConstants.ECRegistrationFields.IS_HIGH_PRIORITY;
import static org.smartregister.AllConstants.SPACE;

public class EligibleCouple {
    private final String village;
    private final String subcenter;
    private String caseId;
    private String wifeName;
    private String husbandName;
    private String ecNumber;
    private Map<String, String> details;
    private Boolean isOutOfArea;
    private Boolean isClosed;
    private String photoPath;

    public EligibleCouple(String caseId, String wifeName, String husbandName, String ecNumber,
                          String village, String subcenter, Map<String, String> details) {
        this.caseId = caseId;
        this.wifeName = wifeName;
        this.husbandName = husbandName;
        this.ecNumber = ecNumber;
        this.village = village;
        this.subcenter = subcenter;
        this.details = details;
        this.isOutOfArea = false;
        this.isClosed = false;
        this.photoPath = null;
    }

    public EligibleCouple asOutOfArea() {
        this.isOutOfArea = true;
        return this;
    }

    public EligibleCouple withPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    public EligibleCouple withOutOfArea(String outOfArea) {
        if (Boolean.parseBoolean(outOfArea)) {
            isOutOfArea = true;
        }
        return this;
    }

    public String wifeName() {
        return wifeName;
    }

    public String husbandName() {
        return husbandName;
    }

    public String ecNumber() {
        return ecNumber;
    }

    public String caseId() {
        return caseId;
    }

    public String village() {
        return village;
    }

    public String subCenter() {
        return subcenter;
    }

    public boolean isOutOfArea() {
        return isOutOfArea;
    }

    public boolean isHighPriority() {
        return parseDetailFieldValueToBoolean(IS_HIGH_PRIORITY);
    }

    public String highPriorityReason() {
        String highRiskReason =
                details.get(HIGH_PRIORITY_REASON) == null ? "" : details.get(HIGH_PRIORITY_REASON);
        return StringUtils
                .join(new HashSet<String>(Arrays.asList(highRiskReason.split(SPACE))).toArray(),
                        SPACE);
    }

    public boolean isYoungestChildUnderTwo() {
        return parseDetailFieldValueToBoolean("isYoungestChildUnderTwo");
    }

    private boolean parseDetailFieldValueToBoolean(String fieldName) {
        String isHighPriority = details.get(fieldName);
        return "1".equals(isHighPriority) || BOOLEAN_TRUE.equals(isHighPriority);
    }

    public Map<String, String> details() {
        return details;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public String photoPath() {
        return photoPath;
    }

    public EligibleCouple setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
        return this;
    }

    public String getDetail(String name) {
        return details.get(name);
    }

    public String age() {
        //TODO: Calculate age from DOB
        return details.get("wifeAge");
    }

    public boolean hasFPMethod() {
        String fpMethod = getDetail(CURRENT_FP_METHOD);
        return isNotBlank(fpMethod) && !"none".equalsIgnoreCase(fpMethod);
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
