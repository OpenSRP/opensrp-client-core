package org.smartregister.shadows;

import android.content.Context;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import androidx.annotation.XmlRes;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import static org.mockito.Mockito.mock;

/**
 * Created by samuelgithengi on 6/24/20.
 */
@Implements(PreferenceManager.class)
public class ShadowPreferenceManager {

    @Implementation
    public PreferenceScreen inflateFromResource(Context context, @XmlRes int resId,
                                                PreferenceScreen rootPreferences) {
        return mock(PreferenceScreen.class);
    }
}
