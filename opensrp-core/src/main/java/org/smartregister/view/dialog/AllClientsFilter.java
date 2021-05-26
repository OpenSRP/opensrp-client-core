package org.smartregister.view.dialog;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.view.contract.SmartRegisterClient;

public class AllClientsFilter implements FilterOption {
    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.filter_by_all_label);
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return true;
    }
}
