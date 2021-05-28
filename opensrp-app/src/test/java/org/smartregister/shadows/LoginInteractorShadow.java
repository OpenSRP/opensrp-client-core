package org.smartregister.shadows;

import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.view.contract.BaseLoginContract;

/**
 * Created by samuelgithengi on 8/4/20.
 */
public class LoginInteractorShadow extends BaseLoginInteractor {

    public LoginInteractorShadow(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {//do nothing
    }
}