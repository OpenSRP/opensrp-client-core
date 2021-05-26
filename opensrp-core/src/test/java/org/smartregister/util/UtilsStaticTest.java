package org.smartregister.util;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.BaseUnitTest;

@PrepareForTest(Utils.class)
public class UtilsStaticTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void testGetUserInitialsReturnsUserInitialsGivenValidFullPreferredName() {
        PowerMockito.mockStatic(Utils.class);
        Mockito.when(Utils.getPrefferedName()).thenReturn("Brian Mwasi");
        Mockito.when(Utils.getUserInitials()).thenCallRealMethod();
        String initials = Utils.getUserInitials();
        Assert.assertEquals("BM", initials);
    }

    @Test
    public void testGetUserInitialsReturnsUserInitialsGivenValidOnePreferredName() {
        PowerMockito.mockStatic(Utils.class);
        Mockito.when(Utils.getPrefferedName()).thenReturn("Brian");
        Mockito.when(Utils.getUserInitials()).thenCallRealMethod();
        String initials = Utils.getUserInitials();
        Assert.assertEquals("B", initials);
    }

    @Test
    public void testGetUserInitialsReturnsDefaultWhenPreferredNameIsNull() {
        PowerMockito.mockStatic(Utils.class);
        Mockito.when(Utils.getPrefferedName()).thenReturn(null);
        Mockito.when(Utils.getUserInitials()).thenCallRealMethod();
        String initials = Utils.getUserInitials();
        Assert.assertEquals("Me", initials);
    }
}
