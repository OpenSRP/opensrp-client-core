package org.smartregister.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.TestApplication;
import org.smartregister.TestP2pApplication;
import org.smartregister.domain.SyncStatus;
import org.smartregister.p2p.model.DataType;
import org.smartregister.p2p.sync.data.JsonData;
import org.smartregister.p2p.sync.data.MultiMediaData;
import org.smartregister.sync.P2PClassifier;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 04-08-2020.
 */
@Config(application = TestP2pApplication.class)
public class P2PSenderTransferDaoTest extends BaseRobolectricUnitTest {

    private P2PSenderTransferDao p2PSenderTransferDao;

    private String locationId = null;

    @Before
    public void setUp() throws Exception {
        p2PSenderTransferDao = new P2PSenderTransferDao();
    }

    @Test
    public void getDataTypesShouldReturnACloneOfDataType() {
        TreeSet<DataType> dataTypes = p2PSenderTransferDao.getDataTypes();

        Assert.assertEquals(p2PSenderTransferDao.dataTypes.size(), dataTypes.size());
        Assert.assertTrue(p2PSenderTransferDao.dataTypes != dataTypes);

        // Verify the types & order
        DataType[] expectedDataTypes = p2PSenderTransferDao.dataTypes.toArray(new DataType[0]);
        DataType[] actualDataTypes = dataTypes.toArray(new DataType[0]);

        for (int i = 0; i < expectedDataTypes.length; i++) {
            Assert.assertEquals(expectedDataTypes[i].getName(), actualDataTypes[i].getName());
            Assert.assertEquals(expectedDataTypes[i].getPosition(), actualDataTypes[i].getPosition());
        }
    }

    @Test
    public void getJsonDataShouldCallEventRepositoryGetEventsWhenDataTypeIsEvent() {
        EventClientRepository eventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "eventClientRepository", eventClientRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(eventClientRepository).getEvents(lastRecordId, batchSize, null);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.event, lastRecordId, batchSize);


        // Verify that the repository was called
        Mockito.verify(eventClientRepository).getEvents(lastRecordId, batchSize, null);
        Assert.assertEquals(jsonData, actualJsonData);
    }

    @Test
    public void getJsonDataShouldCallEventRepositoryGetClientsWhenDataTypeIsClient() {
        EventClientRepository eventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "eventClientRepository", eventClientRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(eventClientRepository).getClients(lastRecordId, batchSize, null);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.client, lastRecordId, batchSize);


        // Verify that the repository was called
        Mockito.verify(eventClientRepository).getClients(lastRecordId, batchSize, null);
        Assert.assertEquals(jsonData, actualJsonData);
    }

    @Test
    public void getJsonDataShouldCallEventRepositoryGetClientsWithLocationIdWhenDataTypeIsClientAndP2pClassifierIsConfigured() {
        EventClientRepository eventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "eventClientRepository", eventClientRepository);

        ((TestApplication) TestApplication.getInstance()).setP2PClassifier(Mockito.mock(P2PClassifier.class));

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(eventClientRepository).getClientsWithLastLocationID(lastRecordId, batchSize);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.client, lastRecordId, batchSize);


        // Verify that the repository was called
        Mockito.verify(eventClientRepository).getClientsWithLastLocationID(lastRecordId, batchSize);
        Assert.assertEquals(jsonData, actualJsonData);
    }

    @Test
    public void getJsonDataShouldCallStructureRepositoryGetStructuresWhenDataTypeIsStructure() {
        StructureRepository structureRepository = Mockito.spy(CoreLibrary.getInstance().context().getStructureRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "structureRepository", structureRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(structureRepository).getStructures(lastRecordId, batchSize, locationId);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.structure, lastRecordId, batchSize);


        // Verify that the repository was called
        Mockito.verify(structureRepository).getStructures(lastRecordId, batchSize, locationId);
        Assert.assertEquals(jsonData, actualJsonData);
    }

    @Test
    public void getJsonDataShouldCallTaskRepositoryGetTasksWhenDataTypeIsTask() {
        TaskRepository taskRepository = Mockito.spy(CoreLibrary.getInstance().context().getTaskRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "taskRepository", taskRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(taskRepository).getTasks(lastRecordId, batchSize, locationId);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.task, lastRecordId, batchSize);

        // Verify that the repository was called
        Mockito.verify(taskRepository).getTasks(lastRecordId, batchSize, locationId);
        Assert.assertEquals(jsonData, actualJsonData);
    }

    @Test
    public void getJsonDataShouldCallEventRepositoryGetClientsWhenContextHasForeignEventsAndDataTypeIsForeignClient() {
        ((TestApplication) TestApplication.getInstance()).setP2PClassifier(Mockito.mock(P2PClassifier.class));
        EventClientRepository foreignEventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getForeignEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "foreignEventClientRepository", foreignEventClientRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(foreignEventClientRepository).getClients(lastRecordId, batchSize, null);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.foreignClient, lastRecordId, batchSize);

        // Verify that the repository was called
        Mockito.verify(foreignEventClientRepository).getClients(lastRecordId, batchSize, null);
        Assert.assertEquals(jsonData, actualJsonData);
    }

    @Test
    public void getJsonDataShouldCallEventRepositoryGetEventsWhenContextHasForeignEventsAndDataTypeIsForeignEvent() {
        ((TestApplication) TestApplication.getInstance()).setP2PClassifier(Mockito.mock(P2PClassifier.class));
        EventClientRepository foreignEventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getForeignEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "foreignEventClientRepository", foreignEventClientRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(foreignEventClientRepository).getEvents(lastRecordId, batchSize, null);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.foreignEvent, lastRecordId, batchSize);

        // Verify that the repository was called
        Mockito.verify(foreignEventClientRepository).getEvents(lastRecordId, batchSize, null);
        Assert.assertEquals(jsonData, actualJsonData);
    }

    @Config(application = TestP2pApplication.class)
    @Test
    public void getJsonDataShouldReturnNullAndMakeNoCallsWhenContextHasForeignEventsAndDataTypeIsCoach() {
        EventClientRepository foreignEventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getForeignEventClientRepository());
        EventClientRepository eventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "foreignEventClientRepository", foreignEventClientRepository);
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "eventClientRepository", eventClientRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(foreignEventClientRepository).getEvents(lastRecordId, batchSize, null);

        DataType coachDataType = new DataType("coach", DataType.Type.NON_MEDIA, 99);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(coachDataType, lastRecordId, batchSize);

        // Verify null is returned
        Assert.assertNull(actualJsonData);

        // Verify that the repository was called
        Mockito.verify(foreignEventClientRepository, Mockito.never()).getEvents(lastRecordId, batchSize, null);
        Mockito.verify(foreignEventClientRepository, Mockito.never()).getClients(lastRecordId, batchSize, null);
        Mockito.verify(eventClientRepository, Mockito.never()).getEvents(lastRecordId, batchSize, null);
        Mockito.verify(eventClientRepository, Mockito.never()).getClients(lastRecordId, batchSize, null);
    }

    @Test
    public void getMultiMediaDataShouldCallImageRepositoryAndReturnMultiMediaDataWhenDataTypeIsProfielPic() throws IOException {
        ((TestApplication) TestApplication.getInstance()).setP2PClassifier(Mockito.mock(P2PClassifier.class));
        ImageRepository imageRepository = Mockito.spy(CoreLibrary.getInstance().context().imageRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "imageRepository", imageRepository);

        int lastRecordId = 789;
        String anmId = "90293-fsdawecSD";
        String imagePath = "profile-pig.png";

        // Create the image
        new File(imagePath).createNewFile();

        HashMap<String, Object> imageDetails = new HashMap<>();
        imageDetails.put(ImageRepository.filepath_COLUMN, imagePath);
        imageDetails.put(ImageRepository.syncStatus_COLUMN, SyncStatus.SYNCED.value());
        imageDetails.put(AllConstants.ROWID, 87L);
        imageDetails.put(ImageRepository.filecategory_COLUMN, "profile-pic-male");
        imageDetails.put(ImageRepository.anm_ID_COLUMN, anmId);
        imageDetails.put(ImageRepository.entityID_COLUMN, "entity-id");

        Mockito.doReturn(imageDetails).when(imageRepository).getImage(lastRecordId);

        // Call the method under test
        MultiMediaData actualJsonData = p2PSenderTransferDao.getMultiMediaData(p2PSenderTransferDao.profilePic, lastRecordId);

        // Verify that the repository was called
        Mockito.verify(imageRepository).getImage(lastRecordId);
        Assert.assertEquals("entity-id", actualJsonData.getMediaDetails().get(ImageRepository.entityID_COLUMN));
        Assert.assertEquals(anmId, actualJsonData.getMediaDetails().get(ImageRepository.anm_ID_COLUMN));
        Assert.assertEquals("profile-pic-male", actualJsonData.getMediaDetails().get(ImageRepository.filecategory_COLUMN));
        Assert.assertEquals(SyncStatus.SYNCED.value(), actualJsonData.getMediaDetails().get(ImageRepository.syncStatus_COLUMN));
        Assert.assertEquals(String.valueOf(87L), actualJsonData.getMediaDetails().get(AllConstants.ROWID));
        Assert.assertEquals(87L, actualJsonData.getRecordId());
        Assert.assertNotNull(actualJsonData.getFile());
    }

    @Test
    public void getMultiMediaDataShouldReturnNullWhenDataTypeIsNotProfielPic() throws IOException {
        ((TestApplication) TestApplication.getInstance()).setP2PClassifier(Mockito.mock(P2PClassifier.class));
        ImageRepository imageRepository = Mockito.spy(CoreLibrary.getInstance().context().imageRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "imageRepository", imageRepository);

        int lastRecordId = 789;
        String anmId = "90293-fsdawecSD";
        String imagePath = "profile-pig.png";

        // Create the image
        new File(imagePath).createNewFile();

        HashMap<String, Object> imageDetails = new HashMap<>();
        imageDetails.put(ImageRepository.filepath_COLUMN, imagePath);
        imageDetails.put(ImageRepository.syncStatus_COLUMN, SyncStatus.SYNCED.value());
        imageDetails.put(AllConstants.ROWID, 87L);
        imageDetails.put(ImageRepository.filecategory_COLUMN, "profile-pic-male");
        imageDetails.put(ImageRepository.anm_ID_COLUMN, anmId);
        imageDetails.put(ImageRepository.entityID_COLUMN, "entity-id");

        Mockito.doReturn(imageDetails).when(imageRepository).getImage(lastRecordId);

        DataType dataType = new DataType("some data type", DataType.Type.MEDIA, 9);

        // Call the method under test
        MultiMediaData actualJsonData = p2PSenderTransferDao.getMultiMediaData(dataType, lastRecordId);

        // Verify that the repository was called
        Mockito.verify(imageRepository, Mockito.times(0)).getImage(lastRecordId);
        Assert.assertNull(actualJsonData);
    }
    @Test
    public void getMultiMediaDataShouldCallImageRepositoryAndReturnNullWhenDataTypeIsProfielPicAndImageRecordIsNotFound() throws IOException {
        ((TestApplication) TestApplication.getInstance()).setP2PClassifier(Mockito.mock(P2PClassifier.class));
        ImageRepository imageRepository = Mockito.spy(CoreLibrary.getInstance().context().imageRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "imageRepository", imageRepository);

        int lastRecordId = 789;
        Mockito.doReturn(null).when(imageRepository).getImage(lastRecordId);

        // Call the method under test
        MultiMediaData actualJsonData = p2PSenderTransferDao.getMultiMediaData(p2PSenderTransferDao.profilePic, lastRecordId);

        // Verify that the repository was called
        Mockito.verify(imageRepository, Mockito.times(1)).getImage(lastRecordId);
        Assert.assertNull(actualJsonData);
    }
}