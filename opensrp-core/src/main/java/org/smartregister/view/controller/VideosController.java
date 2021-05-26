package org.smartregister.view.controller;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import org.smartregister.R;
import org.smartregister.util.Log;

import java.text.MessageFormat;

public class VideosController {
    public static final String VIDEO_PLAYER_INTENT = "org.ei.dristhi_iec.VIDEO_PLAYER";
    public static final String VIDEO_NAME_PARAMETER = "VideoName";
    private Context context;

    public VideosController(Context context) {
        this.context = context;
    }

    public void play(String videoName) {
        try {
            Intent videoPlayerIntent = new Intent(VIDEO_PLAYER_INTENT);
            videoPlayerIntent.putExtra(VIDEO_NAME_PARAMETER, videoName);
            context.startActivity(videoPlayerIntent);
        } catch (ActivityNotFoundException e) {
            Log.logError(MessageFormat
                    .format("Could not play video: {0}. Exception: {1}", videoName, e));
            new AlertDialog.Builder(context)
                    .setMessage(R.string.videos_IEC_not_installed_dialog_message)
                    .setTitle(R.string.videos_cannot_play_video_dialog_title).setCancelable(true)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }).show();
        } catch (Exception e) {
            Log.logError(MessageFormat
                    .format("Could not play video: {0}. Exception: {1}", videoName, e));
            new AlertDialog.Builder(context)
                    .setMessage(R.string.videos_unknown_error_dialog_message)
                    .setTitle(R.string.videos_cannot_play_video_dialog_title).setCancelable(true)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }
}
