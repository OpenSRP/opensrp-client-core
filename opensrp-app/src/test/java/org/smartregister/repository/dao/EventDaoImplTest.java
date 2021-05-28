package org.smartregister.repository.dao;

import com.ibm.fhir.model.resource.QuestionnaireResponse;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.repository.EventClientRepositoryTest;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 9/3/20.
 */

public class EventDaoImplTest extends BaseUnitTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private EventDaoImpl eventDao;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;


    @Before
    public void setUp() {
        eventDao = new EventDaoImpl();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
    }


    @Test
    public void testFindEventsByEntityIdAndPlan() throws Exception {
        String query = "select json from event where baseEntityId =? and (planId is null or planId =? )";
        String[] params = new String[]{"location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", "IRS_2018_S1"};
        when(sqLiteDatabase.rawQuery(query, params)).thenReturn(EventClientRepositoryTest.getEventCursor());
        List<QuestionnaireResponse> questionnaireResponses = eventDao.findEventsByEntityIdAndPlan("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", "IRS_2018_S1");
        verify(sqLiteDatabase).rawQuery(query, params);

        assertEquals(20, questionnaireResponses.size());
        QuestionnaireResponse questionnaireResponse = questionnaireResponses.iterator().next();

        assertEquals("Household_Registration", questionnaireResponse.getQuestionnaire().getValue());
        assertEquals("2184aaaa-d1cf-4099-945a-c66bd8a93e1e", questionnaireResponse.getId());
        assertEquals(11, questionnaireResponse.getItem().size());


    }
}
