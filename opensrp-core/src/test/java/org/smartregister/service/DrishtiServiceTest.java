package org.smartregister.service;

import org.apache.commons.io.IOUtils;
import org.ei.drishti.dto.Action;
import org.ei.drishti.dto.AlertStatus;
import org.ei.drishti.dto.BeneficiaryType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.util.ActionBuilder;

import java.util.Arrays;
import java.util.List;

public class DrishtiServiceTest extends BaseUnitTest {
    @Mock
    private HTTPAgent httpAgent;

    private DrishtiService drishtiService;
    public static final String EXPECTED_URL = "http://base.drishti.url/actions?anmIdentifier=anm1&timeStamp=0";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        drishtiService = new DrishtiService(httpAgent, "http://base.drishti.url");
    }

    @Test
    public void shouldFetchAlertActions() throws Exception {
        Mockito.when(httpAgent.fetch(EXPECTED_URL)).thenReturn(new Response<String>(ResponseStatus.success, IOUtils.toString(getClass().getResource("/alerts.json"))));

        Response<List<Action>> actions = drishtiService.fetchNewActions("anm1", "0");

        Mockito.verify(httpAgent).fetch(EXPECTED_URL);
        Assert.assertEquals(Arrays.asList(ActionBuilder.actionForCreateAlert("Case X", AlertStatus.normal.value(), BeneficiaryType.mother.value(), "Ante Natal Care - Normal", "ANC 1", "2012-01-01", "2012-01-11", "1333695798583"),
                ActionBuilder.actionForCloseAlert("Case Y", "ANC 1", "2012-01-01", "1333695798644")), actions.payload());
        Assert.assertEquals(ResponseStatus.success, actions.status());
    }

    @Test
    public void shouldFetchNoAlertActionsWhenJsonIsForEmptyList() throws Exception {
        Mockito.when(httpAgent.fetch(EXPECTED_URL)).thenReturn(new Response<String>(ResponseStatus.success, "[]"));

        Response<List<Action>> actions = drishtiService.fetchNewActions("anm1", "0");

        Assert.assertTrue(actions.payload().isEmpty());
    }

    @Test
    public void shouldFetchNoAlertActionsWhenHTTPCallFails() throws Exception {
        Mockito.when(httpAgent.fetch(EXPECTED_URL)).thenReturn(new Response<String>(ResponseStatus.failure, null));

        Response<List<Action>> actions = drishtiService.fetchNewActions("anm1", "0");

        Assert.assertTrue(actions.payload().isEmpty());
        Assert.assertEquals(ResponseStatus.failure, actions.status());
    }

    @Test
    public void shouldURLEncodeTheANMIdentifierPartWhenItHasASpace() {
        String expectedURLWithSpaces = "http://base.drishti.url/actions?anmIdentifier=ANM+WITH+SPACE&timeStamp=0";
        Mockito.when(httpAgent.fetch(expectedURLWithSpaces)).thenReturn(new Response<String>(ResponseStatus.success, "[]"));

        drishtiService.fetchNewActions("ANM WITH SPACE", "0");

        Mockito.verify(httpAgent).fetch(expectedURLWithSpaces);
    }

    @Test
    public void shouldReturnFailureResponseWhenJsonIsMalformed() {
        String expectedURLWithSpaces = "http://base.drishti.url/actions?anmIdentifier=ANMX&timeStamp=0";
        Mockito.when(httpAgent.fetch(expectedURLWithSpaces)).thenReturn(new Response<String>(ResponseStatus.success, "[{\"anmIdentifier\": \"ANMX\", "));

        Response<List<Action>> actions = drishtiService.fetchNewActions("ANMX", "0");

        Assert.assertTrue(actions.payload().isEmpty());
        Assert.assertEquals(ResponseStatus.failure, actions.status());
    }
}
