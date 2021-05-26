package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.ChildService;

public class VitaminAHandler implements FormSubmissionHandler {
    private final ChildService childService;

    public VitaminAHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.updateVitaminAProvided(submission);
    }
}
