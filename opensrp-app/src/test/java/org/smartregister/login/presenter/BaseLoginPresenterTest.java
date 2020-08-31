package org.smartregister.login.presenter;

import android.content.res.Configuration;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.R;
import org.smartregister.login.model.BaseLoginModel;
import org.smartregister.view.activity.BaseLoginActivityTest.BaseLoginActivityImpl;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 8/18/20.
 */
public class BaseLoginPresenterTest extends BaseRobolectricUnitTest {

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private BaseLoginPresenter presenter;

    @Mock
    private BaseLoginContract.View loginView;
    @Mock
    private BaseLoginContract.Interactor loginInteractor;
    @Spy
    private BaseLoginContract.Model loginModel = new BaseLoginModel();
    @Mock
    private ViewTreeObserver.OnGlobalLayoutListener layoutListener;

    private AppCompatActivity activity;

    @Before
    public void setUp() {
        presenter.setLoginModel(loginModel);
        presenter.setLoginInteractor(loginInteractor);
        presenter.setLoginView(new WeakReference<>(loginView));
        activity = Robolectric.buildActivity(BaseLoginActivityImpl.class).create().get();
        when(loginView.getActivityContext()).thenReturn(activity);
    }

    @Test
    public void testOnDestroyShouldCleanUp() {
        presenter.onDestroy(false);
        verify(loginInteractor).onDestroy(false);
        assertNull(presenter.getLoginView());
        assertNull(Whitebox.getInternalState(presenter, "mLoginInteractor"));
        assertNull(Whitebox.getInternalState(presenter, "mLoginModel"));
    }

    @Test
    public void testGetLoginViewShouldReturnView() {
        assertEquals(loginView, presenter.getLoginView());
    }

    @Test
    public void testAttemptLoginShouldErrorIfAppIsOutdated() {
        when(loginView.isAppVersionAllowed()).thenReturn(false);
        presenter.attemptLogin("john", "doe");
        verify(loginView).showErrorDialog(activity.getString(R.string.outdated_app));
        verify(loginView).isAppVersionAllowed();
        verify(loginView).getActivityContext();
        verifyNoMoreInteractions(loginView);
    }


    @Test
    public void testAttemptLoginShouldNotInvokeLoginAndDisplaysErrors() {
        when(loginView.isAppVersionAllowed()).thenReturn(true);
        presenter.attemptLogin("", "");
        verify(loginView).setPasswordError(R.string.error_invalid_password);
        verify(loginView).setUsernameError(R.string.error_field_required);
        verify(loginView).enableLoginButton(true);
        verify(loginInteractor, never()).login(any(), any(), any());
    }

    @Test
    public void testAttemptLoginShouldInvokeLogin() {
        when(loginView.isAppVersionAllowed()).thenReturn(true);
        presenter.attemptLogin("john", "doe");
        verify(loginView, never()).setPasswordError(R.string.error_invalid_password);
        verify(loginView, never()).setUsernameError(R.string.error_field_required);
        verify(loginView, never()).enableLoginButton(true);
        verify(loginInteractor).login(any(), eq("john"), eq("doe"));
    }

    @Test
    public void testIsUserLoggedOutShouldReturnModelValue() {
        assertTrue(presenter.isUserLoggedOut());
        verify(loginModel).isUserLoggedOut();
    }

    @Test
    public void testPositionViewsAndCanvasGlobalLayoutListenerProcessor() {
        presenter.positionViews();
        final ScrollView canvasSV = loginView.getActivityContext().findViewById(R.id.canvasSV);
        presenter.canvasGlobalLayoutListenerProcessor(canvasSV, layoutListener);

        RelativeLayout view = loginView.getActivityContext().findViewById(R.id.login_layout);
        assertEquals(0, view.getMinimumHeight());
    }

    @Test
    public void testSetLanguage() {
        presenter.setLanguage();
        Configuration config = activity.getResources().getConfiguration();
        assertEquals(1, config.getLocales().size());
        assertEquals("en", config.getLocales().get(0).getLanguage());
    }

    @Test
    public void testGetJsonViewFromPreference() {
        getDefaultSharedPreferences(loginView.getActivityContext()).edit().putString("asdsa", "232").commit();
        assertEquals("232", presenter.getJsonViewFromPreference("asdsa"));
    }
}
