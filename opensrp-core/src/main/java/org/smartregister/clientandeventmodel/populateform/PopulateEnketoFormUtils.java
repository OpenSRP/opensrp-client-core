package org.smartregister.clientandeventmodel.populateform;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.Years;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.repository.EventClientRepository;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import timber.log.Timber;

import static org.smartregister.clientandeventmodel.FormEntityConstants.FieldType.person;
import static org.smartregister.clientandeventmodel.FormEntityConstants.FieldType.person_address;
import static org.smartregister.clientandeventmodel.FormEntityConstants.FieldType.person_attribute;
import static org.smartregister.clientandeventmodel.FormEntityConstants.FieldType.person_identifier;
import static org.smartregister.clientandeventmodel.FormEntityConstants.Person;
import static org.smartregister.util.JsonFormUtils.gson;

/**
 * Created by samuelgithengi on 1/19/18.
 */

public class PopulateEnketoFormUtils {

    private static PopulateEnketoFormUtils instance;

    private static List<String> personProperties = new ArrayList<>();
    private static List<String> addressProperties = new ArrayList<>();

    private EventClientRepository eventClientRepository;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String assetsPath = "www/form/";

    private Context context;

    public static PopulateEnketoFormUtils getInstance(Context context, EventClientRepository eventClientRepository) {
        if (instance == null) {
            instance = new PopulateEnketoFormUtils(context, eventClientRepository);
            for (Person property : Person.values())
                personProperties.add(property.name());
            for (FormEntityConstants.Address property : FormEntityConstants.Address.values())
                addressProperties.add(property.name());
        }
        return instance;
    }

    private PopulateEnketoFormUtils(Context context, EventClientRepository eventClientRepository) {
        this.context = context;
        this.eventClientRepository = eventClientRepository;
    }

    public FieldOverrides populateFormOverrides(String baseEntityId, String formSubmissionId, String enketoForm) {
        Map fields = new HashMap();
        Gson gson = new GsonBuilder().create();
        String model = readFileAssets(assetsPath + enketoForm + "/model.xml");
        ModelXMLHandler modelXMLHandler = new ModelXMLHandler();
        List<Model> tags = parseXML(modelXMLHandler, model);
        JSONObject event;
        Client client = null;
        if (formSubmissionId != null) {
            event = eventClientRepository.getEventsByFormSubmissionId(formSubmissionId);
        } else {
            event = eventClientRepository.getEventsByBaseEntityIdAndEventType(baseEntityId, modelXMLHandler.getEventType());
        }
        if (event == null)
            return new FieldOverrides(new JSONObject().toString());
        Type listType = new TypeToken<List<Obs>>() {
        }.getType();
        try {
            List<Obs> obs = gson.fromJson(event.getJSONArray("obs").toString(), listType);
            Map<String, Obs> formObservations = new HashMap<>();
            for (Obs observation : obs)
                formObservations.put(observation.getFormSubmissionField(), observation);
            for (Model tag : tags) {
                Obs observation = formObservations.get(tag.getTag());
                if (observation != null) {
                    if (!observation.getHumanReadableValues().isEmpty())
                        fields.put(tag.getTag(), TextUtils.join(",", observation.getHumanReadableValues()));
                    else
                        fields.put(tag.getTag(), observation.getValue());
                } else if (tag.getOpenMRSEntity() != null) {
                    if (client == null && tag.getOpenMRSEntity().startsWith(person.name()))
                        client = fetchClient(baseEntityId);
                    if (tag.getOpenMRSEntity().equals(person.name())) {
                        fields.put(tag.getTag(), retrieveClientPropertyValue(client, tag));
                    } else if (tag.getOpenMRSEntity().equals(person_identifier.name())) {
                        fields.put(tag.getTag(), client.getIdentifier(tag.getOpenMRSEntityId()));
                    } else if (tag.getOpenMRSEntity().equals(person_attribute.name())) {
                        fields.put(tag.getTag(), client.getAttribute(tag.getOpenMRSEntityId()));
                    } else if (tag.getOpenMRSEntity().equals(person_address.name())) {
                        fields.put(tag.getTag(), retrieveAddressPropertyValue(client.getAddress(tag.getOpenMRSEntityParent()), tag));
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e, "populateFormOverrides: Error Parsing Json ");
        }
        JSONObject fieldOverridesJson = new JSONObject(fields);
        FieldOverrides fieldOverrides = new FieldOverrides(fieldOverridesJson.toString());
        return fieldOverrides;
    }

    private Client fetchClient(String baseEntityId) {
        JSONObject clientJSON = eventClientRepository.getClientByBaseEntityId(baseEntityId);
        return gson.fromJson(clientJSON.toString(), Client.class);
    }

    private String retrieveClientPropertyValue(Client client, Model tag) {
        if (tag.getTag().equalsIgnoreCase("age"))
            return Years.yearsBetween(new DateTime(client.getBirthdate()), DateTime.now()).getYears() + "";
        else if (!personProperties.contains(tag.getTag()))
            return null;
        Person personProperty = Person.valueOf(tag.getTag());
        switch (personProperty) {
            case first_name:
                return client.getFirstName();
            case middle_name:
                return client.getMiddleName();
            case last_name:
                return client.getLastName();
            case gender:
                return client.getGender();
            case birthdate:
                return dateFormat.format(client.getBirthdate());
            case birthdate_estimated:
                return client.getBirthdateApprox().toString();
            case deathdate:
                return dateFormat.format(client.getDeathdate());
            case deathdate_estimated:
                return client.getDeathdateApprox().toString();
            case client_type:
                return client.getClientType();
            default:
                return null;
        }
    }

    /**
     * Retrieve address property Value as populated by
     * org.smartregister.clientandeventmodel.FormEntityConverter.fillAddressFields
     */
    private String retrieveAddressPropertyValue(Address address, Model tag) {
        if (address == null)
            return null;
        else if (!addressProperties.contains(tag.getOpenMRSEntityId()))
            return address.getAddressField(tag.getOpenMRSEntityId());
        FormEntityConstants.Address addressProperty = FormEntityConstants.Address.valueOf(tag.getOpenMRSEntityId());
        switch (addressProperty) {
            case start_date:
            case startDate:
                return dateFormat.format(address.getStartDate());
            case end_date:
            case endDate:
                return dateFormat.format(address.getEndDate());
            case latitude:
                return address.getLatitude();
            case longitute:
                return address.getLongitude();
            case geopoint:
                return address.getGeopoint();
            case postal_code:
            case postalCode:
                return address.getPostalCode();
            case sub_town:
            case subTown:
                return address.getSubTown();
            case town:
                return address.getTown();
            case sub_district:
            case subDistrict:
                return address.getSubDistrict();
            case district:
            case county:
            case county_district:
            case countyDistrict:
                return address.getCountyDistrict();
            case city:
            case village:
            case cityVillage:
            case city_village:
                return address.getCityVillage();
            case state:
            case state_province:
            case stateProvince:
                return address.getStateProvince();
            case country:
                return address.getCountry();
            default:
                return null;
        }
    }

    private List<Model> parseXML(ModelXMLHandler modelXMLHandler, String xmlInput) {
        List<Model> modelTags = new ArrayList<>();
        try {
            Timber.i("Start Parsing ModelXML");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            xr.setContentHandler(modelXMLHandler);
            InputSource inStream = new InputSource();

            inStream.setCharacterStream(new StringReader(xmlInput));

            xr.parse(inStream);

            modelTags = modelXMLHandler.getTags();
        } catch (SAXTerminationException e) {
            modelTags = modelXMLHandler.getTags();
            Timber.i("Finished Parsing the Model");
        } catch (Exception e) {
            Timber.w(e);
        }
        return modelTags;
    }

    private String readFileAssets(String fileName) {
        String fileContents;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Timber.e(ex);
            return null;
        }
        return fileContents;
    }

}
