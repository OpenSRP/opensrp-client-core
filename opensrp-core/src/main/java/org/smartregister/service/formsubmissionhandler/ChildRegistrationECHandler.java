package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.ChildService;

public class ChildRegistrationECHandler implements FormSubmissionHandler {
    private final ChildService childService;

    public ChildRegistrationECHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.registerForEC(submission);
    }
}
