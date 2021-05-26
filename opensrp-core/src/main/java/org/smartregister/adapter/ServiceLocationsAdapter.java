package org.smartregister.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.R;
import org.smartregister.location.helper.LocationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jason Rogena - jrogena@ona.io
 * @since 03/03/2017
 */
public class ServiceLocationsAdapter extends BaseAdapter {
    private final List<String> locationNames;
    private final HashMap<String, View> views;
    private final Context context;
    private String selectedLocation;

    public ServiceLocationsAdapter(Context context, List<String> locationNames) {
        this.context = context;
        this.locationNames = locationNames == null ? new ArrayList<>() : locationNames;
        this.views = new HashMap<>();
    }

    @Override
    public int getCount() {
        return locationNames.size();
    }

    @Override
    public Object getItem(int position) {
        return views.get(locationNames.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position + 2321;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (!views.containsKey(locationNames.get(position))) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.location_picker_dropdown_item, null);
            view.setId(position + 2321);

            TextView text1 = view.findViewById(android.R.id.text1);
            text1.setText(LocationHelper.getInstance().getOpenMrsReadableName(locationNames.get(position)));
            views.put(locationNames.get(position), view);
        }

        refreshView(views.get(locationNames.get(position)),
                locationNames.get(position).equals(selectedLocation));

        return views.get(locationNames.get(position));
    }

    public void setSelectedLocation(final String locationName) {
        selectedLocation = locationName;
        if (locationName != null
                && locationNames.contains(locationName)
                && views.containsKey(locationName)) {
            for (String curLocation : locationNames) {
                View view = views.get(curLocation);
                refreshView(view, curLocation.equals(locationName));
            }
        }
    }

    private void refreshView(View view, boolean selected) {
        if (selected) {
            ImageView checkbox = view.findViewById(R.id.checkbox);
            checkbox.setVisibility(View.VISIBLE);
        } else {
            ImageView checkbox = view.findViewById(R.id.checkbox);
            checkbox.setVisibility(View.INVISIBLE);
        }
    }

    public String getLocationAt(int position) {
        return locationNames.get(position);
    }

    public List<String> getLocationNames() {
        return locationNames;
    }
}
