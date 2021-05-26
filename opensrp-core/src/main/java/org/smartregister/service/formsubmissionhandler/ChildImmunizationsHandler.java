package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.ChildService;

public class ChildImmunizationsHandler implements FormSubmissionHandler {
    private ChildService childService;

    public ChildImmunizationsHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.updateImmunizations(submission);
    }
}
