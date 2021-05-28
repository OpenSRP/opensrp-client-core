package org.smartregister.view.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.ListContract;
import org.smartregister.view.interactor.ListInteractor;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author rkodev
 */
public class ListPresenter<T extends ListContract.Identifiable> implements ListContract.Presenter<T> {

    @Nullable
    private WeakReference<ListContract.View<T>> weakReference;
    private ListContract.Interactor<T> interactor;
    private ListContract.Model<T> model;

    /**
     * Calling the fetch list method directly from the view may lead to memory leaks,
     * Use this method with caution when on the view
     *
     * @param callable
     */
    @Override
    public void fetchList(@NotNull Callable<List<T>> callable, @NonNull AppExecutors.Request request) {
        ListContract.View<T> currentView = getView();
        if (currentView != null)
            currentView.setLoadingState(true);

        getInteractor().runRequest(callable, request, this);
    }

    @Override
    public void onFetchRequestError(Exception e) {
        ListContract.View<T> currentView = getView();
        if (currentView == null) return;

        currentView.setLoadingState(false);
        currentView.onFetchError(e);
    }

    @Override
    public void onItemsFetched(List<T> identifiables) {
        ListContract.View<T> currentView = getView();
        if (currentView == null) return;

        currentView.renderData(identifiables);
        currentView.refreshView();
        currentView.setLoadingState(false);
    }

    @Override
    public <V extends ListContract.View<T>> ListContract.Presenter<T> with(V view) {
        weakReference = new WeakReference<>(view);
        return this;
    }

    @Override
    public <I extends ListContract.Interactor<T>> ListContract.Presenter<T> using(I interactor) {
        this.interactor = interactor;
        return this;
    }

    @Override
    public <M extends ListContract.Model<T>> ListContract.Presenter<T> withModel(M model) {
        this.model = model;
        return this;
    }


    @Nullable
    @Override
    public ListContract.View<T> getView() {
        if (weakReference != null)
            return weakReference.get();

        return null;
    }

    @Override
    public <M extends ListContract.Model<T>> M getModel() {
        return (M) model;
    }

    @Override
    public ListContract.Interactor<T> getInteractor() {
        if(interactor == null)
            interactor = new ListInteractor<>();

        return interactor;
    }
}
