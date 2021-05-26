package org.smartregister.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.client.utils.contract.ClientFormContract;
import org.smartregister.domain.ClientForm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by cozej4 on 2020-04-06.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class ClientFormRepository extends BaseRepository implements ClientFormContract.Dao {

    protected static final String ID = "id";
    protected static final String VERSION = "version";
    protected static final String IDENTIFIER = "identifier";
    protected static final String MODULE = "module";
    protected static final String JSON = "json";
    protected static final String JURISDICTION = "jurisdiction";
    protected static final String LABEL = "label";
    protected static final String IS_NEW = "is_new";
    protected static final String ACTIVE = "active";
    protected static final String CREATED_AT = "created_at";
    protected static final String CLIENT_FORM_TABLE = "client_form";
    public static final String CREATE_CLIENT_FORM_TABLE =
            "CREATE TABLE " + CLIENT_FORM_TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY," +
                    VERSION + " VARCHAR , " +
                    IDENTIFIER + " VARCHAR , " +
                    MODULE + " VARCHAR , " +
                    JSON + " VARCHAR , " +
                    JURISDICTION + " VARCHAR , " +
                    LABEL + " VARCHAR , " +
                    IS_NEW + " INTEGER , " +
                    ACTIVE + " INTEGER  NOT NULL, " +
                    CREATED_AT + " VARCHAR NOT NULL ) ";
    protected static final String[] COLUMNS = new String[]{ID, VERSION, IDENTIFIER, MODULE, JSON, JURISDICTION, LABEL, IS_NEW, ACTIVE, CREATED_AT};
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private static final String CREATE_CLIENT_FORM_IDENTIFIER_INDEX = "CREATE INDEX "
            + CLIENT_FORM_TABLE + "_" + IDENTIFIER + "_ind ON " + CLIENT_FORM_TABLE + "(" + IDENTIFIER + ")";
    private static final String CREATE_CLIENT_FORM_IS_ACTIVE_INDEX = "CREATE INDEX "
            + CLIENT_FORM_TABLE + "_" + ACTIVE + "_ind ON " + CLIENT_FORM_TABLE + "(" + ACTIVE + ")";

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_CLIENT_FORM_TABLE);
        database.execSQL(CREATE_CLIENT_FORM_IDENTIFIER_INDEX);
        database.execSQL(CREATE_CLIENT_FORM_IS_ACTIVE_INDEX);
    }

    protected String getClientFormTableName() {
        return CLIENT_FORM_TABLE;
    }

    @Override
    public void addOrUpdate(ClientFormContract.Model clientForm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, clientForm.getId());
        contentValues.put(VERSION, clientForm.getVersion());
        contentValues.put(IDENTIFIER, clientForm.getIdentifier());
        contentValues.put(MODULE, clientForm.getModule());
        contentValues.put(JSON, clientForm.getJson());
        contentValues.put(JURISDICTION, clientForm.getJurisdiction());
        contentValues.put(LABEL, clientForm.getLabel());
        contentValues.put(IS_NEW, clientForm.isNew());
        contentValues.put(ACTIVE, clientForm.isActive());
        contentValues.put(CREATED_AT, DATE_FORMAT.format(clientForm.getCreatedAt()));
        getWritableDatabase().replace(getClientFormTableName(), null, contentValues);
    }

    @Override
    public void setIsNew(boolean isNew, int formId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, formId);
        contentValues.put(IS_NEW, isNew);
        getWritableDatabase().update(getClientFormTableName(),contentValues, ID + " = ?", new String[]{String.valueOf(formId)});
    }

    protected ClientForm readCursor(Cursor cursor) {
        ClientForm clientForm = new ClientForm();
        clientForm.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        clientForm.setVersion(cursor.getString(cursor.getColumnIndex(VERSION)));
        clientForm.setIdentifier(cursor.getString(cursor.getColumnIndex(IDENTIFIER)));
        clientForm.setModule(cursor.getString(cursor.getColumnIndex(MODULE)));
        clientForm.setJson(cursor.getString(cursor.getColumnIndex(JSON)));
        clientForm.setJurisdiction(cursor.getString(cursor.getColumnIndex(JURISDICTION)));
        clientForm.setLabel(cursor.getString(cursor.getColumnIndex(LABEL)));
        clientForm.setNew(cursor.getInt(cursor.getColumnIndex(IS_NEW)) == 1);
        clientForm.setActive(cursor.getInt(cursor.getColumnIndex(ACTIVE)) == 1);
        try {
            clientForm.setCreatedAt(DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(CREATED_AT))));
        } catch (ParseException e) {
            Timber.e(e);
        }

        return clientForm;
    }

    /**
     * Get a list of ClientForms for the passed identifier
     *
     * @param identifier of a the client form
     * @return a list of ClientForms for the passed identifier
     */
    @Override
    public List<ClientFormContract.Model> getClientFormByIdentifier(String identifier) {
        List<ClientFormContract.Model> clientForms = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getClientFormTableName() +
                " WHERE " + IDENTIFIER + " =? ORDER BY " + CREATED_AT + " DESC", new String[]{identifier})) {
            while (cursor.moveToNext()) {
                clientForms.add(readCursor(cursor));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return clientForms;

    }

    /**
     * Get the active ClientForms for the passed identifier
     *
     * @param identifier of a the client form
     * @return active ClientForm for the passed identifier
     */
    public ClientForm getActiveClientFormByIdentifier(String identifier) {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getClientFormTableName() +
                " WHERE " + IDENTIFIER + " =? AND " + ACTIVE + " = 1", new String[]{identifier})) {
            if (cursor.moveToFirst()) {
                return readCursor(cursor);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;

    }

    /**
     * Get the latest ClientForms for the passed identifier. The latest ClientForm might not be
     * active if unstable
     *
     * @param identifier of a the client form
     * @return active ClientForm for the passed identifier
     */
    @Override
    public ClientForm getLatestFormByIdentifier(String identifier) {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + getClientFormTableName() +
                " WHERE " + IDENTIFIER + " = ? ORDER BY " + CREATED_AT + " DESC LIMIT 1", new String[]{identifier})) {
            if (cursor.moveToFirst()) {
                return readCursor(cursor);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;

    }


    /**
     * Delete client form by id
     *
     * @param clientFormId
     */
    @Override
    public void delete(int clientFormId) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(CLIENT_FORM_TABLE, ID + "= ?", new String[]{String.valueOf(clientFormId)});
    }

    @Override
    public ClientFormContract.Model createNewClientFormModel() {
        return new ClientForm();
    }

}
