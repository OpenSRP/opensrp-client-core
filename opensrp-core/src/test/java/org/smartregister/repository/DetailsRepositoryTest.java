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
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Map;

/**
 * Created by kaderchowdhury on 19/11/17.
 */

public class DetailsRepositoryTest extends BaseUnitTest {

    private DetailsRepository detailsRepository;
    @Mock
    private Repository repository;
    @Mock
    private SQLiteDatabase sqLiteDatabase;
    private static final String BASE_ENTITY_ID_COLUMN = "base_entity_id";
    private static final String KEY_COLUMN = "key";
    private static final String VALUE_COLUMN = "value";
    private static final String EVENT_DATE_COLUMN = "event_date";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        detailsRepository = new DetailsRepository();
        detailsRepository.updateMasterRepository(repository);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(getCursor());
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(detailsRepository);
    }

    @Test
    public void assertOnCreateCallsDatabaseExec() {
        detailsRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertAddCallsRawQueryAndInsertUpdate() {
        detailsRepository.add("1", "key", "value", new Long(0));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).rawQuery(Mockito.anyString(), Mockito.any(String[].class));

        detailsRepository.add("1", "key", "xyz", new Long(0));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));

        Mockito.when(sqLiteDatabase.rawQuery(Mockito.anyString(), Mockito.any(String[].class))).thenReturn(null);
        detailsRepository.add("1", "key", "xyz", new Long(0));
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertgetAllDetailsForClient() {
        Map<String, String> detail = detailsRepository.getAllDetailsForClient("1");
        Assert.assertNotNull(detail);
        Assert.assertEquals(detail.get("key"), "value");
    }

    @Test
    public void assertupdateDetails() {
        Assert.assertNotNull(detailsRepository.updateDetails(Mockito.mock(CommonPersonObject.class)));
    }

    @Test
    public void assertupdateDetails2() {
        Assert.assertNotNull(detailsRepository.updateDetails(Mockito.mock(CommonPersonObjectClient.class)));
    }

    @Test
    public void assertdeleteDetails() {
        Assert.assertEquals(detailsRepository.deleteDetails("1"), false);
        Mockito.when(sqLiteDatabase.delete(Mockito.anyString(), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(1);
        Assert.assertEquals(detailsRepository.deleteDetails("1"), true);
    }

    public MatrixCursor getCursor() {
        String[] columns = {BASE_ENTITY_ID_COLUMN, KEY_COLUMN, VALUE_COLUMN, EVENT_DATE_COLUMN};
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{"1", "key", "value", "2017-10-10"});
        return cursor;
    }
}
