package org.smartregister.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowToast;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;

import androidx.viewpager.widget.ViewPager;

public class BaseProfileActivityTest extends BaseUnitTest {

    private TestProfileActivity profileActivity;

    private final String INTENT_KEY = "intent_key";

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra(INTENT_KEY, INTENT_KEY);
        profileActivity = Mockito.spy(Robolectric.buildActivity(TestProfileActivity.class, intent).get());
    }

    @Test
    public void testOnCreateShouldBootstrapActivity() {
        profileActivity.onCreation();
        Assert.assertNotNull(profileActivity.getProfileAppBarLayout());
        Assert.assertNotNull(profileActivity.getActionBar());
        Assert.assertNotNull(profileActivity.getProfileAppBarLayout());
        Assert.assertNotNull(profileActivity.imageRenderHelper);
        Assert.assertNotNull(profileActivity.tabLayout);
        Assert.assertNotNull(profileActivity.getViewPager());

        Mockito.verify(profileActivity).initializePresenter();
        Mockito.verify(profileActivity).setupViewPager(Mockito.any(ViewPager.class));

        profileActivity.onClick(Mockito.mock(View.class));
        Mockito.verify(profileActivity).fetchProfileData();
    }

    @Test
    public void testProgressDialogControlsShouldPerformCorrectAction() {
        Assert.assertNull(ShadowDialog.getLatestDialog());
        profileActivity.showProgressDialog(R.string.empty_string);
        Dialog dialog = ShadowDialog.getLatestDialog();
        Assert.assertNotNull(dialog);
        Assert.assertTrue(dialog.isShowing());
        profileActivity.hideProgressDialog();
        Assert.assertFalse(dialog.isShowing());
    }

    @Test
    public void testDisplayToastShouldDisplayToast() {
        Assert.assertNull(ShadowToast.getLatestToast());
        profileActivity.displayToast(R.string.empty_string);
        Assert.assertNotNull(ShadowToast.getLatestToast());
    }

    @Test
    public void testGetIntentStringShouldGetCorrectIntentString() {
        Assert.assertEquals(INTENT_KEY, profileActivity.getIntentString(INTENT_KEY));
    }
}
