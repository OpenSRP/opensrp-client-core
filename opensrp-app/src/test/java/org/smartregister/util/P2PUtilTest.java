package org.smartregister.util;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.smartregister.BaseRobolectricUnitTest;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 22-09-2020.
 */
public class P2PUtilTest extends BaseRobolectricUnitTest {

    @Test
    public void getMaxRowIdShouldReturnCursorResultWhenCursorIsNotNull() {
        SQLiteDatabase db = Mockito.mock(SQLiteDatabase.class);
        String tableName = "structure";
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"max_row_id"});
        matrixCursor.addRow(new Object[]{56});

        Mockito.doReturn(matrixCursor).when(db).rawQuery(Mockito.eq("SELECT max(rowid) AS max_row_id FROM structure"), Mockito.nullable(String[].class));

        int maxRowId = P2PUtil.getMaxRowId(tableName, db);

        Assert.assertEquals(56, maxRowId);
        Mockito.verify(db, Mockito.times(1)).rawQuery(Mockito.eq("SELECT max(rowid) AS max_row_id FROM structure"), Mockito.nullable(String[].class));
    }

    @Test
    public void checkIfExistsByIdShouldReturnTrueWhenCursorIsNotNullAndNotEmpty() {
        SQLiteDatabase db = Mockito.mock(SQLiteDatabase.class);
        String tableName = "structure";
        String entityId = "i9sd23dsci9230";
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"max_row_id"});
        matrixCursor.addRow(new Object[]{56});

        ArgumentCaptor<String[]> argumentCaptor = ArgumentCaptor.forClass(String[].class);
        Mockito.doReturn(matrixCursor).when(db).rawQuery(Mockito.eq("SELECT _id FROM structure WHERE _id =?"), argumentCaptor.capture());

        Boolean recordExists = P2PUtil.checkIfExistsById(tableName, entityId, db);

        Assert.assertTrue(recordExists);
        Assert.assertEquals(entityId, argumentCaptor.getValue()[0]);
        Mockito.verify(db, Mockito.times(1)).rawQuery(Mockito.eq("SELECT _id FROM structure WHERE _id =?"), Mockito.nullable(String[].class));
    }

    @Test
    public void checkIfExistsByIdShouldReturnFalseWhenCursorIsNotNullAndNotEmpty() {
        SQLiteDatabase db = Mockito.mock(SQLiteDatabase.class);
        String tableName = "structure";
        String entityId = "i9sd23dsci9230";
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"max_row_id"});

        ArgumentCaptor<String[]> argumentCaptor = ArgumentCaptor.forClass(String[].class);
        Mockito.doReturn(matrixCursor).when(db).rawQuery(Mockito.eq("SELECT _id FROM structure WHERE _id =?"), argumentCaptor.capture());

        Boolean recordExists = P2PUtil.checkIfExistsById(tableName, entityId, db);

        Assert.assertFalse(recordExists);
        Assert.assertEquals(entityId, argumentCaptor.getValue()[0]);
        Mockito.verify(db, Mockito.times(1)).rawQuery(Mockito.eq("SELECT _id FROM structure WHERE _id =?"), Mockito.nullable(String[].class));
    }
}