package org.smartregister.service.formsubmissionhandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.event.Listener;
import org.smartregister.repository.FormDataRepository;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.smartregister.event.Event.FORM_SUBMITTED;
import static org.smartregister.util.FormSubmissionBuilder.create;

public class FormSubmissionRouterTest extends BaseUnitTest {
    @Mock
    private FormDataRepository formDataRepository;
    @Mock
    private ECRegistrationHandler ecRegistrationHandler;
    @Mock
    private FPComplicationsHandler fpComplicationsHandler;
    @Mock
    private FPChangeHandler fpChangeHandler;
    @Mock
    private RenewFPProductHandler renewFPProductHandler;
    @Mock
    private ECCloseHandler ecCloseHandler;
    @Mock
    private ANCCloseHandler ancCloseHandler;
    @Mock
    private ANCRegistrationHandler ancRegistrationHandler;
    @Mock
    private ANCRegistrationOAHandler ancRegistrationOAHandler;
    @Mock
    private ANCVisitHandler ancVisitHandler;
    @Mock
    private TTHandler ttHandler;
    @Mock
    private IFAHandler ifaHandler;
    @Mock
    private HBTestHandler hbTestHandler;
    @Mock
    private DeliveryOutcomeHandler deliveryOutcomeHandler;
    @Mock
    private PNCRegistrationOAHandler pncRegistrationOAHandler;
    @Mock
    private PNCCloseHandler pncCloseHandler;
    @Mock
    private PNCVisitHandler pncVisitHandler;
    @Mock
    private ChildImmunizationsHandler childImmunizationsHandler;
    @Mock
    private ChildRegistrationECHandler childRegistrationECHandler;
    @Mock
    private ChildRegistrationOAHandler childRegistrationOAHandler;
    @Mock
    private ChildCloseHandler childCloseHandler;
    @Mock
    private ChildIllnessHandler childIllnessHandler;
    @Mock
    private VitaminAHandler vitaminAHandler;
    @Mock
    private DeliveryPlanHandler deliveryPlanHandler;
    @Mock
    private ECEditHandler ecEditHandler;
    @Mock
    private ANCInvestigationsHandler ancInvestigationsHandler;
    @Mock
    private Listener<String> formSubmittedListener;

    private FormSubmissionRouter router;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        router = new FormSubmissionRouter(formDataRepository,
                ecRegistrationHandler,
                fpComplicationsHandler,
                fpChangeHandler,
                renewFPProductHandler,
                ecCloseHandler,
                ancRegistrationHandler,
                ancRegistrationOAHandler,
                ancVisitHandler,
                ancCloseHandler,
                ttHandler,
                ifaHandler,
                hbTestHandler,
                deliveryOutcomeHandler,
                pncRegistrationOAHandler,
                pncCloseHandler,
                pncVisitHandler,
                childImmunizationsHandler, childRegistrationECHandler, childRegistrationOAHandler, childCloseHandler,
                childIllnessHandler, vitaminAHandler, deliveryPlanHandler, ecEditHandler, ancInvestigationsHandler);
    }

    @Test
    public void shouldNotifyFormSubmittedListenersWhenFormIsHandled() throws Exception {
        FormSubmission formSubmission = create().withFormName("ec_registration").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);
        FORM_SUBMITTED.addListener(formSubmittedListener);

        router.route("instance id 1");

        InOrder inOrder = inOrder(formDataRepository, ecRegistrationHandler, formSubmittedListener);
        inOrder.verify(formDataRepository).fetchFromSubmission("instance id 1");
        inOrder.verify(ecRegistrationHandler).handle(formSubmission);
        inOrder.verify(formSubmittedListener).onEvent("instance id 1");
    }

    @Test
    public void shouldNotifyFormSubmittedListenersWhenThereIsNoHandlerForForm() throws Exception {
        FormSubmission formSubmission = create().withFormName("form-without-handler").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);
        FORM_SUBMITTED.addListener(formSubmittedListener);

        router.route("instance id 1");

        InOrder inOrder = inOrder(formDataRepository, formSubmittedListener);
        inOrder.verify(formDataRepository).fetchFromSubmission("instance id 1");
        inOrder.verify(formSubmittedListener).onEvent("instance id 1");
    }

    @Test
    public void shouldDelegateECRegistrationFormSubmissionHandlingToECRegistrationHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("ec_registration").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);
        FORM_SUBMITTED.addListener(formSubmittedListener);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ecRegistrationHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateFPComplicationsFormSubmissionHandlingToFPComplicationsHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("fp_complications").withInstanceId("instance id 2").withVersion("123").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(fpComplicationsHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateRenewFPProductFormSubmissionHandlingToRenewFPProductHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("renew_fp_product").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(renewFPProductHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateECCloseFormSubmissionHandlingToECCloseHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("ec_close").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ecCloseHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateANCRegistrationFormSubmissionHandlingToANCRegistrationHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("anc_registration").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ancRegistrationHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateANCRegistrationOAFormSubmissionHandlingToANCRegistrationOAHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("anc_registration_oa").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ancRegistrationOAHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateANCVisitFormSubmissionHandlingToANCVisitHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("anc_visit").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ancVisitHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateANCCloseFormSubmissionHandlingToANCCloseHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("anc_close").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ancCloseHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateTTFormSubmissionHandlingToTTHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("tt").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ttHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateTTBoosterFormSubmissionHandlingToTTHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("tt_booster").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ttHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateTT1FormSubmissionHandlingToTTHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("tt_1").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ttHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateTT2FormSubmissionHandlingToTTHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("tt_2").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ttHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateIFAFormSubmissionHandlingToIFAHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("ifa").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ifaHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateHBTestFormSubmissionHandlingToHBTestHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("hb_test").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(hbTestHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateDeliveryOutcomeFormSubmissionHandlingToDeliveryOutcomeHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("delivery_outcome").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(deliveryOutcomeHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegatePNCRegistrationFormSubmissionHandlingToPNCRegistrationHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("pnc_registration_oa").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(pncRegistrationOAHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegatePNCVisitFormSubmissionHandlingToPNCVisitHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("pnc_visit").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(pncVisitHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegatePNCCloseFormSubmissionHandlingToPNCCloseHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("pnc_close").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(pncCloseHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateChildImmunizationsFormSubmissionHandlingToChildImmunizationsHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("child_immunizations").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(childImmunizationsHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateChildRegistrationECFormSubmissionHandlingToChildRegistrationECHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("child_registration_ec").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(childRegistrationECHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateChildCloseFormSubmissionHandlingToChildCloseHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("child_close").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(childCloseHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateChildIllnessFormSubmissionHandlingToChildIllnessHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("child_illness").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(childIllnessHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateVitaminAFormSubmissionHandlingToVitaminAHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("vitamin_a").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(vitaminAHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateChildRegistrationOAFormSubmissionHandlingToChildRegistrationOAHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("child_registration_oa").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(childRegistrationOAHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateDeliveryPlanFormSubmissionHandlingToDeliveryPlanHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("delivery_plan").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(deliveryPlanHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateECEditFormSubmissionHandlingToECEditHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("ec_edit").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ecEditHandler).handle(formSubmission);
    }

    @Test
    public void shouldDelegateANCInvestigationsFormSubmissionHandlingToANCInvestigationsHandler() throws Exception {
        FormSubmission formSubmission = create().withFormName("anc_investigations").withInstanceId("instance id 1").withVersion("122").build();
        when(formDataRepository.fetchFromSubmission("instance id 1")).thenReturn(formSubmission);

        router.route("instance id 1");

        verify(formDataRepository).fetchFromSubmission("instance id 1");
        verify(ancInvestigationsHandler).handle(formSubmission);
    }
}
