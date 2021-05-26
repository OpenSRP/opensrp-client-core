package org.smartregister.view.contract;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.smartregister.domain.ANCServiceType;
import org.smartregister.util.IntegerUtil;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joda.time.Days.daysBetween;
import static org.joda.time.LocalDateTime.parse;
import static org.smartregister.AllConstants.ANCVisitFields.BP_DIASTOLIC;
import static org.smartregister.AllConstants.ANCVisitFields.BP_SYSTOLIC;
import static org.smartregister.AllConstants.COMMA_WITH_SPACE;
import static org.smartregister.AllConstants.ECRegistrationFields.BPL_VALUE;
import static org.smartregister.AllConstants.ECRegistrationFields.SC_VALUE;
import static org.smartregister.AllConstants.ECRegistrationFields.ST_VALUE;
import static org.smartregister.AllConstants.FORM_DATE_TIME_FORMAT;
import static org.smartregister.AllConstants.IN_AREA;
import static org.smartregister.AllConstants.OUT_OF_AREA;
import static org.smartregister.AllConstants.SLASH_STRING;
import static org.smartregister.AllConstants.SPACE;
import static org.smartregister.domain.ANCServiceType.ANC_1;
import static org.smartregister.domain.ANCServiceType.ANC_2;
import static org.smartregister.domain.ANCServiceType.ANC_3;
import static org.smartregister.domain.ANCServiceType.ANC_4;
import static org.smartregister.domain.ANCServiceType.DELIVERY_PLAN;
import static org.smartregister.domain.ANCServiceType.HB_TEST;
import static org.smartregister.domain.ANCServiceType.IFA;
import static org.smartregister.domain.ANCServiceType.PNC;
import static org.smartregister.domain.ANCServiceType.TT_1;
import static org.smartregister.domain.ANCServiceType.TT_2;
import static org.smartregister.domain.ANCServiceType.TT_BOOSTER;
import static org.smartregister.util.DateUtil.formatDate;
import static org.smartregister.util.DateUtil.today;
import static org.smartregister.util.StringUtil.humanize;
import static org.smartregister.util.StringUtil.replaceAndHumanize;
import static org.smartregister.view.contract.AlertDTO.emptyAlert;
import static org.smartregister.view.contract.ServiceProvidedDTO.emptyService;

public class ANCClient implements ANCSmartRegisterClient {

    public static final String CATEGORY_ANC = "anc";
    public static final String CATEGORY_TT = "tt";
    public static final String CATEGORY_IFA = "ifa";
    public static final String CATEGORY_HB = "hb";
    public static final String CATEGORY_DELIVERY_PLAN = "delivery_plan";
    public static final String CATEGORY_PNC = "pnc";

    private static final String[] SERVICE_CATEGORIES = {CATEGORY_ANC, CATEGORY_TT, CATEGORY_IFA,
            CATEGORY_HB, CATEGORY_DELIVERY_PLAN, CATEGORY_PNC};

    private static Map<String, List<ANCServiceType>> categoriesToServiceTypeMap = new
            HashMap<String, List<ANCServiceType>>();

    static {
        categoriesToServiceTypeMap.put(CATEGORY_ANC, Arrays.asList(ANC_1, ANC_2, ANC_3, ANC_4));
        categoriesToServiceTypeMap.put(CATEGORY_TT, Arrays.asList(TT_1, TT_2, TT_BOOSTER));
        categoriesToServiceTypeMap.put(CATEGORY_IFA, Arrays.asList(IFA));
        categoriesToServiceTypeMap.put(CATEGORY_HB, Arrays.asList(HB_TEST));
        categoriesToServiceTypeMap.put(CATEGORY_DELIVERY_PLAN, Arrays.asList(DELIVERY_PLAN));
        categoriesToServiceTypeMap.put(CATEGORY_PNC, Arrays.asList(PNC));
    }

    private String entityId;
    private String ec_number;
    private String village;
    private String name;
    private String thayi;
    private String ancNumber;
    private String age;
    private String husbandName;
    private String photo_path;
    private String edd;
    private String lmp;
    private boolean isHighPriority;
    private boolean isHighRisk;
    private String riskFactors;
    private String locationStatus;
    private String caste;
    private String economicStatus;
    private List<AlertDTO> alerts;
    private List<ServiceProvidedDTO> services_provided;
    private String entityIdToSavePhoto;
    private String ashaPhoneNumber;
    private Map<String, Visits> serviceToVisitsMap;

    public ANCClient(String entityId, String village, String name, String thayi, String edd,
                     String lmp) {
        this.entityId = entityId;
        this.village = village;
        this.name = name;
        this.thayi = thayi;
        this.edd = parse(edd, DateTimeFormat.forPattern(FORM_DATE_TIME_FORMAT))
                .toString(ISODateTimeFormat.dateTime());
        this.lmp = lmp;
        this.serviceToVisitsMap = new HashMap<String, Visits>();
    }

    @Override
    public String entityId() {
        return entityId;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String displayName() {
        return humanize(name);
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
        return isHighRisk;
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
        return locationStatus;
    }

    @Override
    public boolean satisfiesFilter(String filterCriterion) {
        return name.toLowerCase(Utils.getDefaultLocale()).startsWith(filterCriterion.toLowerCase())
                || String.valueOf(ec_number).startsWith(filterCriterion) || String.valueOf(thayi)
                .startsWith(filterCriterion);
    }

    @Override
    public int compareName(SmartRegisterClient client) {
        return this.name().compareToIgnoreCase(client.name());
    }

    @Override
    public String eddForDisplay() {
        return formatDate(parse(edd).toLocalDate().toString(), "dd/MM/yy");
    }

    @Override
    public LocalDateTime edd() {
        return parse(edd);
    }

    @Override
    public String pastDueInDays() {
        return isBlank(edd) ? "0"
                : Integer.toString(daysBetween(parse(edd).toLocalDate(), today()).getDays());
    }

    @Override
    public String weeksAfterLMP() {
        return isBlank(lmp) ? "0"
                : Integer.toString(daysBetween(parse(lmp).toLocalDate(), today()).getDays() / 7);
    }

    @Override
    public AlertDTO getAlert(ANCServiceType type) {
        return serviceToProvide(type.category());
    }

    @Override
    public boolean isVisitsDone() {
        return isServiceProvided(CATEGORY_ANC);
    }

    @Override
    public boolean isTTDone() {
        return isServiceProvided(CATEGORY_TT);
    }

    @Override
    public boolean isIFADone() {
        return isServiceProvided(CATEGORY_IFA);
    }

    @Override
    public String ifaDoneDate() {
        return serviceProvidedToACategory(CATEGORY_IFA).servicedOnWithServiceName();
    }

    @Override
    public String ttDoneDate() {
        return serviceProvidedToACategory(CATEGORY_TT).servicedOnWithServiceName();
    }

    @Override
    public String visitDoneDateWithVisitName() {
        return serviceProvidedToACategory(CATEGORY_ANC).servicedOnWithServiceName();
    }

    @Override
    public String thayiCardNumber() {
        return thayi;
    }

    @Override
    public String ancNumber() {
        return ancNumber;
    }

    @Override
    public String lmp() {
        return formatDate(lmp, "dd/MM/yy");
    }

    @Override
    public String riskFactors() {
        if (StringUtils.isNotEmpty(riskFactors)) {
            return replaceAndHumanize(riskFactors.trim(), SPACE, COMMA_WITH_SPACE);
        }
        return null;
    }

    @Override
    public List<ServiceProvidedDTO> allServicesProvidedForAServiceType(String serviceType) {
        List<ServiceProvidedDTO> servicesProvided = new ArrayList<ServiceProvidedDTO>();
        for (ServiceProvidedDTO serviceProvided : services_provided) {
            if (serviceProvided.name().equalsIgnoreCase(serviceType)) {
                servicesProvided.add(serviceProvided);
            }
        }
        return servicesProvided;
    }

    @Override
    public String ashaPhoneNumber() {
        return ashaPhoneNumber;
    }

    public Map<String, Visits> serviceToVisitsMap() {
        return serviceToVisitsMap;
    }

    public ANCClient withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public ANCClient withAge(String age) {
        this.age = age;
        return this;
    }

    public ANCClient withECNumber(String ecNumber) {
        this.ec_number = ecNumber;
        return this;
    }

    public ANCClient withANCNumber(String ancNumber) {
        this.ancNumber = ancNumber;
        return this;
    }

    public ANCClient withIsHighPriority(boolean highPriority) {
        this.isHighPriority = highPriority;
        return this;
    }

    public ANCClient withIsHighRisk(boolean highRisk) {
        this.isHighRisk = highRisk;
        return this;
    }

    public ANCClient withIsOutOfArea(boolean outOfArea) {
        this.locationStatus = outOfArea ? OUT_OF_AREA : IN_AREA;
        return this;
    }

    public ANCClient withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public ANCClient withHighRiskReason(String highRiskReason) {
        this.riskFactors = highRiskReason;
        return this;
    }

    public ANCClient withEconomicStatus(String economicStatus) {
        this.economicStatus = economicStatus;
        return this;
    }

    public ANCClient withPhotoPath(String photoPath) {
        this.photo_path = photoPath;
        return this;
    }

    public ANCClient withAlerts(List<AlertDTO> alerts) {
        this.alerts = alerts;
        return this;
    }

    public ANCClient withServicesProvided(List<ServiceProvidedDTO> servicesProvided) {
        this.services_provided = servicesProvided;
        return this;
    }

    public ANCClient withEntityIdToSavePhoto(String entityIdToSavePhoto) {
        this.entityIdToSavePhoto = entityIdToSavePhoto;
        return this;
    }

    public ANCClient withAshaPhoneNumber(String ashaPhoneNumber) {
        this.ashaPhoneNumber = ashaPhoneNumber;
        return this;
    }

    public ANCClient withPreProcess() {
        initialize(SERVICE_CATEGORIES, serviceToVisitsMap);
        initializeAllServiceToProvideAndProvided(categoriesToServiceTypeMap);
        return this;
    }

    public ANCClient withServiceToVisitMap(Map<String, Visits> serviceToVisitMap) {
        this.serviceToVisitsMap = serviceToVisitMap;
        return this;
    }

    private void initialize(String[] types, Map<String, Visits> serviceToVisitsMap) {
        for (String type : types) {
            serviceToVisitsMap.put(type, new Visits());
        }
    }

    private void initializeAllServiceToProvideAndProvided(Map<String, List<ANCServiceType>>
                                                                  categoriesToServiceTypeMap) {
        Set<String> keys = categoriesToServiceTypeMap.keySet();
        for (String key : keys) {
            initializeServiceToProvideAndProvided(categoriesToServiceTypeMap.get(key));
        }
    }

    private void initializeServiceToProvideAndProvided(List<ANCServiceType> types) {
        for (ANCServiceType type : types) {
            initializeServiceToProvideAndProvided(type);
        }
    }

    private void initializeServiceToProvideAndProvided(ANCServiceType type) {
        for (AlertDTO alert : alerts) {
            if (type.equals(alert.ancServiceType())) {
                serviceToVisitsMap.get(type.category()).toProvide = alert;
            }
        }

        for (ServiceProvidedDTO service : services_provided) {
            if (type.equals(service.ancServiceType())) {
                serviceToVisitsMap.get(type.category()).provided = service;
            }
        }
    }

    private boolean isServiceProvided(String category) {
        if (StringUtils.isBlank(category)) {
            return false;
        }
        return serviceToVisitsMap.get(category).provided != emptyService;
    }

    public ServiceProvidedDTO serviceProvidedToACategory(String category) {
        if (StringUtils.isBlank(category)) {
            return emptyService;
        }
        return serviceToVisitsMap.get(category).provided;
    }

    private AlertDTO serviceToProvide(String category) {
        if (StringUtils.isBlank(category)) {
            return emptyAlert;
        }
        return serviceToVisitsMap.get(category).toProvide;
    }

    @Override
    public String getHyperTension(ServiceProvidedDTO ancServiceProvided) {
        String systolic = ancServiceProvided.data().get(BP_SYSTOLIC);
        String diastolic = ancServiceProvided.data().get(BP_DIASTOLIC);
        if (StringUtils.isEmpty(systolic) && StringUtils.isEmpty(diastolic)) {
            return EMPTY;
        }
        return systolic + SLASH_STRING + diastolic;
    }

    @Override
    public ServiceProvidedDTO getServiceProvidedDTO(String serviceName) {
        for (ServiceProvidedDTO serviceProvidedDTO : services_provided) {
            if (serviceProvidedDTO.name().equalsIgnoreCase(serviceName)) {
                return serviceProvidedDTO;
            }
        }
        return null;
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
