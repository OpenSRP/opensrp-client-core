package org.smartregister.service;

import com.google.gson.Gson;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.domain.form.FormData;
import org.smartregister.domain.form.FormField;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.domain.form.SubForm;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.FormDataRepository;

import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static org.smartregister.AllConstants.ENTITY_ID_PARAM;
import static org.smartregister.AllConstants.FORM_NAME_PARAM;
import static org.smartregister.AllConstants.INSTANCE_ID_PARAM;
import static org.smartregister.AllConstants.SYNC_STATUS;
import static org.smartregister.AllConstants.VERSION_PARAM;
import static org.smartregister.domain.SyncStatus.SYNCED;
import static org.smartregister.util.EasyMap.create;
import static org.smartregister.util.Log.logError;

public class FormSubmissionService {
    private ZiggyService ziggyService;
    private FormDataRepository formDataRepository;
    private AllSettings allSettings;
    private Map<String, AllCommonsRepository> allCommonsRepositoryMap;

    public FormSubmissionService(ZiggyService ziggyService, FormDataRepository
            formDataRepository, AllSettings allSettings) {
        this.ziggyService = ziggyService;
        this.formDataRepository = formDataRepository;
        this.allSettings = allSettings;
    }

    public FormSubmissionService(ZiggyService ziggyService, FormDataRepository
            formDataRepository, AllSettings allSettings, Map<String, AllCommonsRepository>
                                         allCommonsRepositoryMap) {
        this.ziggyService = ziggyService;
        this.formDataRepository = formDataRepository;
        this.allSettings = allSettings;
        this.allCommonsRepositoryMap = allCommonsRepositoryMap;
    }

    public void processSubmissions(List<FormSubmission> formSubmissions) {
        for (FormSubmission submission : formSubmissions) {
            if (!formDataRepository.submissionExists(submission.instanceId())) {
                try {
                    ziggyService.saveForm(getParams(submission), submission.instance());

                    // Update FTS Tables
                    updateFTSsearch(submission);

                } catch (Exception e) {
                    logError(format("Form submission processing failed, with instanceId: {0}. "
                                    + "Exception: {1}, StackTrace: {2}", submission.instanceId(),
                            e.getMessage(), ExceptionUtils.getStackTrace(e)));
                }
            }
            formDataRepository
                    .updateServerVersion(submission.instanceId(), submission.serverVersion());
            allSettings.savePreviousFormSyncIndex(submission.serverVersion());
        }
    }

    private String getParams(FormSubmission submission) {
        return new Gson().toJson(create(INSTANCE_ID_PARAM, submission.instanceId())
                .put(ENTITY_ID_PARAM, submission.entityId())
                .put(FORM_NAME_PARAM, submission.formName())
                .put(VERSION_PARAM, submission.version()).put(SYNC_STATUS, SYNCED.value()).map());
    }

    public void updateFTSsearch(FormSubmission formSubmission) {
        if (allCommonsRepositoryMap == null || allCommonsRepositoryMap.isEmpty()) {
            return;
        }

        FormData form = formSubmission.getForm();
        String bindType = form.getBind_type();
        for (FormField field : form.fields()) {
            if (field.name() != null && field.name().equals("id")) {
                String entityId = field.value();
                updateFTSsearch(bindType, entityId);
            }
        }

        List<FormField> fields = form.fields();

        if (fields != null && !fields.isEmpty()) {
            for (FormField field : fields) {
                String source = field.source();
                if (source != null && source.contains(".id")) {
                    String[] sourceArray = source.split("\\.");
                    String innerBindType = sourceArray[sourceArray.length - 2];
                    if (!bindType.equals(innerBindType)) {
                        String innerEntityId = field.value();
                        updateFTSsearch(innerBindType, innerEntityId);
                    }
                }
            }
        }

        List<SubForm> subForms = form.getSub_forms();
        if (subForms != null && !subForms.isEmpty()) {
            for (SubForm subForm : subForms) {
                String subBindType = subForm.getBindType();
                List<Map<String, String>> instances = subForm.instances();
                if (instances != null && !instances.isEmpty()) {
                    for (Map<String, String> instance : instances) {
                        String subEntityId = instance.get("id");
                        updateFTSsearch(subBindType, subEntityId);

                    }
                }
            }
        }
    }

    private boolean updateFTSsearch(String bindType, String entityId) {
        if (allCommonsRepositoryMap != null && !allCommonsRepositoryMap.isEmpty()) {
            AllCommonsRepository allCommonsRepository = allCommonsRepositoryMap.get(bindType);
            if (allCommonsRepository != null) {
                return allCommonsRepository.updateSearch(entityId);
            }
        }
        return false;
    }

}
