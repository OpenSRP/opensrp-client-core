package org.smartregister.repository;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.PlanDefinitionSearch;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PlanDefinitionSearchRepositoryTest extends BaseUnitTest {

    private PlanDefinitionSearchRepository planDefinitionSearchRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private Cursor cursor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        planDefinitionSearchRepository = spy(new PlanDefinitionSearchRepository());
    }

    @Test
    public void testFindPlanDefinitionSearchByPlanId() {
        String planId = UUID.randomUUID().toString();
        long start = 1605560400000l;
        doReturn(3).when(cursor).getColumnIndex(PlanDefinitionSearchRepository.START);
        doReturn(start).when(cursor).getLong(3);
        doReturn(sqLiteDatabase).when(planDefinitionSearchRepository).getReadableDatabase();
        doReturn(cursor).when(sqLiteDatabase).rawQuery(anyString(), any(String[].class));
        doAnswer(new Answer() {
            int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                count++;
                return count == 1;
            }
        }).when(cursor).moveToNext();
        List<PlanDefinitionSearch> planDefinitionSearches = planDefinitionSearchRepository.findPlanDefinitionSearchByPlanId(planId);
        verify(sqLiteDatabase).rawQuery(anyString(), eq(new String[]{planId}));
        assertNotNull(planDefinitionSearches);
        assertEquals(1, planDefinitionSearches.size());
        assertEquals(start, planDefinitionSearches.get(0).getStart().toDate().getTime());
    }

    @Test
    public void testFindAllPlanDefinitionSearchByStatus() {
        doReturn(sqLiteDatabase).when(planDefinitionSearchRepository).getReadableDatabase();
        doReturn(cursor).when(sqLiteDatabase).rawQuery(anyString(), any(String[].class));
        doAnswer(new Answer() {
            int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                count++;
                return count == 1;
            }
        }).when(cursor).moveToNext();
        List<PlanDefinitionSearch> planDefinitionSearches = planDefinitionSearchRepository.findPlanDefinitionSearchByPlanStatus(PlanDefinition.PlanStatus.ACTIVE);
        verify(sqLiteDatabase).rawQuery(anyString(), eq(new String[]{PlanDefinition.PlanStatus.ACTIVE.value()}));
        assertNotNull(planDefinitionSearches);
        assertEquals(1, planDefinitionSearches.size());
    }
}