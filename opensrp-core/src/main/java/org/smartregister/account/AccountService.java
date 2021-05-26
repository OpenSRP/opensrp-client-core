package org.smartregister.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ndegwamartin on 2020-04-27.
 */
public class AccountService extends Service {

    private AccountAuthenticator authenticator;

    @Override
    public void onCreate() {
        authenticator = new AccountAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();

    }
}
