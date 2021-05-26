package org.smartregister.view.controller;

import com.google.gson.Gson;

import org.smartregister.service.ANMService;
import org.smartregister.util.Cache;
import org.smartregister.util.CacheableData;
import org.smartregister.view.contract.HomeContext;

public class ANMController {
    private static final String HOME_CONTEXT = "homeContext";
    private static final String NATIVE_HOME_CONTEXT = "nativeHomeContext";
    private final ANMService anmService;
    private final Cache<String> cache;
    private final Cache<HomeContext> nativeCache;

    public ANMController(ANMService anmService, Cache<String> cache, Cache<HomeContext>
            homeContextCache) {
        this.anmService = anmService;
        this.cache = cache;
        this.nativeCache = homeContextCache;
    }

    public String get() {
        return cache.get(HOME_CONTEXT, new CacheableData<String>() {
            @Override
            public String fetch() {
                return new Gson().toJson(new HomeContext(anmService.fetchDetails()));
            }
        });
    }

    public HomeContext getHomeContext() {
        return nativeCache.get(NATIVE_HOME_CONTEXT, new CacheableData<HomeContext>() {
            @Override
            public HomeContext fetch() {
                return new HomeContext(anmService.fetchDetails());
            }
        });
    }
}
