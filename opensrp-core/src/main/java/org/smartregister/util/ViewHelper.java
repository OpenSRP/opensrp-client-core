package org.smartregister.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import org.smartregister.R;

/**
 * Created by ndegwamartin on 2020-03-04.
 */
public class ViewHelper {

    public static PaginationHolder addPaginationCore(View.OnClickListener onClickListener, ListView clientsView) {

        ViewGroup footerView = getPaginationView((Activity) clientsView.getContext());

        PaginationHolder paginationHolder = new PaginationHolder();

        paginationHolder.setNextPageView(footerView.findViewById(R.id.btn_next_page));
        paginationHolder.setPreviousPageView(footerView.findViewById(R.id.btn_previous_page));
        paginationHolder.setPageInfoView(footerView.findViewById(R.id.txt_page_info));

        paginationHolder.getNextPageView().setOnClickListener(onClickListener);
        paginationHolder.getPreviousPageView().setOnClickListener(onClickListener);

        footerView.setLayoutParams(
                new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                        (int) clientsView.getContext().getResources().getDimension(R.dimen.pagination_bar_height)));

        clientsView.addFooterView(footerView);

        return paginationHolder;
    }

    public static ViewGroup getPaginationView(Activity context) {
        return (ViewGroup) context.getLayoutInflater()
                .inflate(R.layout.smart_register_pagination, null);
    }
}
