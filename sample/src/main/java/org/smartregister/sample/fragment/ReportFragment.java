package org.smartregister.sample.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.Toast;

import org.smartregister.sample.R;
import org.smartregister.sample.adapter.ReportsFragmentAdapter;
import org.smartregister.sample.callable.LoadReports;
import org.smartregister.sample.domain.Report;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.adapter.ListableAdapter;
import org.smartregister.view.fragment.BaseListFragment;
import org.smartregister.view.viewholder.ListableViewHolder;

import java.util.List;
import java.util.concurrent.Callable;

import timber.log.Timber;

/**
 * @author rkodev
 */
public class ReportFragment extends BaseListFragment<Report> {
    public static final String TAG = "ReportFragment";

    @NonNull
    @Override
    protected Callable<List<Report>> onStartCallable(@Nullable Bundle bundle) {
        return new LoadReports();
    }

    @Override
    protected AppExecutors.Request fetchRequestType() {
        return AppExecutors.Request.NETWORK_THREAD;
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_report;
    }

    @Override
    protected int getRecyclerViewID() {
        return R.id.recyclerView;
    }

    @Override
    protected int getProgressBarID() {
        return R.id.progress_bar;
    }

    @Override
    public void onListItemClicked(Report report, int layoutID) {
        Toast.makeText(getContext(), "You clicked on " + report.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFetchError(Exception ex) {
        Toast.makeText(getContext(), "An error occurred, handle it bro", Toast.LENGTH_SHORT).show();
        Timber.e(ex);
    }

    @NonNull
    @Override
    public ListableAdapter<Report, ListableViewHolder<Report>> adapter() {
        return new ReportsFragmentAdapter(list, this);
    }
}
