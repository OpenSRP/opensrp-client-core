package org.smartregister.util;

public interface CallableInteractorCallBack<T> {

    void onResult(T t);

    void onError(Exception ex);

}
