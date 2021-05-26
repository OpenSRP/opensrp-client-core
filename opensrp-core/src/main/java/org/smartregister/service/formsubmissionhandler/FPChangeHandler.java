package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.EligibleCoupleService;

public class FPChangeHandler implements FormSubmissionHandler {
    private EligibleCoupleService ecService;

    public FPChangeHandler(EligibleCoupleService ecService) {
        this.ecService = ecService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ecService.fpChange(submission);
    }
}
