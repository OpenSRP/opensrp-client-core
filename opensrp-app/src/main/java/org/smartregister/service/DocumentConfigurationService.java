package org.smartregister.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.ClientForm;
import org.smartregister.domain.Manifest;
import org.smartregister.domain.Response;
import org.smartregister.dto.ClientFormResponse;
import org.smartregister.dto.ManifestDTO;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.ManifestRepository;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;

import timber.log.Timber;

public class DocumentConfigurationService {
    public static final String FORM_VERSION = "form_version";
    public static final String CURRENT_FORM_VERSION = "current_form_version";
    public static final String IDENTIFIERS = "identifiers";
    private static final String MANIFEST_SYNC_URL = "rest/manifest/";
    private static final String CLIENT_FORM_SYNC_URL = "rest/clientForm";
    private static final String FORM_IDENTIFIER = "form_identifier";

    private HTTPAgent httpAgent;
    private ManifestRepository manifestRepository;
    private ClientFormRepository clientFormRepository;
    private String packageName;

    public DocumentConfigurationService(HTTPAgent httpAgentArg, ManifestRepository manifestRepositoryArg, ClientFormRepository clientFormRepositoryArg, String packageNameArg) {
        httpAgent = httpAgentArg;
        manifestRepository = manifestRepositoryArg;
        clientFormRepository = clientFormRepositoryArg;
        packageName = packageNameArg;
    }

    public void fetchManifest() throws NoHttpResponseException, JSONException, IllegalArgumentException {
        if (httpAgent == null) {
            throw new IllegalArgumentException(MANIFEST_SYNC_URL + " http agent is null");
        }
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        Response resp = httpAgent.fetch(
                MessageFormat.format("{0}{1}{2}",
                        baseUrl, MANIFEST_SYNC_URL, packageName));

        if (resp.isFailure()) {
            throw new NoHttpResponseException(MANIFEST_SYNC_URL + " not returned data");
        }

        ManifestDTO receivedManifestDTO =
                new Gson().fromJson(resp.payload().toString(), ManifestDTO.class);

        Manifest receivedManifest = convertManifestDTOToManifest(receivedManifestDTO);

        //Note active manifest is null for the first time synchronization of the application
        Manifest activeManifest = manifestRepository.getActiveManifest();

        if (activeManifest != null && !activeManifest.getFormVersion().equals(receivedManifest.getFormVersion())) {
            //Untaging the active manifest and saving the received manifest and tagging it as active
            activeManifest.setActive(false);
            activeManifest.setNew(false);
            manifestRepository.addOrUpdate(activeManifest);
            saveReceivedManifest(receivedManifest);
        } else if (activeManifest == null) {
            saveReceivedManifest(receivedManifest);
        }

        syncClientForms(receivedManifest);
    }

    protected void syncClientForms(Manifest activeManifest) {
        //Fetching Client Forms for identifiers in the manifest
        for (String identifier : activeManifest.getIdentifiers()) {
            try {
                ClientForm clientForm = clientFormRepository.getActiveClientFormByIdentifier(identifier);
                if (clientForm == null || !clientForm.getVersion().equals(activeManifest.getFormVersion())) {
                    fetchClientForm(identifier, activeManifest.getFormVersion(), clientFormRepository.getActiveClientFormByIdentifier(identifier));
                }
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    protected void fetchClientForm(String identifier, String formVersion, ClientForm activeClientForm) throws NoHttpResponseException {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        if (httpAgent == null) {
            throw new IllegalArgumentException(CLIENT_FORM_SYNC_URL + " http agent is null");
        }
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        Response resp = httpAgent.fetch(
                MessageFormat.format("{0}{1}{2}",
                        baseUrl, CLIENT_FORM_SYNC_URL,
                        URLEncoder.encode("?" + FORM_IDENTIFIER + "=" + identifier +
                                "&" + FORM_VERSION + "=" + formVersion +
                                (activeClientForm == null ? "" : "&" + CURRENT_FORM_VERSION + "=" + activeClientForm.getVersion()))));

        if (resp.isFailure()) {
            throw new NoHttpResponseException(CLIENT_FORM_SYNC_URL + " not returned data");
        }


        ClientFormResponse clientFormResponse =
                new Gson().fromJson(resp.payload().toString(), ClientFormResponse.class);

        if (activeClientForm == null || !clientFormResponse.getClientFormMetadata().getVersion().equals(activeClientForm.getVersion())) {
            //if the previously active client form is not null it should be untagged from being new nor active
            if (activeClientForm != null) {
                activeClientForm.setActive(false);
                activeClientForm.setNew(false);
                clientFormRepository.addOrUpdate(activeClientForm);
            }
            ClientForm clientForm = convertClientFormResponseToClientForm(clientFormResponse);
            saveReceivedClientForm(clientForm);
        }
    }

    protected ClientForm convertClientFormResponseToClientForm(ClientFormResponse clientFormResponse) {
        ClientForm clientForm = new ClientForm();
        clientForm.setId(clientFormResponse.getClientForm().getId().toString());
        clientForm.setCreatedAt(clientFormResponse.getClientFormMetadata().getCreatedAt());
        clientForm.setIdentifier(clientFormResponse.getClientFormMetadata().getIdentifier());
        clientForm.setJson(clientFormResponse.getClientForm().getJson());
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
        List<Manifest> manifestsList = manifestRepository.getAllManifestsManifest();
        if (manifestsList.size() > 2) {
            manifestRepository.delete(manifestsList.get(2).getId());
        }
    }

    private void saveReceivedClientForm(ClientForm clientForm) {
        clientForm.setNew(true);
        clientForm.setActive(true);
        clientFormRepository.addOrUpdate(clientForm);

        //deleting the third oldest client Form from the repository
        List<ClientForm> clientFormList = clientFormRepository.getClientFormByIdentifier(clientForm.getIdentifier());
        if (clientFormList.size() > 2) {
            clientFormRepository.delete(clientFormList.get(2).getId());
        }
    }

    protected Manifest convertManifestDTOToManifest(ManifestDTO manifestDTO) throws JSONException {
        Manifest manifest = new Manifest();
        manifest.setId(manifestDTO.getId().toString());
        manifest.setAppVersion(manifestDTO.getAppVersion());
        manifest.setCreatedAt(manifestDTO.getCreatedAt());

        JSONObject json = new JSONObject(manifestDTO.getJson());
        if (json.has(FORM_VERSION)) {
            manifest.setFormVersion(json.getString(FORM_VERSION));
        }

        if (json.has(IDENTIFIERS)) {
            List<String> identifiers = new Gson().fromJson(json.getString(IDENTIFIERS),
                    new TypeToken<List<String>>() {
                    }.getType());
            manifest.setIdentifiers(identifiers);
        }
        return manifest;

    }


}