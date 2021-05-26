package org.smartregister.view.dialog;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.view.contract.SmartRegisterClients;

import java.util.Collections;

import static org.smartregister.view.contract.SmartRegisterClient.NAME_COMPARATOR;

public class NameSort implements SortOption {
    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.sort_by_name_label);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, NAME_COMPARATOR);
        return allClients;
    }
}
