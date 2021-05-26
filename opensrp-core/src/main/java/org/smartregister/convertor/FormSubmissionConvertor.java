package org.smartregister.convertor;

import org.ei.drishti.dto.form.FormSubmissionDTO;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.domain.SyncStatus.SYNCED;

public class FormSubmissionConvertor {
    public static List<org.smartregister.domain.form.FormSubmission> toDomain
            (List<FormSubmissionDTO> formSubmissionsDto) {
        List<org.smartregister.domain.form.FormSubmission> submissions = new ArrayList<org
                .smartregister.domain.form.FormSubmission>();
        for (FormSubmissionDTO formSubmission : formSubmissionsDto) {
            submissions.add(new org.smartregister.domain.form.FormSubmission(
                    formSubmission.instanceId(), formSubmission.entityId(),
                    formSubmission.formName(), formSubmission.instance(),
                    formSubmission.clientVersion(), SYNCED,
                    formSubmission.formDataDefinitionVersion(), formSubmission.serverVersion()));
        }
        return submissions;
    }
}
