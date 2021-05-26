package org.smartregister.sync;

import org.smartregister.p2p.model.DataType;

public interface P2PClassifier<T> {

    boolean isForeign(T t, DataType dataType);

}
