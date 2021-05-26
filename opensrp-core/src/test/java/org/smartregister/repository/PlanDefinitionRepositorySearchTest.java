package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.smartregister.domain.PlanDefinitionTest.gson;
import static org.smartregister.domain.PlanDefinitionTest.planDefinitionJSON;
import static org.smartregister.repository.PlanDefinitionSearchRepository.END;
import static org.smartregister.repository.PlanDefinitionSearchRepository.JURISDICTION_ID;
import static org.smartregister.repository.PlanDefinitionSearchRepository.NAME;
import static org.smartregister.repository.PlanDefinitionSearchRepository.PLAN_DEFINITION_SEARCH_TABLE;
import static org.smartregister.repository.PlanDefinitionSearchRepository.PLAN_ID;
import static org.smartregister.repository.PlanDefinitionSearchRepository.START;
import static org.smartregister.repository.PlanDefinitionSearchRepository.STATUS;

/**
 * Created by samuelgithengi on 5/8/19.
 */
public class PlanDefinitionRepositorySearchTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private PlanDefinitionSearchRepository searchRepository;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private PlanDefinitionRepository planDefinitionRepository;

    @Captor
    private ArgumentCaptor<ContentValues> contentValuesArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Before
    public void setUp() {
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        searchRepository = new PlanDefinitionSearchRepository();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void testCreateTable() {
        PlanDefinitionSearchRepository.createTable(sqLiteDatabase);
        verify(sqLiteDatabase, times(2)).execSQL(stringArgumentCaptor.capture());
    }

    @Test
    public void testAddOrUpdate() {
        PlanDefinition planDefinition = gson.fromJson(planDefinitionJSON, PlanDefinition.class);
        String jurisdictionId = UUID.randomUUID().toString();
        searchRepository.addOrUpdate(planDefinition, jurisdictionId);
        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());

        assertEquals(2, stringArgumentCaptor.getAllValues().size());
        assertEquals(PLAN_DEFINITION_SEARCH_TABLE, stringArgumentCaptor.getAllValues().get(0));
        assertNull(stringArgumentCaptor.getAllValues().get(1));
        assertEquals(6, contentValuesArgumentCaptor.getValue().size());
        assertEquals(planDefinition.getIdentifier(), contentValuesArgumentCaptor.getValue().get(PLAN_ID));
        assertEquals(jurisdictionId, contentValuesArgumentCaptor.getValue().get(JURISDICTION_ID));
        assertEquals(planDefinition.getName(), contentValuesArgumentCaptor.getValue().get(NAME));
        assertEquals(planDefinition.getStatus().value(), contentValuesArgumentCaptor.getValue().get(STATUS));
        assertEquals(planDefinition.getEffectivePeriod().getStart().toDate().getTime(), contentValuesArgumentCaptor.getValue().get(START));
        assertEquals(planDefinition.getEffectivePeriod().getEnd().toDate().getTime(), contentValuesArgumentCaptor.getValue().get(END));
    }

    @Test
    public void testFindActivePlansByJurisdiction() {

        String jurisdictionId = UUID.randomUUID().toString();
        String planId = "4708ca0a-d0d6-4199-bb1b-8701803c2d02";
        searchRepository.setPlanDefinitionRepository(planDefinitionRepository);
        when(sqLiteDatabase.rawQuery(anyString(), any(String[].class)))
                .thenReturn(getCursor(jurisdictionId));
        Set<PlanDefinition> expected = Collections.singleton(gson.fromJson(planDefinitionJSON, PlanDefinition.class));
        when(planDefinitionRepository.findPlanDefinitionByIds(Collections.singleton(planId))).thenReturn(expected);
        Set<PlanDefinition> planDefinitions = searchRepository.findActivePlansByJurisdiction(planId);
        assertNotNull(planDefinitions);
        PlanDefinition planDefinition = planDefinitions.iterator().next();
        assertEquals("4708ca0a-d0d6-4199-bb1b-8701803c2d02", planDefinition.getIdentifier());
        assertEquals(planDefinitionJSON, gson.toJson(planDefinition));
        verify(sqLiteDatabase).rawQuery("SELECT plan_id FROM plan_definition_search WHERE jurisdiction_id=? AND status=?  AND end  >=? ",
                new String[]{"4708ca0a-d0d6-4199-bb1b-8701803c2d02", "active", String.valueOf(LocalDate.now().toDate().getTime())});
    }

    @Test
    public void testSetPlanDefinitionRepository() {
        searchRepository.setPlanDefinitionRepository(planDefinitionRepository);
        assertEquals(planDefinitionRepository, searchRepository.getPlanDefinitionRepository());
    }


    @Test
    public void testPlanExists() {
        String jurisdictionId = UUID.randomUUID().toString();
        String planId = "4708ca0a-d0d6-4199-bb1b-8701803c2d02";
        assertFalse(searchRepository.planExists(planId, jurisdictionId));

        searchRepository.setPlanDefinitionRepository(planDefinitionRepository);
        when(sqLiteDatabase.rawQuery(anyString(), any(String[].class)))
                .thenReturn(getPlanExistsCursor(planId));
        assertTrue(searchRepository.planExists(planId, jurisdictionId));

        verify(sqLiteDatabase, times(2)).rawQuery("SELECT plan_id FROM plan_definition_search WHERE plan_id=? AND jurisdiction_id=? AND status=?  AND end  >=? ",
                new String[]{planId, jurisdictionId, "active", String.valueOf(LocalDate.now().toDate().getTime())});
    }


    private MatrixCursor getCursor(String jurisdiction) {
        MatrixCursor cursor = new MatrixCursor(PlanDefinitionSearchRepository.COLUMNS);
        PlanDefinition planDefinition = gson.fromJson(planDefinitionJSON, PlanDefinition.class);
        cursor.addRow(new Object[]{planDefinition.getIdentifier(), jurisdiction,
                planDefinition.getName(), planDefinition.getStatus(),
                planDefinition.getEffectivePeriod().getStart().toDate().getTime(),
                planDefinition.getEffectivePeriod().getEnd().toDate().getTime()
        });
        return cursor;
    }

    private MatrixCursor getPlanExistsCursor(String planId) {
        MatrixCursor cursor = new MatrixCursor(new String[1]);
        cursor.addRow(new Object[]{planId});
        return cursor;
    }

}
