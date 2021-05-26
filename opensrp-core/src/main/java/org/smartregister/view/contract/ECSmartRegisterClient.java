package org.smartregister.view.contract;

import org.smartregister.domain.FPMethod;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public interface ECSmartRegisterClient extends BaseFPSmartRegisterClient,
        ECSmartRegisterBaseClient {

    Comparator<SmartRegisterClient> EC_NUMBER_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            return ((ECSmartRegisterClient) client).ecNumber()
                    .compareTo(((ECSmartRegisterClient) anotherClient).ecNumber());
        }
    };

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

    List<ECChildClient> children();

    Map<String, String> status();
}
