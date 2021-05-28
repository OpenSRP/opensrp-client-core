package org.smartregister;

import androidx.annotation.Nullable;

import org.smartregister.p2p.authorizer.P2PAuthorizationService;
import org.smartregister.p2p.callback.SyncFinishedCallback;
import org.smartregister.p2p.contract.RecalledIdentifier;
import org.smartregister.p2p.model.dao.ReceiverTransferDao;
import org.smartregister.p2p.model.dao.SenderTransferDao;
import org.smartregister.sync.ClientProcessorForJava;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-06-03
 */
public class P2POptions {

    private P2PAuthorizationService authorizationService;
    private ReceiverTransferDao receiverTransferDao;
    private SenderTransferDao senderTransferDao;
    private ClientProcessorForJava clientProcessor;
    private SyncFinishedCallback syncFinishedCallback;
    @Nullable
    private RecalledIdentifier recalledIdentifier;
    private String[] locationsFilter;

    private boolean enableP2PLibrary;
    private int batchSize = AllConstants.PeerToPeer.P2P_LIBRARY_DEFAULT_BATCH_SIZE;

    public P2POptions(boolean enableP2PLibrary) {
        this.enableP2PLibrary = enableP2PLibrary;
    }

    public void setAuthorizationService(P2PAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public void setReceiverTransferDao(ReceiverTransferDao receiverTransferDao) {
        this.receiverTransferDao = receiverTransferDao;
    }

    public void setSenderTransferDao(SenderTransferDao senderTransferDao) {
        this.senderTransferDao = senderTransferDao;
    }

    public P2PAuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public ReceiverTransferDao getReceiverTransferDao() {
        return receiverTransferDao;
    }

    public SenderTransferDao getSenderTransferDao() {
        return senderTransferDao;
    }

    public boolean isEnableP2PLibrary() {
        return enableP2PLibrary;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public SyncFinishedCallback getSyncFinishedCallback() {
        return syncFinishedCallback;
    }

    public void setSyncFinishedCallback(SyncFinishedCallback syncFinishedCallback) {
        this.syncFinishedCallback = syncFinishedCallback;
    }

    @Nullable
    public RecalledIdentifier getRecalledIdentifier() {
        return recalledIdentifier;
    }

    public void setRecalledIdentifier(@Nullable RecalledIdentifier recalledIdentifier) {
        this.recalledIdentifier = recalledIdentifier;
    }

    public String[] getLocationsFilter() {
        return locationsFilter;
    }

    public void setLocationsFilter(String[] locationsFilter) {
        this.locationsFilter = locationsFilter;
    }
}
