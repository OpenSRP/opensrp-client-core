package org.smartregister.view.contract;

import org.smartregister.domain.ANM;

public class HomeContext {
    private final long fpCount;
    private String anmName;
    private long ancCount;
    private long pncCount;
    private long childCount;
    private long eligibleCoupleCount;

    public HomeContext(ANM anm) {
        this.anmName = anm.name();
        this.ancCount = anm.ancCount();
        this.pncCount = anm.pncCount();
        this.childCount = anm.childCount();
        this.eligibleCoupleCount = anm.ecCount();
        this.fpCount = anm.fpCount();
    }

    public long fpCount() {
        return fpCount;
    }

    public long ancCount() {
        return ancCount;
    }

    public long pncCount() {
        return pncCount;
    }

    public long childCount() {
        return childCount;
    }

    public long eligibleCoupleCount() {
        return eligibleCoupleCount;
    }
}
