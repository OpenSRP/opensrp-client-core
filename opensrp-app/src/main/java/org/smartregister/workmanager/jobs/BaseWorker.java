package org.smartregister.workmanager.jobs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

public abstract class BaseWorker extends Worker {

    @NotNull
    public Context getContext() {
        return super.getApplicationContext();
    }

    public BaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public abstract Result doWork();
}
