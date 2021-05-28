package org.smartregister.login.presenter;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.security.SecurityHelper;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;
import java.util.Locale;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by ndegwamartin on 22/06/2018.
 */
public abstract class BaseLoginPresenter implements BaseLoginContract.Presenter {

    protected WeakReference<BaseLoginContract.View> mLoginView;
    protected BaseLoginContract.Interactor mLoginInteractor;
    protected BaseLoginContract.Model mLoginModel;

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        mLoginView = null;//set to null on destroy
        // Inform interactor
        mLoginInteractor.onDestroy(isChangingConfiguration);
        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            mLoginInteractor = null;
            mLoginModel = null;
        }

    }

    @Override
    public void attemptLogin(String username, char[] password) {
        if (!mLoginView.get().isAppVersionAllowed()) {
            getLoginView().showErrorDialog(getLoginView()
                    .getActivityContext().getResources().getString(R.string.outdated_app));
            return;
        }

        // Reset errors.
        getLoginView().resetUsernameError();
        getLoginView().resetPaswordError();

        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (!mLoginModel.isPasswordValid(password)) {
            getLoginView().setPasswordError(R.string.error_invalid_password);
            cancel = true;
        }

        // Check for a valid username
        if (mLoginModel.isEmptyUsername(username)) {
            getLoginView().setUsernameError(R.string.error_field_required);
            cancel = true;
            getLoginView().enableLoginButton(true);
        }

        if (!cancel) {
            mLoginInteractor.login(mLoginView, username.trim(), password);

        }
    }

    @Override
    public BaseLoginContract.View getLoginView() {
        if (mLoginView != null) {
            return mLoginView.get();
        } else {
            return null;
        }
    }

    @Override
    public boolean isUserLoggedOut() {
        return mLoginModel.isUserLoggedOut();
    }

    @Override
    public void positionViews() {
        final ScrollView canvasSV = getLoginView().getActivityContext().findViewById(R.id.canvasSV);
        canvasSV.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                canvasGlobalLayoutListenerProcessor(canvasSV, this);
            }

        });
    }

    public void canvasGlobalLayoutListenerProcessor(ScrollView canvasSV, ViewTreeObserver.OnGlobalLayoutListener layoutListener) {
        final RelativeLayout canvasRL = getLoginView().getActivityContext().findViewById(R.id.login_layout);
        final LinearLayout logoCanvasLL = getLoginView().getActivityContext().findViewById(R.id.bottom_section);
        final LinearLayout credentialsCanvasLL = getLoginView().getActivityContext().findViewById(R.id.middle_section);

        canvasSV.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);

        int windowHeight = canvasSV.getHeight();
        int topMargin = (windowHeight / 2)
                - (credentialsCanvasLL.getHeight() / 2)
                - logoCanvasLL.getHeight();
        topMargin = topMargin / 2;

        RelativeLayout.LayoutParams logoCanvasLP = (RelativeLayout.LayoutParams) logoCanvasLL.getLayoutParams();
        logoCanvasLP.setMargins(0, topMargin, 0, 0);
        logoCanvasLL.setLayoutParams(logoCanvasLP);

        canvasRL.setMinimumHeight(windowHeight);
    }

    @Override
    public abstract void processViewCustomizations();

    @Override
    public void setLanguage() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(mLoginModel.getOpenSRPContext()
                .applicationContext()));
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        Resources resources = mLoginModel.getOpenSRPContext().applicationContext().getResources();

        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(preferredLocale);
        resources.updateConfiguration(configuration, displayMetrics);
    }

    @Override
    public char[] getPassword() {
        return SecurityHelper.readValue(getLoginView().getPasswordEditText().getText());
    }

    @Override
    public Context getOpenSRPContext() {
        return mLoginModel.getOpenSRPContext();
    }

    @Override
    public abstract boolean isServerSettingsSet();

    //for testing only, setter methods instead?
    public void setLoginInteractor(BaseLoginContract.Interactor mLoginInteractor) {
        this.mLoginInteractor = mLoginInteractor;
    }

    public void setLoginModel(BaseLoginContract.Model mLoginModel) {
        this.mLoginModel = mLoginModel;

    }

    public void setLoginView(WeakReference<BaseLoginContract.View> mLoginView) {
        this.mLoginView = mLoginView;
    }

    public String getJsonViewFromPreference(String viewKey) {
        return getDefaultSharedPreferences(getLoginView().getActivityContext()).getString(viewKey, null);
    }
}
