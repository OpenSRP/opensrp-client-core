package org.smartregister.shadows;
import android.content.Context;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.util.AssetData;
import org.smartregister.util.AssetHandler;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 17-03-2020.
 */

@Implements(AssetHandler.class)
public class ShadowAssetHandler {

    @Implementation
    public static String readFileFromAssetsFolder(String fileName, Context context) {
        if ("ec_client_classification.json".equals(fileName)) {
            return AssetData.ec_client_classification_json;
        }

        return "";
    }
}