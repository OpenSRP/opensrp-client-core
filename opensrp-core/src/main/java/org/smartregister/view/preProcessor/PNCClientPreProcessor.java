package org.smartregister.view.preProcessor;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.joda.time.LocalDate;
import org.smartregister.util.DateUtil;
import org.smartregister.view.contract.ServiceProvidedDTO;
import org.smartregister.view.contract.pnc.PNCCircleDatum;
import org.smartregister.view.contract.pnc.PNCClient;
import org.smartregister.view.contract.pnc.PNCFirstSevenDaysVisits;
import org.smartregister.view.contract.pnc.PNCLineDatum;
import org.smartregister.view.contract.pnc.PNCStatusColor;
import org.smartregister.view.contract.pnc.PNCStatusDatum;
import org.smartregister.view.contract.pnc.PNCTickDatum;
import org.smartregister.view.contract.pnc.PNCVisitDaysDatum;
import org.smartregister.view.contract.pnc.PNCVisitStatus;
import org.smartregister.view.contract.pnc.PNCVisitType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.smartregister.util.DateUtil.dayDifference;
import static org.smartregister.util.DateUtil.formatDate;

public class PNCClientPreProcessor {
    private static final String PNC_IDENTIFIER = "PNC";
    private static List<Integer> defaultVisitDays = new ArrayList<Integer>(Arrays.asList(1, 3, 7));

    public PNCClient preProcess(PNCClient client) {
        List<PNCCircleDatum> circleData = new ArrayList<PNCCircleDatum>();
        List<PNCStatusDatum> statusData = new ArrayList<PNCStatusDatum>();
        List<PNCTickDatum> tickData = new ArrayList<PNCTickDatum>();
        List<PNCLineDatum> lineData = new ArrayList<PNCLineDatum>();

        int currentDay = DateUtil.dayDifference(client.deliveryDate(), DateUtil.today());
        populateExpectedVisitDates(client);
        List<ServiceProvidedDTO> first7DaysVisits = getFirst7DaysVisits(client);
        createViewElements(client, currentDay, first7DaysVisits, circleData, statusData, tickData,
                lineData);
        populateRecent3Visits(client);
        return client;
    }

    private void populateRecent3Visits(PNCClient client) {
        List<ServiceProvidedDTO> servicesProvided = getServicesProvidedAfterFirstSevenDays(client);
        Collections.sort(servicesProvided, new DateComparator());
        ArrayList<ServiceProvidedDTO> recentServicesProvided = new ArrayList<ServiceProvidedDTO>();
        int iter;
        for (iter = 0; iter < 3; iter++) {
            int index = servicesProvided.size() - iter - 1;
            if (index >= 0) {
                recentServicesProvided.add(servicesProvided.get(index));
            }

        }
        List<ServiceProvidedDTO> recentServicesWithDays =
                findAndSetTheVisitDayOfTheServicesWithRespectToDeliveryDate(
                        recentServicesProvided, client.deliveryDate());
        client.withRecentlyProvidedServices(recentServicesWithDays);
    }

    private List<ServiceProvidedDTO> getServicesProvidedAfterFirstSevenDays(PNCClient client) {
        ArrayList<ServiceProvidedDTO> servicesProvidedAfterFirstSevenDays = new
                ArrayList<ServiceProvidedDTO>();
        int VISIT_END_OFFSET_DAY_COUNT = 7;
        LocalDate endDate = client.deliveryDate().plusDays(VISIT_END_OFFSET_DAY_COUNT);
        if (client.servicesProvided() != null && !client.servicesProvided().isEmpty()) {
            for (ServiceProvidedDTO serviceProvided : client.servicesProvided()) {
                LocalDate serviceProvidedDate = serviceProvided.localDate();
                if (serviceProvidedDate.isAfter(endDate)) {
                    servicesProvidedAfterFirstSevenDays.add(serviceProvided);
                }
            }
            Collections.sort(servicesProvidedAfterFirstSevenDays, new DateComparator());
            Collections.reverse(servicesProvidedAfterFirstSevenDays);
        }
        return servicesProvidedAfterFirstSevenDays;
    }

    private void populateExpectedVisitDates(PNCClient client) {
        List<ServiceProvidedDTO> expectedVisits = new ArrayList<ServiceProvidedDTO>();
        for (Integer visitDay : defaultVisitDays) {
            LocalDate expectedVisitDate = client.deliveryDate().plusDays(visitDay);
            ServiceProvidedDTO expectedVisit = new ServiceProvidedDTO("PNC", visitDay,
                    formatDate(expectedVisitDate, "YYYY-MM-dd"));
            expectedVisits.add(expectedVisit);
        }
        client.withExpectedVisits(expectedVisits);
    }

    private void createViewElements(PNCClient client, int numberOfDaysFromDeliveryDate,
                                    List<ServiceProvidedDTO> first7DaysVisits,
                                    List<PNCCircleDatum> circleData, List<PNCStatusDatum>
                                            statusData, List<PNCTickDatum> tickData,
                                    List<PNCLineDatum> lineData) {
        int currentDay = DateUtil.dayDifference(client.deliveryDate(), DateUtil.today());
        createViewElementsBasedOnExpectedVisits(client, first7DaysVisits, circleData, statusData);
        createViewDataBasedOnServicesProvided(first7DaysVisits, circleData);
        PNCStatusColor pncVisitStatusColor = getPNCVisitStatusColor(client, first7DaysVisits,
                numberOfDaysFromDeliveryDate);
        createTickData(currentDay, circleData, tickData);
        createLineData(currentDay, lineData);
        ArrayList<PNCVisitDaysDatum> pncVisitDaysData = generateDayNumbers(currentDay, circleData);
        PNCFirstSevenDaysVisits pncFirstSevenDaysVisits = new PNCFirstSevenDaysVisits(circleData,
                statusData, pncVisitStatusColor, tickData, lineData, pncVisitDaysData);
        client.withFirstSevenDaysVisit(pncFirstSevenDaysVisits);
    }

    private ArrayList<PNCVisitDaysDatum> generateDayNumbers(int currentDay, List<PNCCircleDatum>
            circleData) {
        ArrayList<PNCVisitDaysDatum> visitDaysData = new ArrayList<PNCVisitDaysDatum>();
        for (PNCCircleDatum pncCircleDatum : circleData) {
            if (pncCircleDatum.day() > currentDay || pncCircleDatum.type()
                    .equals(PNCVisitType.ACTUAL)) {
                visitDaysData
                        .add(new PNCVisitDaysDatum(pncCircleDatum.day(), pncCircleDatum.type()));
            }
        }
        return visitDaysData;
    }

    private void createLineData(int currentDay, List<PNCLineDatum> lineData) {
        if (currentDay > 1) {
            lineData.add(new PNCLineDatum(1, Math.min(7, currentDay), PNCVisitType.ACTUAL));
        }
        if (currentDay < 7) {
            lineData.add(new PNCLineDatum(Math.max(1, currentDay), 7, PNCVisitType.EXPECTED));
        }
    }

    private void createTickData(Integer currentDay, List<PNCCircleDatum> circleData,
                                List<PNCTickDatum> tickData) {
        List<Integer> possibleTickDays = Arrays.asList(2, 4, 5, 6);
        List<Integer> circleDays = new ArrayList<Integer>();
        for (PNCCircleDatum pncCircleDatum : circleData) {
            circleDays.add(pncCircleDatum.day());
        }
        for (Integer tickDay : possibleTickDays) {
            if (!circleDays.contains(tickDay)) {
                if (tickDay > currentDay) {
                    tickData.add(new PNCTickDatum(tickDay, PNCVisitType.EXPECTED));
                } else {
                    tickData.add(new PNCTickDatum(tickDay, PNCVisitType.ACTUAL));
                }
            }
        }
    }

    private PNCStatusColor getPNCVisitStatusColor(PNCClient client, List<ServiceProvidedDTO>
            first7DaysVisits, int numberOfDaysFromDeliveryDate) {
        PNCStatusColor statusColor = PNCStatusColor.YELLOW;
        if (first7DaysVisits.isEmpty() && numberOfDaysFromDeliveryDate > 1) {
            statusColor = PNCStatusColor.RED;
        } else if (actualVisitsHaveBeenDoneOnExpectedDays(client, first7DaysVisits,
                numberOfDaysFromDeliveryDate)) {
            statusColor = PNCStatusColor.GREEN;
        }
        return statusColor;
    }

    private boolean actualVisitsHaveBeenDoneOnExpectedDays(PNCClient client,
                                                           List<ServiceProvidedDTO>
                                                                   first7DaysVisits, int
                                                                   numberOfDaysFromDeliveryDate) {
        List<Integer> expectedVisitDaysTillToday = getExpectedVisitDaysTillToday(
                client.expectedVisits(), numberOfDaysFromDeliveryDate); //valid_expected_visit_days

        ArrayList<Integer> actualVisitDays = getActualVisitDays(first7DaysVisits);
        //valid_actual_visit_days
        for (Integer expectedVisitDay : expectedVisitDaysTillToday) {
            if (!actualVisitDays.contains(expectedVisitDay)) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Integer> getActualVisitDays(List<ServiceProvidedDTO> first7DaysVisits) {
        ArrayList<Integer> actualVisitDays = new ArrayList<Integer>();
        for (ServiceProvidedDTO serviceProvided : first7DaysVisits) {
            actualVisitDays.add(serviceProvided.day());
        }
        return actualVisitDays;
    }

    private ArrayList<Integer> getExpectedVisitDaysTillToday(List<ServiceProvidedDTO>
                                                                     expectedVisits, int
                                                                     numberOfDaysFromDeliveryDate) {
        ArrayList<Integer> expectedVisitDaysTillToday = new ArrayList<Integer>();
        for (ServiceProvidedDTO expectedVisit : expectedVisits) {
            if (expectedVisit.day() < numberOfDaysFromDeliveryDate) {
                expectedVisitDaysTillToday.add(expectedVisit.day());
            }
        }
        return expectedVisitDaysTillToday;
    }

    private void createViewDataBasedOnServicesProvided(List<ServiceProvidedDTO> first7DaysVisits,
                                                       List<PNCCircleDatum> circleData) {
        for (ServiceProvidedDTO serviceProvided : first7DaysVisits) {
            circleData.add(new PNCCircleDatum(serviceProvided.day(), PNCVisitType.ACTUAL, true));
        }
    }

    private void createViewElementsBasedOnExpectedVisits(PNCClient client,
                                                         List<ServiceProvidedDTO>
                                                                 first7DaysVisits,
                                                         List<PNCCircleDatum> circleData,
                                                         List<PNCStatusDatum> statusData) {
        int currentDay = DateUtil.dayDifference(client.deliveryDate(), DateUtil.today());
        for (ServiceProvidedDTO expectedVisit : client.expectedVisits()) {
            LocalDate expectedVisitDate = DateUtil.getLocalDate(expectedVisit.date());
            int expectedVisitDay = DateUtil.dayDifference(client.deliveryDate(), expectedVisitDate);

            if (expectedVisitDay >= currentDay) {
                circleData.add(new PNCCircleDatum(expectedVisitDay, PNCVisitType.EXPECTED, false));
            } else if (expectedVisitDay < currentDay) {
                if (!hasVisitHappenedOn(expectedVisitDay, first7DaysVisits)) {
                    //TODO : check if this boolean here is necessary
                    PNCCircleDatum circleDatum = new PNCCircleDatum(expectedVisitDay,
                            PNCVisitType.EXPECTED, true);
                    circleData.add(circleDatum);
                    statusData.add(new PNCStatusDatum(expectedVisitDay, PNCVisitStatus.MISSED));
                } else {
                    statusData.add(new PNCStatusDatum(expectedVisitDay, PNCVisitStatus.DONE));
                }
            }
        }
    }

    private boolean hasVisitHappenedOn(int expectedVisitDay, List<ServiceProvidedDTO>
            first7DaysVisits) {
        for (ServiceProvidedDTO first7DaysVisit : first7DaysVisits) {
            if (first7DaysVisit.day() == expectedVisitDay) {
                return true;
            }
        }
        return false;
    }

    private List<ServiceProvidedDTO> getFirst7DaysVisits(PNCClient client) {
        int VISIT_END_OFFSET_DAY_COUNT = 7;
        LocalDate endDate = client.deliveryDate().plusDays(VISIT_END_OFFSET_DAY_COUNT);
        List<ServiceProvidedDTO> validServices = getValidServicesProvided(client, endDate);
        validServices = removeDuplicateServicesAndReturnList(validServices);
        Collections.sort(validServices, new DateComparator());//Ascending sort based on the date
        findAndSetTheVisitDayOfTheServicesWithRespectToDeliveryDate(validServices,
                client.deliveryDate());//Find the day offset from the delivery date for the visits
        return validServices;
    }

    private List<ServiceProvidedDTO> getValidServicesProvided(PNCClient client, final LocalDate
            visitEndDate) {
        List<ServiceProvidedDTO> validServicesProvided = new ArrayList<ServiceProvidedDTO>();
        List<ServiceProvidedDTO> servicesProvided = client.servicesProvided();
        if (servicesProvided != null && servicesProvided.size() > 0) {
            Iterables.addAll(validServicesProvided,
                    Iterables.filter(servicesProvided, new Predicate<ServiceProvidedDTO>() {
                        @Override
                        public boolean apply(ServiceProvidedDTO serviceProvided) {
                            LocalDate serviceProvidedDate = serviceProvided.localDate();
                            return PNC_IDENTIFIER.equalsIgnoreCase(serviceProvided.name()) && (
                                    serviceProvidedDate.isBefore(visitEndDate)
                                            || serviceProvidedDate.equals(visitEndDate));
                        }
                    }));
        }
        return validServicesProvided;
    }

    private List<ServiceProvidedDTO> removeDuplicateServicesAndReturnList
            (List<ServiceProvidedDTO> serviceList) {
        if (serviceList != null) {
            Set<ServiceProvidedDTO> serviceSet = new HashSet<ServiceProvidedDTO>(serviceList);
            //To function this correctly the hash code of the object and equals method should
            // override and should return the proper values
            serviceList.clear();
            serviceList.addAll(serviceSet);
        }
        return serviceList;
    }

    private List<ServiceProvidedDTO> findAndSetTheVisitDayOfTheServicesWithRespectToDeliveryDate
            (List<ServiceProvidedDTO> services, LocalDate deliveryDate) {
        if (services != null) {
            for (ServiceProvidedDTO service : services) {
                service.withDay(dayDifference(service.localDate(), deliveryDate));
            }
        }
        return services;
    }

    static class DateComparator implements Comparator<ServiceProvidedDTO> {

        @Override
        public int compare(ServiceProvidedDTO serviceProvidedDTO1, ServiceProvidedDTO
                serviceProvidedDTO2) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date date1;
            Date date2;
            try {
                date1 = simpleDateFormat.parse(serviceProvidedDTO1.date());
                date2 = simpleDateFormat.parse(serviceProvidedDTO2.date());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                //TODO
            }
            return -1;
        }
    }
}