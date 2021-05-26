package org.smartregister.repository.dao;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ibm.fhir.model.resource.QuestionnaireResponse;
import com.ibm.fhir.model.resource.Task;
import com.ibm.fhir.path.FHIRPathElementNode;

import org.smartregister.CoreLibrary;
import org.smartregister.converters.TaskConverter;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.pathevaluator.dao.TaskDao;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.repository.TaskRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.smartregister.AllConstants.INTENT_KEY.TASK_GENERATED;
import static org.smartregister.AllConstants.INTENT_KEY.TASK_GENERATED_EVENT;

/**
 * Created by samuelgithengi on 9/3/20.
 */
public class TaskDaoImpl extends TaskRepository implements TaskDao {

    private static final String GET_STRUCTURE_FROM_QUESTIONNAIRE = "$this.item.where(definition='details' and linkId='location_id').answer";

    public TaskDaoImpl(TaskNotesRepository taskNotesRepository) {
        super(taskNotesRepository);
    }

    @Override
    public List<Task> findTasksForEntity(String id, String planIdentifier) {
        return getTasksByPlanAndEntity(planIdentifier, id)
                .stream()
                .map(TaskConverter::convertTasktoFihrResource)
                .collect(Collectors.toList());
    }

    @Override
    public void saveTask(org.smartregister.domain.Task task, QuestionnaireResponse questionnaireResponse) {
        if (questionnaireResponse != null) {
            FHIRPathElementNode structure = PathEvaluatorLibrary.getInstance()
                    .evaluateElementExpression(questionnaireResponse,
                            GET_STRUCTURE_FROM_QUESTIONNAIRE);
            if (structure != null) {
                String structureId = structure.element().as(QuestionnaireResponse.Item.Answer.class).getValue().as(com.ibm.fhir.model.type.String.class).getValue();
                task.setStructureId(structureId);
            } else {
                task.setStructureId(task.getForEntity());
            }
        }
        task.setSyncStatus(BaseRepository.TYPE_Created);
        addOrUpdate(task);
        sendBroadcast(task);
    }

    private void sendBroadcast(org.smartregister.domain.Task task) {
        Intent taskGeneratedIntent = new Intent(TASK_GENERATED_EVENT);
        taskGeneratedIntent.putExtra(TASK_GENERATED, task);
        LocalBroadcastManager.getInstance(CoreLibrary.getInstance().context().applicationContext()).sendBroadcast(taskGeneratedIntent);
    }

    @Override
    public boolean checkIfTaskExists(String baseEntityId, String jurisdiction, String planIdentifier, String code) {
        return !getTasksByEntityAndCode(planIdentifier, jurisdiction, baseEntityId, code).isEmpty();
    }

    @Override
    public List<Task> findAllTasksForEntity(String entityId) {
        return getTasksByEntity(entityId)
                .stream()
                .map(TaskConverter::convertTasktoFihrResource)
                .collect(Collectors.toList());
    }

    @Override
    public org.smartregister.domain.Task updateTask(org.smartregister.domain.Task task) {
        task.setSyncStatus(BaseRepository.TYPE_Created);
        addOrUpdate(task, true);
        sendBroadcast(task);
        return task;
    }


    @Override
    public List<Task> findTasksByJurisdiction(String jurisdiction, String planIdentifier) {
        return convertToListOfFHIRTasks(getTasksByJurisdictionAndPlan(jurisdiction, planIdentifier));
    }

    @Override
    public List<Task> findTasksByJurisdiction(String jurisdiction) {
        return convertToListOfFHIRTasks(getTasksByJurisdiction(jurisdiction));

    }

    private List<Task> convertToListOfFHIRTasks(Set<org.smartregister.domain.Task> tasks) {
        return tasks.stream()
                .map(TaskConverter::convertTasktoFihrResource)
                .collect(Collectors.toList());
    }
}
