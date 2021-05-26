package org.smartregister.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.smartregister.AllConstants.ANCRegistrationFields.HIGH_RISK_REASON;
import static org.smartregister.AllConstants.ANCRegistrationFields.IS_HIGH_RISK;
import static org.smartregister.AllConstants.BOOLEAN_TRUE;
import static org.smartregister.AllConstants.SPACE;

public class Mother {
    private final String caseId;
    private final String ecCaseId;
    private final String thayiCardNumber;
    private String referenceDate;
    private Map<String, String> details;
    private boolean isClosed;
    private String type;

    public Mother(String caseId, String ecCaseId, String thayiCardNumber, String referenceDate) {
        this.caseId = caseId;
        this.ecCaseId = ecCaseId;
        this.thayiCardNumber = thayiCardNumber;
        this.referenceDate = referenceDate;
        this.details = new HashMap<String, String>();
        this.isClosed = false;
    }

    public String caseId() {
        return caseId;
    }

    public String ecCaseId() {
        return ecCaseId;
    }

    public String thayiCardNumber() {
        return thayiCardNumber;
    }

    public String referenceDate() {
        return referenceDate;
    }

    public Mother withDetails(Map<String, String> details) {
        this.details = details;
        return this;
    }

    public boolean isHighRisk() {
        return BOOLEAN_TRUE.equals(details.get(IS_HIGH_RISK));
    }

    public String highRiskReason() {
        String highRiskReason =
                details.get(HIGH_RISK_REASON) == null ? "" : details.get(HIGH_RISK_REASON).trim();
        return StringUtils
                .join(new HashSet<String>(Arrays.asList(highRiskReason.split(SPACE))).toArray(),
                        SPACE);
    }

    public Map<String, String> details() {
        return details;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public Mother setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
        return this;
    }

    public Mother withType(String type) {
        this.type = type;
        return this;
    }

    public String getDetail(String name) {
        return details.get(name);
    }

    public boolean isANC() {
        return "anc".equalsIgnoreCase(this.type);
    }

    public boolean isPNC() {
        return "pnc".equalsIgnoreCase(this.type);
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
