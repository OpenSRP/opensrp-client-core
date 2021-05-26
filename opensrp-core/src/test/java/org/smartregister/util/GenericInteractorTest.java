package org.smartregister.util;

import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.BaseUnitTest;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class GenericInteractorTest extends BaseUnitTest implements Executor {

    private GenericInteractor interactor = new GenericInteractor(new AppExecutors(this, this, this));

    @Test
    public void testDefaultExecuteFunction() {
        interactor = Mockito.spy(interactor);

        final String test = "Test String";

        Callable<String> callable = () -> test;

        CallableInteractorCallBack<String> callBack = Mockito.mock(CallableInteractorCallBack.class);

        interactor.execute(callable, callBack);
        Mockito.verify(interactor).execute(callable, callBack, AppExecutors.Request.DISK_THREAD);
        Mockito.verify(callBack).onResult(test);

        // test error
        Exception exception = new IllegalStateException("Exception");
        Callable<String> errorCall = () -> {
            throw exception;
        };
        interactor.execute(errorCall, callBack);
        Mockito.verify(callBack).onError(exception);

    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
