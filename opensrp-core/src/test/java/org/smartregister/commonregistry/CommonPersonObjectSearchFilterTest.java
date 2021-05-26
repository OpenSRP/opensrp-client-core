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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/*
 by Raihan Ahmed
 */
public class CommonPersonObjectSearchFilterTest extends BaseUnitTest {
    @Mock
    private AllCommonsRepository allCommonsRepository;
    @Mock
    private AllBeneficiaries allBeneficiaries;


    private Map<String, String> emptyDetails;

    private CommonObjectSearchFilterOption commonObjectSearchFilterOption;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        emptyDetails = Collections.emptyMap();
        CommonPersonObjectController controller = new CommonPersonObjectController(allCommonsRepository,
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
    public void shouldSearchCommonObjectsByColumnName() throws Exception {
        CommonObjectSearchFilterOption.FilterOptionsForSearch filterOptionsForSearch = new
                CommonObjectSearchFilterOption.FilterOptionsForSearch(
                CommonObjectSearchFilterOption.ByColumnAndByDetails.byColumn,
                "name");
        ArrayList<CommonObjectSearchFilterOption.FilterOptionsForSearch> List_Of_Filters = new
                ArrayList<CommonObjectSearchFilterOption.FilterOptionsForSearch>();
        List_Of_Filters.add(filterOptionsForSearch);
        commonObjectSearchFilterOption = new CommonObjectSearchFilterOption("Woman A",
                List_Of_Filters);

        Map<String, String> column1 = EasyMap.create("name", "Woman A").map();
        Map<String, String> column2 = EasyMap.create("name", "Woman B").map();
        Map<String, String> column3 = EasyMap.create("name", "Woman C").map();

        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",
                emptyDetails,
                "Woman A");
        expectedClient1.setColumnmaps(column1);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",
                emptyDetails,
                "Woman B");
        expectedClient2.setColumnmaps(column2);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",
                emptyDetails,
                "Woman C");
        expectedClient3.setColumnmaps(column3);

        Assert.assertEquals(true, commonObjectSearchFilterOption.filter(expectedClient1));
        Assert.assertEquals(false, commonObjectSearchFilterOption.filter(expectedClient2));
        Assert.assertEquals(false, commonObjectSearchFilterOption.filter(expectedClient3));
    }

    @Test
    public void shouldSearchCommonObjectsByDetailName() throws Exception {

        CommonObjectSearchFilterOption.FilterOptionsForSearch filterOptionsForSearch = new
                CommonObjectSearchFilterOption.FilterOptionsForSearch(
                CommonObjectSearchFilterOption.ByColumnAndByDetails.byDetails,
                "name");
        ArrayList<CommonObjectSearchFilterOption.FilterOptionsForSearch> List_Of_Filters = new
                ArrayList<CommonObjectSearchFilterOption.FilterOptionsForSearch>();
        List_Of_Filters.add(filterOptionsForSearch);
        commonObjectSearchFilterOption = new CommonObjectSearchFilterOption("Woman A",
                List_Of_Filters);
        Map<String, String> detail1 = EasyMap.create("name", "Woman A").map();
        Map<String, String> detail2 = EasyMap.create("name", "Woman B").map();
        Map<String, String> detail3 = EasyMap.create("name", "Woman C").map();

        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",
                detail1,
                "Woman A");
        expectedClient1.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",
                detail2,
                "Woman B");
        expectedClient2.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",
                detail3,
                "Woman C");
        expectedClient3.setColumnmaps(emptyDetails);

        Assert.assertEquals(true, commonObjectSearchFilterOption.filter(expectedClient1));
        Assert.assertEquals(false, commonObjectSearchFilterOption.filter(expectedClient2));
        Assert.assertEquals(false, commonObjectSearchFilterOption.filter(expectedClient3));
    }
}
