package org.smartregister.view.customcontrols;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.smartregister.R;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-06-27
 */

public class ProcessingInProgressSnackbar extends BaseTransientBottomBar<Snackbar> {

    protected ProcessingInProgressSnackbar(@NonNull ViewGroup parent, @NonNull View content
            , @NonNull com.google.android.material.snackbar.ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);

        getView().setBackgroundColor(ContextCompat.getColor(parent.getContext(), android.R.color.transparent));
        getView().setPadding(0, 0, 0, 0);
        getView().setClickable(false);
        getView().setFocusable(false);
    }

    @NonNull
    public static ProcessingInProgressSnackbar make(@NonNull View view) {
        final ViewGroup parent = findSuitableParent(view);
        if (parent == null) {
            throw new IllegalArgumentException(
                    "No suitable parent found from the given view. Please provide a valid view.");
        }

        ProcessingInProgressView processingInProgressView = (ProcessingInProgressView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.snackbar_processing_in_progress, parent, false);
        return new ProcessingInProgressSnackbar(parent, processingInProgressView, processingInProgressView);
    }

    private static ViewGroup findSuitableParent(View proposedView) {
        View view = proposedView;
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }

    public void addBottomBarMargin(int margin) {
        View view = getView();
        if (view instanceof Snackbar.SnackbarLayout) {
            ViewGroup.LayoutParams layoutParams = ((Snackbar.SnackbarLayout) view).getLayoutParams();

            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                marginLayoutParams.bottomMargin = marginLayoutParams.bottomMargin + margin;
            }
        }
    }
}
