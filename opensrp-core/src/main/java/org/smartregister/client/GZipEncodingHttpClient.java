package org.smartregister.client;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.smartregister.util.HttpResponseUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import timber.log.Timber;

import static org.apache.http.HttpStatus.SC_OK;

public class GZipEncodingHttpClient {

    private DefaultHttpClient httpClient;

    public GZipEncodingHttpClient(DefaultHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String fetchContent(HttpGet request) throws IOException, ParseException {
        String responseContent = null;
        HttpResponse response = null;
        try {
            if (!request.containsHeader("Accept-Encoding")) {
                request.addHeader("Accept-Encoding", "gzip");
            }

            response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != SC_OK) {
                throw new IOException(
                        "Invalid status code: " + response.getStatusLine().getStatusCode());
            }

            responseContent = retrieveStringResponse(response);
        } finally {
            consumeResponse(response);
        }
        return responseContent;
    }

    public HttpResponse execute(HttpGet request) throws IOException {
        return httpClient.execute(request);
    }

    public CredentialsProvider getCredentialsProvider() {
        return httpClient.getCredentialsProvider();
    }

    public HttpResponse postContent(HttpPost request) throws IOException {
        return httpClient.execute(request);
    }

    public void consumeResponse(HttpResponse httpResponse) {
        try {
            if (httpResponse == null || httpResponse.getEntity() == null) {
                return;
            }

            httpResponse.getEntity().consumeContent();

        } catch (IOException e) {
            Timber.e(e);
        }
    }

    public String retrieveStringResponse(HttpResponse httpResponse) throws IOException, ParseException {
        if (httpResponse == null) {
            return null;
        }
        InputStream inputStream = HttpResponseUtil.getResponseStream(httpResponse);
        return IOUtils.toString(inputStream);
    }
}
