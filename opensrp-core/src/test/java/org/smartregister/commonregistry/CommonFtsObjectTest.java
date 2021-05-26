package org.smartregister.commonregistry;

import android.util.Pair;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.BaseUnitTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Vincent Karuri on 14/04/2020
 */
public class CommonFtsObjectTest extends BaseUnitTest {

    private CommonFtsObject commonFtsObject;
    private final String[] tables = new String[]{"table1", "table2", "table3"};

    @Before
    public void setUp() {
        commonFtsObject = new CommonFtsObject(tables);
    }

    @Test
    public void testSearchTableNameShouldReturnCorrectSearchTableName() {
        assertEquals("table1_search", commonFtsObject.searchTableName("table1"));
    }

    @Test
    public void testContainsTableShouldReturnCorrectStatus() {
        assertTrue(commonFtsObject.containsTable("table1"));
        assertFalse(commonFtsObject.containsTable("table100"));
    }

    @Test
    public void testGetAlertFilterVisitCodesShouldGetCorrectCodes() {
        String[] filterCodes = new String[]{"filter1", "filter2"};
        commonFtsObject.updateAlertFilterVisitCodes(filterCodes);
        assertEquals(filterCodes, commonFtsObject.getAlertFilterVisitCodes());
    }

    @Test
    public void testUpdateSearchFieldsShouldCorrectlyUpdateSearchFields() {
        String[] searchFields = new String[]{"search_field1", "search_field2"};
        commonFtsObject.updateSearchFields("table1", searchFields);
        assertEquals(searchFields, commonFtsObject.getSearchFields("table1"));
    }

    @Test
    public void testUpdateSortFieldsShouldCorrectlyUpdateSortFields() {
        String[] sortFields = new String[]{"sort_field1", "sort_field2"};
        commonFtsObject.updateSortFields("table1", sortFields);
        assertEquals(sortFields, commonFtsObject.getSortFields("table1"));
    }

    @Test
    public void testUpdateMainConditionsShouldCorrectlyUpdateMainConditions() {
        String[] mainConditions = new String[]{"main_condition_1", "main_condition_2"};
        commonFtsObject.updateMainConditions("table1", mainConditions);
        assertEquals(mainConditions, commonFtsObject.getMainConditions("table1"));
    }

    @Test
    public void testUpdateCustomRelationalIdShouldCorrectlyUpdateCustomRelationalId() {
        commonFtsObject.updateCustomRelationalId("table1", "relation_id_1");
        assertEquals("relation_id_1", commonFtsObject.getCustomRelationalId("table1"));
    }

    @Test
    public void testUpdateAlertScheduleMapShouldCorrectlyUpdateAlertScheduleMap() {
        Map<String, Pair<String, Boolean>> alertsScheduleMap = new HashMap<>();
        alertsScheduleMap.put("BCG", new Pair<>("dose1", true));
        commonFtsObject.updateAlertScheduleMap(alertsScheduleMap);
        assertEquals("BCG", commonFtsObject.getAlertScheduleName("bcg"));
        assertNull(commonFtsObject.getAlertScheduleName("does_not_exist"));
        assertEquals("dose1", commonFtsObject.getAlertBindType("bcg"));
        assertNull(commonFtsObject.getAlertBindType("does_not_exist"));
    }

    @Test
    public void testGetTablesShouldGetAllTables() {
        assertEquals(tables, commonFtsObject.getTables());
    }

    @Test
    public void testAlertUpdateVisitCodeShouldCorrectlyUpdateCode() {
        Map<String, Pair<String, Boolean>> alertsScheduleMap = new HashMap<>();
        alertsScheduleMap.put("BCG", new Pair<>("dose1", true));
        commonFtsObject.updateAlertScheduleMap(alertsScheduleMap);
        assertTrue(commonFtsObject.alertUpdateVisitCode("BCG"));
        assertNull(commonFtsObject.alertUpdateVisitCode(null));
        assertNull(commonFtsObject.alertUpdateVisitCode("does_not_exist"));
    }
}
