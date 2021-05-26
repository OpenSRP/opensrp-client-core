package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.MotherService;

public class ANCRegistrationOAHandler implements FormSubmissionHandler {
    private MotherService motherService;

    public ANCRegistrationOAHandler(MotherService motherService) {
        this.motherService = motherService;
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.registerOutOfAreaANC(submission);
    }
}
