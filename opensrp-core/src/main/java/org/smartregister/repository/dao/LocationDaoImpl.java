package org.smartregister.repository.dao;

import com.ibm.fhir.model.resource.Location;

import org.smartregister.converters.LocationConverter;
import org.smartregister.domain.PhysicalLocation;
import org.smartregister.pathevaluator.dao.LocationDao;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.StructureRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by samuelgithengi on 9/3/20.
 */
public class LocationDaoImpl extends LocationRepository implements LocationDao {

    @Override
    public List<Location> findJurisdictionsById(String id) {
        PhysicalLocation location = getLocationById(id);
        return Collections.singletonList(LocationConverter.convertPhysicalLocationToLocationResource(location));
    }

    @Override
    public List<Location> findLocationsById(String id) {
        PhysicalLocation location = getLocationById(id, StructureRepository.STRUCTURE_TABLE);
        return Collections.singletonList(LocationConverter.convertPhysicalLocationToLocationResource(location));
    }

    @Override
    public List<Location> findLocationByJurisdiction(String jurisdiction) {
        return getLocationsByParentId(jurisdiction, StructureRepository.STRUCTURE_TABLE)
                .stream()
                .map(LocationConverter::convertPhysicalLocationToLocationResource)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findChildLocationByJurisdiction(String jurisdictionId) {
        throw new UnsupportedOperationException("This is not supported on android apps");
    }
}
