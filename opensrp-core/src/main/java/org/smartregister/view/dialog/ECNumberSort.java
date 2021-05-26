package org.smartregister.view.dialog;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.view.contract.ECSmartRegisterBaseClient;
import org.smartregister.view.contract.SmartRegisterClients;

import java.util.Collections;

public class ECNumberSort implements SortOption {
    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.sort_by_ec_number_label);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, ECSmartRegisterBaseClient.EC_NUMBER_COMPARATOR);
        return allClients;
    }
}
