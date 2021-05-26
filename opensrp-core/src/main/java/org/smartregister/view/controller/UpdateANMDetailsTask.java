package org.smartregister.view.controller;

import android.os.AsyncTask;

import java.util.concurrent.locks.ReentrantLock;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static org.smartregister.util.Log.logWarn;

public class UpdateANMDetailsTask {
    private static final ReentrantLock lock = new ReentrantLock();
    private final ANMController anmController;

    public UpdateANMDetailsTask(ANMController anmController) {
        this.anmController = anmController;
    }

    public void fetch(final AfterANMDetailsFetchListener afterFetchListener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                if (!lock.tryLock()) {
                    logWarn("Update ANM details is in progress, so going away.");
                    cancel(true);
                    return null;
                }
                try {
                    return anmController.get();
                } finally {
                    lock.unlock();
                }
            }

            @Override
            protected void onPostExecute(String anm) {
                afterFetchListener.afterFetch(anm);
            }
        }.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }
}
