package org.smartregister.sync;

import org.smartregister.domain.FetchStatus;

/**
 * Created by Dimas Ciputra on 9/18/15.
 */
public interface AdditionalSyncService {
    FetchStatus sync();
}
