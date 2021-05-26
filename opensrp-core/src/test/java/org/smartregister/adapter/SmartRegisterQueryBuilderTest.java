package org.smartregister.adapter;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Richard Kareko on 5/12/20.
 */

public class SmartRegisterQueryBuilderTest extends BaseUnitTest {

    private String selectquery;
    private SmartRegisterQueryBuilder smartRegisterQueryBuilder;

    @Before
    public void setUp() {
        selectquery = "SELECT COUNT(*) FROM table1";
        smartRegisterQueryBuilder = new SmartRegisterQueryBuilder(selectquery);
    }

    @Test
    public void testGetSelectQuery() {
        assertEquals(selectquery, smartRegisterQueryBuilder.getSelectquery());
    }

    @Test
    public void testSetSelectQuery() {
        String expectedQuery = "SELECT * FROM task";
        smartRegisterQueryBuilder.setSelectquery(expectedQuery);
        assertEquals(expectedQuery, Whitebox.getInternalState(smartRegisterQueryBuilder, "Selectquery"));
    }

    @Test
    public void testQueryForCountOnRegisters() {
        String expectedTable = "task";
        String expectedCondition = "status IS NULL";

        String actualQuery = smartRegisterQueryBuilder.queryForCountOnRegisters(expectedTable, expectedCondition);
        assertEquals("SELECT COUNT (*)  FROM task WHERE status IS NULL", actualQuery);
    }

    @Test
    public void testQueryForCountOnRegistersWithoutCondition() {
        String expectedTable = "task";

        String actualQuery = smartRegisterQueryBuilder.queryForCountOnRegisters(expectedTable, null);
        assertEquals("SELECT COUNT (*)  FROM task", actualQuery);
    }

    @Test
    public void testSelectInitiateMainTable() {
        String[] tableNames = {"event"};
        String[] columns = {"createdAt", "baseEntityId"};

        String actualQuery = smartRegisterQueryBuilder.selectInitiateMainTable(tableNames, columns);
        assertEquals("Select event.id as _id , createdAt , baseEntityId From event", actualQuery);
    }

    @Test
    public void testSelectInitiateMainTableCounts() {
        String actualQuery = smartRegisterQueryBuilder.selectInitiateMainTableCounts("event");
        assertEquals("SELECT COUNT(*) FROM event", actualQuery);
    }

    @Test
    public void testJoinWithalertsWithAlertName() {
        String actualQuery = smartRegisterQueryBuilder.joinwithALerts("table1", "Alert1");
        assertEquals("SELECT COUNT(*) FROM table1 LEFT JOIN alerts  ON table1.id = alerts.caseID AND  alerts.scheduleName = 'Alert1'", actualQuery);
    }

    @Test
    public void testJoinWithalerts() {
        String actualQuery = smartRegisterQueryBuilder.joinwithALerts("table1");
        assertEquals("SELECT COUNT(*) FROM table1 LEFT JOIN alerts  ON table1.id = alerts.caseID ", actualQuery);
    }

    @Test
    public void testCustomJoin() {
        String actualQuery = smartRegisterQueryBuilder.customJoin("JOIN table2 ON table1.id = table2.entityId");
        assertEquals("SELECT COUNT(*) FROM table1 JOIN table2 ON table1.id = table2.entityId", actualQuery);
    }

    @Test
    public void testToString() {
        assertEquals(selectquery, smartRegisterQueryBuilder.toString());
    }

    @Test
    public void testToStringFts() {
        List<String> ids = new ArrayList<>();
        ids.add("id1");
        ids.add("id2");

        String actualQuery = smartRegisterQueryBuilder.toStringFts(ids, "id");
        assertEquals("SELECT COUNT(*) FROM table1 WHERE id IN ('id1','id2') ", actualQuery);
    }

    @Test
    public void testToStringFtsWithWhereClause() {
        List<String> ids = new ArrayList<>();
        ids.add("id3");
        ids.add("id4");
        smartRegisterQueryBuilder.setSelectquery("SELECT COUNT(*) FROM table1 WHERE id IN ('id1','id2')");
        String actualQuery = smartRegisterQueryBuilder.toStringFts(ids, "id");
        assertEquals("SELECT COUNT(*) FROM table1  WHERE id IN ('id3','id4') ", actualQuery);
    }

    @Test
    public void testToStringFtsWithSortBy() {
        List<String> ids = new ArrayList<>();
        ids.add("id4");
        ids.add("id5");

        String query = "SELECT COUNT(*) FROM table1 JOIN table2 ON table1.id = table2.entityId WHERE table1.id IS NOT NULL";
        smartRegisterQueryBuilder.setSelectquery(query);

        String actualQuery = smartRegisterQueryBuilder.toStringFts(ids, "table1", "id", "created_at ASC");
        assertEquals("SELECT COUNT(*) FROM table1 JOIN table2 ON table1.id = table2.entityId  WHERE table1.id IN ('id4','id5')  ORDER BY table1.created_at ASC", actualQuery);

    }

    @Test
    public void testSearchQueryFts() {
        String expectedQuery = "SELECT object_id FROM table1_search WHERE object_id " +
                "IN ( SELECT object_id FROM table1_search WHERE where id in (1,2,3) " +
                "AND phrase MATCH 'John*'  UNION SELECT object_relational_id " +
                "FROM table2_search WHERE phrase MATCH 'John*'  ) " +
                "ORDER BY created_at ASC LIMIT 0,10";
        String actualQuery = smartRegisterQueryBuilder.searchQueryFts("table1",
                "table2", "where id in (1,2,3)", "John",
                "created_at ASC", 10, 0);
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testSearchQueryFtsWithoutSearchJoinTableAndFilter() {
        String expectedQuery = "SELECT object_id FROM table1_search WHERE where id in (1,2,3) ORDER BY created_at ASC LIMIT 0,10";
        String actualQuery = smartRegisterQueryBuilder.searchQueryFts("table1",
                "", "where id in (1,2,3)", "",
                "created_at ASC", 10, 0);
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testCountQueryFts() {
        String expectedQuery = "SELECT COUNT(object_id) FROM table1_search " +
                "WHERE where id in (1,2,3) AND phrase MATCH 'John*'  " +
                "UNION SELECT object_relational_id FROM table2_search WHERE phrase MATCH 'John*' ";
        String actualQuery = smartRegisterQueryBuilder.countQueryFts("table1",
                "table2", "where id in (1,2,3)", "John");
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testCountQueryFtsWithoutSearchJoinTableAndFilter() {
        String expectedQuery = "SELECT COUNT(object_id) FROM table1_search WHERE where id in (1,2,3)";
        String actualQuery = smartRegisterQueryBuilder.countQueryFts("table1",
                "", "where id in (1,2,3)", "");
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testSearchQuerFtsWithSearchJoinTableArray() {
        String expectedQuery = "SELECT object_id FROM table1_search " +
                "WHERE object_id IN ( SELECT object_id FROM table1_search WHERE where id in (1,2,3) AND phrase MATCH 'John*'  " +
                "UNION  SELECT object_relational_id FROM table2_search WHERE phrase MATCH 'John*'  " +
                "UNION   SELECT object_relational_id FROM table3_search WHERE phrase MATCH 'John*'   ) " +
                "ORDER BY created_at ASC LIMIT 0,10";
        String[] searchJoinTables = {"table2", "table3"};
        String actualQuery = smartRegisterQueryBuilder.searchQueryFts("table1",
                searchJoinTables, "where id in (1,2,3)", "John",
                "created_at ASC", 10, 0);
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testSearchQuerFtsWithSearchJoinTableArrayMissingJoinTablesAndFilter() {
        String expectedQuery = "SELECT object_id FROM table1_search WHERE where id in (1,2,3) AND phrase MATCH 'John*'  ORDER BY created_at ASC LIMIT 0,10";
        String actualQuery = smartRegisterQueryBuilder.searchQueryFts("table1",
                new String[]{}, "where id in (1,2,3)", "John",
                "created_at ASC", 10, 0);
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testQueryForRegisterSortBasedOnRegisterAndAlert() {
        String expectedQuery = "Select event.id as _id , createdAt , baseEntityId FROM event LEFT JOIN alerts  ON event.id = alerts.caseID WHERE status IS NULL AND alerts.scheduleName = 'first_alert' ORDER BY CASE WHEN alerts.status = 'urgent' THEN '1'\n" +
                "WHEN alerts.status = 'upcoming' THEN '2'\n" +
                "WHEN alerts.status = 'normal' THEN '3'\n" +
                "WHEN alerts.status = 'expired' THEN '4'\n" +
                "WHEN alerts.status is Null THEN '5'\n" +
                "Else alerts.status END ASC";
        String tableName = "event";
        String[] columns = {"createdAt", "baseEntityId"};
        String condition = "status IS NULL";
        String alertName = "first_alert";
        String actualQuery = smartRegisterQueryBuilder.queryForRegisterSortBasedOnRegisterAndAlert(tableName, columns, condition, alertName);
        assertEquals(expectedQuery, actualQuery);
    }

    @Test
    public void testQueryForRegisterSortBasedOnRegisterAndAlertWithNullcondition() {
        String expectedQuery = "Select event.id as _id , createdAt , baseEntityId FROM event LEFT JOIN alerts  ON event.id = alerts.caseID WHERE alerts.scheduleName = 'first_alert' ORDER BY CASE WHEN alerts.status = 'urgent' THEN '1'\n" +
                "WHEN alerts.status = 'upcoming' THEN '2'\n" +
                "WHEN alerts.status = 'normal' THEN '3'\n" +
                "WHEN alerts.status = 'expired' THEN '4'\n" +
                "WHEN alerts.status is Null THEN '5'\n" +
                "Else alerts.status END ASC";
        String tableName = "event";
        String[] columns = {"createdAt", "baseEntityId"};
        String alertName = "first_alert";
        String actualQuery = smartRegisterQueryBuilder.queryForRegisterSortBasedOnRegisterAndAlert(tableName, columns,null, alertName);
        assertEquals(expectedQuery, actualQuery);
    }

}
