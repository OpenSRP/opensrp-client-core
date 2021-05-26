package org.smartregister.view.controller;

import org.smartregister.domain.Alert;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.service.AlertService;
import org.smartregister.util.Cache;
import org.smartregister.util.CacheableData;
import org.smartregister.view.contract.AlertDTO;
import org.smartregister.view.contract.FPClient;
import org.smartregister.view.contract.FPClients;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.AllConstants.DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH;

public class FPSmartRegisterController {
    public static final String OCP_REFILL_ALERT_NAME = "OCP Refill";
    public static final String CONDOM_REFILL_ALERT_NAME = "Condom Refill";
    public static final String DMPA_INJECTABLE_REFILL_ALERT_NAME = "DMPA Injectable Refill";
    public static final String FEMALE_STERILIZATION_FOLLOWUP_1_ALERT_NAME =
            "Female " + "sterilization" + " Followup 1";
    public static final String FEMALE_STERILIZATION_FOLLOWUP_2_ALERT_NAME =
            "Female " + "sterilization" + " Followup 2";
    public static final String FEMALE_STERILIZATION_FOLLOWUP_3_ALERT_NAME =
            "Female " + "sterilization" + " Followup 3";
    public static final String MALE_STERILIZATION_FOLLOWUP_1_ALERT_NAME =
            "Male sterilization " + "Followup 1";
    public static final String MALE_STERILIZATION_FOLLOWUP_2_ALERT_NAME =
            "Male sterilization " + "Followup 2";
    public static final String IUD_FOLLOWUP_1_ALERT_NAME = "IUD Followup 1";
    public static final String IUD_FOLLOWUP_2_ALERT_NAME = "IUD Followup 2";
    public static final String FP_FOLLOWUP_ALERT_NAME = "FP Followup";
    public static final String FP_REFERRAL_FOLLOWUP_ALERT_NAME = "FP Referral Followup";

    private final static String FP_CLIENTS_LIST = "FPClientsList";

    private final AllEligibleCouples allEligibleCouples;
    private final AllBeneficiaries allBeneficiaries;
    private final AlertService alertService;
    private Cache<String> cache;
    private Cache<FPClients> fpClientsCache;

    public FPSmartRegisterController(AllEligibleCouples allEligibleCouples, AllBeneficiaries
            allBeneficiaries, AlertService alertService, Cache<String> cache, Cache<FPClients>
                                             fpClientsCache) {
        this.allEligibleCouples = allEligibleCouples;
        this.allBeneficiaries = allBeneficiaries;
        this.alertService = alertService;
        this.cache = cache;
        this.fpClientsCache = fpClientsCache;
    }

    private List<AlertDTO> getFPAlertsForEC(String entityId) {
        List<Alert> alerts = alertService
                .findByEntityIdAndAlertNames(entityId, OCP_REFILL_ALERT_NAME,
                        CONDOM_REFILL_ALERT_NAME, DMPA_INJECTABLE_REFILL_ALERT_NAME,
                        FEMALE_STERILIZATION_FOLLOWUP_1_ALERT_NAME,
                        FEMALE_STERILIZATION_FOLLOWUP_2_ALERT_NAME,
                        FEMALE_STERILIZATION_FOLLOWUP_3_ALERT_NAME,
                        MALE_STERILIZATION_FOLLOWUP_1_ALERT_NAME,
                        MALE_STERILIZATION_FOLLOWUP_2_ALERT_NAME, IUD_FOLLOWUP_1_ALERT_NAME,
                        IUD_FOLLOWUP_2_ALERT_NAME, FP_FOLLOWUP_ALERT_NAME,
                        FP_REFERRAL_FOLLOWUP_ALERT_NAME);
        ArrayList<AlertDTO> alertDTOs = new ArrayList<AlertDTO>();
        for (Alert alert : alerts) {
            alertDTOs.add(new AlertDTO(alert.visitCode(), String.valueOf(alert.status()),
                    alert.startDate()));
        }
        return alertDTOs;
    }

    public FPClients getClients() {
        return fpClientsCache.get(FP_CLIENTS_LIST, new CacheableData<FPClients>() {
            @Override
            public FPClients fetch() {
                List<EligibleCouple> ecs = allEligibleCouples.all();
                FPClients fpClients = new FPClients();
                for (EligibleCouple ec : ecs) {
                    if (allBeneficiaries.isPregnant(ec.caseId())) {
                        continue;
                    }

                    String photoPath =
                            isBlank(ec.photoPath()) ? DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH
                                    : ec.photoPath();
                    List<AlertDTO> alerts = getFPAlertsForEC(ec.caseId());
                    FPClient fpClient = new FPClient(ec.caseId(), ec.wifeName(), ec.husbandName(),
                            ec.village(), ec.ecNumber()).withAge(ec.age())
                            .withFPMethod(ec.getDetail("currentMethod"))
                            .withFamilyPlanningMethodChangeDate(
                                    ec.getDetail("familyPlanningMethodChangeDate"))
                            .withComplicationDate(ec.getDetail("complicationDate"))
                            .withIUDPlace(ec.getDetail("iudPlace"))
                            .withIUDPerson(ec.getDetail("iudPerson"))
                            .withNumberOfCondomsSupplied(ec.getDetail("numberOfCondomsSupplied"))
                            .withNumberOfCentchromanPillsDelivered(
                                    ec.getDetail("numberOfCentchromanPillsDelivered"))
                            .withNumberOfOCPDelivered(ec.getDetail("numberOfOCPDelivered"))
                            .withFPMethodFollowupDate(ec.getDetail("fpFollowupDate"))
                            .withCaste(ec.getDetail("caste"))
                            .withEconomicStatus(ec.getDetail("economicStatus"))
                            .withNumberOfPregnancies(ec.getDetail("numberOfPregnancies"))
                            .withParity(ec.getDetail("parity"))
                            .withNumberOfLivingChildren(ec.getDetail("numberOfLivingChildren"))
                            .withNumberOfStillBirths(ec.getDetail("numberOfStillBirths"))
                            .withNumberOfAbortions(ec.getDetail("numberOfAbortions"))
                            .withIsYoungestChildUnderTwo(ec.isYoungestChildUnderTwo())
                            .withYoungestChildAge(ec.getDetail("youngestChildAge"))
                            .withIsHighPriority(ec.isHighPriority()).withPhotoPath(photoPath)
                            .withAlerts(alerts)
                            .withCondomSideEffect(ec.getDetail("condomSideEffect"))
                            .withIUDSidEffect(ec.getDetail("iudSidEffect"))
                            .withOCPSideEffect(ec.getDetail("ocpSideEffect"))
                            .withInjectableSideEffect(ec.getDetail("injectableSideEffect"))
                            .withSterilizationSideEffect(ec.getDetail("sterilizationSideEffect"))
                            .withOtherSideEffect(ec.getDetail("otherSideEffect"))
                            .withHighPriorityReason(ec.getDetail("highPriorityReason"))
                            .preprocess();
                    fpClients.add(fpClient);
                }
                return fpClients;
            }
        });
    }
}