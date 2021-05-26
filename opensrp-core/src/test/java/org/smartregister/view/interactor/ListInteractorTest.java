package org.smartregister.view.interactor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.ListContract;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class ListInteractorTest implements Executor {

    private AppExecutors appExecutors = Mockito.spy(new AppExecutors(Mockito.spy(this), Mockito.spy(this), Mockito.spy(this)));

    @Mock
    private ListContract.Presenter<ListContract.Identifiable> presenter;

    private ListInteractor<ListContract.Identifiable> interactor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = new ListInteractor<>(appExecutors);
    }

    @Test
    public void testRunRequest() {
        Callable<List<ListContract.Identifiable>> callable = Mockito.mock(Callable.class);
        AppExecutors.Request request = AppExecutors.Request.DISK_THREAD;

        interactor.runRequest(callable, request, presenter);

        Mockito.verify(appExecutors.diskIO()).execute(Mockito.any());
        Mockito.verify(appExecutors.mainThread()).execute(Mockito.any());
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
