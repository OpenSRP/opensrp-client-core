package org.smartregister.commonregistry;

import android.content.ContentValues;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.TimelineEventRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/*
 by Raihan Ahmed
 */
public class AllCommonsRepositoryTest extends BaseUnitTest {

    @Mock
    private CommonRepository personRepository;
    @Mock
    private TimelineEventRepository timelineEventRepository;
    @Mock
    private AlertRepository alertRepository;

    private AllCommonsRepository allCommonsRepository;

    @Captor
    private ArgumentCaptor<HashMap> mapArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        allCommonsRepository = new AllCommonsRepository(personRepository,
                alertRepository,
                timelineEventRepository);
    }

    @Test
    public void ShouldReturnAllPersonObjectOfaCertainTypeFromRepository() {
        List<CommonPersonObject> expectedpersonobjects = Arrays.asList(new CommonPersonObject(
                "case1",
                "relationid1",
                new HashMap<String, String>(),
                ""));
        when(personRepository.allcommon()).thenReturn(expectedpersonobjects);
        assertEquals(expectedpersonobjects, personRepository.allcommon());
    }

    @Test
    public void ShouldShowcount() throws Exception {
        when(personRepository.count()).thenReturn((long) 8);
        assertEquals(personRepository.count(), (long) 8);

    }

    @Test
    public void testAll() {
        allCommonsRepository.all();
        verify(personRepository).allcommon();
    }

    @Test
    public void testFindByCaseId() {
        allCommonsRepository.findByCaseID("case 1");
        verify(personRepository).findByCaseID("case 1");
    }

    @Test
    public void testFindHHByGOBHHID() {
        allCommonsRepository.findHHByGOBHHID("gobhhid 1");
        verify(personRepository).findHHByGOBHHID("gobhhid 1");
    }

    @Test
    public void testCount() {
        allCommonsRepository.count();
        verify(personRepository).count();
    }

    @Test
    public void testFindByCaseIds() {
        List<String> expectedCaseIds = new ArrayList<>();
        expectedCaseIds.add("case 1");
        expectedCaseIds.add("case 2");

        allCommonsRepository.findByCaseIDs(expectedCaseIds);
        verify(personRepository).findByCaseIDs(new String[] {expectedCaseIds.get(0), expectedCaseIds.get(1)});
    }

    @Test
    public void testFindByRelationIds() {
        List<String> expectedRelationIds = new ArrayList<>();
        expectedRelationIds.add("relation id 1");
        expectedRelationIds.add("relation id 2");

        allCommonsRepository.findByRelationalIDs(expectedRelationIds);
        verify(personRepository).findByRelationalIDs(new String[] {expectedRelationIds.get(0), expectedRelationIds.get(1)});
    }

    @Test
    public void testFindByRelationIds2() {
        List<String> expectedRelationIds = new ArrayList<>();
        expectedRelationIds.add("relation id 1");
        expectedRelationIds.add("relation id 2");

        allCommonsRepository.findByRelational_IDs(expectedRelationIds);
        verify(personRepository).findByRelational_IDs(new String[] {expectedRelationIds.get(0), expectedRelationIds.get(1)});
    }

    @Test
    public void testClose() {
        String entityId = "Entity 1";
        allCommonsRepository.close(entityId);
        verify(alertRepository).deleteAllAlertsForEntity(entityId);
        verify(timelineEventRepository).deleteAllTimelineEventsForEntity(entityId);
        verify(personRepository).close(entityId);
    }

    @Test
    public void testMergeDetails() {
        String entityId = "Entity 1";
        Map<String, String> details = new HashMap<>();
        details.put("case id", "Case 1");

        allCommonsRepository.mergeDetails(entityId, details);
        verify(personRepository).mergeDetails(entityId, details);
    }

    @Test
    public void testUpdate() {
        String tableName = "sprayed_structures";
        String caseId = "Case 1";
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", "Ready");

        allCommonsRepository.update(tableName, contentValues, caseId);
        verify(personRepository).updateColumn(tableName,contentValues,caseId);

    }

    @Test
    public void testCustomQuery() {
        String tableName = "sprayed_structures";
        String sql = "SELECT count(*) FROM sprayed_structures WHERE status = ?";
        String[] selectionArgs = {"Complete"};

        allCommonsRepository.customQuery(sql, selectionArgs, tableName);
        verify(personRepository).customQuery(sql, selectionArgs, tableName);

    }

    @Test
    public void testCustomQueryForCompleteRow() {
        String tableName = "sprayed_structures";
        String sql = "SELECT count(*) FROM sprayed_structures WHERE status = ?";
        String[] selectionArgs = {"Complete"};

        allCommonsRepository.customQueryForCompleteRow(sql, selectionArgs, tableName);
        verify(personRepository).customQueryForCompleteRow(sql, selectionArgs, tableName);
    }

    @Test
    public void testUpdateSearchWithListToRemove() {
        String caseId = "Case id 1";
        String field = "status";
        String value = "synced";
        String[] listToremove = {"created", "deleted"};

        allCommonsRepository.updateSearch(caseId,field,value,listToremove);
        verify(personRepository).populateSearchValues(caseId,field,value,listToremove);
    }

    @Test
    public void testUpdateSearchWithListToRemoveMissingCaseId() {
        allCommonsRepository.updateSearch(null,"status","synced",new String[]{"created", "deleted"});
        verifyZeroInteractions(personRepository);
    }

    @Test
    public void testDeleteSearchRecord() {
        String caseId = "Case id 1";
        allCommonsRepository.deleteSearchRecord(caseId);
        verify(personRepository).deleteSearchRecord(caseId);
    }

    @Test
    public void testUpdateSearchWithNoCaseId() {
        allCommonsRepository.updateSearch("");
        verifyZeroInteractions(personRepository);
    }

    @Test
    public void testUpdateSearchWithCaseId() {
        String caseId = "Case id 1";
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", "Ready");
        when(personRepository.populateSearchValues(caseId)).thenReturn(contentValues);

        allCommonsRepository.updateSearch(caseId);
        verify(personRepository).searchBatchInserts(mapArgumentCaptor.capture());

        HashMap actualsearchMap = mapArgumentCaptor.getValue();
        assertNotNull(actualsearchMap);
        ContentValues actualContentValues = (ContentValues) actualsearchMap.get(caseId);
        assertNotNull(actualContentValues);
        assertEquals(1, actualContentValues.size());
        assertEquals("Ready", actualContentValues.getAsString("status"));

    }

    @Test
    public void testUpdateSearchWithNoCaseIdList() {
        allCommonsRepository.updateSearch(new ArrayList<>());
        verifyZeroInteractions(personRepository);
    }

    @Test
    public void testUpdateSearchWithCaseIdList() {
        String caseId1 = "Case id 1";
        String caseId2 = "Case id 2";
        List<String> caseIdList = new ArrayList<>();
        caseIdList.add(caseId1);
        caseIdList.add(caseId2);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("status", "Ready");
        doReturn(contentValues1).when(personRepository).populateSearchValues(caseId1);

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put("status", "Complete");
        doReturn(contentValues2).when(personRepository).populateSearchValues(caseId2);

        allCommonsRepository.updateSearch(caseIdList);
        verify(personRepository).searchBatchInserts(mapArgumentCaptor.capture());

        HashMap actualsearchMap = mapArgumentCaptor.getValue();
        assertNotNull(actualsearchMap);
        assertEquals(2, actualsearchMap.size());

        ContentValues actualContentValues1 = (ContentValues) actualsearchMap.get(caseId1);
        assertNotNull(actualContentValues1);
        assertEquals(1, actualContentValues1.size());
        assertEquals("Ready", actualContentValues1.getAsString("status"));

        ContentValues actualContentValues2 = (ContentValues) actualsearchMap.get(caseId2);
        assertNotNull(actualContentValues2);
        assertEquals(1, actualContentValues2.size());
        assertEquals("Complete", actualContentValues2.getAsString("status"));

    }

}
