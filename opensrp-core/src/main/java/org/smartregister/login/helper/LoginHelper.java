package org.smartregister.login.helper;

import org.smartregister.view.contract.BaseLoginContract;


/**
 * Created by ndegwamartin on 09/04/2018.
 */

public class LoginHelper {
    private static LoginHelper instance;
    protected BaseLoginContract.Interactor mLoginInteractor;
    private LoginHelper(BaseLoginContract.Interactor mLoginInteractor) {

        this.mLoginInteractor = mLoginInteractor;
    }

    public static void init(BaseLoginContract.Interactor mLoginInteractor) {
        if (instance == null && mLoginInteractor!=null) {
            instance = new LoginHelper(mLoginInteractor);
        }
    }

    public static LoginHelper getInstance() {
        return instance;
    }




}




