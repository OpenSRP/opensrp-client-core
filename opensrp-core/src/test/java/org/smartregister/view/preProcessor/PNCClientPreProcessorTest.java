package org.smartregister.view.preProcessor;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.util.DateUtil;
import org.smartregister.view.contract.ServiceProvidedDTO;
import org.smartregister.view.contract.pnc.PNCCircleDatum;
import org.smartregister.view.contract.pnc.PNCClient;
import org.smartregister.view.contract.pnc.PNCLineDatum;
import org.smartregister.view.contract.pnc.PNCStatusColor;
import org.smartregister.view.contract.pnc.PNCStatusDatum;
import org.smartregister.view.contract.pnc.PNCTickDatum;
import org.smartregister.view.contract.pnc.PNCVisitDaysDatum;
import org.smartregister.view.contract.pnc.PNCVisitStatus;
import org.smartregister.view.contract.pnc.PNCVisitType;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PNCClientPreProcessorTest {
    @Mock
    private Context mockedContext;

    @Mock
    private CoreLibrary coreLibrary;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        doReturn(mockedContext).when(coreLibrary).context();
        when(mockedContext.getStringResource(R.string.str_pnc_circle_type_expected)).thenReturn("expected");
        when(mockedContext.getStringResource(R.string.str_pnc_circle_type_actual)).thenReturn("actual");
    }

    @Test
    public void should_properly_calculate_the_expected_visits_day_date_values() throws Exception {
        PNCClient pncClient = new PNCClient("entityId", "village", "name", "1122334", "2013-05-13");
        ServiceProvidedDTO expectedVisit1 = new ServiceProvidedDTO("PNC", 1, "2013-05-14");
        ServiceProvidedDTO expectedVisit2 = new ServiceProvidedDTO("PNC", 3, "2013-05-16");
        ServiceProvidedDTO expectedVisit3 = new ServiceProvidedDTO("PNC", 7, "2013-05-20");
        List<ServiceProvidedDTO> expectedVisits = Arrays.asList(expectedVisit1,
                expectedVisit2,
                expectedVisit3);

        PNCClient processedPNCClient = new PNCClientPreProcessor().preProcess(pncClient);

        assertEquals(expectedVisits, processedPNCClient.expectedVisits());
    }

    @Test
    public void shouldGenerateViewElementsWhenTodayIsTheDeliveryDate() throws Exception {
        PNCClient pncClient = new PNCClient("entityId",
                "village",
                "name",
                "theyiNo",
                DateUtil.today().toString());
        PNCClient processedClient = new PNCClientPreProcessor().preProcess(pncClient);

        List<PNCCircleDatum> expectedCircleData = Arrays.asList(
                new PNCCircleDatum(1, PNCVisitType.EXPECTED, false),
                new PNCCircleDatum(3, PNCVisitType.EXPECTED, false),
                new PNCCircleDatum(7, PNCVisitType.EXPECTED, false)
        );
        List<PNCTickDatum> expectedTickData = Arrays.asList(
                new PNCTickDatum(2, PNCVisitType.EXPECTED),
                new PNCTickDatum(4, PNCVisitType.EXPECTED),
                new PNCTickDatum(5, PNCVisitType.EXPECTED),
                new PNCTickDatum(6, PNCVisitType.EXPECTED)
        );
        List<PNCVisitDaysDatum> expectedVisitDaysData = Arrays.asList(
                new PNCVisitDaysDatum(1, PNCVisitType.EXPECTED),
                new PNCVisitDaysDatum(3, PNCVisitType.EXPECTED),
                new PNCVisitDaysDatum(7, PNCVisitType.EXPECTED)
        );

        assertEquals(expectedCircleData, processedClient.pncCircleData());
        assertEquals(0, pncClient.pncStatusData().size());
        assertEquals(PNCStatusColor.GREEN, pncClient.pncVisitStatusColor());
        assertEquals(expectedTickData, pncClient.pncTickData());
        assertEquals(Arrays.asList(new PNCLineDatum(1, 7, PNCVisitType.EXPECTED)),
                pncClient.pncLineData());
        assertEquals(expectedVisitDaysData, pncClient.visitDaysData());
    }

    @Test
    public void shouldGenerateViewElements_whenTodayIs2DaysFromDeliveryDate() throws Exception {
        DateUtil.fakeIt(new LocalDate("2013-05-15"));
        PNCClient pncClient = new PNCClient("entityId", "village", "name", "thayiNo", "2013-05-13");

        PNCClient processedClient = new PNCClientPreProcessor().preProcess(pncClient);

        List<PNCCircleDatum> expectedCircleData = Arrays.asList(
                new PNCCircleDatum(1, PNCVisitType.EXPECTED, true),
                new PNCCircleDatum(3, PNCVisitType.EXPECTED, false),
                new PNCCircleDatum(7, PNCVisitType.EXPECTED, false)
        );
        List<PNCTickDatum> expectedTickData = Arrays.asList(
                new PNCTickDatum(2, PNCVisitType.ACTUAL),
                new PNCTickDatum(4, PNCVisitType.EXPECTED),
                new PNCTickDatum(5, PNCVisitType.EXPECTED),
                new PNCTickDatum(6, PNCVisitType.EXPECTED)
        );
        List<PNCLineDatum> expectedLineData = Arrays.asList(
                new PNCLineDatum(1, 2, PNCVisitType.ACTUAL),
                new PNCLineDatum(2, 7, PNCVisitType.EXPECTED)
        );
        List<PNCVisitDaysDatum> expectedVisitDays = Arrays.asList(
                new PNCVisitDaysDatum(3, PNCVisitType.EXPECTED),
                new PNCVisitDaysDatum(7, PNCVisitType.EXPECTED)
        );
        assertEquals(expectedCircleData, processedClient.pncCircleData());
        assertEquals(Arrays.asList(new PNCStatusDatum(1, PNCVisitStatus.MISSED)),
                processedClient.pncStatusData());
        assertEquals(PNCStatusColor.RED, processedClient.pncVisitStatusColor());
        assertEquals(expectedTickData, processedClient.pncTickData());
        assertEquals(expectedLineData, processedClient.pncLineData());
        assertEquals(expectedVisitDays, processedClient.visitDaysData());
    }

    @Test
    public void shouldGenerateViewElementsWhenTodayIs3DaysFromDeliveryDateAndServiceIsProvidedOnDay2()
            throws Exception {
        DateUtil.fakeIt(new LocalDate("2013-05-16"));
        PNCClient pncClient = new PNCClient("entityId", "village", "name", "thayiNo", "2013-05-13")
                .withServicesProvided(Arrays.asList(new ServiceProvidedDTO("PNC",
                        "2013-05-15",
                        null)));

        PNCClient processedClient = new PNCClientPreProcessor().preProcess(pncClient);

        List<PNCCircleDatum> expectedCircleData = Arrays.asList(
                new PNCCircleDatum(1, PNCVisitType.EXPECTED, true),
                new PNCCircleDatum(3, PNCVisitType.EXPECTED, false),
                new PNCCircleDatum(7, PNCVisitType.EXPECTED, false),
                new PNCCircleDatum(2, PNCVisitType.ACTUAL, true)
        );
        List<PNCTickDatum> expectedPNCTickData = Arrays.asList(
                new PNCTickDatum(4, PNCVisitType.EXPECTED),
                new PNCTickDatum(5, PNCVisitType.EXPECTED),
                new PNCTickDatum(6, PNCVisitType.EXPECTED)
        );
        List<PNCLineDatum> expectedLineData = Arrays.asList(
                new PNCLineDatum(1, 3, PNCVisitType.ACTUAL),
                new PNCLineDatum(3, 7, PNCVisitType.EXPECTED)
        );
        List<PNCVisitDaysDatum> expectedvisitDaysData = Arrays.asList(
                new PNCVisitDaysDatum(7, PNCVisitType.EXPECTED),
                new PNCVisitDaysDatum(2, PNCVisitType.ACTUAL)
        );
        List<PNCStatusDatum> expectedStatusData = Arrays.asList(
                new PNCStatusDatum(1, PNCVisitStatus.MISSED));
        assertEquals("create a circle of type actual on day 2",
                expectedCircleData, processedClient.pncCircleData());
        assertEquals("create a missed status on day 1",
                expectedStatusData, processedClient.pncStatusData());
        assertEquals("set active color to yellow",
                PNCStatusColor.YELLOW, processedClient.pncVisitStatusColor());
        assertEquals("should not create a tick on day 2 with grey ticks on the remainder",
                expectedPNCTickData, processedClient.pncTickData());
        assertEquals("create an actual line from 1 to 3 and an expected line from 3 to 7",
                expectedLineData, processedClient.pncLineData());
        assertEquals("show day nos on days 2 and 7",
                expectedvisitDaysData, processedClient.visitDaysData());
    }

    @Test
    public void test_8th_day_with_all_services_provided_on_time() throws Exception {
        DateUtil.fakeIt(new LocalDate("2013-05-21"));
        PNCClient client = new PNCClient("entityId", "village", "name", "thayiNo", "2013-05-13")
                .withServicesProvided(Arrays.asList(
                        new ServiceProvidedDTO("PNC", "2013-05-26", null),
                        new ServiceProvidedDTO("PNC", "2013-05-20", null),
                        new ServiceProvidedDTO("PNC", "2013-05-16", null),
                        new ServiceProvidedDTO("PNC", "2013-05-16", null),
                        new ServiceProvidedDTO("PNC", "2013-05-15", null),
                        new ServiceProvidedDTO("PNC", "2013-05-14", null)
                ));

        PNCClient processedClient = new PNCClientPreProcessor().preProcess(client);

        List<PNCCircleDatum> expectedCircleData = Arrays.asList(
                new PNCCircleDatum(1, PNCVisitType.ACTUAL, true),
                new PNCCircleDatum(2, PNCVisitType.ACTUAL, true),
                new PNCCircleDatum(3, PNCVisitType.ACTUAL, true),
                new PNCCircleDatum(7, PNCVisitType.ACTUAL, true)
        );
        List<PNCStatusDatum> expectedStatusData = Arrays.asList(
                new PNCStatusDatum(1, PNCVisitStatus.DONE),
                new PNCStatusDatum(3, PNCVisitStatus.DONE),
                new PNCStatusDatum(7, PNCVisitStatus.DONE)
        );
        List<PNCTickDatum> expectedTickData = Arrays.asList(
                new PNCTickDatum(4, PNCVisitType.ACTUAL),
                new PNCTickDatum(5, PNCVisitType.ACTUAL),
                new PNCTickDatum(6, PNCVisitType.ACTUAL)
        );
        List<PNCVisitDaysDatum> expectedVisitDaysData = Arrays.asList(
                new PNCVisitDaysDatum(1, PNCVisitType.ACTUAL),
                new PNCVisitDaysDatum(2, PNCVisitType.ACTUAL),
                new PNCVisitDaysDatum(3, PNCVisitType.ACTUAL),
                new PNCVisitDaysDatum(7, PNCVisitType.ACTUAL)
        );
        assertEquals("create circles of type actual on each expected day",
                expectedCircleData, processedClient.pncCircleData());
        assertEquals("should create done statuses for each expected day",
                expectedStatusData, processedClient.pncStatusData());
        assertEquals("should set active color to green",
                PNCStatusColor.GREEN, processedClient.pncVisitStatusColor());
        assertEquals("should create ticks on days 4, 5 and 6",
                expectedTickData, processedClient.pncTickData());
        assertEquals("should create an actual line from 1 to 7",
                Arrays.asList(new PNCLineDatum(1, 7, PNCVisitType.ACTUAL)),
                processedClient.pncLineData());
        assertEquals("should show day nos 1, 2, 3 and 7 as actual",
                expectedVisitDaysData, processedClient.visitDaysData());
    }

    @Test
    public void test_8th_day_with_no_services_provided() throws Exception {
        DateUtil.fakeIt(new LocalDate("2013-05-21"));
        PNCClient client = new PNCClient("entityId", "village", "name", "thayino", "2013-05-13");

        PNCClient processedClient = new PNCClientPreProcessor().preProcess(client);

        List<PNCCircleDatum> expectedCircleData = Arrays.asList(
                new PNCCircleDatum(1, PNCVisitType.EXPECTED, true),
                new PNCCircleDatum(3, PNCVisitType.EXPECTED, true),
                new PNCCircleDatum(7, PNCVisitType.EXPECTED, true)
        );
        List<PNCStatusDatum> expectedStatusData = Arrays.asList(
                new PNCStatusDatum(1, PNCVisitStatus.MISSED),
                new PNCStatusDatum(3, PNCVisitStatus.MISSED),
                new PNCStatusDatum(7, PNCVisitStatus.MISSED)
        );
        List<PNCTickDatum> expectedTickData = Arrays.asList(
                new PNCTickDatum(2, PNCVisitType.ACTUAL),
                new PNCTickDatum(4, PNCVisitType.ACTUAL),
                new PNCTickDatum(5, PNCVisitType.ACTUAL),
                new PNCTickDatum(6, PNCVisitType.ACTUAL)
        );
        assertEquals("should create circles of type expected on each expected day",
                expectedCircleData, processedClient.pncCircleData());
        assertEquals("should create missed statuses for each expected day",
                expectedStatusData, processedClient.pncStatusData());
        assertEquals("should set active color to red",
                PNCStatusColor.RED, processedClient.pncVisitStatusColor());
        assertEquals("should create ticks on days 2,4,5,6",
                expectedTickData, processedClient.pncTickData());
    }

    @Test
    public void shouldPopulateRecentlyProvidedServices() throws Exception {
        DateUtil.fakeIt(new LocalDate("2014-07-13"));
        PNCClient pncClient = new PNCClient("entityId", "village", "name", "thayino", "2014-07-01");
        ServiceProvidedDTO recentService1 = new ServiceProvidedDTO("PNC", "2014-07-11", null);
        ServiceProvidedDTO recentService2 = new ServiceProvidedDTO("PNC", "2014-07-10", null);
        ServiceProvidedDTO recentService3 = new ServiceProvidedDTO("PNC", "2014-07-09", null);
        pncClient.withServicesProvided(Arrays.asList(
                new ServiceProvidedDTO("PNC", "2014-07-02", null),
                recentService3,
                recentService2,
                recentService1
        ));

        PNCClient processedClient = new PNCClientPreProcessor().preProcess(pncClient);

        assertEquals(3, processedClient.recentlyProvidedServices().size());
        assertEquals(Arrays.asList(recentService1, recentService2, recentService3),
                processedClient.recentlyProvidedServices());

        pncClient = new PNCClient("entityId", "village", "name", "thayino", "2014-07-01");
        pncClient.withServicesProvided(Arrays.asList(
                new ServiceProvidedDTO("PNC", "2014-07-02", null),
                recentService3,
                recentService1
        ));

        processedClient = new PNCClientPreProcessor().preProcess(pncClient);

        assertEquals(2, processedClient.recentlyProvidedServices().size());
        assertEquals(Arrays.asList(recentService1, recentService3),
                processedClient.recentlyProvidedServices());

        pncClient = new PNCClient("entityId", "village", "name", "thayino", "2014-07-01");
        pncClient.withServicesProvided(Arrays.asList(
                new ServiceProvidedDTO("PNC", "2014-07-02", null),
                new ServiceProvidedDTO("PNC", "2014-07-01", null),
                new ServiceProvidedDTO("PNC", "2014-07-03", null),
                new ServiceProvidedDTO("PNC", "2014-07-04", null)
        ));

        processedClient = new PNCClientPreProcessor().preProcess(pncClient);

        assertEquals(0, processedClient.recentlyProvidedServices().size());

        pncClient = new PNCClient("entityId", "village", "name", "thayino", "2014-07-01");
        pncClient.withServicesProvided(Arrays.asList(
                new ServiceProvidedDTO("PNC", "2014-07-02", null),
                new ServiceProvidedDTO("PNC", "2014-07-01", null),
                new ServiceProvidedDTO("PNC", "2014-07-03", null),
                new ServiceProvidedDTO("PNC", "2014-07-04", null),
                recentService1
        ));

        processedClient = new PNCClientPreProcessor().preProcess(pncClient);

        assertEquals(1, processedClient.recentlyProvidedServices().size());
    }
}