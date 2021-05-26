package org.smartregister.view.contract;

import com.google.common.base.Strings;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.smartregister.AllConstants;
import org.smartregister.domain.ChildServiceType;
import org.smartregister.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.smartregister.AllConstants.ChildIllnessFields.REPORT_CHILD_DISEASE;
import static org.smartregister.AllConstants.ChildIllnessFields.REPORT_CHILD_DISEASE_DATE;
import static org.smartregister.AllConstants.ChildIllnessFields.REPORT_CHILD_DISEASE_OTHER;
import static org.smartregister.AllConstants.ECRegistrationFields.BPL_VALUE;
import static org.smartregister.AllConstants.ECRegistrationFields.SC_VALUE;
import static org.smartregister.AllConstants.ECRegistrationFields.ST_VALUE;
import static org.smartregister.AllConstants.FEMALE_GENDER;
import static org.smartregister.AllConstants.IN_AREA;
import static org.smartregister.AllConstants.OUT_OF_AREA;
import static org.smartregister.domain.ChildServiceType.BCG;
import static org.smartregister.domain.ChildServiceType.DPTBOOSTER_1;
import static org.smartregister.domain.ChildServiceType.DPTBOOSTER_2;
import static org.smartregister.domain.ChildServiceType.HEPB_0;
import static org.smartregister.domain.ChildServiceType.ILLNESS_VISIT;
import static org.smartregister.domain.ChildServiceType.MEASLES;
import static org.smartregister.domain.ChildServiceType.MEASLESBOOSTER;
import static org.smartregister.domain.ChildServiceType.OPV_0;
import static org.smartregister.domain.ChildServiceType.OPV_1;
import static org.smartregister.domain.ChildServiceType.OPV_2;
import static org.smartregister.domain.ChildServiceType.OPV_3;
import static org.smartregister.domain.ChildServiceType.OPV_BOOSTER;
import static org.smartregister.domain.ChildServiceType.PENTAVALENT_1;
import static org.smartregister.domain.ChildServiceType.PENTAVALENT_2;
import static org.smartregister.domain.ChildServiceType.PENTAVALENT_3;
import static org.smartregister.domain.ChildServiceType.VITAMIN_A;
import static org.smartregister.util.DateUtil.formatDate;
import static org.smartregister.util.StringUtil.humanize;
import static org.smartregister.view.contract.AlertDTO.emptyAlert;
import static org.smartregister.view.contract.ServiceProvidedDTO.emptyService;

public class ChildClient implements ChildSmartRegisterClient {
    public static final String CATEGORY_BCG = "bcg";
    public static final String CATEGORY_MEASLES = "measles";
    public static final String CATEGORY_OPV = "opv";
    public static final String CATEGORY_OPVBOOSTER = "opvbooster";
    public static final String CATEGORY_DPT = "dpt";
    public static final String CATEGORY_PENTAVALENT = "pentavalent";
    public static final String CATEGORY_HEPB = "hepb";
    public static final String CATEGORY_VITAMIN_A = "vitamin_a";
    public static final String CATEGORY_CHILD_ILLNESS = "child_illness";

    private static final String[] SERVICE_CATEGORIES = {CATEGORY_BCG, CATEGORY_MEASLES,
            CATEGORY_OPV, CATEGORY_OPVBOOSTER, CATEGORY_DPT, CATEGORY_PENTAVALENT, CATEGORY_HEPB,
            CATEGORY_VITAMIN_A, CATEGORY_CHILD_ILLNESS};

    private static Map<String, List<ChildServiceType>> categoriesToServiceTypeMap = new
            HashMap<String, List<ChildServiceType>>();

    static {
        categoriesToServiceTypeMap.put(CATEGORY_BCG, Arrays.asList(BCG));
        categoriesToServiceTypeMap.put(CATEGORY_MEASLES, Arrays.asList(MEASLES, MEASLESBOOSTER));
        categoriesToServiceTypeMap.put(CATEGORY_OPV, Arrays.asList(OPV_0, OPV_1, OPV_2, OPV_3));
        categoriesToServiceTypeMap.put(CATEGORY_OPVBOOSTER, Arrays.asList(OPV_BOOSTER));
        categoriesToServiceTypeMap.put(CATEGORY_DPT, Arrays.asList(DPTBOOSTER_1, DPTBOOSTER_2));
        categoriesToServiceTypeMap.put(CATEGORY_PENTAVALENT,
                Arrays.asList(PENTAVALENT_1, PENTAVALENT_2, PENTAVALENT_3));
        categoriesToServiceTypeMap.put(CATEGORY_HEPB, Arrays.asList(HEPB_0));
        categoriesToServiceTypeMap.put(CATEGORY_VITAMIN_A, Arrays.asList(VITAMIN_A));
        categoriesToServiceTypeMap.put(CATEGORY_CHILD_ILLNESS, Arrays.asList(ILLNESS_VISIT));
    }

    private final String entityId;
    private final String thayiCardNumber;
    Map<String, Treatments> serviceToTreatmentMap = new HashMap<String, Treatments>();
    private String gender;
    private String weight;
    private String name;
    private String motherName;
    private String dob;
    private String motherAge;
    private String fatherName;
    private String village;
    private String locationStatus;
    private String economicStatus;
    private String caste;
    private boolean isHighRisk;
    private String photo_path;
    private String ecNumber;
    private List<AlertDTO> alerts;
    private List<ServiceProvidedDTO> services_provided;
    private String entityIdToSavePhoto;

    private ServiceProvidedDTO lastService;
    private ServiceProvidedDTO illnessVisitServiceProvided;

    public ChildClient(String entityId, String gender, String weight, String thayiCardNumber) {
        this.entityId = entityId;
        this.gender = gender;
        this.weight = weight;
        this.thayiCardNumber = thayiCardNumber;
    }

    @Override
    public String motherName() {
        return humanize(motherName);
    }

    @Override
    public String village() {
        return humanize(village);
    }

    @Override
    public String wifeName() {
        return name();
    }

    @Override
    public String displayName() {
        return isBlank(name) ? "B/o " + motherName() : humanize(name);
    }

    @Override
    public String name() {
        return isBlank(name) ? "" : humanize(name);
    }

    @Override
    public String husbandName() {
        return motherName() + ", " + fatherName();
    }

    @Override
    public String fatherName() {
        return humanize(fatherName);
    }

    @Override
    public int age() {
        return isBlank(dob) ? 0
                : Years.yearsBetween(LocalDate.parse(dob), LocalDate.now()).getYears();
    }

    @Override
    public String ageInString() {
        return "(" + format(ageInDays()) + ", " + formatGender(gender()) + ")";
    }

    private String formatGender(String gender) {
        return FEMALE_GENDER.equalsIgnoreCase(gender) ? "F" : "M";
    }

    @Override
    public String gender() {
        return humanize(gender);
    }

    @Override
    public String weight() {
        return weight;
    }

    @Override
    public String locationStatus() {
        return locationStatus;
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
        return false;
    }

    @Override
    public boolean isBPL() {
        return economicStatus != null && economicStatus.equalsIgnoreCase(BPL_VALUE);
    }

    @Override
    public String entityId() {
        return entityId;
    }

    @Override
    public String profilePhotoPath() {
        return photo_path;
    }

    @Override
    public boolean satisfiesFilter(String filterCriterion) {
        return (!isBlank(name) && name.toLowerCase().startsWith(filterCriterion.toLowerCase())) || (
                !isBlank(motherName) && motherName.toLowerCase()
                        .startsWith(filterCriterion.toLowerCase())) || String.valueOf(ecNumber)
                .startsWith(filterCriterion) || String.valueOf(thayiCardNumber)
                .startsWith(filterCriterion);
    }

    @Override
    public int ageInDays() {
        return isBlank(dob) ? 0
                : Days.daysBetween(LocalDate.parse(dob), DateUtil.today()).getDays();
    }

    @Override
    public int compareName(SmartRegisterClient anotherClient) {
        ChildSmartRegisterClient anotherChildClient = (ChildSmartRegisterClient) anotherClient;
        return this.motherName().compareTo(anotherChildClient.motherName());
    }

    public String format(int days_since) {
        int DAYS_THRESHOLD = 28;
        int WEEKS_THRESHOLD = 119;
        int MONTHS_THRESHOLD = 720;
        if (days_since < DAYS_THRESHOLD) {
            return (int) Math.floor(days_since) + "d";
        } else if (days_since < WEEKS_THRESHOLD) {
            return (int) Math.floor(days_since / 7) + "w";
        } else if (days_since < MONTHS_THRESHOLD) {
            return (int) Math.floor(days_since / 30) + "m";
        } else {
            return (int) Math.floor(days_since / 365) + "y";
        }
    }

    @Override
    public String thayiCardNumber() {
        return thayiCardNumber;
    }

    @Override
    public String motherEcNumber() {
        return ecNumber;
    }

    @Override
    public String dateOfBirth() {
        return isBlank(dob) ? "" : formatDate(dob);
    }

    @Override
    public List<ServiceProvidedDTO> serviceProvided() {
        return services_provided;
    }

    @Override
    public ServiceProvidedDTO lastServiceProvided() {
        if (lastService == null) {
            lastService = serviceProvided().size() > 0 ? serviceProvided()
                    .get(serviceProvided().size() - 1) : emptyService;
        }
        return lastService;
    }

    @Override
    public ServiceProvidedDTO illnessVisitServiceProvided() {
        if (illnessVisitServiceProvided == null) {
            illnessVisitServiceProvided = getIllnessVisitServiceProvided();
        }
        return illnessVisitServiceProvided;
    }

    private ServiceProvidedDTO getIllnessVisitServiceProvided() {
        Collections.reverse(services_provided);
        for (ServiceProvidedDTO service : services_provided) {
            if (ILLNESS_VISIT.equals(service.type())) {
                return service;
            }
        }
        return emptyService;
    }

    @Override
    public ChildSickStatus sickStatus() {
        ServiceProvidedDTO service = illnessVisitServiceProvided();
        if (service == emptyService) {
            return ChildSickStatus.noDiseaseStatus;
        } else {
            final Map<String, String> data = service.data();
            String diseases;
            String otherDiseases;
            String date;
            if (data.containsKey(REPORT_CHILD_DISEASE)) {
                diseases = data.get(REPORT_CHILD_DISEASE);
                otherDiseases = data.get(REPORT_CHILD_DISEASE_OTHER);
                date = data.get(REPORT_CHILD_DISEASE_DATE);
            } else {
                diseases = data.get(AllConstants.ChildIllnessFields.CHILD_SIGNS);
                otherDiseases = data.get(AllConstants.ChildIllnessFields.CHILD_SIGNS_OTHER);
                date = data.get(AllConstants.ChildIllnessFields.SICK_VISIT_DATE);
            }
            return new ChildSickStatus(diseases, otherDiseases, date);
        }
    }

    public ChildClient withEntityIdToSavePhoto(String entityIdToSavePhoto) {
        this.entityIdToSavePhoto = entityIdToSavePhoto;
        return this;
    }

    public ChildClient withName(String name) {
        this.name = name;
        return this;
    }

    public ChildClient withMotherName(String motherName) {
        this.motherName = motherName;
        return this;
    }

    public ChildClient withDOB(String dob) {
        this.dob = dob;
        return this;
    }

    public ChildClient withMotherAge(String motherAge) {
        this.motherAge = motherAge;
        return this;
    }

    public ChildClient withFatherName(String fatherName) {
        this.fatherName = fatherName;
        return this;
    }

    public ChildClient withVillage(String village) {
        this.village = village;
        return this;
    }

    public ChildClient withOutOfArea(boolean outOfArea) {
        this.locationStatus = outOfArea ? OUT_OF_AREA : IN_AREA;
        return this;
    }

    public ChildClient withEconomicStatus(String economicStatus) {
        this.economicStatus = economicStatus;
        return this;
    }

    public ChildClient withCaste(String caste) {
        this.caste = caste;
        return this;
    }

    public ChildClient withIsHighRisk(boolean isHighRisk) {
        this.isHighRisk = isHighRisk;
        return this;
    }

    public ChildClient withPhotoPath(String photoPath) {
        this.photo_path = photoPath;
        return this;
    }

    public ChildClient withECNumber(String ecNumber) {
        this.ecNumber = ecNumber;
        return this;
    }

    public ChildClient withAlerts(List<AlertDTO> alerts) {
        this.alerts = alerts;
        return this;
    }

    public ChildClient withServicesProvided(List<ServiceProvidedDTO> servicesProvided) {
        Collections.sort(servicesProvided, new DateComparator());
        this.services_provided = servicesProvided;
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

    public ChildClient withPreprocess() {
        initialize(SERVICE_CATEGORIES, serviceToTreatmentMap);
        initializeAllServiceToProvideAndProvided(categoriesToServiceTypeMap);
        return this;
    }

    private void initializeAllServiceToProvideAndProvided(Map<String, List<ChildServiceType>>
                                                                  categoriesToServiceTypeMap) {
        Set<String> keys = categoriesToServiceTypeMap.keySet();
        for (String key : keys) {
            initializeServiceToProvideAndProvided(categoriesToServiceTypeMap.get(key));
        }
    }

    private void initializeServiceToProvideAndProvided(List<ChildServiceType> types) {
        for (ChildServiceType type : types) {
            initializeServiceToProvideAndProvided(type);
        }
    }

    private void initializeServiceToProvideAndProvided(ChildServiceType type) {
        for (AlertDTO alert : alerts) {
            if (!alert.isCompleted() && type.equals(alert.type())) {
                serviceToTreatmentMap.get(type.category()).toProvide = alert;
            }
        }

        for (ServiceProvidedDTO service : services_provided) {
            if (type.equals(service.type())) {
                serviceToTreatmentMap.get(type.category()).provided = service;
            }
        }
    }

    private void initialize(String[] types, Map<String, Treatments> serviceToTreatmentMap) {
        for (String type : types) {
            serviceToTreatmentMap.put(type, new Treatments());
        }
    }

    @Override
    public boolean isBcgDone() {
        return isServiceProvided(CATEGORY_BCG);
    }

    @Override
    public boolean isOpvDone() {
        return isServiceProvided(CATEGORY_OPV);
    }

    @Override
    public boolean isHepBDone() {
        return isServiceProvided(CATEGORY_HEPB);
    }

    @Override
    public boolean isPentavDone() {
        return isServiceProvided(CATEGORY_PENTAVALENT);
    }

    private boolean isServiceProvided(String category) {
        if (StringUtils.isBlank(category)) {
            return false;
        }
        return serviceToTreatmentMap.get(category).provided != emptyService;
    }

    private ServiceProvidedDTO serviceProvided(String category) {
        if (StringUtils.isBlank(category)) {
            return emptyService;
        }
        return serviceToTreatmentMap.get(category).provided;
    }

    private AlertDTO serviceToProvide(String category) {
        if (StringUtils.isBlank(category)) {
            return emptyAlert;
        }
        return serviceToTreatmentMap.get(category).toProvide;
    }

    @Override
    public String bcgDoneDate() {
        return serviceProvided(CATEGORY_BCG).shortDate();
    }

    @Override
    public String opvDoneDate() {
        return serviceProvided(CATEGORY_OPV).servicedOn();
    }

    @Override
    public String hepBDoneDate() {
        return serviceProvided(CATEGORY_HEPB).servicedOn();
    }

    @Override
    public String pentavDoneDate() {
        return serviceProvided(CATEGORY_PENTAVALENT).servicedOn();
    }

    @Override
    public AlertDTO getAlert(ChildServiceType type) {
        return serviceToProvide(type.category());
    }

    @Override
    public boolean isMeaslesDone() {
        return isServiceProvided(CATEGORY_MEASLES);
    }

    @Override
    public boolean isOpvBoosterDone() {
        return isServiceProvided(CATEGORY_OPVBOOSTER);
    }

    @Override
    public boolean isDptBoosterDone() {
        return isServiceProvided(CATEGORY_DPT);
    }

    @Override
    public boolean isVitaminADone() {
        return isServiceProvided(CATEGORY_VITAMIN_A);
    }

    @Override
    public String measlesDoneDate() {
        return serviceProvided(CATEGORY_MEASLES).servicedOn();
    }

    @Override
    public String opvBoosterDoneDate() {
        return serviceProvided(CATEGORY_OPVBOOSTER).servicedOn();
    }

    @Override
    public String dptBoosterDoneDate() {
        return serviceProvided(CATEGORY_DPT).servicedOn();
    }

    @Override
    public String vitaminADoneDate() {
        return serviceProvided(CATEGORY_VITAMIN_A).servicedOn();
    }

    @Override
    public List<AlertDTO> alerts() {
        return alerts;
    }

    @Override
    public boolean isDataError() {
        // only important data
        return (Strings.isNullOrEmpty(motherName) || Strings.isNullOrEmpty(fatherName));
    }

    private class Treatments {
        public ServiceProvidedDTO provided = emptyService;
        public AlertDTO toProvide = emptyAlert;
    }

    class DateComparator implements Comparator<ServiceProvidedDTO> {

        @Override
        public int compare(ServiceProvidedDTO serviceProvidedDTO1, ServiceProvidedDTO
                serviceProvidedDTO2) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date date1;
            Date date2;
            try {
                date1 = simpleDateFormat.parse(serviceProvidedDTO1.date());
                date2 = simpleDateFormat.parse(serviceProvidedDTO2.date());
                if (date1.equals(date2)) {
                    return -1;
                }
                return date1.compareTo(date2);
            } catch (ParseException e) {
                //TODO
            }
            return -1;
        }
    }
}
