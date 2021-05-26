package org.smartregister.view.contract;

import org.joda.time.LocalDateTime;
import org.smartregister.domain.ANCServiceType;

import java.util.Comparator;
import java.util.List;

public interface ANCSmartRegisterClient extends SmartRegisterClient {

    Comparator<SmartRegisterClient> EDD_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            return ((ANCSmartRegisterClient) client).edd()
                    .compareTo(((ANCSmartRegisterClient) anotherClient).edd());
        }
    };

    String eddForDisplay();

    LocalDateTime edd();

    String pastDueInDays();

    String weeksAfterLMP();

    AlertDTO getAlert(ANCServiceType type);

    boolean isVisitsDone();

    boolean isTTDone();

    boolean isIFADone();

    String visitDoneDateWithVisitName();

    String ttDoneDate();

    String ifaDoneDate();

    String thayiCardNumber();

    String ancNumber();

    String lmp();

    String riskFactors();

    ServiceProvidedDTO serviceProvidedToACategory(String category);

    String getHyperTension(ServiceProvidedDTO ancServiceProvided);

    ServiceProvidedDTO getServiceProvidedDTO(String serviceName);

    List<ServiceProvidedDTO> allServicesProvidedForAServiceType(String serviceType);

    String ashaPhoneNumber();
}
