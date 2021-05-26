package org.smartregister.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Child;
import org.smartregister.domain.Mother;
import org.smartregister.domain.ServiceProvided;
import org.smartregister.domain.TimelineEvent;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.domain.form.SubForm;
import org.smartregister.repository.AllAlerts;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllTimelineEvents;
import org.smartregister.repository.ChildRepository;
import org.smartregister.repository.MotherRepository;
import org.smartregister.util.EasyMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ChildServiceTest extends BaseUnitTest {

    @Mock
    private AllTimelineEvents allTimelineEvents;
    @Mock
    private ChildRepository childRepository;
    @Mock
    private MotherRepository motherRepository;
    @Mock
    private ServiceProvidedService serviceProvidedService;
    @Mock
    private AllAlerts allAlerts;
    @Mock
    private AllBeneficiaries allBeneficiaries;
    @Mock
    private Child child;
    private ChildService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new ChildService(allBeneficiaries, motherRepository, childRepository, allTimelineEvents, serviceProvidedService, allAlerts);
    }

    @Test
    public void shouldUpdateEveryOnlyNewlyBornChildrenWhileRegistering() throws Exception {
        Child firstChild = new Child("Child X", "Mother X", "female", EasyMap.create("weight", "3").put("immunizationsGiven", "bcg opv_0").map());
        Child secondChild = new Child("Child Y", "Mother X", "female", EasyMap.create("weight", "4").put("immunizationsGiven", "bcg").map());
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        SubForm subForm = Mockito.mock(SubForm.class);
        Mockito.when(motherRepository.findById("Mother X")).thenReturn(new Mother("Mother X", "EC 1", "TC 1", "2012-01-01"));
        Mockito.when(childRepository.find("Child X")).thenReturn(firstChild);
        Mockito.when(childRepository.find("Child Y")).thenReturn(secondChild);
        Mockito.when(submission.entityId()).thenReturn("Mother X");
        Mockito.when(submission.getFieldValue("referenceDate")).thenReturn("2012-01-01");
        Mockito.when(submission.getFieldValue("deliveryPlace")).thenReturn("phc");
        Mockito.when(submission.getSubFormByName("child_registration")).thenReturn(subForm);
        Mockito.when(subForm.instances()).thenReturn(Arrays.asList(EasyMap.mapOf("id", "Child X"), EasyMap.mapOf("id", "Child Y")));

        service.register(submission);

        Mockito.verify(childRepository).find("Child X");
        Mockito.verify(childRepository).find("Child Y");
        Mockito.verify(childRepository).update(firstChild.setIsClosed(false).setDateOfBirth("2012-01-01").setThayiCardNumber("TC 1"));
        Mockito.verify(childRepository).update(secondChild.setIsClosed(false).setDateOfBirth("2012-01-01").setThayiCardNumber("TC 1"));
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildBirthInChildProfile("Child X", "2012-01-01", "3", "bcg opv_0"));
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildBirthInChildProfile("Child Y", "2012-01-01", "4", "bcg"));
        Mockito.verify(allTimelineEvents, Mockito.times(2)).add(TimelineEvent.forChildBirthInMotherProfile("Mother X", "2012-01-01", "female", "2012-01-01", "phc"));
        Mockito.verify(allTimelineEvents, Mockito.times(2)).add(TimelineEvent.forChildBirthInECProfile("EC 1", "2012-01-01", "female", "2012-01-01"));
        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("Child X", "bcg", "2012-01-01"));
        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("Child X", "opv_0", "2012-01-01"));
        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("Child Y", "bcg", "2012-01-01"));
        Mockito.verifyNoMoreInteractions(childRepository);
        Mockito.verifyNoMoreInteractions(allTimelineEvents);
    }

    @Test
    public void shouldDeleteRegisteredChildWhenDeliveryOutcomeIsStillBirth() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        SubForm subForm = Mockito.mock(SubForm.class);
        Mockito.when(submission.entityId()).thenReturn("Mother X");
        Mockito.when(submission.getFieldValue("referenceDate")).thenReturn("2012-01-01");
        Mockito.when(submission.getFieldValue("deliveryPlace")).thenReturn("phc");
        Mockito.when(submission.getFieldValue("deliveryOutcome")).thenReturn("still_birth");
        Mockito.when(submission.getSubFormByName("child_registration")).thenReturn(subForm);
        Mockito.when(subForm.instances()).thenReturn(Arrays.asList(EasyMap.mapOf("id", "Child X")));

        service.register(submission);

        Mockito.verify(childRepository).delete("Child X");
        Mockito.verifyNoMoreInteractions(childRepository);
        Mockito.verifyNoMoreInteractions(allTimelineEvents);
        Mockito.verifyNoMoreInteractions(serviceProvidedService);
    }

    @Test
    public void shouldUpdateNewlyRegisteredChildrenDuringPNCRegistrationOA() throws Exception {
        Child firstChild = new Child("Child X", "Mother X", "female", EasyMap.create("weight", "3").put("immunizationsGiven", "bcg opv_0").map());
        Child secondChild = new Child("Child Y", "Mother X", "female", EasyMap.create("weight", "4").put("immunizationsGiven", "bcg").map());
        Mother mother = new Mother("Mother X", "EC X", "TC 1", "2012-01-02");
        Mockito.when(motherRepository.findAllCasesForEC("EC X")).thenReturn(Arrays.asList(mother));
        Mockito.when(childRepository.find("Child X")).thenReturn(firstChild);
        Mockito.when(childRepository.find("Child Y")).thenReturn(secondChild);
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        SubForm subForm = Mockito.mock(SubForm.class);
        Mockito.when(submission.entityId()).thenReturn("EC X");
        Mockito.when(submission.getFieldValue("referenceDate")).thenReturn("2012-01-01");
        Mockito.when(submission.getFieldValue("deliveryPlace")).thenReturn("subcenter");
        Mockito.when(submission.getSubFormByName("child_registration_oa")).thenReturn(subForm);
        Mockito.when(subForm.instances()).thenReturn(Arrays.asList(EasyMap.mapOf("id", "Child X"), EasyMap.mapOf("id", "Child Y")));

        service.pncRegistrationOA(submission);

        Mockito.verify(childRepository).find("Child X");
        Mockito.verify(childRepository).find("Child Y");
        Mockito.verify(childRepository).update(firstChild.setIsClosed(false).setDateOfBirth("2012-01-01").setThayiCardNumber("TC 1"));
        Mockito.verify(childRepository).update(secondChild.setIsClosed(false).setDateOfBirth("2012-01-01").setThayiCardNumber("TC 1"));
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildBirthInChildProfile("Child X", "2012-01-01", "3", "bcg opv_0"));
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildBirthInChildProfile("Child Y", "2012-01-01", "4", "bcg"));
        Mockito.verify(allTimelineEvents, Mockito.times(2)).add(TimelineEvent.forChildBirthInMotherProfile("Mother X", "2012-01-01", "female", "2012-01-01", "subcenter"));
        Mockito.verify(allTimelineEvents, Mockito.times(2)).add(TimelineEvent.forChildBirthInECProfile("EC X", "2012-01-01", "female", "2012-01-01"));

        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("Child X", "bcg", "2012-01-01"));
        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("Child X", "opv_0", "2012-01-01"));
        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("Child Y", "bcg", "2012-01-01"));
        Mockito.verifyNoMoreInteractions(childRepository);
    }

    @Test
    public void shouldDeleteRegisteredChildWhenPNCRegistrationOAIsHandledAndDeliveryOutcomeIsStillBirth() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        SubForm subForm = Mockito.mock(SubForm.class);
        Mockito.when(submission.entityId()).thenReturn("Mother X");
        Mockito.when(submission.getFieldValue("referenceDate")).thenReturn("2012-01-01");
        Mockito.when(submission.getFieldValue("deliveryPlace")).thenReturn("phc");
        Mockito.when(submission.getFieldValue("deliveryOutcome")).thenReturn("still_birth");
        Mockito.when(submission.getSubFormByName("child_registration_oa")).thenReturn(subForm);
        Mockito.when(subForm.instances()).thenReturn(Arrays.asList(EasyMap.mapOf("id", "Child X")));

        service.pncRegistrationOA(submission);

        Mockito.verify(childRepository).delete("Child X");
        Mockito.verifyNoMoreInteractions(childRepository);
        Mockito.verifyNoMoreInteractions(allTimelineEvents);
        Mockito.verifyNoMoreInteractions(serviceProvidedService);
    }

    @Test
    public void shouldCheckForEmptyInstanceInTheCaseOfStillBirth() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        SubForm subForm = Mockito.mock(SubForm.class);
        Mockito.when(submission.entityId()).thenReturn("Mother X");
        Mockito.when(submission.getFieldValue("referenceDate")).thenReturn("2012-01-01");
        Mockito.when(submission.getFieldValue("deliveryPlace")).thenReturn("phc");
        Mockito.when(submission.getFieldValue("deliveryOutcome")).thenReturn("still_birth");
        Mockito.when(submission.getSubFormByName("child_registration_oa")).thenReturn(subForm);
        Mockito.when(subForm.instances()).thenReturn(new ArrayList<Map<String, String>>());

        service.pncRegistrationOA(submission);

        Mockito.verifyNoMoreInteractions(childRepository);
        Mockito.verifyNoMoreInteractions(allTimelineEvents);
        Mockito.verifyNoMoreInteractions(serviceProvidedService);
    }

    @Test
    public void shouldAddTimelineEventsWhenChildImmunizationsAreUpdated() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mockito.when(submission.entityId()).thenReturn("child id 1");
        Mockito.when(submission.getFieldValue("previousImmunizations")).thenReturn("bcg");
        Mockito.when(submission.getFieldValue("immunizationsGiven")).thenReturn("bcg opv_0 pentavalent_0");
        Mockito.when(submission.getFieldValue("immunizationDate")).thenReturn("2013-01-01");

        service.updateImmunizations(submission);

        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildImmunization("child id 1", "opv_0", "2013-01-01"));
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildImmunization("child id 1", "pentavalent_0", "2013-01-01"));
        Mockito.verifyNoMoreInteractions(allTimelineEvents);
    }

    @Test
    public void shouldAddServiceProvidedWhenChildImmunizationsAreUpdated() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mockito.when(submission.entityId()).thenReturn("child id 1");
        Mockito.when(submission.getFieldValue("previousImmunizations")).thenReturn("bcg");
        Mockito.when(submission.getFieldValue("immunizationsGiven")).thenReturn("bcg opv_0 pentavalent_0");
        Mockito.when(submission.getFieldValue("immunizationDate")).thenReturn("2013-01-01");

        service.updateImmunizations(submission);

        Mockito.verify(serviceProvidedService).add(new ServiceProvided("child id 1", "opv_0", "2013-01-01", null));
        Mockito.verify(serviceProvidedService).add(new ServiceProvided("child id 1", "pentavalent_0", "2013-01-01", null));
        Mockito.verifyNoMoreInteractions(serviceProvidedService);
    }

    @Test
    public void shouldMarkRemindersAsInProcessWhenImmunizationsAreProvided() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mockito.when(submission.entityId()).thenReturn("child id 1");
        Mockito.when(submission.getFieldValue("previousImmunizations")).thenReturn("bcg");
        Mockito.when(submission.getFieldValue("immunizationsGiven")).thenReturn("bcg opv_0 pentavalent_0");
        Mockito.when(submission.getFieldValue("immunizationDate")).thenReturn("2013-01-01");

        service.updateImmunizations(submission);

        Mockito.verify(allAlerts).changeAlertStatusToInProcess("child id 1", "opv_0");
        Mockito.verify(allAlerts).changeAlertStatusToInProcess("child id 1", "pentavalent_0");
        Mockito.verifyNoMoreInteractions(allAlerts);
    }

    @Test
    public void shouldAddTimelineEventWhenChildIsRegisteredForEC() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi card number", "2013-01-01");
        Mockito.when(submission.entityId()).thenReturn("ec id 1");
        Mockito.when(submission.getFieldValue("motherId")).thenReturn("mother id 1");
        Mockito.when(submission.getFieldValue("childId")).thenReturn("child id 1");
        Mockito.when(submission.getFieldValue("dateOfBirth")).thenReturn("2013-01-02");
        Mockito.when(submission.getFieldValue("gender")).thenReturn("female");
        Mockito.when(submission.getFieldValue("weight")).thenReturn("3");
        Mockito.when(submission.getFieldValue("immunizationsGiven")).thenReturn("bcg opv_0");
        Mockito.when(submission.getFieldValue("bcgDate")).thenReturn("2012-01-06");
        Mockito.when(submission.getFieldValue("opv0Date")).thenReturn("2012-01-07");
        Mockito.when(submission.getFieldValue("shouldCloseMother")).thenReturn("");
        Mockito.when(allBeneficiaries.findMother("mother id 1")).thenReturn(mother);

        service.registerForEC(submission);

        Mockito.verify(allBeneficiaries).updateMother(mother.setIsClosed(true));
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildBirthInChildProfile("child id 1", "2013-01-02", "3", "bcg opv_0"));
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildBirthInMotherProfile("mother id 1", "2013-01-02", "female", null, null));
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildBirthInECProfile("ec id 1", "2013-01-02", "female", null));

        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("child id 1", "bcg", "2012-01-06"));
        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("child id 1", "opv_0", "2012-01-07"));
    }

    @Test
    public void shouldNotCloseMotherWhenAnOpenANCAlreadyExistWhileRegisteringAChildForEC() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mockito.when(submission.entityId()).thenReturn("ec id 1");
        Mockito.when(submission.getFieldValue("motherId")).thenReturn("mother id 1");
        Mockito.when(submission.getFieldValue("childId")).thenReturn("child id 1");
        Mockito.when(submission.getFieldValue("dateOfBirth")).thenReturn("2013-01-02");
        Mockito.when(submission.getFieldValue("gender")).thenReturn("female");
        Mockito.when(submission.getFieldValue("weight")).thenReturn("3");
        Mockito.when(submission.getFieldValue("immunizationsGiven")).thenReturn("bcg opv_0");
        Mockito.when(submission.getFieldValue("bcgDate")).thenReturn("2012-01-06");
        Mockito.when(submission.getFieldValue("opv0Date")).thenReturn("2012-01-07");
        Mockito.when(submission.getFieldValue("shouldCloseMother")).thenReturn("false");

        service.registerForEC(submission);

        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildBirthInChildProfile("child id 1", "2013-01-02", "3", "bcg opv_0"));
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildBirthInMotherProfile("mother id 1", "2013-01-02", "female", null, null));
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildBirthInECProfile("ec id 1", "2013-01-02", "female", null));

        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("child id 1", "bcg", "2012-01-06"));
        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("child id 1", "opv_0", "2012-01-07"));
    }

    @Test
    public void shouldAddPNCVisitTimelineEventWhenPNCVisitHappens() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        SubForm subForm = Mockito.mock(SubForm.class);
        Mockito.when(submission.entityId()).thenReturn("mother id 1");
        Mockito.when(submission.getFieldValue("pncVisitDay")).thenReturn("2");
        Mockito.when(submission.getFieldValue("pncVisitDate")).thenReturn("2012-01-01");
        Mockito.when(submission.getSubFormByName("child_pnc_visit")).thenReturn(subForm);
        Mockito.when(subForm.instances()).thenReturn(
                Arrays.asList(
                        EasyMap.create("id", "child id 1")
                                .put("weight", "3")
                                .put("temperature", "98")
                                .map(),
                        EasyMap.create("id", "child id 2")
                                .put("weight", "4")
                                .put("temperature", "98.1")
                                .map()));

        service.pncVisitHappened(submission);

        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildPNCVisit("child id 1", "2", "2012-01-01", "3", "98"));
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildPNCVisit("child id 2", "2", "2012-01-01", "4", "98.1"));
    }

    @Test
    public void shouldHandleStillBirthWhenPNCVisitHappens() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        SubForm subForm = Mockito.mock(SubForm.class);
        Mockito.when(submission.entityId()).thenReturn("mother id 1");
        Mockito.when(submission.getFieldValue("pncVisitDay")).thenReturn("2");
        Mockito.when(submission.getFieldValue("pncVisitDate")).thenReturn("2012-01-01");
        Mockito.when(submission.getFieldValue("deliveryOutcome")).thenReturn("still_birth");
        Mockito.when(submission.getSubFormByName("child_pnc_visit")).thenReturn(subForm);
        Mockito.when(subForm.instances()).thenReturn(Arrays.asList(EasyMap.create("id", "child id 1").map()));

        service.pncVisitHappened(submission);

        Mockito.verify(childRepository).delete("child id 1");
        Mockito.verifyNoMoreInteractions(allTimelineEvents);
    }

    @Test
    public void shouldAddPNCVisitServiceProvidedWhenPNCVisitHappens() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        SubForm subForm = Mockito.mock(SubForm.class);
        Mockito.when(submission.entityId()).thenReturn("mother id 1");
        Mockito.when(submission.getFieldValue("pncVisitDay")).thenReturn("2");
        Mockito.when(submission.getFieldValue("pncVisitDate")).thenReturn("2012-01-01");
        Mockito.when(submission.getSubFormByName("child_pnc_visit")).thenReturn(subForm);
        Mockito.when(subForm.instances()).thenReturn(
                Arrays.asList(EasyMap.mapOf("id", "child id 1"), EasyMap.mapOf("id", "child id 2")));

        service.pncVisitHappened(submission);

        Mockito.verify(serviceProvidedService).add(new ServiceProvided("child id 1", "PNC", "2012-01-01", EasyMap.mapOf("day", "2")));
        Mockito.verify(serviceProvidedService).add(new ServiceProvided("child id 2", "PNC", "2012-01-01", EasyMap.mapOf("day", "2")));
    }

    @Test
    public void shouldCloseChildRecordForDeleteChildAction() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mockito.when(submission.entityId()).thenReturn("child id 1");

        service.close(submission);

        Mockito.verify(allBeneficiaries).closeChild("child id 1");
    }

    @Test
    public void shouldUpdateIllnessForUpdateIllnessAction() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);

        Mockito.when(submission.entityId()).thenReturn("child id 1");
        Mockito.when(submission.getFieldValue("submissionDate")).thenReturn("2012-01-02");
        Mockito.when(submission.getFieldValue("sickVisitDate")).thenReturn("2012-01-01");
        Mockito.when(submission.getFieldValue("childSigns")).thenReturn("child signs");
        Mockito.when(submission.getFieldValue("childSignsOther")).thenReturn("child signs other");
        Mockito.when(submission.getFieldValue("reportChildDisease")).thenReturn("report child disease");
        Mockito.when(submission.getFieldValue("reportChildDiseaseOther")).thenReturn("report child disease other");
        Mockito.when(submission.getFieldValue("reportChildDiseaseDate")).thenReturn(null);
        Mockito.when(submission.getFieldValue("reportChildDiseasePlace")).thenReturn("report child disease place");
        Mockito.when(submission.getFieldValue("childReferral")).thenReturn("child referral");

        service.updateIllnessStatus(submission);

        Map<String, String> map = EasyMap.create("sickVisitDate", "2012-01-01")
                .put("childSignsOther", "child signs other")
                .put("childSigns", "child signs")
                .put("reportChildDisease", "report child disease")
                .put("reportChildDiseaseOther", "report child disease other")
                .put("reportChildDiseaseDate", null)
                .put("reportChildDiseasePlace", "report child disease place")
                .put("childReferral", "child referral").map();

        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildIllnessVisit("child id 1", "2012-01-01", map));
    }

    @Test
    public void shouldUpdateVitaminADosagesForUpdateVitaminAProvidedAction() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);

        Mockito.when(submission.entityId()).thenReturn("child id 1");
        Mockito.when(submission.getFieldValue("vitaminADate")).thenReturn("2012-01-01");
        Mockito.when(submission.getFieldValue("vitaminADose")).thenReturn("1");
        Mockito.when(submission.getFieldValue("vitaminAPlace")).thenReturn("PHC");

        service.updateVitaminAProvided(submission);

        Mockito.verify(serviceProvidedService).add(ServiceProvided.forVitaminAProvided("child id 1", "2012-01-01", "1", "PHC"));
    }

    @Test
    public void shouldAddTimelineEventWhenChildIsRegisteredForOA() throws Exception {
        FormSubmission submission = Mockito.mock(FormSubmission.class);
        Mockito.when(submission.entityId()).thenReturn("ec id 1");
        Mockito.when(submission.getFieldValue("motherId")).thenReturn("mother id 1");
        Mockito.when(submission.getFieldValue("id")).thenReturn("child id 1");
        Mockito.when(submission.getFieldValue("dateOfBirth")).thenReturn("2013-01-02");
        Mockito.when(submission.getFieldValue("gender")).thenReturn("female");
        Mockito.when(submission.getFieldValue("weight")).thenReturn("3");
        Mockito.when(submission.getFieldValue("immunizationsGiven")).thenReturn("bcg opv_0");
        Mockito.when(submission.getFieldValue("bcgDate")).thenReturn("2012-01-06");
        Mockito.when(submission.getFieldValue("opv0Date")).thenReturn("2012-01-07");
        Mockito.when(submission.getFieldValue("thayiCardNumber")).thenReturn("1234567");
        Mockito.when(allBeneficiaries.findChild("child id 1")).thenReturn(child);

        service.registerForOA(submission);

        Mockito.verify(child).setThayiCardNumber("1234567");
        Mockito.verify(allBeneficiaries).updateChild(child);
        Mockito.verify(allTimelineEvents).add(TimelineEvent.forChildBirthInChildProfile("child id 1", "2013-01-02", "3", "bcg opv_0"));

        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("ec id 1", "bcg", "2012-01-06"));
        Mockito.verify(serviceProvidedService).add(ServiceProvided.forChildImmunization("ec id 1", "opv_0", "2012-01-07"));
    }
}
