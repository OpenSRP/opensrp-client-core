package org.smartregister.view.activity;

import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;

import org.smartregister.util.LangUtils;


/**
 * Use this class to ensure your activities have multi-language support
 *
 * @author Rodgers Andati
 * @since 2019-05-03
 */
public class MultiLanguageActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = LangUtils.getLanguage(base.getApplicationContext());
        Configuration newConfiguration = LangUtils.setAppLocale(base, lang);

        super.attachBaseContext(base);

        applyOverrideConfiguration(newConfiguration);
    }


}
