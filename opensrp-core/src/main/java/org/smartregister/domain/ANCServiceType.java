package org.smartregister.domain;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.ANCClient;

import static org.smartregister.util.Log.logWarn;

public enum ANCServiceType {
    ANC_1 {
        @Override
        public String displayName() {
            return "ANC 1";
        }

        @Override
        public String serviceName() {
            return "ANC 1";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_ANC;
        }
    }, ANC_2 {
        @Override
        public String displayName() {
            return "ANC 2";
        }

        @Override
        public String serviceName() {
            return "ANC 2";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_ANC;
        }
    }, ANC_3 {
        @Override
        public String displayName() {
            return "ANC 3";
        }

        @Override
        public String serviceName() {
            return "ANC 3";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_ANC;
        }
    }, ANC_4 {
        @Override
        public String displayName() {
            return "ANC 4";
        }

        @Override
        public String serviceName() {
            return "ANC 4";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_ANC;
        }
    }, TT_1 {
        @Override
        public String displayName() {
            return "TT";
        }

        @Override
        public String serviceName() {
            return "TT 1";
        }

        @Override
        public String serviceDisplayName() {
            return "TT 1";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_TT;
        }
    }, TT_2 {
        @Override
        public String serviceDisplayName() {
            return "TT 2";
        }

        @Override
        public String displayName() {
            return "TT 2";
        }

        @Override
        public String serviceName() {
            return "TT 2";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_TT;
        }
    }, TT_BOOSTER {
        @Override
        public String serviceDisplayName() {
            return "TT B";
        }

        @Override
        public String displayName() {
            return "TT B";
        }

        @Override
        public String serviceName() {
            return "TT Booster";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_TT;
        }
    }, IFA {
        @Override
        public String displayName() {
            return "IFA Tablets";
        }

        @Override
        public String serviceName() {
            return "IFA";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_IFA;
        }
    }, HB_TEST {
        @Override
        public String displayName() {
            return "Hb Test";
        }

        @Override
        public String serviceName() {
            return "Hb";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_HB;
        }
    }, DELIVERY_PLAN {
        @Override
        public String displayName() {
            return "Delivery Plan";
        }

        @Override
        public String serviceName() {
            return "Delivery Plan";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_DELIVERY_PLAN;
        }
    }, PNC {
        @Override
        public String displayName() {
            return "PNC";
        }

        @Override
        public String serviceName() {
            return "PNC";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_PNC;
        }
    }, PNC_1 {
        @Override
        public String displayName() {
            return "PNC 1";
        }

        @Override
        public String serviceName() {
            return "PNC 1";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_PNC;
        }
    }, PNC_2 {
        @Override
        public String displayName() {
            return "PNC 2";
        }

        @Override
        public String serviceName() {
            return "PNC 2";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_PNC;
        }
    }, PNC_3 {
        @Override
        public String displayName() {
            return "PNC 3";
        }

        @Override
        public String serviceName() {
            return "PNC 3";
        }

        @Override
        public String category() {
            return ANCClient.CATEGORY_PNC;
        }
    }, EMPTY {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_empty);
        }

        @Override
        public String serviceName() {
            return "";
        }

        @Override
        public String category() {
            return "";
        }
    }, KB_IUD {
        @Override
        public String displayName() {
            return "KB IUD";
        }

        @Override
        public String serviceName() {
            return "KB IUD";
        }

        @Override
        public String category() {
            return "kb";
        }
    }, KB_Implant {
        @Override
        public String displayName() {
            return "KB Implant";
        }

        @Override
        public String serviceName() {
            return "KB Implant";
        }

        @Override
        public String category() {
            return "kb";
        }
    }, KB_Injection_Cyclofem {
        @Override
        public String displayName() {
            return "KB Injection Cyclofem";
        }

        @Override
        public String serviceName() {
            return "KB Injection Cyclofem";
        }

        @Override
        public String category() {
            return "kb";
        }
    }, KB_Injection_Depoprovera {
        @Override
        public String displayName() {
            return "KB Injection Depoprovera";
        }

        @Override
        public String serviceName() {
            return "KB Injection Depoprovera";
        }

        @Override
        public String category() {
            return "kb";
        }
    };

    public static ANCServiceType tryParse(String type, ANCServiceType defaultType) {
        try {
            if (type.equalsIgnoreCase("ANC 1")) {
                return ANCServiceType.valueOf("ANC_1");
            } else if (type.equalsIgnoreCase("ANC 2")) {
                return ANCServiceType.valueOf("ANC_2");
            } else if (type.equalsIgnoreCase("ANC 3")) {
                return ANCServiceType.valueOf("ANC_3");
            } else if (type.equalsIgnoreCase("ANC 4")) {
                return ANCServiceType.valueOf("ANC_4");
            } else if (type.equalsIgnoreCase("TT 1")) {
                return ANCServiceType.valueOf("TT_1");
            } else if (type.equalsIgnoreCase("TT 2")) {
                return ANCServiceType.valueOf("TT_2");
            } else if (type.equalsIgnoreCase("TT Booster")) {
                return ANCServiceType.valueOf("TT_BOOSTER");
            } else if (type.equalsIgnoreCase("IFA 1")) {
                return ANCServiceType.valueOf("IFA");
            } else if (type.equalsIgnoreCase("IFA 2")) {
                return ANCServiceType.valueOf("IFA");
            } else if (type.equalsIgnoreCase("IFA 3")) {
                return ANCServiceType.valueOf("IFA");
            } else if (type.equalsIgnoreCase("HB Test 1")) {
                return ANCServiceType.valueOf("HB_TEST");
            } else if (type.equalsIgnoreCase("HB Test 2")) {
                return ANCServiceType.valueOf("HB_TEST");
            } else if (type.equalsIgnoreCase("Delivery Plan")) {
                return ANCServiceType.valueOf("DELIVERY_PLAN");
            } else if (type.equalsIgnoreCase("PNC")) {
                return ANCServiceType.valueOf("PNC");
            } else if (type.equalsIgnoreCase("PNC 1")) {
                return ANCServiceType.valueOf("PNC_1");
            } else if (type.equalsIgnoreCase("PNC 2")) {
                return ANCServiceType.valueOf("PNC_2");
            } else if (type.equalsIgnoreCase("PNC 3")) {
                return ANCServiceType.valueOf("PNC_3");
            } else if (type.equalsIgnoreCase("KB IUD")) {
                return ANCServiceType.valueOf("KB_IUD");
            } else if (type.equalsIgnoreCase("KB Implant")) {
                return ANCServiceType.valueOf("KB_Implant");
            } else if (type.equalsIgnoreCase("KB Injection Cyclofem")) {
                return ANCServiceType.valueOf("KB_Injection_Cyclofem");
            } else if (type.equalsIgnoreCase("KB Injection Depoprovera")) {
                return ANCServiceType.valueOf("KB_Injection_Depoprovera");
            } else {
                return StringUtils.isBlank(type) || type.equalsIgnoreCase("REMINDER") ? defaultType
                        : ANCServiceType.valueOf(type.toUpperCase(Utils.getDefaultLocale()));
            }
        } catch (IllegalArgumentException e) {
            logWarn("Unknown current Service Type : " + type + " Exception : " + e);
            return defaultType;
        }
    }

    public abstract String displayName();

    public abstract String serviceName();

    public String serviceDisplayName() {
        return serviceName();
    }

    public String shortName() {
        return displayName();
    }

    public abstract String category();
}
