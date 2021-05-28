package org.smartregister.util;

import androidx.core.util.Pair;

import com.google.gson.Gson;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.json.JSONArray;
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
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.ClientProcessorForJava;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 1/6/20.
 */
public class RecreateECUtilTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private SQLiteDatabase database;

    @Mock
    private ClientProcessorForJava clientProcessor;

    @Mock
    private EventClientRepository eventClientRepository;

    @Captor
    private ArgumentCaptor<JSONArray> jsonArrayArgumentCaptor;

    private RecreateECUtil recreateECUtil;
    private FormTag formTag;

    private String[] params = {"a", "b"};
    private String query = "select * from events";
    private String tableName = "ec_family_member";

    @Before
    public void setUp() {
        formTag = new FormTag();
        formTag.locationId = UUID.randomUUID().toString();
        recreateECUtil = new RecreateECUtil();
        Whitebox.setInternalState(recreateECUtil, "clientProcessor", clientProcessor);
    }


    @Test
    public void testCreateEventAndClients() throws IOException {
        when(database.rawQuery(query, params)).thenReturn(getECCursor());
        when(clientProcessor.getColumnMappings(tableName)).thenReturn(getEcConfig());
        Pair<List<Event>, List<Client>> eventsAndClients = recreateECUtil.createEventAndClients(database, tableName, query, params, "FamilyRegistration", "Family", formTag);
        assertNotNull(eventsAndClients);
        assertEquals(3, eventsAndClients.first.size());
        assertEquals(3, eventsAndClients.second.size());

        Event event1 = eventsAndClients.first.get(0);
        Client client1 = eventsAndClients.second.get(0);

        assertEquals("person12", client1.getBaseEntityId());
        assertEquals("12", client1.getIdentifiers().get("opensrp_id"));
        assertEquals("family10", client1.getRelationships().get("family").get(0));
        assertEquals("Jane", client1.getFirstName());
        assertEquals("Doe", client1.getLastName());
        assertEquals("23", client1.getAttribute("residence"));
        assertEquals(0, new DateTime("1993-10-21T07:00:00.000+07:00").toDate().compareTo(client1.getBirthdate()));

        assertEquals("person12", event1.getBaseEntityId());
        assertEquals("Screening", event1.getEventType());
        assertEquals("FamilyMember", event1.getEntityType());
        assertEquals(2, event1.getObs().size());
        assertEquals("163084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", event1.getObs().get(0).getFieldCode());
        assertEquals("1230", event1.getObs().get(0).getValue());
        assertEquals("sleeps_outdoors", event1.getObs().get(1).getFieldCode());
        assertEquals("no", event1.getObs().get(1).getValue());
        assertEquals("12383", event1.getDetails().get("task_identifier"));
        assertEquals(formTag.locationId, event1.getLocationId());
    }


    @Test
    public void testCreateEventAndClientsForMissingTable() {
        when(clientProcessor.getColumnMappings(tableName)).thenReturn(null);
        Pair<List<Event>, List<Client>> eventsAndClients = recreateECUtil.createEventAndClients(database, tableName, query, params, "FamilyRegistration", "Family", formTag);
        assertNull(eventsAndClients);
        verifyZeroInteractions(database);
        verify(clientProcessor).getColumnMappings(tableName);
    }


    @Test
    public void testSaveEventAndClientsWithNullEC() {
        recreateECUtil.saveEventAndClients(null, database);
        verifyZeroInteractions(database);
    }

    @Test
    public void testSaveEventAndClients() throws IOException {
        when(database.rawQuery(query, params)).thenReturn(getECCursor());
        when(clientProcessor.getColumnMappings(tableName)).thenReturn(getEcConfig());
        Whitebox.setInternalState(recreateECUtil, "eventClientRepository", eventClientRepository);
        Pair<List<Event>, List<Client>> eventsAndClients = recreateECUtil.createEventAndClients(database, tableName, query, params, "FamilyRegistration", "Family", formTag);
        eventsAndClients.first.get(0).addDetails("opearational_area1", "123");
        eventsAndClients.first.get(0).addDetails("task_business_status", "Completed");
        recreateECUtil.saveEventAndClients(eventsAndClients, database);
        verify(eventClientRepository).batchInsertClients(jsonArrayArgumentCaptor.capture(), eq(database));
        verify(eventClientRepository).batchInsertEvents(jsonArrayArgumentCaptor.capture(), eq(0l), eq(database));
        Gson gson = JsonFormUtils.gson;
        assertEquals(gson.toJson(eventsAndClients.second), jsonArrayArgumentCaptor.getAllValues().get(0).toString());
        assertEquals(gson.toJson(eventsAndClients.first), jsonArrayArgumentCaptor.getAllValues().get(1).toString());

    }

    private MatrixCursor getECCursor() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"base_entity_id", "unique_id", "national_id", "relational_id", "first_name", "last_name", "structure_id", "dob", "sleeps_outdoors", "entity_type", "registration_date", "last_service", "task_identifier"});
        matrixCursor.addRow(new Object[]{"person12", "12", "1230", "family10", "Jane", "Doe", "23", "1993-10-21T07:00:00.000+07:00", "no", "FamilyMember", "2019-12-04T12:00:00.000-05:00", "Screening", "12383"});
        matrixCursor.addRow(new Object[]{"person11", "11", "1231", "family10", "John", "Doe", "23", "1998-09-16T07:00:00.000+07:00", "yes", "FamilyMember", "2019-11-13T12:00:00.000-05:00", "MDA Adherence", "122232"});
        matrixCursor.addRow(new Object[]{"family10", "10", "John", "", "", "Family", "23", "1970-01-01T00:00:00.000+07:00", "no", "Family", "2019-11-13T12:00:00.000-05:00", "Registration", "123"});
        return matrixCursor;

    }


    private Table getEcConfig() throws IOException {
        String path = "src/test/assets/" + "ec_client_fields.json";
        String config = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        return new Gson().fromJson(config, Table.class);
    }
}
