package org.smartregister.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.TimelineEvent;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.repository.AllTimelineEvents;
import org.smartregister.repository.EligibleCoupleRepository;
import org.smartregister.repository.TimelineEventRepository;
import org.smartregister.util.EasyMap;

public class EligibleCoupleServiceTest extends BaseUnitTest {

    @Mock
    private EligibleCoupleRepository eligibleCoupleRepository;
    @Mock
    private TimelineEventRepository timelineEventRepository;
    @Mock
    private AllTimelineEvents allTimelineEvents;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllBeneficiaries allBeneficiaries;

    private EligibleCoupleService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new EligibleCoupleService(allEligibleCouples, allTimelineEvents, allBeneficiaries);
    }

    @Test
    public void shouldCreateTimelineEventWhenECIsRegistered() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mockito.when(submission.entityId()).thenReturn("entity id 1");
        Mockito.when(submission.getFieldValue("submissionDate")).thenReturn("2012-01-01");

        service.register(submission);

        Mockito.verify(allTimelineEvents).add(TimelineEvent.forECRegistered("entity id 1", "2012-01-01"));
    }

    @Test
    public void shouldCloseEC() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mockito.when(submission.entityId()).thenReturn("entity id 1");

        service.closeEligibleCouple(submission);

        Mockito.verify(allEligibleCouples).close("entity id 1");
        Mockito.verify(allBeneficiaries).closeAllMothersForEC("entity id 1");
    }

    @Test
    public void shouldNotCreateTimelineEventWhenECIsRegisteredWithoutSubmissionDate() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mockito.when(submission.entityId()).thenReturn("entity id 1");
        Mockito.when(submission.getFieldValue("submissionDate")).thenReturn(null);

        service.register(submission);

        Mockito.verifyZeroInteractions(allTimelineEvents);
    }

    @Test
    public void shouldCreateTimelineEventAndUpdateEntityWhenFPChangeIsReported() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mockito.when(submission.entityId()).thenReturn("entity id 1");
        Mockito.when(submission.getFieldValue("currentMethod")).thenReturn("condom");
        Mockito.when(submission.getFieldValue("newMethod")).thenReturn("ocp");
        Mockito.when(submission.getFieldValue("familyPlanningMethodChangeDate")).thenReturn("2012-01-01");

        service.fpChange(submission);

        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChangeOfFPMethod("entity id 1", "condom", "ocp", "2012-01-01"));
        Mockito.verify(allEligibleCouples).mergeDetails("entity id 1", EasyMap.mapOf("currentMethod", "ocp"));
    }

    @Test
    public void shouldUseFormSubmissionDateAsChangeDateWhenFPMethodIsChangedAndChangeDateIsBlank() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mockito.when(submission.entityId()).thenReturn("entity id 1");
        Mockito.when(submission.getFieldValue("currentMethod")).thenReturn("condom");
        Mockito.when(submission.getFieldValue("newMethod")).thenReturn("none");
        Mockito.when(submission.getFieldValue("submissionDate")).thenReturn("2012-02-01");

        service.fpChange(submission);

        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChangeOfFPMethod("entity id 1", "condom", "none", "2012-02-01"));
        Mockito.verify(allEligibleCouples).mergeDetails("entity id 1", EasyMap.mapOf("currentMethod", "none"));
    }
}
