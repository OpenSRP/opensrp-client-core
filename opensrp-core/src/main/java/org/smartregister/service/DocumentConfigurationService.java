package org.smartregister.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.client.utils.contract.ClientFormContract;
import org.smartregister.domain.ClientForm;
import org.smartregister.domain.Manifest;
import org.smartregister.domain.Response;
import org.smartregister.dto.ClientFormResponse;
import org.smartregister.dto.ManifestDTO;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.ManifestRepository;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.Utils;

import java.text.MessageFormat;
import java.util.List;

import timber.log.Timber;

public class DocumentConfigurationService {
    public static final String MANIFEST_FORMS_VERSION = "forms_version";
    public static final String FORM_VERSION = "form_version";
    public static final String CURRENT_FORM_VERSION = "current_form_version";
    public static final String IDENTIFIERS = "identifiers";
    private static final String MANIFEST_SYNC_URL = "/rest/manifest/";
    private static final String MANIFEST_SEARCH_URL = "/rest/manifest/search";
    private static final String CLIENT_FORM_SYNC_URL = "/rest/clientForm";
    private static final String FORM_IDENTIFIER = "form_identifier";
    private static final String APP_ID = "app_id";
    private static final String APP_VERSION = "app_version";
    private final DristhiConfiguration configuration;
    private HTTPAgent httpAgent;
    private ManifestRepository manifestRepository;
    private ClientFormRepository clientFormRepository;

    public DocumentConfigurationService(HTTPAgent httpAgentArg, ManifestRepository manifestRepositoryArg, ClientFormRepository clientFormRepositoryArg, DristhiConfiguration
            configurationArg) {
        httpAgent = httpAgentArg;
        manifestRepository = manifestRepositoryArg;
        clientFormRepository = clientFormRepositoryArg;
        configuration = configurationArg;
    }

    public void fetchManifest() throws NoHttpResponseException, JSONException, IllegalArgumentException {
        if (httpAgent == null) {
            throw new IllegalArgumentException(MANIFEST_SYNC_URL + " http agent is null");
        }

        Context context = CoreLibrary.getInstance().context().applicationContext();

        String baseUrl = getBaseUrl();
        String finalUrls = MessageFormat.format("{0}{1}",
                baseUrl, MANIFEST_SEARCH_URL + "?" + APP_ID + "=" + Utils.getAppId(context) +
                        "&" + APP_VERSION + "=" + Utils.getAppVersion(context));
        Response resp = httpAgent.fetch(finalUrls);

        if (resp.isFailure()) {
            throw new NoHttpResponseException(MANIFEST_SYNC_URL + " not returned data");
        }

        ManifestDTO receivedManifestDTO = JsonFormUtils.gson.fromJson(resp.payload().toString(), ManifestDTO.class);

        if (receivedManifestDTO != null) {
            Manifest receivedManifest = convertManifestDTOToManifest(receivedManifestDTO);
            updateActiveManifest(receivedManifest);
            syncClientForms(receivedManifest);
        }
    }

    protected void updateActiveManifest(Manifest receivedManifest){
        //Note active manifest is null for the first time synchronization of the application
        Manifest activeManifest = manifestRepository.getActiveManifest();

        if (activeManifest != null && !activeManifest.getFormVersion().equals(receivedManifest.getFormVersion())) {
            //Untaging the active manifest and saving the received manifest and tagging it as active
            activeManifest.setActive(false);
            activeManifest.setNew(false);
            manifestRepository.addOrUpdate(activeManifest);
            saveReceivedManifest(receivedManifest);
            saveManifestVersion(receivedManifest.getVersion());
            saveFormsVersion(receivedManifest.getFormVersion());
        } else if (activeManifest == null) {
            saveReceivedManifest(receivedManifest);
            saveManifestVersion(receivedManifest.getVersion());
            saveFormsVersion(receivedManifest.getFormVersion());
        }
    }

    @VisibleForTesting
    protected void saveManifestVersion(@NonNull String manifestVersion) {
        boolean manifestVersionSaved = CoreLibrary.getInstance()
                .context()
                .allSharedPreferences()
                .saveManifestVersion(manifestVersion);
        if (!manifestVersionSaved) {
            Timber.e(new Exception("Saving manifest version failed"));
        }
    }

    @VisibleForTesting
    protected void saveFormsVersion(@NonNull String formsVersion) {
        boolean manifestVersionSaved = CoreLibrary.getInstance()
                .context()
                .allSharedPreferences()
                .saveFormsVersion(formsVersion);
        if (!manifestVersionSaved) {
            Timber.e(new Exception("Saving manifest version failed"));
        }
    }

    protected void syncClientForms(Manifest activeManifest) {
        //Fetching Client Forms for identifiers in the manifest
        for (String identifier : activeManifest.getIdentifiers()) {
            try {
                ClientForm clientForm = clientFormRepository.getLatestFormByIdentifier(identifier);
                if (clientForm == null || !clientForm.getVersion().equals(activeManifest.getFormVersion())) {
                    fetchClientForm(identifier, activeManifest.getFormVersion(), clientFormRepository.getActiveClientFormByIdentifier(identifier));
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    protected void fetchClientForm(String identifier, String formVersion, ClientForm latestClientForm) throws NoHttpResponseException {
        if (httpAgent == null) {
            throw new IllegalArgumentException(CLIENT_FORM_SYNC_URL + " http agent is null");
        }

        String baseUrl = getBaseUrl();
        Response resp = httpAgent.fetch(
                MessageFormat.format("{0}{1}{2}",
                        baseUrl, CLIENT_FORM_SYNC_URL,
                        "?" + FORM_IDENTIFIER + "=" + identifier +
                                "&" + FORM_VERSION + "=" + formVersion +
                                (latestClientForm == null ? "" : "&" + CURRENT_FORM_VERSION + "=" + latestClientForm.getVersion())));

        if (resp.isFailure()) {
            throw new NoHttpResponseException(CLIENT_FORM_SYNC_URL + " not returned data");
        }

        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy, hh:mm:ss aaa").create();
        ClientFormResponse clientFormResponse =
                gson.fromJson(resp.payload().toString(), ClientFormResponse.class);

        if (clientFormResponse == null) {
            throw new NoHttpResponseException(CLIENT_FORM_SYNC_URL + " not returned data");
        }

        if (latestClientForm == null || !clientFormResponse.getClientFormMetadata().getVersion().equals(latestClientForm.getVersion())) {
            //if the previously active client form is not null it should be untagged from being new nor active
            if (latestClientForm != null) {
                latestClientForm.setActive(false);
                latestClientForm.setNew(false);
                clientFormRepository.addOrUpdate(latestClientForm);
            }
            ClientForm clientForm = convertClientFormResponseToClientForm(clientFormResponse);
            saveReceivedClientForm(clientForm);
        }
    }

    protected ClientForm convertClientFormResponseToClientForm(ClientFormResponse clientFormResponse) {
        ClientForm clientForm = new ClientForm();
        clientForm.setId(clientFormResponse.getClientForm().getId());
        clientForm.setCreatedAt(clientFormResponse.getClientFormMetadata().getCreatedAt());
        clientForm.setIdentifier(clientFormResponse.getClientFormMetadata().getIdentifier());

        String jsonString = StringEscapeUtils.unescapeJson(clientFormResponse.getClientForm().getJson());
        jsonString = jsonString.substring(1, jsonString.length() - 1); //TODO DO NOT REMOVE this necessary evil; necessary because the unescaped json String still contains " at the beginning and end of the string making it not a valid json string

        clientForm.setJson(jsonString);
        clientForm.setVersion(clientFormResponse.getClientFormMetadata().getVersion());
        clientForm.setLabel(clientFormResponse.getClientFormMetadata().getLabel());
        clientForm.setJurisdiction(clientFormResponse.getClientFormMetadata().getJurisdiction());
        clientForm.setModule(clientFormResponse.getClientFormMetadata().getModule());

        return clientForm;
    }

    private void saveReceivedManifest(Manifest receivedManifest) {
        receivedManifest.setNew(true);
        receivedManifest.setActive(true);
        manifestRepository.addOrUpdate(receivedManifest);

        //deleting the third oldest manifest from the repository
        List<Manifest> manifestsList = manifestRepository.getAllManifests();
        if (manifestsList.size() > 2) {
            manifestRepository.delete(manifestsList.get(2).getId());
        }
    }

    private void saveReceivedClientForm(ClientForm clientForm) {
        clientForm.setNew(true);
        clientForm.setActive(true);
        clientFormRepository.addOrUpdate(clientForm);

        //deleting the third oldest client Form from the repository
        List<ClientFormContract.Model> clientFormList = clientFormRepository.getClientFormByIdentifier(clientForm.getIdentifier());
        if (clientFormList.size() > 2) {
            clientFormRepository.delete(clientFormList.get(2).getId());
        }
    }

    protected Manifest convertManifestDTOToManifest(ManifestDTO manifestDTO) throws JSONException {
        Manifest manifest = new Manifest();
        manifest.setVersion(manifestDTO.getIdentifier());
        manifest.setAppVersion(manifestDTO.getAppVersion());
        manifest.setCreatedAt(manifestDTO.getCreatedAt());

        JSONObject json = new JSONObject(manifestDTO.getJson());
        if (json.has(MANIFEST_FORMS_VERSION)) {
            manifest.setFormVersion(json.getString(MANIFEST_FORMS_VERSION));
        }

        if (json.has(IDENTIFIERS)) {
            List<String> identifiers = new Gson().fromJson(json.getJSONArray(IDENTIFIERS).toString(),
                    new TypeToken<List<String>>() {
                    }.getType());
            manifest.setIdentifiers(identifiers);
        }
        return manifest;

    }

    private String getBaseUrl(){
        String baseUrl = configuration.dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        return baseUrl;
    }

}
