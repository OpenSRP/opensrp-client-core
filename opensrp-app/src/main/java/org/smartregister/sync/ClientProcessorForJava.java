package org.smartregister.sync;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;

import com.ibm.fhir.model.resource.QuestionnaireResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.converters.ClientConverter;
import org.smartregister.converters.EventConverter;
import org.smartregister.domain.Address;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClassificationRule;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.ClientField;
import org.smartregister.domain.jsonmapping.Column;
import org.smartregister.domain.jsonmapping.ColumnType;
import org.smartregister.domain.jsonmapping.JsonMapping;
import org.smartregister.domain.jsonmapping.Rule;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.util.AppExecutors;
import org.smartregister.util.AssetHandler;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.event.Event.FORM_SUBMITTED;

public class ClientProcessorForJava {

    public static final String JSON_ARRAY = "json_array";
    protected static final String VALUES_KEY = "values";
    protected static final String detailsUpdated = "detailsUpdated";
    protected static ClientProcessorForJava instance;
    protected HashMap<String, MiniClientProcessorForJava> processorMap = new HashMap<>();
    protected HashMap<MiniClientProcessorForJava, List<Event>> unsyncEventsPerProcessor = new HashMap<>();
    private String[] openmrsGenIds = {};
    private Map<String, Object> jsonMap = new HashMap<>();
    private Context mContext;

    private AppExecutors appExecutors;

    public ClientProcessorForJava(Context context) {
        mContext = context;
        appExecutors = new AppExecutors();
    }

    public static ClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new ClientProcessorForJava(context);
        }

        return instance;
    }


    public synchronized void processClient(List<EventClient> eventClientList) throws Exception {
        processClient(eventClientList, false);
    }

    public synchronized void processClient(List<EventClient> eventClientList, boolean localSubmission) throws Exception {

        final String EC_CLIENT_CLASSIFICATION = "ec_client_classification.json";
        ClientClassification clientClassification = assetJsonToJava(EC_CLIENT_CLASSIFICATION, ClientClassification.class);
        if (clientClassification == null) {
            return;
        }

        if (!eventClientList.isEmpty()) {
            for (EventClient eventClient : eventClientList) {
                // Iterate through the events
                Client client = eventClient.getClient();
                if (client != null) {
                    Event event = eventClient.getEvent();
                    String eventType = event.getEventType();

                    if (processorMap.containsKey(eventType)) {
                        try {
                            processEventUsingMiniProcessor(clientClassification, eventClient, eventType);
                        } catch (Exception ex) {
                            Timber.e(ex);
                        }
                    } else {
                        processEvent(event, client, clientClassification);
                    }
                }

                if (localSubmission && CoreLibrary.getInstance().getSyncConfiguration().runPlanEvaluationOnClientProcessing()) {
                    processPlanEvaluation(eventClient);
                }
            }
        }
    }

    /**
     * Process plan evaluation for an event client
     *
     * @param eventClient
     */
    public void processPlanEvaluation(EventClient eventClient) {
        appExecutors.diskIO().execute(() -> {
            String planIdentifier = eventClient.getEvent().getDetails().get("planIdentifier");

            if (StringUtils.isNotBlank(planIdentifier)) {
                PlanDefinition plan = CoreLibrary.getInstance().context().getPlanDefinitionRepository().findPlanDefinitionById(planIdentifier);
                PlanEvaluator planEvaluator = new PlanEvaluator(eventClient.getEvent().getProviderId());
                QuestionnaireResponse questionnaireResponse = EventConverter.convertEventToEncounterResource(eventClient.getEvent());
                if (eventClient.getClient() != null) {
                    questionnaireResponse = questionnaireResponse.toBuilder().contained(ClientConverter.convertClientToPatientResource(eventClient.getClient())).build();
                }
                planEvaluator.evaluatePlan(plan, questionnaireResponse);
            }
        });
    }

    /**
     * Call this method to flag the event as processed in the local repository.
     * All events valid or otherwise must be flagged to avoid re-processing
     *
     * @param event
     */
    public void completeProcessing(Event event) {
        if (event == null)
            return;

        if (event.getServerVersion() != 0) {
            CoreLibrary.getInstance().context().allSharedPreferences().updateLastClientProcessedTimeStamp(event.getServerVersion());
        }
        CoreLibrary.getInstance().context()
                .getEventClientRepository().markEventAsProcessed(event.getFormSubmissionId());
    }

    public Boolean processEvent(Event event, Client client, ClientClassification clientClassification) throws Exception {
        try {
            // mark event as processed regardless of any errors
            completeProcessing(event);

            if (event.getCreator() != null) {
                Timber.i("EVENT from openmrs");
            }
            // For data integrity check if a client exists, if not pull one from cloudant and
            // insert in drishti sqlite db

            if (client == null) {
                return false;
            }

            // Get the client type classification
            List<ClassificationRule> clientClasses = clientClassification.case_classification_rules;
            if (clientClasses == null || clientClasses.isEmpty()) {
                return false;
            }

            // Check if child is deceased and skip
            if (client.getDeathdate() != null) {
                return false;
            }

            for (ClassificationRule clientClass : clientClasses) {
                processClientClass(clientClass, event, client);
            }

            // Incase the details have not been updated
            String updatedString = event.getDetails() != null ? event.getDetails().get(detailsUpdated) : null;
            if (StringUtils.isBlank(updatedString) || !Boolean.TRUE.toString().equals(updatedString)) {
                updateClientDetailsTable(event, client);
            }

            return true;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public Boolean processClientClass(ClassificationRule clientClass, Event event, Client client) {
        try {
            if (clientClass == null) {
                return false;
            }

            if (event == null) {
                return false;
            }

            if (client == null) {
                return false;
            }

            Rule rule = clientClass.rule;
            List<org.smartregister.domain.jsonmapping.Field> fields = rule.fields;

            for (org.smartregister.domain.jsonmapping.Field field : fields) {
                processField(field, event, client);
            }
            return true;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public Boolean processField(org.smartregister.domain.jsonmapping.Field field, Event event, Client client) {
        try {
            if (field == null) {
                return false;
            }

            // keep checking if the event data matches the values expected by each rule, break the
            // moment the rule fails
            String dataSegment = null;
            String fieldName = field.field;
            String fieldValue = field.field_value;
            String responseKey = null;

            if (fieldName != null && fieldName.contains(".")) {
                String fieldNameArray[] = fieldName.split("\\.");
                dataSegment = fieldNameArray[0];
                fieldName = fieldNameArray[1];
                String concept = field.concept;

                if (concept != null) {
                    fieldValue = concept;
                    responseKey = VALUES_KEY;
                }
            }

            List<String> createsCase = field.creates_case;
            List<String> closesCase = field.closes_case;

            // some fields are in the main doc e.g event_type so fetch them from the main doc
            if (StringUtils.isNotBlank(dataSegment)) {
                List<String> responseValues = field.values;
                Object dataSegmentObject = getValue(event, dataSegment);
                if (dataSegmentObject != null) {
                    if (dataSegmentObject instanceof List) {

                        List dataSegmentList = (List) dataSegmentObject;
                        // Iterate in the segment e.g obs segment
                        for (Object segment : dataSegmentList) {
                            // let's discuss this further, to get the real value in the doc we've to
                            // use the keys 'fieldcode' and 'value'
                            Object value = getValue(segment, fieldName);
                            String docSegmentFieldValue = value != null ? value.toString() : "";
                            Object values = getValue(segment, responseKey);
                            List<String> docSegmentResponseValues = new ArrayList<>();
                            if (values instanceof List) {
                                docSegmentResponseValues = getValues((List) values);
                            }

                            if (docSegmentFieldValue.equalsIgnoreCase(fieldValue) && (!Collections
                                    .disjoint(responseValues, docSegmentResponseValues))) {
                                // this is the event obs we're interested in put it in the respective
                                // bucket specified by type variable
                                processCaseModel(event, client, createsCase);
                                closeCase(client, closesCase);
                            }

                        }
                    } else if (dataSegmentObject instanceof Map) {
                        Map map = (Map) dataSegmentObject;
                        // This means field_value and response_key are null so pick the
                        // value from the json object for the field_name
                        if (map.containsKey(fieldName)) {
                            Object objectValue = map.get(fieldName);
                            if (objectValue != null && objectValue instanceof String) {
                                String docSegmentFieldValue = objectValue.toString();
                                if (docSegmentFieldValue.equalsIgnoreCase(fieldValue)) {
                                    processCaseModel(event, client, createsCase);
                                    closeCase(client, closesCase);
                                }
                            }
                        }
                    }
                }

            } else {
                //fetch from the main doc
                Object value = getValue(event, fieldName);
                String docSegmentFieldValue = value != null ? value.toString() : "";
                if (docSegmentFieldValue.equalsIgnoreCase(fieldValue)) {
                    processCaseModel(event, client, createsCase);
                    closeCase(client, closesCase);
                }
            }
            return true;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public Boolean closeCase(Client client, List<String> closesCase) {
        try {
            if (closesCase == null || closesCase.isEmpty()) {
                return false;
            }

            String baseEntityId = client.getBaseEntityId();

            for (String tableName : closesCase) {
                closeCase(tableName, baseEntityId);
                updateFTSsearch(tableName, client.getClientType(), baseEntityId, null);
            }

            return true;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public Boolean processCaseModel(Event event, Client client, List<String> createsCase) {
        try {

            if (createsCase == null || createsCase.isEmpty()) {
                return false;
            }
            for (String tableName : createsCase) {
                Table table = getColumnMappings(tableName);
                List<Column> columns = table.columns;
                String baseEntityId = getBaseEntityId(event, client, tableName);

                ContentValues contentValues = new ContentValues();
                //Add the base_entity_id
                contentValues.put(CommonRepository.BASE_ENTITY_ID_COLUMN, baseEntityId);
                contentValues.put(CommonRepository.IS_CLOSED_COLUMN, 0);

                for (Column colObject : columns) {
                    processCaseModel(event, client, colObject, contentValues);
                }

                // Modify openmrs generated identifier, Remove hyphen if it exists
                updateIdentifier(contentValues);

                // save the values to db
                executeInsertStatement(contentValues, tableName);

                String entityId = contentValues.getAsString(CommonRepository.BASE_ENTITY_ID_COLUMN);
                updateFTSsearch(tableName, client.getClientType(), entityId, contentValues);
                Long timestamp = getEventDate(event.getEventDate());
                addContentValuesToDetailsTable(contentValues, timestamp);
                updateClientDetailsTable(event, client);
            }

            return true;
        } catch (Exception e) {
            Timber.e(e);

            return null;
        }
    }

    /***
     * Method for retrieving baseEntityId used when processing Case Models
     * Allows customizing the baseEntityId for different cases
     * @param event event object
     * @param client client object
     * @param clientType client classification type
     * @return base entity id
     */
    protected String getBaseEntityId(Event event, Client client, String clientType) {
        return client != null ? client.getBaseEntityId() : event != null ? event.getBaseEntityId() : null;
    }

    public void processCaseModel(Event event, Client client, Column column, ContentValues contentValues) {
        try {
            String expectedEncounterType = event.getEventType();
            String docType = column.type;
            String columnName = column.column_name;
            JsonMapping jsonMapping = column.json_mapping;
            String dataSegment = null;
            String fieldName = jsonMapping.field;
            String fieldValue = null;
            String responseKey = null;

            String valueField = jsonMapping.value_field;

            if (fieldName != null && fieldName.contains(".")) {
                String fieldNameArray[] = fieldName.split("\\.");
                dataSegment = fieldNameArray[0];
                fieldName = fieldNameArray[1];
                fieldValue = StringUtils.isNotBlank(jsonMapping.concept) ? jsonMapping.concept
                        : (StringUtils.isNotBlank(jsonMapping.formSubmissionField) ? jsonMapping
                        .formSubmissionField : null);
                if (fieldValue != null) {
                    responseKey = VALUES_KEY;
                }
            }

            Object document = docType == null ? event : docType.equalsIgnoreCase("Event") ? event : client;

            Object docSegment;

            if (StringUtils.isNotBlank(dataSegment)) {
                // pick data from a specific section of the doc
                docSegment = getValue(document, dataSegment);
            } else {
                // else the use the main doc as the doc segment
                docSegment = document;
            }

            // special handler needed to process address,
            if (dataSegment != null && dataSegment.equalsIgnoreCase("addresses")) {
                Map<String, String> addressMap = getClientAddressAsMap(client);
                if (addressMap.containsKey(fieldName)) {
                    contentValues.put(columnName, addressMap.get(fieldName));
                }
                return;
            }

            // special handler for relationalid
            if (dataSegment != null && dataSegment.equalsIgnoreCase("relationships") && document instanceof Client) {
                Map<String, List<String>> relationshipMap = client.getRelationships();

                List<String> relationShipIds = relationshipMap.get(fieldName);
                if (relationShipIds != null && !relationShipIds.isEmpty()) {
                    contentValues.put(columnName, relationShipIds.get(0));
                }

                return;
            }

            String encounterType = jsonMapping.event_type;

            if (docSegment instanceof List) {

                List docSegmentList = (List) docSegment;

                for (Object segment : docSegmentList) {
                    String columnValue = null;

                    if (fieldValue == null) {
                        // This means field_value and response_key are null so pick the
                        // value from the json object for the field_name
                        columnValue = getValueAsString(segment, fieldName);
                    } else {
                        // this means field_value and response_key are not null e.g when
                        // retrieving some value in the events obs section
                        String expectedFieldValue = getValueAsString(segment, fieldName);
                        // some events can only be differentiated by the event_type value
                        // eg pnc1,pnc2, anc1,anc2
                        // check if encountertype (the one in ec_client_fields.json) is
                        // null or it matches the encounter type from the ec doc we're
                        // processing
                        boolean encounterTypeMatches =
                                (encounterType == null) || (encounterType
                                        .equalsIgnoreCase(expectedEncounterType));

                        if (encounterTypeMatches && expectedFieldValue
                                .equalsIgnoreCase(fieldValue)) {

                            if (StringUtils.isNotBlank(valueField)) {
                                columnValue = getValueAsString(segment, valueField);
                            }

                            if (columnValue == null) {
                                Object values = getValue(segment, responseKey);
                                if (values instanceof List) {
                                    columnValue = getValuesStr(segment, getValues((List) values), column.saveFormat);
                                }
                            }
                        }
                    }

                    // after successfully retrieving the column name and value store it
                    // in Content value
                    if (columnValue != null) {
                        columnValue = getHumanReadableConceptResponse(columnValue, segment);
                        String formattedValue = getFormattedValue(column, columnValue);
                        contentValues.put(columnName, formattedValue);
                    }
                }

            } else if (docSegment instanceof Map) {
                Map map = (Map) docSegment;
                // This means field_value and response_key are null so pick the
                // value from the json object for the field_name
                if (fieldValue == null && map.containsKey(fieldName)) {
                    Object mapValue = map.get(fieldName);
                    if (mapValue != null) {
                        if (mapValue instanceof String) {
                            String columnValue = getHumanReadableConceptResponse(mapValue.toString(), docSegment);
                            contentValues.put(columnName, columnValue);
                        } else {
                            contentValues.put(columnName, String.valueOf(mapValue));
                        }
                    }
                }
            } else {
                //e.g client attributes section
                String columnValue = getValueAsString(docSegment, fieldName);

                // after successfully retrieving the column name and value store it in
                // Content value
                if (columnValue != null) {
                    columnValue = getHumanReadableConceptResponse(columnValue,
                            docSegment);
                    contentValues.put(columnName, columnValue);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Formats values from {@param values} into a string based on {@param segment} properties
     *
     * @param segment
     * @param values
     * @return @return A formatted values String
     */
    private String getValuesStr(Object segment, List<String> values, String saveFormat) {
        String columnValue = null;
        if (values.isEmpty()) {
            return columnValue;
        }

        // save obs as json array string e.g ["val1","val2"] if specified by the developer
        if ((saveFormat != null && JSON_ARRAY.equals(saveFormat))
                || ((segment instanceof Obs) && ((Obs) segment).isSaveObsAsArray())) {
            columnValue = getValuesAsArray(values);
        } else {
            columnValue = values.get(0);
        }

        return columnValue;
    }

    private String getValuesAsArray(List<String> values) {
        JSONArray jsonArray = new JSONArray();
        for (String value : values) {
            jsonArray.put(value);
        }
        return jsonArray.toString();
    }

    /**
     * Reformat the data to be persisted in the database.
     * This function will reformat dates with supplied types for storage in the DB
     *
     * @param column
     * @param columnValue
     * @return
     */
    protected String getFormattedValue(Column column, String columnValue) {
        // covert the column if its a formatted column with both

        String dataType = StringUtils.isNotBlank(column.dataType) ? column.dataType : "";
        switch (dataType) {
            case ColumnType.Date:
                if (StringUtils.isNotBlank(column.saveFormat) && StringUtils.isNotBlank(column.sourceFormat)) {
                    try {
                        Date sourceDate = new SimpleDateFormat(column.sourceFormat, Locale.ENGLISH).parse(columnValue);
                        return new SimpleDateFormat(column.saveFormat, Locale.ENGLISH).format(sourceDate);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            case ColumnType.String:
                if (StringUtils.isNotBlank(column.saveFormat)) {
                    return String.format(column.saveFormat, columnValue);
                }
                break;
            default:
                return columnValue;
        }

        return columnValue;
    }

    /**
     * Save the populated content values to details table
     *
     * @param values
     * @param eventDate
     */
    protected void addContentValuesToDetailsTable(ContentValues values, Long eventDate) {
        if (!CoreLibrary.getInstance().getSyncConfiguration().updateClientDetailsTable())
            return;

        try {
            String baseEntityId = values.getAsString("base_entity_id");

            for (String key : values.keySet()) {
                String value = values.getAsString(key);
                saveClientDetails(baseEntityId, key, value, eventDate);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Update the details table with the new info, All the obs are extracted and saved as key
     * value with the key being the formSubmissionField and value being the value field.
     * If the key value already exists then the row will simply be updated with the value if the
     * event date is most recent
     *
     * @param event
     * @param client
     */
    public void updateClientDetailsTable(Event event, Client client) {
        try {
            Timber.d("Started updateClientDetailsTable");

            if (CoreLibrary.getInstance().getSyncConfiguration().updateClientDetailsTable()) {
                String baseEntityId = client.getBaseEntityId();
                Long timestamp = getEventDate(event.getEventDate());

                Map<String, String> genderInfo = getGender(client);
                saveClientDetails(baseEntityId, genderInfo, timestamp);

                Map<String, String> addressInfo = getClientAddressAsMap(client);
                saveClientDetails(baseEntityId, addressInfo, timestamp);

                Map<String, String> attributes = getClientAttributes(client);
                saveClientDetails(baseEntityId, attributes, timestamp);

                Map<String, String> obs = getObsFromEvent(event);
                saveClientDetails(baseEntityId, obs, timestamp);
            }

            event.addDetails(detailsUpdated, Boolean.TRUE.toString());

            Timber.d("Finished updateClientDetailsTable");
            // save the other misc, client info date of birth...
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Retrieve the obs as key value pair <br/>
     * Key being the formSubmissionField and value being entered value
     *
     * @param event
     * @return
     */
    private Map<String, String> getObsFromEvent(Event event) {
        Map<String, String> obsMap = new HashMap<String, String>();

        try {
            List<Obs> obsList = event.getObs();
            for (Obs obs : obsList) {
                List<String> values = getValues(obs.getValues());
                String key = obs.getFormSubmissionField();
                if (StringUtils.isNotBlank(key)) {
                    for (String conceptValue : values) {
                        String value = getHumanReadableConceptResponse(conceptValue, obs);
                        if (StringUtils.isNotBlank(value)) {
                            obsMap.put(key, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return obsMap;
    }

    private Map<String, String> getClientAttributes(Client client) {
        Map<String, String> attributes = new HashMap<>();
        try {
            Map<String, Object> clientAttributes = client.getAttributes();
            for (Map.Entry<String, Object> entry : clientAttributes.entrySet()) {
                Object value = entry.getValue();
                String key = entry.getKey();

                if (value != null) {
                    attributes.put(key, value.toString());
                }
            }
        } catch (NullPointerException e) {
            Timber.e(e);
        }

        return attributes;
    }

    private Map<String, String> getGender(Client client) {
        Map<String, String> map = new HashMap<String, String>();
        final String GENDER = "gender";
        try {
            String gender = client.getGender();
            if (StringUtils.isNotBlank(gender)) {
                map.put(GENDER, gender);
            }
        } catch (NullPointerException e) {
            Timber.e(e);
        }

        return map;
    }

    public void saveClientDetails(String baseEntityId, Map<String, String> values, Long timestamp) {
        for (String key : values.keySet()) {
            String value = values.get(key);
            saveClientDetails(baseEntityId, key, value, timestamp);
        }
    }

    /**
     * Save a single details row to the db
     *
     * @param baseEntityId
     * @param key
     * @param value
     * @param timestamp
     */
    private void saveClientDetails(String baseEntityId, String key, String value, Long timestamp) {
        DetailsRepository detailsRepository = org.smartregister.CoreLibrary.getInstance().context().
                detailsRepository();
        detailsRepository.add(baseEntityId, key, value, timestamp);
    }


    /**
     * Get human readable values from the json doc humanreadablevalues key if the key is empty
     * return value
     *
     * @param value
     * @param object
     * @return
     * @throws Exception
     */
    protected String getHumanReadableConceptResponse(String value, Object object) {
        try {
            if (StringUtils.isBlank(value) || (object != null && !(object instanceof Obs))) {
                return value;
            }

            final String HUMAN_READABLE_VALUES = "humanReadableValues";
            List humanReadableValues = new ArrayList();
            Object humanReadableObject = getValue(object, HUMAN_READABLE_VALUES);
            if (humanReadableObject != null && humanReadableObject instanceof List) {
                humanReadableValues = (List) humanReadableObject;
            }

            if (object == null || humanReadableValues.isEmpty()) {
                String humanReadableValue = org.smartregister.CoreLibrary.getInstance().context().
                        customHumanReadableConceptResponse().get(value);

                if (StringUtils.isNotBlank(humanReadableValue)) {
                    return humanReadableValue;
                }

                return value;
            }

            return humanReadableValues.size() == 1 ? humanReadableValues.get(0).toString()
                    : humanReadableValues.toString();
        } catch (Exception e) {
            Timber.e(e);
        }
        return value;
    }

    public Map<String, String> getClientAddressAsMap(Client client) {
        Map<String, String> addressMap = new HashMap<String, String>();
        if (client == null) {
            return addressMap;
        }
        try {
            final String addressFieldsKey = "addressFields";

            List<Address> addressList = client.getAddresses();
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                Map<String, String> addressFieldMap = address.getAddressFields();
                if (addressFieldMap != null) {
                    for (Map.Entry<String, String> entry : addressFieldMap.entrySet()) {
                        addressMap.put(entry.getKey(), entry.getValue());
                    }
                }

                List<Field> fields = getFields(address.getClass());
                for (Field classField : fields) {
                    String fieldName = classField.getName();
                    if (!fieldName.equals(addressFieldsKey)) {
                        String value = getValueAsString(address, classField.getName());
                        if (value != null) {
                            addressMap.put(classField.getName(), value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return addressMap;
    }

    /**
     * Insert the a new record to the database and returns its id
     **/
    public Long executeInsertStatement(ContentValues values, String tableName) {
        CommonRepository cr = org.smartregister.CoreLibrary.getInstance().context().commonrepository(tableName);
        return cr.executeInsertStatement(values, tableName);
    }

    public void closeCase(String tableName, String baseEntityId) {
        CommonRepository cr = org.smartregister.CoreLibrary.getInstance().context().commonrepository(tableName);
        cr.closeCase(baseEntityId, tableName);
    }

    public boolean deleteCase(String tableName, String baseEntityId) {
        CommonRepository cr = org.smartregister.CoreLibrary.getInstance().context().commonrepository(tableName);
        return cr.deleteCase(baseEntityId, tableName);
    }

    public Table getColumnMappings(String registerName) {
        try {
            ClientField clientField = assetJsonToJava(CoreLibrary.getInstance().getEcClientFieldsFile(), ClientField.class);
            if (clientField == null) {
                return null;
            }
            List<Table> bindObjects = clientField.bindobjects;
            for (Table bindObject : bindObjects) {
                if (bindObject.name.equalsIgnoreCase(registerName)) {
                    return bindObject;
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    protected <T> T assetJsonToJava(String fileName, Class<T> clazz) {
        return AssetHandler.assetJsonToJava(jsonMap, mContext, fileName, clazz);
    }

    protected List<String> getValues(List list) {
        List<String> values = new ArrayList<String>();
        if (list == null) {
            return values;
        }
        for (Object o : list) {
            if (o != null) {
                values.add(o.toString());
            }
        }
        return values;
    }

    protected Object getValue(Object instance, String fieldName) {
        if (instance == null || StringUtils.isBlank(fieldName)) {
            return null;
        }
        try {
            Field field = getField(instance.getClass(), fieldName);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            return field.get(instance);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    protected String getValueAsString(Object instance, String fieldName) {
        Object object = getValue(instance, fieldName);
        if (object != null) {
            return object.toString();
        }
        return null;
    }


    private long getEventDate(DateTime eventDate) {
        if (eventDate == null) {
            return new Date().getTime();
        } else {
            return eventDate.getMillis();
        }
    }

    private List<Field> getFields(Class clazz) {
        List<Field> fields = new ArrayList<>();
        if (instance == null) {
            return new ArrayList<>();
        }


        Class current = clazz;
        while (current != null) { // we don't want to process Object.class
            // do something with current's fields
            Field[] fieldArray = current.getDeclaredFields();
            if (fieldArray != null) {
                fields.addAll(Arrays.asList(fieldArray));
            }

            current = current.getSuperclass();
        }

        return fields;
    }

    private Field getField(Class clazz, String fieldName) {
        if (clazz == null || StringUtils.isBlank(fieldName)) {
            return null;
        }

        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // No need to log this, log will be to big
        }
        if (field != null) {
            return field;
        }

        return getField(clazz.getSuperclass(), fieldName);
    }

    /**
     * Update the fts table with the provided values. This overloaded method adds the parameter entityType to uniquely distiguish the client types being processed
     *
     * @param tableName     the case table
     * @param entityType    the client type or bind type
     * @param entityId      the entity identifier
     * @param contentValues the fields to update and corresponding values
     */
    public void updateFTSsearch(String tableName, String entityType, String entityId, ContentValues contentValues) {
        updateFTSsearch(tableName, entityId, contentValues);
    }

    public void updateFTSsearch(String tableName, String entityId, ContentValues contentValues) {
        Timber.d("Starting updateFTSsearch table: " + tableName);
        AllCommonsRepository allCommonsRepository = org.smartregister.CoreLibrary.getInstance().context().
                allCommonsRepositoryobjects(tableName);

        if (allCommonsRepository != null) {
            allCommonsRepository.updateSearch(entityId);
            updateRegisterCount(entityId);
        }

        Timber.d("Finished updateFTSsearch table: " + tableName);
    }

    protected void updateRegisterCount(String entityId) {
        FORM_SUBMITTED.notifyListeners(entityId);
    }

    /**
     * Update given OPENMRS identifier, removes hyphen
     *
     * @param values
     */
    private void updateIdentifier(ContentValues values) {
        try {
            for (String identifier : getOpenmrsGenIds()) {
                Object value = values.get(identifier); //TODO
                if (value != null) {
                    String sValue = value.toString();
                    if (value instanceof String && StringUtils.isNotBlank(sValue)) {
                        values.remove(identifier);
                        values.put(identifier, sValue.replace("-", ""));
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public Context getContext() {
        return mContext;
    }

    protected String[] getOpenmrsGenIds() {
        return openmrsGenIds;
    }

    protected void addMiniProcessors(MiniClientProcessorForJava... miniClientProcessorsForJava) {
        for (MiniClientProcessorForJava miniClientProcessorForJava : miniClientProcessorsForJava) {
            unsyncEventsPerProcessor.put(miniClientProcessorForJava, new ArrayList<Event>());

            HashSet<String> eventTypes = miniClientProcessorForJava.getEventTypes();

            for (String eventType : eventTypes) {
                processorMap.put(eventType, miniClientProcessorForJava);
            }
        }
    }

    protected void processEventUsingMiniProcessor(@NonNull ClientClassification clientClassification, @NonNull EventClient eventClient, @NonNull String eventType) throws Exception {
        MiniClientProcessorForJava miniClientProcessorForJava = processorMap.get(eventType);
        if (miniClientProcessorForJava != null) {
            List<Event> processorUnsyncEvents = unsyncEventsPerProcessor.get(miniClientProcessorForJava);
            if (processorUnsyncEvents == null) {
                processorUnsyncEvents = new ArrayList<>();
                unsyncEventsPerProcessor.put(miniClientProcessorForJava, processorUnsyncEvents);
            }

            completeProcessing(eventClient.getEvent());
            miniClientProcessorForJava.processEventClient(eventClient, processorUnsyncEvents, clientClassification);
        }
    }
}
