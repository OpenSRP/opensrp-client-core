package org.smartregister.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;

/**
 * Created by ndegwamartin on 2020-02-18.
 */
public class DisplayUtilsTest extends BaseUnitTest {

    @Mock
    private Resources resources;

    @Mock
    private Activity context;

    @Mock
    private WindowManager windowManager;


    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testGetScreenDpiReturnsCorrectValuesForLowDensity() {

        Mockito.doReturn(resources).when(context).getResources();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.density = 0.65f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        DisplayUtils.ScreenDpi screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.LDPI, screenDpi);


        //Test Edge case

        displayMetrics = new DisplayMetrics();
        displayMetrics.density = 0.75f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.LDPI, screenDpi);
    }

    @Test
    public void testGetScreenDpiReturnsCorrectValuesForMediumDensity() {

        Mockito.doReturn(resources).when(context).getResources();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.density = 0.76f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        DisplayUtils.ScreenDpi screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.MDPI, screenDpi);


        //Test Edge case

        displayMetrics = new DisplayMetrics();
        displayMetrics.density = 1.0f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.MDPI, screenDpi);
    }


    @Test
    public void testGetScreenDpiReturnsCorrectValuesForHighDensity() {

        Mockito.doReturn(resources).when(context).getResources();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.density = 1.1f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        DisplayUtils.ScreenDpi screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.HDPI, screenDpi);


        //Test Edge case

        displayMetrics = new DisplayMetrics();
        displayMetrics.density = 1.5f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.HDPI, screenDpi);
    }


    @Test
    public void testGetScreenDpiReturnsCorrectValuesForExtraHighDensity() {

        Mockito.doReturn(resources).when(context).getResources();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.density = 1.6f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        DisplayUtils.ScreenDpi screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.XHDPI, screenDpi);


        //Test Edge case

        displayMetrics = new DisplayMetrics();
        displayMetrics.density = 2.0f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.XHDPI, screenDpi);
    }

    @Test
    public void testGetScreenDpiReturnsCorrectValuesForExtraExtraHighDensity() {

        Mockito.doReturn(resources).when(context).getResources();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.density = 2.1f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        DisplayUtils.ScreenDpi screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.XXHDPI, screenDpi);


        //Test Edge case

        displayMetrics = new DisplayMetrics();
        displayMetrics.density = 3.0f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.XXHDPI, screenDpi);
    }


    @Test
    public void testGetScreenDpiReturnsCorrectValuesForExtraExtraExtraHighDensity() {

        Mockito.doReturn(resources).when(context).getResources();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.density = 3.1f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        DisplayUtils.ScreenDpi screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.XXXHDPI, screenDpi);


        //Test Edge case

        displayMetrics = new DisplayMetrics();
        displayMetrics.density = 4.0f;

        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        screenDpi = DisplayUtils.getScreenDpi(context);

        Assert.assertEquals(DisplayUtils.ScreenDpi.XXXHDPI, screenDpi);
    }

    @Test
    public void testGetDisplayWidthReturnsCorrectWidthPixelsValue() {

        Mockito.doReturn(resources).when(context).getResources();

        Mockito.doReturn(windowManager).when(context).getWindowManager();

        DisplayManager displayManager = (DisplayManager) RuntimeEnvironment.application.getSystemService(Context.DISPLAY_SERVICE);

        Display display = displayManager.getDisplays()[0];

        Mockito.doReturn(display).when(windowManager).getDefaultDisplay();

        int defaultMetrics = display.getWidth();

        int displayWidth = DisplayUtils.getDisplayWidth(context);

        Assert.assertTrue(displayWidth > 0);
        Assert.assertEquals(defaultMetrics, displayWidth);


    }

    @Test
    public void testGetDisplayHeightReturnsCorrectHeightPixelsValue() {

        Mockito.doReturn(resources).when(context).getResources();

        Mockito.doReturn(windowManager).when(context).getWindowManager();

        DisplayManager displayManager = (DisplayManager) RuntimeEnvironment.application.getSystemService(Context.DISPLAY_SERVICE);

        Display display = displayManager.getDisplays()[0];

        Mockito.doReturn(display).when(windowManager).getDefaultDisplay();

        int defaultMetrics = display.getHeight();

        int displayHeight = DisplayUtils.getDisplayHeight(context);

        Assert.assertTrue(displayHeight > 0);
        Assert.assertEquals(defaultMetrics, displayHeight);

    }


    @Test
    public void testGetScreenSizeReturnsCorrectValues() {

        Mockito.doReturn(resources).when(context).getResources();

        Mockito.doReturn(windowManager).when(context).getWindowManager();

        DisplayManager displayManager = (DisplayManager) RuntimeEnvironment.application.getSystemService(Context.DISPLAY_SERVICE);

        Display display = displayManager.getDisplays()[0];//Default width by height 320 by 470

        Mockito.doReturn(display).when(windowManager).getDefaultDisplay();

        double screenSize = DisplayUtils.getScreenSize(context);

        Assert.assertEquals(3.5, screenSize, 0.1);

    }

}
