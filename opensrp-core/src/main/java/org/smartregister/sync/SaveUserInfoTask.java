package org.smartregister.sync;

import org.smartregister.repository.AllSettings;
import org.smartregister.util.Log;
import org.smartregister.view.BackgroundAction;
import org.smartregister.view.LockingBackgroundTask;
import org.smartregister.view.ProgressIndicator;

/**
 * Created by Dimas Ciputra on 3/24/15.
 */
public class SaveUserInfoTask {

    private LockingBackgroundTask lockingBackgroundTask;
    private AllSettings allSettings;

    public SaveUserInfoTask(AllSettings allSettings) {
        this.allSettings = allSettings;
        lockingBackgroundTask = new LockingBackgroundTask(new ProgressIndicator() {
            @Override
            public void setVisible() {
            }

            @Override
            public void setInvisible() {
                Log.logInfo("Successfully saved User information");
            }
        });
    }

    public void save(final String userInfo) {
        lockingBackgroundTask.doActionInBackground(new BackgroundAction<Object>() {
            @Override
            public Object actionToDoInBackgroundThread() {
                allSettings.saveUserInformation(userInfo);
                return userInfo;
            }

            @Override
            public void postExecuteInUIThread(Object result) {

            }
        });
    }
}
