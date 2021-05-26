package org.smartregister.util;

import android.content.Context;
import android.content.Intent;
import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormAttributeParser;
import org.smartregister.clientandeventmodel.FormData;
import org.smartregister.clientandeventmodel.FormEntityConverter;
import org.smartregister.clientandeventmodel.FormField;
import org.smartregister.clientandeventmodel.FormInstance;
import org.smartregister.clientandeventmodel.SubFormData;
import org.smartregister.domain.SyncStatus;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.domain.form.SubForm;
import org.smartregister.service.intentservices.ReplicationIntentService;
import org.smartregister.sync.CloudantDataHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import timber.log.Timber;

/**
 * Created by koros on 9/28/15.
 */
public class FormUtils {

    public static final String TAG = "FormUtils";
    public static final String ecClientRelationships = "ec_client_relationships.json";
    private static final String shouldLoadValueKey = "shouldLoadValue";
    private static final String relationalIdKey = "relational_id";
    private static final String databaseIdKey = "_id";
    private static final String injectedBaseEntityIdKey = "injectedBaseEntityId";
    private static FormUtils instance;
    private Context mContext;
    private org.smartregister.Context theAppContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
    private Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    private FormEntityConverter formEntityConverter;
    private CloudantDataHandler mCloudantDataHandler;

    public FormUtils(Context context) throws Exception {
        mContext = context;
        theAppContext = CoreLibrary.getInstance().context();
        FormAttributeParser formAttributeParser = new FormAttributeParser(context);
        formEntityConverter = new FormEntityConverter(formAttributeParser, mContext);
        // Protect creation of static variable.
        mCloudantDataHandler = CloudantDataHandler.getInstance(context.getApplicationContext());
    }

    public static FormUtils getInstance(Context ctx) throws Exception {
        if (instance == null)
            instance = new FormUtils(ctx);

        if (ctx != null && instance.mContext != ctx)
            instance.mContext = ctx;

        return instance;
    }

    /* Checks if the provided node has Child elements
     * @param element
     * @return
     */
    public static boolean hasChildElements(Node element) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }

        return false;
    }

    private static JSONObject retrieveRelationshipJsonForLink(String link, JSONArray array)
            throws Exception {
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (relationShipExist(link, object)) {
                System.out.println("Relationship found ##");

                return object;
            }
        }

        return null;
    }

    private static boolean relationShipExist(String link, JSONObject json) {
        try {
            String[] path = link.split("\\.");
            String parentTable = path[0];
            String childTable = path[1];

            String jsonParentTableString = json.getString("parent");
            String jsonChildTableString = json.getString("child");

            boolean parentToChildExist =
                    jsonParentTableString.equals(parentTable) && jsonChildTableString
                            .equals(childTable);
            boolean childToParentExist =
                    jsonParentTableString.equals(childTable) && jsonChildTableString
                            .equals(parentTable);

            if (parentToChildExist || childToParentExist) {
                return true;
            }

        } catch (Exception e) {
            Timber.e(e);
        }

        return false;
    }

    public static int getIndexForFormName(String formName, String[] formNames) {
        for (int i = 0; i < formNames.length; i++) {
            if (formName.equalsIgnoreCase(formNames[i])) {
                return i;
            }
        }

        return -1;
    }

    public FormSubmission generateFormSubmisionFromXMLString(String entity_id, String formData,
                                                             String formName, JSONObject
                                                                     overrides) throws Exception {
        JSONObject formSubmission = XML.toJSONObject(formData);

        //FileUtilities fu = new FileUtilities();
        //fu.write("xmlform.txt", formData);
        //fu.write("xmlformsubmission.txt", formSubmission.toString());
        System.out.println(formSubmission);

        // use the form_definition.json to iterate through fields
        String formDefinitionJson = readFileFromAssetsFolder(
                "www/form/" + formName + "/form_definition.json");
        JSONObject formDefinition = new JSONObject(formDefinitionJson);

        String rootNodeKey = formSubmission.keys().next();

        // retrieve the id, if it fails use the provided value by the param
        entity_id = formSubmission.getJSONObject(rootNodeKey).has(databaseIdKey) ? formSubmission
                .getJSONObject(rootNodeKey).getString(databaseIdKey) : generateRandomUUIDString();

        //String bindPath = formDefinition.getJSONObject("form").getString("bind_type");
        JSONObject fieldsDefinition = formDefinition.getJSONObject("form");
        JSONArray populatedFieldsArray = getPopulatedFieldsForArray(fieldsDefinition, entity_id,
                formSubmission, overrides);

        // replace all the fields in the form
        formDefinition.getJSONObject("form").put("fields", populatedFieldsArray);

        //get the subforms
        if (formDefinition.getJSONObject("form").has("sub_forms")) {
            JSONObject subFormDefinition = formDefinition.getJSONObject("form").
                    getJSONArray("sub_forms").getJSONObject(0);
            // get the bind path for the sub-form, helps us to locate the node that holds the data
            // in the corresponding data json
            String bindPath = subFormDefinition.getString("default_bind_path");

            // get the actual sub-form data
            JSONArray subFormDataArray = new JSONArray();
            Object subFormDataObject = getObjectAtPath(bindPath.split("/"), formSubmission);

            if (subFormDataObject instanceof JSONObject) {
                JSONObject subFormData = (JSONObject) subFormDataObject;
                subFormDataArray.put(0, subFormData);
            } else if (subFormDataObject instanceof JSONArray) {
                subFormDataArray = (JSONArray) subFormDataObject;
            }

            JSONArray subForms = getSubForms(subFormDataArray, entity_id, subFormDefinition,
                    overrides);

            // replace the subforms field with real data
            formDefinition.getJSONObject("form").put("sub_forms", subForms);
            //throw new Exception();
        }

        String instanceId = generateRandomUUIDString();
        String entityId = retrieveIdForSubmission(formDefinition);
        String formDefinitionVersionString = formDefinition
                .getString("form_data_definition_version");

        String clientVersion = String.valueOf(new Date().getTime());
        String instance = formDefinition.toString();
        FormSubmission fs = new FormSubmission(instanceId, entityId, formName, instance,
                clientVersion, SyncStatus.PENDING, formDefinitionVersionString);

        generateClientAndEventModelsForFormSubmission(fs, formName);

        return fs;
    }

    private void generateClientAndEventModelsForFormSubmission(FormSubmission formSubmission,
                                                               String formName) {
        org.smartregister.clientandeventmodel.FormSubmission v2FormSubmission;

        String anmId = CoreLibrary.getInstance().context().anmService().fetchDetails().name();
        String instanceId = formSubmission.instanceId();
        String entityId = formSubmission.entityId();
        Long clientVersion = new Date().getTime();
        String formDataDefinitionVersion = formSubmission.formDataDefinitionVersion();

        String bind_type = formSubmission.getFormInstance().getForm().getBind_type();
        String default_bind_path = formSubmission.getFormInstance().getForm()
                .getDefault_bind_path();

        List<FormField> fields = convertFormFields(formSubmission.getFormInstance().getForm().
                getFields());

        List<SubFormData> sub_forms = getSubFormList(formSubmission);
        FormData formData = new FormData(bind_type, default_bind_path, fields, sub_forms);

        FormInstance formInstance = new FormInstance(formData);
        formInstance.setForm_data_definition_version(formDataDefinitionVersion);

        v2FormSubmission = new org.smartregister.clientandeventmodel.FormSubmission(anmId,
                instanceId, formName, entityId, clientVersion, formDataDefinitionVersion,
                formInstance, clientVersion);

        // retrieve client and events
        Client c = formEntityConverter.getClientFromFormSubmission(v2FormSubmission);
        printClient(c);
        Event e = formEntityConverter.getEventFromFormSubmission(v2FormSubmission);
        printEvent(e);
        org.smartregister.cloudant.models.Event event = new org.smartregister.cloudant.models.Event(
                e);
        createNewEventDocument(event);
        if (c != null) {
            org.smartregister.cloudant.models.Client client = new org.smartregister.cloudant
                    .models.Client(
                    c);
            createNewClientDocument(client);
        }

        Map<String, Map<String, Object>> dep = formEntityConverter.
                getDependentClientsFromFormSubmission(v2FormSubmission);
        for (Map<String, Object> cm : dep.values()) {
            Client cin = (Client) cm.get("client");
            Event evin = (Event) cm.get("event");
            event = new org.smartregister.cloudant.models.Event(evin);
            createNewEventDocument(event);

            if (cin != null) {
                org.smartregister.cloudant.models.Client client = new org.smartregister.cloudant
                        .models.Client(
                        cin);
                createNewClientDocument(client);
                printClient(cin);
            }
            printEvent(evin);

        }

        startReplicationIntentService();
    }

    private void printClient(Client client) {
        Timber.d("============== CLIENT ================");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        String clientJson = gson.toJson(client);
        Timber.d(clientJson);
        Timber.d("====================================");

    }

    private void printEvent(Event event) {
        Timber.d("============== EVENT ================");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        String eventJson = gson.toJson(event);
        Timber.d(eventJson);
        Timber.d("====================================");
    }

    /**
     * Start ReplicationIntentService which handles cloudant sync processes
     */
    private void startReplicationIntentService() {

        Intent serviceIntent = new Intent(mContext, ReplicationIntentService.class);
        mContext.startService(serviceIntent);
    }

    private List<SubFormData> getSubFormList(FormSubmission formSubmission) {
        List<SubFormData> sub_forms = new ArrayList<SubFormData>();
        List<SubForm> subForms = formSubmission.getFormInstance().getForm().getSub_forms();
        if (subForms != null) {
            for (SubForm sf : subForms) {
                SubFormData sd = new SubFormData();
                sd.setDefault_bind_path(sf.getDefaultBindPath());
                sd.setBind_type(sf.getBindType());

                List<FormField> subFormFields = convertFormFields(sf.getFields());
                sd.setFields(subFormFields);

                sd.setInstances(sf.getInstances());
                sd.setName(sf.getName());
                sub_forms.add(sd);
            }
        }

        return sub_forms;
    }

    private List<FormField> convertFormFields(List<org.smartregister.domain.form.FormField>
                                                      formFields) {
        List<FormField> fields = new ArrayList<FormField>();
        for (org.smartregister.domain.form.FormField ff : formFields) {
            FormField f = new FormField(ff.getName(), ff.getValue(), ff.getSource());
            fields.add(f);
        }

        return fields;
    }

    private JSONArray getSubForms(JSONArray subFormDataArray, String entity_id, JSONObject
            subFormDefinition, JSONObject overrides) throws Exception {
        JSONArray subForms = new JSONArray();

        JSONArray subFormFields = getFieldsArrayForSubFormDefinition(subFormDefinition);
        JSONArray subFormInstances = new JSONArray();

        // the id of each subform is contained in the attribute of the enclosing element
        for (int i = 0; i < subFormDataArray.length(); i++) {
            JSONObject subFormData = subFormDataArray.getJSONObject(i);
            String relationalId =
                    subFormData.has(relationalIdKey) ? subFormData.getString(relationalIdKey)
                            : entity_id;
            String id = subFormData.has(databaseIdKey) ? subFormData.getString(databaseIdKey)
                    : generateRandomUUIDString();
            JSONObject subFormInstance = getFieldValuesForSubFormDefinition(subFormDefinition,
                    relationalId, id, subFormData, overrides);
            subFormInstances.put(i, subFormInstance);
        }

        subFormDefinition.put("instances", subFormInstances);
        subFormDefinition.put("fields", subFormFields);
        subForms.put(0, subFormDefinition);

        return subForms;
    }

    public String generateXMLInputForFormWithEntityId(String entityId, String formName, String
            overrides) {
        try {
            // get the field overrides map
            JSONObject fieldOverrides = new JSONObject();
            if (overrides != null) {
                fieldOverrides = new JSONObject(overrides);
                String overridesStr = fieldOverrides.getString("fieldOverrides");
                fieldOverrides = new JSONObject(overridesStr);
            }

            // use the form_definition.json to get the form mappings
            String formDefinitionJson = readFileFromAssetsFolder(
                    "www/form/" + formName + "/form_definition.json");
            JSONObject formDefinition = new JSONObject(formDefinitionJson);
            String ec_bind_path = formDefinition.getJSONObject("form").getString("ec_bind_type");

            String sql =
                    "select * from " + ec_bind_path + " where base_entity_id =?";
            Map<String, String> dbEntity = theAppContext.formDataRepository().
                    getMapFromSQLQuery(sql, new String[]{entityId});
            Map<String, String> detailsMap = theAppContext.detailsRepository().
                    getAllDetailsForClient(entityId);
            detailsMap.putAll(dbEntity);

            JSONObject entityJson = new JSONObject();
            if (detailsMap != null && !detailsMap.isEmpty()) {
                entityJson = new JSONObject(detailsMap);
            }

            //read the xml form model, the expected form model that is passed to the form mirrors it
            String formModelString = readFileFromAssetsFolder(
                    "www/form/" + formName + "/model" + ".xml").replaceAll("\n", " ")
                    .replaceAll("\r", " ");
            InputStream is = new ByteArrayInputStream(formModelString.getBytes());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(is);

            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            serializer.setOutput(writer);
            serializer.startDocument(CharEncoding.UTF_8, true);

            //skip processing <model><instance>
            NodeList els = ((Element) document.getElementsByTagName("model").item(0)).
                    getElementsByTagName("instance");
            Element el = (Element) els.item(0);
            NodeList entries = el.getChildNodes();
            int num = entries.getLength();
            for (int i = 0; i < num; i++) {
                Node n = entries.item(i);
                if (n instanceof Element) {
                    Element node = (Element) n;
                    writeXML(node, serializer, fieldOverrides, formDefinition, entityJson, null);
                }
            }

            serializer.endDocument();

            String xml = writer.toString();
            // Add model and instance tags
            xml = xml.substring(56);
            System.out.println(xml);

            return xml;

        } catch (Exception e) {
            Timber.e(e);
        }
        return "";
    }

    private void writeXML(Element node, XmlSerializer serializer, JSONObject fieldOverrides,
                          JSONObject formDefinition, JSONObject entityJson, String parentId) {
        try {
            String nodeName = node.getNodeName();
            String entityId =
                    entityJson.has("id") ? entityJson.getString("id") : generateRandomUUIDString();
            String relationalId =
                    entityJson.has(relationalIdKey) ? entityJson.getString(relationalIdKey)
                            : parentId;

            serializer.startTag("", nodeName);

            // write the xml attributes
            writeXMLAttributes(node, serializer, entityId, relationalId);

            String nodeValue = retrieveValueForNodeName(nodeName, entityJson, formDefinition);
            //overwrite the node value with contents from overrides map
            if (fieldOverrides.has(nodeName)) {
                nodeValue = fieldOverrides.getString(nodeName);
            }
            //write the node value
            if (nodeValue != null) {
                serializer.text(nodeValue);
            }

            List<String> subFormNames = getSubFormNames(formDefinition);

            // get all child nodes
            NodeList entries = node.getChildNodes();
            int num = entries.getLength();
            for (int i = 0; i < num; i++) {
                if (entries.item(i) instanceof Element) {
                    Element child = (Element) entries.item(i);
                    String fieldName = child.getNodeName();

                    // its a subform element process it
                    if (!subFormNames.isEmpty() && subFormNames.contains(fieldName)) {
                        // its a subform element process it
                        // get the subform definition
                        JSONArray subForms = formDefinition.getJSONObject("form").
                                getJSONArray("sub_forms");
                        JSONObject subFormDefinition = retriveSubformDefinitionForBindPath(subForms,
                                fieldName);
                        if (subFormDefinition != null) {

                            String childTableName = subFormDefinition.getString("ec_bind_type");
                            String sql = "select * from " + childTableName + " where relational_id = ?";
                            String childRecordsString = theAppContext.formDataRepository().
                                    queryList(sql, new String[]{relationalId});
                            JSONArray childRecords = new JSONArray(childRecordsString);

                            JSONArray fieldsArray = subFormDefinition.getJSONArray("fields");
                            // check whether we are supposed to load the id of the child record
                            JSONObject idFieldDefn = getJsonFieldFromArray("id", fieldsArray);

                            // definition for id
                            boolean shouldLoadId =
                                    idFieldDefn.has(shouldLoadValueKey) && idFieldDefn
                                            .getBoolean(shouldLoadValueKey);

                            if (shouldLoadId && childRecords.length() > 0) {
                                for (int k = 0; k < childRecords.length(); k++) {
                                    JSONObject childEntityJson = childRecords.getJSONObject(k);
                                    JSONObject obj = getCombinedJsonObjectForObject(
                                            childEntityJson);
                                    writeXML(child, serializer, fieldOverrides, subFormDefinition,
                                            childEntityJson, entityId);
                                }

                            }
                        }
                    } else {
                        // it's not a sub-form element write its value
                        serializer.startTag("", fieldName);
                        // write the xml attributes
                        // a value node doesn't have id or relationalId fields
                        writeXMLAttributes(child, serializer, null, null);
                        // write the node value
                        String value = retrieveValueForNodeName(fieldName, entityJson,
                                formDefinition);
                        // write the node value
                        if (value != null) {
                            serializer.text(value);
                        }

                        // overwrite the node value with contents from overrides map
                        if (fieldOverrides.has(fieldName)) {
                            serializer.text(fieldOverrides.getString(fieldName));
                        }

                        serializer.endTag("", fieldName);
                    }
                }
            }

            serializer.endTag("", node.getNodeName());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve additional details for this record from the details Table.
     *
     * @param entityJson
     * @return
     */
    private JSONObject getCombinedJsonObjectForObject(JSONObject entityJson) {
        try {
            String baseEntityId = entityJson.getString("base_entity_id");
            Map<String, String> map = theAppContext.detailsRepository().
                    getAllDetailsForClient(baseEntityId);

            for (String key : map.keySet()) {
                if (!entityJson.has(key)) {
                    entityJson.put(key, map.get(key));
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return entityJson;
    }

    /**
     * Iterate through the provided array and retrieve a json object whose name attribute matches
     * the name supplied
     *
     * @param nameValue
     * @param array
     * @return
     */
    private JSONObject getJsonFieldFromArray(String nameValue, JSONArray array) {
        try {
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject field = array.getJSONObject(i);
                    String name = field.has("name") ? field.getString("name") : null;
                    if (nameValue.equals(name)) {
                        return field;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    /**
     * retrieves node value for cases which the nodename don't match the name of the xml element
     *
     * @param nodeName
     * @param entityJson
     * @param formDefinition
     * @return
     */
    private String retrieveValueForNodeName(String nodeName, JSONObject entityJson, JSONObject
            formDefinition) {
        try {
            if (entityJson != null && entityJson.length() > 0) {
                JSONObject fieldsObject =
                        formDefinition.has("form") ? formDefinition.getJSONObject("form")
                                : formDefinition.has("sub_forms") ? formDefinition
                                .getJSONObject("sub_forms") : formDefinition;
                if (fieldsObject.has("fields")) {
                    JSONArray fields = fieldsObject.getJSONArray("fields");
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject field = fields.getJSONObject(i);
                        String bindPath = field.has("bind") ? field.getString("bind") : null;
                        String name = field.has("name") ? field.getString("name") : null;

                        boolean matchingNodeFound =
                                bindPath != null && name != null && bindPath.endsWith(nodeName)
                                        || name != null && name.equals(nodeName);

                        if (matchingNodeFound) {
                            if (field.has("shouldLoadValue") && field
                                    .getBoolean("shouldLoadValue")) {
                                String keyName = entityJson.has(nodeName) ? nodeName : name;
                                if (entityJson.has(keyName)) {
                                    return entityJson.getString(keyName);
                                } else {
                                    return "";
                                }
                            } else {
                                // the shouldLoadValue flag isn't set
                                return "";
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return "";
    }

    /**
     * Currently not used but, the method should retrieve the path of a given node,
     * useful when confirming if the current node has been properly mapped to its bind_path
     **/
    private String getXPath(Node node) {
        Node parent = node.getParentNode();
        if (parent == null) {
            return "/" + node.getNodeName();
        }

        return getXPath(parent) + "/";
    }

    private List<String> getSubFormNames(JSONObject formDefinition) throws Exception {
        List<String> subFormNames = new ArrayList<String>();
        if (formDefinition.has("form") && formDefinition.getJSONObject("form").has("sub_forms")) {
            JSONArray subForms = formDefinition.getJSONObject("form").getJSONArray("sub_forms");
            for (int i = 0; i < subForms.length(); i++) {
                JSONObject subForm = subForms.getJSONObject(i);
                String subFormNameStr = subForm.getString("default_bind_path");
                String[] path = subFormNameStr.split("/");
                String subFormName = path[path.length - 1]; // the last token
                subFormNames.add(subFormName);
            }
        }

        return subFormNames;
    }

    private JSONObject retriveSubformDefinitionForBindPath(JSONArray subForms, String fieldName)
            throws Exception {
        for (int i = 0; i < subForms.length(); i++) {
            JSONObject subForm = subForms.getJSONObject(i);
            String subFormNameStr = subForm.getString("default_bind_path");
            String[] path = subFormNameStr.split("/");
            String subFormName = path[path.length - 1]; // the last token

            if (fieldName.equalsIgnoreCase(subFormName)) {
                return subForm;
            }
        }

        return null;
    }

    private void writeXMLAttributes(Element node, XmlSerializer serializer, String id, String
            relationalId) {
        try {
            // get a map containing the attributes of this node
            NamedNodeMap attributes = node.getAttributes();

            // get the number of nodes in this map
            int numAttrs = attributes.getLength();

            if (id != null) {
                serializer.attribute("", databaseIdKey, id);
            }

            if (relationalId != null) {
                serializer.attribute("", relationalIdKey, relationalId);
            }

            for (int i = 0; i < numAttrs; i++) {
                Attr attr = (Attr) attributes.item(i);
                String attrName = attr.getNodeName();
                String attrValue = attr.getNodeValue();
                serializer.attribute("", attrName, attrValue);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }

    private String retrieveIdForSubmission(JSONObject jsonObject) throws Exception {
        JSONArray fields = jsonObject.getJSONObject("form").getJSONArray("fields");

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);

            if (field.has("name") && field.getString("name").equalsIgnoreCase("id")) {
                return field.getString("value");
            }
        }
        return null;
    }

    public Object getObjectAtPath(String[] path, JSONObject jsonObject) throws Exception {
        JSONObject object = jsonObject;
        int i = 0;
        while (i < path.length - 1) {
            if (object.has(path[i])) {
                Object o = object.get(path[i]);
                if (o instanceof JSONObject) {
                    object = object.getJSONObject(path[i]);
                } else if (o instanceof JSONArray) {
                    object = object.getJSONArray(path[i]).getJSONObject(0);
                }
            }
            i++;
        }
        return object.has(path[i]) ? object.get(path[i]) : null;
    }

    public JSONArray getPopulatedFieldsForArray(JSONObject fieldsDefinition, String entityId,
                                                JSONObject jsonObject, JSONObject overrides)
            throws Exception {
        String bindPath = fieldsDefinition.getString("bind_type");
        String sql = "select * from " + bindPath + " where id=?";
        String dbEntity = theAppContext.formDataRepository().queryUniqueResult(sql, new String[]{entityId});

        JSONObject entityJson = new JSONObject();

        if (dbEntity != null && !dbEntity.isEmpty()) {
            entityJson = new JSONObject(dbEntity);
        }

        JSONArray fieldsArray = fieldsDefinition.getJSONArray("fields");

        for (int i = 0; i < fieldsArray.length(); i++) {
            JSONObject item = fieldsArray.getJSONObject(i);

            if (!item.has("name")) {
                continue; // skip elements without name
            }

            String itemName = item.getString("name");
            boolean shouldLoadValue =
                    item.has("shouldLoadValue") && item.getBoolean("shouldLoadValue");

            if (item.has("bind")) {
                String pathSting = item.getString("bind");
                pathSting = pathSting.startsWith("/") ? pathSting.substring(1) : pathSting;
                String[] path = pathSting.split("/");
                String value = getValueForPath(path, jsonObject);
                item.put("value", value);
            }

            if (shouldLoadValue && overrides.has(item.getString("name"))) {
                // if the value is not set use the value in the overrides filed
                if (!item.has("value")) {
                    item.put("value", overrides.getString(item.getString("name")));
                }
            }

            // map the id field for child elements
            if (isForeignIdPath(item)) {
                String value = null;
                if (entityJson.length() > 0 && shouldLoadValue) {
                    //retrieve the child attributes
                    value = retrieveValueForLinkedRecord(item.getString("source"), entityJson);
                }

                // generate uuid if its still not available
                if (item.getString("source").endsWith(".id") && value == null) {
                    value = generateRandomUUIDString();
                }

                if (value != null && !item.has("value")) {
                    item.put("value", value);
                }
            }

            // add source property if not available
            if (!item.has("source")) {
                item.put("source", bindPath + "." + item.getString("name"));
            }

            if (itemName.equalsIgnoreCase("id") && !isForeignIdPath(item)) {
                assert entityId != null;
                item.put("value", entityId);
            }

            if (itemName.equalsIgnoreCase(injectedBaseEntityIdKey)) {
                assert entityId != null;
                item.put("value", entityId);
            }

            if (itemName.equalsIgnoreCase("start") || itemName.equalsIgnoreCase("end")) {
                try {
                    boolean isEndTime = itemName.equalsIgnoreCase("end");
                    String val =
                            item.has("value") ? item.getString("value") : sdf.format(new Date());

                    if (isEndTime) {
                        val = formatter.format(new Date());
                    } else {
                        Date d = sdf.parse(val);
                        //parse the date to match OpenMRS format
                        val = formatter.format(d);
                    }

                    item.put("value", val);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
        return fieldsArray;
    }

    private boolean isForeignIdPath(JSONObject item) throws Exception {
        // e.g ibu.anak.id
        return item.has("source") && item.getString("source").split("\\.").length > 2;
    }

    public String retrieveValueForLinkedRecord(String link, JSONObject entityJson) {
        try {
            String entityRelationships = readFileFromAssetsFolder(
                    "www/form/entity_relationship" + AllConstants.JSON_FILE_EXTENSION);
            JSONArray json = new JSONArray(entityRelationships);
            Timber.i(json.toString());

            JSONObject rJson;

            if ((rJson = retrieveRelationshipJsonForLink(link, json)) != null) {
                String[] path = link.split("\\.");
                String parentTable = path[0];
                String childTable = path[1];

                String joinValueKey =
                        parentTable.equals(rJson.getString("parent")) ? rJson.getString("from")
                                : rJson.getString("to");
                joinValueKey = joinValueKey.contains(".") ? joinValueKey
                        .substring(joinValueKey.lastIndexOf(".") + 1) : joinValueKey;

                String val = entityJson.getString(joinValueKey);

                String joinField =
                        parentTable.equals(rJson.getString("parent")) ? rJson.getString("to")
                                : rJson.getString("from");
                String sql =
                        "select * from " + childTable + " where " + joinField + "=?";
                Timber.d(sql);
                String dbEntity = theAppContext.formDataRepository().queryUniqueResult(sql, new String[]{val});
                JSONObject linkedEntityJson = new JSONObject();

                if (dbEntity != null && !dbEntity.isEmpty()) {
                    linkedEntityJson = new JSONObject(dbEntity);
                }

                // finally retrieve the value from the child entity, need to improve or remove
                // entirely these hacks
                String sourceKey = link.substring(link.lastIndexOf(".") + 1);

                if (linkedEntityJson.has(sourceKey)) {
                    return linkedEntityJson.getString(sourceKey);
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    public JSONArray getFieldsArrayForSubFormDefinition(JSONObject fieldsDefinition) throws
            Exception {
        JSONArray fieldsArray = fieldsDefinition.getJSONArray("fields");
        String bindPath = fieldsDefinition.getString("bind_type");

        JSONArray subFormFieldsArray = new JSONArray();

        for (int i = 0; i < fieldsArray.length(); i++) {
            JSONObject field = new JSONObject();
            JSONObject item = fieldsArray.getJSONObject(i);

            if (!item.has("name")) {
                continue; // skip elements without name
            }

            field.put("name", item.getString("name"));

            if (!item.has("source")) {
                field.put("source", bindPath + "." + item.getString("name"));
            } else {
                field.put("source", bindPath + "." + item.getString("source"));
            }

            subFormFieldsArray.put(i, field);
        }

        return subFormFieldsArray;
    }

    public JSONObject getFieldValuesForSubFormDefinition(JSONObject fieldsDefinition, String
            relationalId, String entityId, JSONObject jsonObject, JSONObject overrides) throws
            Exception {

        JSONArray fieldsArray = fieldsDefinition.getJSONArray("fields");

        JSONObject fieldsValues = new JSONObject();

        for (int i = 0; i < fieldsArray.length(); i++) {
            JSONObject item = fieldsArray.getJSONObject(i);

            if (!item.has("name")) {
                continue; // skip elements without name
            }

            if (item.has("bind")) {
                String pathSting = item.getString("bind");
                pathSting = pathSting.startsWith("/") ? pathSting.substring(1) : pathSting;
                String[] path = pathSting.split("/");

                //check if we need to override this val
                if (overrides.has(item.getString("name"))) {
                    fieldsValues.put(item.getString("name"),
                            overrides.getString(item.getString("name")));
                } else {
                    String value = getValueForPath(path, jsonObject);
                    fieldsValues.put(item.getString("name"), value);
                }
            }

            //TODO: generate the id for the record
            if (item.has("name") && item.getString("name").equalsIgnoreCase("id")) {
                String id = entityId != null ? entityId : generateRandomUUIDString();
                fieldsValues.put(item.getString("name"), id);
            }

            //TODO: generate the relational for the record
            if (item.has("name") && item.getString("name").equalsIgnoreCase(relationalIdKey)) {
                fieldsValues.put(item.getString("name"), relationalId);
            }

            //TODO: generate the injectedBaseEntityIdKey for the record
            if (item.has("name") && item.getString("name")
                    .equalsIgnoreCase(injectedBaseEntityIdKey)) {
                fieldsValues.put(item.getString("name"), relationalId);
            }

            populateRelationField(item, fieldsValues, relationalId);
        }
        return fieldsValues;
    }

    private String getECClientRelationships() {
        return AssetHandler.readFileFromAssetsFolder(ecClientRelationships, mContext);
    }

    /**
     * see if the current field is a client_relationship field, if so set it's value to the
     * relationId since that's the parent base_entity_id
     *
     * @param fieldItem
     * @param fieldsValues
     * @param relationalId
     */
    private void populateRelationField(JSONObject fieldItem, JSONObject fieldsValues, String
            relationalId) {
        try {
            if (fieldItem.has("name")) {
                String fieldName = fieldItem.getString("name");
                String relationships = getECClientRelationships();
                JSONArray jsonArray = new JSONArray(relationships);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject rObject = jsonArray.getJSONObject(i);
                    if (rObject.has("field") && rObject.getString("field")
                            .equalsIgnoreCase(fieldName)) {
                        fieldsValues.put(fieldName, relationalId);
                    }
                }
            }

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    public String getValueForPath(String[] path, JSONObject jsonObject) throws Exception {
        JSONObject object = jsonObject;
        String value = null;
        int i = 0;

        while (i < path.length - 1) {
            if (object.has(path[i])) {
                Object o = object.get(path[i]);
                if (o instanceof JSONObject) {
                    object = object.getJSONObject(path[i]);
                } else if (o instanceof JSONArray) {
                    object = object.getJSONArray(path[i]).getJSONObject(0);
                }
            }

            i++;
        }
        Object valueObject = object.has(path[i]) ? object.get(path[i]) : null;

        if (valueObject == null) {
            return value;
        }
        if (valueObject instanceof JSONObject && ((JSONObject) valueObject).has("content")) {
            value = ((JSONObject) object.get(path[i])).getString("content");
        } else if (valueObject instanceof JSONArray) {
            value = ((JSONArray) valueObject).get(0).toString();
        } else if (!(valueObject instanceof JSONObject)) {
            value = valueObject.toString();
        }

        return value;
    }

    private String readFileFromAssetsFolder(String fileName) throws IOException {
        String fileContents = null;
        InputStream inputStream = null;
        try {

            inputStream = mContext.getAssets().open(fileName);
            fileContents = IOUtils.toString(inputStream);

        } catch (IOException e) {
            Timber.e(e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return fileContents;
    }

    public JSONObject getFormJson(String formIdentity) {
        if (mContext != null) {

            InputStream inputStream = null;
            try {
                String locale = mContext.getResources().getConfiguration().locale.getLanguage();
                locale = locale.equalsIgnoreCase(Locale.ENGLISH.getLanguage()) ? "" : "-" + locale;
                try {
                    inputStream = mContext.getApplicationContext().getAssets()
                            .open("json.form" + locale + "/" + formIdentity + AllConstants.JSON_FILE_EXTENSION);
                } catch (FileNotFoundException e) {
                    // file for the language not found, defaulting to english language
                    inputStream = mContext.getApplicationContext().getAssets()
                            .open("json.form/" + formIdentity + AllConstants.JSON_FILE_EXTENSION);
                }
                String rawForm = IOUtils.toString(inputStream);
                return new JSONObject(rawForm);
            } catch (IOException | JSONException e) {
                Timber.e(e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Timber.e(e);
                    }
                }
            }
        }
        return null;
    }

    private void createNewEventDocument(org.smartregister.cloudant.models.Event event) {
        mCloudantDataHandler.createEventDocument(event);
    }

    private void createNewClientDocument(org.smartregister.cloudant.models.Client client) {
        mCloudantDataHandler.createClientDocument(client);
    }

}