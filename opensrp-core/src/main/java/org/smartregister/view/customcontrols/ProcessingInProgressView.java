package org.smartregister.view.customcontrols;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.ContentViewCallback;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import org.smartregister.R;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-06-27
 */

public class ProcessingInProgressView extends LinearLayout implements ContentViewCallback {

    public ProcessingInProgressView(Context context) {
        super(context);
    }

    public ProcessingInProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProcessingInProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProcessingInProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_processing_in_progress, this);
    }

    @Override
    public void animateContentIn(int i, int i1) {
        // No animation for adding the content in
    }

    @Override
    public void animateContentOut(int i, int i1) {
        // No animation for content out
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

}
