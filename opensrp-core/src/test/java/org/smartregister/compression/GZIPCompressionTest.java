package org.smartregister.compression;

import org.apache.commons.lang3.CharEncoding;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by ndegwamartin on 2019-05-16.
 */
public class GZIPCompressionTest {

    public final String TEST_STRING = "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression " +
            "Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression, " +
            "I Am a Compression Test String with many bytes that totally needs compression, I Am a Compression Test String with many bytes that totally needs compression";

    private ICompression gzipCompression;


    @Before
    public void setUp() {
        gzipCompression = new GZIPCompression();
    }

    @Test
    public void testCompressMethodReturnsACompressedOutput() throws IOException {

        byte[] original = TEST_STRING.getBytes(CharEncoding.UTF_8);
        byte[] compressed = gzipCompression.compress(TEST_STRING);

        Assert.assertTrue(original.length > compressed.length);
    }

    @Test
    public void testDecompressMethodDecompressesBackToCorrectInput() throws IOException {

        byte[] compressed = gzipCompression.compress(TEST_STRING);

        String decompressedString = gzipCompression.decompress(compressed);

        Assert.assertEquals(TEST_STRING, decompressedString);
    }

    @Test
    public void testCompressFileMethodReturnsACompressedOutputFile() throws IOException {

        String filePath = getFilePath("compression_test_file.txt");

        String outputFilePath = filePath + "_compressed.gz";

        File originalFile = new File(filePath);

        Assert.assertTrue(originalFile.length() > 0);

        File compressedFile = new File(outputFilePath);

        Assert.assertEquals(0, compressedFile.length());

        gzipCompression.compress(filePath, outputFilePath);

        Assert.assertTrue(compressedFile.length() > 0);

        Assert.assertTrue(compressedFile.length() < originalFile.length());

    }

    @Test
    public void testDecompressFileMethodDecompressesBackToCorrectInputFile() throws IOException {

        String filePath = getFilePath("compression_test_file.txt");

        String compressedOutputFilePath = filePath + "_compressed.gz";

        String decompressedOutputFilePath = filePath + "_decompressed.txt";

        File originalFile = new File(filePath);

        File compressedFile = new File(compressedOutputFilePath);

        File deCompressedFile = new File(decompressedOutputFilePath);

        gzipCompression.compress(filePath, compressedOutputFilePath);

        Assert.assertTrue(compressedFile.length() > 0);
        Assert.assertEquals(0, deCompressedFile.length());

        gzipCompression.decompress(compressedOutputFilePath, decompressedOutputFilePath);

        Assert.assertTrue(compressedFile.length() > 0);
        Assert.assertEquals(originalFile.length(), deCompressedFile.length());
    }

    @After
    public void cleanUpFiles() {

        String filePath = getFilePath("compression_test_file.txt_compressed.gz");
        File compressedFile = new File(filePath);
        if (compressedFile.exists()) {
            compressedFile.delete();
        }

        filePath = getFilePath("compression_test_file.txt_decompressed.txt");
        File deCompressedFile = new File(filePath);
        if (deCompressedFile.exists()) {
            deCompressedFile.delete();
        }
    }

    private String getFilePath(String fileName) {

        return "src/test/assets/" + fileName;
    }
}
