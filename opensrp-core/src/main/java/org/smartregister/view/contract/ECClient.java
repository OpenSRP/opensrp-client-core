package org.smartregister.view.contract;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.smartregister.domain.FPMethod;
import org.smartregister.util.DateUtil;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.smartregister.AllConstants.ECRegistrationFields.BPL_VALUE;
import static org.smartregister.AllConstants.ECRegistrationFields.SC_VALUE;
import static org.smartregister.AllConstants.ECRegistrationFields.ST_VALUE;
import static org.smartregister.AllConstants.IN_AREA;
import static org.smartregister.AllConstants.OUT_OF_AREA;
import static org.smartregister.util.DateUtil.formatDate;
import static org.smartregister.util.StringUtil.humanize;
import static org.smartregister.util.StringUtil.humanizeAndDoUPPERCASE;

public class ECClient implements ECSmartRegisterClient {

    private String entityId;
    private String entityIdToSavePhoto;
    private String name;
    private String husbandName;
    private String dateOfBirth;
    private Integer ecNumber;
    private String village;
    private String fpMethod;
    private String numPregnancies;
    private String parity;
    private String numLivingChildren;
    private String numStillbirths;
    private String numAbortions;
    private boolean isHighPriority;
    private String familyPlanningMethodChangeDate;
    private String photo_path;
    private String caste;
    private String economicStatus;
    private String iudPlace;
    private String iudPerson;
    private String numberOfCondomsSupplied;
    private String numberOfCentchromanPillsDelivered;
    private String numberOfOCPDelivered;
    private String highPriorityReason;
    private String locationStatus;
    private List<ECChildClient> children;
    private Map<String, String> status = new HashMap<String, String>();

    public ECClient(String entityId, String name, String husbandName, String village, Integer
            ecNumber) {
        this.entityId = entityId;
        this.entityIdToSavePhoto = entityId;
        this.name = name;
        this.husbandName = husbandName;
        this.village = village;
        this.ecNumber = ecNumber;
        this.children = new ArrayList<ECChildClient>();
    }

    public String wifeName() {
        return name;
    }

    @Override
    public String village() {
        return humanize(village);
    }

    @Override
    public String name() {
        return humanize(name);
    }

    @Override
    public String husbandName() {
        return humanize(husbandName);
    }

    @Override
    public String ageInString() {
        return "(" + age() + ")";
    }

    @Override
    public int age() {
        return StringUtils.isBlank(dateOfBirth) ? 0
                : Years.yearsBetween(LocalDate.parse(dateOfBirth), LocalDate.now()).getYears();
    }

    @Override
    public int ageInDays() {
        return StringUtils.isBlank(dateOfBirth) ? 0
                : Days.daysBetween(LocalDate.parse(dateOfBirth), DateUtil.today()).getDays();
    }

    @Override
    public String displayName() {
        return name();
    }

    @Override
    public int compareName(SmartRegisterClient client) {
        return this.name().compareToIgnoreCase(client.name());
    }

    public Integer ecNumber() {
        return ecNumber;
    }

    public String locationStatus() {
        return locationStatus;
    }

    @Override
    public boolean isSC() {
        return caste != null && caste.equalsIgnoreCase(SC_VALUE);
    }

    @Override
    public boolean isST() {
        return caste != null && caste.equalsIgnoreCase(ST_VALUE);
    }

    @Override
    public boolean isHighRisk() {
        return false;
    }

    @Override
    public boolean isHighPriority() {
        return isHighPriority;
    }

    @Override
    public boolean isBPL() {
        return economicStatus != null && economicStatus.equalsIgnoreCase(BPL_VALUE);
    }

    @Override
    public String profilePhotoPath() {
        return photo_path;
    }

    public FPMethod fpMethod() {
        return FPMethod.tryParse(this.fpMethod, FPMethod.NONE);
    }

    public List<ECChildClient> children() {
        return children;
    }

    public Map<String, String> status() {
        return status;
    }

    public String entityId() {
        return entityId;
    }

    public String numberOfPregnancies() {
        return numPregnancies;
    }

    public String parity() {
        return parity;
    }

    public String numberOfLivingChildren() {
        return numLivingChildren;
    }

    public String numberOfStillbirths() {
        return numStillbirths;
    }

    public String numberOfAbortions() {
        return numAbortions;
    }

    public String familyPlanningMethodChangeDate() {
        return formatDate(familyPlanningMethodChangeDate);
    }

    public String numberOfOCPDelivered() {
        return numberOfOCPDelivered;
    }

    public String numberOfCondomsSupplied() {
        return numberOfCondomsSupplied;
    }

    public String numberOfCentchromanPillsDelivered() {
        return numberOfCentchromanPillsDelivered;
    }

    public String iudPerson() {
        return humanizeAndDoUPPERCASE(iudPerson);
    }

    public String iudPlace() {
        return humanizeAndDoUPPERCASE(iudPlace);
    }

    public ECClient withDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ECClient withIsOutOfArea(boolean outOfArea) {
        this.locationStatus = outOfArea ? OUT_OF_AREA : IN_AREA;
        return this;
    }

    public ECClient withFPMethod(String fp_method) {
        this.fpMethod = fp_method;
        return this;
    }

    public ECClient withNumberOfPregnancies(String num_pregnancies) {
        this.numPregnancies = num_pregnancies;
        return this;
    }

    public ECClient withParity(String parity) {
        this.parity = parity;
        return this;
    }

    public ECClient withNumberOfLivingChildren(String num_living_children) {
        this.numLivingChildren = num_living_children;
        return this;
    }

    public ECClient withNumberOfStillBirths(String num_stillbirths) {
        this.numStillbirths = num_stillbirths;
        return this;
    }

    public ECClient withNumberOfAbortions(String num_abortions) {
        this.numAbortions = num_abortions;
        return this;
    }

    public ECClient withIsHighPriority(boolean highPriority) {
        isHighPriority = highPriority;
        return this;
    }

    public ECClient withFamilyPlanningMethodChangeDate(String family_planning_method_change_date) {
        this.familyPlanningMethodChangeDate = family_planning_method_change_date;
        return this;
    }

    public ECClient withPhotoPath(String photo_path) {
        this.photo_path = photo_path;
        return this;
    }

    public ECClient withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public ECClient withEconomicStatus(String economicStatus) {
        this.economicStatus = economicStatus;
        return this;
    }

    public ECClient withIUDPlace(String iudPlace) {
        this.iudPlace = iudPlace;
        return this;
    }

    public ECClient withIUDPerson(String iudPerson) {
        this.iudPerson = iudPerson;
        return this;
    }

    public ECClient withNumberOfCondomsSupplied(String numberOfCondomsSupplied) {
        this.numberOfCondomsSupplied = numberOfCondomsSupplied;
        return this;
    }

    public ECClient withNumberOfCentchromanPillsDelivered(String numberOfCentchromanPillsDelivered) {
        this.numberOfCentchromanPillsDelivered = numberOfCentchromanPillsDelivered;
        return this;
    }

    public ECClient withNumberOfOCPDelivered(String numberOfOCPDelivered) {
        this.numberOfOCPDelivered = numberOfOCPDelivered;
        return this;
    }

    public ECClient withHighPriorityReason(String highPriorityReason) {
        this.highPriorityReason = highPriorityReason;
        return this;
    }

    public ECClient withChildren(List<ECChildClient> children) {
        this.children = children;
        return this;
    }

    public ECClient addChild(ECChildClient childClient) {
        children.add(childClient);
        return this;
    }

    public ECClient withStatus(Map<String, String> status) {
        this.status = status;
        return this;
    }

    public boolean satisfiesFilter(String filter) {
        return name.toLowerCase(Utils.getDefaultLocale()).startsWith(filter.toLowerCase()) || String
                .valueOf(ecNumber).startsWith(filter);
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
