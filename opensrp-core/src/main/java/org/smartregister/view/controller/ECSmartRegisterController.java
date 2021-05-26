package org.smartregister.view.controller;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.AllConstants;
import org.smartregister.domain.Child;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.util.Cache;
import org.smartregister.util.CacheableData;
import org.smartregister.util.EasyMap;
import org.smartregister.util.IntegerUtil;
import org.smartregister.view.contract.ECChildClient;
import org.smartregister.view.contract.ECClient;
import org.smartregister.view.contract.ECClients;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.AllConstants.ANCRegistrationFields.EDD;
import static org.smartregister.AllConstants.DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH;
import static org.smartregister.AllConstants.ECRegistrationFields.CASTE;
import static org.smartregister.AllConstants.ECRegistrationFields.CURRENT_FP_METHOD;
import static org.smartregister.AllConstants.ECRegistrationFields.ECONOMIC_STATUS;
import static org.smartregister.AllConstants.ECRegistrationFields.FAMILY_PLANNING_METHOD_CHANGE_DATE;
import static org.smartregister.AllConstants.ECRegistrationFields.HIGH_PRIORITY_REASON;
import static org.smartregister.AllConstants.ECRegistrationFields.IUD_PERSON;
import static org.smartregister.AllConstants.ECRegistrationFields.IUD_PLACE;
import static org.smartregister.AllConstants.ECRegistrationFields.NUMBER_OF_ABORTIONS;
import static org.smartregister.AllConstants.ECRegistrationFields.NUMBER_OF_CENTCHROMAN_PILLS_DELIVERED;
import static org.smartregister.AllConstants.ECRegistrationFields.NUMBER_OF_CONDOMS_SUPPLIED;
import static org.smartregister.AllConstants.ECRegistrationFields.NUMBER_OF_LIVING_CHILDREN;
import static org.smartregister.AllConstants.ECRegistrationFields.NUMBER_OF_OCP_DELIVERED;
import static org.smartregister.AllConstants.ECRegistrationFields.NUMBER_OF_PREGNANCIES;
import static org.smartregister.AllConstants.ECRegistrationFields.NUMBER_OF_STILL_BIRTHS;
import static org.smartregister.AllConstants.ECRegistrationFields.PARITY;
import static org.smartregister.AllConstants.ECRegistrationFields.REGISTRATION_DATE;
import static org.smartregister.AllConstants.ECRegistrationFields.WOMAN_DOB;

public class ECSmartRegisterController {
    public static final String STATUS_TYPE_FIELD = "type";
    public static final String STATUS_DATE_FIELD = "date";
    public static final String EC_STATUS = "ec";
    public static final String ANC_STATUS = "anc";
    public static final String PNC_STATUS = "pnc";
    public static final String PNC_FP_STATUS = "pnc/fp";
    public static final String FP_STATUS = "fp";
    public static final String STATUS_EDD_FIELD = "edd";
    public static final String FP_METHOD_DATE_FIELD = "fpMethodDate";
    private static final String EC_CLIENTS_LIST = "ECClientsList";

    private final AllEligibleCouples allEligibleCouples;
    private final AllBeneficiaries allBeneficiaries;
    private final Cache<String> cache;
    private final Cache<ECClients> ecClientsCache;

    public ECSmartRegisterController(AllEligibleCouples allEligibleCouples, AllBeneficiaries
            allBeneficiaries, Cache<String> cache, Cache<ECClients> ecClientsCache) {
        this.allEligibleCouples = allEligibleCouples;
        this.allBeneficiaries = allBeneficiaries;
        this.cache = cache;
        this.ecClientsCache = ecClientsCache;
    }

    public String get() {
        return cache.get(EC_CLIENTS_LIST, new CacheableData<String>() {
            @Override
            public String fetch() {
                List<EligibleCouple> ecs = allEligibleCouples.all();
                List<ECClient> ecClients = new ArrayList<ECClient>();

                for (EligibleCouple ec : ecs) {
                    String photoPath =
                            isBlank(ec.photoPath()) ? DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH
                                    : ec.photoPath();
                    ECClient ecClient = new ECClient(ec.caseId(), ec.wifeName(), ec.husbandName(),
                            ec.village(), IntegerUtil.tryParse(ec.ecNumber(), 0))
                            .withDateOfBirth(ec.getDetail(WOMAN_DOB))
                            .withFPMethod(ec.getDetail(CURRENT_FP_METHOD))
                            .withFamilyPlanningMethodChangeDate(
                                    ec.getDetail(FAMILY_PLANNING_METHOD_CHANGE_DATE))
                            .withIUDPlace(ec.getDetail(IUD_PLACE))
                            .withIUDPerson(ec.getDetail(IUD_PERSON))
                            .withNumberOfCondomsSupplied(ec.getDetail(NUMBER_OF_CONDOMS_SUPPLIED))
                            .withNumberOfCentchromanPillsDelivered(
                                    ec.getDetail(NUMBER_OF_CENTCHROMAN_PILLS_DELIVERED))
                            .withNumberOfOCPDelivered(ec.getDetail(NUMBER_OF_OCP_DELIVERED))
                            .withCaste(ec.getDetail(CASTE))
                            .withEconomicStatus(ec.getDetail(ECONOMIC_STATUS))
                            .withNumberOfPregnancies(ec.getDetail(NUMBER_OF_PREGNANCIES))
                            .withParity(ec.getDetail(PARITY))
                            .withNumberOfLivingChildren(ec.getDetail(NUMBER_OF_LIVING_CHILDREN))
                            .withNumberOfStillBirths(ec.getDetail(NUMBER_OF_STILL_BIRTHS))
                            .withNumberOfAbortions(ec.getDetail(NUMBER_OF_ABORTIONS))
                            .withIsHighPriority(ec.isHighPriority()).withPhotoPath(photoPath)
                            .withHighPriorityReason(ec.getDetail(HIGH_PRIORITY_REASON))
                            .withIsOutOfArea(ec.isOutOfArea());
                    updateStatusInformation(ec, ecClient);
                    updateChildrenInformation(ecClient);
                    ecClients.add(ecClient);
                }
                sortByName(ecClients);
                return new Gson().toJson(ecClients);
            }
        });
    }

    //#TODO: Remove duplication
    public ECClients getClients() {
        return ecClientsCache.get(EC_CLIENTS_LIST, new CacheableData<ECClients>() {
            @Override
            public ECClients fetch() {
                List<EligibleCouple> ecs = allEligibleCouples.all();
                ECClients ecClients = new ECClients();

                for (EligibleCouple ec : ecs) {
                    String photoPath =
                            isBlank(ec.photoPath()) ? DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH
                                    : ec.photoPath();
                    ECClient ecClient = new ECClient(ec.caseId(), ec.wifeName(), ec.husbandName(),
                            ec.village(), IntegerUtil.tryParse(ec.ecNumber(), 0))
                            .withDateOfBirth(ec.getDetail(WOMAN_DOB))
                            .withFPMethod(ec.getDetail(CURRENT_FP_METHOD))
                            .withFamilyPlanningMethodChangeDate(
                                    ec.getDetail(FAMILY_PLANNING_METHOD_CHANGE_DATE))
                            .withIUDPlace(ec.getDetail(IUD_PLACE))
                            .withIUDPerson(ec.getDetail(IUD_PERSON))
                            .withNumberOfCondomsSupplied(ec.getDetail(NUMBER_OF_CONDOMS_SUPPLIED))
                            .withNumberOfCentchromanPillsDelivered(
                                    ec.getDetail(NUMBER_OF_CENTCHROMAN_PILLS_DELIVERED))
                            .withNumberOfOCPDelivered(ec.getDetail(NUMBER_OF_OCP_DELIVERED))
                            .withCaste(ec.getDetail(CASTE))
                            .withEconomicStatus(ec.getDetail(ECONOMIC_STATUS))
                            .withNumberOfPregnancies(ec.getDetail(NUMBER_OF_PREGNANCIES))
                            .withParity(ec.getDetail(PARITY))
                            .withNumberOfLivingChildren(ec.getDetail(NUMBER_OF_LIVING_CHILDREN))
                            .withNumberOfStillBirths(ec.getDetail(NUMBER_OF_STILL_BIRTHS))
                            .withNumberOfAbortions(ec.getDetail(NUMBER_OF_ABORTIONS))
                            .withIsHighPriority(ec.isHighPriority()).withPhotoPath(photoPath)
                            .withHighPriorityReason(ec.getDetail(HIGH_PRIORITY_REASON))
                            .withIsOutOfArea(ec.isOutOfArea());
                    updateStatusInformation(ec, ecClient);
                    updateChildrenInformation(ecClient);
                    ecClients.add(ecClient);
                }
                sortByName(ecClients);
                return ecClients;
            }
        });
    }

    private void updateChildrenInformation(ECClient ecClient) {
        List<Child> children = allBeneficiaries.findAllChildrenByECId(ecClient.entityId());
        sortByDateOfBirth(children);
        Iterable<Child> youngestTwoChildren = Iterables
                .skip(children, children.size() < 2 ? 0 : children.size() - 2);
        for (Child child : youngestTwoChildren) {
            ecClient.addChild(
                    new ECChildClient(child.caseId(), child.gender(), child.dateOfBirth()));
        }
    }

    private void sortByDateOfBirth(List<Child> children) {
        sort(children, new Comparator<Child>() {
            @Override
            public int compare(Child child, Child anotherChild) {
                return LocalDate.parse(child.dateOfBirth())
                        .compareTo(LocalDate.parse(anotherChild.dateOfBirth()));
            }
        });
    }

    private void sortByName(List<? extends SmartRegisterClient> ecClients) {
        sort(ecClients, new Comparator<SmartRegisterClient>() {
            @Override
            public int compare(SmartRegisterClient oneECClient, SmartRegisterClient
                    anotherECClient) {
                return oneECClient.wifeName().compareToIgnoreCase(anotherECClient.wifeName());
            }
        });
    }

    //#TODO: Needs refactoring
    private void updateStatusInformation(EligibleCouple eligibleCouple, ECClient ecClient) {
        Mother mother = allBeneficiaries.findMotherWithOpenStatusByECId(eligibleCouple.caseId());

        if (mother == null && !eligibleCouple.hasFPMethod()) {
            ecClient.withStatus(EasyMap.create(STATUS_TYPE_FIELD, EC_STATUS)
                    .put(STATUS_DATE_FIELD, eligibleCouple.getDetail(REGISTRATION_DATE)).map());

            return;
        }

        if (mother == null && eligibleCouple.hasFPMethod()) {
            ecClient.withStatus(EasyMap.create(STATUS_TYPE_FIELD, FP_STATUS).put(STATUS_DATE_FIELD,
                    eligibleCouple.getDetail(FAMILY_PLANNING_METHOD_CHANGE_DATE)).map());

            return;
        }

        if (mother != null && mother.isANC()) {
            ecClient.withStatus(EasyMap.create(STATUS_TYPE_FIELD, ANC_STATUS)
                    .put(STATUS_DATE_FIELD, mother.referenceDate()).put(STATUS_EDD_FIELD, LocalDate
                            .parse(mother.getDetail(EDD),
                                    DateTimeFormat.forPattern(AllConstants.FORM_DATE_TIME_FORMAT))
                            .toString()).map());

            return;
        }

        if (mother != null && mother.isPNC() && !eligibleCouple.hasFPMethod()) {
            ecClient.withStatus(EasyMap.create(STATUS_TYPE_FIELD, PNC_STATUS)
                    .put(STATUS_DATE_FIELD, mother.referenceDate()).map());

            return;
        }

        if (mother != null && mother.isPNC() && eligibleCouple.hasFPMethod()) {
            ecClient.withStatus(EasyMap.create(STATUS_TYPE_FIELD, PNC_FP_STATUS)
                    .put(STATUS_DATE_FIELD, mother.referenceDate()).put(FP_METHOD_DATE_FIELD,
                            eligibleCouple.getDetail(FAMILY_PLANNING_METHOD_CHANGE_DATE)).map());
        }
    }
}
