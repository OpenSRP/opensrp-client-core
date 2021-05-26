package org.smartregister.domain;

import java.util.List;

import static java.util.Arrays.asList;
import static org.smartregister.domain.ReportIndicator.ANC4;
import static org.smartregister.domain.ReportIndicator.ANCS_AND_PNCS_WITH_BPL;
import static org.smartregister.domain.ReportIndicator.BCG;
import static org.smartregister.domain.ReportIndicator.BF_POST_BIRTH;
import static org.smartregister.domain.ReportIndicator.CESAREAN;
import static org.smartregister.domain.ReportIndicator.CESAREAN_GOV;
import static org.smartregister.domain.ReportIndicator.CESAREAN_PRI;
import static org.smartregister.domain.ReportIndicator.CHILD_DIARRHEA;
import static org.smartregister.domain.ReportIndicator.CHILD_MORTALITY;
import static org.smartregister.domain.ReportIndicator.CHILD_MORTALITY_DUE_TO_DIARRHEA;
import static org.smartregister.domain.ReportIndicator.CONDOM;
import static org.smartregister.domain.ReportIndicator.CONDOM_QTY;
import static org.smartregister.domain.ReportIndicator.DELIVERY;
import static org.smartregister.domain.ReportIndicator.DPT_BOOSTER1;
import static org.smartregister.domain.ReportIndicator.DPT_BOOSTER2;
import static org.smartregister.domain.ReportIndicator.DPT_BOOSTER_OR_OPV_BOOSTER;
import static org.smartregister.domain.ReportIndicator.D_CHC;
import static org.smartregister.domain.ReportIndicator.D_DH;
import static org.smartregister.domain.ReportIndicator.D_HOM;
import static org.smartregister.domain.ReportIndicator.D_PHC;
import static org.smartregister.domain.ReportIndicator.D_PRI;
import static org.smartregister.domain.ReportIndicator.D_SC;
import static org.smartregister.domain.ReportIndicator.D_SDH;
import static org.smartregister.domain.ReportIndicator.EARLY_ABORTIONS;
import static org.smartregister.domain.ReportIndicator.EARLY_ANC_REGISTRATIONS;
import static org.smartregister.domain.ReportIndicator.ENM;
import static org.smartregister.domain.ReportIndicator.FEMALE_STERILIZATION;
import static org.smartregister.domain.ReportIndicator.FS_APL;
import static org.smartregister.domain.ReportIndicator.FS_BPL;
import static org.smartregister.domain.ReportIndicator.HEP;
import static org.smartregister.domain.ReportIndicator.IB_1Y;
import static org.smartregister.domain.ReportIndicator.INFANT_BALANCE_BALANCE;
import static org.smartregister.domain.ReportIndicator.INFANT_BALANCE_LESS_THAN_FIVE_YEAR;
import static org.smartregister.domain.ReportIndicator.INFANT_BALANCE_LESS_THAN_ONE_YEAR;
import static org.smartregister.domain.ReportIndicator.INFANT_BALANCE_OA_CHILDREN;
import static org.smartregister.domain.ReportIndicator.INFANT_BALANCE_ON_HAND;
import static org.smartregister.domain.ReportIndicator.INFANT_BALANCE_TOTAL;
import static org.smartregister.domain.ReportIndicator.INFANT_LEFT;
import static org.smartregister.domain.ReportIndicator.INFANT_MORTALITY;
import static org.smartregister.domain.ReportIndicator.INFANT_REG;
import static org.smartregister.domain.ReportIndicator.INSTITUTIONAL_DELIVERY;
import static org.smartregister.domain.ReportIndicator.IUD;
import static org.smartregister.domain.ReportIndicator.JE;
import static org.smartregister.domain.ReportIndicator.LATE_ABORTIONS;
import static org.smartregister.domain.ReportIndicator.LATE_ANC_REGISTRATIONS;
import static org.smartregister.domain.ReportIndicator.LBW;
import static org.smartregister.domain.ReportIndicator.LIVE_BIRTH;
import static org.smartregister.domain.ReportIndicator.LNM;
import static org.smartregister.domain.ReportIndicator.MALE_STERILIZATION;
import static org.smartregister.domain.ReportIndicator.MEASLES;
import static org.smartregister.domain.ReportIndicator.MM;
import static org.smartregister.domain.ReportIndicator.MMA;
import static org.smartregister.domain.ReportIndicator.MMD;
import static org.smartregister.domain.ReportIndicator.MMP;
import static org.smartregister.domain.ReportIndicator.NM;
import static org.smartregister.domain.ReportIndicator.OCP;
import static org.smartregister.domain.ReportIndicator.OCP_C_OTHERS;
import static org.smartregister.domain.ReportIndicator.OCP_SC;
import static org.smartregister.domain.ReportIndicator.OCP_ST;
import static org.smartregister.domain.ReportIndicator.OPV;
import static org.smartregister.domain.ReportIndicator.OPV3;
import static org.smartregister.domain.ReportIndicator.OPV_BOOSTER;
import static org.smartregister.domain.ReportIndicator.PENTAVALENT3_OR_OPV3;
import static org.smartregister.domain.ReportIndicator.PENTAVALENT_1;
import static org.smartregister.domain.ReportIndicator.PENTAVALENT_2;
import static org.smartregister.domain.ReportIndicator.PENTAVALENT_3;
import static org.smartregister.domain.ReportIndicator.PNC3;
import static org.smartregister.domain.ReportIndicator.SPONTANEOUS_ABORTION;
import static org.smartregister.domain.ReportIndicator.STILL_BIRTH;
import static org.smartregister.domain.ReportIndicator.SUB_TT;
import static org.smartregister.domain.ReportIndicator.TOTAL_ANC_REGISTRATIONS;
import static org.smartregister.domain.ReportIndicator.TT1;
import static org.smartregister.domain.ReportIndicator.TT2;
import static org.smartregister.domain.ReportIndicator.TTB;
import static org.smartregister.domain.ReportIndicator.VIT_A_1;
import static org.smartregister.domain.ReportIndicator.VIT_A_1_FOR_FEMALE_CHILD;
import static org.smartregister.domain.ReportIndicator.VIT_A_1_FOR_MALE_CHILD;
import static org.smartregister.domain.ReportIndicator.VIT_A_2;
import static org.smartregister.domain.ReportIndicator.VIT_A_2_FOR_FEMALE_CHILD;
import static org.smartregister.domain.ReportIndicator.VIT_A_2_FOR_MALE_CHILD;
import static org.smartregister.domain.ReportIndicator.VIT_A_5_FOR_FEMALE_CHILD;
import static org.smartregister.domain.ReportIndicator.VIT_A_5_FOR_MALE_CHILD;
import static org.smartregister.domain.ReportIndicator.VIT_A_9_FOR_FEMALE_CHILD;
import static org.smartregister.domain.ReportIndicator.VIT_A_9_FOR_MALE_CHILD;
import static org.smartregister.domain.ReportIndicator.VIT_A_FOR_FEMALE;
import static org.smartregister.domain.ReportIndicator.VIT_A_FOR_MALE;
import static org.smartregister.domain.ReportIndicator.WEIGHED_AT_BIRTH;

public enum ReportsCategory {
    FPS("Family Planning Services",
            asList(IUD, CONDOM, CONDOM_QTY, OCP, OCP_SC, OCP_ST, OCP_C_OTHERS, MALE_STERILIZATION,
                    FEMALE_STERILIZATION, FS_BPL, FS_APL)),

    ANC_SERVICES("ANC Services",
            asList(EARLY_ANC_REGISTRATIONS, LATE_ANC_REGISTRATIONS, TOTAL_ANC_REGISTRATIONS, SUB_TT,
                    TT1, TT2, TTB, ANC4)),

    PREGNANCY_OUTCOMES("Pregnancy Outcomes",
            asList(LIVE_BIRTH, STILL_BIRTH, EARLY_ABORTIONS, LATE_ABORTIONS, SPONTANEOUS_ABORTION,
                    DELIVERY, INSTITUTIONAL_DELIVERY, D_HOM, D_SC, D_PHC, D_CHC, D_SDH, D_DH, D_PRI,
                    CESAREAN, CESAREAN_GOV, CESAREAN_PRI)),

    PNC_SERVICES("PNC Services", asList(PNC3)),

    CHILD_SERVICES("Child Services",
            asList(INFANT_BALANCE_ON_HAND, INFANT_REG, INFANT_BALANCE_TOTAL, INFANT_LEFT,
                    INFANT_MORTALITY, INFANT_BALANCE_BALANCE, INFANT_BALANCE_OA_CHILDREN,
                    INFANT_BALANCE_LESS_THAN_ONE_YEAR, INFANT_BALANCE_LESS_THAN_FIVE_YEAR,
                    PENTAVALENT3_OR_OPV3, DPT_BOOSTER_OR_OPV_BOOSTER, DPT_BOOSTER2, HEP, OPV,
                    MEASLES, PENTAVALENT_1, PENTAVALENT_2, PENTAVALENT_3, BCG, LBW, BF_POST_BIRTH,
                    WEIGHED_AT_BIRTH, VIT_A_1, VIT_A_1_FOR_FEMALE_CHILD, VIT_A_1_FOR_MALE_CHILD,
                    VIT_A_2, VIT_A_2_FOR_FEMALE_CHILD, VIT_A_2_FOR_MALE_CHILD,
                    VIT_A_5_FOR_FEMALE_CHILD, VIT_A_5_FOR_MALE_CHILD, VIT_A_9_FOR_FEMALE_CHILD,
                    VIT_A_9_FOR_MALE_CHILD, VIT_A_FOR_FEMALE, VIT_A_FOR_MALE, CHILD_DIARRHEA,
                    CHILD_MORTALITY_DUE_TO_DIARRHEA, IB_1Y, OPV3, OPV_BOOSTER, JE, DPT_BOOSTER1)),

    MORTALITY("Mortality",
            asList(ENM, NM, LNM, INFANT_MORTALITY, CHILD_MORTALITY, MMA, MMD, MMP, MM)),

    BENEFICIARY_SCHEMES("Beneficiary Schemes", asList(ANCS_AND_PNCS_WITH_BPL));

    private String description;
    private List<ReportIndicator> indicators;

    ReportsCategory(String description, List<ReportIndicator> indicators) {
        this.description = description;
        this.indicators = indicators;
    }

    public List<ReportIndicator> indicators() {
        return indicators;
    }

    public String description() {
        return description;
    }

    public String value() {
        return name();
    }
}
