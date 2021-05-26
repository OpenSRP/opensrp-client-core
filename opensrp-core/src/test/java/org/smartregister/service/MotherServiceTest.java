package org.smartregister.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Mother;
import org.smartregister.domain.ServiceProvided;
import org.smartregister.domain.TimelineEvent;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.repository.AllTimelineEvents;
import org.smartregister.repository.EligibleCoupleRepository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.domain.TimelineEvent.forDeliveryPlan;
import static org.smartregister.domain.TimelineEvent.forIFATabletsGiven;
import static org.smartregister.domain.TimelineEvent.forTTShotProvided;
import static org.smartregister.util.EasyMap.create;
import static org.smartregister.util.EasyMap.mapOf;

public class MotherServiceTest extends BaseUnitTest {
    @Mock
    private AllBeneficiaries allBeneficiaries;
    @Mock
    private EligibleCoupleRepository eligibleCoupleRepository;
    @Mock
    private AllTimelineEvents allTimelineEvents;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private ServiceProvidedService serviceProvidedService;

    private MotherService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new MotherService(allBeneficiaries, allEligibleCouples, allTimelineEvents, serviceProvidedService);
    }

    @Test
    public void shouldRegisterANC() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("motherId")).thenReturn("mother id 1");
        when(submission.getFieldValue("thayiCardNumber")).thenReturn("thayi 1");
        when(submission.getFieldValue("registrationDate")).thenReturn("2012-01-02");
        when(submission.getFieldValue("referenceDate")).thenReturn("2012-01-01");

        service.registerANC(submission);

        allTimelineEvents.add(TimelineEvent.forStartOfPregnancy("mother id 1", "2012-01-02", "2012-01-01"));
        allTimelineEvents.add(TimelineEvent.forStartOfPregnancyForEC("entity id 1", "thayi 1", "2012-01-02", "2012-01-01"));
    }

    @Test
    public void shouldRegisterOutOfAreaANC() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("motherId")).thenReturn("mother id 1");
        when(submission.getFieldValue("thayiCardNumber")).thenReturn("thayi 1");
        when(submission.getFieldValue("registrationDate")).thenReturn("2012-01-02");
        when(submission.getFieldValue("referenceDate")).thenReturn("2012-01-01");

        service.registerOutOfAreaANC(submission);

        allTimelineEvents.add(TimelineEvent.forStartOfPregnancy("mother id 1", "2012-01-02", "2012-01-01"));
        allTimelineEvents.add(TimelineEvent.forStartOfPregnancyForEC("entity id 1", "thayi 1", "2012-01-02", "2012-01-01"));
    }

    @Test
    public void shouldCreateTimelineEventsWhenANCVisitHappens() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("ancVisitDate")).thenReturn("2013-01-01");
        when(submission.getFieldValue("ancVisitNumber")).thenReturn("2");
        when(submission.getFieldValue("weight")).thenReturn("21");
        when(submission.getFieldValue("bpDiastolic")).thenReturn("80");
        when(submission.getFieldValue("bpSystolic")).thenReturn("90");
        when(submission.getFieldValue("temperature")).thenReturn("98.5");

        service.ancVisit(submission);

        verify(allTimelineEvents).add(TimelineEvent.forANCCareProvided("entity id 1", "2", "2013-01-01",
                create("bpDiastolic", "80").put("bpSystolic", "90").put("temperature", "98.5").put("weight", "21").map()));
    }

    @Test
    public void shouldAddServiceProvidedWhenANCVisitHappens() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("ancVisitDate")).thenReturn("2013-01-01");
        when(submission.getFieldValue("ancVisitNumber")).thenReturn("1");
        when(submission.getFieldValue("weight")).thenReturn("21");
        when(submission.getFieldValue("bpDiastolic")).thenReturn("80");
        when(submission.getFieldValue("bpSystolic")).thenReturn("90");

        service.ancVisit(submission);

        verify(serviceProvidedService).add(
                new ServiceProvided("entity id 1", "ANC 1", "2013-01-01", create("bpDiastolic", "80").put("bpSystolic", "90").put("weight", "21").map()));
    }

    @Test
    public void shouldNotDoAnythingWhenANCIsClosedAndMotherDoesNotExist() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(allBeneficiaries.findMotherWithOpenStatus("entity id 1")).thenReturn(null);

        service.close(submission);

        verify(allBeneficiaries, times(0)).closeMother("entity id 1");
        verifyZeroInteractions(allEligibleCouples);
    }

    @Test
    public void shouldCloseECWhenMotherIsClosedAndReasonIsDeath() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("closeReason")).thenReturn("death_of_mother");
        when(allBeneficiaries.findMotherWithOpenStatus("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1", "2013-01-01"));

        service.close(submission);

        verify(allEligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldCloseECWhenWomanIsClosedAndReasonIsDeath() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("closeReason")).thenReturn("death_of_woman");
        when(allBeneficiaries.findMotherWithOpenStatus("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1", "2013-01-01"));

        service.close(submission);

        verify(allEligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldCloseECWhenMotherIsClosedAndReasonIsPermanentRelocation() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("closeReason")).thenReturn("relocation_permanent");
        when(allBeneficiaries.findMotherWithOpenStatus("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1", "2013-01-01"));

        service.close(submission);

        verify(allEligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldCloseECWhenMotherIsClosedUsingPNCCloseAndReasonIsPermanentRelocation() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("closeReason")).thenReturn("permanent_relocation");
        when(allBeneficiaries.findMotherWithOpenStatus("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1", "2013-01-01"));

        service.close(submission);

        verify(allEligibleCouples).close("ec entity id 1");
    }

    @Test
    public void shouldNotCloseECWhenMotherIsClosedForOtherReasons() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("closeReason")).thenReturn("other_reason");
        when(allBeneficiaries.findMotherWithOpenStatus("entity id 1")).thenReturn(new Mother("entity id 1", "ec entity id 1", "thayi 1", "2013-01-01"));

        service.close(submission);

        verifyZeroInteractions(allEligibleCouples);
    }

    @Test
    public void shouldHandleTTProvided() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("ttDose")).thenReturn("ttbooster");
        when(submission.getFieldValue("ttDate")).thenReturn("2013-01-01");

        service.ttProvided(submission);

        verify(allTimelineEvents).add(forTTShotProvided("entity id 1", "ttbooster", "2013-01-01"));
        verify(serviceProvidedService).add(new ServiceProvided("entity id 1", "TT Booster", "2013-01-01", mapOf("dose", "TT Booster")));
    }

    @Test
    public void shouldAddTimelineEventWhenIFATabletsAreGiven() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("numberOfIFATabletsGiven")).thenReturn("100");
        when(submission.getFieldValue("ifaTabletsDate")).thenReturn("2013-02-01");

        service.ifaTabletsGiven(submission);

        verify(allTimelineEvents).add(forIFATabletsGiven("entity id 1", "100", "2013-02-01"));
    }

    @Test
    public void shouldAddServiceProvidedWhenIFATabletsAreGiven() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("numberOfIFATabletsGiven")).thenReturn("100");
        when(submission.getFieldValue("ifaTabletsDate")).thenReturn("2013-02-01");

        service.ifaTabletsGiven(submission);
        verify(serviceProvidedService).add(new ServiceProvided("entity id 1", "IFA", "2013-02-01", mapOf("dose", "100")));
    }

    @Test
    public void shouldDoNothingWhenIFATabletsAreNotGiven() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("numberOfIFATabletsGiven")).thenReturn("0");
        when(submission.getFieldValue("ifaTabletsDate")).thenReturn("2013-02-01");

        service.ifaTabletsGiven(submission);

        verifyZeroInteractions(allTimelineEvents);
        verifyZeroInteractions(serviceProvidedService);
    }

    @Test
    public void shouldHandleHBTest() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("hbLevel")).thenReturn("11");
        when(submission.getFieldValue("hbTestDate")).thenReturn("2013-01-01");

        service.hbTest(submission);

        verify(serviceProvidedService).add(new ServiceProvided("entity id 1", "Hb Test", "2013-01-01", mapOf("hbLevel", "11")));
    }

    @Test
    public void shouldHandleDeliveryOutcome() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("didWomanSurvive")).thenReturn("yes");
        when(allBeneficiaries.findMotherWithOpenStatus("entity id 1")).thenReturn(new Mother("entity id 1", "ec id 1", "1234567", "2014-01-01"));
        service.deliveryOutcome(submission);

        verify(allBeneficiaries).switchMotherToPNC("entity id 1");
    }

    @Test
    public void shouldCloseMotherAndECIfDeadDuringDeliveryOutcome() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(allBeneficiaries.findMotherWithOpenStatus("entity id 1"))
                .thenReturn(new Mother("entity id 1", "ec id 1", "1234567", "2014-01-01"));
        when(submission.getFieldValue("didWomanSurvive")).thenReturn("");
        when(submission.getFieldValue("didMotherSurvive")).thenReturn("no");

        service.deliveryOutcome(submission);

        verify(allBeneficiaries, times(1)).closeMother("entity id 1");
        verify(allEligibleCouples, times(1)).close("ec id 1");
    }

    @Test
    public void shouldCloseWomanIfDeadDuringDeliveryOutcome() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(allBeneficiaries.findMotherWithOpenStatus("entity id 1")).thenReturn(new Mother("entity id 1", "ec id 1", "1234567", "2014-01-01"));
        when(submission.getFieldValue("didWomanSurvive")).thenReturn("no");
        when(submission.getFieldValue("didMotherSurvive")).thenReturn("");

        service.deliveryOutcome(submission);

        verify(allBeneficiaries, times(1)).closeMother("entity id 1");
        verify(allEligibleCouples, times(1)).close("ec id 1");
    }

    @Test
    public void shouldAddPNCVisitTimelineEventWhenPNCVisitHappens() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("pncVisitDay")).thenReturn("2");
        when(submission.getFieldValue("pncVisitDate")).thenReturn("2012-01-01");
        when(submission.getFieldValue("numberOfIFATabletsGiven")).thenReturn("100");
        when(submission.getFieldValue("bpSystolic")).thenReturn("120");
        when(submission.getFieldValue("bpDiastolic")).thenReturn("80");
        when(submission.getFieldValue("temperature")).thenReturn("98.1");
        when(submission.getFieldValue("hbLevel")).thenReturn("10.0");
        when(submission.getFieldValue("submissionDate")).thenReturn("2013-01-01");

        service.pncVisitHappened(submission);

        verify(allTimelineEvents).add(TimelineEvent.forMotherPNCVisit("entity id 1", "2", "2012-01-01", "120", "80", "98.1", "10.0"));
    }

    @Test
    public void shouldAddIFATabletsGivenTimelineEventWhenPNCVisitHappens() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("pncVisitDay")).thenReturn("2");
        when(submission.getFieldValue("pncVisitDate")).thenReturn("2012-01-01");
        when(submission.getFieldValue("numberOfIFATabletsGiven")).thenReturn("100");
        when(submission.getFieldValue("bpSystolic")).thenReturn("120");
        when(submission.getFieldValue("bpDiastolic")).thenReturn("80");
        when(submission.getFieldValue("temperature")).thenReturn("98.1");
        when(submission.getFieldValue("hbLevel")).thenReturn("10.0");
        when(submission.getFieldValue("submissionDate")).thenReturn("2012-01-02");

        service.pncVisitHappened(submission);

        verify(allTimelineEvents).add(forIFATabletsGiven("entity id 1", "100", "2012-01-02"));
    }

    @Test
    public void shouldAddPNCVisitServiceProvidedWhenPNCVisitHappens() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("pncVisitDay")).thenReturn("2");
        when(submission.getFieldValue("pncVisitDate")).thenReturn("2012-01-01");
        when(submission.getFieldValue("numberOfIFATabletsGiven")).thenReturn("100");
        when(submission.getFieldValue("bpSystolic")).thenReturn("120");
        when(submission.getFieldValue("bpDiastolic")).thenReturn("80");
        when(submission.getFieldValue("temperature")).thenReturn("98.1");
        when(submission.getFieldValue("hbLevel")).thenReturn("10.0");
        when(submission.getFieldValue("submissionDate")).thenReturn("2013-01-01");

        service.pncVisitHappened(submission);

        verify(serviceProvidedService).add(new ServiceProvided("entity id 1", "PNC", "2012-01-01", mapOf("day", "2")));
    }

    @Test
    public void shouldNotAddIFATabletsGivenTimelineEventWhenPNCVisitHappensAndNoIFATabletsWereGiven() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("pncVisitDay")).thenReturn("2");
        when(submission.getFieldValue("pncVisitDate")).thenReturn("2012-01-01");
        when(submission.getFieldValue("numberOfIFATabletsGiven")).thenReturn("");
        when(submission.getFieldValue("ifaTabletsDate")).thenReturn(null);
        when(submission.getFieldValue("bpSystolic")).thenReturn("120");
        when(submission.getFieldValue("bpDiastolic")).thenReturn("80");
        when(submission.getFieldValue("temperature")).thenReturn("98.1");
        when(submission.getFieldValue("hbLevel")).thenReturn("10.0");

        service.pncVisitHappened(submission);

        verify(allTimelineEvents, times(0)).add(forIFATabletsGiven("entity id 1", "2012-01-01", "100"));
    }

    @Test
    public void shouldAddTimelineEventWhenDeliveryPlanHappens() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("deliveryFacilityName")).thenReturn("Delivery Facility Name");
        when(submission.getFieldValue("transportationPlan")).thenReturn("Transportation Plan");
        when(submission.getFieldValue("birthCompanion")).thenReturn("Birth Companion");
        when(submission.getFieldValue("ashaPhoneNumber")).thenReturn("Asha Phone");
        when(submission.getFieldValue("phoneNumber")).thenReturn("1234567890");
        when(submission.getFieldValue("reviewedHRPStatus")).thenReturn("HRP Status");
        when(submission.getFieldValue("submissionDate")).thenReturn("2012-01-01");

        service.deliveryPlan(submission);

        verify(allTimelineEvents).add(forDeliveryPlan("entity id 1", "Delivery Facility Name", "Transportation Plan", "Birth Companion", "Asha Phone", "1234567890", "HRP Status", "2012-01-01"));
    }

    @Test
    public void shouldAddServiceProvidedWhenDeliveryPlanHappens() throws Exception {
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("entity id 1");
        when(submission.getFieldValue("deliveryFacilityName")).thenReturn("Delivery Facility Name");
        when(submission.getFieldValue("transportationPlan")).thenReturn("Transportation Plan");
        when(submission.getFieldValue("birthCompanion")).thenReturn("Birth Companion");
        when(submission.getFieldValue("ashaPhoneNumber")).thenReturn("Asha Phone");
        when(submission.getFieldValue("phoneNumber")).thenReturn("1234567890");
        when(submission.getFieldValue("reviewedHRPStatus")).thenReturn("HRP Status");
        when(submission.getFieldValue("submissionDate")).thenReturn("2012-01-01");

        service.deliveryPlan(submission);

        verify(serviceProvidedService).add(ServiceProvided.forDeliveryPlan("entity id 1", "Delivery Facility Name", "Transportation Plan", "Birth Companion", "Asha Phone", "1234567890", "HRP Status", "2012-01-01"));
    }
}
