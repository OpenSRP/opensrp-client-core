package org.smartregister.repository.dao;

import com.ibm.fhir.model.resource.Patient;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.converters.ClientConverter;
import org.smartregister.domain.Client;
import org.smartregister.pathevaluator.dao.ClientDao;
import org.smartregister.repository.EventClientRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by samuelgithengi on 9/3/20.
 */
public class ClientDaoImpl extends EventClientRepository implements ClientDao {

    @Override
    public List<Patient> findClientById(String id) {
        Client client = fetchClientByBaseEntityId(id);
        return Collections.singletonList(ClientConverter.convertClientToPatientResource(client));
    }

    @Override
    public List<Patient> findFamilyByJurisdiction(String jurisdiction) {
        return fetchClients(String.format("select %s from %s where %s =? and %s =?", client_column.json,
                clientTable.name(), client_column.locationId, client_column.clientType), new String[]{jurisdiction, AllConstants.FAMILY})
                .stream()
                .map(ClientConverter::convertClientToPatientResource)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> findFamilyByResidence(String structureId) {
        return fetchClients(String.format("select %s from %s where %s =? and %s =?", client_column.json,
                clientTable.name(), client_column.residence, client_column.clientType), new String[]{structureId, AllConstants.FAMILY})
                .stream()
                .map(ClientConverter::convertClientToPatientResource)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> findFamilyMemberyByJurisdiction(String jurisdiction) {
        return fetchClients(String.format("select %s from %s where %s =? and (%s is null or %s !=? )", client_column.json,
                clientTable.name(), client_column.locationId, client_column.clientType, client_column.clientType), new String[]{jurisdiction, AllConstants.FAMILY})
                .stream()
                .map(ClientConverter::convertClientToPatientResource)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> findFamilyMemberByResidence(String structureId) {
        return fetchClients(String.format("select %s from %s where %s =? and (%s is null or %s !=? )", client_column.json,
                clientTable.name(), client_column.residence, client_column.clientType, client_column.clientType), new String[]{structureId, AllConstants.FAMILY})
                .stream()
                .map(ClientConverter::convertClientToPatientResource)
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> findClientByRelationship(String relationship, String id) {
        return CoreLibrary.getInstance().context().getClientRelationshipRepository().findClientByRelationship(relationship, id)
                .stream()
                .map(ClientConverter::convertClientToPatientResource)
                .collect(Collectors.toList());
    }

}
