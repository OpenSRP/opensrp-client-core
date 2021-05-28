package org.smartregister.shadows;

import android.os.AsyncTask;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import java.util.concurrent.Executor;

/**
 * Created by kaderchowdhury on 06/12/17.
 */

@Implements(AsyncTask.class)
public class MyShadowAsyncTask<Params, Progress, Result> extends ShadowAsyncTask<Params, Progress, Result> {

    @RealObject
    private AsyncTask<Params, Progress, Result> realAsyncTask;

    @Implementation
    public AsyncTask<Params, Progress, Result> executeOnExecutor(Executor executor, Params... params) {
        return super.execute(params);
    }
}