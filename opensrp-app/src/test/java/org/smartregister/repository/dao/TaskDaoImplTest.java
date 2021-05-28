package org.smartregister.repository.dao;

import android.content.Intent;

import com.ibm.fhir.model.resource.QuestionnaireResponse;
import com.ibm.fhir.model.resource.Task;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.converters.EventConverter;
import org.smartregister.domain.Event;
import org.smartregister.repository.Repository;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.List;
import java.util.UUID;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.ibm.fhir.model.type.code.TaskStatus.READY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.AllConstants.INTENT_KEY.TASK_GENERATED;
import static org.smartregister.AllConstants.INTENT_KEY.TASK_GENERATED_EVENT;
import static org.smartregister.domain.Task.TaskStatus.ARCHIVED;
import static org.smartregister.domain.Task.TaskStatus.CANCELLED;
import static org.smartregister.repository.TaskRepositoryTest.getCursor;
import static org.smartregister.repository.TaskRepositoryTest.javaTimeFormater;

/**
 * Created by samuelgithengi on 9/3/20.
 */

public class TaskDaoImplTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private TaskDaoImpl taskDao;

    @Mock
    private Repository repository;
    @Mock
    private TaskNotesRepository taskNotesRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;


    @Before
    public void setUp() {

        taskDao = new TaskDaoImpl(taskNotesRepository);
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
    }


    @Test
    public void testFindTasksForEntity() {
        String query = "SELECT * FROM task WHERE plan_id=? AND for =? AND status  NOT IN (?,?)";
        String[] params = new String[]{"IRS_2018_S1", "location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", CANCELLED.name(), ARCHIVED.name()};
        when(sqLiteDatabase.rawQuery(query, params)).thenReturn(getCursor());

        List<Task> allTasks = taskDao.findTasksForEntity("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", "IRS_2018_S1");
        verify(sqLiteDatabase).rawQuery(query, params);

        assertEquals(1, allTasks.size());
        Task task = allTasks.iterator().next();

        assertEquals("tsk11231jh22", task.getIdentifier().get(0).getValue().getValue());
        assertEquals("2018_IRS-3734", task.getGroupIdentifier().getValue().getValue());
        assertEquals(READY, task.getStatus());
        assertEquals("Not Visited", task.getBusinessStatus().getText().getValue());
        assertEquals("IRS", task.getCode().getText().getValue());
        assertEquals("Spray House", task.getDescription().getValue());
        assertEquals("IRS Visit", task.getFocus().getReference().getValue());
        assertEquals("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", task.getFor().getReference().getValue());
        assertEquals("2018-11-10T2200", javaTimeFormater.format(task.getExecutionPeriod().getStart().getValue()));
        assertNull(task.getExecutionPeriod().getEnd());
        assertEquals("2018-10-31T0700", javaTimeFormater.format(task.getAuthoredOn().getValue()));
        assertEquals("2018-10-31T0700", javaTimeFormater.format(task.getLastModified().getValue()));
        assertEquals("demouser", task.getOwner().getReference().getValue());

    }

    @Test
    public void testFindTasksByJurisdictionAndPlanShouldReturnMatchingRecords() {
        String jurisdictionId = "jurisdiction-id";
        String planId = "plan-id";

        String query = "SELECT * FROM task WHERE group_id =? AND plan_id =?"; // ensure query exactly matches what is in TaskDaoImpl
        when(sqLiteDatabase.rawQuery(query, new String[]{jurisdictionId, planId})).thenReturn(getCursor());
        taskDao = Mockito.spy(taskDao);

        // Call the method under test
        List<Task> allTasks = taskDao.findTasksByJurisdiction(jurisdictionId, planId);

        // Perform verifications and assertions
        verify(taskDao).getTasksByJurisdictionAndPlan(jurisdictionId, planId);
        assertEquals(1, allTasks.size());
    }

    @Test
    public void testFindTasksByJurisdictionShouldReturnMatchingRecords() {
        String jurisdictionId = "jurisdiction-id";

        String query = "SELECT * FROM task WHERE group_id =?"; // ensure query exactly matches what is in TaskDaoImpl
        when(sqLiteDatabase.rawQuery(query, new String[]{jurisdictionId})).thenReturn(getCursor());
        taskDao = Mockito.spy(taskDao);

        // Call the method under test
        List<Task> allTasks = taskDao.findTasksByJurisdiction(jurisdictionId);

        // Perform verifications and assertions
        verify(taskDao).getTasksByJurisdiction(jurisdictionId);
        assertEquals(1, allTasks.size());
    }


    @Test
    public void testUpdateTaskShouldInvokeExpectedMethods() {
        taskDao = Mockito.spy(taskDao);

        LocalBroadcastManager localBroadcastManager = mock(LocalBroadcastManager.class);

        doReturn(false).when(localBroadcastManager).sendBroadcast(any(Intent.class));

        ReflectionHelpers.setStaticField(LocalBroadcastManager.class, "mInstance", localBroadcastManager);

        org.smartregister.domain.Task task = new org.smartregister.domain.Task();
        task.setIdentifier(UUID.randomUUID().toString());
        task.setPriority(org.smartregister.domain.Task.TaskPriority.ROUTINE);
        task.setStatus(ARCHIVED);

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);

        taskDao.updateTask(task);

        verify(taskDao).addOrUpdate(any(org.smartregister.domain.Task.class), eq(true));

        verify(localBroadcastManager).sendBroadcast(intentArgumentCaptor.capture());

        Intent intent = intentArgumentCaptor.getValue();

        assertNotNull(intent);

        assertEquals(intent.getAction(), TASK_GENERATED_EVENT);

        assertTrue(intent.hasExtra(TASK_GENERATED));

        ReflectionHelpers.setStaticField(LocalBroadcastManager.class, "mInstance", null);
    }

    @Test
    public void testSaveTaskShouldInvokeExpectedMethods() {
        taskDao = Mockito.spy(taskDao);

        LocalBroadcastManager localBroadcastManager = mock(LocalBroadcastManager.class);

        doReturn(false).when(localBroadcastManager).sendBroadcast(any(Intent.class));

        ReflectionHelpers.setStaticField(LocalBroadcastManager.class, "mInstance", localBroadcastManager);

        org.smartregister.domain.Task task = new org.smartregister.domain.Task();
        task.setIdentifier(UUID.randomUUID().toString());
        task.setPriority(org.smartregister.domain.Task.TaskPriority.ROUTINE);
        task.setStatus(ARCHIVED);

        String eventJson = "{\"baseEntityId\":\"69227a92-7979-490c-b149-f28669c6b760\",\"duration\":0,\"entityType\":\"product\",\"eventDate\":\"2021-01-20T00:00:00.000+0300\",\"eventType\":\"flag_problem\",\"formSubmissionId\":\"cfcdfaf1-9e78-49f0-ba68-da412830bf7d\",\"locationId\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"obs\":[{\"fieldCode\":\"flag_problem\",\"fieldDataType\":\"text\",\"fieldType\":\"formsubmissionField\",\"formSubmissionField\":\"flag_problem\",\"humanReadableValues\":[],\"keyValPairs\":{\"not_there\":\"Product is not there\"},\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"Product is not there\"]},{\"fieldCode\":\"not_there\",\"fieldDataType\":\"text\",\"fieldType\":\"formsubmissionField\",\"formSubmissionField\":\"not_there\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"never_received\"]},{\"fieldCode\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"start\",\"fieldType\":\"concept\",\"formSubmissionField\":\"start\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"2021-01-20 10:36:31\"]},{\"fieldCode\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"end\",\"fieldType\":\"concept\",\"formSubmissionField\":\"end\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"2021-01-20 10:36:36\"]},{\"fieldCode\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"deviceid\",\"fieldType\":\"concept\",\"formSubmissionField\":\"deviceid\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"358240051111110\"]},{\"fieldCode\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"subscriberid\",\"fieldType\":\"concept\",\"formSubmissionField\":\"subscriberid\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"310260000000000\"]},{\"fieldCode\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"simserial\",\"fieldType\":\"concept\",\"formSubmissionField\":\"simserial\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"89014103211118510720\"]},{\"fieldCode\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"fieldDataType\":\"phonenumber\",\"fieldType\":\"concept\",\"formSubmissionField\":\"phonenumber\",\"humanReadableValues\":[],\"parentCode\":\"\",\"saveObsAsArray\":false,\"values\":[\"+15555215554\"]}],\"providerId\":\"demo\",\"team\":\"Commune A Team\",\"teamId\":\"abf1be43-32da-4848-9b50-630fb89ec0ef\",\"version\":1611128196841,\"clientApplicationVersion\":1,\"clientApplicationVersionName\":\"0.0.3-v2-EUSM-SNAPSHOT\",\"dateCreated\":\"2021-01-20T10:36:36.841+0300\",\"type\":\"Event\",\"details\":{\"mission\":\"SS\",\"locationName\":\"Ambatoharanana\",\"productId\":\"2\",\"locationId\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"taskIdentifier\":\"6c303b8b-e47c-45e9-8ab5-3374c8f539a3\",\"location_id\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"productName\":\"Scale\",\"planIdentifier\":\"335ef7a3-7f35-58aa-8263-4419464946d8\",\"appVersionName\":\"2.0.1-SNAPSHOT\",\"formVersion\":\"0.0.1\"}}";

        Event event = JsonFormUtils.gson.fromJson(eventJson, Event.class);
        QuestionnaireResponse eventQuestionnaire = EventConverter.convertEventToEncounterResource(event);

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);

        ArgumentCaptor<org.smartregister.domain.Task> taskArgumentCaptor = ArgumentCaptor.forClass(org.smartregister.domain.Task.class);

        taskDao.saveTask(task, eventQuestionnaire);

        verify(taskDao).addOrUpdate(taskArgumentCaptor.capture());

        verify(localBroadcastManager).sendBroadcast(intentArgumentCaptor.capture());

        org.smartregister.domain.Task resultTask = taskArgumentCaptor.getValue();

        assertNotNull(resultTask);

        assertEquals("b8a7998c-5df6-49eb-98e6-f0675db71848", resultTask.getStructureId());

        ReflectionHelpers.setStaticField(LocalBroadcastManager.class, "mInstance", null);

    }
}
