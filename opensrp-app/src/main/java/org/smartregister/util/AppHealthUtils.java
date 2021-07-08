package org.smartregister.util;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;

import org.jetbrains.annotations.NotNull;
import org.smartregister.AllConstants;
import org.smartregister.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by ndegwamartin on 26/06/2021.
 */
public class AppHealthUtils {

    /**
     * @param view The view we want to bind the event that launches the app health action selection dialog
     *             The selection Alert Dialog will show if you long click/long press the view
     */
    public AppHealthUtils(View view) {
        view.setOnLongClickListener(v -> {
            AppHealthUtils.showAppHealthSelectDialog(view.getContext());
            return false;
        });
    }

    /**
     * Shows the Alert Dialog with the app health tools selection if the form of a dialog list
     *
     * @param context Android Activity or Context with a theme
     */
    public static AlertDialog showAppHealthSelectDialog(@NotNull Context context) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item);
        adapter.add(context.getString(R.string.download_database));
        adapter.add(context.getString(R.string.view_sync_stats));

        return new AlertDialog.Builder(context)
                .setAdapter(adapter, (dialog, which) -> {

                    Context healthStatContext = context instanceof HealthStatsView ? context : ((ContextThemeWrapper) context).getBaseContext();

                    switch (which) {
                        case 0:
                            if (healthStatContext instanceof HealthStatsView) {
                                ((HealthStatsView) healthStatContext).performDatabaseDownload();
                            }
                            break;
                        case 1:
                            if (healthStatContext instanceof HealthStatsView) {
                                ((HealthStatsView) healthStatContext).showSyncStats();
                            }
                            break;
                        default:
                            break;
                    }

                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public static void triggerDBCopying(Context context) {
        Utils.showToast(context, context.getString(R.string.export_db_notification));

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Utils.copyDatabase(AllConstants.DATABASE_NAME, createCopyDBName(context), context);
            refreshFileSystem(context, Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT);

            handler.post(() -> Utils.showToast(context, context.getString(R.string.database_download_success)));
        });
    }

    /**
     * A method to create the moniker for the database file created after exporting
     */
    public static String createCopyDBName(Context context) {
        String currentTimeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        return new StringBuilder(context.getApplicationContext().getPackageName()).append('_').append(currentTimeStamp).append(".db").toString();
    }

    /**
     * Once the file is saved trigger a refresh on the file system so that the file is available promptly to the user
     *
     * @param context         the Android context
     * @param isKitKatOrBelow A flag to refreshed based on the version of Android we are running
     */
    public static void refreshFileSystem(Context context, @VisibleForTesting boolean isKitKatOrBelow) {
        if (isKitKatOrBelow) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))));
        } else {
            MediaScannerConnection.scanFile(context, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()}, null, (path, uri) -> {
                //Overridden: Do nothing
            });
        }
    }

    public interface HealthStatsView {

        void performDatabaseDownload();

        void showSyncStats();
    }
}
