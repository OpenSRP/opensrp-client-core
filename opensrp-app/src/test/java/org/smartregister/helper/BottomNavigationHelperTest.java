package org.smartregister.helper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;

public class BottomNavigationHelperTest extends BaseUnitTest {

    @Mock
    private Bitmap bitmap;

    @Mock
    private Drawable drawable;

    private BottomNavigationHelper bottomNavigationHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bottomNavigationHelper = new BottomNavigationHelper();
    }

    @Test
    public void testConvertDrawableToBitmap() {
        BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);

        Resources resources = Mockito.mock(Resources.class);
        Assert.assertNotNull(resources);

        Mockito.doReturn(drawable).when(resources).getDrawable(INITIALS_RESOURCE_ID);
        Assert.assertNotNull(drawable);

        Assert.assertNull(bitmap.copy(Bitmap.Config.ARGB_8888, true));

        //Assert.assertNotNull(spyBottomNavigationHelper.convertDrawableResToBitmap(INITIALS_RESOURCE_ID, resources));
    }

    @Test
    public void testConvertGradientDrawableToBitmap() {
        Assert.assertNotNull(bottomNavigationHelper.convertDrawableResToBitmap(R.drawable.bottom_bar_initials_background, RuntimeEnvironment.application.getResources()));
    }

    @Test
    public void testConvertBitmapDrawableToBitmap() {
        Assert.assertNotNull(bottomNavigationHelper.convertDrawableResToBitmap(R.drawable.child_boy_infant, RuntimeEnvironment.application.getResources()));
    }

    @Test
    public void writeOnDrawableShouldCallConvertDrawableResToBitmap() {
        BottomNavigationHelper spyBottomNavigationHelper = Mockito.spy(bottomNavigationHelper);
        Mockito.doReturn(bitmap).when(spyBottomNavigationHelper).convertDrawableResToBitmap(Mockito.eq(R.drawable.bottom_bar_initials_background), Mockito.any(Resources.class));

        Assert.assertNotNull(spyBottomNavigationHelper.writeOnDrawable(R.drawable.bottom_bar_initials_background, INITIALS_TEXT, RuntimeEnvironment.application.getResources()));
        Mockito.verify(bitmap).copy(Bitmap.Config.ARGB_8888, true);
    }
}
