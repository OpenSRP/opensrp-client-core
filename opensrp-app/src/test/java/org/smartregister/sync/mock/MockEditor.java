package org.smartregister.sync.mock;

import android.content.SharedPreferences;
import androidx.annotation.Nullable;

import java.util.Set;

/**
 * Created by kaderchowdhury on 13/11/17.
 */

public class MockEditor {

    public static SharedPreferences.Editor getEditor() {
        return new SharedPreferences.Editor() {
            @Override
            public SharedPreferences.Editor putString(String s, @Nullable String s1) {
                return this;
            }

            @Override
            public SharedPreferences.Editor putStringSet(String s, @Nullable Set<String> set) {
                return this;
            }

            @Override
            public SharedPreferences.Editor putInt(String s, int i) {
                return this;
            }

            @Override
            public SharedPreferences.Editor putLong(String s, long l) {
                return this;
            }

            @Override
            public SharedPreferences.Editor putFloat(String s, float v) {
                return this;
            }

            @Override
            public SharedPreferences.Editor putBoolean(String s, boolean b) {
                return this;
            }

            @Override
            public SharedPreferences.Editor remove(String s) {
                return null;
            }

            @Override
            public SharedPreferences.Editor clear() {
                return null;
            }

            @Override
            public boolean commit() {
                return true;
            }

            @Override
            public void apply() {

            }
        };
    }
}
