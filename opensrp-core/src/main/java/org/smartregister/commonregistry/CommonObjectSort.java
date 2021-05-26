package org.smartregister.commonregistry;

import androidx.annotation.NonNull;

import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.SortOption;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Raihan Ahmed on 3/22/15.
 */
class CommonObjectSort implements SortOption {

    private String field;
    private ByColumnAndByDetails byColumnAndByDetails;
    private boolean isInteger;
    private String sortOptionName;
    private Comparator<SmartRegisterClient> commonComparator = new
            Comparator<SmartRegisterClient>() {
                @Override
                public int compare(SmartRegisterClient smartRegisterClient, SmartRegisterClient
                        smartRegisterClient2) {
                    CommonPersonObjectClient client = (CommonPersonObjectClient) smartRegisterClient;
                    CommonPersonObjectClient client2 = (CommonPersonObjectClient) smartRegisterClient2;
                    boolean isDetails;

                    switch (byColumnAndByDetails) {
                        case byColumn:
                            isDetails = false;
                            break;
                        case byDetails:
                            isDetails = true;
                            break;
                        default:
                            return 0;
                    }

                    String fieldValue = getFieldValue(client, isDetails);
                    String fieldValue2 = getFieldValue(client2, isDetails);

                    return isInteger ? Integer.valueOf(fieldValue).compareTo(Integer.valueOf(fieldValue2))
                            : fieldValue.compareTo(fieldValue2);
                }
            };

    CommonObjectSort(ByColumnAndByDetails byColumnAndByDetailsArg, boolean isIntegerArg, String
            fieldArg, String sortOptionNameArg) {
        byColumnAndByDetails = byColumnAndByDetailsArg;
        isInteger = isIntegerArg;
        field = fieldArg;
        sortOptionName = sortOptionNameArg;
    }

    @Override
    public String name() {
        return sortOptionName;
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, commonComparator);
        return allClients;
    }

    @NonNull
    private String getFieldValue(CommonPersonObjectClient commonPersonObjectClient, boolean
            isDetails) {
        String defaultValue = isInteger ? "0" : "";
        Map<String, String> valueMap = isDetails ? commonPersonObjectClient.getDetails()
                : commonPersonObjectClient.getColumnmaps();
        String detailsFieldValue = valueMap.get(field);

        return (detailsFieldValue != null ? detailsFieldValue : defaultValue).trim().toLowerCase();
    }

    public enum ByColumnAndByDetails {
        byColumn, byDetails
    }
}
