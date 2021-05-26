package org.smartregister.commonregistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.util.Cache;
import org.smartregister.util.EasyMap;
import org.smartregister.view.contract.SmartRegisterClients;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/*
 by Raihan Ahmed
 */
public class CommonPersonObjectSortTest extends BaseUnitTest {
    @Mock
    private AllCommonsRepository allCommonsRepository;
    @Mock
    private AllBeneficiaries allBeneficiaries;

    private CommonPersonObjectController controller;
    private Map<String, String> emptyDetails;

    private CommonObjectSort commonObjectSort;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        emptyDetails = Collections.emptyMap();
        controller = new CommonPersonObjectController(allCommonsRepository, allBeneficiaries, new Cache<String>(), new Cache<CommonPersonObjectClients>(), "name", "bindtype", "name", CommonPersonObjectController.ByColumnAndByDetails.byDetails);
    }

    @Test
    public void shouldSortCommonObjectsByColumnName() throws Exception {

        commonObjectSort = new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byColumn, false, "name", "name");

        Map<String, String> column1 = EasyMap.create("name", "Woman A").map();
        Map<String, String> column2 = EasyMap.create("name", "Woman B").map();
        Map<String, String> column3 = EasyMap.create("name", "Woman C").map();

        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1", emptyDetails, "Woman A");
        expectedClient1.setColumnmaps(column1);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2", emptyDetails, "Woman B");
        expectedClient2.setColumnmaps(column2);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3", emptyDetails, "Woman C");
        expectedClient3.setColumnmaps(column3);

        SmartRegisterClients clients = new SmartRegisterClients();
        clients.add(expectedClient2);
        clients.add(expectedClient3);
        clients.add(expectedClient1);
        //        Gson gson = new Gson();
//        String objectlist = gson.toJson(asList(expectedClient1, expectedClient2, expectedClient3));

//        List<CommonPersonObjectClient> actualClients = new Gson().fromJson(clients, new TypeToken<List<CommonPersonObjectClient>>() {
//        }.getType());
        Assert.assertEquals(Arrays.asList(expectedClient1, expectedClient2, expectedClient3), commonObjectSort.sort(clients));
//        assertEquals(objectlist,clients);
    }

    @Test
    public void shouldSortCommonObjectsByDetailName() throws Exception {

        commonObjectSort = new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails, false, "name", "name");

        Map<String, String> detail1 = EasyMap.create("name", "Woman A").map();
        Map<String, String> detail2 = EasyMap.create("name", "Woman B").map();
        Map<String, String> detail3 = EasyMap.create("name", "Woman C").map();

        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1", detail1, "Woman A");
        expectedClient1.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2", detail2, "Woman B");
        expectedClient2.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3", detail3, "Woman C");
        expectedClient3.setColumnmaps(emptyDetails);

        SmartRegisterClients clients = new SmartRegisterClients();
        clients.add(expectedClient2);
        clients.add(expectedClient3);
        clients.add(expectedClient1);

        Assert.assertEquals(Arrays.asList(expectedClient1, expectedClient2, expectedClient3), commonObjectSort.sort(clients));

    }

    @Test
    public void shouldSortCommonObjectsByInteger() throws Exception {

        commonObjectSort = new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails, true, "HID", "HID");

        Map<String, String> detail1 = EasyMap.create("HID", "1").map();
        Map<String, String> detail2 = EasyMap.create("HID", "2").map();
        Map<String, String> detail3 = EasyMap.create("HID", "3").map();

        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1", detail1, "Woman A");
        expectedClient1.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2", detail2, "Woman B");
        expectedClient2.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3", detail3, "Woman C");
        expectedClient3.setColumnmaps(emptyDetails);

        SmartRegisterClients clients = new SmartRegisterClients();
        clients.add(expectedClient2);
        clients.add(expectedClient3);
        clients.add(expectedClient1);

        Assert.assertEquals(Arrays.asList(expectedClient1, expectedClient2, expectedClient3), commonObjectSort.sort(clients));

    }

}
