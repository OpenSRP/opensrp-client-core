package org.smartregister.view.controller;

import com.google.gson.Gson;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.domain.Alert;
import org.smartregister.domain.Child;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.domain.ServiceProvided;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.service.AlertService;
import org.smartregister.service.ServiceProvidedService;
import org.smartregister.util.Cache;
import org.smartregister.util.CacheableData;
import org.smartregister.view.contract.AlertDTO;
import org.smartregister.view.contract.ChildClient;
import org.smartregister.view.contract.ServiceProvidedDTO;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.Village;
import org.smartregister.view.contract.pnc.PNCClient;
import org.smartregister.view.contract.pnc.PNCClients;
import org.smartregister.view.preProcessor.PNCClientPreProcessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Collections.sort;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.AllConstants.DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH;
import static org.smartregister.AllConstants.ECRegistrationFields.CASTE;
import static org.smartregister.AllConstants.ECRegistrationFields.CURRENT_FP_METHOD;
import static org.smartregister.AllConstants.ECRegistrationFields.ECONOMIC_STATUS;
import static org.smartregister.AllConstants.ECRegistrationFields.FAMILY_PLANNING_METHOD_CHANGE_DATE;
import static org.smartregister.AllConstants.ECRegistrationFields.IUD_PERSON;
import static org.smartregister.AllConstants.ECRegistrationFields.IUD_PLACE;
import static org.smartregister.AllConstants.ECRegistrationFields.NUMBER_OF_CENTCHROMAN_PILLS_DELIVERED;
import static org.smartregister.AllConstants.ECRegistrationFields.NUMBER_OF_CONDOMS_SUPPLIED;
import static org.smartregister.AllConstants.ECRegistrationFields.NUMBER_OF_OCP_DELIVERED;
import static org.smartregister.AllConstants.ECRegistrationFields.WOMAN_DOB;
import static org.smartregister.AllConstants.PNCRegistrationFields.DELIVERY_COMPLICATIONS;
import static org.smartregister.AllConstants.PNCRegistrationFields.DELIVERY_PLACE;
import static org.smartregister.AllConstants.PNCRegistrationFields.DELIVERY_TYPE;
import static org.smartregister.AllConstants.PNCRegistrationFields.IMMEDIATE_REFERRAL_REASON;
import static org.smartregister.AllConstants.PNCRegistrationFields.OTHER_DELIVERY_COMPLICATIONS;
import static org.smartregister.domain.ServiceProvided.PNC_SERVICE_PROVIDED_NAME;

public class PNCSmartRegisterController {
    private static final String PNC_1_ALERT_NAME = "PNC 1";

    private static final String PNC_CLIENTS_LIST = "PNCClientList";
    private final ServiceProvidedService serviceProvidedService;
    private AllEligibleCouples allEligibleCouples;
    private AllBeneficiaries allBeneficiaries;
    private AlertService alertService;
    private Cache<String> cache;
    private Cache<PNCClients> pncClientsCache;
    private PNCClientPreProcessor pncClientPreProcessor;

    public PNCSmartRegisterController(ServiceProvidedService serviceProvidedService, AlertService
            alertService, AllEligibleCouples allEligibleCouples, AllBeneficiaries
                                              allBeneficiaries, Cache<String> cache, Cache<PNCClients> pncClientsCache) {
        this(serviceProvidedService, alertService, allEligibleCouples, allBeneficiaries, cache,
                pncClientsCache, new PNCClientPreProcessor());
    }

    public PNCSmartRegisterController(ServiceProvidedService serviceProvidedService, AlertService
            alertService, AllEligibleCouples allEligibleCouples, AllBeneficiaries
                                              allBeneficiaries, Cache<String> cache, Cache<PNCClients> pncClientsCache,
                                      PNCClientPreProcessor pncClientPreProcessor) {
        this.allEligibleCouples = allEligibleCouples;
        this.allBeneficiaries = allBeneficiaries;
        this.alertService = alertService;
        this.serviceProvidedService = serviceProvidedService;
        this.cache = cache;
        this.pncClientsCache = pncClientsCache;
        this.pncClientPreProcessor = pncClientPreProcessor;
    }

    public PNCClients getClients() {
        return pncClientsCache.get(PNC_CLIENTS_LIST, new CacheableData<PNCClients>() {
            @Override
            public PNCClients fetch() {
                PNCClients pncClients = new PNCClients();
                List<Pair<Mother, EligibleCouple>> pncsWithEcs = allBeneficiaries.allPNCsWithEC();

                for (Pair<Mother, EligibleCouple> pncWithEc : pncsWithEcs) {
                    Mother pnc = pncWithEc.getLeft();
                    EligibleCouple ec = pncWithEc.getRight();
                    String photoPath =
                            isBlank(ec.photoPath()) ? DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH
                                    : ec.photoPath();

                    List<ServiceProvidedDTO> servicesProvided = getServicesProvided(pnc.caseId());
                    List<AlertDTO> alerts = getAlerts(pnc.caseId());
                    PNCClient client = new PNCClient(pnc.caseId(), ec.village(), ec.wifeName(),
                            pnc.thayiCardNumber(), pnc.referenceDate())
                            .withHusbandName(ec.husbandName()).withAge(ec.age())
                            .withWomanDOB(ec.getDetail(WOMAN_DOB)).withECNumber(ec.ecNumber())
                            .withIsHighPriority(ec.isHighPriority())
                            .withIsHighRisk(pnc.isHighRisk())
                            .withEconomicStatus(ec.getDetail(ECONOMIC_STATUS))
                            .withIsOutOfArea(ec.isOutOfArea()).withCaste(ec.getDetail(CASTE))
                            .withPhotoPath(photoPath).withFPMethod(ec.getDetail(CURRENT_FP_METHOD))
                            .withIUDPlace(ec.getDetail(IUD_PLACE))
                            .withIUDPerson(ec.getDetail(IUD_PERSON))
                            .withNumberOfCondomsSupplied(ec.getDetail(NUMBER_OF_CONDOMS_SUPPLIED))
                            .withFamilyPlanningMethodChangeDate(
                                    ec.getDetail(FAMILY_PLANNING_METHOD_CHANGE_DATE))
                            .withNumberOfOCPDelivered(ec.getDetail(NUMBER_OF_OCP_DELIVERED))
                            .withNumberOfCentchromanPillsDelivered(
                                    ec.getDetail(NUMBER_OF_CENTCHROMAN_PILLS_DELIVERED))
                            .withDeliveryPlace(pnc.getDetail(DELIVERY_PLACE))
                            .withDeliveryType(pnc.getDetail(DELIVERY_TYPE))
                            .withDeliveryComplications(pnc.getDetail(DELIVERY_COMPLICATIONS))
                            .withPNCComplications(pnc.getDetail(IMMEDIATE_REFERRAL_REASON))
                            .withOtherDeliveryComplications(
                                    pnc.getDetail(OTHER_DELIVERY_COMPLICATIONS))
                            .withEntityIdToSavePhoto(ec.caseId()).withAlerts(alerts)
                            .withServicesProvided(servicesProvided).withChildren(findChildren(pnc))
                            .withPreProcess();
                    pncClients.add(pncClientPreProcessor.preProcess(client));
                }
                sortByName(pncClients);
                return pncClients;
            }
        });
    }

    public String villages() {
        List<Village> villagesList = new ArrayList<Village>();
        List<String> villages = allEligibleCouples.villages();
        for (String village : villages) {
            villagesList.add(new Village(village));
        }
        return new Gson().toJson(villagesList);
    }

    private List<ServiceProvidedDTO> getServicesProvided(String entityId) {
        List<ServiceProvided> servicesProvided = serviceProvidedService
                .findByEntityIdAndServiceNames(entityId, PNC_SERVICE_PROVIDED_NAME);
        List<ServiceProvidedDTO> serviceProvidedDTOs = new ArrayList<ServiceProvidedDTO>();
        for (ServiceProvided serviceProvided : servicesProvided) {
            serviceProvidedDTOs
                    .add(new ServiceProvidedDTO(serviceProvided.name(), serviceProvided.date(),
                            serviceProvided.data()));
        }
        return serviceProvidedDTOs;
    }

    private List<AlertDTO> getAlerts(String entityId) {
        List<Alert> alerts = alertService.findByEntityIdAndAlertNames(entityId, PNC_1_ALERT_NAME);
        List<AlertDTO> alertDTOs = new ArrayList<AlertDTO>();
        for (Alert alert : alerts) {
            alertDTOs.add(new AlertDTO(alert.visitCode(), valueOf(alert.status()),
                    alert.startDate()));
        }
        return alertDTOs;
    }

    private void sortByName(List<? extends SmartRegisterClient> pncClients) {
        sort(pncClients, new Comparator<SmartRegisterClient>() {
            @Override
            public int compare(SmartRegisterClient onePNCClient, SmartRegisterClient
                    anotherPNCClient) {
                return onePNCClient.wifeName().compareToIgnoreCase(anotherPNCClient.wifeName());
            }
        });
    }

    private List<ChildClient> findChildren(Mother mother) {
        List<Child> children = allBeneficiaries.findAllChildrenByMotherId(mother.caseId());
        List<ChildClient> childClientList = new ArrayList<ChildClient>();
        for (Child child : children) {
            childClientList
                    .add(new ChildClient(child.caseId(), child.gender(), child.getDetail("weight"),
                            mother.thayiCardNumber()));
        }
        return childClientList;
    }
}
