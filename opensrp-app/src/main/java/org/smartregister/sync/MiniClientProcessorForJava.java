package org.smartregister.sync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;

import java.util.HashSet;
import java.util.List;

/**
 * This is a processor for java that enables the application processor to handover processing of an event
 * to a library processor. This means that logic for processing register-specific(eg. ANC, OPD, Maternity)
 * or library specific events (eg. Vaccination) can live in the library and only needs to be attatched to the
 * application ClientProcessorForJava. This makes libraries come with barries included :smiley:
 *
 *
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-31
 */

public interface MiniClientProcessorForJava {

    @NonNull
    HashSet<String> getEventTypes();

    boolean canProcess(@NonNull String eventType);

    void processEventClient(@NonNull EventClient eventClient, @NonNull List<Event> unsyncEvents, @Nullable ClientClassification clientClassification) throws Exception;

    boolean unSync(@Nullable List<Event> events);
}
