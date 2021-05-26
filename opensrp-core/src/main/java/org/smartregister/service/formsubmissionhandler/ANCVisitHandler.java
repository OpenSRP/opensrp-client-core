package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.MotherService;

public class ANCVisitHandler implements FormSubmissionHandler {
    private MotherService motherService;

    public ANCVisitHandler(MotherService motherService) {
        this.motherService = motherService;
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.ancVisit(submission);
    }
}
