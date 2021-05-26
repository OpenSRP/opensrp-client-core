package org.smartregister.compression;

/**
 * Created by ndegwamartin on 28/04/2019.
 */
public interface ICompression {

    byte[] compress(String rawString);

    String decompress(byte[] compressedBytes);

    void compress(String inputFilePath, String compressedOutputFilepath);

    void decompress(String compressedInputFilePath, String decompressedOutputFilePath);
}
