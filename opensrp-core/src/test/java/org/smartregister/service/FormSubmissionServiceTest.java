package org.smartregister.service;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.SyncStatus;
import org.smartregister.domain.form.FormData;
import org.smartregister.domain.form.FormField;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.domain.form.SubForm;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.FormDataRepository;
import org.smartregister.util.EasyMap;
import org.smartregister.util.FormSubmissionBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FormSubmissionServiceTest {
    @Mock
    private ZiggyService ziggyService;
    @Mock
    private AllSettings allSettings;
    @Mock
    private FormDataRepository formDataRepository;
    @Mock
    private Map<String, AllCommonsRepository> allCommonsRepositoryMap;
    @Mock
    private AllCommonsRepository allCommonsRepository;

    private FormSubmissionService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new FormSubmissionService(ziggyService, formDataRepository, allSettings);
    }

    @Test
    public void shouldDelegateProcessingToZiggyServiceAndMarkAsSynced() throws Exception {
        List<FormSubmission> submissions = Arrays.asList(FormSubmissionBuilder.create().withInstanceId("instance id 1").withVersion("122").build(),
                FormSubmissionBuilder.create().withInstanceId("instance id 2").withVersion("123").build());

        service.processSubmissions(submissions);

        String paramsForFirstSubmission = new Gson().toJson(
                EasyMap.create("instanceId", "instance id 1")
                        .put("entityId", "entity id 1")
                        .put("formName", "form name 1")
                        .put("version", "122")
                        .put("sync_status", SyncStatus.SYNCED.value())
                        .map());
        String paramsForSecondSubmission = new Gson().toJson(
                EasyMap.create("instanceId", "instance id 2")
                        .put("entityId", "entity id 1")
                        .put("formName", "form name 1")
                        .put("sync_status", SyncStatus.SYNCED.value())
                        .put("version", "123")
                        .map());
        InOrder inOrder = Mockito.inOrder(ziggyService, allSettings, formDataRepository);
        inOrder.verify(ziggyService).saveForm(paramsForFirstSubmission, "{}");
        inOrder.verify(formDataRepository).updateServerVersion("instance id 1", "0");
        inOrder.verify(allSettings).savePreviousFormSyncIndex("0");
        inOrder.verify(ziggyService).saveForm(paramsForSecondSubmission, "{}");
        inOrder.verify(formDataRepository).updateServerVersion("instance id 2", "0");
        inOrder.verify(allSettings).savePreviousFormSyncIndex("0");
    }

    @Test
    public void shouldNotDelegateProcessingToZiggyServiceForProcessedSubmissions() throws Exception {
        FormSubmission firstFormSubmission = FormSubmissionBuilder.create().withInstanceId("instance id 1").withVersion("122").build();
        FormSubmission secondFormSubmission = FormSubmissionBuilder.create().withInstanceId("instance id 2").withVersion("123").withServerVersion("1").build();
        List<FormSubmission> submissions = Arrays.asList(firstFormSubmission, secondFormSubmission);
        when(formDataRepository.submissionExists("instance id 1")).thenReturn(true);
        when(formDataRepository.submissionExists("instance id 2")).thenReturn(false);

        service.processSubmissions(submissions);

        String paramsForFirstSubmission = new Gson().toJson(
                EasyMap.create("instanceId", "instance id 1")
                        .put("entityId", "entity id 1")
                        .put("formName", "form name 1")
                        .put("version", "122")
                        .put("sync_status", SyncStatus.SYNCED.value())
                        .map());
        String paramsForSecondSubmission = new Gson().toJson(
                EasyMap.create("instanceId", "instance id 2")
                        .put("entityId", "entity id 1")
                        .put("formName", "form name 1")
                        .put("version", "123")
                        .put("sync_status", SyncStatus.SYNCED.value())
                        .map());
        InOrder inOrder = Mockito.inOrder(ziggyService, allSettings, formDataRepository);
        inOrder.verify(ziggyService, Mockito.times(0)).saveForm(paramsForFirstSubmission, "{}");
        inOrder.verify(formDataRepository).updateServerVersion("instance id 1", "0");
        inOrder.verify(allSettings).savePreviousFormSyncIndex("0");
        inOrder.verify(ziggyService).saveForm(paramsForSecondSubmission, "{}");
        inOrder.verify(formDataRepository).updateServerVersion("instance id 2", "1");
        inOrder.verify(allSettings).savePreviousFormSyncIndex("1");
    }

    @Test
    public void testConstructor() {
        service = null;
        assertNull(service);
        service = new FormSubmissionService(ziggyService, formDataRepository, allSettings, allCommonsRepositoryMap);
        assertNotNull(service);
        assertNotNull(Whitebox.getInternalState(service, "ziggyService"));
        assertNotNull(Whitebox.getInternalState(service, "formDataRepository"));
        assertNotNull(Whitebox.getInternalState(service, "allSettings"));
        assertNotNull(Whitebox.getInternalState(service, "allCommonsRepositoryMap"));

    }

    @Test
    public void testPrivateUpdateFTSSearch() throws Exception {
        service = new FormSubmissionService(ziggyService, formDataRepository, allSettings, allCommonsRepositoryMap);
        when(allCommonsRepositoryMap.isEmpty()).thenReturn(false);
        when(allCommonsRepositoryMap.get("bindtype_1")).thenReturn(allCommonsRepository);
        Whitebox.invokeMethod(service, "updateFTSsearch", "bindtype_1", "entity_1");
        verify(allCommonsRepository).updateSearch("entity_1");
    }

    @Test
    public void testPrivateUpdateFTSSearchWithEmptyAllC0mmonsrepositoryMap() throws Exception {
        when(allCommonsRepositoryMap.isEmpty()).thenReturn(true);
        Object ftsSearchUpdated = Whitebox.invokeMethod(service, "updateFTSsearch", "bindtype_1", "entity_1");
        assertFalse((Boolean) ftsSearchUpdated);
        verify(allCommonsRepository, times(0)).updateSearch("entity_1");
    }

    @Test
    public void testUpdateFTSSearchForIdField() {
        service = spy(service);
        Whitebox.setInternalState(service, "allCommonsRepositoryMap", allCommonsRepositoryMap);
        when(allCommonsRepositoryMap.get("bindtype1")).thenReturn(allCommonsRepository);
        FormSubmission formSubmission = mock(FormSubmission.class);
        FormData form = mock(FormData.class);
        when(formSubmission.getForm()).thenReturn(form);
        when(form.getBind_type()).thenReturn("bindtype1");
        FormField field = new FormField("id", "formid1", "bindtype1.id");
        List<FormField> fields = Collections.singletonList(field);
        when(form.fields()).thenReturn(fields);

        service.updateFTSsearch(formSubmission);
        verify(allCommonsRepository).updateSearch("formid1");

    }

    @Test
    public void testUpdateFTSSearchForInnerBindType() {
        service = spy(service);
        Whitebox.setInternalState(service, "allCommonsRepositoryMap", allCommonsRepositoryMap);
        when(allCommonsRepositoryMap.get("innerBindType")).thenReturn(allCommonsRepository);
        FormSubmission formSubmission = mock(FormSubmission.class);
        FormData form = mock(FormData.class);
        when(formSubmission.getForm()).thenReturn(form);
        when(form.getBind_type()).thenReturn("bindtype1");
        FormField field = new FormField("baseEntityid", "formid1", "innerBindType.id");
        List<FormField> fields = Collections.singletonList(field);
        when(form.fields()).thenReturn(fields);

        service.updateFTSsearch(formSubmission);
        verify(allCommonsRepository).updateSearch("formid1");

    }


    @Test
    public void testUpdateFTSSearchForSubForms() {
        String subFormBindType = "subBindType1";
        String subFormEntityId = "subEntityId1";
        Whitebox.setInternalState(service, "allCommonsRepositoryMap", allCommonsRepositoryMap);
        when(allCommonsRepositoryMap.get(subFormBindType)).thenReturn(allCommonsRepository);
        FormSubmission formSubmission = mock(FormSubmission.class);
        FormData form = mock(FormData.class);
        when(formSubmission.getForm()).thenReturn(form);
        when(form.getBind_type()).thenReturn("bindtype1");

        SubForm subForm = mock(SubForm.class);
        when(subForm.getBindType()).thenReturn(subFormBindType);
        Map<String, String> instance = new HashMap<>();
        instance.put("id", subFormEntityId);
        when(subForm.instances()).thenReturn(Collections.singletonList(instance));
        List<SubForm> subforms = Collections.singletonList(subForm);
        when(form.getSub_forms()).thenReturn(subforms);

        service.updateFTSsearch(formSubmission);
        verify(allCommonsRepository).updateSearch(subFormEntityId);

    }

}
