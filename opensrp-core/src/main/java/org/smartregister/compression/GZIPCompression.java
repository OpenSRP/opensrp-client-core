package org.smartregister.compression;

import android.util.Log;

import org.apache.commons.lang3.CharEncoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 26/04/2019.
 */
public class GZIPCompression implements ICompression {

    /**
     * Compression using gzip
     */
    @Override
    public byte[] compress(String rawString) {
        try {

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(os);
            gos.write(rawString.getBytes(CharEncoding.UTF_8));
            gos.close();
            byte[] compressed = os.toByteArray();
            os.close();

            return compressed;
        } catch (IOException e) {

            Timber.e(e);
            return null;
        }

    }

    /**
     * Decompression from gzip to string
     */
    @Override
    public String decompress(byte[] compressedBytes) {
        try {

            byte[] readBuffer = new byte[4096];
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(compressedBytes);

            GZIPInputStream inputStream = new GZIPInputStream(arrayInputStream);

            int read = inputStream.read(readBuffer, 0, readBuffer.length);
            inputStream.close();

            byte[] result = Arrays.copyOf(readBuffer, read);

            return new String(result, CharEncoding.UTF_8);

        } catch (IOException e) {

            Timber.e(e);
            return null;
        }

    }

    /**
     * Compress file
     *
     * @param inputFilePath            path of input file
     * @param compressedOutputFilepath path of output file (recommended use a .gz extension)
     */

    @Override
    public void compress(String inputFilePath, String compressedOutputFilepath) {
        try {
            byte[] buffer = new byte[1024];


            FileOutputStream os = new FileOutputStream(compressedOutputFilepath);
            GZIPOutputStream gos = new GZIPOutputStream(os);

            FileInputStream in = new FileInputStream(inputFilePath);

            int len;
            while ((len = in.read(buffer)) > 0) {
                gos.write(buffer, 0, len);
            }

            in.close();

            gos.finish();
            gos.close();

        } catch (IOException e) {

            Timber.e(e);
        }

    }

    /**
     * Compress file
     *
     * @param compressedInputFilePath    path of input file usually a .gz file extension
     * @param decompressedOutputFilePath path of output file
     */

    @Override
    public void decompress(String compressedInputFilePath, String decompressedOutputFilePath) {

        try {

            byte[] buffer = new byte[1024];


            GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(compressedInputFilePath));

            FileOutputStream out = new FileOutputStream(decompressedOutputFilePath);

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzis.close();
            out.close();

        } catch (IOException e) {
            Timber.e(e);
        }


    }

}
