package org.smartregister.repository.dao;

import com.ibm.fhir.model.resource.QuestionnaireResponse;

import org.smartregister.converters.EventConverter;
import org.smartregister.domain.Event;
import org.smartregister.pathevaluator.dao.EventDao;
import org.smartregister.repository.EventClientRepository;

import java.util.List;
import java.util.stream.Collectors;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 9/3/20.
 */
public class EventDaoImpl extends EventClientRepository implements EventDao {

    @Override
    public List<QuestionnaireResponse> findEventsByEntityIdAndPlan(String resourceId, String
            planIdentifier) {
        return fetchEvents(String.format("select %s from %s where %s =? and (%s is null or %s =? )", event_column.json,
                eventTable.name(), event_column.baseEntityId, event_column.planId, event_column.planId), new String[]{resourceId, planIdentifier})
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionnaireResponse> findEventsByJurisdictionIdAndPlan(String jurisdictionId, String planIdentifier) {
        return fetchEvents(String.format("select %s from %s where %s =? and (%s is null or %s =? )", event_column.json,
                eventTable.name(), event_column.locationId, event_column.planId, event_column.planId), new String[]{jurisdictionId, planIdentifier})
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private QuestionnaireResponse convert(Event event) {
        try {
            return EventConverter.convertEventToEncounterResource(event);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }
}
