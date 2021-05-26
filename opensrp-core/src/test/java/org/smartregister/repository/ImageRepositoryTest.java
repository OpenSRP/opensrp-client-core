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
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.SyncStatus;

import java.util.HashMap;


/**
 * Created by kaderchowdhury on 21/11/17.
 */

public class ImageRepositoryTest extends BaseUnitTest {

    public static final String ID_COLUMN = "imageid";
    public static final String anm_ID_COLUMN = "anmId";
    public static final String entityID_COLUMN = "entityID";
    public static final String filepath_COLUMN = "filepath";
    public static final String syncStatus_COLUMN = "syncStatus";
    public static final String filecategory_COLUMN = "filecategory";
    private static final String contenttype_COLUMN = "contenttype";

    private ImageRepository imageRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private Repository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        imageRepository = new ImageRepository();
        imageRepository.updateMasterRepository(repository);
        Mockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
    }

    @Test
    public void assertConstructorNotNull() {
        Assert.assertNotNull(imageRepository);
    }

    @Test
    public void assertOnCrateCallsDatabaseExec() {
        imageRepository.onCreate(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(2)).execSQL(Mockito.anyString());
    }

    @Test
    public void assertAddCallsDatabaseInsert() {
        imageRepository.add(getProfileImage());
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(String.class), Mockito.any(ContentValues.class));
    }

    @Test
    public void assertallProfileImages() {
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getCursor());
        Assert.assertNotNull(imageRepository.allProfileImages());
    }

    @Test
    public void assertclose() {
        imageRepository.close("1");
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(Mockito.anyString(), Mockito.any(ContentValues.class), Mockito.anyString(), Mockito.any(String[].class));
    }

    @Test
    public void assertfindAllUnSynced() {
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getCursor());
        Assert.assertNotNull(imageRepository.findAllUnSynced());
    }

    @Test
    public void assertfindByEntityId() {
        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString(), Mockito.any(String[].class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class), Mockito.isNull(String.class))).thenReturn(getCursor());
        Assert.assertNotNull(imageRepository.findByEntityId("1"));
    }

    @Test
    public void getImageShouldReturnValidHashMap() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"rowid", filepath_COLUMN, syncStatus_COLUMN
                , entityID_COLUMN, anm_ID_COLUMN , filecategory_COLUMN});
        matrixCursor.addRow(new String[]{"1", "sample_file_path", SyncStatus.SYNCED.toString(), "992jdwmkkk", "sample_anm_id", "profile_image"});

        Mockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(String[].class), Mockito.anyString()
                , Mockito.any(String[].class), (String) Mockito.isNull(), (String) Mockito.isNull()
                , Mockito.anyString(), Mockito.anyString()))
                .thenReturn(matrixCursor);

        HashMap<String, Object> actualDetails = imageRepository.getImage(9);

        Assert.assertEquals(1l, actualDetails.get(AllConstants.ROWID));
        Assert.assertEquals(6, actualDetails.size());
    }

    public ProfileImage getProfileImage() {
        ProfileImage profileImage = new ProfileImage();
        profileImage.setImageid("1");
        profileImage.setAnmId("2");
        profileImage.setContenttype("ANC");
        profileImage.setEntityID("4");
        profileImage.setFilepath("/");
        profileImage.setSyncStatus("0");
        profileImage.setFilecategory("png");
        return profileImage;
    }

    public MatrixCursor getCursor() {
        MatrixCursor matrixCursor = new MatrixCursor(new String[]{ID_COLUMN, anm_ID_COLUMN,
                entityID_COLUMN, contenttype_COLUMN, filepath_COLUMN, syncStatus_COLUMN,
                filecategory_COLUMN});
        matrixCursor.addRow(new String[]{"1", "2", "3", "4", "5", "6", "7"});
        return matrixCursor;
    }
}
