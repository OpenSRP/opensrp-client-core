package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.ChildService;

public class ChildCloseHandler implements FormSubmissionHandler {
    private final ChildService childService;

    public ChildCloseHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.close(submission);
    }
}
