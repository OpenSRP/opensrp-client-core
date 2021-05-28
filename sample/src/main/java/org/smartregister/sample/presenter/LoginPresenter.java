package org.smartregister.sample.presenter;

import org.smartregister.login.model.BaseLoginModel;
import org.smartregister.login.presenter.BaseLoginPresenter;
import org.smartregister.sample.interactor.LoginInteractor;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;

/**
 * Created by ndegwamartin on 08/05/2020.
 */
public class LoginPresenter extends BaseLoginPresenter {

    public LoginPresenter(BaseLoginContract.View loginView) {
        mLoginView = new WeakReference<>(loginView);
        mLoginInteractor = new LoginInteractor(this);
        mLoginModel = new BaseLoginModel();
    }
    @Override
    public void processViewCustomizations() {
       //Do nothing
    }

    @Override
    public boolean isServerSettingsSet() {
        return false;
    }
}
