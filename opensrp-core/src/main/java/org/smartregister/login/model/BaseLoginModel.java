package org.smartregister.login.model;

import android.text.TextUtils;

import org.smartregister.CoreLibrary;
import org.smartregister.view.contract.BaseLoginContract;

/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class BaseLoginModel implements BaseLoginContract.Model {

    @Override
    public org.smartregister.Context getOpenSRPContext() {
        return CoreLibrary.getInstance().context();

    }

    @Override
    public boolean isPasswordValid(char[] password) {
        return password != null && password.length > 1;
    }

    @Override
    public boolean isEmptyUsername(String username) {
        return username == null || TextUtils.isEmpty(username);
    }

    @Override
    public boolean isUserLoggedOut() {
        return getOpenSRPContext().IsUserLoggedOut();
    }

}
