package org.smartregister.service.formsubmissionhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.service.ChildService;
import org.smartregister.service.MotherService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.util.FormSubmissionBuilder.create;

public class DeliveryOutcomeHandlerTest extends BaseUnitTest {
    @Mock
    private MotherService motherService;
    @Mock
    private ChildService childService;

    private DeliveryOutcomeHandler handler;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        handler = new DeliveryOutcomeHandler(motherService, childService);
    }

    @Test
    public void shouldDelegateFormSubmissionHandlingToMotherService() throws Exception {
        FormSubmission submission = create().withFormName("delivery_outcome").withInstanceId("instance id 1").withVersion("122").build();

        handler.handle(submission);

        verify(motherService).deliveryOutcome(submission);
        verify(childService).register(submission);
    }
}
