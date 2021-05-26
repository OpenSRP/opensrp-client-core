package org.smartregister.repository;

import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.ServiceProvided;

import java.util.HashMap;

/**
 * Created by kaderchowdhury on 21/11/17.
 */

public class ServiceProvidedRepositoryTest extends BaseUnitTest {

    private ServiceProvidedRepository serviceProvidedRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Mock
    private Repository repository;
    @Mock
    private ServiceProvided serviceProvided;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        serviceProvidedRepository = new ServiceProvidedRepository();
        serviceProvidedRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(serviceProvidedRepository);
    }

    @Test
    public void ssertOnCreateCallsDatabaseExec() {
        serviceProvidedRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertadCallsDatabaseInsert() {
        ServiceProvided serviceProvided = new ServiceProvided("", "", "", new HashMap<String, String>());
        serviceProvidedRepository.add(serviceProvided);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    public static final String ENTITY_ID_COLUMN = "entityId";
    public static final String NAME_ID_COLUMN = "name";
    public static final String DATE_ID_COLUMN = "date";
    public static final String DATA_ID_COLUMN = "data";

    @Test
    public void assertAllfindByEntityIdAndServiceNames() {
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(getCursor());
        Assert.assertNotNull(serviceProvidedRepository.findByEntityIdAndServiceNames(ENTITY_ID_COLUMN, "a", "b"));
    }

    @Test
    public void assertAllReturnsList() {
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.anyString())).thenReturn(getCursor());
        Assert.assertNotNull(serviceProvidedRepository.all());
    }

    public MatrixCursor getCursor() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{ENTITY_ID_COLUMN,
                NAME_ID_COLUMN, DATE_ID_COLUMN, DATA_ID_COLUMN});
        matrixCursor.addRow(new String[]{"1", "2", "3", "{\"json\":\"data\"}"});
        return matrixCursor;
    }

}
