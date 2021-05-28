package org.smartregister.sync.helper;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Event;
import org.smartregister.domain.Task;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by Richard Kareko on 2/5/21.
 */

public class TaskServiceProcessor {

    protected static TaskServiceProcessor instance;
    private TaskRepository taskRepository;
    private EventClientRepository eventClientRepository;

    public static TaskServiceProcessor getInstance() {
        if (instance == null) {
            instance = new TaskServiceProcessor(CoreLibrary.getInstance().context().getTaskRepository(),
                    CoreLibrary.getInstance().context().getEventClientRepository() );
        }
        return instance;
    }

    public  TaskServiceProcessor(TaskRepository taskRepository, EventClientRepository eventClientRepository) {
        this.taskRepository = taskRepository;
        this.eventClientRepository = eventClientRepository;
    }

    public void processDuplicateTasks() {
        List<EventClient> eventsToProcess = new ArrayList<>();
        List<String> entityIds = taskRepository.getEntityIdsWithDuplicateTasks();
        for (String entityId: entityIds) {
            processTasks(taskRepository.getDuplicateTasksForEntity(entityId), eventsToProcess);
        }
        //trigger client processing for updated events
        try {
            DrishtiApplication.getInstance().getClientProcessor().processClient(eventsToProcess);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    protected void processTasks(Set<Task> duplicateTasks, List<EventClient> eventsToProcess) {
        if ((duplicateTasks == null) || (duplicateTasks.size()<2)) {
            return;
        }
        Task localTask;
        Task serverTask;
        List<Task> duplicateTaskList = new ArrayList<>(duplicateTasks);

        if (duplicateTaskList.get(0).getServerVersion() > duplicateTaskList.get(1).getServerVersion()){
            serverTask = duplicateTaskList.get(0);
            localTask = duplicateTaskList.get(1);
        } else {
            serverTask = duplicateTaskList.get(1);
            localTask = duplicateTaskList.get(0);
        }

        if (Task.TaskStatus.READY.equals(localTask.getStatus())){
            // delete local task
            taskRepository.deleteTasksByIds(Collections.singletonList(localTask.getIdentifier()));
            return;
        }

        populateTaskDetails(serverTask, localTask);

        // Fetch event & update taskIdentifier value
        List<Event> localEvents = eventClientRepository.getEventsByTaskIds(Collections.singleton(localTask.getIdentifier()));
        if (localEvents != null && !localEvents.isEmpty()) {
            Event localEvent = localEvents.get(0);
            localEvent.getDetails().put(AllConstants.TASK_IDENTIFIER, serverTask.getIdentifier());
            JSONObject localEventJSON = eventClientRepository.convertToJson(localEvent);
            eventClientRepository.addEvent(localEvent.getBaseEntityId(), localEventJSON,BaseRepository.TYPE_Unsynced);
            eventsToProcess.add(new EventClient(localEvent));
        }

        // delete local task
        taskRepository.deleteTasksByIds(Collections.singletonList(localTask.getIdentifier()));
        taskRepository.addOrUpdate(serverTask,true);

    }

    protected void populateTaskDetails(Task serverTask, Task localTask) {

        serverTask.setStatus(localTask.getStatus());
        serverTask.setBusinessStatus(localTask.getBusinessStatus());
        serverTask.setOwner(localTask.getOwner());
        serverTask.setRestriction(localTask.getRestriction());
        serverTask.setNotes(localTask.getNotes());
        serverTask.setSyncStatus(BaseRepository.TYPE_Unsynced);

    }

}
