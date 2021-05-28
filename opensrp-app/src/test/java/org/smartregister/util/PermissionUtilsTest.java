package org.smartregister.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.view.activity.BaseLoginActivityTest;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 18-05-2021.
 */
public class PermissionUtilsTest extends BaseRobolectricUnitTest {

    private Activity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(BaseLoginActivityTest.BaseLoginActivityImpl.class)
                .create()
                .get();
    }

    @Test
    public void isPermissionGrantedShouldReturnFalseAndRequestPermissionsWhenStatusIsPermissionDenied() {
        Activity activity = Mockito.spy(this.activity);
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;

        Mockito.doReturn(PackageManager.PERMISSION_DENIED).when(activity)
                .checkPermission(Mockito.eq(permission), Mockito.anyInt(), Mockito.anyInt());

        // call the method under test and perform assertion
        Assert.assertFalse(PermissionUtils.isPermissionGranted(activity, permission, 89));

        // Verify method call
        ArgumentCaptor<String[]> permissionsArgumentCaptor = ArgumentCaptor.forClass(String[].class);
        Mockito.verify(activity).requestPermissions(permissionsArgumentCaptor.capture(), Mockito.eq(89));
        Assert.assertEquals(permission, permissionsArgumentCaptor.getValue()[0]);
    }

    @Test
    public void isPermissionGrantedShouldReturnTrueWhenStatusIsPermissionGranted() {
        Activity activity = Mockito.spy(this.activity);
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;

        Mockito.doReturn(PackageManager.PERMISSION_GRANTED).when(activity)
                .checkPermission(Mockito.eq(permission), Mockito.anyInt(), Mockito.anyInt());

        // call the method under test and perform assertion
        Assert.assertTrue(PermissionUtils.isPermissionGranted(activity, permission, 89));
    }

    @Test
    public void isPermissionGrantedShouldReturnTrueWhenGivenArrayOfGrantedPermissions() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.WRITE_CONTACTS};

        Activity activity = Mockito.spy(this.activity);

        Mockito.doReturn(PackageManager.PERMISSION_GRANTED).when(activity).checkPermission(Mockito.eq(permissions[0]), Mockito.anyInt(), Mockito.anyInt());
        Mockito.doReturn(PackageManager.PERMISSION_GRANTED).when(activity).checkPermission(Mockito.eq(permissions[1]), Mockito.anyInt(), Mockito.anyInt());
        Mockito.doReturn(PackageManager.PERMISSION_GRANTED).when(activity).checkPermission(Mockito.eq(permissions[2]), Mockito.anyInt(), Mockito.anyInt());

        Assert.assertTrue(PermissionUtils.isPermissionGranted(activity, permissions, 78));
    }

    @Test
    public void isPermissionGrantedShouldReturnFalseWhenGivenNullPermissions() {
        Activity context = Mockito.spy(activity);
        Assert.assertFalse(PermissionUtils.isPermissionGranted(context, (String[]) null, 78));
    }

    @Test
    public void isPermissionGrantedShouldReturnFalseWhenGivenEmptyPermissions() {
        Activity context = Mockito.spy(activity);
        Assert.assertFalse(PermissionUtils.isPermissionGranted(context, new String[]{}, 78));
    }

    @Test
    public void isPermissionGrantedShouldReturnFalseWhenGivenArrayOfPermissionsWithASingleDeniedPermission() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.WRITE_CONTACTS};

        Activity activity = Mockito.spy(this.activity);

        Mockito.doReturn(PackageManager.PERMISSION_GRANTED).when(activity).checkPermission(Mockito.eq(permissions[0]), Mockito.anyInt(), Mockito.anyInt());
        Mockito.doReturn(PackageManager.PERMISSION_DENIED).when(activity).checkPermission(Mockito.eq(permissions[1]), Mockito.anyInt(), Mockito.anyInt());
        Mockito.doReturn(PackageManager.PERMISSION_GRANTED).when(activity).checkPermission(Mockito.eq(permissions[2]), Mockito.anyInt(), Mockito.anyInt());

        // call the method under test and perform assertion
        Assert.assertFalse(PermissionUtils.isPermissionGranted(activity, permissions, 78));

        // Verify method call
        ArgumentCaptor<String[]> permissionsArgumentCaptor = ArgumentCaptor.forClass(String[].class);
        Mockito.verify(activity).requestPermissions(permissionsArgumentCaptor.capture(), Mockito.eq(78));
        Assert.assertEquals(permissions[1], permissionsArgumentCaptor.getValue()[0]);
    }

    @Test
    public void verifyPermissionGrantedShouldReturnFalseWhenAPermissionWasNotGranted() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE};
        int[] grantResults = new int[]{PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_DENIED, PackageManager.PERMISSION_GRANTED};

        // call the method under test and perform assertion
        Assert.assertFalse(PermissionUtils.verifyPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE));
    }

    @Test
    public void verifyPermissionGrantedShouldReturnTrueWhenAllPermissionsAreGranted() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE};
        int[] grantResults = new int[]{PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_GRANTED};

        // call the method under test and perform assertion
        Assert.assertTrue(PermissionUtils.verifyPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE));
    }
}