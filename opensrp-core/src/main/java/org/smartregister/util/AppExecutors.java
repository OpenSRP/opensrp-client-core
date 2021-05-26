package org.smartregister.util;


/**
 * Created by keyman on 12/11/18.
 */

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
public class AppExecutors {

    private static final int THREAD_COUNT = 3;

    private final Executor diskIO;

    private final Executor networkIO;

    private final Executor mainThread;

    public AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public AppExecutors() {
        this(new DiskIOThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT),
                new MainThreadExecutor());
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    /**
     * Auto assign the executor by request type
     *
     * @param runnable
     * @param request
     */
    public void execute(@NonNull Runnable runnable, @NonNull Request request) {
        switch (request) {
            case DISK_THREAD:
                diskIO().execute(runnable);
                break;
            case NETWORK_THREAD:
                networkIO().execute(runnable);
                break;
            case MAIN_THREAD:
                mainThread().execute(runnable);
                break;
            default:
                throw new IllegalArgumentException("Unknown request");
        }
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    /**
     * Executor that runs a task on a new background thread.
     */
    private static class DiskIOThreadExecutor implements Executor {

        private final Executor mDiskIO;

        public DiskIOThreadExecutor() {
            mDiskIO = Executors.newSingleThreadExecutor();
        }

        @Override
        public void execute(@NonNull Runnable command) {
            mDiskIO.execute(command);
        }
    }

    public enum Request {
        MAIN_THREAD, NETWORK_THREAD, DISK_THREAD
    }
}

