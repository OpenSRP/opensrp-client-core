package org.smartregister.view.interactor;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.ConfigurableMemberProfileRowDataProvider;
import org.smartregister.domain.ConfigurableMemberProfileRowData;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.contract.ConfigurableMemberProfileActivityContract;

import java.util.ArrayList;
import java.util.List;

public class BaseConfigurableMemberProfileInteractor implements ConfigurableMemberProfileActivityContract.Interactor {
    protected AppExecutors appExecutors;
    protected ConfigurableMemberProfileRowDataProvider dataProvider;

    BaseConfigurableMemberProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseConfigurableMemberProfileInteractor(ConfigurableMemberProfileRowDataProvider dataProvider) {
        this(new AppExecutors());
        this.dataProvider = dataProvider;
    }

    @Override
    public void refreshProfileView(CommonPersonObjectClient client, boolean isForEdit, ConfigurableMemberProfileActivityContract.InteractorCallBack callBack) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            List<ConfigurableMemberProfileRowData> rowDataList = dataProvider != null? dataProvider.getRowData(client) : new ArrayList<>();
            callBack.refreshProfileTopSection(client);
            callBack.refreshProfileBottomSection(rowDataList);
        });
        appExecutors.diskIO().execute(runnable);
    }
}
