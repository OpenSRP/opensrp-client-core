package org.smartregister.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import timber.log.Timber;

import static java.text.MessageFormat.format;

/**
 * Created by raihan on 5/25/15.
 */
public class FileUtilities {
    //    private final Context context;
    private static String mUserAgent = null;
    private Writer writer;
    private String absolutePath;

    public FileUtilities() {
        super();
//        this.context = context;
    }

    public static Bitmap retrieveStaticImageFromDisk(String fileName) {

        InputStream is = null;
        try {
            if (fileName == null) {
                return null;
            }
            File inputFile = new File(fileName);
            is = new FileInputStream(inputFile);
            Bitmap result = BitmapFactory.decodeStream(is);
            inputFile = null;
            is.close();
            is = null;
            return result;
        } catch (FileNotFoundException e) {
            Timber.e(e);
            return null;
        } catch (IOException e) {
            Timber.e(e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Timber.e("Failed to close static images input stream after attempting to retrieve image");
                }
            }
            System.gc();
        }
    }

    public static String getFileExtension(String fileName) {
        String extension = "";
        if (fileName != null && !fileName.isEmpty()) {
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                extension = fileName.substring(i + 1);
            }
        }
        return extension;
    }

    public static String getUserAgent(Context mContext) {
        if (mUserAgent == null) {
            mUserAgent = "OpenSRP";
            try {
                String packageName = mContext.getPackageName();
                String version = mContext.getPackageManager()
                        .getPackageInfo(packageName, 0).versionName;
                mUserAgent = mUserAgent + " (" + packageName + "/" + version + ")";
            } catch (PackageManager.NameNotFoundException e) {
                Timber.e(e, "Unable to find self by package name");
            }
        }
        return mUserAgent;
    }

    public static String getImageUrl(String entityID) {
        String url = format("{0}/{1}/{2}",
                CoreLibrary.getInstance().context().allSharedPreferences().fetchBaseURL(""),
                AllConstants.PROFILE_IMAGES_DOWNLOAD_PATH, entityID);
        return url;
    }

    public void write(String fileName, String data) {
        File root = Environment.getExternalStorageDirectory();
        File outDir = new File(root.getAbsolutePath() + File.separator + "EZ_time_tracker");
        if (!outDir.isDirectory()) {
            outDir.mkdir();
        }
        try {
            if (!outDir.isDirectory()) {
                throw new IOException("Unable to create directory EZ_time_tracker. Maybe the SD "
                        + "card is mounted?");
            }
            File outputFile = new File(outDir, fileName);
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(data);
//            Toast.makeText(context.getApplicationContext(),
//                    "Report successfully saved to: " + outputFile.getAbsolutePath(),
//                    Toast.LENGTH_LONG).show();
            writer.close();
        } catch (IOException e) {
            Timber.w(e);
//            Toast.makeText(context, e.getMessage() + " Unable to write to external storage.",
//                    Toast.LENGTH_LONG).show();
        }

    }

    public Writer getWriter() {
        return writer;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
