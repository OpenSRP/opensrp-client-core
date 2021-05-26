package org.smartregister.service.formsubmissionhandler;

import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.EligibleCoupleService;

public class RenewFPProductHandler implements FormSubmissionHandler {
    private EligibleCoupleService ecService;

    public RenewFPProductHandler(EligibleCoupleService ecService) {
        this.ecService = ecService;
    }

    @Override
    public void handle(FormSubmission submission) {
        ecService.renewFPProduct(submission);
    }
}
