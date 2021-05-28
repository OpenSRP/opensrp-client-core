package org.smartregister.sample;

import android.content.Intent;
import android.os.Bundle;

import org.smartregister.sample.presenter.LoginPresenter;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.activity.NativeECSmartRegisterActivity;
import org.smartregister.view.contract.BaseLoginContract;

/**
 * Created by ndegwamartin on 03/05/2020.
 */
public class SampleLoginActivity extends BaseLoginActivity implements BaseLoginContract.View {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    public void goToHome(boolean isRemote) {
        Intent navigateToRegister = new Intent(this, NativeECSmartRegisterActivity.class);
        startActivity(navigateToRegister);
    }

}
