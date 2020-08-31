package org.smartregister.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.p2p.model.DataType;
import org.smartregister.p2p.model.dao.SenderTransferDao;
import org.smartregister.p2p.sync.data.JsonData;
import org.smartregister.p2p.sync.data.MultiMediaData;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.util.HashMap;
import java.util.TreeSet;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 10/04/2019
 */

public class P2PSenderTransferDao extends BaseP2PTransferDao implements SenderTransferDao {

    @Nullable
    @Override
    public TreeSet<DataType> getDataTypes() {
        return (TreeSet<DataType>) dataTypes.clone();
    }

    @Nullable
    @Override
    public JsonData getJsonData(@NonNull DataType dataType, long lastRecordId, int batchSize) {
        if (dataType.getName().equals(event.getName())) {
            return CoreLibrary.getInstance().context()
                    .getEventClientRepository().getEvents(lastRecordId, batchSize);
        } else if (dataType.getName().equals(client.getName())) {

            if (DrishtiApplication.getInstance().getP2PClassifier() == null) {
                return CoreLibrary.getInstance().context()
                        .getEventClientRepository().getClients(lastRecordId, batchSize);
            } else {
                return CoreLibrary.getInstance().context()
                        .getEventClientRepository().getClientsWithLastLocationID(lastRecordId, batchSize);
            }

        } else if (dataType.getName().equals(structure.getName())) {
            return CoreLibrary.getInstance().context()
                    .getStructureRepository().getStructures(lastRecordId, batchSize);
        } else if (dataType.getName().equals(task.getName())) {
            return CoreLibrary.getInstance().context()
                    .getTaskRepository().getTasks(lastRecordId, batchSize);
        } else if (CoreLibrary.getInstance().context().hasForeignEvents() && dataType.getName().equals(foreignClient.getName())) {
            return CoreLibrary.getInstance().context()
                    .getForeignEventClientRepository().getClients(lastRecordId, batchSize);
        } else if (CoreLibrary.getInstance().context().hasForeignEvents() && dataType.getName().equals(foreignEvent.getName())) {
            return CoreLibrary.getInstance().context()
                    .getForeignEventClientRepository().getEvents(lastRecordId, batchSize);
        } else {
            Timber.e(P2PLibrary.getInstance().getContext().getString(R.string.log_data_type_provided_does_not_exist_in_the_sender)
                    , dataType.getName());
            return null;
        }
    }

    @Nullable
    @Override
    public MultiMediaData getMultiMediaData(@NonNull DataType dataType, long lastRecordId) {
        if (dataType.getName().equalsIgnoreCase(profilePic.getName())) {
            HashMap<String, Object> imageDetails = CoreLibrary.getInstance().context()
                    .imageRepository().getImage(lastRecordId);

            if (imageDetails != null) {
                File inputFile = new File((String) imageDetails.get(ImageRepository.filepath_COLUMN));

                if (inputFile.exists()) {
                    MultiMediaData multiMediaData = new MultiMediaData(
                            inputFile,
                            (long) imageDetails.get(AllConstants.ROWID)
                    );

                    imageDetails.remove(ImageRepository.filepath_COLUMN);

                    HashMap<String, String> multimediaDataDetails = new HashMap<>();
                    multimediaDataDetails.put(ImageRepository.syncStatus_COLUMN, (String) imageDetails.get(ImageRepository.syncStatus_COLUMN));
                    multimediaDataDetails.put(AllConstants.ROWID, String.valueOf((long) imageDetails.get(AllConstants.ROWID)));
                    multimediaDataDetails.put(ImageRepository.filecategory_COLUMN, (String) imageDetails.get(ImageRepository.filecategory_COLUMN));
                    multimediaDataDetails.put(ImageRepository.anm_ID_COLUMN, (String) imageDetails.get(ImageRepository.anm_ID_COLUMN));
                    multimediaDataDetails.put(ImageRepository.entityID_COLUMN, (String) imageDetails.get(ImageRepository.entityID_COLUMN));

                    multiMediaData.setMediaDetails(multimediaDataDetails);

                    return multiMediaData;
                } else {
                    return null;
                }
            }

            return null;
        } else {
            Timber.e(P2PLibrary.getInstance().getContext().getString(R.string.log_data_type_provided_does_not_exist_in_the_sender)
                    , dataType.getName());
            return null;
        }
    }

}
