package org.smartregister.helper;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.DrawableRes;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.smartregister.R;

import static android.util.DisplayMetrics.DENSITY_DEFAULT;

public class BottomNavigationHelper {

    /**
     * Removes the default bottom navigation bar behaviour to enable display of all 5 icons and text
     *
     * @param view
     */
    @SuppressLint("RestrictedApi")
    public void disableShiftMode(BottomNavigationView view) {
        view.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
    }

    /**
     * Convert Some kinds of Drawables to Bitmaps
     *
     * @param drawableId
     * @param resources
     * @return
     */
    protected Bitmap convertDrawableResToBitmap(@DrawableRes int drawableId,
                                                Resources resources) {
        Drawable drawable = resources.getDrawable(drawableId);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;

            int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 27;
            int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 27;

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            gradientDrawable.setBounds(0, 0, width, height);
            gradientDrawable.setStroke(2, resources.getColor(R.color.light_grey_text));
            gradientDrawable.setColor(resources.getColor(R.color.transparent));
            gradientDrawable.setFilterBitmap(true);
            gradientDrawable.draw(canvas);
            return bitmap;
        } else {
            Bitmap bit = BitmapFactory.decodeResource(resources, drawableId);
            return bit.copy(Bitmap.Config.ARGB_8888, true);
        }
    }

    /**
     * Write Text to drawables, example used to write the bottom navigation bar me menu
     *
     * @param drawableId
     * @param initials
     * @param resources
     * @return
     */
    public BitmapDrawable writeOnDrawable(int drawableId, String initials, Resources resources) {
        Bitmap drawableResToBitmap = convertDrawableResToBitmap(drawableId, resources);
        drawableResToBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(drawableResToBitmap);
        canvas.setDensity(DENSITY_DEFAULT);

        Paint initialsPaint = new Paint();
        initialsPaint.setStyle(Paint.Style.FILL);
        initialsPaint.setColor(resources.getColor(R.color.scan_qr_code_bg_end_grey));
        initialsPaint.setTextSize((float) (resources.getDimensionPixelSize(R.dimen.me_bottom_nav_bar_text_size)));
        initialsPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        initialsPaint.setTextAlign(Paint.Align.CENTER);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) (((canvas.getHeight() / 2) - ((initialsPaint.descent() + initialsPaint.ascent()) / 2)) + 1);

        canvas.drawText(initials, xPos, yPos, initialsPaint);

        return new BitmapDrawable(resources, drawableResToBitmap);
    }
}
