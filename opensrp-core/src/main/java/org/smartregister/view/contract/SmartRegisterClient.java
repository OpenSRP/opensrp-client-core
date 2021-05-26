package org.smartregister.view.contract;

import org.smartregister.util.IntegerUtil;

import java.util.Comparator;

public interface SmartRegisterClient {

    Comparator<SmartRegisterClient> NAME_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            return client.compareName(anotherClient);
        }
    };

    Comparator<SmartRegisterClient> HIGH_PRIORITY_COMPARATOR = new
            Comparator<SmartRegisterClient>() {
                @Override
                public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
                    return client.isHighPriority() == anotherClient.isHighPriority() ? client.name()
                            .compareToIgnoreCase(anotherClient.name())
                            : anotherClient.isHighPriority() ? 1 : -1;
                }
            };

    Comparator<SmartRegisterClient> HIGH_RISK_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            return client.isHighRisk() == anotherClient.isHighRisk() ? client.name()
                    .compareToIgnoreCase(anotherClient.name())
                    : anotherClient.isHighRisk() ? 1 : -1;
        }
    };

    Comparator<SmartRegisterClient> BPL_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            if ((client.isBPL() && anotherClient.isBPL()) || (!client.isBPL() && !anotherClient
                    .isBPL())) {
                return client.compareName(anotherClient);
            } else {
                return anotherClient.isBPL() ? 1 : -1;
            }
        }
    };

    Comparator<SmartRegisterClient> SC_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            if ((client.isSC() && anotherClient.isSC()) || (!client.isSC() && !anotherClient
                    .isSC())) {
                return client.compareName(anotherClient);
            } else {
                return anotherClient.isSC() ? 1 : -1;
            }
        }
    };

    Comparator<SmartRegisterClient> ST_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            if ((client.isST() && anotherClient.isST()) || (!client.isST() && !anotherClient
                    .isST())) {
                return client.compareName(anotherClient);
            } else {
                return anotherClient.isST() ? 1 : -1;
            }
        }
    };

    Comparator<SmartRegisterClient> AGE_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            return IntegerUtil.compare(client.ageInDays(), anotherClient.ageInDays());
        }
    };

    Comparator<SmartRegisterClient> HR_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            if ((client.isHighRisk() && anotherClient.isHighRisk()) || (!client.isHighRisk()
                    && !anotherClient.isHighRisk())) {
                return client.compareName(anotherClient);
            } else {
                return anotherClient.isHighRisk() ? 1 : -1;
            }
        }
    };

    String entityId();

    String name();

    String displayName();

    String village();

    String wifeName();

    String husbandName();

    int age();

    int ageInDays();

    String ageInString();

    boolean isSC();

    boolean isST();

    boolean isHighRisk();

    boolean isHighPriority();

    boolean isBPL();

    String profilePhotoPath();

    String locationStatus();

    boolean satisfiesFilter(String filterCriterion);

    int compareName(SmartRegisterClient client);
}
