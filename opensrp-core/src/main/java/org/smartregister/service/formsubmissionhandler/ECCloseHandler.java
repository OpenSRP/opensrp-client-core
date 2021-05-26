package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.EligibleCoupleService;

public class ECCloseHandler implements FormSubmissionHandler {
    private EligibleCoupleService ecService;

    public ECCloseHandler(EligibleCoupleService ecService) {
        this.ecService = ecService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ecService.closeEligibleCouple(submission);
    }
}
