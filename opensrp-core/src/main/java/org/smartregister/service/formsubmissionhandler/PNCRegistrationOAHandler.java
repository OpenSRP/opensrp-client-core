package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.ChildService;

public class PNCRegistrationOAHandler implements FormSubmissionHandler {
    private ChildService childService;

    public PNCRegistrationOAHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.pncRegistrationOA(submission);
    }
}
