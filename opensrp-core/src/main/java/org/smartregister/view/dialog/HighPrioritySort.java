package org.smartregister.view.dialog;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.view.contract.SmartRegisterClients;

import java.util.Collections;

import static org.smartregister.view.contract.SmartRegisterClient.HIGH_PRIORITY_COMPARATOR;

public class HighPrioritySort implements SortOption {
    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.sort_by_high_priority_label);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, HIGH_PRIORITY_COMPARATOR);
        return allClients;
    }
}
