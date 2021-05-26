package org.smartregister.view.dialog;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;

import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class AllEligibleCoupleServiceMode extends ServiceModeOption {

    public AllEligibleCoupleServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return CoreLibrary.getInstance().context().getStringResource(R.string.couple_selection);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() {
                return 7;
            }

            @Override
            public int weightSum() {
                return 1000;
            }

            @Override
            public int[] weights() {
                return new int[]{239, 73, 103, 107, 158, 221, 87};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{R.string.header_name, R.string.header_ec_no, R.string
                        .header_gplsa, R.string.header_fp, R.string.header_children, R.string
                        .header_status, R.string.header_edit};
            }
        };
    }


}
