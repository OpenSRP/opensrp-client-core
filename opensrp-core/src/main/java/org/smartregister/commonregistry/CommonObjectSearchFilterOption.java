package org.smartregister.commonregistry;

import org.smartregister.CoreLibrary;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.FilterOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raihan Ahmed on 6/21/15.
 */
public class CommonObjectSearchFilterOption implements FilterOption {
    private final String criteria;
    ArrayList<FilterOptionsForSearch> filterOptions;

    public CommonObjectSearchFilterOption(String criteria, ArrayList<FilterOptionsForSearch>
            filterMaps) {
        this.criteria = criteria;
        this.filterOptions = filterMaps;

    }

    @Override
    public String name() {
        return "Search";
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        for (int i = 0; i < filterOptions.size(); i++) {

            switch (filterOptions.get(i).byColumnAndByDetails) {
                case byColumn:
                    return ((CommonPersonObjectClient) client).getColumnmaps()
                            .get(filterOptions.get(i).fieldName).trim().toLowerCase()
                            .contains(criteria.trim().toLowerCase());
                case byDetails:
                    return (((CommonPersonObjectClient) client).getDetails()
                            .get(filterOptions.get(i).fieldName) != null
                            ? ((CommonPersonObjectClient) client).getDetails()
                            .get(filterOptions.get(i).fieldName) : "").trim().toLowerCase().
                            contains(criteria.toLowerCase());
                case byChildren:
                    CommonPersonObjectClient currentclient = (CommonPersonObjectClient) client;
                    AllCommonsRepository allchildRepository = CoreLibrary.getInstance().context().
                            allCommonsRepositoryobjects(filterOptions.get(i).childName);
                    ArrayList<String> list = new ArrayList<String>();
                    list.add((currentclient.entityId()));
                    List<CommonPersonObject> allchild = allchildRepository.
                            findByRelational_IDs(list);

                    for (int j = 0; j < allchild.size(); j++) {
                        switch (filterOptions.get(i).byChildColumnAndByDetails) {
                            case byDetails:
                                if (allchild.get(j).getDetails().get(filterOptions.get(i).fieldName)
                                        != null) {
                                    if (allchild.get(i).getDetails()
                                            .get(filterOptions.get(i).fieldName).toLowerCase()
                                            .trim().
                                                    contains(criteria.toLowerCase().trim())) {
                                        return true;
                                    }
                                }
                                break;
                            case byColumn:
                                if (allchild.get(j).getColumnmaps()
                                        .get(filterOptions.get(i).fieldName) != null) {
                                    if (allchild.get(i).getColumnmaps()
                                            .get(filterOptions.get(i).fieldName).toLowerCase()
                                            .trim().
                                                    contains(criteria.toLowerCase().trim())) {
                                        return true;
                                    }
                                }
                                break;
                        }
                    }
                    break;
            }
        }
        return false;
    }

    public enum ByColumnAndByDetails {
        byColumn, byDetails, byChildren
    }

    static class FilterOptionsForSearch {
        String childName;
        ByColumnAndByDetails byColumnAndByDetails;
        ByColumnAndByDetails byChildColumnAndByDetails;
        String fieldName;

        FilterOptionsForSearch(ByColumnAndByDetails byColumnAndByDetailsArg, String fieldNameArg) {
            byColumnAndByDetails = byColumnAndByDetailsArg;
            fieldName = fieldNameArg;
        }

        FilterOptionsForSearch(String childNameArg, ByColumnAndByDetails byColumnAndByDetailsArg,
                               String fieldNameArg, ByColumnAndByDetails
                                       byChildColumnAndByDetailsArg) {
            childName = childNameArg;
            byColumnAndByDetails = byColumnAndByDetailsArg;
            fieldName = fieldNameArg;
            byChildColumnAndByDetails = byChildColumnAndByDetailsArg;
        }
    }
}
