package org.smartregister.presenter;

import android.app.Activity;
import android.content.res.Resources;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.login.presenter.BaseLoginPresenter;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Vincent Karuri on 10/03/2020
 */
public class BaseLoginPresenterTest {

    BaseLoginPresenter presenter;

    @Mock
    private WeakReference<BaseLoginContract.View> mLoginView;

    @Mock
    private BaseLoginContract.View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new TestLoginPresenter();
        Mockito.doReturn(view).when(mLoginView).get();
        Whitebox.setInternalState(presenter, "mLoginView", mLoginView);
    }

    @Test
    public void testAttemptLoginShouldFailForUnauthorizedApp() {
        Mockito.doReturn(false).when(view).isAppVersionAllowed();

        Activity context = mock(Activity.class);
        Resources resources = mock(Resources.class);
        doReturn(context).when(view).getActivityContext();
        doReturn(resources).when(context).getResources();
        doReturn("string").when(resources).getString(anyInt());

        presenter.attemptLogin("", "".toCharArray());
        verify(view).showErrorDialog(any());
    }
}
