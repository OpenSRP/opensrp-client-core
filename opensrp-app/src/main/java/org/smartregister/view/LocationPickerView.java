package org.smartregister.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.adapter.ServiceLocationsAdapter;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jason Rogena - jrogena@ona.io
 * @since 03/03/2017
 */
public class LocationPickerView extends CustomFontTextView implements View.OnClickListener {

    private final Context context;
    private Dialog locationPickerDialog;
    private ServiceLocationsAdapter serviceLocationsAdapter;
    private OnLocationChangeListener onLocationChangeListener;

    public LocationPickerView(Context context) {
        this(context, null);
    }

    public LocationPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.org_ei_drishti_view_customControls_CustomFontTextView, 0, defStyle);
            //int variant = attributes.getInt(R.styleable.anc_CustomFontTextView_fontVariant, 0);
            attributes.recycle();
            //setFontVariant(variant);
        }
    }

    public void init() {
        locationPickerDialog = new Dialog(context);
        locationPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        locationPickerDialog.setContentView(R.layout.location_picker_dialog);

        ListView locationsLV = locationPickerDialog.findViewById(R.id.locations_lv);

        String defaultLocation = LocationHelper.getInstance().getDefaultLocation();

        Set<String> uniqueLocations = new HashSet<>(LocationHelper.getInstance().locationNamesFromHierarchy(defaultLocation));

        List<String> advancedStrategies = LocationHelper.getInstance().getAdvancedDataCaptureStrategies();
        boolean hasAdvancedDataStrategies = advancedStrategies != null && advancedStrategies.size() > 0;
        if (hasAdvancedDataStrategies) {
            uniqueLocations.addAll(advancedStrategies);
        }

        List<String> sortedLocations = new ArrayList<>(uniqueLocations);
        sortedLocations.remove(defaultLocation);
        Collections.sort(sortedLocations);
        sortedLocations.add(0, defaultLocation);

        serviceLocationsAdapter = new ServiceLocationsAdapter(context, sortedLocations);
        locationsLV.setAdapter(serviceLocationsAdapter);
        locationsLV.setOnItemClickListener((parent, view, position, id) -> {
            CoreLibrary.getInstance().context().allSharedPreferences().saveCurrentLocality(serviceLocationsAdapter.getLocationAt(position));
            CoreLibrary.getInstance().context().allSharedPreferences().saveCurrentDataStrategy(hasAdvancedDataStrategies &&
                    advancedStrategies.contains(serviceLocationsAdapter.getLocationAt(position)) ? AllConstants.DATA_CAPTURE_STRATEGY.ADVANCED : AllConstants.DATA_CAPTURE_STRATEGY.NORMAL);
            LocationPickerView.this.setText(LocationHelper.getInstance().getOpenMrsReadableName(serviceLocationsAdapter.getLocationAt(position)));
            if (onLocationChangeListener != null) {
                onLocationChangeListener.onLocationChange(serviceLocationsAdapter.getLocationAt(position));
            }
            locationPickerDialog.dismiss();
        });
        this.setText(LocationHelper.getInstance().getOpenMrsReadableName(getSelectedItem()));

        setClickable(true);
        setOnClickListener(this);
    }

    public String getSelectedItem() {
        String selectedLocation = CoreLibrary.getInstance().context().allSharedPreferences().fetchCurrentLocality();
        if (TextUtils.isEmpty(selectedLocation) || !serviceLocationsAdapter.getLocationNames().contains(selectedLocation)) {
            selectedLocation = LocationHelper.getInstance().getDefaultLocation();
            CoreLibrary.getInstance().context().allSharedPreferences().saveCurrentLocality(selectedLocation);
            CoreLibrary.getInstance().context().allSharedPreferences().saveCurrentDataStrategy(AllConstants.DATA_CAPTURE_STRATEGY.NORMAL);
        }
        return selectedLocation;
    }

    public void setOnLocationChangeListener(final OnLocationChangeListener onLocationChangeListener) {
        this.onLocationChangeListener = onLocationChangeListener;
    }

    @Override
    public void onClick(View v) {
        showDialog();
    }

    private void showDialog() {
        serviceLocationsAdapter.setSelectedLocation(getSelectedItem());

        Window window = locationPickerDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        int[] coords = new int[2];
        LocationPickerView.this.getLocationInWindow(coords);

        locationPickerDialog.show();
    }

    public interface OnLocationChangeListener {
        void onLocationChange(String newLocation);
    }

    public ServiceLocationsAdapter getServiceLocationsAdapter() {
        return serviceLocationsAdapter;
    }
}
