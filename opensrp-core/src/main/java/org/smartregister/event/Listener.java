package org.smartregister.event;

public interface Listener<T> {
    void onEvent(T data);
}
