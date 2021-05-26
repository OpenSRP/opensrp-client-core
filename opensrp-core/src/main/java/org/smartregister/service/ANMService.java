package org.smartregister.service;

import org.smartregister.domain.ANM;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.repository.AllSharedPreferences;

public class ANMService {
    private AllSharedPreferences allSharedPreferences;
    private AllBeneficiaries allBeneficiaries;
    private AllEligibleCouples allEligibleCouples;

    public ANMService(AllSharedPreferences allSharedPreferences, AllBeneficiaries
            allBeneficiaries, AllEligibleCouples allEligibleCouples) {
        this.allSharedPreferences = allSharedPreferences;
        this.allBeneficiaries = allBeneficiaries;
        this.allEligibleCouples = allEligibleCouples;
    }

    public ANM fetchDetails() {
        return new ANM(allSharedPreferences.fetchRegisteredANM(), allEligibleCouples.count(),
                allEligibleCouples.fpCount(), allBeneficiaries.ancCount(),
                allBeneficiaries.pncCount(), allBeneficiaries.childCount());
    }
}
