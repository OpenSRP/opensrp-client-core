package org.smartregister.commonregistry;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.util.Cache;
import org.smartregister.util.EasyMap;
import org.smartregister.view.dialog.SortOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/*
 by Raihan Ahmed
 */
public class CommonPersonObjectControllerTest extends BaseUnitTest {

    @Mock
    private AllCommonsRepository allCommonsRepository;
    @Mock
    private AllBeneficiaries allBeneficiaries;

    @Mock
    private org.smartregister.Context context;

    private CommonPersonObjectController controller;
    private Map<String, String> emptyDetails;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context);
        emptyDetails = Collections.emptyMap();
        controller = new CommonPersonObjectController(allCommonsRepository,
                allBeneficiaries,
                new Cache<String>(),
                new Cache<CommonPersonObjectClients>(),
                "name",
                "bindtype",
                "name",
                CommonPersonObjectController
                        .ByColumnAndByDetails.byDetails);
    }

    @Test
    public void assertConstructorsCreateNonNullObjectsOnInstantiation() throws Exception {

        Assert.assertNotNull(new CommonPersonObjectController(allCommonsRepository,
                allBeneficiaries,
                new Cache<String>(),
                new Cache<CommonPersonObjectClients>(),
                "nameString",
                "bindtype",
                new ArrayList<ControllerFilterMap>(),
                CommonPersonObjectController.ByColumnAndByDetails.byDetails,
                "null_check_key",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails,
                Mockito.mock(SortOption.class)));

        Assert.assertNotNull(new CommonPersonObjectController(allCommonsRepository,
                allBeneficiaries,
                new Cache<String>(),
                new Cache<CommonPersonObjectClients>(),
                "nameString",
                "bindtype",
                "filterkey",
                "filtervalue",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails,
                "null_check_key",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails,
                Mockito.mock(SortOption.class)));

        Assert.assertNotNull(new CommonPersonObjectController(allCommonsRepository,
                allBeneficiaries,
                new Cache<String>(),
                new Cache<CommonPersonObjectClients>(),
                "nameString",
                "bindtype",
                new ArrayList<ControllerFilterMap>(),
                CommonPersonObjectController.ByColumnAndByDetails.byDetails,
                "null_check_key",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails));

        Assert.assertNotNull(new CommonPersonObjectController(allCommonsRepository,
                allBeneficiaries,
                new Cache<String>(),
                new Cache<CommonPersonObjectClients>(),
                "name",
                "bindtype",
                "nullCheckKey",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails));

        Assert.assertNotNull(new CommonPersonObjectController(allCommonsRepository,
                allBeneficiaries,
                new Cache<String>(),
                new Cache<CommonPersonObjectClients>(),
                "name",
                "bindtype",
                "nullCheckKey",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails,
                Mockito.mock(SortOption.class)
        ));

        Assert.assertNotNull(new CommonPersonObjectController(allCommonsRepository,
                allBeneficiaries,
                new Cache<String>(),
                new Cache<CommonPersonObjectClients>(),
                "nameString",
                "bindtype",
                "filterkey",
                "filtervalue",
                true,
                CommonPersonObjectController.ByColumnAndByDetails.byDetails,
                "null_check_key",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails,
                Mockito.mock(SortOption.class)));

    }

    @Test
    public void shouldSortECsByName() throws Exception {
        Map<String, String> personDetails1 = EasyMap.create("name", "Woman A").map();
        Map<String, String> personDetails2 = EasyMap.create("name", "Woman B").map();
        Map<String, String> personDetails3 = EasyMap.create("name", "Woman C").map();

        CommonPersonObject cpo2 = new CommonPersonObject("entity id 2",
                "relational id 2",
                personDetails2,
                "bindtype");
        cpo2.setColumnmaps(emptyDetails);
        CommonPersonObject cpo3 = new CommonPersonObject("entity id 3",
                "relational id 3",
                personDetails3,
                "bindtype");
        cpo3.setColumnmaps(emptyDetails);
        CommonPersonObject cpo1 = new CommonPersonObject("entity id 1",
                "relational id 1",
                personDetails1,
                "bindtype");
        cpo1.setColumnmaps(emptyDetails);

        Mockito.when(allCommonsRepository.all()).thenReturn(Arrays.asList(cpo2, cpo3, cpo1));
        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",
                personDetails1,
                "Woman A");
        expectedClient1.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",
                personDetails2,
                "Woman B");
        expectedClient2.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",
                personDetails3,
                "Woman C");
        expectedClient3.setColumnmaps(emptyDetails);

        String clients = controller.get();

        Gson gson = new Gson();
        String objectlist = gson.toJson(Arrays.asList(expectedClient1, expectedClient2, expectedClient3));
        Assert.assertEquals(objectlist, clients);
    }

    @Test
    public void shouldMapCommonObjectToCommonObjectClient() throws Exception {
        Map<String, String> details = EasyMap.create("name", "Woman A").map();

        CommonPersonObject commonpersonobject = new CommonPersonObject("entity id 1",
                "relational id 1",
                details,
                "bindtype");
        commonpersonobject.setColumnmaps(emptyDetails);
        Mockito.when(allCommonsRepository.all()).thenReturn(Arrays.asList(commonpersonobject));
        CommonPersonObjectClient expectedCommonObjectClient = new CommonPersonObjectClient(
                "entity id 1",
                details,
                "Woman A");
        expectedCommonObjectClient.setColumnmaps(emptyDetails);
        String clients = controller.get();
        Gson gson = new Gson();
        String objectlist = gson.toJson(Arrays.asList(expectedCommonObjectClient));
        Assert.assertEquals(objectlist, clients);
    }

    @Test
    public void shouldfilterNullsAccordingToFilterKey() throws Exception {
        Map<String, String> personDetails1 = EasyMap.create("name", "Woman A").map();
        Map<String, String> personDetails2 = EasyMap.create("name", "Woman B").map();
        Map<String, String> personDetails3 = emptyDetails;

        CommonPersonObject cpo2 = new CommonPersonObject("entity id 2",
                "relational id 2",
                personDetails2,
                "bindtype");
        cpo2.setColumnmaps(emptyDetails);
        CommonPersonObject cpo3 = new CommonPersonObject("entity id 3",
                "relational id 3",
                personDetails3,
                "bindtype");
        cpo3.setColumnmaps(emptyDetails);
        CommonPersonObject cpo1 = new CommonPersonObject("entity id 1",
                "relational id 1",
                personDetails1,
                "bindtype");
        cpo1.setColumnmaps(emptyDetails);

        Mockito.when(allCommonsRepository.all()).thenReturn(Arrays.asList(cpo2, cpo3, cpo1));
        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",
                personDetails1,
                "Woman A");
        expectedClient1.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",
                personDetails2,
                "Woman B");
        expectedClient2.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",
                personDetails3,
                "Woman C");
        expectedClient3.setColumnmaps(emptyDetails);

        String clients = controller.get();

        Gson gson = new Gson();
        String objectlist = gson.toJson(Arrays.asList(expectedClient1, expectedClient2));
        Assert.assertEquals(objectlist, clients);
    }

}
