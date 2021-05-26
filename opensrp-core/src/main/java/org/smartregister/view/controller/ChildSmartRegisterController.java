package org.smartregister.view.controller;

import com.google.gson.Gson;

import org.apache.commons.lang3.ArrayUtils;
import org.smartregister.AllConstants;
import org.smartregister.domain.Alert;
import org.smartregister.domain.Child;
import org.smartregister.domain.ServiceProvided;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.service.AlertService;
import org.smartregister.service.ServiceProvidedService;
import org.smartregister.util.Cache;
import org.smartregister.util.CacheableData;
import org.smartregister.view.contract.AlertDTO;
import org.smartregister.view.contract.ChildClient;
import org.smartregister.view.contract.ServiceProvidedDTO;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Collections.sort;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.domain.ServiceProvided.CHILD_ILLNESS_SERVICE_PROVIDED_NAME;
import static org.smartregister.domain.ServiceProvided.PNC_SERVICE_PROVIDED_NAME;
import static org.smartregister.domain.ServiceProvided.VITAMIN_A_SERVICE_PROVIDED_NAME;

public class ChildSmartRegisterController {
    private static final String CHILD_CLIENTS_LIST_CACHE_ENTRY_NAME = "ChildClientList";
    private final ServiceProvidedService serviceProvidedService;
    private final AlertService alertService;
    private final AllBeneficiaries allBeneficiaries;
    private final Cache<String> cache;
    private final Cache<SmartRegisterClients> smartRegisterCache;

    public ChildSmartRegisterController(ServiceProvidedService serviceProvidedService,
                                        AlertService alertService, AllBeneficiaries
                                                allBeneficiaries, Cache<String> cache,
                                        Cache<SmartRegisterClients> smartRegisterCache) {
        this.serviceProvidedService = serviceProvidedService;
        this.alertService = alertService;
        this.allBeneficiaries = allBeneficiaries;
        this.cache = cache;
        this.smartRegisterCache = smartRegisterCache;
    }

    public String get() {
        return cache.get(CHILD_CLIENTS_LIST_CACHE_ENTRY_NAME, new CacheableData<String>() {
            @Override
            public String fetch() {
                List<Child> children = allBeneficiaries.allChildrenWithMotherAndEC();
                List<ChildClient> childrenClient = new ArrayList<ChildClient>();

                for (Child child : children) {
                    String photoPath = isBlank(child.photoPath()) ? (
                            AllConstants.FEMALE_GENDER.equalsIgnoreCase(child.gender())
                                    ? AllConstants.DEFAULT_GIRL_INFANT_IMAGE_PLACEHOLDER_PATH
                                    : AllConstants.DEFAULT_BOY_INFANT_IMAGE_PLACEHOLDER_PATH)
                            : child.photoPath();
                    List<AlertDTO> alerts = getAlerts(child.caseId());
                    List<ServiceProvidedDTO> servicesProvided = getServicesProvided(child.caseId());
                    ChildClient childClient = new ChildClient(child.caseId(), child.gender(),
                            child.getDetail(AllConstants.ChildRegistrationFields.WEIGHT),
                            child.mother().thayiCardNumber())
                            .withName(child.getDetail(AllConstants.ChildRegistrationFields.NAME))
                            .withEntityIdToSavePhoto(child.caseId())
                            .withMotherName(child.ec().wifeName()).withDOB(child.dateOfBirth())
                            .withMotherAge(child.ec().age())
                            .withFatherName(child.ec().husbandName())
                            .withVillage(child.ec().village())
                            .withOutOfArea(child.ec().isOutOfArea()).withEconomicStatus(child.ec()
                                    .getDetail(AllConstants.ECRegistrationFields.ECONOMIC_STATUS))
                            .withCaste(
                                    child.ec().getDetail(AllConstants.ECRegistrationFields.CASTE))
                            .withIsHighRisk(child.isHighRisk()).withPhotoPath(photoPath)
                            .withECNumber(child.ec().ecNumber()).withAlerts(alerts)
                            .withServicesProvided(servicesProvided);

                    childrenClient.add(childClient);
                }
                sortByMotherName(childrenClient);
                return new Gson().toJson(childrenClient);
            }
        });
    }

    public SmartRegisterClients getClients() {
        return smartRegisterCache.get(CHILD_CLIENTS_LIST_CACHE_ENTRY_NAME,
                new CacheableData<SmartRegisterClients>() {
                    @Override
                    public SmartRegisterClients fetch() {
                        List<Child> children = allBeneficiaries.allChildrenWithMotherAndEC();
                        SmartRegisterClients childrenClient = new SmartRegisterClients();

                        for (Child child : children) {
                            String photoPath = isBlank(child.photoPath()) ? (
                                    AllConstants.FEMALE_GENDER.equalsIgnoreCase(child.gender())
                                            ? AllConstants
                                            .DEFAULT_GIRL_INFANT_IMAGE_PLACEHOLDER_PATH
                                            : AllConstants
                                            .DEFAULT_BOY_INFANT_IMAGE_PLACEHOLDER_PATH)
                                    : child.photoPath();
                            List<AlertDTO> alerts = getAlerts(child.caseId());
                            List<ServiceProvidedDTO> servicesProvided = getServicesProvided(
                                    child.caseId());
                            ChildClient childClient = new ChildClient(child.caseId(),
                                    child.gender(),
                                    child.getDetail(AllConstants.ChildRegistrationFields.WEIGHT),
                                    child.mother().thayiCardNumber()).withName(
                                    child.getDetail(AllConstants.ChildRegistrationFields.NAME))
                                    .withEntityIdToSavePhoto(child.caseId())
                                    .withMotherName(child.ec().wifeName())
                                    .withDOB(child.dateOfBirth()).withMotherAge(child.ec().age())
                                    .withFatherName(child.ec().husbandName())
                                    .withVillage(child.ec().village())
                                    .withOutOfArea(child.ec().isOutOfArea()).withEconomicStatus(
                                            child.ec().getDetail(
                                                    AllConstants.ECRegistrationFields
                                                            .ECONOMIC_STATUS))
                                    .withCaste(child.ec()
                                            .getDetail(AllConstants.ECRegistrationFields.CASTE))
                                    .withIsHighRisk(child.isHighRisk()).withPhotoPath(photoPath)
                                    .withECNumber(child.ec().ecNumber()).withAlerts(alerts)
                                    .withServicesProvided(servicesProvided).withPreprocess();

                            childrenClient.add(childClient);
                        }

                        sortByName(childrenClient);
                        return childrenClient;
                    }
                });
    }

    private List<ServiceProvidedDTO> getServicesProvided(String entityId) {
        List<ServiceProvided> servicesProvided = serviceProvidedService
                .findByEntityIdAndServiceNames(entityId, ArrayUtils
                        .addAll(AllConstants.Immunizations.ALL, VITAMIN_A_SERVICE_PROVIDED_NAME,
                                CHILD_ILLNESS_SERVICE_PROVIDED_NAME, PNC_SERVICE_PROVIDED_NAME));
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
                .findByEntityIdAndAlertNames(entityId, AllConstants.Immunizations.ALL);
        List<AlertDTO> alertDTOs = new ArrayList<AlertDTO>();
        for (Alert alert : alerts) {
            alertDTOs.add(new AlertDTO(alert.visitCode(), valueOf(alert.status()),
                    alert.startDate()));
        }
        return alertDTOs;
    }

    private void sortByName(List<SmartRegisterClient> childrenClient) {
        sort(childrenClient, new Comparator<SmartRegisterClient>() {
            @Override
            public int compare(SmartRegisterClient oneChild, SmartRegisterClient anotherChild) {
                return oneChild.compareName(anotherChild);
            }
        });
    }

    private void sortByMotherName(List<ChildClient> childrenClient) {
        sort(childrenClient, new Comparator<ChildClient>() {
            @Override
            public int compare(ChildClient oneChild, ChildClient anotherChild) {
                return oneChild.motherName().compareToIgnoreCase(anotherChild.motherName());
            }
        });
    }
}
