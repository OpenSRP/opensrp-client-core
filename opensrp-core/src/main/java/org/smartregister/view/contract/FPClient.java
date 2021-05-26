package org.smartregister.view.contract;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.FPMethod;
import org.smartregister.util.IntegerUtil;
import org.smartregister.util.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.smartregister.AllConstants.COMMA_WITH_SPACE;
import static org.smartregister.AllConstants.ECRegistrationFields.BPL_VALUE;
import static org.smartregister.AllConstants.ECRegistrationFields.SC_VALUE;
import static org.smartregister.AllConstants.ECRegistrationFields.ST_VALUE;
import static org.smartregister.AllConstants.SPACE;
import static org.smartregister.util.DateUtil.formatDate;
import static org.smartregister.util.StringUtil.humanize;
import static org.smartregister.util.StringUtil.humanizeAndDoUPPERCASE;
import static org.smartregister.util.StringUtil.replaceAndHumanize;

public class FPClient implements FPSmartRegisterClient {

    public static final String CONDOM_REFILL = "Condom Refill";
    public static final String DPMA_INJECTABLE_REFILL = "DMPA Injectable Refill";
    public static final String OCP_REFILL = "OCP Refill";
    public static final String MALE_STERILIZATION_FOLLOW_UP_1 = "Male sterilization Followup 1";
    public static final String MALE_STERILIZATION_FOLLOW_UP_2 = "Male sterilization Followup 2";
    public static final String FEMALE_STERILIZATION_FOLLOW_UP_1 = "Female sterilization Followup 1";
    public static final String FEMALE_STERILIZATION_FOLLOW_UP_2 = "Female sterilization Followup 2";
    public static final String FEMALE_STERILIZATION_FOLLOW_UP_3 = "Female sterilization Followup 3";
    public static final String IUD_FOLLOW_UP_1 = "IUD Followup 1";
    public static final String IUD_FOLLOW_UP_2 = "IUD Followup 2";
    public static final String FP_FOLLOW_UP = "FP Followup";
    public static final String REFERRAL_FOLLOW_UP = "FP Referral Followup";

    private static final String[] refillTypes = {CONDOM_REFILL, DPMA_INJECTABLE_REFILL, OCP_REFILL};

    private static final String[] followUpTypes = {MALE_STERILIZATION_FOLLOW_UP_1,
            MALE_STERILIZATION_FOLLOW_UP_2, FEMALE_STERILIZATION_FOLLOW_UP_1,
            FEMALE_STERILIZATION_FOLLOW_UP_2, FEMALE_STERILIZATION_FOLLOW_UP_3, IUD_FOLLOW_UP_1,
            IUD_FOLLOW_UP_2};

    private static Map<String, String> alertNameToFPMethodMap = new HashMap<String, String>();

    static {
        alertNameToFPMethodMap.put(CONDOM_REFILL, "condom");
        alertNameToFPMethodMap.put(DPMA_INJECTABLE_REFILL, "dmpa_injectable");
        alertNameToFPMethodMap.put(OCP_REFILL, "ocp");
        alertNameToFPMethodMap.put(MALE_STERILIZATION_FOLLOW_UP_1, "male_sterilization");
        alertNameToFPMethodMap.put(MALE_STERILIZATION_FOLLOW_UP_2, "male_sterilization");
        alertNameToFPMethodMap.put(FEMALE_STERILIZATION_FOLLOW_UP_1, "female_sterilization");
        alertNameToFPMethodMap.put(FEMALE_STERILIZATION_FOLLOW_UP_2, "female_sterilization");
        alertNameToFPMethodMap.put(FEMALE_STERILIZATION_FOLLOW_UP_3, "female_sterilization");
        alertNameToFPMethodMap.put(IUD_FOLLOW_UP_1, "iud");
        alertNameToFPMethodMap.put(IUD_FOLLOW_UP_2, "iud");

    }

    private String entityId;
    private String entityIdToSavePhoto;
    private String name;
    private String husbandName;
    private String age;
    private String ec_number;
    private String village;
    private String fp_method;
    private String num_pregnancies;
    private String parity;
    private String num_living_children;
    private String num_stillbirths;
    private String num_abortions;
    private boolean isHighPriority;
    private String family_planning_method_change_date;
    private String photo_path;
    private boolean is_youngest_child_under_two;
    private String youngest_child_age;
    private List<AlertDTO> alerts;
    private String complication_date;
    private String caste;
    private String economicStatus;
    private String fp_method_followup_date;
    private String iudPlace;
    private String iudPerson;
    private String numberOfCondomsSupplied;
    private String numberOfCentchromanPillsDelivered;
    private String numberOfOCPDelivered;
    private String condomSideEffect;
    private String iudSidEffect;
    private String ocpSideEffect;
    private String sterilizationSideEffect;
    private String injectableSideEffect;
    private String otherSideEffect;
    private String highPriorityReason;
    private RefillFollowUps refillFollowUps;

    public FPClient(String entityId, String name, String husbandName, String village, String
            ecNumber) {
        this.entityId = entityId;
        this.entityIdToSavePhoto = entityId;
        this.name = name;
        this.husbandName = husbandName;
        this.village = village;
        this.ec_number = ecNumber;
    }

    public FPClient preprocess() {
        return setRefillFollowUp();
    }

    public FPClient setRefillFollowUp() {
        List<AlertDTO> alerts = alerts();
        AlertDTO fpReferralFollowUpAlert = getFPReferralFollowUpAlert(alerts);
        AlertDTO fpFollowUpAlert = getFPFollowUpAlert(alerts);
        if (fpReferralFollowUpAlert != null) {
            this.withRefillFollowUps(
                    new RefillFollowUps(REFERRAL_FOLLOW_UP, fpReferralFollowUpAlert,
                            CoreLibrary.getInstance().context().getStringResource(R.string.str_referral)));
        } else if (fpFollowUpAlert != null) {
            this.withRefillFollowUps(new RefillFollowUps(FP_FOLLOW_UP, fpFollowUpAlert,
                    CoreLibrary.getInstance().context().getStringResource(R.string.str_follow_up)));
        } else {
            this.withRefillFollowUps(getOtherFPMethod(alerts));
        }
        return this;
    }

    private RefillFollowUps getOtherFPMethod(List<AlertDTO> alerts) {
        for (AlertDTO alert : alerts) {
            if (isOtherFPMethodAlert(alert) && isAlertBelongsTo(alert, followUpTypes)) {
                return new RefillFollowUps(alert.name(), alert,
                        CoreLibrary.getInstance().context().getStringResource(R.string.str_follow_up));
            } else if (isOtherFPMethodAlert(alert) && isAlertBelongsTo(alert, refillTypes)) {
                return new RefillFollowUps(alert.name(), alert,
                        CoreLibrary.getInstance().context().getStringResource(R.string.str_refill));
            }
        }
        return null;
    }

    private AlertDTO getFPFollowUpAlert(List<AlertDTO> alerts) {
        for (AlertDTO alert : alerts) {
            if (isFPFollowUpAlert(alert)) {
                return alert;
            }
        }
        return null;
    }

    private AlertDTO getFPReferralFollowUpAlert(List<AlertDTO> alerts) {
        for (AlertDTO alert : alerts) {
            if (isFPReferralFollowUpAlert(alert)) {
                return alert;
            }
        }
        return null;
    }

    private boolean isAlertBelongsTo(AlertDTO alert, String[] types) {
        return Arrays.asList(types).contains(alert.name());
    }

    private boolean isOtherFPMethodAlert(AlertDTO alert) {
        return this.fpMethod().name().equalsIgnoreCase(alertNameToFPMethodMap.get(alert.name()));
    }

    private boolean isFPFollowUpAlert(AlertDTO alert) {
        return alert.name().equalsIgnoreCase(FP_FOLLOW_UP);
    }

    private boolean isFPReferralFollowUpAlert(AlertDTO alert) {
        return alert.name().equalsIgnoreCase(REFERRAL_FOLLOW_UP);
    }

    @Override
    public RefillFollowUps refillFollowUps() {
        return refillFollowUps;
    }

    @Override
    public String highPriorityReason() {
        return humanize(highPriorityReason);
    }

    public FPClient withRefillFollowUps(RefillFollowUps refillFollowUps) {
        this.refillFollowUps = refillFollowUps;
        return this;
    }

    @Override
    public String entityId() {
        return entityId;
    }

    @Override
    public String name() {
        return humanize(name);
    }

    @Override
    public String displayName() {
        return name();
    }

    @Override
    public String village() {
        return humanize(village);
    }

    public String wifeName() {
        return name;
    }

    @Override
    public String husbandName() {
        return humanize(husbandName);
    }

    @Override
    public int age() {
        return IntegerUtil.tryParse(age, 0);
    }

    @Override
    public int ageInDays() {
        return IntegerUtil.tryParse(age, 0) * 365;
    }

    @Override
    public String ageInString() {
        return "(" + age + ")";
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

    @Override
    public String locationStatus() {
        return null;
    }

    @Override
    public boolean satisfiesFilter(String filter) {
        return name.toLowerCase(Utils.getDefaultLocale()).startsWith(filter.toLowerCase()) || String
                .valueOf(ec_number).startsWith(filter);
    }

    @Override
    public int compareName(SmartRegisterClient client) {
        return this.name().compareToIgnoreCase(client.name());
    }

    public FPClient withAge(String age) {
        this.age = age;
        return this;
    }

    public FPClient withFPMethod(String fp_method) {
        this.fp_method = fp_method;
        return this;
    }

    public FPClient withNumberOfPregnancies(String num_pregnancies) {
        Integer value = IntegerUtil.tryParse(num_pregnancies, 0);
        this.num_pregnancies = value > 8 ? "8+" : value.toString();
        return this;
    }

    public FPClient withParity(String parity) {
        Integer value = IntegerUtil.tryParse(parity, 0);
        this.parity = value > 8 ? "8+" : value.toString();
        return this;
    }

    public FPClient withNumberOfLivingChildren(String num_living_children) {
        Integer value = IntegerUtil.tryParse(num_living_children, 0);
        this.num_living_children = value > 8 ? "8+" : value.toString();
        return this;
    }

    public FPClient withNumberOfStillBirths(String num_stillbirths) {
        Integer value = IntegerUtil.tryParse(num_stillbirths, 0);
        this.num_stillbirths = value > 8 ? "8+" : value.toString();
        return this;
    }

    public FPClient withNumberOfAbortions(String num_abortions) {
        Integer value = IntegerUtil.tryParse(num_abortions, 0);
        this.num_abortions = value > 8 ? "8+" : value.toString();
        return this;
    }

    public FPClient withIsHighPriority(boolean highPriority) {
        isHighPriority = highPriority;
        return this;
    }

    public FPClient withFamilyPlanningMethodChangeDate(String family_planning_method_change_date) {
        this.family_planning_method_change_date = family_planning_method_change_date;
        return this;
    }

    public FPClient withPhotoPath(String photo_path) {
        this.photo_path = photo_path;
        return this;
    }

    public FPClient withIsYoungestChildUnderTwo(boolean is_youngest_child_under_two) {
        this.is_youngest_child_under_two = is_youngest_child_under_two;
        return this;
    }

    public FPClient withYoungestChildAge(String youngest_child_age) {
        this.youngest_child_age = youngest_child_age;
        return this;
    }

    public FPClient withAlerts(List<AlertDTO> alerts) {
        this.alerts = alerts;
        return this;
    }

    public FPClient withComplicationDate(String complication_date) {
        this.complication_date = complication_date;
        return this;
    }

    public FPClient withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public FPClient withEconomicStatus(String economicStatus) {
        this.economicStatus = economicStatus;
        return this;
    }

    public FPClient withFPMethodFollowupDate(String fp_method_followup_date) {
        this.fp_method_followup_date = fp_method_followup_date;
        return this;
    }

    public FPClient withIUDPlace(String iudPlace) {
        this.iudPlace = iudPlace;
        return this;
    }

    public FPClient withIUDPerson(String iudPerson) {
        this.iudPerson = iudPerson;
        return this;
    }

    public FPClient withNumberOfCondomsSupplied(String numberOfCondomsSupplied) {
        this.numberOfCondomsSupplied = numberOfCondomsSupplied;
        return this;
    }

    public FPClient withNumberOfCentchromanPillsDelivered(String numberOfCentchromanPillsDelivered) {
        this.numberOfCentchromanPillsDelivered = numberOfCentchromanPillsDelivered;
        return this;
    }

    public FPClient withNumberOfOCPDelivered(String numberOfOCPDelivered) {
        this.numberOfOCPDelivered = numberOfOCPDelivered;
        return this;
    }

    public FPClient withCondomSideEffect(String condomSideEffect) {
        this.condomSideEffect = condomSideEffect;
        return this;
    }

    public FPClient withIUDSidEffect(String iudSidEffect) {
        this.iudSidEffect = iudSidEffect;
        return this;
    }

    public FPClient withOCPSideEffect(String ocpSideEffect) {
        this.ocpSideEffect = ocpSideEffect;
        return this;
    }

    public FPClient withSterilizationSideEffect(String sterilizationSideEffect) {
        this.sterilizationSideEffect = sterilizationSideEffect;
        return this;
    }

    public FPClient withInjectableSideEffect(String injectableSideEffect) {
        this.injectableSideEffect = injectableSideEffect;
        return this;
    }

    public FPClient withOtherSideEffect(String otherSideEffect) {
        this.otherSideEffect = otherSideEffect;
        return this;
    }

    public FPClient withHighPriorityReason(String highPriorityReason) {
        this.highPriorityReason = highPriorityReason;
        return this;
    }

    public String numberOfPregnancies() {
        return num_pregnancies;
    }

    public String parity() {
        return parity;
    }

    public String numberOfLivingChildren() {
        return num_living_children;
    }

    public String numberOfStillbirths() {
        return num_stillbirths;
    }

    public String numberOfAbortions() {
        return num_abortions;
    }

    @Override
    public String familyPlanningMethodChangeDate() {
        return formatDate(family_planning_method_change_date, "dd/MM/YYYY");
    }

    @Override
    public String numberOfOCPDelivered() {
        return numberOfOCPDelivered;
    }

    @Override
    public String numberOfCondomsSupplied() {
        return numberOfCondomsSupplied;
    }

    @Override
    public String numberOfCentchromanPillsDelivered() {
        return numberOfCentchromanPillsDelivered;
    }

    @Override
    public String iudPerson() {
        return humanizeAndDoUPPERCASE(iudPerson);
    }

    @Override
    public String iudPlace() {
        return humanizeAndDoUPPERCASE(iudPlace);
    }

    @Override
    public Integer ecNumber() {
        return IntegerUtil.tryParse(ec_number, 0);
    }

    @Override
    public FPMethod fpMethod() {
        return FPMethod.tryParse(this.fp_method, FPMethod.NONE);
    }

    @Override
    public String youngestChildAge() {
        return StringUtils.isBlank(youngest_child_age) ? null : (youngest_child_age + "m");
    }

    @Override
    public String complicationDate() {
        return formatDate(complication_date, "dd/MM/YYYY");
    }

    @Override
    public String condomSideEffect() {
        return replaceAndHumanize(condomSideEffect, SPACE, COMMA_WITH_SPACE);
    }

    @Override
    public String iudSidEffect() {
        return replaceAndHumanize(iudSidEffect, SPACE, COMMA_WITH_SPACE);
    }

    @Override
    public String ocpSideEffect() {
        return replaceAndHumanize(ocpSideEffect, SPACE, COMMA_WITH_SPACE);
    }

    @Override
    public String sterilizationSideEffect() {
        return replaceAndHumanize(sterilizationSideEffect, SPACE, COMMA_WITH_SPACE);
    }

    @Override
    public String injectableSideEffect() {
        return replaceAndHumanize(injectableSideEffect, SPACE, COMMA_WITH_SPACE);
    }

    @Override
    public String otherSideEffect() {
        return replaceAndHumanize(otherSideEffect, SPACE, COMMA_WITH_SPACE);
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

    @Override
    public List<AlertDTO> alerts() {
        return alerts;
    }

}
