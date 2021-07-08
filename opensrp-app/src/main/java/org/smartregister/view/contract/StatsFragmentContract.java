package org.smartregister.view.contract;

import android.content.Context;

import java.util.Map;

public interface StatsFragmentContract {
    interface Presenter {

        void onECSyncInfoFetched(Map<String, Integer> syncInfoMap);

        void fetchSyncInfo();

    }

    interface View {
        Context getContext();

        void refreshECSyncInfo(Map<String, Integer> syncInfoMap);
    }

    interface Interactor {
        void fetchECSyncInfo();
    }
}
