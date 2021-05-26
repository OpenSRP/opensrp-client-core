package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.domain.PlanDefinition.PlanStatus.ACTIVE;
import static org.smartregister.domain.PlanDefinitionTest.fiPlanDefinitionJSON;
import static org.smartregister.domain.PlanDefinitionTest.gson;
import static org.smartregister.domain.PlanDefinitionTest.planDefinitionJSON;
import static org.smartregister.repository.PlanDefinitionRepository.ID;
import static org.smartregister.repository.PlanDefinitionRepository.JSON;
import static org.smartregister.repository.PlanDefinitionRepository.NAME;
import static org.smartregister.repository.PlanDefinitionSearchRepository.PLAN_ID;
import static org.smartregister.repository.PlanDefinitionSearchRepository.STATUS;

/**
 * Created by samuelgithengi on 5/8/19.
 */
public class PlanDefinitionRepositoryTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private PlanDefinitionRepository planDefinitionRepository;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private PlanDefinitionSearchRepository searchRepository;

    @Captor
    private ArgumentCaptor<ContentValues> contentValuesArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<String[]> argsCaptor;

    @Before
    public void setUp() {
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        planDefinitionRepository = new PlanDefinitionRepository();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testCreateTable() {
        PlanDefinitionRepository.createTable(sqLiteDatabase);
        verify(sqLiteDatabase).execSQL(stringArgumentCaptor.capture());
        assertEquals("CREATE TABLE plan_definition (_id VARCHAR NOT NULL PRIMARY KEY,json VARCHAR NOT NULL,status VARCHAR NOT NULL)",
                stringArgumentCaptor.getValue());
    }

    @Test
    public void testAddOrUpdate() {
        PlanDefinition planDefinition = gson.fromJson(planDefinitionJSON, PlanDefinition.class);
        int jurisdictionCount = planDefinition.getJurisdiction().size();
        planDefinitionRepository.addOrUpdate(planDefinition);
        verify(sqLiteDatabase, Mockito.times(jurisdictionCount + 1)).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());

        assertEquals((jurisdictionCount + 1) * 2, stringArgumentCaptor.getAllValues().size());
        assertEquals("plan_definition_search", stringArgumentCaptor.getAllValues().get(0));
        assertNull(stringArgumentCaptor.getAllValues().get(1));
        assertEquals(jurisdictionCount + 1, contentValuesArgumentCaptor.getAllValues().size());
        assertEquals(planDefinition.getIdentifier(), contentValuesArgumentCaptor.getAllValues().get(0).get(PLAN_ID));
        assertEquals(planDefinition.getName(), contentValuesArgumentCaptor.getAllValues().get(0).get(NAME));
        assertEquals(planDefinition.getStatus().value(), contentValuesArgumentCaptor.getAllValues().get(0).get(STATUS));
    }


    @Test
    public void testAddOrUpdateSavesSearchTable() {
        Whitebox.setInternalState(planDefinitionRepository, "searchRepository", searchRepository);

        PlanDefinition planDefinition = gson.fromJson(planDefinitionJSON, PlanDefinition.class);
        String jurisdictionId = UUID.randomUUID().toString();
        planDefinition.setJurisdiction(Collections.singletonList(new Jurisdiction(jurisdictionId)));
        planDefinitionRepository.addOrUpdate(planDefinition);
        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());

        assertEquals(2, stringArgumentCaptor.getAllValues().size());
        assertEquals("plan_definition", stringArgumentCaptor.getAllValues().get(0));
        assertNull(stringArgumentCaptor.getAllValues().get(1));
        assertEquals(3, contentValuesArgumentCaptor.getValue().size());
        assertEquals(planDefinition.getIdentifier(), contentValuesArgumentCaptor.getValue().get(ID));

        verify(searchRepository).addOrUpdate(planDefinition, jurisdictionId);
    }

    @Test
    public void testFindPlanDefinitionById() {
        when(sqLiteDatabase.rawQuery(anyString(), any(String[].class)))
                .thenReturn(getCursor());
        PlanDefinition planDefinition = planDefinitionRepository.findPlanDefinitionById("4708ca0a-d0d6-4199-bb1b-8701803c2d02");
        assertNotNull(planDefinition);
        assertEquals("4708ca0a-d0d6-4199-bb1b-8701803c2d02", planDefinition.getIdentifier());
        assertEquals(planDefinitionJSON, gson.toJson(planDefinition));
        verify(sqLiteDatabase).rawQuery("SELECT  json FROM plan_definition WHERE _id =?",
                new String[]{"4708ca0a-d0d6-4199-bb1b-8701803c2d02"});
    }

    @Test
    public void testFindPlanDefinitionByIdShouldReturnNull() {
        when(sqLiteDatabase.rawQuery(anyString(), any(String[].class)))
                .thenReturn(new MatrixCursor(new String[]{}));
        PlanDefinition planDefinition = planDefinitionRepository.findPlanDefinitionById("4708ca0a-d0d6-4199-bb1b-8701803c2d02");
        assertNull(planDefinition);
        verify(sqLiteDatabase).rawQuery("SELECT  json FROM plan_definition WHERE _id =?",
                new String[]{"4708ca0a-d0d6-4199-bb1b-8701803c2d02"});
    }

    @Test
    public void testFindPlanDefinitionByIdWithException() {
        doThrow(new SQLiteException()).when(sqLiteDatabase).rawQuery(anyString(), any(String[].class));
        PlanDefinition planDefinition = planDefinitionRepository.findPlanDefinitionById("4708ca0a-d0d6-4199-bb1b-8701803c2d02");
        assertNull(planDefinition);
        verify(sqLiteDatabase).rawQuery("SELECT  json FROM plan_definition WHERE _id =?",
                new String[]{"4708ca0a-d0d6-4199-bb1b-8701803c2d02"});
    }

    @Test
    public void testFindAllPlanDefinitions() {
        when(sqLiteDatabase.rawQuery(anyString(), argsCaptor.capture()))
                .thenReturn(getCursor());
        Set<PlanDefinition> planDefinitions = planDefinitionRepository.findAllPlanDefinitions();
        assertNotNull(planDefinitions);
        PlanDefinition planDefinition = planDefinitions.iterator().next();
        assertEquals("4708ca0a-d0d6-4199-bb1b-8701803c2d02", planDefinition.getIdentifier());
        assertEquals(planDefinitionJSON, gson.toJson(planDefinition));
        verify(sqLiteDatabase).rawQuery("SELECT json  FROM plan_definition WHERE status =?", new String[]{ACTIVE.value()});
    }

    @Test
    public void testFindAllPlanDefinitionsWithExceptions() {
        doThrow(new SQLiteException()).when(sqLiteDatabase).rawQuery(anyString(), argsCaptor.capture());
        Set<PlanDefinition> planDefinitions = planDefinitionRepository.findAllPlanDefinitions();
        assertTrue(planDefinitions.isEmpty());
        verify(sqLiteDatabase).rawQuery("SELECT json  FROM plan_definition WHERE status =?", new String[]{ACTIVE.value()});
    }


    @Test
    public void testFindAllPlanDefinitionIds() {
        when(sqLiteDatabase.rawQuery(anyString(), argsCaptor.capture()))
                .thenReturn(getIdCursor());
        Set<String> planDefinitions = planDefinitionRepository.findAllPlanDefinitionIds();
        assertNotNull(planDefinitions);
        assertEquals(1, planDefinitions.size());
        String planDefinition = planDefinitions.iterator().next();
        assertEquals("4708ca0a-d0d6-4199-bb1b-8701803c2d02", planDefinition);
        verify(sqLiteDatabase).rawQuery("SELECT _id  FROM plan_definition WHERE status =?", new String[]{ACTIVE.value()});
    }

    @Test
    public void testFindAllPlanDefinitionIdsWithException() {
        doThrow(new SQLiteException()).when(sqLiteDatabase).rawQuery(anyString(), any());
        Set<String> planDefinitions = planDefinitionRepository.findAllPlanDefinitionIds();
        assertTrue(planDefinitions.isEmpty());
        verify(sqLiteDatabase).rawQuery("SELECT _id  FROM plan_definition WHERE status =?", new String[]{ACTIVE.value()});
    }

    @Test
    public void testFindPlanDefinitionByIds() {
        when(sqLiteDatabase.rawQuery(anyString(), any(String[].class)))
                .thenReturn(getMultiplePlanCursor());
        Set<String> expectedIdentifiers = new HashSet<>();
        expectedIdentifiers.add("4708ca0a-d0d6-4199-bb1b-8701803c2d02");
        expectedIdentifiers.add("10f9e9fa-ce34-4b27-a961-72fab5206ab6");

        Set<PlanDefinition> expectedPlanDefinitions = new HashSet<>();
        expectedPlanDefinitions.add(gson.fromJson(planDefinitionJSON, PlanDefinition.class));
        expectedPlanDefinitions.add(gson.fromJson(fiPlanDefinitionJSON, PlanDefinition.class));

        Set<PlanDefinition> planDefinitionsSet = planDefinitionRepository.findPlanDefinitionByIds(expectedIdentifiers);
        assertNotNull(planDefinitionsSet);
        assertFalse(planDefinitionsSet.isEmpty());
        assertEquals(2, planDefinitionsSet.size());
        Iterator<PlanDefinition> iterator = planDefinitionsSet.iterator();
        while (iterator.hasNext()){
            PlanDefinition planDefinition = iterator.next();
            expectedIdentifiers.contains(planDefinition.getIdentifier());
            expectedPlanDefinitions.contains(planDefinition);
        }

        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());
        List<String> planIds = Arrays.asList(argsCaptor.getAllValues().get(0));
        assertTrue(planIds.contains("4708ca0a-d0d6-4199-bb1b-8701803c2d02"));
        assertTrue(planIds.contains("10f9e9fa-ce34-4b27-a961-72fab5206ab6"));
        assertEquals("SELECT json  FROM plan_definition  WHERE _id IN (?,?)", stringArgumentCaptor.getValue());
    }

    @Test
    public void testFindPlanDefinitionByIdsWithExceptionThrown() {
        doThrow(new SQLiteException()).when(sqLiteDatabase).rawQuery(anyString(), any(String[].class));
        Set<String> expectedIdentifiers = new HashSet<>();
        expectedIdentifiers.add("4708ca0a-d0d6-4199-bb1b-8701803c2d02");
        expectedIdentifiers.add("10f9e9fa-ce34-4b27-a961-72fab5206ab6");
        Set<PlanDefinition> planDefinitionsSet = planDefinitionRepository.findPlanDefinitionByIds(expectedIdentifiers);
        assertTrue(planDefinitionsSet.isEmpty());
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());
        List<String> planIds = Arrays.asList(argsCaptor.getAllValues().get(0));
        assertTrue(planIds.contains("4708ca0a-d0d6-4199-bb1b-8701803c2d02"));
        assertTrue(planIds.contains("10f9e9fa-ce34-4b27-a961-72fab5206ab6"));
        assertEquals("SELECT json  FROM plan_definition  WHERE _id IN (?,?)", stringArgumentCaptor.getValue());
    }

    private MatrixCursor getCursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{JSON});
        cursor.addRow(new Object[]{planDefinitionJSON});
        return cursor;
    }

    private MatrixCursor getMultiplePlanCursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{JSON});
        cursor.addRow(new Object[]{planDefinitionJSON});
        cursor.addRow(new Object[]{fiPlanDefinitionJSON});
        return cursor;
    }

    private MatrixCursor getIdCursor() {
        MatrixCursor cursor = new MatrixCursor(new String[]{ID});
        cursor.addRow(new Object[]{"4708ca0a-d0d6-4199-bb1b-8701803c2d02"});
        return cursor;
    }

}
