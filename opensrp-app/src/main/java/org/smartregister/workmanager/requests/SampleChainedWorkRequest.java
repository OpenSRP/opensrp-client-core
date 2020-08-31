package org.smartregister.workmanager.requests;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.smartregister.workmanager.jobs.BaseWorker;

public class SampleChainedWorkRequest implements LifecycleOwner {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void runTask() {
        Data inputData = new Data.Builder()
                .putString("key", "value")
                .build();

        // first job
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(false)
                .setRequiresDeviceIdle(false)
                .build();

        final OneTimeWorkRequest request1 = new OneTimeWorkRequest.Builder(BaseWorker.class)
                .setInputData(inputData)
                .setConstraints(constraints)
                .build();

        // second job
        inputData = new Data.Builder()
                .putString("key", "value")
                .build();

        final OneTimeWorkRequest request2 = new OneTimeWorkRequest.Builder(BaseWorker.class)
                .setInputData(inputData)
                .build();

        // third job
        inputData = new Data.Builder()
                .putString("key", "value")
                .build();

        constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(false)
                .setRequiresDeviceIdle(false)
                .build();

        final OneTimeWorkRequest request3 = new OneTimeWorkRequest.Builder(BaseWorker.class)
                .setInputData(inputData)
                .setConstraints(constraints)
                .build();

        // queue work requests
        // requests are executed in the order they are queued
        WorkManager.getInstance()
                .beginWith(request1)
                .then(request2)
                .then(request3)
                .enqueue();

        WorkManager.getInstance().getWorkInfoByIdLiveData(request3.getId())
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

        WorkManager.getInstance().getWorkInfoByIdLiveData(request2.getId())
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

        WorkManager.getInstance().getWorkInfoByIdLiveData(request3.getId())
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
