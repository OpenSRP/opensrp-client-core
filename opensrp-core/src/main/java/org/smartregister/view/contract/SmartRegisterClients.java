package org.smartregister.view.contract;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import java.util.ArrayList;

public class SmartRegisterClients extends ArrayList<SmartRegisterClient> {

    //#TODO: REMOVE THIS METHOD AND USE BELOW METHOD
    public SmartRegisterClients applyFilter(final FilterOption villageFilter, final
    ServiceModeOption serviceModeOption, final FilterOption searchFilter, SortOption sortOption) {
        SmartRegisterClients results = new SmartRegisterClients();
        Iterables.addAll(results, Iterables.filter(this, new Predicate<SmartRegisterClient>() {
            @Override
            public boolean apply(SmartRegisterClient client) {
                return villageFilter.filter(client) && searchFilter.filter(client);
            }
        }));

        serviceModeOption.apply();
        return sortOption.sort(results);
    }

    public SmartRegisterClients applyFilterWithFP(final ServiceModeOption serviceModeOption,
                                                  SortOption sortOption, final FilterOption...
                                                          filterOptions) {
        SmartRegisterClients results = new SmartRegisterClients();

        Iterables.addAll(results, Iterables.filter(this, new Predicate<SmartRegisterClient>() {
            @Override
            public boolean apply(SmartRegisterClient client) {
                boolean isClientToBeFiltered = true;
                for (FilterOption filterOption : filterOptions) {
                    isClientToBeFiltered = isClientToBeFiltered && filterOption.filter(client);
                }
                return isClientToBeFiltered;
            }
        }));

        serviceModeOption.apply();
        return sortOption.sort(results);
    }

}
