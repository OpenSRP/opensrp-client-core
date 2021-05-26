package org.smartregister.dto;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by cozej4 on 2020-04-24.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class ClientFormResponseTest {

    @Test
    public void getClientForm() {
        ClientFormDTO clientFormDTO = new ClientFormDTO();
        clientFormDTO.setId(1);
        clientFormDTO.setJson("[\"test json\"]");

        ClientFormMetadataDTO clientFormMetadataDTO = new ClientFormMetadataDTO();
        Date now = Calendar.getInstance().getTime();
        clientFormMetadataDTO.setCreatedAt(now);
        clientFormMetadataDTO.setId(1L);
        clientFormMetadataDTO.setIdentifier("referral/anc_form");
        clientFormMetadataDTO.setJurisdiction("test jurisdiction");
        clientFormMetadataDTO.setLabel("ANC Referral form");
        clientFormMetadataDTO.setModule("ANC");
        clientFormMetadataDTO.setVersion("0.0.1");

        ClientFormResponse clientFormResponse = new ClientFormResponse(clientFormDTO, clientFormMetadataDTO);


        Assert.assertEquals("referral/anc_form", clientFormResponse.getClientFormMetadata().getIdentifier());
        Assert.assertEquals("test jurisdiction", clientFormResponse.getClientFormMetadata().getJurisdiction());
        Assert.assertEquals("ANC Referral form", clientFormResponse.getClientFormMetadata().getLabel());
        Assert.assertEquals("ANC", clientFormResponse.getClientFormMetadata().getModule());
        Assert.assertEquals("0.0.1", clientFormResponse.getClientFormMetadata().getVersion());
        Assert.assertEquals(now, clientFormResponse.getClientFormMetadata().getCreatedAt());
        Assert.assertEquals(1, clientFormResponse.getClientForm().getId());
        Assert.assertEquals("[\"test json\"]", clientFormResponse.getClientForm().getJson());
    }
}