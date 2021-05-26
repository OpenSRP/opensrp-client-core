package org.smartregister.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.ListContract;
import org.smartregister.view.presenter.ListPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ListPresenterTest {

    @Mock
    private ListContract.View<ListContract.Identifiable> view;

    @Mock
    private ListContract.Interactor<ListContract.Identifiable> interactor;

    private ListPresenter<ListContract.Identifiable> listPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        listPresenter = new ListPresenter<>();
        Whitebox.setInternalState(listPresenter, "interactor", interactor);
    }

    @Test
    public void testFetchListExecutesFetchOnATread() {
        listPresenter.with(view);
        List<ListContract.Identifiable> testList = new ArrayList<>();
        Callable<List<ListContract.Identifiable>> callable = () -> testList;

        listPresenter.fetchList(callable, AppExecutors.Request.DISK_THREAD);
        Mockito.verify(view).setLoadingState(true);
        Mockito.verify(interactor).runRequest(callable, AppExecutors.Request.DISK_THREAD, listPresenter);
    }

    @Test
    public void testOnFetchRequestError() {
        listPresenter.with(view);
        List<ListContract.Identifiable> identifiables = new ArrayList<>();
        listPresenter.onItemsFetched(identifiables);

        Mockito.verify(view).renderData(identifiables);
        Mockito.verify(view).refreshView();
        Mockito.verify(view).setLoadingState(false);
    }

    @Test
    public void testWithAttachesView() {
        listPresenter.with(view);
        Assert.assertEquals(listPresenter.getView(), view);
    }

    @Test
    public void testUsingInteractor() {
        listPresenter.using(interactor);
        Assert.assertEquals(listPresenter.getInteractor(), interactor);
    }

    @Test
    public void testWithModel() {
        ListContract.Model<ListContract.Identifiable> model = Mockito.mock(ListContract.Model.class);
        listPresenter.withModel(model);
        Assert.assertEquals(listPresenter.getModel(), model);
    }

}
