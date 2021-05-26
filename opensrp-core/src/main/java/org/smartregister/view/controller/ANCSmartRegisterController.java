package org.smartregister.view.controller;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.AllConstants;
import org.smartregister.domain.Alert;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.domain.ServiceProvided;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.service.AlertService;
import org.smartregister.service.ServiceProvidedService;
import org.smartregister.util.Cache;
import org.smartregister.util.CacheableData;
import org.smartregister.view.contract.ANCClient;
import org.smartregister.view.contract.ANCClients;
import org.smartregister.view.contract.AlertDTO;
import org.smartregister.view.contract.ServiceProvidedDTO;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Collections.sort;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.AllConstants.DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH;
import static org.smartregister.domain.ServiceProvided.ANC_1_SERVICE_PROVIDED_NAME;
import static org.smartregister.domain.ServiceProvided.ANC_2_SERVICE_PROVIDED_NAME;
import static org.smartregister.domain.ServiceProvided.ANC_3_SERVICE_PROVIDED_NAME;
import static org.smartregister.domain.ServiceProvided.ANC_4_SERVICE_PROVIDED_NAME;
import static org.smartregister.domain.ServiceProvided.DELIVERY_PLAN_SERVICE_PROVIDED_NAME;
import static org.smartregister.domain.ServiceProvided.HB_TEST_SERVICE_PROVIDED_NAME;
import static org.smartregister.domain.ServiceProvided.IFA_SERVICE_PROVIDED_NAME;
import static org.smartregister.domain.ServiceProvided.TT_1_SERVICE_PROVIDED_NAME;
import static org.smartregister.domain.ServiceProvided.TT_2_SERVICE_PROVIDED_NAME;
import static org.smartregister.domain.ServiceProvided.TT_BOOSTER_SERVICE_PROVIDED_NAME;

public class ANCSmartRegisterController {
    private static final String ANC_1_ALERT_NAME = "ANC 1";
    private static final String ANC_2_ALERT_NAME = "ANC 2";
    private static final String ANC_3_ALERT_NAME = "ANC 3";
    private static final String ANC_4_ALERT_NAME = "ANC 4";
    private static final String IFA_1_ALERT_NAME = "IFA 1";
    private static final String IFA_2_ALERT_NAME = "IFA 2";
    private static final String IFA_3_ALERT_NAME = "IFA 3";
    private static final String LAB_REMINDER_ALERT_NAME = "REMINDER";
    private static final String TT_1_ALERT_NAME = "TT 1";
    private static final String TT_2_ALERT_NAME = "TT 2";
    private static final String HB_TEST_1_ALERT_NAME = "Hb Test 1";
    private static final String HB_TEST_2_ALERT_NAME = "Hb Test 2";
    private static final String HB_FOLLOWUP_TEST_ALERT_NAME = "Hb Followup Test";
    private static final String DELIVERY_PLAN_ALERT_NAME = "Delivery Plan";

    private static final String ANC_CLIENTS_LIST = "ANCClientList";
    private final ServiceProvidedService serviceProvidedService;
    private AllBeneficiaries allBeneficiaries;
    private AlertService alertService;
    private Cache<String> cache;
    private Cache<ANCClients> ancClientsCache;

    public ANCSmartRegisterController(ServiceProvidedService serviceProvidedService, AlertService
            alertService, AllBeneficiaries allBeneficiaries, Cache<String> cache,
                                      Cache<ANCClients> ancClientsCache) {
        this.allBeneficiaries = allBeneficiaries;
        this.alertService = alertService;
        this.serviceProvidedService = serviceProvidedService;
        this.cache = cache;
        this.ancClientsCache = ancClientsCache;
    }

    private List<ServiceProvidedDTO> getServicesProvided(String entityId) {
        List<ServiceProvided> servicesProvided = serviceProvidedService
                .findByEntityIdAndServiceNames(entityId, IFA_SERVICE_PROVIDED_NAME,
                        TT_1_SERVICE_PROVIDED_NAME, TT_2_SERVICE_PROVIDED_NAME,
                        TT_BOOSTER_SERVICE_PROVIDED_NAME, HB_TEST_SERVICE_PROVIDED_NAME,
                        ANC_1_SERVICE_PROVIDED_NAME, ANC_2_SERVICE_PROVIDED_NAME,
                        ANC_3_SERVICE_PROVIDED_NAME, ANC_4_SERVICE_PROVIDED_NAME,
                        DELIVERY_PLAN_SERVICE_PROVIDED_NAME);
        List<ServiceProvidedDTO> serviceProvidedDTOs = new ArrayList<ServiceProvidedDTO>();
        for (ServiceProvided serviceProvided : servicesProvided) {
            serviceProvidedDTOs
                    .add(new ServiceProvidedDTO(serviceProvided.name(), serviceProvided.date(),
                            serviceProvided.data()));
        }
        return serviceProvidedDTOs;
    }

    private List<AlertDTO> getAlerts(String entityId) {
        List<Alert> alerts = alertService
                .findByEntityIdAndAlertNames(entityId, ANC_1_ALERT_NAME, ANC_2_ALERT_NAME,
                        ANC_3_ALERT_NAME, ANC_4_ALERT_NAME, IFA_1_ALERT_NAME, IFA_2_ALERT_NAME,
                        IFA_3_ALERT_NAME, LAB_REMINDER_ALERT_NAME, TT_1_ALERT_NAME, TT_2_ALERT_NAME,
                        HB_TEST_1_ALERT_NAME, HB_FOLLOWUP_TEST_ALERT_NAME, HB_TEST_2_ALERT_NAME,
                        DELIVERY_PLAN_ALERT_NAME);
        List<AlertDTO> alertDTOs = new ArrayList<AlertDTO>();
        for (Alert alert : alerts) {
            alertDTOs.add(new AlertDTO(alert.visitCode(), valueOf(alert.status()),
                    alert.startDate()));
        }
        return alertDTOs;
    }

    public ANCClients getClients() {
        return ancClientsCache.get(ANC_CLIENTS_LIST, new CacheableData<ANCClients>() {
            @Override
            public ANCClients fetch() {
                ANCClients ancClients = new ANCClients();
                List<Pair<Mother, EligibleCouple>> ancsWithEcs = allBeneficiaries.allANCsWithEC();

                for (Pair<Mother, EligibleCouple> ancWithEc : ancsWithEcs) {
                    Mother anc = ancWithEc.getLeft();
                    EligibleCouple ec = ancWithEc.getRight();
                    String photoPath =
                            isBlank(ec.photoPath()) ? DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH
                                    : ec.photoPath();

                    List<ServiceProvidedDTO> servicesProvided = getServicesProvided(anc.caseId());
                    List<AlertDTO> alerts = getAlerts(anc.caseId());
                    ANCClient ancClient = new ANCClient(anc.caseId(), ec.village(), ec.wifeName(),
                            anc.thayiCardNumber(),
                            anc.getDetail(AllConstants.ANCRegistrationFields.EDD),
                            anc.referenceDate()).withHusbandName(ec.husbandName()).withAge(ec.age())
                            .withECNumber(ec.ecNumber()).withANCNumber(
                                    anc.getDetail(AllConstants.ANCRegistrationFields.ANC_NUMBER))
                            .withIsHighPriority(ec.isHighPriority())
                            .withIsHighRisk(anc.isHighRisk()).withIsOutOfArea(ec.isOutOfArea())
                            .withHighRiskReason(anc.highRiskReason())
                            .withCaste(ec.getDetail(AllConstants.ECRegistrationFields.CASTE))
                            .withEconomicStatus(
                                    ec.getDetail(AllConstants.ECRegistrationFields.ECONOMIC_STATUS))
                            .withPhotoPath(photoPath).withEntityIdToSavePhoto(ec.caseId())
                            .withAlerts(alerts).withAshaPhoneNumber(anc.getDetail(
                                    AllConstants.ANCRegistrationFields.ASHA_PHONE_NUMBER))
                            .withServicesProvided(servicesProvided).withPreProcess();
                    ancClients.add(ancClient);
                }
                sortByName(ancClients);
                return ancClients;
            }
        });
    }

    private void sortByName(ANCClients ancClients) {
        sort(ancClients, new Comparator<SmartRegisterClient>() {
            @Override
            public int compare(SmartRegisterClient oneANCClient, SmartRegisterClient
                    anotherANCClient) {
                return oneANCClient.wifeName().compareToIgnoreCase(anotherANCClient.wifeName());
            }
        });
    }
}
