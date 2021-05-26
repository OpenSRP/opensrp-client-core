package org.smartregister.view.viewpager;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Geoffrey Koros on 9/12/2015.
 */
public class OpenSRPViewPager extends ViewPager {

    public OpenSRPViewPager(Context context) {
        super(context);
    }

    public OpenSRPViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }
}
