package org.smartregister.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;

import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.R;
import org.smartregister.adapter.PagerAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.util.AppExecutors;
import org.smartregister.util.PermissionUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseRegisterContract;
import org.smartregister.view.fragment.BaseRegisterFragment;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by keyman on 26/06/2018.
 */

public abstract class BaseRegisterActivity extends SecuredNativeSmartRegisterActivity implements BaseRegisterContract.View {

    protected OpenSRPViewPager mPager;

    protected BaseRegisterContract.Presenter presenter;
    protected BaseRegisterFragment mBaseFragment = null;

    protected String userInitials;

    protected BottomNavigationHelper bottomNavigationHelper;
    protected BottomNavigationView bottomNavigationView;

    private ProgressDialog progressDialog;
    private FragmentPagerAdapter mPagerAdapter;

    protected int currentPage;

    public static int BASE_REG_POSITION;
    public static int ADVANCED_SEARCH_POSITION;
    public static int SORT_FILTER_POSITION;
    public static int LIBRARY_POSITION;
    public static int ME_POSITION;

    private AppExecutors appExecutors = new AppExecutors();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_register);

        mPager = findViewById(R.id.base_view_pager);

        Fragment[] otherFragments = getOtherFragments();

        mBaseFragment = getRegisterFragment();
        mBaseFragment.setArguments(this.getIntent().getExtras());

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mBaseFragment, otherFragments);
        mPager.setOffscreenPageLimit(otherFragments.length);
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

        });
        initializePresenter();
        presenter.updateInitials();


        registerBottomNavigation();
    }

    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().add(Menu.NONE, R.string.action_me, Menu.NONE, R.string.me)
                    .setIcon(bottomNavigationHelper
                            .writeOnDrawable(R.drawable.bottom_bar_initials_background, userInitials, getResources()));
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);

            BottomNavigationListener bottomNavigationListener = new BottomNavigationListener(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy(isChangingConfigurations());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = findFragmentByPosition(currentPage);
        if (fragment instanceof BaseRegisterFragment) {
            setSelectedBottomBarMenuItem(R.id.action_clients);
            BaseRegisterFragment registerFragment = (BaseRegisterFragment) fragment;
            if (registerFragment.onBackPressed()) {
                return;
            }
        }

        if (currentPage == 0) {
            super.onBackPressed();
        } else {
            switchToBaseFragment();
            setSelectedBottomBarMenuItem(R.id.action_clients);
        }
    }

    protected abstract void initializePresenter();

    protected abstract BaseRegisterFragment getRegisterFragment();

    protected abstract Fragment[] getOtherFragments();

    @Override
    public void displaySyncNotification() {
        Snackbar syncStatusSnackbar =
                Snackbar.make(this.getWindow().getDecorView(), R.string.manual_sync_triggered, Snackbar.LENGTH_LONG);
        syncStatusSnackbar.show();
    }

    @Override
    public void displayToast(int resourceId) {
        displayToast(getString(resourceId));
    }

    @Override
    public void displayToast(String message) {
        Utils.showToast(getApplicationContext(), message);
    }

    @Override
    public void displayShortToast(int resourceId) {
        Utils.showShortToast(getApplicationContext(), getString(resourceId));
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void setupViews() {//Implement Abstract Method
    }

    @Override
    protected void onResumption() {
        presenter.registerViewConfigurations(getViewIdentifiers());
    }

    @Override
    protected void onInitialization() {//Implement Abstract Method
    }

    @Override
    public abstract void startFormActivity(String formName, String entityId, Map<String, String> metaData);

    @Override
    public abstract void startFormActivity(JSONObject form);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AllConstants.BARCODE.BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Barcode barcode = data.getParcelableExtra(AllConstants.BARCODE.BARCODE_KEY);
                Timber.d("Scanned QR Code %s", barcode.displayValue);
                mBaseFragment.onQRCodeSucessfullyScanned(barcode.displayValue);
                mBaseFragment.setSearchTerm(barcode.displayValue);
            } else
                Timber.i("NO RESULT FOR QR CODE");
        } else {
            onActivityResultExtended(requestCode, resultCode, data);
        }
    }

    protected abstract void onActivityResultExtended(int requestCode, int resultCode, Intent data);

    public void refreshList(final FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            BaseRegisterFragment registerFragment = (BaseRegisterFragment) findFragmentByPosition(0);
            if (registerFragment != null && fetchStatus.equals(FetchStatus.fetched)) {
                registerFragment.refreshListView();
            }
        } else {
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    BaseRegisterFragment registerFragment = (BaseRegisterFragment) findFragmentByPosition(0);
                    if (registerFragment != null && fetchStatus.equals(FetchStatus.fetched)) {
                        registerFragment.refreshListView();
                    }
                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (bottomNavigationView != null && bottomNavigationView.getSelectedItemId() != R.id.action_clients) {
            setSelectedBottomBarMenuItem(R.id.action_clients);
        }
    }

    @Override
    public void showProgressDialog(int titleIdentifier) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(titleIdentifier);
        progressDialog.setMessage(getString(R.string.please_wait_message));
        if (!isFinishing())
            progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unregisterViewConfiguration(getViewIdentifiers());
    }

    public abstract List<String> getViewIdentifiers();

    @Override
    public Context getContext() {
        return this;
    }

    public void startQrCodeScanner() {
        if (PermissionUtils.isPermissionGranted(this, Manifest.permission.CAMERA, PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE)) {
            try {
                Intent intent = new Intent(this, BarcodeScanActivity.class);
                startActivityForResult(intent, AllConstants.BARCODE.BARCODE_REQUEST_CODE);
            } catch (SecurityException e) {
                Utils.showToast(this, getString(R.string.allow_camera_management));
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        Intent intent = new Intent(this, BarcodeScanActivity.class);
                        startActivityForResult(intent, AllConstants.BARCODE.BARCODE_REQUEST_CODE);
                    } catch (SecurityException e) {
                        Utils.showToast(this, getString(R.string.allow_camera_management));
                    }
                } else {
                    Utils.showToast(this, getString(R.string.allow_camera_management));
                }
                break;
            default:
                break;
        }
    }

    public void switchToFragment(final int position) {
        Timber.v("we are here switchtofragragment");
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                mPager.setCurrentItem(position, false);
            } else {
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        mPager.setCurrentItem(position, false);
                    }
                });
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void updateInitialsText(String initials) {
        this.userInitials = initials;
    }

    public void switchToBaseFragment() {
        switchToFragment(BASE_REG_POSITION);
    }

    public void setSelectedBottomBarMenuItem(int itemId) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(itemId);
        }
    }

    public void setSearchTerm(String searchTerm) {
        mBaseFragment.setSearchTerm(searchTerm);
    }
}
