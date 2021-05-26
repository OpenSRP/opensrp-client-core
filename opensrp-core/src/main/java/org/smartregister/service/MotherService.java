package org.smartregister.service;

import org.smartregister.AllConstants;
import org.smartregister.domain.Mother;
import org.smartregister.domain.ServiceProvided;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.repository.AllTimelineEvents;

import static org.smartregister.AllConstants.ANCCloseFields.CLOSE_REASON_FIELD_NAME;
import static org.smartregister.AllConstants.ANCCloseFields.DEATH_OF_WOMAN_FIELD_VALUE;
import static org.smartregister.AllConstants.ANCRegistrationFields.REGISTRATION_DATE;
import static org.smartregister.AllConstants.ANCVisitFields.ANC_VISIT_DATE;
import static org.smartregister.AllConstants.ANCVisitFields.ANC_VISIT_NUMBER;
import static org.smartregister.AllConstants.ANCVisitFields.BP_DIASTOLIC;
import static org.smartregister.AllConstants.ANCVisitFields.BP_SYSTOLIC;
import static org.smartregister.AllConstants.ANCVisitFields.REFERENCE_DATE;
import static org.smartregister.AllConstants.ANCVisitFields.TEMPERATURE;
import static org.smartregister.AllConstants.ANCVisitFields.THAYI_CARD_NUMBER;
import static org.smartregister.AllConstants.ANCVisitFields.WEIGHT;
import static org.smartregister.AllConstants.BOOLEAN_FALSE;
import static org.smartregister.AllConstants.CommonFormFields.SUBMISSION_DATE;
import static org.smartregister.AllConstants.DeliveryOutcomeFields.DID_MOTHER_SURVIVE;
import static org.smartregister.AllConstants.DeliveryOutcomeFields.DID_WOMAN_SURVIVE;
import static org.smartregister.AllConstants.DeliveryPlanFields.ASHA_PHONE_NUMBER;
import static org.smartregister.AllConstants.DeliveryPlanFields.BIRTH_COMPANION;
import static org.smartregister.AllConstants.DeliveryPlanFields.DELIVERY_FACILITY_NAME;
import static org.smartregister.AllConstants.DeliveryPlanFields.PHONE_NUMBER;
import static org.smartregister.AllConstants.DeliveryPlanFields.REVIEWED_HRP_STATUS;
import static org.smartregister.AllConstants.DeliveryPlanFields.TRANSPORTATION_PLAN;
import static org.smartregister.AllConstants.HbTestFields.HB_LEVEL;
import static org.smartregister.AllConstants.HbTestFields.HB_TEST_DATE;
import static org.smartregister.AllConstants.IFAFields.IFA_TABLETS_DATE;
import static org.smartregister.AllConstants.IFAFields.NUMBER_OF_IFA_TABLETS_GIVEN;
import static org.smartregister.AllConstants.PNCCloseFields.DEATH_OF_MOTHER_FIELD_VALUE;
import static org.smartregister.AllConstants.TTFields.TT_DATE;
import static org.smartregister.AllConstants.TTFields.TT_DOSE;
import static org.smartregister.domain.ServiceProvided.forHBTest;
import static org.smartregister.domain.ServiceProvided.forTTDose;
import static org.smartregister.domain.TimelineEvent.forANCCareProvided;
import static org.smartregister.domain.TimelineEvent.forDeliveryPlan;
import static org.smartregister.domain.TimelineEvent.forIFATabletsGiven;
import static org.smartregister.domain.TimelineEvent.forMotherPNCVisit;
import static org.smartregister.domain.TimelineEvent.forStartOfPregnancy;
import static org.smartregister.domain.TimelineEvent.forStartOfPregnancyForEC;
import static org.smartregister.domain.TimelineEvent.forTTShotProvided;
import static org.smartregister.util.EasyMap.create;
import static org.smartregister.util.IntegerUtil.tryParse;
import static org.smartregister.util.Log.logWarn;

public class MotherService {
    public static final String MOTHER_ID = "motherId";
    public static String submissionDate = "submissionDate";
    private AllBeneficiaries allBeneficiaries;
    private AllTimelineEvents allTimelines;
    private AllEligibleCouples allEligibleCouples;
    private ServiceProvidedService serviceProvidedService;

    public MotherService(AllBeneficiaries allBeneficiaries, AllEligibleCouples
            allEligibleCouples, AllTimelineEvents allTimelineEvents, ServiceProvidedService
                                 serviceProvidedService) {
        this.allBeneficiaries = allBeneficiaries;
        this.allTimelines = allTimelineEvents;
        this.allEligibleCouples = allEligibleCouples;
        this.serviceProvidedService = serviceProvidedService;
    }

    public void registerANC(FormSubmission submission) {
        addTimelineEventsForMotherRegistration(submission);
    }

    public void registerOutOfAreaANC(FormSubmission submission) {
        addTimelineEventsForMotherRegistration(submission);
    }

    public void ancVisit(FormSubmission submission) {
        allTimelines.add(forANCCareProvided(submission.entityId(),
                submission.getFieldValue(ANC_VISIT_NUMBER),
                submission.getFieldValue(ANC_VISIT_DATE),
                create(BP_SYSTOLIC, submission.getFieldValue(BP_SYSTOLIC))
                        .put(BP_DIASTOLIC, submission.getFieldValue(BP_DIASTOLIC))
                        .put(TEMPERATURE, submission.getFieldValue(TEMPERATURE))
                        .put(WEIGHT, submission.getFieldValue(WEIGHT)).map()));
        serviceProvidedService.add(ServiceProvided.forANCCareProvided(submission.entityId(),
                submission.getFieldValue(ANC_VISIT_NUMBER),
                submission.getFieldValue(ANC_VISIT_DATE), submission.getFieldValue(BP_SYSTOLIC),
                submission.getFieldValue(BP_DIASTOLIC), submission.getFieldValue(WEIGHT)));
    }

    public void close(FormSubmission submission) {
        close(submission.entityId(), submission.getFieldValue(CLOSE_REASON_FIELD_NAME));
    }

    public void close(String entityId, String reason) {
        Mother mother = allBeneficiaries.findMotherWithOpenStatus(entityId);
        if (mother == null) {
            logWarn("Tried to close non-existent mother. Entity ID: " + entityId);
            return;
        }

        allBeneficiaries.closeMother(entityId);
        if (DEATH_OF_WOMAN_FIELD_VALUE.equalsIgnoreCase(reason) || DEATH_OF_MOTHER_FIELD_VALUE
                .equalsIgnoreCase(reason)
                || AllConstants.ANCCloseFields.PERMANENT_RELOCATION_FIELD_VALUE
                .equalsIgnoreCase(reason)
                || AllConstants.PNCCloseFields.PERMANENT_RELOCATION_FIELD_VALUE
                .equalsIgnoreCase(reason)) {
            allEligibleCouples.close(mother.ecCaseId());
        }
    }

    public void ttProvided(FormSubmission submission) {
        allTimelines.add(forTTShotProvided(submission.entityId(), submission.getFieldValue(TT_DOSE),
                submission.getFieldValue(TT_DATE)));
        serviceProvidedService
                .add(forTTDose(submission.entityId(), submission.getFieldValue(TT_DOSE),
                        submission.getFieldValue(TT_DATE)));
    }

    public void ifaTabletsGiven(FormSubmission submission) {
        String numberOfIFATabletsGiven = submission.getFieldValue(NUMBER_OF_IFA_TABLETS_GIVEN);
        if (tryParse(numberOfIFATabletsGiven, 0) > 0) {
            allTimelines.add(forIFATabletsGiven(submission.entityId(), numberOfIFATabletsGiven,
                    submission.getFieldValue(IFA_TABLETS_DATE)));
            serviceProvidedService.add(ServiceProvided
                    .forIFATabletsGiven(submission.entityId(), numberOfIFATabletsGiven,
                            submission.getFieldValue(IFA_TABLETS_DATE)));
        }
    }

    private void addTimelineEventsForMotherRegistration(FormSubmission submission) {
        allTimelines.add(forStartOfPregnancy(submission.getFieldValue(MOTHER_ID),
                submission.getFieldValue(REGISTRATION_DATE),
                submission.getFieldValue(REFERENCE_DATE)));
        allTimelines.add(forStartOfPregnancyForEC(submission.entityId(),
                submission.getFieldValue(THAYI_CARD_NUMBER),
                submission.getFieldValue(REGISTRATION_DATE),
                submission.getFieldValue(REFERENCE_DATE)));
    }

    public void hbTest(FormSubmission submission) {
        serviceProvidedService
                .add(forHBTest(submission.entityId(), submission.getFieldValue(HB_LEVEL),
                        submission.getFieldValue(HB_TEST_DATE)));
    }

    public void deliveryOutcome(FormSubmission submission) {
        Mother mother = allBeneficiaries.findMotherWithOpenStatus(submission.entityId());
        if (mother == null) {
            logWarn("Failed to handle delivery outcome for mother. Entity ID: " + submission
                    .entityId());
            return;
        }
        if (BOOLEAN_FALSE.equals(submission.getFieldValue(DID_WOMAN_SURVIVE)) || BOOLEAN_FALSE
                .equals(submission.getFieldValue(DID_MOTHER_SURVIVE))) {
            allBeneficiaries.closeMother(submission.entityId());
            allEligibleCouples.close(mother.ecCaseId());
            return;
        }

        allBeneficiaries.switchMotherToPNC(submission.entityId());
    }

    public void pncVisitHappened(FormSubmission submission) {
        allTimelines.add(forMotherPNCVisit(submission.entityId(),
                submission.getFieldValue(AllConstants.PNCVisitFields.PNC_VISIT_DAY),
                submission.getFieldValue(AllConstants.PNCVisitFields.PNC_VISIT_DATE),
                submission.getFieldValue(AllConstants.PNCVisitFields.BP_SYSTOLIC),
                submission.getFieldValue(AllConstants.PNCVisitFields.BP_DIASTOLIC),
                submission.getFieldValue(AllConstants.PNCVisitFields.TEMPERATURE),
                submission.getFieldValue(AllConstants.PNCVisitFields.HB_LEVEL)));
        serviceProvidedService.add(ServiceProvided.forMotherPNCVisit(submission.entityId(),
                submission.getFieldValue(AllConstants.PNCVisitFields.PNC_VISIT_DAY),
                submission.getFieldValue(AllConstants.PNCVisitFields.PNC_VISIT_DATE)));

        String numberOfIFATabletsGiven = submission
                .getFieldValue(AllConstants.PNCVisitFields.NUMBER_OF_IFA_TABLETS_GIVEN);
        if (tryParse(numberOfIFATabletsGiven, 0) > 0) {
            allTimelines.add(forIFATabletsGiven(submission.entityId(), numberOfIFATabletsGiven,
                    submission.getFieldValue(SUBMISSION_DATE)));
        }
    }

    public void deliveryPlan(FormSubmission submission) {
        allTimelines.add(forDeliveryPlan(submission.entityId(),
                submission.getFieldValue(DELIVERY_FACILITY_NAME),
                submission.getFieldValue(TRANSPORTATION_PLAN),
                submission.getFieldValue(BIRTH_COMPANION),
                submission.getFieldValue(ASHA_PHONE_NUMBER), submission.getFieldValue(PHONE_NUMBER),
                submission.getFieldValue(REVIEWED_HRP_STATUS),
                submission.getFieldValue(SUBMISSION_DATE)));

        serviceProvidedService.add(ServiceProvided.forDeliveryPlan(submission.entityId(),
                submission.getFieldValue(DELIVERY_FACILITY_NAME),
                submission.getFieldValue(TRANSPORTATION_PLAN),
                submission.getFieldValue(BIRTH_COMPANION),
                submission.getFieldValue(ASHA_PHONE_NUMBER), submission.getFieldValue(PHONE_NUMBER),
                submission.getFieldValue(REVIEWED_HRP_STATUS),
                submission.getFieldValue(SUBMISSION_DATE)));
    }
}
