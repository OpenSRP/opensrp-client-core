package org.smartregister.service;

import android.content.res.AssetManager;
import android.webkit.JavascriptInterface;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;

import static java.text.MessageFormat.format;
import static org.smartregister.util.Log.logError;

public class ZiggyFileLoader {
    private String ziggyDirectoryPath;
    private String formDirectoryPath;
    private AssetManager assetManager;

    public ZiggyFileLoader(String ziggyDirectoryPath, String formDirectoryPath, AssetManager
            assetManager) {
        this.ziggyDirectoryPath = ziggyDirectoryPath;
        this.formDirectoryPath = formDirectoryPath;
        this.assetManager = assetManager;
    }

    public String getJSFiles() throws IOException, URISyntaxException {
        StringBuilder builder = new StringBuilder();
        String[] fileNames = assetManager.list(ziggyDirectoryPath);
        for (String fileName : fileNames) {
            if (fileName.endsWith(".js")) {
                builder.append(
                        IOUtils.toString(assetManager.open(ziggyDirectoryPath + "/" + fileName),
                                "UTF-8"));
            }
        }
        return builder.toString();
    }

    @JavascriptInterface
    public String loadAppData(String fileName) {
        try {
            FormPathService fps = new FormPathService(assetManager);
            return fps.getForms(fileName, "UTF-8");
        } catch (IOException e) {
            logError(format("Error while loading app data file: {0}, with exception: {1}", fileName,
                    e));
        }
        return null;
    }
}
