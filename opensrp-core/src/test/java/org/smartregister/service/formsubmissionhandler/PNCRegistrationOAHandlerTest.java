package org.smartregister.service.formsubmissionhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.ChildService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.util.FormSubmissionBuilder.create;

public class PNCRegistrationOAHandlerTest extends BaseUnitTest {
    @Mock
    private ChildService childService;

    private PNCRegistrationOAHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new PNCRegistrationOAHandler(childService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToChildService() throws Exception {
        FormSubmission submission = create().withFormName("pnc_registration_oa").withInstanceId("instance id 1").withVersion("122").build();

        handler.handle(submission);

        verify(childService).pncRegistrationOA(submission);
    }
}
