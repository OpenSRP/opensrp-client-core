package org.smartregister.shadows;

import android.os.AsyncTask;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowAsyncTaskBridge;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Implements(AsyncTask.class)
public class ShadowAsyncTask<P, X, R> {

    @RealObject
    private AsyncTask<P, X, R> realAsyncTask;

    private final FutureTask<R> future;
    private final BackgroundWorker worker;
    private AsyncTask.Status status = AsyncTask.Status.PENDING;

    public ShadowAsyncTask() {
        worker = new BackgroundWorker();
        future = new FutureTask<R>(worker) {
            @Override
            protected void done() {
                status = AsyncTask.Status.FINISHED;
                try {
                    final R r = get();

                    try {
                        ShadowApplication.getInstance().getForegroundThreadScheduler().post(new Runnable() {
                            @Override
                            public void run() {
                                getBridge().onPostExecute(r);
                            }
                        });
                    } catch (Throwable t) {
                        throw new OnPostExecuteException(t);
                    }
                } catch (CancellationException e) {
                    ShadowApplication.getInstance().getForegroundThreadScheduler().post(new Runnable() {
                        @Override
                        public void run() {
                            getBridge().onCancelled();
                        }
                    });
                } catch (InterruptedException e) {
                    // Ignore.
                } catch (OnPostExecuteException e) {
                    // Ignore. //throw new RuntimeException(e.getCause());
                } catch (Throwable t) {
                    // Ignore. //throw new RuntimeException("An error occured while executing doInBackground()", t.getCause());
                }
            }
        };
    }

    @Implementation
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Implementation
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Implementation
    public R get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Implementation
    public R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    @Implementation
    public AsyncTask<P, X, R> execute(final P... params) {
        status = AsyncTask.Status.RUNNING;
        getBridge().onPreExecute();

        worker.params = params;

        ShadowApplication.getInstance().getBackgroundThreadScheduler().post(new Runnable() {
            @Override
            public void run() {
                future.run();
            }
        });

        return realAsyncTask;
    }

    @Implementation
    public AsyncTask<P, X, R> executeOnExecutor(Executor executor, P... params) {
        status = AsyncTask.Status.RUNNING;
        getBridge().onPreExecute();

        worker.params = params;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                future.run();
            }
        });

        return realAsyncTask;
    }

    @Implementation
    public AsyncTask.Status getStatus() {
        return status;
    }

    @Implementation
    public void publishProgress(final X... values) {
        ShadowApplication.getInstance().getForegroundThreadScheduler().post(new Runnable() {
            @Override
            public void run() {
                getBridge().onProgressUpdate(values);
            }
        });
    }

    private ShadowAsyncTaskBridge<P, X, R> getBridge() {
        return new ShadowAsyncTaskBridge<>(realAsyncTask);
    }

    private final class BackgroundWorker implements Callable<R> {
        private P[] params;

        @Override
        public R call() throws Exception {
            return getBridge().doInBackground(params);
        }
    }

    private static class OnPostExecuteException extends Exception {
        public OnPostExecuteException(Throwable throwable) {
            super(throwable);
        }
    }
}