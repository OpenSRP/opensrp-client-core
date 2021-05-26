package org.smartregister.util.mock;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;

/**
 * Created by kaderchowdhury on 28/11/17.
 */

public class XmlSerializerMock implements XmlSerializer {
    @Override
    public void setFeature(String s, boolean b) throws IllegalArgumentException, IllegalStateException {

    }

    @Override
    public boolean getFeature(String s) {
        return false;
    }

    @Override
    public void setProperty(String s, Object o) throws IllegalArgumentException, IllegalStateException {

    }

    @Override
    public Object getProperty(String s) {
        return null;
    }

    @Override
    public void setOutput(OutputStream outputStream, String s) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setOutput(Writer writer) throws IOException, IllegalArgumentException, IllegalStateException {
        try {
            String FORMNAME = "birthnotificationpregnancystatusfollowup";
            String model = "www/form/" + FORMNAME + "/model.xml";
            writer.write(getStringFromStream(new FileInputStream(getFileFromPath(this, model))));
        } catch (Exception e) {

        }
    }

    @Override
    public void startDocument(String s, Boolean aBoolean) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void endDocument() throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setPrefix(String s, String s1) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public String getPrefix(String s, boolean b) throws IllegalArgumentException {
        return null;
    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public XmlSerializer startTag(String s, String s1) throws IOException, IllegalArgumentException, IllegalStateException {
        return null;
    }

    @Override
    public XmlSerializer attribute(String s, String s1, String s2) throws IOException, IllegalArgumentException, IllegalStateException {
        return null;
    }

    @Override
    public XmlSerializer endTag(String s, String s1) throws IOException, IllegalArgumentException, IllegalStateException {
        return null;
    }

    @Override
    public XmlSerializer text(String s) throws IOException, IllegalArgumentException, IllegalStateException {
        return null;
    }

    @Override
    public XmlSerializer text(char[] chars, int i, int i1) throws IOException, IllegalArgumentException, IllegalStateException {
        return null;
    }

    @Override
    public void cdsect(String s) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void entityRef(String s) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void processingInstruction(String s) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void comment(String s) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void docdecl(String s) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void ignorableWhitespace(String s) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void flush() throws IOException {

    }

    private static File getFileFromPath(Object obj, String fileName) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getPath());
    }

    public String getStringFromStream(InputStream is) throws Exception {
        String fileContents = "";
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        fileContents = new String(buffer, "UTF-8");
        return fileContents;
    }
}
