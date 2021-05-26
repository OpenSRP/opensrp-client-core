package org.smartregister.dao;

import android.database.Cursor;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Alert;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@PrepareForTest(DrishtiApplication.class)
public class AbstractDaoTest extends BaseUnitTest {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private AbstractDao.DataMap<String> dataMap;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.doReturn(sqLiteDatabase).when(repository).getReadableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
    }

    @Test
    public void testGetDateFormat() {
        // validate the date date format returned is of type 'yyyy-MM-dd'
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        Assert.assertEquals(sdf.format(now), AbstractDao.getDobDateFormat().format(now));
    }

    @Test
    public void testGetNativeFormsDateFormat() {
        // validate the date date format returned is of type 'yyyy-MM-dd'
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date now = new Date();
        Assert.assertEquals(sdf.format(now), AbstractDao.getNativeFormsDateFormat().format(now));
    }

    @Test
    public void testObjectsConstructedEqualsCursorSize() {
        MatrixCursor cursor = new MatrixCursor(new String[]{"count"});
        cursor.addRow(new Object[]{"1"});
        Mockito.doReturn(cursor).when(sqLiteDatabase).rawQuery(Mockito.anyString(), Mockito.any(String[].class));

        SampleAbstractDaoImp.setRepository(repository);
        int count = SampleAbstractDaoImp.getCountOfEvents();
        Assert.assertEquals(count, 1);
    }

    @Test
    public void testDataTypeConversion() {
        MatrixCursor cursor = new MatrixCursor(new String[]{"int", "date", "long", "string", "sample_long_date", "sample_string_value"});
        cursor.addRow(new Object[]{"1", "2019-01-01", "123123123", "Greatest", "0", null});
        cursor.addRow(new Object[]{"1", "2019-01-01", "123123123", "Greatest", "0", null});
        Mockito.doReturn(cursor).when(sqLiteDatabase).rawQuery(Mockito.anyString(), Mockito.any(String[].class));

        SampleAbstractDaoImp.setRepository(repository);
        List<SampleAbstractDaoImp.SampleObject> res = SampleAbstractDaoImp.getSamples();

        // verify 2 objects where constructed
        Assert.assertEquals(res.size(), 2);

        SampleAbstractDaoImp.SampleObject obj = res.get(0);

        Assert.assertEquals(1, (int) obj.getSampleInt());
        Assert.assertEquals(123123123L, obj.getSampleLong());
        Assert.assertEquals("2019-01-01", AbstractDao.getDobDateFormat().format(obj.getSampleDate()));
        Assert.assertEquals("1970-01-01", AbstractDao.getDobDateFormat().format(obj.getSampleLongDate()));
        Assert.assertEquals("Greatest", obj.getSampleString());
        Assert.assertEquals("default", obj.getSampleStringWithDefault());
    }

    @Test
    public void testErrorInSerializationReturnsNull() {
        MatrixCursor cursor = new MatrixCursor(new String[]{"count"});
        cursor.addRow(new Object[]{"1"});
        Mockito.doReturn(cursor).when(sqLiteDatabase).rawQuery(Mockito.anyString(), Mockito.any(String[].class));

        SampleAbstractDaoImp.setRepository(repository);
        List<Alert> alerts = SampleAbstractDaoImp.getAllAlerts();
        Assert.assertNull(alerts);
    }

    @Test
    public void testUpdateDB() {
        String sql = "update table set col1 = 'value' where id = x";
        AbstractDao.setRepository(repository);
        AbstractDao.updateDB(sql);
        Mockito.verify(sqLiteDatabase).rawExecSQL(sql);
    }

    @Test
    public void testUpdateDBWithException() {
        String sql = "update table set col1 = 'value' where id = x";
        AbstractDao.setRepository(repository);
        Mockito.doThrow(new NullPointerException()).when(sqLiteDatabase).rawExecSQL(Mockito.anyString());
        AbstractDao.updateDB(sql);
        Mockito.verify(sqLiteDatabase).rawExecSQL(sql);
    }

    @Test
    public void testReadValue() {
        MatrixCursor cursor = new MatrixCursor(new String[]{"name", "age"});
        cursor.addRow(new Object[]{"Tony", 25});
        cursor.addRow(new Object[]{"Jessica", 22});
        Mockito.doReturn(cursor).when(sqLiteDatabase).rawQuery(Mockito.anyString(), Mockito.any(String[].class));

        AbstractDao.setRepository(repository);
        List<Map<String, Object>> vals = AbstractDao.readData("select * from people", new String[]{"1234"});
        Assert.assertEquals(vals.size(), 2);
        Assert.assertEquals(vals.get(0).get("name"), "Tony");
        Assert.assertEquals(vals.get(0).get("age"), 25);
        Assert.assertEquals(vals.get(1).get("name"), "Jessica");
        Assert.assertEquals(vals.get(1).get("age"), 22);
    }

    @Test
    public void testReadInvalidSingleValue() throws Exception {
        List result = Whitebox.invokeMethod(AbstractDao.class, "readSingleValue", "test_sql", dataMap);
        Assert.assertNull(result);
    }

    @Test
    public void testReadInvalidSingleValueWithDB() throws Exception {
        SQLiteDatabase db = Mockito.mock(SQLiteDatabase.class);
        String result = Whitebox.invokeMethod(AbstractDao.class, "readSingleValue", "test_sql", dataMap, db, "0");
        Assert.assertEquals("0", result);
    }

    @Test
    public void testGetCursorValueWithNullValue() throws Exception {
        MatrixCursor cursor = Mockito.mock(MatrixCursor.class);
        Mockito.doReturn(-1).when(cursor).getColumnIndex(Mockito.anyString());

        String result = Whitebox.invokeMethod(AbstractDao.class, "getCursorValue", cursor, "column_name", "0");
        Assert.assertEquals("0", result);
    }

    @Test
    public void testGetCursorLongValueWithNullValue() throws Exception {
        MatrixCursor cursor = Mockito.mock(MatrixCursor.class);
        Mockito.doReturn(-1).when(cursor).getColumnIndex(Mockito.anyString());

        Long result = Whitebox.invokeMethod(AbstractDao.class, "getCursorLongValue", cursor, "column_name");
        Assert.assertNull(result);
    }

    @Test
    public void testGetCursorIntValueWithNullValue() throws Exception {
        MatrixCursor cursor = Mockito.mock(MatrixCursor.class);
        Mockito.doReturn(-1).when(cursor).getColumnIndex(Mockito.anyString());

        int result = Whitebox.invokeMethod(AbstractDao.class, "getCursorIntValue", cursor, "column_name", 0);
        Assert.assertEquals(0, result);
    }

    @Test
    public void testGetCursorDateValueWithBlankColumn() throws Exception {
        MatrixCursor cursor = Mockito.mock(MatrixCursor.class);
        Mockito.doReturn(-1).when(cursor).getColumnIndex(Mockito.anyString());

        Date result = Whitebox.invokeMethod(AbstractDao.class, "getCursorValueAsDate", cursor, "");
        Assert.assertNull(result);
    }

    @Test
    public void testGetCursorDateValueWithDateParserColumn() throws Exception {
        MatrixCursor cursor = Mockito.mock(MatrixCursor.class);
        Mockito.doReturn(0).when(cursor).getColumnIndex(Mockito.anyString());
        Mockito.doReturn("24234234").when(cursor).getString(Mockito.anyInt());

        SimpleDateFormat sdf = Mockito.mock(SimpleDateFormat.class);

        Date result1 = Whitebox.invokeMethod(AbstractDao.class, "getCursorValueAsDate", cursor, "", sdf);
        Assert.assertNull(result1);
        Mockito.doReturn(Cursor.FIELD_TYPE_INTEGER).when(cursor).getType(Mockito.anyInt());

        Mockito.doThrow(new ParseException("Test exception", 0)).when(sdf).parse(Mockito.anyString());
        Date result2 = Whitebox.invokeMethod(AbstractDao.class, "getCursorValueAsDate", cursor, "sample_date", sdf);
        Assert.assertNull(result2);
    }
}
