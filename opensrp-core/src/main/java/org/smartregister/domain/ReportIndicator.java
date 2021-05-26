package org.smartregister.domain;

import org.smartregister.CoreLibrary;
import org.smartregister.view.contract.Beneficiary;

import java.util.List;

import static org.smartregister.view.controller.ProfileNavigationController.navigateToANCProfile;
import static org.smartregister.view.controller.ProfileNavigationController.navigateToChildProfile;
import static org.smartregister.view.controller.ProfileNavigationController.navigateToECProfile;
import static org.smartregister.view.controller.ProfileNavigationController.navigateToPNCProfile;

public enum ReportIndicator {
    IUD("IUD", "IUD Adoption") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToECProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchECCaseList(caseIds);
        }
    }, CONDOM("CONDOM", "Condom Usage") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToECProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchECCaseList(caseIds);
        }
    }, CONDOM_QTY("CONDOM_QTY", "Condom Pieces") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToECProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchECCaseList(caseIds);
        }
    }, OCP("OCP", "Oral Pills") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToECProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchECCaseList(caseIds);
        }
    }, OCP_ST("OCP_ST", "Oral Pills ST") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToECProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchECCaseList(caseIds);
        }
    }, OCP_SC("OCP_SC", "Oral Pills SC") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToECProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchECCaseList(caseIds);
        }
    }, OCP_C_OTHERS("OCP_C_OTHERS", "Oral Pills Other Castes") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToECProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchECCaseList(caseIds);
        }
    }, MALE_STERILIZATION("MALE_STERILIZATION", "Male Sterilization") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToECProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchECCaseList(caseIds);
        }
    }, FEMALE_STERILIZATION("FEMALE_STERILIZATION", "Female Sterilization") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToECProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchECCaseList(caseIds);
        }
    }, FS_APL("FS_APL", "Female Sterilization APL") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToECProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchECCaseList(caseIds);
        }
    }, FS_BPL("FS_BPL", "Female Sterilization BPL") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToECProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchECCaseList(caseIds);
        }
    }, PENTAVALENT3_OR_OPV3("PENTAVALENT3_OPV3", "PENTAVALENT 3 / OPV 3") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, DPT_BOOSTER_OR_OPV_BOOSTER("DPTB_OPVB", "DPT Booster / OPV Booster") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, DPT_BOOSTER2("DPT_BOOSTER_2", "DPT Booster 2") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, DPT_BOOSTER1("DPT_BOOSTER_1", "DPT Booster 1") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, OPV_BOOSTER("OPV_BOOSTER", "OPV Booster") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, OPV3("OPV3", "OPV 3") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, JE("JE", "JE") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, HEP("HEP", "HEP") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, OPV("OPV", "OPV") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, MEASLES("MEASLES", "MEASLES") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, PENTAVALENT_1("PENT1", "Pentavalent 1") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, PENTAVALENT_2("PENT2", "Pentavalent 2") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, PENTAVALENT_3("PENT3", "Pentavalent 3") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_1("VIT_A_1", "First Dose of Vitamin A") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_1_FOR_FEMALE_CHILD("F_VIT_A_1", "Vit A 1 (Female)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_1_FOR_MALE_CHILD("M_VIT_A_1", "Vit A 1 (Male)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_2("VIT_A_2", "Second Dose of Vitamin A") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_2_FOR_FEMALE_CHILD("F_VIT_A_2", "Vit A 2 (Female)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_2_FOR_MALE_CHILD("M_VIT_A_2", "Vit A 2 (Male)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_5_FOR_FEMALE_CHILD("F_VIT_A_5", "Vit A 5 (Female)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_5_FOR_MALE_CHILD("M_VIT_A_5", "Vit A 5 (Male)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_9_FOR_FEMALE_CHILD("F_VIT_A_9", "Vit A 9 (Female)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_9_FOR_MALE_CHILD("M_VIT_A_9", "Vit A 9 (Male)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_FOR_FEMALE("F_VIT_A", "Vit A (Female)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, VIT_A_FOR_MALE("M_VIT_A", "Vit A (Male)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, CHILD_DIARRHEA("CHILD_DIARRHEA", "Number of children had diarrhea episode") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, WEIGHED_AT_BIRTH("WEIGHED_AT_BIRTH", "Number of infants weighed at birth") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, BF_POST_BIRTH("BF_POST_BIRTH", "Exclusively BF within 1 hr of birth") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, BCG("BCG", "BCG") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, EARLY_ANC_REGISTRATIONS("ANC_LT_12", "Early ANC Registration") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToANCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, LATE_ANC_REGISTRATIONS("ANC_GT_12", "Late ANC Registration") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToANCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, TOTAL_ANC_REGISTRATIONS("ANC", "Total ANC Registration") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToANCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, ANC4("ANC4", "Minimum 4 ANC Visits") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToANCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, SUB_TT("SUB_TT", "TT2 and TT Booster (Pregnant Women)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToANCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, TT1("TT1", "TT1") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToANCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, TT2("TT2", "TT2") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToANCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, TTB("TTB", "TT Booster") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToANCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, INFANT_MORTALITY("IM", "Infant Mortality") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, INFANT_BALANCE_BALANCE("IB_BAL", "Infant Balance (Balance)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, INFANT_BALANCE_OA_CHILDREN("IB_OA", "Infant Balance (O/A Infants)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, INFANT_BALANCE_LESS_THAN_ONE_YEAR("IB_LT_1Y", "No. of children (0-1 Year)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, INFANT_BALANCE_LESS_THAN_FIVE_YEAR("IB_LT_5Y", "No. of children (0-5 Years)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, ENM("ENM", "Early Neonatal mortality") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, NM("NM", "Neonatal mortality") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, LNM("LNM", "29 days to 1 year of birth") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, CHILD_MORTALITY("UFM", "Under 5 mortality") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, LIVE_BIRTH("LIVE_BIRTH", "Live Birth") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, STILL_BIRTH("STILL_BIRTH", "Still Birth") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, LBW("LBW", "Low Birth Weight") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, EARLY_ABORTIONS("MTP_LT_12", "Abortions before 12 weeks") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, LATE_ABORTIONS("MTP_GT_12", "Abortions after 12 weeks") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, SPONTANEOUS_ABORTION("SPONTANEOUS_ABORTION", "Spontaneous abortions") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, DELIVERY("DELIVERY", "Total Deliveries") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, CESAREAN("CESAREAN", "Cesareans") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, CESAREAN_GOV("CESAREAN_GOV", "Cesareans Government Hospital") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, CESAREAN_PRI("CESAREAN_PRI", "Cesareans Private Hospital") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, MMA("MMA", "Mother mortality (during ANC)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, MMD("MMD", "Mother mortality (during Delivery)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, MMP("MMP", "Mother mortality (during PNC)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, MM("MM", "Total Mother Mortality ") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, INSTITUTIONAL_DELIVERY("INSTITUTIONAL_DELIVERY", "Institutional Deliveries") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, PNC3("PNC3", "No. of PNC women who received 3 visits") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, D_HOM("D_HOM", "Number of deliveries conducted at Home") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, D_SC("D_SC", "Number of deliveries conducted at Sub Center") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, D_PHC("D_PHC", "Number of deliveries conducted at PHC") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, D_CHC("D_CHC", "Number of deliveries conducted at Community Health Center") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, D_SDH("D_SDH", "Number of deliveries conducted at Sub District Hospital") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, D_DH("D_DH", "Number of deliveries conducted at District Hospital") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, D_PRI("D_PRI", "Number of deliveries conducted at Private facility") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToPNCProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, INFANT_BALANCE_ON_HAND("IB_OH", "Infant Balance (On Hand)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, INFANT_REG("INFANT_REG", "Infant Balance (During Month Registration)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, INFANT_LEFT("INFANT_LEFT", "Infant Balance (Left the Place)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, INFANT_BALANCE_TOTAL("IBT", "Infant Balance (Total)") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, CHILD_MORTALITY_DUE_TO_DIARRHEA("CMD", "Number of children who died of diarrhea") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    }, ANCS_AND_PNCS_WITH_BPL("ANCS_AND_PNCS_WITH_BPL",
            "Number of open ANCs and PNCs who have " + "economic status as BPL") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {

        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchMotherCaseList(caseIds);
        }
    }, IB_1Y("IB_1Y", "Number of children turning one year old in the current reporting month") {
        @Override
        public void startCaseDetailActivity(android.content.Context context, String caseId) {
            navigateToChildProfile(context, caseId);
        }

        @Override
        public List<Beneficiary> fetchCaseList(List<String> caseIds) {
            return fetchChildCaseList(caseIds);
        }
    };

    private String value;

    private String description;

    ReportIndicator(String value, String description) {
        this.value = value;
        this.description = description;
    }

    private static List<Beneficiary> fetchECCaseList(List<String> caseIds) {
        return CoreLibrary.getInstance().context().beneficiaryService().fetchFromEcCaseIds(caseIds);
    }

    private static List<Beneficiary> fetchMotherCaseList(List<String> caseIds) {
        return CoreLibrary.getInstance().context().beneficiaryService().fetchFromMotherCaseIds(caseIds);
    }

    private static List<Beneficiary> fetchChildCaseList(List<String> caseIds) {
        return CoreLibrary.getInstance().context().beneficiaryService().fetchFromChildCaseIds(caseIds);
    }

    public static ReportIndicator parseToReportIndicator(String indicator) {
        for (ReportIndicator reportIndicator : values()) {
            if (reportIndicator.value().equalsIgnoreCase(indicator)) {
                return reportIndicator;
            }
        }
        throw new IllegalArgumentException(
                "Could not find ReportIndicator for value: " + indicator);
    }

    public String description() {
        return description;
    }

    public String value() {
        return value;
    }

    public abstract List<Beneficiary> fetchCaseList(List<String> caseIds);

    public abstract void startCaseDetailActivity(android.content.Context context, String caseId);
}
