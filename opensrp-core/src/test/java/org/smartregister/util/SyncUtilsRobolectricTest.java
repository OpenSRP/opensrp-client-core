package org.smartregister.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.account.AccountHelper;
import org.smartregister.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 09-02-2021.
 */
public class SyncUtilsRobolectricTest extends BaseRobolectricUnitTest {

    private SyncUtils syncUtils;
    private android.content.Context context;
    private UserService userService;

    @Before
    public void setUp() {
        Context opensrpContext = Mockito.spy(CoreLibrary.getInstance().context());
        userService = Mockito.spy(opensrpContext.userService());
        Mockito.doReturn(userService).when(opensrpContext).userService();

        context = Mockito.spy(RuntimeEnvironment.application);

        syncUtils = new SyncUtils(context);
        ReflectionHelpers.setField(syncUtils, "opensrpContext", opensrpContext);
    }

    @Test
    public void logoutUser() throws AuthenticatorException, OperationCanceledException, IOException {
        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.doNothing().when(context).startActivity(intentArgumentCaptor.capture());

        // Mock AccountManager
        AccountManager originalAccountManager =  ReflectionHelpers.getStaticField(AccountHelper.class, "accountManager");
        AccountManager accountManager =  Mockito.mock(AccountManager.class);
        ReflectionHelpers.setStaticField(AccountHelper.class, "accountManager", accountManager);

        final String CORE_ACCOUNT_NAME = "demo";
        String CORE_ACCOUNT_TYPE = "org.smartregister.core";
        Account[] accounts = {new Account(CORE_ACCOUNT_NAME, CORE_ACCOUNT_TYPE)};
        Mockito.doReturn(accounts).when(accountManager).getAccountsByType(CORE_ACCOUNT_TYPE);
        AccountManagerFuture<Bundle> accountManagerFuture = Mockito.mock(AccountManagerFuture.class);
        Intent accountAuthenticatorIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, accountAuthenticatorIntent);
        Mockito.doReturn(bundle).when(accountManagerFuture).getResult();
        Mockito.doReturn(accountManagerFuture).when(accountManager).updateCredentials(accounts[0], "provider", null, null, null, null);

        // Mock the registered anm
        CoreLibrary.getInstance().context().allSharedPreferences().updateANMUserName("demo");

        // Call the method under test
        syncUtils.logoutUser(R.string.logout_text);

        Mockito.verify(userService).forceRemoteLogin(Mockito.anyString());
        Mockito.verify(userService).logoutSession();

        Intent intent = intentArgumentCaptor.getValue();
        Assert.assertEquals(Intent.ACTION_MAIN, intent.getAction());
        Assert.assertTrue(intent.getCategories().contains(Intent.CATEGORY_LAUNCHER));
        Assert.assertEquals("org.smartregister.test", intent.getPackage());

        Mockito.verify(context).startActivity(Mockito.any(Intent.class));

        // Return the original account manager
        ReflectionHelpers.setStaticField(AccountHelper.class, "accountManager", originalAccountManager);
    }

    @Test
    public void getLogoutUserIntentShouldProvideAnonymousIntentWhenLauncherActivityIsUnavailable() {
        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.doNothing().when(context).startActivity(intentArgumentCaptor.capture());

        Intent intent = syncUtils.getLogoutUserIntent(R.string.logout_text);

        Assert.assertEquals(Intent.ACTION_MAIN, intent.getAction());
        Assert.assertTrue(intent.getCategories().contains(Intent.CATEGORY_LAUNCHER));
        Assert.assertEquals("org.smartregister.test", intent.getPackage());
        Assert.assertNull(intent.getComponent());
    }

    @Test
    public void getLogoutUserIntentShouldProvideLaunchActivityIntentWhenLauncherActivityIsAvailable() {

        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);
        Mockito.doNothing().when(context).startActivity(intentArgumentCaptor.capture());

        List<ResolveInfo> activities = new ArrayList<>();
        ResolveInfo resolveInfo = new ResolveInfo();
        String activityName = "activity.sample_activity";
        resolveInfo.activityInfo =  new ActivityInfo();
        resolveInfo.activityInfo.name = activityName;
        activities.add(resolveInfo);

        PackageManager packageManager = Mockito.spy(context.getPackageManager());
        Mockito.doReturn(packageManager).when(context).getPackageManager();
        Mockito.doReturn(activities).when(packageManager).queryIntentActivities(Mockito.any(Intent.class), Mockito.eq(0));

        // Call the method under test
        Intent intent = syncUtils.getLogoutUserIntent(R.string.logout_text);

        Assert.assertEquals(Intent.ACTION_MAIN, intent.getAction());
        Assert.assertTrue(intent.getCategories().contains(Intent.CATEGORY_LAUNCHER));
        Assert.assertEquals("org.smartregister.test", intent.getPackage());
        Assert.assertEquals(activityName, intent.getComponent().getClassName());
        Assert.assertEquals("org.smartregister.test", intent.getComponent().getPackageName());
        int flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        flags |= Intent.FLAG_ACTIVITY_CLEAR_TOP;
        Assert.assertEquals(flags, intent.getFlags());
        Assert.assertEquals(context.getString(R.string.logout_text), intent.getStringExtra(AllConstants.ACCOUNT_DISABLED));
    }
}
