package org.smartregister.view.viewholder;

import android.view.ViewGroup;
import android.view.ViewStub;

public class ViewStubInflater {
    ViewStub stub;
    ViewGroup inflatedLayout = null;

    public ViewStubInflater(ViewStub stub) {
        this.stub = stub;
    }

    public ViewGroup get() {
        if (!isInflated()) {
            this.inflatedLayout = (ViewGroup) stub.inflate();
        }
        return this.inflatedLayout;
    }

    private boolean isInflated() {
        return inflatedLayout != null;
    }

    public void setVisibility(int visibility) {
        if (isInflated()) {
            inflatedLayout.setVisibility(visibility);
        }
    }
}
