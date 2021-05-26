package org.smartregister.view.fragment;

import android.view.View;
import android.widget.ProgressBar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.view.ListContract;

import java.util.concurrent.atomic.AtomicInteger;

public class BaseListFragmentTest {

    private BaseListFragment baseListFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        baseListFragment = Mockito.mock(BaseListFragment.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testSetLoadingStateCalledTwiceHidesViewAfterBeingCalledTwice() {
        ProgressBar progressBar = Mockito.mock(ProgressBar.class);
        Whitebox.setInternalState(baseListFragment, "progressBar", progressBar);

        AtomicInteger incompleteRequests = new AtomicInteger(0);
        Whitebox.setInternalState(baseListFragment, "incompleteRequests", incompleteRequests);
        //
        baseListFragment.setLoadingState(true);
        Assert.assertEquals(incompleteRequests.get(), 1);
        baseListFragment.setLoadingState(true);
        Assert.assertEquals(incompleteRequests.get(), 2);
        baseListFragment.setLoadingState(false);
        Assert.assertEquals(incompleteRequests.get(), 1);

        Mockito.verify(progressBar, Mockito.times(3)).setVisibility(View.VISIBLE);

        baseListFragment.setLoadingState(false);
        Assert.assertEquals(incompleteRequests.get(), 0);
        Mockito.verify(progressBar, Mockito.times(1)).setVisibility(View.INVISIBLE);
    }

    @Test
    public void testLoadPresenterAssignsPresenter() {
        Assert.assertNull(Whitebox.getInternalState(baseListFragment, "presenter"));

        ListContract.Presenter presenter = baseListFragment.loadPresenter();

        Assert.assertNotNull(Whitebox.getInternalState(baseListFragment, "presenter"));
        Assert.assertEquals(presenter, Whitebox.getInternalState(baseListFragment, "presenter"));
    }

}
