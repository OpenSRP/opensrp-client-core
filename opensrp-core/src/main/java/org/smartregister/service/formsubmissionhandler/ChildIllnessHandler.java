package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.ChildService;

public class ChildIllnessHandler implements FormSubmissionHandler {
    private ChildService childService;

    public ChildIllnessHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.updateIllnessStatus(submission);
    }
}
