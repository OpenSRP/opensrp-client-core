package org.smartregister.util;

import org.smartregister.domain.FetchStatus;
import org.smartregister.event.CapturedPhotoInformation;
import org.smartregister.event.Listener;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static java.text.MessageFormat.format;
import static org.smartregister.domain.FetchStatus.fetched;
import static org.smartregister.event.Event.ACTION_HANDLED;
import static org.smartregister.event.Event.FORM_SUBMITTED;
import static org.smartregister.event.Event.ON_DATA_FETCHED;
import static org.smartregister.event.Event.ON_PHOTO_CAPTURED;

public class Cache<T> {
    private final Listener<String> formSubmittedListener;
    private final Listener<FetchStatus> actionsFetchedListener;
    private final Listener<CapturedPhotoInformation> photoCapturedListener;
    private final Listener<String> actionHandledListener;
    private Map<String, T> value = new HashMap<String, T>();

    public Cache() {
        actionsFetchedListener = new Listener<FetchStatus>() {
            @Override
            public void onEvent(FetchStatus data) {
                if (fetched.equals(data)) {
                    Timber.i("List cache invalidated as new data was fetched from server.");
                    value.clear();
                }
            }
        };
        formSubmittedListener = new Listener<String>() {
            @Override
            public void onEvent(String reason) {
                Timber.i(format("List cache invalidated: {0}.", reason));
                value.clear();
            }
        };
        photoCapturedListener = new Listener<CapturedPhotoInformation>() {
            @Override
            public void onEvent(CapturedPhotoInformation data) {
                value.clear();
            }
        };
        actionHandledListener = new Listener<String>() {
            @Override
            public void onEvent(String data) {
                Timber.i(format("List cache invalidated as Action handled: {0}", data));
                value.clear();
            }
        };
        ON_DATA_FETCHED.addListener(actionsFetchedListener);
        FORM_SUBMITTED.addListener(formSubmittedListener);
        ON_PHOTO_CAPTURED.addListener(photoCapturedListener);
        ACTION_HANDLED.addListener(actionHandledListener);
    }

    public T get(String key, CacheableData<T> cacheableData) {
        if (value.get(key) != null) {
            return value.get(key);
        }
        T fetchedData = cacheableData.fetch();
        value.put(key, fetchedData);
        return fetchedData;
    }

    public void evict(String key) {
        if (value.get(key) != null) {
            value.remove(key);
        }
    }
}

