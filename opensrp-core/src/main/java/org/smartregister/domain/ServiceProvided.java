package org.smartregister.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.smartregister.domain.mapper.TTMapper;

import java.util.Map;

import static org.smartregister.AllConstants.ANCVisitFields.BP_DIASTOLIC;
import static org.smartregister.AllConstants.ANCVisitFields.BP_SYSTOLIC;
import static org.smartregister.AllConstants.ANCVisitFields.WEIGHT;
import static org.smartregister.AllConstants.DeliveryPlanFields.ASHA_PHONE_NUMBER;
import static org.smartregister.AllConstants.DeliveryPlanFields.BIRTH_COMPANION;
import static org.smartregister.AllConstants.DeliveryPlanFields.DELIVERY_FACILITY_NAME;
import static org.smartregister.AllConstants.DeliveryPlanFields.PHONE_NUMBER;
import static org.smartregister.AllConstants.DeliveryPlanFields.REVIEWED_HRP_STATUS;
import static org.smartregister.AllConstants.DeliveryPlanFields.TRANSPORTATION_PLAN;
import static org.smartregister.AllConstants.VitaminAFields.VITAMIN_A_DOSE;
import static org.smartregister.AllConstants.VitaminAFields.VITAMIN_A_PLACE;
import static org.smartregister.util.EasyMap.create;
import static org.smartregister.util.EasyMap.mapOf;

public class ServiceProvided {
    public static final String IFA_SERVICE_PROVIDED_NAME = "IFA";
    public static final String TT_1_SERVICE_PROVIDED_NAME = "TT 1";
    public static final String TT_2_SERVICE_PROVIDED_NAME = "TT 2";
    public static final String TT_BOOSTER_SERVICE_PROVIDED_NAME = "TT Booster";
    public static final String HB_TEST_SERVICE_PROVIDED_NAME = "Hb Test";
    public static final String ANC_SERVICE_PREFIX = "ANC ";
    public static final String PNC_SERVICE_PROVIDED_NAME = "PNC";
    public static final String ANC_1_SERVICE_PROVIDED_NAME = ANC_SERVICE_PREFIX + "1";
    public static final String ANC_2_SERVICE_PROVIDED_NAME = ANC_SERVICE_PREFIX + "2";
    public static final String ANC_3_SERVICE_PROVIDED_NAME = ANC_SERVICE_PREFIX + "3";
    public static final String ANC_4_SERVICE_PROVIDED_NAME = ANC_SERVICE_PREFIX + "4";
    public static final String PNC_VISIT_DAY = "day";
    public static final String CHILD_ILLNESS_SERVICE_PROVIDED_NAME = "Illness Visit";
    public static final String VITAMIN_A_SERVICE_PROVIDED_NAME = "Vitamin A";
    public static final String DELIVERY_PLAN_SERVICE_PROVIDED_NAME = "Delivery Plan";
    private final String entityId;
    private final String name;
    private final String date;
    private final Map<String, String> data;

    public ServiceProvided(String entityId, String name, String date, Map<String, String> data) {
        this.name = name;
        this.date = date;
        this.data = data;
        this.entityId = entityId;
    }

    public static ServiceProvided forTTDose(String entityId, String ttDose, String date) {
        String mappedTTDose = TTMapper.valueOf(ttDose).value();
        return new ServiceProvided(entityId, mappedTTDose, date, mapOf("dose", mappedTTDose));
    }

    public static ServiceProvided forHBTest(String entityId, String hbLevel, String date) {
        return new ServiceProvided(entityId, HB_TEST_SERVICE_PROVIDED_NAME, date,
                mapOf("hbLevel", hbLevel));
    }

    public static ServiceProvided forIFATabletsGiven(String entityId, String
            numberOfIFATabletsGiven, String date) {
        return new ServiceProvided(entityId, IFA_SERVICE_PROVIDED_NAME, date,
                mapOf("dose", numberOfIFATabletsGiven));
    }

    public static ServiceProvided forANCCareProvided(String entityId, String ancVisitNumber,
                                                     String date, String bpSystolic, String
                                                             bpDiastolic, String weight) {
        return new ServiceProvided(entityId, ANC_SERVICE_PREFIX + ancVisitNumber, date,
                create(BP_SYSTOLIC, bpSystolic).put(BP_DIASTOLIC, bpDiastolic).put(WEIGHT, weight)
                        .map());
    }

    public static ServiceProvided forMotherPNCVisit(String entityId, String pncVisitDay, String
            date) {
        return new ServiceProvided(entityId, PNC_SERVICE_PROVIDED_NAME, date,
                mapOf(PNC_VISIT_DAY, pncVisitDay));
    }

    public static ServiceProvided forDeliveryPlan(String entityId, String deliveryFacilityName,
                                                  String transportationPlan, String
                                                          birthCompanion, String ashaPhoneNumber,
                                                  String familyContactNumber, String
                                                          highRiskReason, String date) {
        return new ServiceProvided(entityId, DELIVERY_PLAN_SERVICE_PROVIDED_NAME, date,
                create(DELIVERY_FACILITY_NAME, deliveryFacilityName)
                        .put(TRANSPORTATION_PLAN, transportationPlan)
                        .put(BIRTH_COMPANION, birthCompanion)
                        .put(ASHA_PHONE_NUMBER, ashaPhoneNumber)
                        .put(PHONE_NUMBER, familyContactNumber)
                        .put(REVIEWED_HRP_STATUS, highRiskReason).map());
    }

    public static ServiceProvided forChildPNCVisit(String entityId, String pncVisitDay, String
            date) {
        return new ServiceProvided(entityId, PNC_SERVICE_PROVIDED_NAME, date,
                mapOf(PNC_VISIT_DAY, pncVisitDay));
    }

    public static ServiceProvided forChildImmunization(String entityId, String immunization,
                                                       String date) {
        return new ServiceProvided(entityId, immunization, date, null);
    }

    public static ServiceProvided forChildIllnessVisit(String entityId, String date, Map<String,
            String> childIllnessMap) {
        return new ServiceProvided(entityId, CHILD_ILLNESS_SERVICE_PROVIDED_NAME, date,
                childIllnessMap);
    }

    public static ServiceProvided forVitaminAProvided(String entityId, String date, String
            vitaminADose, String vitaminAPlace) {
        return new ServiceProvided(entityId, VITAMIN_A_SERVICE_PROVIDED_NAME, date,
                create(VITAMIN_A_DOSE, vitaminADose).put(VITAMIN_A_PLACE, vitaminAPlace).map());
    }

    public String name() {
        return name;
    }

    public String date() {
        return date;
    }

    public Map<String, String> data() {
        return data;
    }

    public String entityId() {
        return entityId;
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
