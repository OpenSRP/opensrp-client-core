package org.smartregister.login.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by samuelgithengi on 8/18/20.
 */
public class BaseLoginModelTest extends BaseRobolectricUnitTest {

    @Mock
    private Context context;

    private BaseLoginModel loginModel;

    @Before
    public void setUp() {
        loginModel = new BaseLoginModel();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }

    @Test
    public void testGetOpenSRPContextShouldReturnContext() {
        assertNotNull(loginModel.getOpenSRPContext());
        assertEquals(CoreLibrary.getInstance().context(), loginModel.getOpenSRPContext());

    }

    @Test
    public void testIsPasswordValidReturnsCorrectResult() {
        assertFalse(loginModel.isPasswordValid("".toCharArray()));
        assertTrue(loginModel.isPasswordValid("qwerty120".toCharArray()));


    }

    @Test
    public void testIsEmptyUsernameReturnsCorrectResult() {
        assertFalse(loginModel.isEmptyUsername("doe"));
        assertTrue(loginModel.isEmptyUsername(""));
        assertTrue(loginModel.isEmptyUsername(null));
    }

    @Test
    public void testIsUserLoggedOutShouldReturnCorrectValue() {
        assertTrue(loginModel.isUserLoggedOut());
        loginModel = spy(loginModel);
        when(loginModel.getOpenSRPContext()).thenReturn(context);
        when(context.IsUserLoggedOut()).thenReturn(false);
        assertFalse(loginModel.isUserLoggedOut());
        verify(context).IsUserLoggedOut();
    }


}
