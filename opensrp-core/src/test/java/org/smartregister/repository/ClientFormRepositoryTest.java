package org.smartregister.repository;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
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
import org.smartregister.domain.ClientForm;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.smartregister.repository.ClientFormRepository.ACTIVE;
import static org.smartregister.repository.ClientFormRepository.CLIENT_FORM_TABLE;
import static org.smartregister.repository.ClientFormRepository.CREATED_AT;
import static org.smartregister.repository.ClientFormRepository.IDENTIFIER;
import static org.smartregister.repository.ClientFormRepository.VERSION;

/**
 * Created by ilakozejumanne on 2020-04-09.
 */

public class ClientFormRepositoryTest extends BaseUnitTest {

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private ClientFormRepository clientFormRepository;
    @Mock
    private Repository repository;
    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Captor
    private ArgumentCaptor<ContentValues> contentValuesArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    private ArgumentCaptor<String[]> argsCaptor;
    private String clientFormJson = "{\"id\":\"1\",\"version\":\"1.1.0\",\"identifier\":\"en/child/enrollment.json\",\"module\":\"child\",\"json\":\"{}\",\"jurisdiction\":\"\",\"label\":\"Child Enrollment Form\",\"isNew\":true,\"active\":true,\"createdAt\":\"2020-04-09 00:00:00\"}";

    @Before
    public void setUp() {
        Whitebox.setInternalState(DrishtiApplication.getInstance(), "repository", repository);
        clientFormRepository = new ClientFormRepository();
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getCursor());

    }

    @Test
    public void testAddOrUpdateShouldAdd() {

        ClientForm clientForm = gson.fromJson(clientFormJson, ClientForm.class);
        clientFormRepository.addOrUpdate(clientForm);

        verify(sqLiteDatabase).replace(stringArgumentCaptor.capture(), stringArgumentCaptor.capture(), contentValuesArgumentCaptor.capture());
        assertEquals(2, stringArgumentCaptor.getAllValues().size());

        Iterator<String> iterator = stringArgumentCaptor.getAllValues().iterator();
        assertEquals(CLIENT_FORM_TABLE, iterator.next());
        assertNull(iterator.next());

        ContentValues contentValues = contentValuesArgumentCaptor.getValue();
        assertEquals(10, contentValues.size());

        assertEquals("1", contentValues.getAsString(ClientFormRepository.ID));
        assertEquals("1.1.0", contentValues.getAsString(VERSION));
        assertEquals("en/child/enrollment.json", contentValues.getAsString(IDENTIFIER));
        assertEquals(true, contentValues.getAsBoolean(ACTIVE));
        assertEquals(true, contentValues.getAsBoolean(ClientFormRepository.IS_NEW));


    }

    @Test
    public void testGetActiveClientFormByIdentifier() {
        String identifier = "en/child/enrollment.json";
        clientFormRepository.getActiveClientFormByIdentifier(identifier);
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM " + CLIENT_FORM_TABLE +
                " WHERE " + IDENTIFIER + " =? AND " + ACTIVE + " = 1", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals(identifier, argsCaptor.getValue()[0]);
    }

    @Test
    public void testGetClientFormByIdentifier() {
        String identifier = "en/child/enrollment.json";
        clientFormRepository.getClientFormByIdentifier(identifier);
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());
        assertEquals("SELECT * FROM " + CLIENT_FORM_TABLE +
                " WHERE " + IDENTIFIER + " =? ORDER BY " + CREATED_AT + " DESC", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals(identifier, argsCaptor.getValue()[0]);

    }

    @Test
    public void testGetLatestFormByIdentifier() {
        String identifier = "en/child/enrollment.json";
        clientFormRepository.getLatestFormByIdentifier(identifier);
        verify(sqLiteDatabase).rawQuery(stringArgumentCaptor.capture(), argsCaptor.capture());

        assertEquals("SELECT * FROM " + CLIENT_FORM_TABLE +
                " WHERE " + IDENTIFIER + " = ? ORDER BY " + CREATED_AT + " DESC LIMIT 1", stringArgumentCaptor.getValue());
        assertEquals(1, argsCaptor.getValue().length);
        assertEquals(identifier, argsCaptor.getValue()[0]);

    }

    @Test
    public void testSetIsNewShouldCallDatabaseUpdateWithCorrectValues() {
        ArgumentCaptor<ContentValues> argumentCaptor = ArgumentCaptor.forClass(ContentValues.class);
        ArgumentCaptor<String[]> stringArgumentCaptor = ArgumentCaptor.forClass(String[].class);
        clientFormRepository.setIsNew(false, 56);

        verify(sqLiteDatabase).update(eq("client_form"), argumentCaptor.capture(), eq("id = ?"), stringArgumentCaptor.capture());

        ContentValues contentValues = argumentCaptor.getValue();
        assertEquals(false, contentValues.getAsBoolean(ClientFormRepository.IS_NEW));
        assertEquals(56, (int) contentValues.getAsInteger(ClientFormRepository.ID));

        String[] whereArgs = stringArgumentCaptor.getValue();
        assertEquals("56", whereArgs[0]);
    }

    @Test
    public void testReadCursorShouldReturnValidObject() {
        int formId = 78;
        String version = "0.0.4";
        String identifier = "child_registration.json";
        String module = null;
        String json = "{}";
        String jurisdiction = null;
        String label = "Child Registration";
        boolean isNew = true;
        boolean isActive = true;

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{ClientFormRepository.ID, VERSION
                , IDENTIFIER, ClientFormRepository.MODULE, ClientFormRepository.JSON
                , ClientFormRepository.JURISDICTION, ClientFormRepository.LABEL
                , ClientFormRepository.IS_NEW, ACTIVE, CREATED_AT});
        matrixCursor.addRow(new Object[]{formId, version, identifier, module, json, jurisdiction, label, isNew ? 1 : 0, isActive ? 1 : 0, ""});
        matrixCursor.moveToFirst();
        ClientForm clientForm = clientFormRepository.readCursor(matrixCursor);

        assertEquals(formId, clientForm.getId());
        assertEquals(version, clientForm.getVersion());
        assertEquals(identifier, clientForm.getIdentifier());
        assertEquals(module, clientForm.getModule());
        assertEquals(json, clientForm.getJson());
        assertEquals(jurisdiction, clientForm.getJurisdiction());
        assertEquals(label, clientForm.getLabel());
        assertEquals(isActive, clientForm.isActive());
        assertEquals(isNew, clientForm.isNew());
    }

    public MatrixCursor getCursor() {
        MatrixCursor cursor = new MatrixCursor(ClientFormRepository.COLUMNS);
        ClientForm clientForm = gson.fromJson(clientFormJson, ClientForm.class);
        cursor.addRow(new Object[]{clientForm.getId(), clientForm.getVersion(), clientForm.getIdentifier(), clientForm.getModule(), clientForm.getJson(), clientForm.getJurisdiction(), clientForm.getLabel(), clientForm.isNew() ? 1 : 0, clientForm.isActive() ? 1 : 0, clientForm.getCreatedAt()});
        return cursor;
    }

}
