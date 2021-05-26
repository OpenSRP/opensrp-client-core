package org.smartregister.cursoradapter;

import org.smartregister.view.contract.SmartRegisterClient;

public class CursorCommonObjectFilterOption implements CursorFilterOption {

    private final String filterOptionName;
    String filterString;

    public CursorCommonObjectFilterOption(String filteroptionname, String filterString) {
        this.filterString = filterString;
        this.filterOptionName = filteroptionname;
    }

    @Override
    public String name() {
        return filterOptionName;
    }

    @Override
    public String filter() {

        return filterString;
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return false;
    }
}
