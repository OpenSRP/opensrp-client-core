package org.smartregister;

import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.smartregister.repository.Repository;
import org.smartregister.sync.P2PClassifier;
import org.smartregister.view.activity.DrishtiApplication;

import static org.mockito.Mockito.mock;

/**
 * Created by samuelgithengi on 12/30/19.
 */
public class TestApplication extends DrishtiApplication {

    private P2PClassifier<JSONObject> p2PClassifier;

    @Override
    public void onCreate() {
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        CoreLibrary.init(context, new TestSyncConfiguration(), 1588062490000l);

        setTheme(R.style.Theme_AppCompat_NoActionBar); //or just R.style.Theme_AppCompat
    }

    @Override
    public void logoutCurrentUser() {
    }

    @Override
    public Repository getRepository() {
        if (repository == null)
            repository = mock(Repository.class);
        return repository;
    }


    @Override
    public void onTerminate() {//do nothing
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Nullable
    @Override
    public P2PClassifier<JSONObject> getP2PClassifier() {
        return p2PClassifier;
    }

    public void setP2PClassifier(P2PClassifier<JSONObject> p2PClassifier) {
        this.p2PClassifier = p2PClassifier;
    }
}
