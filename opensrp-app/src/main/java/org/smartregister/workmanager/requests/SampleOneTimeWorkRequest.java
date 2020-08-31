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

public class SampleOneTimeWorkRequest implements LifecycleOwner {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void runTask() {
        Data inputData = new Data.Builder()
                .putString("key", "value")
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(false)
                .setRequiresDeviceIdle(false)
                .build();

        final OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(BaseWorker.class)
                .setInputData(inputData)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance().enqueue(request);

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
