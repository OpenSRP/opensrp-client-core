package org.smartregister.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.repository.FormDataRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PendingFormSubmissionServiceTest {
    @Mock
    private FormDataRepository repository;
    private PendingFormSubmissionService pendingFormSubmissionService;

    @Before
    public void setUp() {
        initMocks(this);
        pendingFormSubmissionService = new PendingFormSubmissionService(repository);
    }

    @Test
    public void shouldFetchPendingFormSubmissionCount() {
        pendingFormSubmissionService.pendingFormSubmissionCount();

        verify(repository).getPendingFormSubmissionsCount();
    }
}
