package org.smartregister.sync.intent;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.smartregister.domain.LocationProperty;
import org.smartregister.sync.helper.LocationServiceHelper;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.PropertiesConverter;

public class LocationIntentService extends BaseSyncIntentService {
    private static final String TAG = "LocationIntentService";
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();

    public LocationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        LocationServiceHelper locationServiceHelper = LocationServiceHelper.getInstance();

        locationServiceHelper.fetchLocationsStructures();

    }
}