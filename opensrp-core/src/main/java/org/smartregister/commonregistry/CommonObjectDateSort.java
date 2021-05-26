package org.smartregister.commonregistry;

import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.SortOption;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Raihan Ahmed on 3/22/15.
 */
public class CommonObjectDateSort implements SortOption {

    String field;
    ByColumnAndByDetails byColumnAndByDetails;
    Comparator<SmartRegisterClient> commoncomparator = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient oneClient, SmartRegisterClient anotherClient2) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient)
                    oneClient;
            CommonPersonObjectClient commonPersonObjectClient2 = (CommonPersonObjectClient)
                    anotherClient2;
            switch (byColumnAndByDetails) {
                case byColumn:
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        Date date1 = dateFormat
                                .parse(commonPersonObjectClient.getColumnmaps().get(field));
                        Date date2 = dateFormat
                                .parse(commonPersonObjectClient2.getColumnmaps().get(field));

                        return date1.compareTo(date2);
                    } catch (Exception e) {
                        break;
                    }

                case byDetails:
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        Date date1 = dateFormat
                                .parse(commonPersonObjectClient.getDetails().get(field));
                        Date date2 = dateFormat
                                .parse(commonPersonObjectClient2.getDetails().get(field));
                        return date1.compareTo(date2);
                    } catch (Exception e) {
                        break;
                    }

            }
            return 0;
        }
    };

    public CommonObjectDateSort(ByColumnAndByDetails byColumnAndByDetails, String field) {
        this.byColumnAndByDetails = byColumnAndByDetails;
        this.field = field;
    }

    @Override
    public String name() {
        return field;
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, commoncomparator);
        return allClients;
    }

    public enum ByColumnAndByDetails {
        byColumn, byDetails
    }
}
