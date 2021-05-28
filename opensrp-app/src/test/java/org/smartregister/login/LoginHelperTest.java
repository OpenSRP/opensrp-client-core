package org.smartregister.login;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.login.helper.LoginHelper;
import org.smartregister.view.contract.BaseLoginContract;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by samuelgithengi on 8/4/20.
 */
@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginHelperTest {

    @Mock
    private BaseLoginContract.Interactor interactor;

    @Test
    public void testGetInstanceShouldReturnNullIfNotInitialized() {
        assertNull(LoginHelper.getInstance());
    }

    @Test
    public void testGetInstanceShouldReturnObjectIfInitialized() {
        LoginHelper.init(interactor);
        assertNotNull(LoginHelper.getInstance());
    }
}
