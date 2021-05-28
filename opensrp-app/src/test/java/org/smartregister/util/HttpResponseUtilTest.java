package org.smartregister.util;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.compression.GZIPCompression;
import org.smartregister.domain.jsonmapping.LoginResponseData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.zip.GZIPInputStream;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 13-04-2021.
 */
public class HttpResponseUtilTest extends BaseRobolectricUnitTest {

    @Test
    public void getResponseStreamShouldReturnPlainInputStreamWhenNotGzipped() throws IOException, ParseException {
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        InputStream inputStream = Mockito.mock(InputStream.class);

        HttpEntity httpEntity = Mockito.mock(HttpEntity.class);

        Mockito.doReturn(httpEntity).when(httpResponse).getEntity();
        Mockito.doReturn(inputStream).when(httpEntity).getContent();

        // Call the method under test
        Assert.assertEquals(inputStream, HttpResponseUtil.getResponseStream(httpResponse));
    }

    @Test
    public void getResponseStreamShouldReturnGzipInputStreamContentWhenResponseIsGzipped() throws IOException, ParseException {
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        InputStream inputStream = new ByteArrayInputStream(new GZIPCompression().compress(""));

        HeaderElement[] headerElements = new HeaderElement[1];
        HeaderElement headerElement = Mockito.mock(HeaderElement.class);
        Mockito.doReturn("gzip").when(headerElement).getName();
        headerElements[0] = headerElement;//new BasicHeaderElement("Content-Encoding", "gzip");
        Header header = Mockito.mock(Header.class);
        Mockito.doReturn(headerElements).when(header).getElements();

        HttpEntity httpEntity = Mockito.mock(HttpEntity.class);

        Mockito.doReturn(httpEntity).when(httpResponse).getEntity();
        Mockito.doReturn(inputStream).when(httpEntity).getContent();
        Mockito.doReturn(header).when(httpEntity).getContentEncoding();

        // Call the method under test
        InputStream actualStream = HttpResponseUtil.getResponseStream(httpResponse);
        Assert.assertTrue(actualStream instanceof GZIPInputStream);
        Assert.assertEquals(inputStream, ReflectionHelpers.getField(actualStream, "in"));
    }

    @Test
    public void getResponseBody() throws IOException {
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);

        LoginResponseData loginResponseData = new LoginResponseData();
        loginResponseData.jurisdictions = Arrays.asList(new String[]{"Kenya", "Mozambique", "Tanzania"});
        InputStream inputStream = new ByteArrayInputStream(JsonFormUtils.gson.toJson(loginResponseData).getBytes(StandardCharsets.UTF_8));

        HttpEntity httpEntity = Mockito.mock(HttpEntity.class);

        Mockito.doReturn(httpEntity).when(httpResponse).getEntity();
        Mockito.doReturn(inputStream).when(httpEntity).getContent();

        // Call the method under test
        LoginResponseData actualLoginResponseData = HttpResponseUtil.getResponseBody(httpResponse);

        // Perform assertions
        Assert.assertEquals(actualLoginResponseData.jurisdictions, loginResponseData.jurisdictions);
    }

    @Test
    public void getResponseBodyWhenGivenResponseString() {
        LoginResponseData loginResponseData = new LoginResponseData();
        loginResponseData.jurisdictions = Arrays.asList(new String[]{"Kenya", "Mozambique", "Tanzania"});
        String responseString = JsonFormUtils.gson.toJson(loginResponseData);

        // Call the method under test
        LoginResponseData actualLoginResponseData = HttpResponseUtil.getResponseBody(responseString);

        // Perform assertions
        Assert.assertEquals(actualLoginResponseData.jurisdictions, loginResponseData.jurisdictions);
    }
}