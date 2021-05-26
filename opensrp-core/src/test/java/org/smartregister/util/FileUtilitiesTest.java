package org.smartregister.util;

import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.when;
@RunWith(PowerMockRunner.class)
@PrepareForTest({Environment.class})
public class FileUtilitiesTest {

    private String FILE_NAME = "newFile.txt";

    @Mock
    private BufferedWriter mockWriter;

    @Rule
    public TemporaryFolder storageDirectory = new TemporaryFolder();
    private File existentDirectory;

    public FileUtilitiesTest() throws IOException {
    }

    @Before
    public void setUp() {
        existentDirectory = storageDirectory.getRoot();
        PowerMockito.mockStatic(Environment.class);
    }

    @Test
    public void assertWriteWritesSuccessfully() throws Exception {
        String testData = "string to write";
        when(Environment.getExternalStorageDirectory()).thenReturn(existentDirectory);
        PowerMockito.whenNew(BufferedWriter.class).withAnyArguments().thenReturn(mockWriter);
        FileUtilities fileUtils = new FileUtilities();
        try
        {
            //Write
            fileUtils.write(FILE_NAME, testData);
            // Read it from temp file
            String path = existentDirectory.getPath() + File.separator + "EZ_time_tracker" + File.separator + FILE_NAME;
            File file = new File(path);
            final String writenText = FileUtils.readFileToString(file);
            Assert.assertEquals(testData, writenText);
        }
        catch (Exception e)
        {
            Assert.fail();
        }

    }
    @Test
    public void assertReturnsCorrectFileExtension() {
        Assert.assertEquals(FileUtilities.getFileExtension(FILE_NAME), "txt");
    }
}
