package org.smartregister.view.contract;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.ChildServiceType;
import org.smartregister.util.DateUtil;
import org.smartregister.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public interface ChildSmartRegisterClient extends SmartRegisterClient {
    List<String> illnessAcronyms = new ArrayList<String>(
            Arrays.asList(CoreLibrary.getInstance().context().getStringResource(R.string.str_child_illness_ari),
                    CoreLibrary.getInstance().context().getStringResource(R.string.str_child_illness_sam)));

    String gender();

    String weight();

    String thayiCardNumber();

    String motherEcNumber();

    String dateOfBirth();

    String fatherName();

    String motherName();

    String locationStatus();

    List<ServiceProvidedDTO> serviceProvided();

    ServiceProvidedDTO lastServiceProvided();

    ServiceProvidedDTO illnessVisitServiceProvided();

    ChildSickStatus sickStatus();

    boolean isBcgDone();

    boolean isOpvDone();

    boolean isHepBDone();

    boolean isPentavDone();

    String bcgDoneDate();

    String opvDoneDate();

    String hepBDoneDate();

    String pentavDoneDate();

    boolean isMeaslesDone();

    boolean isOpvBoosterDone();

    boolean isDptBoosterDone();

    boolean isVitaminADone();

    String measlesDoneDate();

    String opvBoosterDoneDate();

    String dptBoosterDoneDate();

    String vitaminADoneDate();

    List<AlertDTO> alerts();

    AlertDTO getAlert(ChildServiceType measles);

    boolean isDataError();

    class ChildSickStatus {
        public static ChildSickStatus noDiseaseStatus = new ChildSickStatus(null, null, null);

        private String diseases;
        private String otherDiseases;
        private String date;

        public ChildSickStatus(String diseasesArg, String otherDiseasesArg, String dateArg) {
            this.diseases = diseasesArg;
            this.otherDiseases = otherDiseasesArg;
            this.date = dateArg;
        }

        public String diseases() {
            return getDiseasesCapitalizeIfAcronymsOrHumanize() + (isBlank(otherDiseases) ? ""
                    : (", " + StringUtil
                    .replaceAndHumanizeWithInitCapText(otherDiseases, AllConstants.SPACE,
                            AllConstants.COMMA_WITH_SPACE)));
        }

        private String getDiseasesCapitalizeIfAcronymsOrHumanize() {
            return StringUtil.replaceAndHumanizeWithInitCapText(
                    illnessAcronyms.contains(diseases) ? diseases.toUpperCase() : diseases,
                    AllConstants.SPACE, AllConstants.COMMA_WITH_SPACE);
        }

        public String date() {
            return DateUtil.formatDate(date);
        }
    }
}
