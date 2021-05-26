package org.smartregister.view.contract;

import java.util.Comparator;

public interface ECSmartRegisterBaseClient {
    Comparator<SmartRegisterClient> EC_NUMBER_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            return ((ECSmartRegisterBaseClient) client).ecNumber()
                    .compareTo(((ECSmartRegisterBaseClient) anotherClient).ecNumber());
        }
    };

    Integer ecNumber();
}
