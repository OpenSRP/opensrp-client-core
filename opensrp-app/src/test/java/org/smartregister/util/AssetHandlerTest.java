package org.smartregister.util;

import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.SyncFilter;
import org.smartregister.domain.Location;
import org.smartregister.domain.Setting;
import org.smartregister.domain.jsonmapping.Field;
import org.smartregister.domain.jsonmapping.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 25-05-2021.
 */
public class AssetHandlerTest extends BaseRobolectricUnitTest {

    @Test
    public void assetJsonToJavaShouldReturnNullWhenClazzIsNull() {
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("filename1.json", new Object());
        jsonMap.put("filename2.json", new Object());
        jsonMap.put("filename3.json", new Object());

        Assert.assertNull(AssetHandler.assetJsonToJava(jsonMap, RuntimeEnvironment.application, "some-file.json", null, new TypeToken<Field>(){}.getType()));
    }

    @Test
    public void assetJsonToJavaShouldReturnFileContentsObjectWhenFileIsCached() {
        Setting setting = new Setting();
        setting.setKey("is_remote");
        setting.setValue("true");
        setting.setLabel("Is configured remotely?");
        setting.setDescription("");
        setting.setIdentifier("92303-asdfaas-3423adasd");
        setting.setVersion("78");
        setting.setType("some-type");
        setting.setSyncStatus("Synced");

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("filename1.json", new Object());
        jsonMap.put("filename2.json", setting);
        jsonMap.put("filename3.json", new Object());

        Setting actualSetting = AssetHandler.assetJsonToJava(jsonMap, RuntimeEnvironment.application, "filename2.json", Setting.class, null);

        Assert.assertEquals(setting, actualSetting);
    }

    @Test
    public void assetJsonToJavaShouldReturnNullWhenFileIsCachedButCannotBeCastToClazz() {
        Setting setting = new Setting();
        setting.setKey("is_remote");
        setting.setValue("true");
        setting.setLabel("Is configured remotely?");
        setting.setDescription("");
        setting.setIdentifier("92303-asdfaas-3423adasd");
        setting.setVersion("78");
        setting.setType("some-type");
        setting.setSyncStatus("Synced");

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("filename1.json", new Object());
        jsonMap.put("filename2.json", setting);
        jsonMap.put("filename3.json", new Object());

        Assert.assertNull(AssetHandler.assetJsonToJava(jsonMap, RuntimeEnvironment.application, "filename2.json", Location.class, null));
    }

    @Test
    public void assetJsonToJavaShouldReturnDeserializedJsonFileWhenFileIsNotCached() {
        Setting setting = new Setting();
        setting.setKey("is_remote");
        setting.setValue("true");
        setting.setLabel("Is configured remotely?");
        setting.setDescription("");
        setting.setIdentifier("92303-asdfaas-3423adasd");
        setting.setVersion("78");
        setting.setType("some-type");
        setting.setSyncStatus("Synced");

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("filename1.json", new Object());
        jsonMap.put("filename2.json", setting);
        jsonMap.put("filename3.json", new Object());

        Table table = AssetHandler.assetJsonToJava(jsonMap, RuntimeEnvironment.application, "ec_client_fields.json", Table.class, new TypeToken<Table>(){}.getType());

        Assert.assertEquals("ec_family_member", table.name);
        Assert.assertEquals(13, table.columns.size());
        Assert.assertEquals("dob", table.columns.get(11).column_name);
    }

    @Test
    public void assetJsonToJavaShouldReturnNullWhenJsonMapIsNull() {
        Assert.assertNull(AssetHandler.assetJsonToJava(null, RuntimeEnvironment.application, "some-file.json", Field.class, new TypeToken<Field>(){}.getType()));
    }

    @Test
    public void jsonStringToJavaShouldReturnDeserializedJson() {
        String jsonString = "[{\"key\":\"is_remote\",\"value\":\"true\",\"label\":\"Is configured remotely?\",\"description\":\"\",\"identifier\":\"902sadf-23rwadfsad-asdf23\",\"version\":\"78\",\"type\":\"some-type\",\"syncStatus\":\"Synced\"}]";

        List<Setting> settingsList = AssetHandler.jsonStringToJava(jsonString, new TypeToken<List<Setting>>(){}.getType());

        Assert.assertEquals("is_remote", settingsList.get(0).getKey());
        Assert.assertEquals("Is configured remotely?", settingsList.get(0).getLabel());
    }

    @Test
    public void jsonStringToJavaShouldReturnNullWhenGivenWrongType() {
        String jsonString = "[{\"key\":\"is_remote\",\"value\":\"true\",\"label\":\"Is configured remotely?\",\"description\":\"\",\"identifier\":\"902sadf-23rwadfsad-asdf23\",\"version\":\"78\",\"type\":\"some-type\",\"syncStatus\":\"Synced\"}]";

        List<Setting> settingsList = AssetHandler.jsonStringToJava(jsonString, new TypeToken<List<SyncFilter>>(){}.getType());

        Assert.assertNull(settingsList);
    }

    @Test
    public void javaToJsonStringShouldReturnDeserializedJson() {
        List<Setting> settingList = new ArrayList<>();
        Setting setting = new Setting();
        setting.setKey("is_remote");
        setting.setValue("true");
        setting.setLabel("Is configured remotely?");
        setting.setDescription("");
        setting.setIdentifier("92303-asdfaas-3423adasd");
        setting.setVersion("78");
        setting.setType("some-type");
        setting.setSyncStatus("Synced");

        settingList.add(setting);

        String jsonString = AssetHandler.javaToJsonString(settingList, new TypeToken<List<Setting>>(){}.getType());
        Assert.assertTrue(jsonString.contains("\"type\":\"some-type\""));
    }

    @Test
    public void javaToJsonStringShouldReturnNullWhenGivenWrongType() {
        List<Setting> settingList = new ArrayList<>();
        Setting setting = new Setting();
        setting.setKey("is_remote");
        setting.setValue("true");
        setting.setLabel("Is configured remotely?");
        setting.setDescription("");
        setting.setIdentifier("92303-asdfaas-3423adasd");
        setting.setVersion("78");
        setting.setType("some-type");
        setting.setSyncStatus("Synced");

        settingList.add(setting);

        Assert.assertNull(AssetHandler.javaToJsonString(settingList, new TypeToken<List<SyncFilter>>(){}.getType()));
    }
}