package org.smartregister.dto;

public class ClientFormResponse {
    private ClientFormDTO clientForm;
    private ClientFormMetadataDTO clientFormMetadata;

    public ClientFormResponse(ClientFormDTO clientForm, ClientFormMetadataDTO clientFormMetadata) {
        this.clientForm = clientForm;
        this.clientFormMetadata = clientFormMetadata;
    }

    public ClientFormDTO getClientForm() {
        return clientForm;
    }

    public void setClientForm(ClientFormDTO clientForm) {
        this.clientForm = clientForm;
    }

    public ClientFormMetadataDTO getClientFormMetadata() {
        return clientFormMetadata;
    }

    public void setClientFormMetadata(ClientFormMetadataDTO clientFormMetadata) {
        this.clientFormMetadata = clientFormMetadata;
    }
}