package org.smartregister.commonregistry;

import org.junit.Before;
import org.mockito.Mock;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.util.Cache;

import java.util.Collections;
import java.util.Map;

/*
 by Raihan Ahmed
 */

//FIXME Test failing
//@RunWith(RobolectricTestRunner.class)
public class CommonPersonObjectFilterOptionTest {
    @Mock
    private AllCommonsRepository allCommonsRepository;
    @Mock
    private AllBeneficiaries allBeneficiaries;

    private CommonPersonObjectController controller;
    private Map<String, String> emptyDetails;

    private CommonObjectFilterOption commonObjectFilterOption;

    @Before
    public void setUp() throws Exception {
        //initMocks(this);
        emptyDetails = Collections.emptyMap();
        controller = new CommonPersonObjectController(
                allCommonsRepository,
                allBeneficiaries,
                new Cache<String>(),
                new Cache<CommonPersonObjectClients>(),
                "name",
                "bindtype",
                "name",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails);
    }

    // TODO : fix failing test
    /*
    @Test
    public void shouldFilterCommonObjectsByColumnName() throws Exception {

        commonObjectFilterOption = new CommonObjectFilterOption("name","Woman A", CommonObjectFilterOption.ByColumnAndByDetails.byColumn,"name");

         Map<String, String> column1 = create("name", "Woman A").map();
        Map<String, String> column2 = create("name","Woman B").map();
        Map<String, String> column3= create("name","Woman C").map();




        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",emptyDetails,"Woman A");
        expectedClient1.setColumnmaps(column1);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",emptyDetails,"Woman B");
        expectedClient2.setColumnmaps(column2);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",emptyDetails,"Woman C");
        expectedClient3.setColumnmaps(column3);

       assertEquals(true, commonObjectFilterOption.filter(expectedClient1));
        assertEquals(false, commonObjectFilterOption.filter(expectedClient2));
        assertEquals(false, commonObjectFilterOption.filter(expectedClient3));
    }

    @Test
    public void shouldFilterCommonObjectsByDetailName() throws Exception {


        commonObjectFilterOption = new CommonObjectFilterOption("name","Woman A", CommonObjectFilterOption.ByColumnAndByDetails.byDetails, "name");


        Map<String, String> detail1 = create("name", "Woman A").map();
        Map<String, String> detail2 = create("name","Woman B").map();
        Map<String, String> detail3= create("name","Woman C").map();




        CommonPersonObjectClient expectedClient1 = new CommonPersonObjectClient("entity id 1",detail1,"Woman A");
        expectedClient1.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient2 = new CommonPersonObjectClient("entity id 2",detail2,"Woman B");
        expectedClient2.setColumnmaps(emptyDetails);
        CommonPersonObjectClient expectedClient3 = new CommonPersonObjectClient("entity id 3",detail3,"Woman C");
        expectedClient3.setColumnmaps(emptyDetails);

        assertEquals(true, commonObjectFilterOption.filter(expectedClient1));
        assertEquals(false, commonObjectFilterOption.filter(expectedClient2));
        assertEquals(false, commonObjectFilterOption.filter(expectedClient3));
    }
    */
}
