package org.smartregister.cursoradapter;

import org.smartregister.view.contract.SmartRegisterClients;

/**
 * Created by Raihan Ahmed on 3/22/15.
 */
public class CursorCommonObjectSort implements CursorSortOption {

    String sortOptionName;
    String query;

    public CursorCommonObjectSort(String sortOptionName, String sortQuery) {
        this.query = sortQuery;
        this.sortOptionName = sortOptionName;
    }

    @Override
    public String name() {
        return sortOptionName;
    }

    @Override
    public String sort() {
        return query;
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        return null;
    }
}
