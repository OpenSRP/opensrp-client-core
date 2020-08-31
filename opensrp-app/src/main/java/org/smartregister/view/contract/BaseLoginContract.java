package org.smartregister.view.contract;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public interface BaseLoginContract {
    interface Presenter {

        void attemptLogin(String username, String password);

        View getLoginView();

        void onDestroy(boolean isChangingConfiguration);

        boolean isUserLoggedOut();

        void processViewCustomizations();

        void positionViews();

        void setLanguage();

        org.smartregister.Context getOpenSRPContext();

        boolean isServerSettingsSet();
    }

    interface View {

        void setUsernameError(int resourceId);

        void resetUsernameError();

        void setPasswordError(int resourceId);

        void resetPaswordError();

        void showProgress(final boolean show);

        void updateProgressMessage(final String message);

        void hideKeyboard();

        void showErrorDialog(String message);

        void enableLoginButton(boolean isClickable);

        void goToHome(boolean isRemote);

        Activity getActivityContext();

        @NonNull
        AppCompatActivity getAppCompatActivity();

        boolean isAppVersionAllowed();

        void showClearDataDialog(@NonNull DialogInterface.OnClickListener onClickListener);
    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void login(WeakReference<View> view, String userName, String password);
    }

    interface Model {

        boolean isEmptyUsername(String username);

        boolean isPasswordValid(String password);

        org.smartregister.Context getOpenSRPContext();

        boolean isUserLoggedOut();

    }
}
