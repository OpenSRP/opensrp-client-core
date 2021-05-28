package org.smartregister.sync.helper;

import com.google.gson.reflect.TypeToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.domain.Task;
import org.smartregister.domain.TaskUpdate;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.service.HTTPAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.smartregister.CoreLibrary.getInstance;
import static org.smartregister.repository.AllSharedPreferences.ANM_IDENTIFIER_PREFERENCE_KEY;
import static org.smartregister.sync.helper.TaskServiceHelper.TASK_LAST_SYNC_DATE;

/**
 * Created by Richard Kareko on 6/23/20.
 */

public class TaskServiceHelperTest extends BaseRobolectricUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PlanDefinitionRepository planDefinitionRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private HTTPAgent httpAgent;

    @Mock
    private DristhiConfiguration configuration;

    @Mock
    private TaskServiceProcessor taskServiceProcessor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    private TaskServiceHelper taskServiceHelper;


    private final String taskJSon = "{\"for\": \"154167\", \"code\": \"Bednet Distribution\", \"focus\": \"158b73f5-49d0-50a9-8020-0468c1bbabdd\", \"owner\": \"nifiUser\", \"status\": \"Cancelled\", \"priority\": \"routine\", \"authoredOn\": \"2020-03-26T10:47:03.586+02:00\", \"identifier\": \"c256c9d8-fe9b-4763-b5af-26585dcbe6bf\", \"description\": \"Visit 100% of residential structures in the operational area and provide nets\", \"lastModified\": \"2020-03-26T10:52:09.750+02:00\", \"serverVersion\": 1585212830433, \"businessStatus\": \"Not Visited\", \"planIdentifier\": \"eb3cd7e1-c849-5230-8d49-943218018f9f\", \"groupIdentifier\": \"3952\", \"executionPeriod\":{\"end\": \"2020-04-02T00:00:00.000+02:00\", \"start\": \"2020-03-26T00:00:00.000+02:00\"}}";

    private final String planId = "eb3cd7e1-c849-5230-8d49-943218018f9f";

    @Before
    public void setUp() {
        Whitebox.setInternalState(getInstance().context(), "planDefinitionRepository", planDefinitionRepository);
        Whitebox.setInternalState(getInstance().context(), "locationRepository", locationRepository);
        Whitebox.setInternalState(getInstance().context(), "httpAgent", httpAgent);
        Whitebox.setInternalState(getInstance().context(), "configuration", configuration);
        when(configuration.dristhiBaseURL()).thenReturn("https://sample-stage.smartregister.org/opensrp");
        getInstance().context().allSharedPreferences().getPreferences().edit().clear().apply();
        getInstance().context().allSharedPreferences().savePreference(ANM_IDENTIFIER_PREFERENCE_KEY, "onatest");
        taskServiceHelper = new TaskServiceHelper(taskRepository);

    }

    @After
    public void tearDown() {
        initCoreLibrary();
    }

    @Test
    public void testSyncTasks() {
        taskServiceHelper = spy(taskServiceHelper);
        Whitebox.setInternalState(taskServiceHelper, "taskServiceProcessor", taskServiceProcessor);
        taskServiceHelper.syncTasks();
        verify(taskServiceProcessor).processDuplicateTasks();
        verify(taskServiceHelper).syncCreatedTaskToServer();
        verify(taskServiceHelper).syncTaskStatusToServer();
    }

    @Test
    public void testFetchTasksFromServerSyncByGroupIdentifier() {
        Set<String> planIdSet = new HashSet<>();
        planIdSet.add(planId);
        when(getInstance().context().getPlanDefinitionRepository().findAllPlanDefinitionIds()).thenReturn(planIdSet);

        String locationId = "3952";
        List<String> locationIdList = new ArrayList<>();
        locationIdList.add(locationId);
        when(getInstance().context().getLocationRepository().getAllLocationIds()).thenReturn(locationIdList);

        //reset task last sync date to zero since this is updated by other tests
        getInstance().context().allSharedPreferences().savePreference(TASK_LAST_SYNC_DATE, "0");

        Task expectedTask = TaskServiceHelper.taskGson.fromJson(taskJSon, new TypeToken<Task>() {
        }.getType());
        expectedTask.setSyncStatus(BaseRepository.TYPE_Unsynced);
        List<Task> tasks = Collections.singletonList(expectedTask);

        Mockito.doReturn(new Response<>(ResponseStatus.success,    // returned on first call
                        TaskServiceHelper.taskGson.toJson(tasks)).withTotalRecords(1L),
                new Response<>(ResponseStatus.success,             //returned on second call
                        TaskServiceHelper.taskGson.toJson(new ArrayList<>())).withTotalRecords(0l))
                .when(httpAgent).post(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        List<Task> actualTasks = taskServiceHelper.fetchTasksFromServer();
        assertNotNull(actualTasks);
        assertEquals(1, actualTasks.size());
        Task actualTask = actualTasks.get(0);

        String syncUrl = stringArgumentCaptor.getAllValues().get(0);
        assertEquals("https://sample-stage.smartregister.org/opensrp/rest/v2/task/sync", syncUrl);
        String requestString = stringArgumentCaptor.getAllValues().get(1);
        assertEquals("{\"plan\":[\"eb3cd7e1-c849-5230-8d49-943218018f9f\"],\"group\":[\"3952\"],\"serverVersion\":0,\"return_count\":true}", requestString);

        verifyTaskInformationFetchedFromServer(expectedTask, actualTask);

    }

    @Test
    public void testFetchTasksFromServerSyncByOwner() {
        Set<String> planIdSet = new HashSet<>();
        planIdSet.add(planId);
        when(getInstance().context().getPlanDefinitionRepository().findAllPlanDefinitionIds()).thenReturn(planIdSet);

        String locationId = "3952";
        List<String> locationIdList = new ArrayList<>();
        locationIdList.add(locationId);
        when(getInstance().context().getLocationRepository().getAllLocationIds()).thenReturn(locationIdList);

        Task expectedTask = TaskServiceHelper.taskGson.fromJson(taskJSon, new TypeToken<Task>() {
        }.getType());
        expectedTask.setSyncStatus(BaseRepository.TYPE_Unsynced);
        List<Task> tasks = Collections.singletonList(expectedTask);

        //reset task last sync date to zero since this is updated by other tests
        getInstance().context().allSharedPreferences().savePreference(TASK_LAST_SYNC_DATE, "0");

        Mockito.doReturn(new Response<>(ResponseStatus.success,    // returned on first call
                        TaskServiceHelper.taskGson.toJson(tasks)).withTotalRecords(1L),
                new Response<>(ResponseStatus.success,             //returned on second call
                        TaskServiceHelper.taskGson.toJson(new ArrayList<>())).withTotalRecords(0l))
                .when(httpAgent).post(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        taskServiceHelper.setSyncByGroupIdentifier(false);

        List<Task> actualTasks = taskServiceHelper.fetchTasksFromServer();
        assertNotNull(actualTasks);
        assertEquals(1, actualTasks.size());
        Task actualTask = actualTasks.get(0);

        String syncUrl = stringArgumentCaptor.getAllValues().get(0);
        assertEquals("https://sample-stage.smartregister.org/opensrp/rest/v2/task/sync", syncUrl);
        String requestString = stringArgumentCaptor.getAllValues().get(1);
        assertEquals("{\"plan\":[\"eb3cd7e1-c849-5230-8d49-943218018f9f\"],\"owner\":\"onatest\",\"serverVersion\":0,\"return_count\":true}", requestString);
        verifyTaskInformationFetchedFromServer(expectedTask, actualTask);
    }

    private void verifyTaskInformationFetchedFromServer(Task expectedTask, Task actualTask) {
        verify(taskRepository).addOrUpdate(taskArgumentCaptor.capture());
        assertEquals(expectedTask.getIdentifier(), taskArgumentCaptor.getValue().getIdentifier());
        assertEquals(expectedTask.getBusinessStatus(), taskArgumentCaptor.getValue().getBusinessStatus());
        assertEquals(BaseRepository.TYPE_Synced, taskArgumentCaptor.getValue().getSyncStatus());
        assertEquals(expectedTask.getServerVersion(), taskArgumentCaptor.getValue().getServerVersion());
        assertEquals(expectedTask.getCode(), taskArgumentCaptor.getValue().getCode());
        assertEquals(expectedTask.getForEntity(), taskArgumentCaptor.getValue().getForEntity());
        assertEquals(expectedTask.getPlanIdentifier(), taskArgumentCaptor.getValue().getPlanIdentifier());

        assertEquals(expectedTask.getIdentifier(), actualTask.getIdentifier());
        assertEquals(expectedTask.getBusinessStatus(), actualTask.getBusinessStatus());
        assertEquals(BaseRepository.TYPE_Synced, actualTask.getSyncStatus());
        assertEquals(expectedTask.getServerVersion(), actualTask.getServerVersion());
        assertEquals(expectedTask.getCode(), actualTask.getCode());
        assertEquals(expectedTask.getForEntity(), actualTask.getForEntity());
        assertEquals(expectedTask.getPlanIdentifier(), actualTask.getPlanIdentifier());
    }

    @Test
    public void testSyncTaskStatusToServer() {

        TaskUpdate taskUpdate = new TaskUpdate();
        taskUpdate.setIdentifier(planId);
        taskUpdate.setBusinessStatus("Not Visited");
        taskUpdate.setStatus("Cancelled");

        when(taskRepository.getUnSyncedTaskStatus()).thenReturn(Collections.singletonList(taskUpdate));

        Mockito.doReturn(new Response<>(ResponseStatus.success,
                "{task_ids : [\"eb3cd7e1-c849-5230-8d49-943218018f9f\"]}"))
                .when(httpAgent).postWithJsonResponse(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        taskServiceHelper.syncTaskStatusToServer();

        String syncUrl = stringArgumentCaptor.getAllValues().get(0);
        assertEquals("https://sample-stage.smartregister.org/opensrp//rest/v2/task/update_status", syncUrl);
        String requestString = stringArgumentCaptor.getAllValues().get(1);
        assertEquals("[{\"identifier\":\"eb3cd7e1-c849-5230-8d49-943218018f9f\",\"status\":\"Cancelled\",\"businessStatus\":\"Not Visited\"}]", requestString);
        verify(taskRepository).markTaskAsSynced(taskUpdate.getIdentifier());
    }

    @Test
    public void testSynCreatedTaskToServerSuccessfully() {
        Task expectedTask = TaskServiceHelper.taskGson.fromJson(taskJSon, new TypeToken<Task>() {
        }.getType());
        expectedTask.setSyncStatus(BaseRepository.TYPE_Created);
        List<Task> tasks = Collections.singletonList(expectedTask);
        String expectedJsonPayload = TaskServiceHelper.taskGson.toJson(tasks);

        when(taskRepository.getAllUnsynchedCreatedTasks()).thenReturn(tasks);

        Mockito.doReturn(new Response<>(ResponseStatus.success,
                "{task_ids : [\"c256c9d8-fe9b-4763-b5af-26585dcbe6bf\"]}"))
                .when(httpAgent).postWithJsonResponse(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        taskServiceHelper.syncCreatedTaskToServer();

        String syncUrl = stringArgumentCaptor.getAllValues().get(0);
        assertEquals("https://sample-stage.smartregister.org/opensrp//rest/v2/task/add", syncUrl);
        String requestString = stringArgumentCaptor.getAllValues().get(1);
        assertEquals(expectedJsonPayload, requestString);
        verify(taskRepository).getAllUnsynchedCreatedTasks();
        verify(taskRepository).markTaskAsSynced(expectedTask.getIdentifier());

    }

    @Test
    public void testSynCreatedTaskToServerWithFailure() {
        Task expectedTask = TaskServiceHelper.taskGson.fromJson(taskJSon, new TypeToken<Task>() {
        }.getType());
        expectedTask.setSyncStatus(BaseRepository.TYPE_Created);
        List<Task> tasks = Collections.singletonList(expectedTask);
        String expectedJsonPayload = TaskServiceHelper.taskGson.toJson(tasks);

        when(taskRepository.getAllUnsynchedCreatedTasks()).thenReturn(tasks);

        Mockito.doReturn(new Response<>(ResponseStatus.failure,
                ""))
                .when(httpAgent).postWithJsonResponse(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        taskServiceHelper.syncCreatedTaskToServer();

        String syncUrl = stringArgumentCaptor.getAllValues().get(0);
        assertEquals("https://sample-stage.smartregister.org/opensrp//rest/v2/task/add", syncUrl);
        String requestString = stringArgumentCaptor.getAllValues().get(1);
        assertEquals(expectedJsonPayload, requestString);
        verify(taskRepository).getAllUnsynchedCreatedTasks();
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    public void testSynCreatedTaskToServerWithTasksNotProcessedResponse() {
        Task expectedTask = TaskServiceHelper.taskGson.fromJson(taskJSon, new TypeToken<Task>() {
        }.getType());
        expectedTask.setSyncStatus(BaseRepository.TYPE_Created);
        List<Task> tasks = Collections.singletonList(expectedTask);
        String expectedJsonPayload = TaskServiceHelper.taskGson.toJson(tasks);

        when(taskRepository.getAllUnsynchedCreatedTasks()).thenReturn(tasks);

        Mockito.doReturn(new Response<>(ResponseStatus.success,
                "Tasks with identifiers not processed: c256c9d8-fe9b-4763-b5af-26585dcbe6bf"))
                .when(httpAgent).postWithJsonResponse(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        taskServiceHelper.syncCreatedTaskToServer();

        String syncUrl = stringArgumentCaptor.getAllValues().get(0);
        assertEquals("https://sample-stage.smartregister.org/opensrp//rest/v2/task/add", syncUrl);
        String requestString = stringArgumentCaptor.getAllValues().get(1);
        assertEquals(expectedJsonPayload, requestString);
        verify(taskRepository).getAllUnsynchedCreatedTasks();
        verifyNoMoreInteractions(taskRepository);

    }

}
