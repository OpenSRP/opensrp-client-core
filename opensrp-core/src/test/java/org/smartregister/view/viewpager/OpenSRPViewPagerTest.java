package org.smartregister.view.viewpager;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.view.ViewAttributes;

/**
 * Created by kaderchowdhury on 12/11/17.
 */

public class OpenSRPViewPagerTest extends BaseUnitTest {

    @Test
    public void assertOpenSRPViewPagerInitializationTest() {
        OpenSRPViewPager openSRPViewPager = new OpenSRPViewPager(RuntimeEnvironment.application);
        Assert.assertNotNull(openSRPViewPager);
    }

    @Test
    @Ignore
    public void assertOpenSRPViewPagerInitializationTest2() {
        OpenSRPViewPager openSRPViewPager = new OpenSRPViewPager(RuntimeEnvironment.application, ViewAttributes.attrs);
        Assert.assertNotNull(openSRPViewPager);
    }

    @Test
    public void assertOnInterceptTouchEventReturnsFalse() {
        OpenSRPViewPager openSRPViewPager = new OpenSRPViewPager(RuntimeEnvironment.application);
        org.junit.Assert.assertEquals(openSRPViewPager.onInterceptTouchEvent(null), false);
    }

    @Test
    public void assertOnTouchEventReturnsFalse() {
        OpenSRPViewPager openSRPViewPager = new OpenSRPViewPager(RuntimeEnvironment.application);
        org.junit.Assert.assertEquals(openSRPViewPager.onTouchEvent(null), false);
    }

}
