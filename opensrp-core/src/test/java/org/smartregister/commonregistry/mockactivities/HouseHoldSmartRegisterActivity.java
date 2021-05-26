package org.smartregister.commonregistry.mockactivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;

import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.R;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.mockactivities.pageradapter.BaseRegisterActivityPagerAdapter;
import org.smartregister.domain.Alert;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.OpenFormOption;
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class HouseHoldSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {

    public static final String TAG = "HouseHoldActivity";

    OpenSRPViewPager mPager;
    public FragmentPagerAdapter mPagerAdapter;
    public int currentPage;

    public String[] formNames = new String[]{};
    public Fragment mBaseFragment = null;
    static Context mockactivitycontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); //we need this here
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPager = (OpenSRPViewPager) findViewById(R.id.view_pager);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        formNames = this.buildFormNameList();
        mBaseFragment = new HouseHoldSmartRegisterFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment);
        mPager.setOffscreenPageLimit(formNames.length);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }
        });

        ziggyService = context().ziggyService();
    }

    public void onPageChanged(int page) {
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected void setupViews() {
    }

    @Override
    protected void onResumption() {
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
    protected void onInitialization() {
    }

    @Override
    public void startRegistration() {
    }

    public DialogOption[] getEditOptions() {

        HashMap<String, String> overridemap = new HashMap<String, String>();
        String alertstate = "";

        return new DialogOption[]{
                new OpenFormOption("census_enrollment_form", "census_enrollment_form", formController, overridemap, OpenFormOption.ByColumnAndByDetails.bydefault)
        };

    }

    private String getalertstateforcensus(CommonPersonObjectClient pc) {
        try {
            List<Alert> alertlist_for_client = org.smartregister.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "FW CENSUS");
            String alertstate = "";
            if (alertlist_for_client.size() == 0) {

            } else {
                for (int i = 0; i < alertlist_for_client.size(); i++) {
//           psrfdue.setText(alertlist_for_client.get(i).expiryDate());
                    Log.v("printing alertlist", alertlist_for_client.get(i).status().value());
                    alertstate = alertlist_for_client.get(i).status().value();

                }
            }
            return alertstate;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides) {
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try {
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);
            ziggyService.saveForm(getParams(submission), submission.instance());
            ClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);
            //switch to forms list fragment
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

        } catch (Exception e) {
            // TODO: show error dialog on the formfragment if the submission fails

            e.printStackTrace();
        }
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        Log.v("fieldoverride", metaData);
        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
            if (entityId != null || metaData != null) {
                String data = null;
                //check if there is previously saved data for the form
                data = getPreviouslySavedDataForForm(formName, metaData, entityId);
                if (data == null) {
                    data = FormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                }

//                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
//                if (displayFormFragment != null) {
//                    displayFormFragment.setFormData(data);
//                    displayFormFragment.setRecordId(entityId);
//                    displayFormFragment.setFieldOverides(metaData);
//                }
            }

            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void switchToBaseFragment(final String data) {
        final int prevPageIndex = currentPage;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(0, false);
                SecuredNativeSmartRegisterFragment registerFragment = (SecuredNativeSmartRegisterFragment) findFragmentByPosition(0);
                if (registerFragment != null && data != null) {
                    registerFragment.refreshListView();
                }

                //hack reset the form
//                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
//                if (displayFormFragment != null) {
//                    displayFormFragment.hideTranslucentProgressDialog();
//                    displayFormFragment.setFormData(null);
//                }
//
//                displayFormFragment.setRecordId(null);
            }
        });

    }

    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }


    @Override
    public void onBackPressed() {
        if (currentPage != 0) {
            retrieveAndSaveUnsubmittedFormData();
            String BENGALI_LOCALE = "bn";
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(Context.getInstance().applicationContext()));

            String preferredLocale = allSharedPreferences.fetchLanguagePreference();
            if (BENGALI_LOCALE.equals(preferredLocale)) {
                new AlertDialog.Builder(this)
                        .setMessage("আপনি কি নিশ্চিত যে আপনি ফর্ম থেকে বের হয়ে যেতে চান? ")
                        .setTitle("ফর্ম বন্ধ নিশ্চিত করুন ")
                        .setCancelable(false)
                        .setPositiveButton("হাঁ",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        switchToBaseFragment(null);
                                    }
                                })
                        .setNegativeButton("না",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                    }
                                })
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("Cancek Form Launch")
                        .setTitle("cancel")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        switchToBaseFragment(null);
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                    }
                                })
                        .show();
            }

        } else if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    private String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add("new_household_registration");
        formNames.add("census_enrollment_form");
//        DialogOption[] options = getEditOptions();
//        for (int i = 0; i < options.length; i++){
//            formNames.add(((OpenFormOption) options[i]).getFormName());
//        }
        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        retrieveAndSaveUnsubmittedFormData();
    }

    public void retrieveAndSaveUnsubmittedFormData() {
        if (currentActivityIsShowingForm()) {
//            DisplayFormFragment formFragment = getDisplayFormFragmentAtIndex(currentPage);
//            formFragment.saveCurrentFormData();
        }
    }

    private boolean currentActivityIsShowingForm() {
        return currentPage != 0;
    }

    @Override
    protected Context context() {
        return mockactivitycontext;
    }

    public static void setContext(Context context) {
        mockactivitycontext = context;
    }

}
