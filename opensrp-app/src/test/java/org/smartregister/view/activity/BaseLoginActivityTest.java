package org.smartregister.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.util.SyncUtils;
import org.smartregister.view.contract.BaseLoginContract;

import static org.robolectric.util.ReflectionHelpers.ClassParameter.from;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 23-06-2020.
 */
public class BaseLoginActivityTest extends BaseRobolectricUnitTest {

    private BaseLoginActivity baseLoginActivity;

    private ActivityController<BaseLoginActivityImpl> controller;

    @BeforeClass
    public static void resetCoreLibrary() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Before
    public void setUp() {
        org.mockito.MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(BaseLoginActivityImpl.class);
        BaseLoginActivityImpl spyActivity = Mockito.spy((BaseLoginActivityImpl) ReflectionHelpers.getField(controller, "component"));
        ReflectionHelpers.setField(controller, "component", spyActivity);

        AppCompatDelegate delegate = AppCompatDelegate.create(RuntimeEnvironment.application, spyActivity, spyActivity);
        Mockito.doReturn(delegate).when(spyActivity).getDelegate();

        ActionBar actionBar = Mockito.mock(ActionBar.class);
        Mockito.doReturn(actionBar).when(spyActivity).getSupportActionBar();

        Mockito.doReturn(RuntimeEnvironment.application.getPackageManager()).when(spyActivity).getPackageManager();

        controller.create()
                .start()
                .resume();
        baseLoginActivity = Mockito.spy(controller.get());
    }

    @After
    public void tearDown() {
        resetCoreLibrary();
    }

    @Test
    public void onCreateShouldCallSetupOperations() {
        // Setup again for
        controller = Robolectric.buildActivity(BaseLoginActivityImpl.class);
        ActionBar actionBar = Mockito.mock(ActionBar.class);

        BaseLoginActivityImpl spyActivity = Mockito.spy((BaseLoginActivityImpl) ReflectionHelpers.getField(controller, "component"));
        ReflectionHelpers.setField(controller, "component", spyActivity);

        AppCompatDelegate delegate = AppCompatDelegate.create(RuntimeEnvironment.application, spyActivity, spyActivity);
        Mockito.doReturn(delegate).when(spyActivity).getDelegate();

        Mockito.doReturn(RuntimeEnvironment.application.getPackageManager()).when(spyActivity).getPackageManager();
        Mockito.doReturn(actionBar).when(spyActivity).getSupportActionBar();
        baseLoginActivity = controller.get();


        ReflectionHelpers.callInstanceMethod(Activity.class, baseLoginActivity, "performCreate", from(Bundle.class, null));

        Mockito.verify(actionBar).setDisplayShowHomeEnabled(Mockito.eq(false));
        Mockito.verify(actionBar).setDisplayShowTitleEnabled(Mockito.eq(false));
        Mockito.verify(actionBar).setBackgroundDrawable(Mockito.any(Drawable.class));

        Mockito.verify(baseLoginActivity.mLoginPresenter).setLanguage();
        Mockito.verify(baseLoginActivity).initializePresenter();
        Mockito.verify(baseLoginActivity.mLoginPresenter).positionViews();
        Mockito.verify(baseLoginActivity).renderBuildInfo();
        Assert.assertNotNull((SyncUtils) ReflectionHelpers.getField(baseLoginActivity, "syncUtils"));
        Assert.assertNotNull((ProgressDialog) ReflectionHelpers.getField(baseLoginActivity, "progressDialog"));
    }

    @Test
    public void onClickLoginShouldCallAttemptLogin() {
        View view = Mockito.mock(View.class);
        Mockito.doReturn(R.id.login_login_btn).when(view).getId();

        baseLoginActivity.onClick(view);
        Mockito.verify(baseLoginActivity.mLoginPresenter).attemptLogin(Mockito.anyString(), Mockito.any(char[].class));
    }


    @Test
    public void onEditorActionShouldShouldCallAttemptLoginAndReturnTrueWhenActionIsDone() {
        View view = Mockito.mock(View.class);
        Mockito.doReturn(R.id.login_login_btn).when(view).getId();

        Assert.assertTrue(baseLoginActivity.onEditorAction(null, EditorInfo.IME_ACTION_DONE, null));
        Mockito.verify(baseLoginActivity.mLoginPresenter).attemptLogin(Mockito.anyString(), Mockito.any(char[].class));
    }

    @Test
    public void enableLoginButtonShouldMakeLoginBtnClickable() {
        boolean isClickable = false;
        baseLoginActivity.enableLoginButton(isClickable);
        Button btn = ReflectionHelpers.getField(baseLoginActivity, "loginButton");
        Assert.assertFalse(btn.isClickable());
    }

    @Test
    public void setUsernameErrorShouldCallSetErrorAndShowErrorDialog() {
        baseLoginActivity.setUsernameError(R.string.error_invalid_username);

        EditText usernameEt = ReflectionHelpers.getField(baseLoginActivity, "userNameEditText");

        Assert.assertEquals(RuntimeEnvironment.application.getString(R.string.error_invalid_username), usernameEt.getError());
        Mockito.verify(baseLoginActivity).showErrorDialog(Mockito.eq(RuntimeEnvironment.application.getString(R.string.unauthorized)));
    }

    @Test
    public void onCreateOptionsShouldReturnTrueAndPopulateMenu() {
        Menu menu = Mockito.mock(Menu.class);

        Assert.assertTrue(baseLoginActivity.onCreateOptionsMenu(menu));
        Mockito.verify(menu).add(Mockito.eq("Settings"));
    }

    @Test
    public void onOptionsItemSelectedShouldReturnTrueAndCallStartActivityWhenSettingsIsClicked() {
        MenuItem menuItem = Mockito.mock(MenuItem.class);
        Mockito.doReturn("Settings").when(menuItem).getTitle();

        Assert.assertTrue(baseLoginActivity.onOptionsItemSelected(menuItem));
        Intent intent = Shadows.shadowOf(baseLoginActivity).peekNextStartedActivity();
        Assert.assertEquals(SettingsActivity.class.getName(), intent.getComponent().getClassName());
    }

    @Test
    public void onDestroyShouldCallPresenterOnDestroy() {
        baseLoginActivity.onDestroy();
        Mockito.verify(baseLoginActivity.mLoginPresenter).onDestroy(Mockito.eq(false));
    }

    @Test
    public void setPasswordErrorShouldCallSetErrorAndShowErrorDialog() {
        baseLoginActivity.setPasswordError(R.string.error_invalid_password);

        EditText passwordEditText = ReflectionHelpers.getField(baseLoginActivity, "passwordEditText");

        Assert.assertEquals(RuntimeEnvironment.application.getString(R.string.error_invalid_password), passwordEditText.getError());
        Mockito.verify(baseLoginActivity).showErrorDialog(Mockito.eq(RuntimeEnvironment.application.getString(R.string.unauthorized)));
    }

    @Test
    public void isAppVersionAllowedShouldReturnSyncUtilsValue() throws PackageManager.NameNotFoundException {
        SyncUtils syncUtils = Mockito.spy((SyncUtils) ReflectionHelpers.getField(baseLoginActivity, "syncUtils"));
        ReflectionHelpers.setField(baseLoginActivity, "syncUtils", syncUtils);
        Mockito.doReturn(false).when(syncUtils).isAppVersionAllowed();

        Assert.assertFalse(baseLoginActivity.isAppVersionAllowed());
    }

    @Test
    public void showClearDataDialog() {
        baseLoginActivity.showClearDataDialog(Mockito.mock(DialogInterface.OnClickListener.class));

        AlertDialog alertDialog = (AlertDialog) ShadowDialog.getLatestDialog();
        Object alertDialogController = ReflectionHelpers.getField(alertDialog, "mAlert");
        Assert.assertNotNull(alertDialog);
        Assert.assertEquals("Do you want to clear data to login with a different team/location", ReflectionHelpers.getField(alertDialogController, "mTitle"));
        Assert.assertFalse(ReflectionHelpers.getField(alertDialog, "mCancelable"));
    }

    @Test
    public void testShowProgressShouldExecuteWhenActivityIsActive() {
        baseLoginActivity.showProgress(true);
        ProgressDialog progressDialog = ReflectionHelpers.getField(baseLoginActivity, "progressDialog");
        Assert.assertTrue(progressDialog.isShowing());
    }

    @Test
    public void testShowProgressShouldNotExecuteWhenActivityIsDestroyed() {
        ProgressDialog spyProgressDialog = Mockito.spy(new ProgressDialog(baseLoginActivity));
        ReflectionHelpers.setField(baseLoginActivity, "progressDialog", spyProgressDialog);
        baseLoginActivity.finish();
        baseLoginActivity.showProgress(true);
        Mockito.verify(spyProgressDialog, Mockito.never()).show();
    }

    @Test
    public void testUpdateProgressMessageShouldExecuteWhenActivityIsActive() {
        String msg = "text";
        ProgressDialog spyProgressDialog = Mockito.spy(new ProgressDialog(baseLoginActivity));
        ReflectionHelpers.setField(baseLoginActivity, "progressDialog", spyProgressDialog);
        baseLoginActivity.updateProgressMessage(msg);
        Mockito.verify(spyProgressDialog, Mockito.times(1)).setTitle(Mockito.eq(msg));
    }

    @Test
    public void testUpdateProgressMessageShouldNotExecuteWhenActivityIsDestroyed() {
        String msg = "text";
        ProgressDialog spyProgressDialog = Mockito.spy(new ProgressDialog(baseLoginActivity));
        ReflectionHelpers.setField(baseLoginActivity, "progressDialog", spyProgressDialog);
        baseLoginActivity.finish();
        baseLoginActivity.updateProgressMessage(msg);
        Mockito.verify(spyProgressDialog, Mockito.never()).setTitle(Mockito.eq(msg));
    }

    public static class BaseLoginActivityImpl extends BaseLoginActivity {

        @Override
        protected int getContentView() {
            return R.layout.activity_login;
        }

        @Override
        protected void initializePresenter() {
            mLoginPresenter = Mockito.mock(BaseLoginContract.Presenter.class);

        }

        @Override
        public void goToHome(boolean isRemote) {

        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar); //we need this here
            super.onCreate(savedInstanceState);
        }
    }

}