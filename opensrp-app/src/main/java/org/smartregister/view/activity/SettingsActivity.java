package org.smartregister.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import org.smartregister.R;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.LangUtils;
import org.smartregister.util.UrlUtil;
import org.smartregister.util.Utils;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, View.OnClickListener {


    private static EditTextPreference baseUrlEditTextPreference;
    private Dialog dialog;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs

        String lang = LangUtils.getLanguage(base.getApplicationContext());
        Configuration newConfiguration = LangUtils.setAppLocale(base, lang);

        super.attachBaseContext(base);

        applyOverrideConfiguration(newConfiguration);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (newValue != null) {
            updateUrl(newValue.toString());
        }
        return true;
    }

    private void updateUrl(String baseUrl) {
        try {

            AllSharedPreferences allSharedPreferences = Utils.getAllSharedPreferences();

            URL url = new URL(baseUrl);

            String base = url.getProtocol() + "://" + url.getHost();
            int port = url.getPort();

            Timber.i("Base URL: %s", base);
            Timber.i("Port: %s", port);

            allSharedPreferences.saveHost(base);
            allSharedPreferences.savePort(port);

            Timber.i("Saved URL: %s", allSharedPreferences.fetchHost(""));
            Timber.i("Port: %s", allSharedPreferences.fetchPort(0));
        } catch (MalformedURLException e) {
            Timber.e("Malformed Url: %s", baseUrl);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        dialog = baseUrlEditTextPreference.getDialog();
        ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(this);
        return false;
    }

    @Override
    public void onClick(View view) {

        String newValue = baseUrlEditTextPreference.getEditText().getText().toString();
        if (newValue != null && UrlUtil.isValidUrl(newValue) && UrlUtil.isValidEnvironment(newValue)) {
            baseUrlEditTextPreference.onClick(null, DialogInterface.BUTTON_POSITIVE);
            dialog.dismiss();
        } else {
            Utils.showShortToast(baseUrlEditTextPreference.getContext(), baseUrlEditTextPreference.getContext().getString(R.string.invalid_url_massage));
        }

    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference baseUrlPreference = findPreference("DRISHTI_BASE_URL");

            if (baseUrlPreference != null) {
                baseUrlEditTextPreference = (EditTextPreference) baseUrlPreference;
                baseUrlEditTextPreference.setOnPreferenceClickListener((SettingsActivity) getActivity());
                baseUrlEditTextPreference.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        Timber.i("baseUrl before text change");
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        Timber.i("baseUrl on text change");
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String text = editable.toString();
                        boolean validUrl = UrlUtil.isValidUrl(text);
                        if (!validUrl) {
                            if (text.isEmpty()) {
                                baseUrlEditTextPreference.getEditText().setError(getString(R.string.msg_empty_url));
                            } else {
                                baseUrlEditTextPreference.getEditText().setError(getString(R.string.invalid_url_massage));
                            }
                        } else if (!UrlUtil.isValidEnvironment(text)) {
                            baseUrlEditTextPreference.getEditText().setError(getString(R.string.no_client_available_for_url));
                        } else {
                            baseUrlEditTextPreference.getEditText().setError(null);
                        }

                    }
                });

                baseUrlEditTextPreference.setOnPreferenceChangeListener((SettingsActivity) getActivity());
            }
        }

    }

}