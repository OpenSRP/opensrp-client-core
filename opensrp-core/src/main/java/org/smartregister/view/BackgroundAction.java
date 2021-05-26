package org.smartregister.view;

public interface BackgroundAction<T> {
    T actionToDoInBackgroundThread();

    void postExecuteInUIThread(T result);
}
