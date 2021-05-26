package org.smartregister.repository;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.TestApplication;
import org.smartregister.customshadows.ShadowOpenSRPImageLoader;
import org.smartregister.domain.ProfileImage;
import org.smartregister.p2p.model.DataType;
import org.smartregister.sync.P2PClassifier;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.util.HashMap;
import java.util.TreeSet;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 03-03-2020.
 */
@Config(shadows = {ShadowOpenSRPImageLoader.class})
public class P2PReceiverTransferDaoTest extends BaseRobolectricUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private P2PClassifier<JSONObject> classifier;
    @Mock
    private EventClientRepository eventClientRepository;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private StructureRepository structureRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private EventClientRepository foreignEventClientRepository;
    @Mock
    private CoreLibrary coreLibrary;
    private Context context;
    private P2PReceiverTransferDao p2PReceiverTransferDao;

    @Before
    public void setUp() throws Exception {
        context = Mockito.spy(CoreLibrary.getInstance().context());

        ReflectionHelpers.setField(CoreLibrary.getInstance(), "context", context);

        Mockito.doReturn(RuntimeEnvironment.application).when(context).applicationContext();
        Mockito.doReturn(eventClientRepository).when(context).getEventClientRepository();
        Mockito.doReturn(structureRepository).when(context).getStructureRepository();
        Mockito.doReturn(taskRepository).when(context).getTaskRepository();
        Mockito.doReturn(imageRepository).when(context).imageRepository();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();
        Mockito.doReturn(foreignEventClientRepository).when(context).getForeignEventClientRepository();
        Mockito.doReturn(true).when(context).hasForeignEvents();

        p2PReceiverTransferDao = Mockito.spy(new P2PReceiverTransferDao());
        ((TestApplication) TestApplication.getInstance()).setP2PClassifier(classifier);
    }

    @After
    public void tearDown() throws Exception {
        initCoreLibrary();
    }

    @Test
    public void receiveJsonShouldReturnRecordsMaxRowId() throws JSONException {
        int count = 12;

        DataType dataType = new DataType("misc", DataType.Type.NON_MEDIA, 1);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < count; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AllConstants.ROWID, (i * 2L));

            jsonArray.put(jsonObject);
        }

        long actualMaxRowId = p2PReceiverTransferDao.receiveJson(dataType, jsonArray);
        Assert.assertEquals((count - 1) * 2, actualMaxRowId);
    }

    @Test
    public void receiveJsonShouldCallEventClientRepositoryBatchInsertEventsWhenDataTypeIsEvent() throws JSONException {
        DataType dataType = new DataType(p2PReceiverTransferDao.event.getName(), DataType.Type.NON_MEDIA, 1);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.put(AllConstants.ROWID, 0);
        jsonArray.put(jsonObject);
        p2PReceiverTransferDao.receiveJson(dataType, jsonArray);
        Mockito.verify(eventClientRepository).batchInsertEvents(Mockito.eq(jsonArray), Mockito.eq(0L));
    }

    @Test
    public void receiveJsonShouldCallEventClientRepositoryBatchInsertClientsWhenDataTypeIsClient() throws JSONException {
        DataType dataType = new DataType(p2PReceiverTransferDao.client.getName(), DataType.Type.NON_MEDIA, 1);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.put(AllConstants.ROWID, 0);
        jsonArray.put(jsonObject);
        p2PReceiverTransferDao.receiveJson(dataType, jsonArray);
        Mockito.verify(eventClientRepository).batchInsertClients(Mockito.eq(jsonArray));
    }

    @Test
    public void receiveJsonShouldCallStructuresRepositoryBatchInsertStructuresWhenDataTypeIsStructure() throws JSONException {
        DataType dataType = new DataType(p2PReceiverTransferDao.structure.getName(), DataType.Type.NON_MEDIA, 1);
        JSONArray jsonArray = new JSONArray();

        p2PReceiverTransferDao.receiveJson(dataType, jsonArray);
        Mockito.verify(structureRepository).batchInsertStructures(Mockito.eq(jsonArray));
    }

    @Test
    public void receiveJsonShouldCallTaskRepositoryBatchInsertTasksWhenDataTypeIsTask() throws JSONException {
        DataType dataType = new DataType(p2PReceiverTransferDao.task.getName(), DataType.Type.NON_MEDIA, 1);
        JSONArray jsonArray = new JSONArray();

        p2PReceiverTransferDao.receiveJson(dataType, jsonArray);
        Mockito.verify(taskRepository).batchInsertTasks(Mockito.eq(jsonArray));
    }

    @Test
    public void receiveJsonShouldRemoveRowIdFromJsonObjectBeforeBatchInserting() throws JSONException {
        int count = 12;

        DataType dataType = new DataType(p2PReceiverTransferDao.event.getName(), DataType.Type.NON_MEDIA, 1);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < count; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AllConstants.ROWID, (i * 2L));

            jsonArray.put(jsonObject);
        }

        ArgumentCaptor<JSONArray> jsonArrayArgumentCaptor = ArgumentCaptor.forClass(JSONArray.class);

        p2PReceiverTransferDao.receiveJson(dataType, jsonArray);
        Mockito.doReturn(true).when(eventClientRepository).batchInsertEvents(jsonArrayArgumentCaptor.capture(), Mockito.eq(0L));

        for (int i = 0; i < jsonArray.length(); i++) {
            Assert.assertFalse(jsonArray.getJSONObject(i).has(AllConstants.ROWID));
        }
    }

    @Test
    public void receiveMultimediaShouldCallImageRepositoryWhenDataTypeIsProfilePic() {
        long fileRecordId = 78873L;

        ArgumentCaptor<ProfileImage> profileImageArgumentCaptor = ArgumentCaptor.forClass(ProfileImage.class);

        DataType dataType = new DataType(p2PReceiverTransferDao.profilePic.getName(), DataType.Type.MEDIA, 1);

        HashMap<String, Object> multimediaDetails = new HashMap<>();
        multimediaDetails.put(ImageRepository.syncStatus_COLUMN, BaseRepository.TYPE_Unsynced);
        String entityId = "isod-sdfsd-32432";
        multimediaDetails.put(ImageRepository.entityID_COLUMN, entityId);
        File file = Mockito.mock(File.class);
        Mockito.doReturn(true).when(file).exists();

        Assert.assertEquals(fileRecordId, p2PReceiverTransferDao.receiveMultimedia(dataType, file, multimediaDetails, fileRecordId));
        Mockito.verify(imageRepository).add(profileImageArgumentCaptor.capture());

        ProfileImage profileImage = profileImageArgumentCaptor.getValue();
        Assert.assertEquals("profilepic", profileImage.getFilecategory());
        Assert.assertEquals(entityId, profileImage.getEntityID());
    }


    @Test
    public void receiveMultimediaShouldReturnNegative1() {
        long fileRecordId = 78873L;

        DataType dataType = new DataType(p2PReceiverTransferDao.profilePic.getName(), DataType.Type.MEDIA, 1);
        HashMap<String, Object> multimediaDetails = new HashMap<>();
        multimediaDetails.put(ImageRepository.syncStatus_COLUMN, BaseRepository.TYPE_Unsynced);
        multimediaDetails.put(ImageRepository.entityID_COLUMN, "isod-sdfsd-32432");
        File file = Mockito.mock(File.class);

        Assert.assertEquals(-1, p2PReceiverTransferDao.receiveMultimedia(dataType, file, multimediaDetails, fileRecordId));
    }

    @Test
    public void getDataTypesShouldReturnAClonedMatchOfDataTypes() {
        TreeSet<DataType> dataTypes = p2PReceiverTransferDao.getDataTypes();

        // Assert that the instance is not the same
        Assert.assertTrue(p2PReceiverTransferDao.dataTypes != dataTypes);
        Assert.assertEquals(p2PReceiverTransferDao.dataTypes.size(), dataTypes.size());
        Assert.assertEquals(p2PReceiverTransferDao.dataTypes.first(), dataTypes.first());
        Assert.assertEquals(p2PReceiverTransferDao.dataTypes.last(), dataTypes.last());
    }

    @Test
    public void getP2PClassifier() {
        Assert.assertEquals(DrishtiApplication.getInstance().getP2PClassifier(), p2PReceiverTransferDao.getP2PClassifier());
    }

    @Test
    public void receiveJsonShouldCallForeignEventClientRepositoryBatchInsertEventsWhenDataTypeIsForeignEvent() throws JSONException {
        DataType dataType = new DataType(p2PReceiverTransferDao.foreignEvent.getName(), DataType.Type.NON_MEDIA, 1);
        JSONObject jsonObject = new JSONObject();
        String foreignLocationId = "foreign-loco-loco";
        jsonObject.put("locationId", foreignLocationId);
        JSONArray jsonArray = new JSONArray();
        jsonObject.put(AllConstants.ROWID, 0);
        jsonArray.put(jsonObject);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                JSONObject jsonObject1 = invocation.getArgument(0);

                String locationId = jsonObject1.optString("locationId");
                return locationId != null && locationId.equals(foreignLocationId);
            }
        }).when(classifier).isForeign(Mockito.any(JSONObject.class), Mockito.any(DataType.class));

        // Call the method under test
        p2PReceiverTransferDao.receiveJson(dataType, jsonArray);

        // Verify method call
        Mockito.verify(foreignEventClientRepository).batchInsertEvents(Mockito.eq(jsonArray), Mockito.eq(0L));
    }

    @Test
    public void receiveJsonShouldCallForeignEventClientRepositoryBatchInsertClientsWhenDataTypeIsForeignClient() throws JSONException {
        DataType dataType = new DataType(p2PReceiverTransferDao.foreignClient.getName(), DataType.Type.NON_MEDIA, 1);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.put(AllConstants.ROWID, 0);
        jsonArray.put(jsonObject);
        p2PReceiverTransferDao.receiveJson(dataType, jsonArray);
        Mockito.verify(eventClientRepository).batchInsertClients(Mockito.eq(jsonArray));
    }
}