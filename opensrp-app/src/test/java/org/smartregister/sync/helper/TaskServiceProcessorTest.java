package org.smartregister.sync.helper;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.domain.Event;
import org.smartregister.domain.Task;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.CoreLibrary.getInstance;

/**
 * Created by Richard Kareko on 2/11/21.
 */

public class TaskServiceProcessorTest extends BaseRobolectricUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EventClientRepository eventClientRepository;

    @Captor
    private ArgumentCaptor<List<String>> listArgumentCaptor;

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    @Captor
    private ArgumentCaptor<Boolean> booleanArgumentCaptor;

    @Captor
    private ArgumentCaptor<JSONObject> jsonObjectArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private TaskServiceProcessor taskServiceProcessor;

    @Before
    public void setUp() {
        Whitebox.setInternalState(getInstance().context(), "taskRepository", taskRepository);
        Whitebox.setInternalState(getInstance().context(), "eventClientRepository", eventClientRepository);
        taskServiceProcessor = new TaskServiceProcessor(taskRepository,eventClientRepository);
    }

    @After
    public void tearDown() {
        initCoreLibrary();
    }

    @Test
    public void testProcessDuplicateTasks() {
        taskServiceProcessor = spy(taskServiceProcessor);
        List<EventClient> eventsToProcess = new ArrayList<>();
        List<String> entityIds = Collections.singletonList("task-id-1");
        Task task = new Task();
        task.setIdentifier("task-id-1");
        Set<Task> duplicateTasks = Collections.singleton(task);
        when(taskRepository.getEntityIdsWithDuplicateTasks()).thenReturn(entityIds);
        when(taskRepository.getDuplicateTasksForEntity(entityIds.get(0))).thenReturn(duplicateTasks);
        taskServiceProcessor.processDuplicateTasks();
        verify(taskServiceProcessor).processTasks(duplicateTasks, eventsToProcess);
    }

    @Test
    public void testProcessTasks() throws JSONException {
        taskServiceProcessor = spy(taskServiceProcessor);
        List<EventClient> eventsToProcess = new ArrayList<>();
        Task localTask = new Task();
        localTask.setIdentifier("task-id-1");
        localTask.setStatus(Task.TaskStatus.COMPLETED);
        localTask.setOwner("Onadev");
        localTask.setRestriction(new Task.Restriction());
        localTask.setNotes(new ArrayList<>());
        localTask.setSyncStatus(BaseRepository.TYPE_Created);
        localTask.setServerVersion(0l);

        Task serverTask = new Task();
        serverTask.setServerVersion(2l);

        Set<Task> duplicateTasks = new HashSet<>();
        duplicateTasks.add(localTask);
        duplicateTasks.add(serverTask);

        List<Event> localEvents = new ArrayList<>();
        Event event = new Event();
        event.setEventId("event-id-1");
        event.setBaseEntityId("base-entity-id-1");
        event.setDetails(new HashMap<>());
        localEvents.add(event);
        when(eventClientRepository.getEventsByTaskIds(Collections.singleton(localTask.getIdentifier()))).thenReturn(localEvents);
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
        when(eventClientRepository.convertToJson(event)).thenReturn(eventJson);

        taskServiceProcessor.processTasks(duplicateTasks, eventsToProcess);

        verify(taskServiceProcessor).populateTaskDetails(serverTask,localTask);
        verify(eventClientRepository).convertToJson(event);
        verify(eventClientRepository).addEvent(stringArgumentCaptor.capture(), jsonObjectArgumentCaptor.capture(), stringArgumentCaptor.capture());
        assertEquals(event.getBaseEntityId(), stringArgumentCaptor.getAllValues().get(0));
        assertEquals(BaseRepository.TYPE_Unsynced, stringArgumentCaptor.getAllValues().get(1));
        assertEquals(eventJson, jsonObjectArgumentCaptor.getValue());

        verify(taskRepository).deleteTasksByIds(listArgumentCaptor.capture());
        assertEquals(localTask.getIdentifier(), listArgumentCaptor.getValue().get(0));
        verify(taskRepository).addOrUpdate(taskArgumentCaptor.capture(), booleanArgumentCaptor.capture());
        assertTrue(booleanArgumentCaptor.getValue());
        Task actualServerTask = taskArgumentCaptor.getValue();
        assertEquals(localTask.getStatus(), actualServerTask.getStatus());
        assertEquals(localTask.getBusinessStatus(), actualServerTask.getBusinessStatus());
        assertEquals(localTask.getOwner(), actualServerTask.getOwner());
        assertEquals(localTask.getRestriction(), actualServerTask.getRestriction());
        assertEquals(localTask.getNotes(), actualServerTask.getNotes());
        assertEquals(BaseRepository.TYPE_Unsynced, actualServerTask.getSyncStatus());

    }

}
