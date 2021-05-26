package org.smartregister.repository;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.smartregister.domain.Client;
import org.smartregister.domain.ClientRelationship;
import org.smartregister.repository.EventClientRepository.client_column;
import org.smartregister.util.JsonFormUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.repository.EventClientRepository.Table.client;

/**
 * Created by samuelgithengi on 7/13/20.
 */
public class ClientRelationshipRepository extends BaseRepository {

    private static final String CLIENT_RELATIONSHIP_TABLE_NAME = "client_relationship";

    private static final String RELATIONSHIP = "relationship";

    private static final String BASE_ENTITY_ID = "base_entity_id";

    private static final String RELATIONAL_ID = "relational_id";


    protected static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + CLIENT_RELATIONSHIP_TABLE_NAME + " (" +
                    BASE_ENTITY_ID + " VARCHAR NOT NULL, " +
                    RELATIONSHIP + " VARCHAR NOT NULL, " +
                    RELATIONAL_ID + " VARCHAR NOT NULL," +
                    "PRIMARY KEY (" + BASE_ENTITY_ID + "," + RELATIONSHIP + "))";


    protected static final String CREATE_BASE_ENTITY_ID_INDEX = "CREATE INDEX IF NOT EXISTS "
            + CLIENT_RELATIONSHIP_TABLE_NAME + "_base_entity_ind  ON " + CLIENT_RELATIONSHIP_TABLE_NAME + "(" + BASE_ENTITY_ID + ")";


    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
        database.execSQL(CREATE_BASE_ENTITY_ID_INDEX);
    }

    public void saveRelationship(ClientRelationship... clientRelationships) {

        String query = String.format("REPLACE INTO %s VALUES(?,?,?)", CLIENT_RELATIONSHIP_TABLE_NAME);
        SQLiteStatement statement = getWritableDatabase().compileStatement(query);
        for (ClientRelationship clientRelationship : clientRelationships) {
            statement.clearBindings();
            statement.bindString(1, clientRelationship.getBaseEntityId());
            statement.bindString(2, clientRelationship.getRelationship());
            statement.bindString(3, clientRelationship.getRelationalId());
            statement.executeInsert();
        }
        statement.close();
    }

    public List<Client> findClientByRelationship(String relationShip, String relationalId) {
        List<Client> clientList = new ArrayList<>();
        String query = String.format("SELECT %s FROM %s JOIN  %s  ON %s=%s WHERE %s=? AND %s =?",
                client_column.json.name(), CLIENT_RELATIONSHIP_TABLE_NAME, client.name(), BASE_ENTITY_ID, client_column.baseEntityId.name(), RELATIONSHIP, RELATIONAL_ID);
        try (Cursor cursor = getReadableDatabase().rawQuery(query, new String[]{relationShip, relationalId})) {
            while (cursor.moveToNext()) {
                clientList.add(JsonFormUtils.gson.fromJson(cursor.getString(0), Client.class));
            }
        } catch (SQLException e) {
            Timber.e(e);
        }
        return clientList;
    }
}
