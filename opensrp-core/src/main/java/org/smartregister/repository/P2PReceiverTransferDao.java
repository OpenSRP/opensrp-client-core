package org.smartregister.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.p2p.model.DataType;
import org.smartregister.p2p.model.dao.ReceiverTransferDao;
import org.smartregister.sync.P2PClassifier;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 10/04/2019
 */
// TODO
public class P2PReceiverTransferDao extends BaseP2PTransferDao implements ReceiverTransferDao {

    private List<String> classifiables = Arrays.asList("Client", "Event", "ForeignEvent", "ForeignClient");

    @Override
    public TreeSet<DataType> getDataTypes() {
        return (TreeSet<DataType>) dataTypes.clone();
    }

    @VisibleForTesting
    public P2PClassifier<JSONObject> getP2PClassifier() {
        return DrishtiApplication.getInstance().getP2PClassifier();
    }

    @Override
    public long receiveJson(@NonNull DataType dataType, @NonNull JSONArray jsonArray) {
        EventClientRepository eventClientRepository = CoreLibrary.getInstance().context().getEventClientRepository();
        StructureRepository structureRepository = CoreLibrary.getInstance().context().getStructureRepository();
        TaskRepository taskRepository = CoreLibrary.getInstance().context().getTaskRepository();
        EventClientRepository foreignEventClientRepository = CoreLibrary.getInstance().context().getForeignEventClientRepository();

        int eventsMaxRowId = eventClientRepository.getMaxRowId(eventClientRepository.getEventTable());
        int foreignEventsMaxRowId = !CoreLibrary.getInstance().context().hasForeignEvents() ? 0 : foreignEventClientRepository.getMaxRowId(foreignEventClientRepository.getEventTable());
        long maxTableRowId = 0;

        P2PClassifier<JSONObject> classifier = getP2PClassifier();
        JSONArray homeData = new JSONArray();
        JSONArray foreignData = new JSONArray();

        // Retrieve the max and classify
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (classifier != null && classifiables.contains(dataType.getName()) && classifier.isForeign(jsonObject, dataType)) {
                    foreignData.put(jsonObject);
                } else {
                    homeData.put(jsonObject);
                }

                long rowId = jsonObject.getLong(AllConstants.ROWID);
                jsonObject.remove(AllConstants.ROWID);

                if (rowId > maxTableRowId) {
                    maxTableRowId = rowId;
                }
            }

        } catch (JSONException e) {
            Timber.e(e);
            return 0;
        }

        if (dataType.getName().startsWith(event.getName())) {

            Timber.e("Received %s total events", String.valueOf(jsonArray.length()));

            Timber.e("  %s resident events", String.valueOf(homeData.length()));
            if (homeData.length() > 0)
                eventClientRepository.batchInsertEvents(homeData, 0);

            Timber.e("  %s foreign events", String.valueOf(foreignData.length()));
            if (foreignData.length() > 0)
                foreignEventClientRepository.batchInsertEvents(foreignData, 0);

        } else if (dataType.getName().startsWith(client.getName())) {

            Timber.e("Received %s clients", String.valueOf(jsonArray.length()));

            Timber.e("  %s resident clients", String.valueOf(homeData.length()));
            if (homeData.length() > 0)
                eventClientRepository.batchInsertClients(homeData);

            Timber.e("  %s foreign clients", String.valueOf(foreignData.length()));
            if (foreignData.length() > 0)
                foreignEventClientRepository.batchInsertClients(foreignData);

        } else if (dataType.getName().startsWith(structure.getName())) {
            Timber.e("Received %s structures", String.valueOf(jsonArray.length()));
            structureRepository.batchInsertStructures(jsonArray);
        } else if (dataType.getName().startsWith(task.getName())) {
            Timber.e("Received %s tasks", String.valueOf(jsonArray.length()));
            taskRepository.batchInsertTasks(jsonArray);
        } else if (dataType.getName().startsWith(foreignClient.getName())) {

            Timber.e("Received %s foreign clients", String.valueOf(jsonArray.length()));

            Timber.e("  %s resident clients", String.valueOf(homeData.length()));
            if (homeData.length() > 0)
                eventClientRepository.batchInsertClients(homeData);

            Timber.e("  %s foreign clients", String.valueOf(foreignData.length()));
            if (foreignData.length() > 0)
                foreignEventClientRepository.batchInsertClients(foreignData);

        } else if (dataType.getName().startsWith(foreignEvent.getName())) {

            Timber.e("Received %s foreign events", String.valueOf(jsonArray.length()));

            Timber.e("  %s resident events", String.valueOf(homeData.length()));
            if (homeData.length() > 0)
                eventClientRepository.batchInsertEvents(homeData, 0);

            Timber.e("  %s foreign events", String.valueOf(foreignData.length()));
            if (foreignData.length() > 0)
                foreignEventClientRepository.batchInsertEvents(foreignData, 0);

        } else {
            Timber.e("The data type provided does not exist");
            return maxTableRowId;
        }

        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        if (!allSharedPreferences.isPeerToPeerUnprocessedEvents()) {
            allSharedPreferences.setLastPeerToPeerSyncProcessedEvent(eventsMaxRowId);
            allSharedPreferences.setLastPeerToPeerSyncForeignProcessedEvent(foreignEventsMaxRowId);
        }

        return maxTableRowId;
    }

    @Override
    public long receiveMultimedia(@NonNull DataType dataType, @NonNull File file, @Nullable HashMap<String, Object> multimediaDetails, long fileRecordId) {
        if (multimediaDetails != null && file.exists()) {
            // Read the input stream to a file
            final String syncStatus = (String) multimediaDetails.get(ImageRepository.syncStatus_COLUMN);
            final String entityId = (String) multimediaDetails.get(ImageRepository.entityID_COLUMN);

            if (OpenSRPImageLoader.moveSyncedImageAndSaveProfilePic(syncStatus, entityId, file)) {
                return fileRecordId;
            }
        }

        return -1;
    }


}
