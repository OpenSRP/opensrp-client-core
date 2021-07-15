package org.smartregister.cursoradapter;

import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.DefaultOptionsProvider;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 11/3/20.
 */
public class RecyclerViewFragmentMock extends RecyclerViewFragment {

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        SecuredNativeSmartRegisterActivity.DefaultOptionsProvider optionsProvider = mock(SecuredNativeSmartRegisterActivity.DefaultOptionsProvider.class);
        ServiceModeOption serviceModeOption = mock(ServiceModeOption.class);
        when(optionsProvider.serviceMode()).thenReturn(serviceModeOption);
        when(serviceModeOption.getHeaderProvider()).thenReturn(mock(SecuredNativeSmartRegisterActivity.ClientsHeaderProvider.class));
        when(optionsProvider.villageFilter()).thenReturn(mock(FilterOption.class));
        when(optionsProvider.sortOption()).thenReturn(mock(SortOption.class));
        return optionsProvider;
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {//do nothing
    }

    @Override
    protected void startRegistration() {//do nothing
    }

    @Override
    protected void onCreation() {//do nothing
    }
}
