package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.MotherService;

public class PNCCloseHandler implements FormSubmissionHandler {
    private final MotherService motherService;

    public PNCCloseHandler(MotherService motherService) {
        this.motherService = motherService;
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.close(submission);
    }
}
