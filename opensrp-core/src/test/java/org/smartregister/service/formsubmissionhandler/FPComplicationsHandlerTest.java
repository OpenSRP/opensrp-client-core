package org.smartregister.service.formsubmissionhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.EligibleCoupleService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.util.FormSubmissionBuilder.create;

public class FPComplicationsHandlerTest extends BaseUnitTest {
    @Mock
    private EligibleCoupleService ecService;

    private FPComplicationsHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new FPComplicationsHandler(ecService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToECService() throws Exception {
        FormSubmission submission = create().withFormName("ec_registration").withInstanceId("instance id 1").withVersion("122").build();

        handler.handle(submission);

        verify(ecService).fpComplications(submission);
    }

}
