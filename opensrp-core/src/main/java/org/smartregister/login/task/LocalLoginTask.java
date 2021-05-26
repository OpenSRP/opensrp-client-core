package org.smartregister.login.task;

import android.os.AsyncTask;

import org.smartregister.CoreLibrary;
import org.smartregister.event.Listener;
import org.smartregister.service.UserService;
import org.smartregister.view.contract.BaseLoginContract;

/**
 * Created by ndegwamartin on 13/06/2020.
 */
public class LocalLoginTask extends AsyncTask<Void, Integer, Boolean> {

    private BaseLoginContract.View mLoginView;
    private final String mUsername;
    private final char[] mPassword;
    private final Listener<Boolean> mAfterLoginCheck;

    public LocalLoginTask(BaseLoginContract.View loginView, String username, char[] password, Listener<Boolean> afterLoginCheck) {
        mLoginView = loginView;
        mUsername = username;
        mPassword = password;
        mAfterLoginCheck = afterLoginCheck;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mLoginView.showProgress(true);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return getUserService().isUserInValidGroup(mUsername, mPassword);
    }

    @Override
    protected void onPostExecute(final Boolean loginResponse) {
        super.onPostExecute(loginResponse);

        mLoginView.showProgress(false);
        mAfterLoginCheck.onEvent(loginResponse);
    }

    private UserService getUserService() {
        return CoreLibrary.getInstance().context().userService();
    }
}
