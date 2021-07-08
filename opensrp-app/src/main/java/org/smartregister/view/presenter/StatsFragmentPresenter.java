package org.smartregister.view.presenter;

import org.smartregister.view.contract.StatsFragmentContract;
import org.smartregister.view.interactor.StatsFragmentInteractor;

import java.util.Map;

public class StatsFragmentPresenter implements StatsFragmentContract.Presenter {

    private StatsFragmentContract.Interactor interactor;
    private StatsFragmentContract.View view;

    public StatsFragmentPresenter(StatsFragmentContract.View view) {
        this.view = view;
        this.interactor = new StatsFragmentInteractor(this);
    }

    @Override
    public void onECSyncInfoFetched(Map<String, Integer> syncInfoMap) {
        view.refreshECSyncInfo(syncInfoMap);
    }

    @Override
    public void fetchSyncInfo() {
        interactor.fetchECSyncInfo();
    }
}
