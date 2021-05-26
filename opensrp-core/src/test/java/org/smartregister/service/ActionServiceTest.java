package org.smartregister.service;

import org.ei.drishti.dto.Action;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.repository.AllReports;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.router.ActionRouter;
import org.smartregister.util.ActionBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionServiceTest extends BaseUnitTest {
    @Mock
    private DrishtiService drishtiService;
    @Mock
    private AllSettings allSettings;
    @Mock
    private AllSharedPreferences allSharedPreferences;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllReports allReports;
    @Mock
    private ActionRouter actionRouter;

    private ActionService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new ActionService(drishtiService, allSettings, allSharedPreferences, allReports, actionRouter);
    }

    @Test
    public void shouldFetchAlertActionsAndNotSaveAnythingIfThereIsNothingNewToSave() throws Exception {
        setupActions(ResponseStatus.success, new ArrayList<Action>());

        Assert.assertEquals(FetchStatus.nothingFetched, service.fetchNewActions());

        Mockito.verify(drishtiService).fetchNewActions("ANM X", "1234");
        Mockito.verifyNoMoreInteractions(drishtiService);
        Mockito.verifyNoMoreInteractions(actionRouter);
    }

    @Test
    public void shouldNotSaveAnythingIfTheDrishtiResponseStatusIsFailure() throws Exception {
        setupActions(ResponseStatus.failure, Arrays.asList(ActionBuilder.actionForCloseAlert("Case X", "ANC 1", "2012-01-01", "0")));

        Assert.assertEquals(FetchStatus.fetchedFailed, service.fetchNewActions());

        Mockito.verify(drishtiService).fetchNewActions("ANM X", "1234");
        Mockito.verifyNoMoreInteractions(drishtiService);
        Mockito.verifyNoMoreInteractions(actionRouter);
    }

    @Test
    public void shouldFetchAlertActionsAndSaveThemToRepository() throws Exception {
        Action action = ActionBuilder.actionForCreateAlert("Case X", "normal", "mother", "Ante Natal Care - Normal", "ANC 1", "2012-01-01", null, "0");
        setupActions(ResponseStatus.success, Arrays.asList(action));

        Assert.assertEquals(FetchStatus.fetched, service.fetchNewActions());

        Mockito.verify(drishtiService).fetchNewActions("ANM X", "1234");
        Mockito.verify(actionRouter).directAlertAction(action);
    }

    @Test
    public void shouldUpdatePreviousIndexWithIndexOfEachActionThatIsHandled() throws Exception {

        Action firstAction = ActionBuilder.actionForCreateAlert("Case X", "normal", "mother", "Ante Natal Care - Normal", "ANC 1", "2012-01-01", "2012-01-22", "11111");
        Action secondAction = ActionBuilder.actionForCreateAlert("Case Y", "normal", "mother", "Ante Natal Care - Normal", "ANC 2", "2012-01-01", "2012-01-11", "12345");

        setupActions(ResponseStatus.success, Arrays.asList(firstAction, secondAction));

        service.fetchNewActions();

        InOrder inOrder = Mockito.inOrder(actionRouter, allSettings);
        inOrder.verify(actionRouter).directAlertAction(firstAction);
        inOrder.verify(allSettings).savePreviousFetchIndex("11111");
        inOrder.verify(actionRouter).directAlertAction(secondAction);
        inOrder.verify(allSettings).savePreviousFetchIndex("12345");
    }

    @Test
    public void shouldHandleDifferentKindsOfActions() throws Exception {
        Action reportAction = ActionBuilder.actionForReport("Case X", "annual target");
        Action alertAction = ActionBuilder.actionForCreateAlert("Case X", "normal", "mother", "Ante Natal Care - Normal", "ANC 1", "2012-01-01", null, "0");
        Action closeMotherAction = ActionBuilder.actionForCloseMother("Case X");
        setupActions(ResponseStatus.success, Arrays.asList(reportAction, alertAction, closeMotherAction));

        service.fetchNewActions();

        Mockito.verify(allReports).handleAction(reportAction);
        Mockito.verify(actionRouter).directAlertAction(alertAction);
        Mockito.verify(actionRouter).directMotherAction(closeMotherAction);
    }

    private void setupActions(ResponseStatus status, List<Action> list) {
        Mockito.when(allSettings.fetchPreviousFetchIndex()).thenReturn("1234");
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("ANM X");
        Mockito.when(drishtiService.fetchNewActions("ANM X", "1234")).thenReturn(new Response<List<Action>>(status, list));
    }
}
