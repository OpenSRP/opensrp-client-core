package org.smartregister.view.controller;

import android.os.AsyncTask;

import org.smartregister.view.contract.HomeContext;

import java.util.concurrent.locks.ReentrantLock;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static org.smartregister.util.Log.logWarn;

public class NativeUpdateANMDetailsTask {
    private static final ReentrantLock lock = new ReentrantLock();
    private final ANMController anmController;

    public NativeUpdateANMDetailsTask(ANMController anmController) {
        this.anmController = anmController;
    }

    public void fetch(final NativeAfterANMDetailsFetchListener afterFetchListener) {
        new AsyncTask<Void, Void, HomeContext>() {
            @Override
            protected HomeContext doInBackground(Void... params) {
                if (!lock.tryLock()) {
                    logWarn("Update ANM details is in progress, so going away.");
                    cancel(true);
                    return null;
                }
                try {
                    return anmController.getHomeContext();
                } finally {
                    lock.unlock();
                }
            }

            @Override
            protected void onPostExecute(HomeContext anm) {
                afterFetchListener.afterFetch(anm);
            }
        }.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }
}
