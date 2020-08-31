package org.smartregister.workmanager.requests;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.smartregister.workmanager.jobs.BaseWorker;

import java.util.concurrent.TimeUnit;

public class SamplePeriodicWorkRequest implements LifecycleOwner {

    public static final int WORK_PERIOD = 6;
    public static final String WORK_NAME = "org.smartregister.periodicwork";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void runTask() {
        Data inputData = new Data.Builder()
                .putString("key", "value")
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(BaseWorker.class, WORK_PERIOD, TimeUnit.HOURS)
                .setInputData(inputData)
                .addTag(WORK_NAME)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, request);

        WorkManager.getInstance().getWorkInfoByIdLiveData(request.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null) {
                            if (workInfo.getState().isFinished()) {
                                Data outputData = workInfo.getOutputData();
                                String output = outputData.getString("key");
                            }
                        }
                    }
                });
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }
}
