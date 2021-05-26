package org.smartregister.view.activity;

import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.view.activity.mock.BarcodeScanActivityMock;

import static org.mockito.Mockito.verify;

public class BarcodeScanActivityTest extends BaseUnitTest {

    private BarcodeScanActivityMock barcodeScanActivity;

    private ActivityController<BarcodeScanActivityMock> controller;

    @Mock
    private android.content.Context applicationContext;

    @Mock
    private SparseArray<Barcode> barcodeSparseArray;

    @Mock
    private Detector.Detections<Barcode> detections;

    @Mock
    private org.smartregister.Context context_;

    @BeforeClass
    public static void resetCoreLibrary() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
        Intent intent = new Intent(RuntimeEnvironment.application, BarcodeScanActivityMock.class);
        controller = Robolectric.buildActivity(BarcodeScanActivityMock.class, intent);
        controller.create()
                .start()
                .resume()
                .visible();
        barcodeScanActivity = Mockito.spy(controller.get());
    }

    @After
    public void tearDown() {
        destroyController();
    }

    @Test
    public void testActivityCreatedSuccessfully() {
        Assert.assertNotNull(barcodeScanActivity);
    }

    @Test
    public void testCloseActivitySuccessfully() {
        barcodeScanActivity.closeBarcodeActivity(barcodeSparseArray);
        Assert.assertTrue(barcodeScanActivity.isFinishing());
    }

    @Test
    public void testReceiveDetections() {
        Assert.assertNotNull(detections);
        Mockito.doReturn(barcodeSparseArray).when(detections).getDetectedItems();
        Assert.assertNotNull(barcodeSparseArray);
        Assert.assertEquals(0, barcodeSparseArray.size());
        Mockito.doReturn(2).when(barcodeSparseArray).size();
        Assert.assertEquals(2, barcodeSparseArray.size());

        barcodeScanActivity.receiveDetections(detections);

        verify(barcodeScanActivity).closeBarcodeActivity(Mockito.eq(barcodeSparseArray));
    }

    private void destroyController() {
        try {
            barcodeScanActivity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), e.getMessage());
        }

        System.gc();
    }
}
