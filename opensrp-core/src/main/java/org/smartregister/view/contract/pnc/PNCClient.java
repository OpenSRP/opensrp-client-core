package org.smartregister.view.contract.pnc;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.smartregister.domain.ANCServiceType;
import org.smartregister.domain.FPMethod;
import org.smartregister.util.IntegerUtil;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.AlertDTO;
import org.smartregister.view.contract.ChildClient;
import org.smartregister.view.contract.ServiceProvidedDTO;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.Visits;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.smartregister.AllConstants.COMMA_WITH_SPACE;
import static org.smartregister.AllConstants.ECRegistrationFields.BPL_VALUE;
import static org.smartregister.AllConstants.ECRegistrationFields.SC_VALUE;
import static org.smartregister.AllConstants.ECRegistrationFields.ST_VALUE;
import static org.smartregister.AllConstants.IN_AREA;
import static org.smartregister.AllConstants.OUT_OF_AREA;
import static org.smartregister.AllConstants.SPACE;
import static org.smartregister.domain.ANCServiceType.PNC;
import static org.smartregister.util.DateUtil.formatDate;
import static org.smartregister.util.DateUtil.formatFromISOString;
import static org.smartregister.util.DateUtil.getLocalDateFromISOString;
import static org.smartregister.util.StringUtil.humanize;
import static org.smartregister.util.StringUtil.humanizeAndDoUPPERCASE;
import static org.smartregister.util.StringUtil.replaceAndHumanizeWithInitCapText;
import static org.smartregister.view.contract.AlertDTO.emptyAlert;
import static org.smartregister.view.contract.ServiceProvidedDTO.emptyService;

public class PNCClient implements PNCSmartRegisterClient {
    private static final String CATEGORY_PNC = "pnc";
    private static final String[] SERVICE_CATEGORIES = {CATEGORY_PNC};
    private static Map<String, List<ANCServiceType>> categoriesToServiceTypeMap = new
            HashMap<String, List<ANCServiceType>>();

    static {
        categoriesToServiceTypeMap.put(CATEGORY_PNC, Arrays.asList(PNC));
    }

    private String entityId;
    private String ec_number;
    private String village;
    private String name;
    private String thayi;
    private String age;
    private String womanDOB;
    private String husbandName;
    private String photo_path;
    private Boolean isHighPriority;
    private Boolean isHighRisk;
    private String locationStatus;
    private String economicStatus;
    private String caste;
    private String fp_method;
    private String iudPlace;
    private String iudPerson;
    private String family_planning_method_change_date;
    private String numberOfCondomsSupplied;
    private String numberOfOCPDelivered;
    private String numberOfCentchromanPillsDelivered;
    private String deliveryDate;
    private String deliveryPlace;
    private String deliveryType;
    private String deliveryComplications;
    private String pncComplications;
    private String otherDeliveryComplications;
    private List<AlertDTO> alerts;
    private List<ServiceProvidedDTO> services_provided;
    private List<ChildClient> children;
    private String entityIdToSavePhoto;
    private List<ServiceProvidedDTO> expectedVisits;
    @SerializedName("first_7_days")
    private PNCFirstSevenDaysVisits pncFirstSevenDaysVisits;
    private List<ServiceProvidedDTO> recentlyProvidedServices;
    private Map<String, Visits> serviceToVisitsMap;

    public PNCClient(String entityId, String village, String name, String thayi, String
            deliveryDate) {
        this.entityId = entityId;
        this.village = village;
        this.name = name;
        this.thayi = thayi;
        this.deliveryDate = LocalDateTime.parse(deliveryDate)
                .toString(ISODateTimeFormat.dateTime());
        this.serviceToVisitsMap = new HashMap<String, Visits>();
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
    public boolean satisfiesFilter(String filter) {
        return name.toLowerCase(Utils.getDefaultLocale()).startsWith(filter.toLowerCase()) || String
                .valueOf(ec_number).startsWith(filter) || String.valueOf(thayi).startsWith(filter);
    }

    @Override
    public int compareName(SmartRegisterClient client) {
        return this.name().compareToIgnoreCase(client.name());
    }

    @Override
    public String thayiNumber() {
        return thayi;
    }

    @Override
    public String deliveryDateForDisplay() {
        return formatFromISOString(deliveryDate, "dd/MM/YY");
    }

    @Override
    public String deliveryShortDate() {
        return formatFromISOString(deliveryDate, "dd/MM");
    }

    @Override
    public LocalDate deliveryDate() {
        return getLocalDateFromISOString(deliveryDate);
    }

    @Override
    public String deliveryPlace() {
        return humanizeAndDoUPPERCASE(deliveryPlace);
    }

    @Override
    public String deliveryType() {
        return humanize(deliveryType);
    }

    @Override
    public String deliveryComplications() {
        return replaceAndHumanizeWithInitCapText(deliveryComplications, SPACE, COMMA_WITH_SPACE);
    }

    @Override
    public FPMethod fpMethod() {
        return FPMethod.tryParse(this.fp_method, FPMethod.NONE);
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
    public String womanDOB() {
        return formatDate(womanDOB, "dd/MM/YYYY");
    }

    public List<ChildClient> children() {
        return children;
    }

    public PNCClient withDeliveryPlace(String deliveryPlace) {
        this.deliveryPlace = deliveryPlace;
        return this;
    }

    public PNCClient withDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
        return this;
    }

    public PNCClient withHusbandName(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public PNCClient withAge(String age) {
        this.age = age;
        return this;
    }

    public PNCClient withWomanDOB(String womanDOB) {
        this.womanDOB = womanDOB;
        return this;
    }

    public PNCClient withECNumber(String ecNumber) {
        this.ec_number = ecNumber;
        return this;
    }

    public PNCClient withIsHighPriority(boolean highPriority) {
        this.isHighPriority = highPriority;
        return this;
    }

    public PNCClient withIsHighRisk(boolean highRisk) {
        this.isHighRisk = highRisk;
        return this;
    }

    public PNCClient withIsOutOfArea(boolean outOfArea) {
        this.locationStatus = outOfArea ? OUT_OF_AREA : IN_AREA;
        return this;
    }

    public PNCClient withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public PNCClient withEconomicStatus(String economicStatus) {
        this.economicStatus = economicStatus;
        return this;
    }

    public PNCClient withFPMethod(String fpMethod) {
        this.fp_method = fpMethod;
        return this;
    }

    public PNCClient withPhotoPath(String photoPath) {
        this.photo_path = photoPath;
        return this;
    }

    public PNCClient withIUDPlace(String iudPlace) {
        this.iudPlace = iudPlace;
        return this;
    }

    public PNCClient withIUDPerson(String iudPerson) {
        this.iudPerson = iudPerson;
        return this;
    }

    public PNCClient withFamilyPlanningMethodChangeDate(String family_planning_method_change_date) {
        this.family_planning_method_change_date = family_planning_method_change_date;
        return this;
    }

    public PNCClient withNumberOfCondomsSupplied(String numberOfCondomsSupplied) {
        this.numberOfCondomsSupplied = numberOfCondomsSupplied;
        return this;
    }

    public PNCClient withNumberOfOCPDelivered(String numberOfOCPDelivered) {
        this.numberOfOCPDelivered = numberOfOCPDelivered;
        return this;
    }

    public PNCClient withNumberOfCentchromanPillsDelivered(String numberOfCentchromanPillsDelivered) {
        this.numberOfCentchromanPillsDelivered = numberOfCentchromanPillsDelivered;
        return this;
    }

    public PNCClient withDeliveryComplications(String deliveryComplications) {
        this.deliveryComplications = deliveryComplications;
        return this;
    }

    public PNCClient withPNCComplications(String pncComplications) {
        this.pncComplications = pncComplications;
        return this;
    }

    public PNCClient withAlerts(List<AlertDTO> alerts) {
        this.alerts = alerts;
        return this;
    }

    public PNCClient withServicesProvided(List<ServiceProvidedDTO> servicesProvided) {
        this.services_provided = servicesProvided;
        return this;
    }

    public PNCClient withOtherDeliveryComplications(String otherDeliveryComplications) {
        this.otherDeliveryComplications = otherDeliveryComplications;
        return this;
    }

    public PNCClient withChildren(List<ChildClient> children) {
        this.children = children;
        return this;
    }

    public PNCClient withEntityIdToSavePhoto(String entityIdToSavePhoto) {
        this.entityIdToSavePhoto = entityIdToSavePhoto;
        return this;
    }

    public PNCClient withExpectedVisits(List<ServiceProvidedDTO> visits) {
        this.expectedVisits = visits;
        return this;
    }

    public PNCClient withFirstSevenDaysVisit(PNCFirstSevenDaysVisits pncFirstSevenDaysVisits) {
        this.pncFirstSevenDaysVisits = pncFirstSevenDaysVisits;
        return this;
    }

    public PNCClient withRecentlyProvidedServices(List<ServiceProvidedDTO>
                                                          recentlyProvidedServices) {
        this.recentlyProvidedServices = recentlyProvidedServices;
        return this;
    }

    public PNCClient withServiceToVisitMap(Map<String, Visits> serviceToVisitsMap) {
        this.serviceToVisitsMap = serviceToVisitsMap;
        return this;
    }

    public PNCClient withPreProcess() {
        initialize(SERVICE_CATEGORIES, serviceToVisitsMap);
        initializeAllServiceToProvideAndProvided(categoriesToServiceTypeMap);
        return this;
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
    public List<PNCCircleDatum> pncCircleData() {
        return pncFirstSevenDaysVisits.pncCircleData();
    }

    @Override
    public List<PNCStatusDatum> pncStatusData() {
        return pncFirstSevenDaysVisits.pncStatusData();
    }

    @Override
    public PNCStatusColor pncVisitStatusColor() {
        return pncFirstSevenDaysVisits.pncVisitStatusColor();
    }

    @Override
    public List<PNCTickDatum> pncTickData() {
        return pncFirstSevenDaysVisits.pncTickData();
    }

    @Override
    public List<PNCLineDatum> pncLineData() {
        return pncFirstSevenDaysVisits.pncLineData();
    }

    @Override
    public List<PNCVisitDaysDatum> visitDaysData() {
        return pncFirstSevenDaysVisits.visitDaysData();
    }

    @Override
    public PNCFirstSevenDaysVisits firstSevenDaysVisits() {
        return pncFirstSevenDaysVisits;
    }

    @Override
    public List<ServiceProvidedDTO> recentlyProvidedServices() {
        return recentlyProvidedServices;
    }

    @Override
    public boolean isVisitsDone() {
        return isServiceProvided(CATEGORY_PNC);
    }

    @Override
    public String visitDoneDateWithVisitName() {
        return serviceProvidedToACategory(CATEGORY_PNC).servicedOnWithServiceName();
    }

    @Override
    public AlertDTO getAlert(ANCServiceType type) {
        return serviceToProvide(type.category());
    }

    private AlertDTO serviceToProvide(String category) {
        if (StringUtils.isBlank(category)) {
            return emptyAlert;
        }
        return serviceToVisitsMap.get(category).toProvide;
    }

    public ServiceProvidedDTO serviceProvidedToACategory(String category) {
        if (StringUtils.isBlank(category)) {
            return emptyService;
        }
        return serviceToVisitsMap.get(category).provided;
    }

    public Map<String, Visits> serviceToVisitsMap() {
        return serviceToVisitsMap;
    }

    public List<ServiceProvidedDTO> expectedVisits() {
        return expectedVisits;
    }

    public List<ServiceProvidedDTO> servicesProvided() {
        return services_provided;
    }

    private void initialize(String[] serviceCategories, Map<String, Visits> serviceToVisitsMap) {
        for (String type : serviceCategories) {
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
        ServiceProvidedDTO serviceProvided = serviceToVisitsMap.get(category).provided;
        return serviceProvided != null && !serviceProvided.equals(emptyService);
    }

    @Override
    public String pncComplications() {
        if (StringUtils.isNotEmpty(pncComplications)) {
            return replaceAndHumanizeWithInitCapText(pncComplications.trim(), SPACE,
                    COMMA_WITH_SPACE);
        }
        return null;
    }
}
