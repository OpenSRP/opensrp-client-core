package org.smartregister.view.interactor;

import androidx.annotation.VisibleForTesting;

import org.jetbrains.annotations.NotNull;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.ListContract;

import java.util.List;
import java.util.concurrent.Callable;

import timber.log.Timber;


/**
 * @author rkodev
 */

public class ListInteractor<T extends ListContract.Identifiable> implements ListContract.Interactor<T> {

    protected AppExecutors appExecutors;

    public ListInteractor() {
        appExecutors = new AppExecutors();
    }

    @VisibleForTesting
    ListInteractor(AppExecutors appExecutors){
        this.appExecutors = appExecutors;
    }

    @Override
    public void runRequest(@NotNull Callable<List<T>> callable, @NotNull AppExecutors.Request request, @NotNull ListContract.Presenter<T> presenter) {

        Runnable runnable = () -> {
            try {
                List<T> tList = callable.call();
                appExecutors.mainThread().execute(() -> presenter.onItemsFetched(tList));
            } catch (Exception e) {
                Timber.e(e);
                appExecutors.mainThread().execute(() -> presenter.onFetchRequestError(e));
            }
        };
        appExecutors.execute(runnable, request);
    }
}
