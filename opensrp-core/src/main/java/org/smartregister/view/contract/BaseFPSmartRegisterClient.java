package org.smartregister.view.contract;

import org.smartregister.domain.FPMethod;

public interface BaseFPSmartRegisterClient extends SmartRegisterClient {

    FPMethod fpMethod();

    String familyPlanningMethodChangeDate();

    String numberOfOCPDelivered();

    String numberOfCondomsSupplied();

    String numberOfCentchromanPillsDelivered();

    String iudPerson();

    String iudPlace();

}
