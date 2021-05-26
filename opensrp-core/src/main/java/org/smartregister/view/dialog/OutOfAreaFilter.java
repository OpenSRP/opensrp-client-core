package org.smartregister.view.dialog;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.view.contract.SmartRegisterClient;

import static org.smartregister.AllConstants.OUT_OF_AREA;

public class OutOfAreaFilter implements FilterOption {
    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.filter_by_out_of_area_label);
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return OUT_OF_AREA.equalsIgnoreCase(client.locationStatus());
    }
}
