package org.smartregister.workmanager.jobs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import org.smartregister.child.workmanager.utils.WorkerUtils;

import timber.log.Timber;

public class TestWorker extends BaseWorker {

    public TestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public ListenableWorker.Result doWork() {
        try {
            Data inputData = getInputData();

            // do something
            performTask(inputData);

            Data outputData = new Data.Builder()
                    .putString("key", "value")
                    .build();

            return ListenableWorker.Result.success(outputData);
        } catch (Exception e) {
            Timber.e("Work Request error");
            return ListenableWorker.Result.failure();
        }
    }

    private void performTask(Data inputData) {
        Timber.i("Job initiated");

        String title = inputData.getString("title");
        String desc = inputData.getString("desc");

        WorkerUtils.makeStatusNotification(this, 1, title, desc);
    }
}
