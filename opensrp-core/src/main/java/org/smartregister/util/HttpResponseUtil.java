package org.smartregister.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.smartregister.domain.jsonmapping.LoginResponseData;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.zip.GZIPInputStream;

import static java.text.MessageFormat.format;
import static org.smartregister.util.Log.logError;

public class HttpResponseUtil {
    public static InputStream getResponseStream(HttpResponse response) throws IOException, ParseException {
        if (response.getEntity() != null && response.getEntity().getContentEncoding() != null) {
            HeaderElement[] codecs = response.getEntity().getContentEncoding().getElements();
            for (HeaderElement codec : codecs) {
                if (codec.getName().equalsIgnoreCase("gzip")) {
                    return new GZIPInputStream(response.getEntity().getContent());
                }
            }
        }
        return response.getEntity().getContent();
    }

    public static LoginResponseData getResponseBody(HttpResponse response) {
        try {
            InputStream responseStream = getResponseStream(response);
            String responseString = IOUtils.toString(responseStream);
            if (StringUtils.isBlank(responseString)) {
                return null;
            }
            return AssetHandler.jsonStringToJava(responseString, LoginResponseData.class);
        } catch (Exception e) {
            logError(format("Cannot read data from response due to exception: {0}. Stack "
                    + "trace: {1}", e.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return null;
    }

    public static LoginResponseData getResponseBody(String responseString) {
        try {
            if (StringUtils.isBlank(responseString)) {
                return null;
            }
            return AssetHandler.jsonStringToJava(responseString, LoginResponseData.class);
        } catch (Exception e) {
            logError(format("Cannot read data from response due to exception: {0}. Stack "
                    + "trace: {1}", e.getMessage(), ExceptionUtils.getStackTrace(e)));
        }
        return null;
    }

}
