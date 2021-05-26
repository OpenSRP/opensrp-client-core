package org.smartregister.repository.dao;

import com.ibm.fhir.model.resource.Patient;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.repository.ClientRelationshipRepository;
import org.smartregister.repository.Repository;
import org.smartregister.sync.ClientData;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 9/3/20.
 */

public class ClientDaoImplTest extends BaseRobolectricUnitTest {

    private ClientDaoImpl clientDao;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        clientDao = new ClientDaoImpl();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
    }

    @After
    public void tearDown() {
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", (Repository) null);
    }

    @Test
    public void testFindClientById() throws Exception {
        String query = "SELECT json FROM client WHERE baseEntityId = ? ";
        String[] params = new String[]{"41587456-b7c8-4c4e-b433-23a786f742fc"};
        when(sqLiteDatabase.rawQuery(anyString(), any())).thenReturn(getCursor(1));
        List<Patient> patients = clientDao.findClientById(params[0]);
        verify(sqLiteDatabase).rawQuery(query, params);

        assertEquals(1, patients.size());
        Patient patient = patients.iterator().next();
        assertNotNull(patient);
        assertEquals("03b1321a-d1fb-4fd0-b1cd-a3f3509fc6a6", patient.getId());
    }

    @Test
    public void testFindFamilyByJurisdiction() throws Exception {
        String query = "select json from client where locationId =? and clientType =?";
        String[] params = new String[]{"41587456-b7c8-4c4e-b433-23a786f742fc", "Family"};
        when(sqLiteDatabase.rawQuery(anyString(), any())).thenReturn(getCursor());
        List<Patient> patients = clientDao.findFamilyByJurisdiction(params[0]);
        verify(sqLiteDatabase).rawQuery(query, params);

        assertEquals(16, patients.size());
        Patient patient = patients.iterator().next();
        assertNotNull(patient);
        assertEquals("03b1321a-d1fb-4fd0-b1cd-a3f3509fc6a6", patient.getId());
    }

    @Test
    public void testFindFamilyByResidence() throws Exception {
        String query = "select json from client where residence =? and clientType =?";
        String[] params = new String[]{"41587456-b7c8-4c4e-b433-23a786f742fc", "Family"};
        when(sqLiteDatabase.rawQuery(anyString(), any())).thenReturn(getCursor());
        List<Patient> patients = clientDao.findFamilyByResidence(params[0]);
        verify(sqLiteDatabase).rawQuery(query, params);

        assertEquals(16, patients.size());
        Patient patient = patients.iterator().next();
        assertNotNull(patient);
        assertEquals("03b1321a-d1fb-4fd0-b1cd-a3f3509fc6a6", patient.getId());
    }

    @Test
    public void testFindFamilyMemberByJurisdiction() throws Exception {
        String query = "select json from client where locationId =? and (clientType is null or clientType !=? )";
        String[] params = new String[]{"41587456-b7c8-4c4e-b433-23a786f742fc", "Family"};
        when(sqLiteDatabase.rawQuery(anyString(), any())).thenReturn(getCursor(10));
        List<Patient> patients = clientDao.findFamilyMemberyByJurisdiction(params[0]);
        verify(sqLiteDatabase).rawQuery(query, params);

        assertEquals(10, patients.size());
        Patient patient = patients.iterator().next();
        assertNotNull(patient);
        assertEquals("03b1321a-d1fb-4fd0-b1cd-a3f3509fc6a6", patient.getId());
    }

    @Test
    public void testFindFamilyMemberByResidence() throws Exception {
        String query = "select json from client where residence =? and (clientType is null or clientType !=? )";
        String[] params = new String[]{"41587456-b7c8-4c4e-b433-23a786f742fc", "Family"};
        when(sqLiteDatabase.rawQuery(anyString(), any())).thenReturn(getCursor(2));
        List<Patient> patients = clientDao.findFamilyMemberByResidence(params[0]);
        verify(sqLiteDatabase).rawQuery(query, params);

        assertEquals(2, patients.size());
        Patient patient = patients.iterator().next();
        assertNotNull(patient);
        assertEquals("03b1321a-d1fb-4fd0-b1cd-a3f3509fc6a6", patient.getId());
    }

    @Test
    public void testFindClientByRelationship() throws Exception {
        ClientRelationshipRepository clientRelationshipRepository = new ClientRelationshipRepository();
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "clientRelationshipRepository", clientRelationshipRepository);

        String query = "SELECT json FROM client_relationship JOIN  client  ON base_entity_id=baseEntityId WHERE relationship=? AND relational_id =?";
        String[] params = new String[]{"41587456-b7c8-4c4e-b433-23a786f742fc", "Family"};
        when(sqLiteDatabase.rawQuery(anyString(), any())).thenReturn(getCursor(2));

        List<Patient> patients = clientDao.findClientByRelationship(params[0], params[1]);
        verify(sqLiteDatabase).rawQuery(query, params);

        assertEquals(2, patients.size());
        Patient patient = patients.iterator().next();
        assertNotNull(patient);
        assertEquals("03b1321a-d1fb-4fd0-b1cd-a3f3509fc6a6", patient.getId());
    }

    public static MatrixCursor getCursor() throws Exception {
        return getCursor(Integer.MAX_VALUE);
    }

    public static MatrixCursor getCursor(int maxValue) throws Exception {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"json"});
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        for (int i = 0; i < clientArray.length() && i < maxValue; i++) {
            matrixCursor.addRow(new String[]{clientArray.getJSONObject(i).toString()});
        }
        return matrixCursor;
    }
}
