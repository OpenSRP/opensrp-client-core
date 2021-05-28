package org.smartregister.sample.interactor;

import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.view.contract.BaseLoginContract;

/**
 * Created by ndegwamartin on 08/05/2020.
 */
public class LoginInteractor extends BaseLoginInteractor {

    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {
        //Schedule your jobs here
    }
}
