package org.smartregister.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.R;

import java.time.LocalDate;

/**
 * Created by ndegwamartin on 29/06/2021.
 */
public class AppHealthUtilsTest extends BaseRobolectricUnitTest {

    @Mock
    private Context context;

    @Mock
    private View view;

    private static final String PACKAGE_NAME = "org.smartregister.test";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(context.getApplicationContext()).thenReturn(ApplicationProvider.getApplicationContext());
        Mockito.when(context.getPackageName()).thenReturn(PACKAGE_NAME);
    }

    @Test
    public void testConstructorBindsLongClickListenerToViewParam() {
        Mockito.doReturn(ApplicationProvider.getApplicationContext()).when(view).getContext();
        new AppHealthUtils(view);
        Mockito.verify(view).setOnLongClickListener(ArgumentMatchers.any(View.OnLongClickListener.class));
    }

    @Test
    public void testShowAppHealthSelectDialog() {
        AlertDialog alertDialog = AppHealthUtils.showAppHealthSelectDialog(ApplicationProvider.getApplicationContext());
        Assert.assertNotNull(alertDialog);
        Assert.assertNotNull(alertDialog.getListView());
        Assert.assertNotNull(alertDialog.getListView().getAdapter());
        Assert.assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.download_database), alertDialog.getListView().getAdapter().getItem(0));
        Assert.assertEquals(ApplicationProvider.getApplicationContext().getString(R.string.view_sync_stats), alertDialog.getListView().getAdapter().getItem(1));
    }

    @Test
    public void testCreateCopyDBNameReturnsCorrectTag() {
        String copyDBName = AppHealthUtils.createCopyDBName(context);
        Assert.assertNotNull(copyDBName);
        LocalDate today = LocalDate.now();
        Assert.assertEquals(String.format("%s_%d-%02d-%02d", context.getApplicationContext().getPackageName(), today.getYear(), today.getMonthValue(), today.getDayOfMonth()), copyDBName.substring(0, copyDBName.lastIndexOf("-")));
        Assert.assertTrue(copyDBName.contains(".db"));
    }

    @Test
    public void testRefreshFileSystemWhenAndroidVersionIsKitKatOrBelow() {
        ArgumentCaptor<Intent> intentArgumentCaptor = ArgumentCaptor.forClass(Intent.class);

        AppHealthUtils.refreshFileSystem(context, true);

        Mockito.verify(context).sendBroadcast(intentArgumentCaptor.capture());

        Intent captured = intentArgumentCaptor.getValue();
        Assert.assertNotNull(captured);
        Assert.assertEquals(Intent.ACTION_MEDIA_MOUNTED, captured.getAction());
    }

    @Test
    public void testRefreshFileSystemWhenAndroidVersionIsAboveKitKat() {
        AppHealthUtils.refreshFileSystem(context, false);
        Mockito.verify(context, Mockito.never()).sendBroadcast(ArgumentMatchers.any(Intent.class));
    }
}