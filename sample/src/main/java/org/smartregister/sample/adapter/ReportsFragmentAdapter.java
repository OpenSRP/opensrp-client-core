package org.smartregister.sample.adapter;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.smartregister.sample.R;
import org.smartregister.sample.domain.Report;
import org.smartregister.view.ListContract;
import org.smartregister.view.adapter.ListableAdapter;
import org.smartregister.view.viewholder.ListableViewHolder;

import java.util.List;

/**
 * @author rkodev
 */
public class ReportsFragmentAdapter extends ListableAdapter<Report, ListableViewHolder<Report>> {

    public ReportsFragmentAdapter(List<Report> items, ListContract.View<Report> view) {
        super(items, view);
    }

    @NonNull
    @Override
    public ListableViewHolder<Report> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_fragment_item, parent, false);
        return new ReportViewHolder(view);
    }

    public static class ReportViewHolder extends ListableViewHolder<Report> {

        private TextView tvName, tvAge, tvSalary;
        private View currentView;

        private ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            currentView = itemView;
            tvName = itemView.findViewById(R.id.tvName);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvSalary = itemView.findViewById(R.id.tvSalary);
        }

        @Override
        public void bindView(Report reportType, ListContract.View<Report> view) {
            tvName.setText(reportType.getName());
            tvAge.setText(reportType.getAge());
            tvSalary.setText(reportType.getSalary());
            currentView.setOnClickListener(v -> view.onListItemClicked(reportType, v.getId()));
        }

        @Override
        public void resetView() {
            tvName.setText("");
            tvAge.setText("");
            tvSalary.setText("");
        }
    }
}
