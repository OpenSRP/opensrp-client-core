package org.smartregister.util;

import java.util.concurrent.Callable;

public interface CallableInteractor {

    /**
     * This will execute any asyc code using the default request type as AppExecutors.Request.DISK_THREAD
     *
     * @param callable
     * @param callBack
     * @param <T>
     */
    <T> void execute(Callable<T> callable, CallableInteractorCallBack<T> callBack);

    /**
     * This will execute any asyc code using and get returned results
     *
     * @param callable
     * @param callBack
     * @param <T>
     */
    <T> void execute(Callable<T> callable, CallableInteractorCallBack<T> callBack, AppExecutors.Request request);

}
