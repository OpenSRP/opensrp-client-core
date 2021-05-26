package org.smartregister.util;

import org.apache.commons.validator.routines.UrlValidator;
import org.smartregister.CoreLibrary;

public class UrlUtil {
    public static boolean isValidUrl(String s){
        return new UrlValidator(new String[]{"http", "https"}).isValid(s);
    }

    public static boolean isValidEnvironment(String url) {
        return CoreLibrary.getInstance().getSyncConfiguration().validateOAuthUrl(url);
    }
}
