package org.smartregister.util;

import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ndegwamartin on 2020-03-04.
 */
public class PaginationHolder {

    private Button nextPageView;
    private Button previousPageView;
    private TextView pageInfoView;

    public Button getNextPageView() {
        return nextPageView;
    }

    public void setNextPageView(Button nextPageView) {
        this.nextPageView = nextPageView;
    }

    public Button getPreviousPageView() {
        return previousPageView;
    }

    public void setPreviousPageView(Button previousPageView) {
        this.previousPageView = previousPageView;
    }

    public TextView getPageInfoView() {
        return pageInfoView;
    }

    public void setPageInfoView(TextView pageInfoView) {
        this.pageInfoView = pageInfoView;
    }

}
