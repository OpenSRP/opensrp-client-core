package org.smartregister.event;

import org.apache.commons.lang3.ObjectUtils;
import org.smartregister.domain.FetchStatus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class Event<CallbackType> {
    public static final Event<FetchStatus> ON_DATA_FETCHED = new Event<FetchStatus>();
    public static final Event<CapturedPhotoInformation> ON_PHOTO_CAPTURED = new Event<>();
    public static final Event<Boolean> ON_LOGOUT = new Event<>();
    public static final Event<Boolean> SYNC_STARTED = new Event<>();
    public static final Event<Boolean> SYNC_COMPLETED = new Event<>();
    public static final Event<String> FORM_SUBMITTED = new Event<>();
    public static final Event<String> ACTION_HANDLED = new Event<>();

    List<WeakReference<Listener<CallbackType>>> listeners;

    public Event() {
        listeners = new ArrayList<WeakReference<Listener<CallbackType>>>();
    }

    public void addListener(Listener<CallbackType> listener) {
        listeners.add(new WeakReference<Listener<CallbackType>>(listener));
    }

    public void removeListener(Listener<CallbackType> listener) {
        WeakReference<Listener<CallbackType>> listenerToRemove = null;
        for (WeakReference<Listener<CallbackType>> l : listeners) {
            if (ObjectUtils.equals(listener, l.get())) {
                listenerToRemove = l;
                break;
            }
        }
        listeners.remove(listenerToRemove);
    }

    public void notifyListeners(CallbackType data) {
        for (WeakReference<Listener<CallbackType>> listener : listeners) {
            if (listener.get() != null) {
                listener.get().onEvent(data);
            }
        }
    }
}
