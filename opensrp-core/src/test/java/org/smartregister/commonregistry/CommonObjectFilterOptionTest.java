package org.smartregister.commonregistry;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.util.EasyMap;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.smartregister.commonregistry.CommonObjectFilterOption.ByColumnAndByDetails.byColumn;
import static org.smartregister.commonregistry.CommonObjectFilterOption.ByColumnAndByDetails.byDetails;

/**
 * Created by Richard Kareko on 5/5/20.
 */

public class CommonObjectFilterOptionTest extends BaseUnitTest {

    private String fieldname;
    private String criteria;
    private String filterOptionName;
    private Map<String, String> emptyMap;

    private CommonObjectFilterOption commonObjectFilterOption;

    @Before
    public void setUp() {
        emptyMap = Collections.emptyMap();
        fieldname = "name";
        criteria = "Woman A";
        filterOptionName = "gender";

        commonObjectFilterOption = new CommonObjectFilterOption(criteria, fieldname, byColumn, filterOptionName);
    }

    @Test
    public void testInitialization() {
        assertNotNull(commonObjectFilterOption);
        assertEquals(fieldname, commonObjectFilterOption.fieldname);
        assertEquals(criteria, Whitebox.getInternalState(commonObjectFilterOption, "criteria"));
        assertEquals(filterOptionName, Whitebox.getInternalState(commonObjectFilterOption, "filterOptionName"));
        assertEquals(byColumn, Whitebox.getInternalState(commonObjectFilterOption, "byColumnAndByDetails"));
    }

    @Test
    public void testFilterByColumn() {
        Map<String, String> column1 = EasyMap.create("name", "Woman A").map();
        CommonPersonObjectClient expectedClient = new CommonPersonObjectClient("entity id 1",
                emptyMap,
                "Woman A");
        expectedClient.setColumnmaps(column1);
        boolean filter = commonObjectFilterOption.filter(expectedClient);
        assertTrue(filter);
    }

    @Test
    public void testFilterOptionName() {
        assertEquals(filterOptionName, commonObjectFilterOption.name());
    }

    @Test
    public void testFilterByDetails() {
        commonObjectFilterOption = new CommonObjectFilterOption(criteria, fieldname, byDetails, filterOptionName);
        Map<String, String> detail = EasyMap.create("name", "Woman A").map();
        CommonPersonObjectClient expectedClient = new CommonPersonObjectClient("entity id 1",
                detail,
                "Woman A");
        expectedClient.setColumnmaps(emptyMap);
        boolean filter = commonObjectFilterOption.filter(expectedClient);
        assertTrue(filter);
    }

}
