package org.smartregister.view.fragment;

import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.smartregister.util.AppExecutors;
import org.smartregister.view.ListContract;
import org.smartregister.view.presenter.ListPresenter;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rkodev
 */
public abstract class BaseListFragment<T extends ListContract.Identifiable> extends Fragment implements ListContract.View<T> {

    protected View view;
    protected ListContract.Adapter<T> mAdapter;
    protected ProgressBar progressBar;
    protected ListContract.Presenter<T> presenter;
    protected List<T> list;
    protected AtomicInteger incompleteRequests = new AtomicInteger(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(getRootLayout(), container, false);
        bindLayout();
        loadPresenter();
        presenter.fetchList(onStartCallable(getArguments()), fetchRequestType());
        return view;
    }

    protected AppExecutors.Request fetchRequestType() {
        return AppExecutors.Request.DISK_THREAD;
    }

    /***
     * Provide a callable with no reference to the current view
     *
     * Avoid holding references to the view to prevent memory leaks
     *
     * @return
     */
    @NonNull
    protected abstract Callable<List<T>> onStartCallable(@Nullable Bundle bundle);

    /**
     * @return
     */
    @LayoutRes
    protected abstract int getRootLayout();

    @IdRes
    protected abstract int getRecyclerViewID();

    @IdRes
    protected abstract int getProgressBarID();

    @Override
    public void bindLayout() {
        RecyclerView recyclerView = view.findViewById(getRecyclerViewID());
        recyclerView.setHasFixedSize(false);

        if (getContext() != null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
        }

        if (hasDivider() && getContext() != null)
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        progressBar = view.findViewById(getProgressBarID());
        progressBar.setVisibility(View.GONE);

        mAdapter = adapter();
        recyclerView.setAdapter(mAdapter.getRecyclerAdapter());
    }

    @Override
    public boolean hasDivider() {
        return true;
    }

    @Override
    public void renderData(List<T> identifiables) {
        this.list = identifiables;
    }

    @Override
    public void refreshView() {
        mAdapter.reloadData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setLoadingState(boolean loadingState) {
        int result = loadingState ? incompleteRequests.incrementAndGet() : incompleteRequests.decrementAndGet();
        progressBar.setVisibility(result > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public abstract void onListItemClicked(T t, int layoutID);

    @NonNull
    @Override
    public abstract ListContract.Adapter<T> adapter();

    @NonNull
    @Override
    public ListContract.Presenter<T> loadPresenter() {
        if (presenter == null) {
            presenter = new ListPresenter<T>()
                    .with(this);
        }
        return presenter;
    }
}
