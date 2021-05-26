package org.smartregister.sync;

/**
 * Created by keyman on 21/07/16.
 */
public class CloudantSyncListener {

    /**
     * Called by CloudantSyncHandler when it receives a replication complete callback.
     * CloudantSyncHandler takes care of calling this on the main thread.
     */
    void replicationComplete() {
    }

    /**
     * Called by TasksModel when it receives a replication error callback.
     * TasksModel takes care of calling this on the main thread.
     */
    void replicationError() {
    }
}
