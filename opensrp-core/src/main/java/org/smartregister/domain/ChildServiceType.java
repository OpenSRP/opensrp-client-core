package org.smartregister.domain;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.ChildClient;

import java.util.Locale;

import static org.smartregister.util.Log.logWarn;

public enum ChildServiceType {
    MEASLES {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_measles);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_MEASLES;
        }
    }, MEASLESBOOSTER {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_measles_booster);
        }

        @Override
        public String shortName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_measles_booster_short);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_MEASLES;
        }
    }, OPV_BOOSTER {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_opv_booster);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_OPVBOOSTER;
        }
    },

    DPTBOOSTER_1 {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_dpt_booster_1);
        }

        @Override
        public String shortName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_dpt_booster_1_short);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_DPT;
        }
    }, DPTBOOSTER_2 {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_dpt_booster_2);
        }

        @Override
        public String shortName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_dpt_booster_2_short);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_DPT;
        }
    }, OPV_0 {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_opv_0);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_OPV;
        }
    }, OPV_1 {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_opv_1);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_OPV;
        }
    }, OPV_2 {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_opv_2);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_OPV;
        }
    }, OPV_3 {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_opv_3);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_OPV;
        }
    },

    PENTAVALENT_1 {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_pentavalent_1);
        }

        @Override
        public String shortName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_pentavalent_1_short);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_PENTAVALENT;
        }
    }, PENTAVALENT_2 {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_pentavalent_2);
        }

        @Override
        public String shortName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_pentavalent_2_short);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_PENTAVALENT;
        }
    }, PENTAVALENT_3 {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_pentavalent_3);
        }

        @Override
        public String shortName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_pentavalent_3_short);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_PENTAVALENT;
        }
    }, BCG {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext().getString(R.string.service_type_bcg);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_BCG;
        }
    }, PNC {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext().getString(R.string.service_type_pnc);
        }

        @Override
        public String category() {
            return "";
        }
    }, JE {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext().getString(R.string.service_type_je);
        }

        @Override
        public String category() {
            return "";
        }
    }, MMR {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext().getString(R.string.service_type_mmr);
        }

        @Override
        public String category() {
            return "";
        }
    }, VITAMIN_A {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_vitamin_a);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_VITAMIN_A;
        }
    }, ILLNESS_VISIT {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_illness_visit);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_CHILD_ILLNESS;
        }
    }, HEPB_0 {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_hepb_0);
        }

        @Override
        public String category() {
            return ChildClient.CATEGORY_HEPB;
        }
    }, EMPTY {
        @Override
        public String displayName() {
            return CoreLibrary.getInstance().context().applicationContext()
                    .getString(R.string.service_type_empty);
        }

        @Override
        public String category() {
            return "";
        }
    };

    public static ChildServiceType tryParse(String type, ChildServiceType defaultType) {
        try {
            if (type.equalsIgnoreCase("Illness Visit")) {
                return ChildServiceType.valueOf("ILLNESS_VISIT");
            } else if (type.equalsIgnoreCase("Vitamin A")) {
                return ChildServiceType.valueOf("VITAMIN_A");
            } else if (type.equalsIgnoreCase("opvbooster")) {
                return ChildServiceType.valueOf("OPV_BOOSTER");
            } else {
                return StringUtils.isBlank(type) ? defaultType
                        : ChildServiceType.valueOf(type.toUpperCase(Utils.getDefaultLocale()));
            }
        } catch (IllegalArgumentException e) {
            logWarn("Unknown current Service Type : " + type + " Exception : " + e);
            return defaultType;
        }
    }

    public abstract String displayName();

    public String shortName() {
        return displayName();
    }

    public abstract String category();
}
