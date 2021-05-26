package org.smartregister.client;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by kaderchowdhury on 30/11/17.
 */

public class MockHttpResponse implements CloseableHttpResponse {

    @Override
    public StatusLine getStatusLine() {
        return new StatusLine() {
            @Override
            public ProtocolVersion getProtocolVersion() {
                return null;
            }

            @Override
            public int getStatusCode() {
                return org.apache.http.HttpStatus.SC_OK;
            }

            @Override
            public String getReasonPhrase() {
                return null;
            }
        };
    }

    @Override
    public void setStatusLine(StatusLine statusLine) {
        System.out.println();
    }

    @Override
    public void setStatusLine(ProtocolVersion protocolVersion, int i) {
        System.out.println();
    }

    @Override
    public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {
        System.out.println();
    }

    @Override
    public void setStatusCode(int i) throws IllegalStateException {
        System.out.println();
    }

    @Override
    public void setReasonPhrase(String s) throws IllegalStateException {
        System.out.println();
    }

    @Override
    public HttpEntity getEntity() {
        return new HttpEntity() {
            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public boolean isChunked() {
                return false;
            }

            @Override
            public long getContentLength() {
                return 0;
            }

            @Override
            public Header getContentType() {
                return null;
            }

            @Override
            public Header getContentEncoding() {
                return null;
            }

            @Override
            public InputStream getContent() throws IOException, IllegalStateException {
                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return 0;
                    }
                };
            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException {
                System.out.println();
            }

            @Override
            public boolean isStreaming() {
                return false;
            }

            @Override
            public void consumeContent() throws IOException {
                System.out.println();
            }
        };
    }

    @Override
    public void setEntity(HttpEntity httpEntity) {
        System.out.println();
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void setLocale(Locale locale) {
        System.out.println();
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return null;
    }

    @Override
    public boolean containsHeader(String s) {
        return false;
    }

    @Override
    public Header[] getHeaders(String s) {
        return new Header[0];
    }

    @Override
    public Header getFirstHeader(String s) {
        return null;
    }

    @Override
    public Header getLastHeader(String s) {
        return null;
    }

    @Override
    public Header[] getAllHeaders() {
        return new Header[0];
    }

    @Override
    public void addHeader(Header header) {
        System.out.println();
    }

    @Override
    public void addHeader(String s, String s1) {
        System.out.println();
    }

    @Override
    public void setHeader(Header header) {
        System.out.println();
    }

    @Override
    public void setHeader(String s, String s1) {
        System.out.println();
    }

    @Override
    public void setHeaders(Header[] headers) {
        System.out.println();
    }

    @Override
    public void removeHeader(Header header) {
        System.out.println();
    }

    @Override
    public void removeHeaders(String s) {
        System.out.println();
    }

    @Override
    public HeaderIterator headerIterator() {
        return null;
    }

    @Override
    public HeaderIterator headerIterator(String s) {
        return null;
    }

    @Override
    public HttpParams getParams() {
        return null;
    }

    @Override
    public void setParams(HttpParams httpParams) {
        System.out.println();
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }
}
