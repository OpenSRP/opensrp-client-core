package org.smartregister.adapter;

import android.view.View;
import android.view.ViewGroup;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.BaseUnitTest;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.ECClient;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

public class SmartRegisterPaginatedAdapterTest extends BaseUnitTest {

    private String NameZERO = "Name0";
    private String NameFOURTYNINE = "Name49";
    private int TWENTY = 20;
    private int TWENTYONE = 21;
    private int THREE = 3;
    private int FOURTYNINE = 49;
    private int NINETEEN = 19;
    private int FOURTY = 40;
    private int FIFTY = 50;
    private int TWENTYFIVE = 25;
    private int FIVE = 5;
    private int ZERO = 0;
    private int NINE = 9;
    private int THIRTYNINE = 39;
    private int ONE = 1;
    private int TWO = 2;

    @Test
    public void assertshouldReturnZEROPageCountForZEROClients() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(ZERO);

        Assert.assertEquals(adapter.getCount(), ZERO);
        Assert.assertEquals(adapter.pageCount(), ZERO);
        Assert.assertEquals(adapter.currentPage(), ZERO);
        Assert.assertFalse(adapter.hasNextPage());
        Assert.assertFalse(adapter.hasPreviousPage());
    }

    @Test
    public void assertshouldReturnONEPageCountForTWENTYClients() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(TWENTY);
        Assert.assertEquals(adapter.getCount(), TWENTY);
        Assert.assertEquals(adapter.pageCount(), ONE);
        Assert.assertEquals(adapter.currentPage(), ZERO);
        Assert.assertFalse(adapter.hasNextPage());
        Assert.assertFalse(adapter.hasPreviousPage());
    }

    @Test
    public void assertshouldReturn2PageCountFor21Clients() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(TWENTYONE);
        Assert.assertEquals(adapter.getCount(), TWENTY);
        Assert.assertEquals(adapter.pageCount(), TWO);
        Assert.assertEquals(adapter.currentPage(), ZERO);
        Assert.assertTrue(adapter.hasNextPage());
        Assert.assertFalse(adapter.hasPreviousPage());

        adapter.nextPage();

        Assert.assertEquals(adapter.getCount(), ONE);
        Assert.assertEquals(adapter.currentPage(), ONE);
        Assert.assertFalse(adapter.hasNextPage());
        Assert.assertTrue(adapter.hasPreviousPage());

        adapter.previousPage();

        Assert.assertEquals(adapter.currentPage(), ZERO);
        Assert.assertTrue(adapter.hasNextPage());
        Assert.assertFalse(adapter.hasPreviousPage());
    }

    @Test
    public void assertshouldReturn3PageCountFor50Clients() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(FIFTY);
        Assert.assertEquals(adapter.pageCount(), THREE);
    }

    @Test
    public void assertgetItemShouldReturnRespectiveItem() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(FIFTY);
        Assert.assertEquals(((ECClient) adapter.getItem(ZERO)).name(), NameZERO);
        Assert.assertEquals(((ECClient) adapter.getItem(FOURTYNINE)).name(), NameFOURTYNINE);
    }

    @Test
    public void assertgetViewShouldDelegateCallToProviderGetViewWithProperClient() {
        FakeClientsProvider fakeClientsProvider = new FakeClientsProvider(getSmartRegisterClients(FIFTY));
        SmartRegisterPaginatedAdapter adapter = getAdapter(fakeClientsProvider);

        adapter.getView(ZERO, null, null);
        Assert.assertEquals(NameZERO, fakeClientsProvider.getViewCurrentClient.name());

        adapter.getView(FOURTYNINE, null, null);
        Assert.assertEquals(NameFOURTYNINE, fakeClientsProvider.getViewCurrentClient.name());
    }

    @Test
    public void assertgetItemIdShouldReturnTheActualPositionWithoutPagination() {
        FakeClientsProvider fakeClientsProvider = new FakeClientsProvider(getSmartRegisterClients(FIFTY));
        SmartRegisterPaginatedAdapter adapter = getAdapter(fakeClientsProvider);

        Assert.assertEquals(ZERO, adapter.getItemId(ZERO));
        Assert.assertEquals(NINETEEN, adapter.getItemId(NINETEEN));
        adapter.nextPage();
        Assert.assertEquals(TWENTY, adapter.getItemId(ZERO));
        Assert.assertEquals(THIRTYNINE, adapter.getItemId(NINETEEN));
        adapter.nextPage();
        Assert.assertEquals(FOURTY, adapter.getItemId(ZERO));
        Assert.assertEquals(FOURTYNINE, adapter.getItemId(NINE));
    }

    @Test
    public void assertupdateClientsShouldApplyFilterToShowOnlyFiveClients() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(FIFTY);
        Assert.assertEquals(THREE, adapter.pageCount());
        Assert.assertEquals(TWENTY, adapter.getCount());

        adapter.refreshList(null, null, null, null);

        Assert.assertEquals(ONE, adapter.pageCount());
        Assert.assertEquals(FIVE, adapter.getCount());
    }

    @Test
    public void assertpaginationShouldWorkFor25ClientsPerPage() {
        SmartRegisterPaginatedAdapter adapter = getAdapterWithFakeClients(FIFTY, TWENTYFIVE);
        Assert.assertEquals(TWO, adapter.pageCount());
        Assert.assertEquals(TWENTYFIVE, adapter.getCount());
    }

    private SmartRegisterPaginatedAdapter getAdapterWithFakeClients(int clientsCount) {
        return getAdapter(getFakeProvider(getSmartRegisterClients(clientsCount)));
    }

    private SmartRegisterPaginatedAdapter getAdapterWithFakeClients(int clientsCount, int clientsPerPage) {
        return getAdapter(clientsPerPage, getFakeProvider(getSmartRegisterClients(clientsCount)));
    }

    private SmartRegisterClients getSmartRegisterClients(int count) {
        SmartRegisterClients clients = new SmartRegisterClients();
        for (int i = 0; i < count; i++) {
            clients.add(getClient(i));
        }
        return clients;
    }

    private SmartRegisterClient getClient(int i) {
        return new ECClient("abcd" + i, "name" + i, "husband" + i, "village" + i, 1000 + i);
    }

    private FakeClientsProvider getFakeProvider(SmartRegisterClients clients) {
        return new FakeClientsProvider(clients);
    }

    private SmartRegisterPaginatedAdapter getAdapter(FakeClientsProvider provider) {
        return new SmartRegisterPaginatedAdapter(provider);
    }

    private SmartRegisterPaginatedAdapter getAdapter(int clientsPerPage, FakeClientsProvider provider) {
        return new SmartRegisterPaginatedAdapter(clientsPerPage, provider);
    }

    private class FakeClientsProvider implements SmartRegisterClientsProvider {
        private SmartRegisterClients clients;

        public SmartRegisterClient getViewCurrentClient;

        public FakeClientsProvider(SmartRegisterClients clients) {
            this.clients = clients;
        }

        @Override
        public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup) {
            this.getViewCurrentClient = client;
            return null;
        }

        @Override
        public SmartRegisterClients getClients() {
            return clients;
        }

        @Override
        public SmartRegisterClients updateClients(
                FilterOption villageFilter, ServiceModeOption serviceModeOption,
                FilterOption searchFilter, SortOption sortOption) {
            return getSmartRegisterClients(FIVE);
        }

        @Override
        public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

        }

        @Override
        public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
            return null;
        }
    }
}
