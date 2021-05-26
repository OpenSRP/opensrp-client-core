package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.MotherService;

public class ANCRegistrationHandler implements FormSubmissionHandler {
    private MotherService motherService;

    public ANCRegistrationHandler(MotherService motherService) {
        this.motherService = motherService;
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.registerANC(submission);
    }
}
