package org.smartregister.view.contract;

import org.smartregister.domain.FPMethod;

import java.util.List;

public interface FPSmartRegisterClient extends BaseFPSmartRegisterClient,
        ECSmartRegisterBaseClient {

    String numberOfPregnancies();

    String parity();

    String numberOfLivingChildren();

    String numberOfStillbirths();

    String numberOfAbortions();

    String familyPlanningMethodChangeDate();

    String numberOfOCPDelivered();

    String numberOfCondomsSupplied();

    String numberOfCentchromanPillsDelivered();

    String iudPerson();

    String iudPlace();

    FPMethod fpMethod();

    String youngestChildAge();

    String complicationDate();

    String condomSideEffect();

    String iudSidEffect();

    String ocpSideEffect();

    String sterilizationSideEffect();

    String injectableSideEffect();

    String otherSideEffect();

    List<AlertDTO> alerts();

    RefillFollowUps refillFollowUps();

    String highPriorityReason();
}

