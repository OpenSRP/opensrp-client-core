package org.smartregister.repository;

import org.smartregister.AllConstants;
import org.smartregister.p2p.model.DataType;

import java.util.TreeSet;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 11/04/2019
 */

public abstract class BaseP2PTransferDao {

    protected DataType client = new DataType(AllConstants.P2PDataTypes.CLIENT, DataType.Type.NON_MEDIA, 1);
    protected DataType event = new DataType(AllConstants.P2PDataTypes.EVENT, DataType.Type.NON_MEDIA, 2);
    protected DataType profilePic = new DataType(AllConstants.P2PDataTypes.PROFILE_PIC, DataType.Type.MEDIA, 3);
    protected DataType structure = new DataType(AllConstants.P2PDataTypes.STRUCTURE, DataType.Type.NON_MEDIA, 4);
    protected DataType task = new DataType(AllConstants.P2PDataTypes.TASK, DataType.Type.NON_MEDIA, 5);
    protected DataType foreignEvent = new DataType(AllConstants.P2PDataTypes.FOREIGN_EVENT, DataType.Type.NON_MEDIA, 6);
    protected DataType foreignClient = new DataType(AllConstants.P2PDataTypes.FOREIGN_CLIENT, DataType.Type.NON_MEDIA, 7);

    protected TreeSet<DataType> dataTypes;

    public BaseP2PTransferDao() {
        dataTypes = new TreeSet<>();

        dataTypes.add(client);
        dataTypes.add(event);
        dataTypes.add(profilePic);
        dataTypes.add(structure);
        dataTypes.add(task);
        dataTypes.add(foreignEvent);
        dataTypes.add(foreignClient);
    }
}
