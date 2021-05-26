package org.smartregister.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.smartregister.util.Log.logError;

/**
 * Created by Dimas Ciputra on 3/21/15.
 */
public class ZipUtil {
    private String zipFile;
    private String zipLocation;

    public ZipUtil(String zipFile, String zipLocation) {
        this.zipFile = zipFile;
        this.zipLocation = zipLocation;
        checkDir(" ");
    }

    public void unzip() {
        try {
            FileInputStream fin = new FileInputStream(this.zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("ZipService: ", "Unzipping " + ze.getName());
                if (ze.isDirectory()) {
                    checkDir(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(this.zipLocation + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }
                    zin.closeEntry();
                    fout.close();
                }
            }
            zin.close();
            /* delete the file */
            deleteFile(this.zipFile);
        } catch (Exception e) {
            logError("" + e);
        }
    }

    private void checkDir(String location) {
        File f = new File(this.zipLocation + location);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    private void deleteFile(String fileToDelete) {
        File f = new File(fileToDelete);
        if (f.exists()) {
            if (f.delete()) {
                Log.v("ZipService: ", "Deleting file " + f.getName());
            } else {
                Log.v("ZipService: ", "Unable to delete " + f.getName());
            }
        }
    }
}