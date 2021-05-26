package org.smartregister.view.controller;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseUnitTest;
import org.smartregister.view.activity.EligibleCoupleDetailActivity;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by Vincent Karuri on 02/02/2021
 */
public class ProfileNavigationControllerTest extends BaseUnitTest {

    private ProfileNavigationController profileNavigationController;

    @Before
    public void setUp() throws Exception {
        profileNavigationController = new ProfileNavigationController();
    }

    @Test
    public void testNavigateToECProfileShouldNavigateToProfile() {
        final String CASE_ID = "case_id";

        Intent expectedIntent = new Intent(RuntimeEnvironment.application,
                EligibleCoupleDetailActivity.class);
        expectedIntent.putExtra(CASE_ID, CASE_ID);

        profileNavigationController.navigateToECProfile(RuntimeEnvironment.application, CASE_ID);
        Intent actualIntent = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }
}