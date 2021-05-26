package org.smartregister.client;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.text.ParseException;

public class GZipEncodingHttpClientImpl extends GZipEncodingHttpClient {

    public GZipEncodingHttpClientImpl(DefaultHttpClient httpClient) {
        super(httpClient);
    }

    @Override
    public String retrieveStringResponse(HttpResponse httpResponse) throws IOException, ParseException {
        if (httpResponse == null) {
            return null;
        }
        return "Response";
    }
}
