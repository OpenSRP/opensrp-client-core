package org.smartregister.domain.form;

import com.google.gson.Gson;

public class FieldOverrides {
    public String fieldOverrides;

    public FieldOverrides(String fieldOverrides) {
        this.fieldOverrides = fieldOverrides;
    }

    public String getJSONString() {
        return new Gson().toJson(this);
    }
}
