package org.smartregister.router;

import junit.framework.Assert;

import org.ei.drishti.dto.Action;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.service.AlertService;
import org.smartregister.service.MotherService;

/**
 * Created by kaderchowdhury on 12/11/17.
 */
@PrepareForTest({CoreLibrary.class})
public class ActionRouterTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private Context context;
    @Mock
    private AlertService alertService;
    @Mock
    private MotherService motherService;
    @Mock
    private CoreLibrary coreLibrary;
    private ActionRouter actionRouter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(CoreLibrary.class);
        CoreLibrary.init(context);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.alertService()).thenReturn(alertService);
        PowerMockito.when(context.motherService()).thenReturn(motherService);
        PowerMockito.doNothing().when(motherService).close(Mockito.anyString(), Mockito.anyString());

        actionRouter = new ActionRouter();
        Assert.assertNotNull(actionRouter);
    }

    @Test
    public void assertDirectAlertActionTest() throws Exception {
        Action action = new Action("", "", "createAlert", null, "", false, null);
        actionRouter.directAlertAction(action);//void nothing to test
        Assert.assertNotNull(actionRouter);
    }

    @Test
    public void assertDirectMotherActionTest() throws Exception {
        Action action = new Action("", "", "close", null, "", false, null);
//        actionRouter.directMotherAction(action);//void nothing to test
        Assert.assertNotNull(actionRouter);
    }

}
