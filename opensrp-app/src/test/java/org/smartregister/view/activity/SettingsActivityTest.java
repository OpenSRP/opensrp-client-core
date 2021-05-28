package org.smartregister.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.view.View;
import android.widget.Toast;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.shadows.ShadowPreferenceManager;

import static org.junit.Assert.assertEquals;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

@Config(shadows = {ShadowPreferenceManager.class})
public class SettingsActivityTest extends BaseUnitTest {

    private static final String HOSTURL = "https://www.opensrp.smartregister.com";
    private static final String PORT = "8080";

    private ActivityController<SettingsActivity> controller;

    @Mock
    private AlertDialog alertDialog;

    @InjectMocks
    private SettingsActivity activity;

    @Mock
    private AllSharedPreferences sharedPreferences;

    @Mock
    private org.smartregister.Context context_;

    @Mock
    private View view;

    private EditTextPreference baseUrlEditTextPreference = new EditTextPreference(RuntimeEnvironment.application);


    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context_);
        Intent intent = new Intent(RuntimeEnvironment.application, SettingsActivity.class);
        controller = Robolectric.buildActivity(SettingsActivity.class, intent);
        activity = controller.get();
        controller.setup();
    }

    @Test
    public void assertActivityCreatedSuccessfully() {
        Assert.assertNotNull(activity);
    }

    @Test
    public void testOnPreferenceChangeInvokesPreferenceSettingsWithCorrectValues() {

        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSharedPreferences", sharedPreferences);
        activity.onPreferenceChange(null, HOSTURL + ":" + PORT);

        Mockito.verify(sharedPreferences).savePort(8080);

        Mockito.verify(sharedPreferences).saveHost(HOSTURL);
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSharedPreferences", null);
    }


    @Test
    public void testOnClickInvokesBaseUrlEditTextPreferenceOnClickMethod() {

        ReflectionHelpers.setField(activity, "dialog", alertDialog);

        ReflectionHelpers.setField(activity, "baseUrlEditTextPreference", baseUrlEditTextPreference);
        Assert.assertNotNull(baseUrlEditTextPreference);
        baseUrlEditTextPreference.getEditText().setText(HOSTURL + ":" + PORT);

        activity.onClick(view);

        Mockito.verify(alertDialog).dismiss();

    }

    @Test
    public void testOnClickInvokesShowErrorToastIfInvalidUrlEntered() {

        ReflectionHelpers.setField(activity, "dialog", alertDialog);
        ReflectionHelpers.setField(activity, "baseUrlEditTextPreference", baseUrlEditTextPreference);


        Assert.assertNotNull(baseUrlEditTextPreference);
        baseUrlEditTextPreference.getEditText().setText("baddly-formed-url:" + PORT);

        activity.onClick(view);

        Toast toast = ShadowToast.getLatestToast();
        assertEquals(Toast.LENGTH_SHORT, toast.getDuration());
        assertEquals(RuntimeEnvironment.application.getString(R.string.invalid_url_massage), ShadowToast.getTextOfLatestToast());
    }
}
