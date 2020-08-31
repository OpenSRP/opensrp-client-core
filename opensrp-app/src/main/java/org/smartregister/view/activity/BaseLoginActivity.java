package org.smartregister.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.smartregister.R;
import org.smartregister.util.SyncUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseLoginContract;

import timber.log.Timber;

import static org.smartregister.AllConstants.ACCOUNT_DISABLED;

/**
 * Created by manu on 01/11/2018.
 */

public abstract class BaseLoginActivity extends MultiLanguageActivity implements BaseLoginContract.View, TextView.OnEditorActionListener, View.OnClickListener {
    private ProgressDialog progressDialog;
    protected BaseLoginContract.Presenter mLoginPresenter;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private TextView showPasswordCheckBoxText;
    private CheckBox showPasswordCheckBox;
    private Button loginButton;
    private Boolean showPasswordChecked = false;
    private SyncUtils syncUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.black)));
        initializePresenter();
        mLoginPresenter.setLanguage();
        setupViews(mLoginPresenter);
        syncUtils = new SyncUtils(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase("Settings")) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null) {
            String logoffReason = getIntent().getStringExtra(ACCOUNT_DISABLED);
            if (logoffReason != null) {
                showErrorDialog(R.string.account_disabled, logoffReason);
            }
        }
    }

    protected abstract int getContentView();

    protected abstract void initializePresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLoginPresenter.onDestroy(isChangingConfigurations());
    }

    private void setupViews(BaseLoginContract.Presenter presenter) {
        presenter.positionViews();
        initializeLoginChildViews();
        initializeProgressDialog();
        setListenerOnShowPasswordCheckbox();
        renderBuildInfo();
    }

    private void initializeLoginChildViews() {
        userNameEditText = findViewById(R.id.login_user_name_edit_text);
        passwordEditText = findViewById(R.id.login_password_edit_text);
        showPasswordCheckBox = findViewById(R.id.login_show_password_checkbox);
        showPasswordCheckBoxText = findViewById(R.id.login_show_password_text_view);
        passwordEditText.setOnEditorActionListener(this);
        loginButton = findViewById(R.id.login_login_btn);
        loginButton.setOnClickListener(this);
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.smartregister.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.smartregister.R.string.loggin_in_dialog_message));
    }

    private void setListenerOnShowPasswordCheckbox() {
        showPasswordCheckBoxText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showPasswordChecked) {
                    showPasswordCheckBox.setChecked(true);
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPasswordChecked = false;
                } else {
                    showPasswordCheckBox.setChecked(false);
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPasswordChecked = true;
                }
            }
        });

        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showPasswordChecked = false;
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    showPasswordChecked = true;
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public void showErrorDialog(String message) {
        showErrorDialog(org.smartregister.R.string.login_failed_dialog_title, message);
    }

    public void showErrorDialog(@StringRes int title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();

    }

    public void showProgress(final boolean show) {
        if (show) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void hideKeyboard() {
        Log.i(getClass().getName(), "Hiding Keyboard " + DateTime.now().toString());
        Utils.hideKeyboard(this);
    }

    @Override
    public void enableLoginButton(boolean isClickable) {
        loginButton.setClickable(isClickable);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == R.integer.login || actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_DONE) {
            String username = userNameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            mLoginPresenter.attemptLogin(username, password);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_login_btn) {
            String username = userNameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            mLoginPresenter.attemptLogin(username, password);
        }
    }

    @Override
    public void setUsernameError(int resourceId) {
        userNameEditText.setError(getString(resourceId));
        userNameEditText.requestFocus();
        showErrorDialog(getResources().getString(R.string.unauthorized));
    }

    @Override
    public void resetUsernameError() {
        userNameEditText.setError(null);
    }

    @Override
    public void setPasswordError(int resourceId) {
        passwordEditText.setError(getString(resourceId));
        passwordEditText.requestFocus();
        showErrorDialog(getResources().getString(R.string.unauthorized));
    }

    @Override
    public void resetPaswordError() {
        passwordEditText.setError(null);
    }

    @Override
    public Activity getActivityContext() {
        return this;
    }

    @Override
    public AppCompatActivity getAppCompatActivity() {
        return this;
    }

    @Override
    public void updateProgressMessage(String message) {
        progressDialog.setTitle(message);
    }

    protected void renderBuildInfo() {
        TextView application_version = findViewById(R.id.login_build_text_view);
        if (application_version != null) {
            try {
                application_version.setText(String.format(getString(R.string.app_version), Utils.getVersion(this), Utils.getBuildDate(true)));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isAppVersionAllowed() {
        boolean isAppVersionAllowed = true;
        try {
            isAppVersionAllowed = syncUtils.isAppVersionAllowed();
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }
        return  isAppVersionAllowed;
    }

    @Override
    public void showClearDataDialog(@NonNull DialogInterface.OnClickListener onClickListener) {
        String username = DrishtiApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
        String teamName = DrishtiApplication.getInstance().getContext().allSharedPreferences().fetchDefaultTeam(username);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.clear_data_dialog_title)
                .setMessage(String.format(getString(R.string.clear_data_dialog_message), username, teamName))
                .setPositiveButton(R.string.ok, onClickListener)
                .setNegativeButton(android.R.string.cancel, onClickListener)
                .setCancelable(false)
                .show();
    }
}