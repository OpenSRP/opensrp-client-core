package org.smartregister.view;

import android.app.Dialog;
import android.widget.ListView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.location.helper.LocationHelper;

import java.util.ArrayList;

/**
 * Created by Vincent Karuri on 06/10/2020
 */
public class LocationPickerViewTest extends BaseRobolectricUnitTest {

    private final String defaultLocation = "default_location";
    private final String advancedLocation = "advanced_location";

    private LocationPickerView locationPickerView;

    @Before
    public void setUp() throws Exception {
        locationPickerView = new LocationPickerView(RuntimeEnvironment.application);
    }

    @Test
    public void initShouldCorrectlyInitializeLocationPicker() {
        if (LocationHelper.getInstance() != null) {
            ReflectionHelpers.setField(LocationHelper.getInstance(), "instance", null);
        }

        LocationPickerView.OnLocationChangeListener onLocationChangeListener = Mockito.mock(LocationPickerView.OnLocationChangeListener.class);
        CoreLibrary.getInstance().context().allSharedPreferences().saveCurrentLocality(defaultLocation);
        LocationHelper.init(new ArrayList<String>() {{ add(defaultLocation); }}, defaultLocation,
                new ArrayList<String>() {{ add(advancedLocation); }});
        ReflectionHelpers.setField(LocationHelper.getInstance(), "defaultLocation", defaultLocation);

        locationPickerView.init();
        locationPickerView.setOnLocationChangeListener(onLocationChangeListener);
        locationPickerView.performClick();

        // default location
        Dialog locationPickerDialog = ShadowDialog.getLatestDialog();
        Assert.assertNotNull(locationPickerDialog);
        verifyCurrentLocationDetailsAreUpdated(locationPickerDialog.findViewById(R.id.locations_lv), 0, defaultLocation, AllConstants.DATA_CAPTURE_STRATEGY.NORMAL);
        Assert.assertEquals(defaultLocation, locationPickerView.getText());
        Mockito.verify(onLocationChangeListener).onLocationChange(ArgumentMatchers.eq(defaultLocation));
        Assert.assertFalse(locationPickerDialog.isShowing());

        // advanced location
        locationPickerView.performClick();
        locationPickerDialog = ShadowDialog.getLatestDialog();
        Assert.assertNotNull(locationPickerDialog);
        Assert.assertNotNull(locationPickerView.getServiceLocationsAdapter());
        verifyCurrentLocationDetailsAreUpdated(locationPickerDialog.findViewById(R.id.locations_lv), 1, advancedLocation, AllConstants.DATA_CAPTURE_STRATEGY.ADVANCED);
        Assert.assertEquals(advancedLocation, locationPickerView.getText());
        Mockito.verify(onLocationChangeListener).onLocationChange(ArgumentMatchers.eq(advancedLocation));
        Assert.assertFalse(locationPickerDialog.isShowing());
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);
    }

    private void verifyCurrentLocationDetailsAreUpdated(ListView listView, int position, String currentLocation, String currentStrategy) {
        listView.performItemClick(null, position, listView.getItemIdAtPosition(position));
        Assert.assertEquals(currentLocation, CoreLibrary.getInstance().context().allSharedPreferences().fetchCurrentLocality());
        Assert.assertEquals(currentStrategy, CoreLibrary.getInstance().context().allSharedPreferences().fetchCurrentDataStrategy());
    }
}