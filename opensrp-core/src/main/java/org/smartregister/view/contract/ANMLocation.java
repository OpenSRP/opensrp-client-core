package org.smartregister.view.contract;

import com.google.gson.Gson;

import java.util.List;

public class ANMLocation {
    private String district;
    private String phcIdentifier;
    private String subCenter;
    private List<String> villages;

    public String asJSONString() {
        return new Gson().toJson(new ANMLocationJSONString(district, phcIdentifier, subCenter));
    }

    private class ANMLocationJSONString {
        private String district;
        private String phc;
        private String subCenter;

        private ANMLocationJSONString(String district, String phc, String subCenter) {
            this.district = district;
            this.phc = phc;
            this.subCenter = subCenter;
        }
    }
}
