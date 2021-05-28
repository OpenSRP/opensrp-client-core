package org.smartregister.repository;


import net.sqlcipher.Cursor;
import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.domain.Client;
import org.smartregister.sync.ClientData;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 7/14/20.
 */
public class ClientRelationshipRepositoryTest extends BaseRobolectricUnitTest {

    @Mock
    private SQLiteDatabase database;

    @Test
    public void testCreateTableShouldCreateTableAndIndex() {
        ClientRelationshipRepository.createTable(database);
        verify(database).execSQL(ClientRelationshipRepository.CREATE_TABLE);
        verify(database).execSQL(ClientRelationshipRepository.CREATE_BASE_ENTITY_ID_INDEX);
    }

    @Test
    public void findClientByRelationshipShouldReturnClients() throws JSONException {
        ClientRelationshipRepository repository = spy(new ClientRelationshipRepository());
        when(repository.getReadableDatabase()).thenReturn(database);
        when(database.rawQuery(anyString(), any())).thenReturn(getClients());
        List<Client> clients = repository.findClientByRelationship("family", "12323");
        assertEquals(1, clients.size());
        assertEquals("03b1321a-d1fb-4fd0-b1cd-a3f3509fc6a6",clients.get(0).getBaseEntityId());
    }

    private Cursor getClients() throws JSONException {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{EventClientRepository.client_column.json.name()});
        JSONArray jsonObject = new JSONArray(ClientData.clientJsonArray);
        matrixCursor.addRow(new Object[]{jsonObject.getJSONObject(0)});
        return matrixCursor;
    }


}
