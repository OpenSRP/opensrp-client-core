package org.smartregister.service;

import org.smartregister.repository.FormDataRepository;

public class PendingFormSubmissionService {

    private FormDataRepository formDataRepository;

    public PendingFormSubmissionService(FormDataRepository formDataRepository) {
        this.formDataRepository = formDataRepository;
    }

    public long pendingFormSubmissionCount() {
        return formDataRepository.getPendingFormSubmissionsCount();
    }
}