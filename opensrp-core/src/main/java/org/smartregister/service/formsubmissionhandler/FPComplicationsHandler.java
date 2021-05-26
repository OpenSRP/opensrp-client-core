package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.EligibleCoupleService;

public class FPComplicationsHandler implements FormSubmissionHandler {
    private EligibleCoupleService ecService;

    public FPComplicationsHandler(EligibleCoupleService ecService) {
        this.ecService = ecService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ecService.fpComplications(submission);
    }
}
