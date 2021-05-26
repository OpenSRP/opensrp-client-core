package org.smartregister.sync;

import org.smartregister.repository.AllSettings;
import org.smartregister.util.Log;
import org.smartregister.view.BackgroundAction;
import org.smartregister.view.LockingBackgroundTask;
import org.smartregister.view.ProgressIndicator;

/**
 * Created by samuelgithengi on 1/2/18.
 */

public class SaveANMTeamTask {
    private final LockingBackgroundTask task;
    private AllSettings allSettings;

    public SaveANMTeamTask(AllSettings allSettings) {
        this.allSettings = allSettings;
        task = new LockingBackgroundTask(new ProgressIndicator() {
            @Override
            public void setVisible() {//Do nothing
            }

            @Override
            public void setInvisible() {
                Log.logInfo("Successfully saved ANM Team information");
            }
        });
    }

    public void save(final String anmTeam) {
        task.doActionInBackground(new BackgroundAction<String>() {
            @Override
            public String actionToDoInBackgroundThread() {
                allSettings.saveANMTeam(anmTeam);
                return anmTeam;
            }

            @Override
            public void postExecuteInUIThread(String result) {//Do nothing
            }
        });
    }
}
