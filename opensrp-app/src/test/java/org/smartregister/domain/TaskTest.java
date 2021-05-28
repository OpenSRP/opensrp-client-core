package org.smartregister.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.smartregister.util.DateTimeTypeConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.smartregister.domain.Task.TaskStatus.READY;

/**
 * Created by samuelgithengi on 11/22/18.
 */
public class TaskTest {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd'T'HHmm"))
            .create();

    protected static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HHmm");

    private String taskJson = "{\"identifier\":\"tsk11231jh22\",\"groupIdentifier\":\"2018_IRS-3734{\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc\",\"executionPeriod\":{\"start\":\"2018-11-10T2200\"},\"authoredOn\":\"2018-10-31T0700\",\"lastModified\":\"2018-10-31T0700\",\"owner\":\"demouser\",\"note\":[{\"authorString\":\"demouser\",\"time\":\"2018-01-01T0800\",\"text\":\"This should be assigned to patrick.\"}],\"serverVersion\":0,\"reasonReference\":\"fad051d9-0ff6-424a-8a44-4b90883e2841\",\"structureId\":\"structure._id.33efadf1-feda-4861-a979-ff4f7cec9ea7\"}";

    private String task2Json = "{\"identifier\":\"tsk11231jh22\",\"campaignIdentifier\":\"IRS_2018_S1\",\"groupIdentifier\":\"2018_IRS-3734{\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":\"stat\",\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc\",\"executionPeriod\":{\"start\":\"2018-11-10T2200\",\"end\":null},\"authoredOn\":\"2018-10-31T0700\",\"lastModified\":\"2018-10-31T07:00:00\",\"owner\":\"demouser\",\"note\":[{\"authorString\":\"demouser\",\"time\":\"2018-01-01T0800\",\"text\":\"This should be assigned to patrick.\"}],\"serverVersion\":0}";

    @Test
    public void testDeserialize() {
        Task task = gson.fromJson(taskJson, Task.class);
        assertEquals("tsk11231jh22", task.getIdentifier());
        assertEquals("2018_IRS-3734{", task.getGroupIdentifier());
        assertEquals(READY, task.getStatus());
        assertEquals("Not Visited", task.getBusinessStatus());
        assertEquals(Task.TaskPriority.ROUTINE, task.getPriority());
        assertEquals("IRS", task.getCode());
        assertEquals("Spray House", task.getDescription());
        assertEquals("IRS Visit", task.getFocus());
        assertEquals("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", task.getForEntity());
        assertEquals("2018-11-10T2200", task.getExecutionPeriod().getStart().toString(formatter));
        assertNull(task.getExecutionPeriod().getEnd());
        assertEquals("2018-10-31T0700", task.getAuthoredOn().toString(formatter));
        assertEquals("2018-10-31T0700", task.getLastModified().toString(formatter));
        assertEquals("demouser", task.getOwner());
        assertEquals(1, task.getNotes().size());
        assertEquals("demouser", task.getNotes().get(0).getAuthorString());
        assertEquals("2018-01-01T0800", task.getNotes().get(0).getTime().toString(formatter));
        assertEquals("This should be assigned to patrick.", task.getNotes().get(0).getText());
        assertEquals("structure._id.33efadf1-feda-4861-a979-ff4f7cec9ea7", task.getStructureId());
        assertEquals("fad051d9-0ff6-424a-8a44-4b90883e2841", task.getReasonReference());
    }

    @Test
    public void testDeserializeWithTimeFormatArgument() {
        Task task = gson.fromJson(task2Json, Task.class);
        assertEquals("2018-10-31T0700", task.getLastModified().toString(formatter));
    }

    @Test
    public void testSerialize() {
        Task task = gson.fromJson(taskJson, Task.class);
        assertEquals(taskJson, gson.toJson(task));
    }

}
