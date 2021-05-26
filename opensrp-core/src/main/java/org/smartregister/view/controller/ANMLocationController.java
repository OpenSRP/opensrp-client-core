package org.smartregister.view.controller;

import com.google.gson.Gson;

import org.smartregister.repository.AllSettings;
import org.smartregister.util.Cache;
import org.smartregister.util.CacheableData;
import org.smartregister.view.contract.ANMLocation;

public class ANMLocationController {
    private static final String ANM_LOCATION = "anmLocation";
    private static final String ANM_LOCATION_JSON = "anmLocationJSON";
    private AllSettings allSettings;
    private Cache<String> cache;

    public ANMLocationController(AllSettings allSettings, Cache<String> cache) {
        this.allSettings = allSettings;
        this.cache = cache;
    }

    public String get() {
        return cache.get(ANM_LOCATION, new CacheableData<String>() {
            @Override
            public String fetch() {
                return allSettings.fetchANMLocation();
            }
        });
    }

    public void evict() {
        cache.evict(ANM_LOCATION);
    }

    public String getLocationJSON() {
        return cache.get(ANM_LOCATION_JSON, new CacheableData<String>() {
            @Override
            public String fetch() {
                return new Gson().fromJson(allSettings.fetchANMLocation(), ANMLocation.class)
                        .asJSONString();
            }
        });
    }
}
