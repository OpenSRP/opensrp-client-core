package org.smartregister.view;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import java.util.concurrent.locks.ReentrantLock;

import static org.smartregister.util.Log.logVerbose;

public class LockingBackgroundTask {
    private static final ReentrantLock lock = new ReentrantLock();
    private ProgressIndicator indicator;

    public LockingBackgroundTask(ProgressIndicator progressIndicator) {
        this.indicator = progressIndicator;
    }

    public <T> void doActionInBackground(final BackgroundAction<T> backgroundAction) {
        startAsyncTask(new AsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... params) {
                if (!lock.tryLock()) {
                    logVerbose("Going away. Something else is holding the lock.");
                    cancel(true);
                    return null;
                }
                try {
                    publishProgress();
                    return backgroundAction.actionToDoInBackgroundThread();
                } finally {
                    lock.unlock();
                }
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                indicator.setVisible();
            }

            @Override
            protected void onPostExecute(T result) {
                backgroundAction.postExecuteInUIThread(result);
                indicator.setInvisible();
            }
        }, null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected <T> void startAsyncTask(AsyncTask<T, ?, ?> asyncTask, T[] params) {
        if (params == null) {
            @SuppressWarnings("unchecked") T[] arr = (T[]) new Void[0];
            params = arr;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            asyncTask.execute(params);
        }
    }
}
