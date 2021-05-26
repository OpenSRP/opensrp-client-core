package org.smartregister.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.AllConstants;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.tag.FormTag;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.smartregister.clientandeventmodel.DateUtil.yyyyMMdd;
import static org.smartregister.clientandeventmodel.DateUtil.yyyyMMddHHmmss;
import static org.smartregister.util.JsonFormUtils.ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.KEY;
import static org.smartregister.util.JsonFormUtils.OPENMRS_ENTITY;
import static org.smartregister.util.JsonFormUtils.OPENMRS_ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.PERSON_ATTRIBUTE;
import static org.smartregister.util.JsonFormUtils.PERSON_INDENTIFIER;
import static org.smartregister.util.JsonFormUtils.SAVE_ALL_CHECKBOX_OBS_AS_ARRAY;
import static org.smartregister.util.JsonFormUtils.SAVE_OBS_AS_ARRAY;
import static org.smartregister.util.JsonFormUtils.VALUE;
import static org.smartregister.util.JsonFormUtils.dd_MM_yyyy;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
public class JsonFormUtilsTest {

    @Captor
    ArgumentCaptor<Obs> obsArgumentCaptor;

    private String formresultJson =
            "{\"count\":\"1\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\"," +
                    "\"relational_id\":\"\",\"step1\":{\"title\":\"Birth Registration\"," +
                    "\"fields\":[{\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"uploadButtonText\":\"শিশুর ছবি " +
                    "তুলুন \",\"openmrs_entity_parent\":\"\",\"type\":\"choose_image\",\"key\":\"Child_Photo\"}," +
                    "{\"default\":\"[\\\"Bangladesh\\\"]\"," +
                    "\"tree\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"nodes\":[{\"key" +
                    "\":\"Kuptala:Ward1:1-KA:Abdur Rahim Memberer bari-Kholapar\",\"level\":\"\",\"name\":\"Abdur Rahim " +
                    "Memberer bari-Kholapar\"},{\"key\":\"Kuptala:Ward1:1-KA:Jaynal Abediner Bari-Jangalia\"," +
                    "\"level\":\"\",\"name\":\"Jaynal Abediner Bari-Jangalia\"},{\"key\":\"Kuptala:Ward1:1-KA:Narayon " +
                    "Cowdhurir bari-Kholapara\",\"level\":\"\",\"name\":\"Narayon Cowdhurir bari-Kholapara\"}]," +
                    "\"key\":\"Kuptala:Ward1:1-KA\",\"level\":\"\",\"name\":\"1-KA\"}],\"key\":\"Kuptala:Ward-1\"," +
                    "\"level\":\"\",\"name\":\"Ward-1\"},{\"nodes\":[{\"key\":\"Kuptala:Ward-2:1-KA\",\"level\":\"\"," +
                    "\"name\":\"1-KA\"}],\"key\":\"Kuptala:Ward-2\",\"level\":\"\",\"name\":\"Ward-2\"}]," +
                    "\"key\":\"Kuptala\",\"level\":\"\",\"name\":\"Kuptala\"}],\"key\":\"Gaibandha Sadar\",\"level\":\"\"," +
                    "\"name\":\"Gaibandha Sadar\"}],\"key\":\"Gaibandha\",\"level\":\"\",\"name\":\"Gaibandha\"}]," +
                    "\"key\":\"Rangpur\",\"level\":\"\",\"name\":\"Rangpur\"}],\"key\":\"Bangladesh\",\"level\":\"\"," +
                    "\"name\":\"Bangladesh\"}],\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home " +
                    "facility\"},\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"value\":\"[\\\"Bangladesh\\\"," +
                    "\\\"Rangpur\\\",\\\"Gaibandha\\\",\\\"Gaibandha Sadar\\\",\\\"Kuptala\\\",\\\"Kuptala:Ward-1\\\"," +
                    "\\\"Kuptala:Ward1:1-KA\\\",\\\"Kuptala:Ward1:1-KA:Abdur Rahim Memberer bari-Kholapar\\\"]\"," +
                    "\"hint\":\"শিশুর EPI কেন্দ্র *\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\"," +
                    "\"type\":\"tree\",\"key\":\"HIE_FACILITIES\"},{\"read_only\":\"true\"," +
                    "\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Child's ZEIR ID\"}," +
                    "\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"OpenMRS_ID\",\"value\":\"10177528\"," +
                    "\"hint\":\"নিবন্ধন নম্বর *\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\"," +
                    "\"key\":\"OpenMRS_ID\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"}}," +
                    "{\"value\":\"123123131231231233\",\"openmrs_entity\":\"person_attribute\"," +
                    "\"openmrs_entity_id\":\"Child_Birth_Certificate\",\"hint\":\"শিশুর জন্মনিবন্ধন নাম্বার\"," +
                    "\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"key\":\"Child_Birth_Certificate\"}," +
                    "{\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter a valid name\"}," +
                    "\"value\":\"hatim\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\"," +
                    "\"hint\":\"শিশুর নাম\",\"edit_type\":\"name\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\"," +
                    "\"key\":\"First_Name\"},{\"values\":[\"Male\",\"Female\"],\"v_required\":{\"value\":\"true\"," +
                    "\"err\":\"Please enter the sex\"},\"value\":\"Male\",\"openmrs_entity\":\"person\"," +
                    "\"openmrs_entity_id\":\"gender\",\"hint\":\"শিশুর লিঙ্গ *\",\"openmrs_entity_parent\":\"\"," +
                    "\"type\":\"spinner\",\"key\":\"Sex\"},{\"max_date\":\"today\",\"duration\":{\"label\":\"Age\"}," +
                    "\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"}," +
                    "\"value\":\"01-01-2017\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\"," +
                    "\"min_date\":\"today-5y\",\"hint\":\"শিশুর জন্ম তারিখ *\",\"openmrs_entity_parent\":\"\"," +
                    "\"type\":\"date_picker\",\"expanded\":false,\"key\":\"Date_Birth\"}," +
                    "{\"v_required\":{\"value\":\"false\",\"err\":\"Enter the child's birth weight\"}," +
                    "\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"value\":\"5\"," +
                    "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"hint\":\"জন্মের সময় ওজন (kg) *\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\"," +
                    "\"type\":\"edit_text\",\"key\":\"Birth_Weight\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a " +
                    "valid weight\"}},{\"household_id\":\"1ae4d826-ef15-45d5-a475-37e8b6791260\"," +
                    "\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother\\/guardian's first name\"}," +
                    "\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"Mohila" +
                    " Nutun\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"1593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"মা\\/অবিভাবকের নাম *\"," +
                    "\"look_up\":\"true\",\"edit_type\":\"name\",\"openmrs_entity_parent\":\"\",\"entity_id\":\"mother\"," +
                    "\"type\":\"edit_text\",\"key\":\"Mother_Guardian_First_Name\"},{\"v_max_length\":{\"value\":\"11\"," +
                    "\"err\":\"Please Enter 11 digit Mobile number\"},\"v_min_length\":{\"value\":\"11\",\"err\":\"Please " +
                    "Enter 11 digit Mobile number\"},\"v_regex\":{\"value\":\"(01[5-9][0-9]{8})|s*\",\"err\":\"Number must" +
                    " begin with 015, 016,017,018 or 019 and must be a total of 11 digits in length\"}," +
                    "\"value\":\"01918901991\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"মা\\/অবিভাবকের মোবাইল " +
                    "নম্বর\",\"openmrs_entity_parent\":\"\",\"type\":\"edit_text\",\"key\":\"Mother_Guardian_Number\"," +
                    "\"v_numeric\":{\"value\":\"true\",\"err\":\"Number must begin with 095, 096, or 097 and must be a " +
                    "total of 11 digits in length\"}},{\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter " +
                    "a valid name\"},\"value\":\"mafinar\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"পিতা \\/ অবিভাবকের নাম\"," +
                    "\"edit_type\":\"name\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\"," +
                    "\"type\":\"edit_text\",\"key\":\"Father_Guardian_Name\"},{\"values\":[\"Health facility\",\"Home\"]," +
                    "\"v_required\":{\"value\":true,\"err\":\"Please enter the place of birth\"},\"value\":\"Home\"," +
                    "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"hint\":\"জন্মস্থান ধরণ *\",\"openmrs_choice_ids\":{\"Health " +
                    "facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"key\":\"Place_Birth\"}," +
                    "{\"v_required\":{\"value\":true,\"err\":\"Please enter the birth facility name\"},\"value\":\"\"," +
                    "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"relevance\":{\"step1:Place_Birth\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"Health " +
                    "facility\\\")\"}},\"hint\":\"শিশুটি জন্মগ্রহণ করার সময় কোন স্বাস্থ্য কেন্দ্রের সুবিধা পেয়েছিল? *\"," +
                    "\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\"," +
                    "\"key\":\"Birth_Facility_Name\"},{\"v_required\":{\"value\":true,\"err\":\"Please specify the health " +
                    "facility the child was born in\"},\"value\":\"\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"relevance\":{\"step1:Birth_Facility_Name\":{\"type\":\"string\",\"ex\":\"equalTo(., " +
                    "\\\"[\\\"Home\\\"]\\\")\"}},\"hint\":\"শিশুর জন্ম স্থান * *\",\"edit_type\":\"name\"," +
                    "\"openmrs_entity_parent\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\"," +
                    "\"key\":\"Birth_Facility_Name_Other\"}]},\"encounter_type\":\"Birth Registration\"," +
                    "\"metadata\":{\"phonenumber\":{\"value\":\"15555215554\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_data_type\":\"phonenumber\"},\"subscriberid\":{\"value\":\"310260000000000\"," +
                    "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"subscriberid\"}," +
                    "\"start\":{\"value\":\"2018-01-01 18:00:49\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_data_type\":\"start\"},\"today\":{\"value\":\"01-01-2018\",\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"}," +
                    "\"look_up\":{\"entity_id\":\"mother\",\"value\":\"69d28d10-1b16-4c56-b8c3-5359a6ba77c4\"}," +
                    "\"encounter_location\":\"4c8cb044-7b15-40b7-8ca2-6eceaa6c4e9a\"," +
                    "\"simserial\":{\"value\":\"89014103211118510720\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_data_type\":\"simserial\"},\"end\":{\"value\":\"2018-01-01 18:01:41\"," +
                    "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
                    "\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"end\"}," +
                    "\"deviceid\":{\"value\":\"bded72fcd7e3a083\",\"openmrs_entity\":\"concept\"," +
                    "\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity_parent\":\"\"," +
                    "\"openmrs_data_type\":\"deviceid\"}}}";
    private JSONObject formjson;
    private String bindtype = "ec_child";

    private final String STEP_1_FIELDS = "[\n" +
            "  {\n" +
            "    \"openmrs_entity\": \"entity\",\n" +
            "    \"openmrs_entity_id\": \"entity_id\",\n" +
            "    \"options\": [\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"entity\",\n" +
            "        \"openmrs_entity_id\": \"entity_id\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"text\": \"None\",\n" +
            "        \"value\": \"value\",\n" +
            "        \"key\": \"none\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"text\": \"Don't know\",\n" +
            "        \"key\": \"dont_know\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"text\": \"Primary\",\n" +
            "        \"key\": \"primary\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"text\": \"Secondary\",\n" +
            "        \"key\": \"secondary\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"160292AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"text\": \"Higher\",\n" +
            "        \"key\": \"higher\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"v_required\": {\n" +
            "      \"err\": \"Please specify your education level\",\n" +
            "      \"value\": true\n" +
            "    },\n" +
            "    \"value\":\"Secondary\",\n" +
            "    \"openmrs_entity_parent\": \"\",\n" +
            "    \"label\": \"Highest level of school\",\n" +
            "    \"type\": \"native_radio\",\n" +
            "    \"key\": \"educ_level\",\n" +
            "    \"label_text_style\": \"bold\"\n" +
            "  },\n" +
            "   {\n" +
            "    \"openmrs_entity\": \"person\",\n" +
            "    \"openmrs_entity_id\": \"first_name\",\n" +
            "    \"entity_id\": \"entity_id\",\n" +
            "    \"options\": [\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"entity\",\n" +
            "        \"openmrs_entity_id\": \"entity_id\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"text\": \"None\",\n" +
            "        \"value\": \"value\",\n" +
            "        \"key\": \"none\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"text\": \"Don't know\",\n" +
            "        \"key\": \"dont_know\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"text\": \"Primary\",\n" +
            "        \"key\": \"primary\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"text\": \"Secondary\",\n" +
            "        \"key\": \"secondary\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"openmrs_entity\": \"concept\",\n" +
            "        \"openmrs_entity_id\": \"160292AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"text\": \"Higher\",\n" +
            "        \"key\": \"higher\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"v_required\": {\n" +
            "      \"err\": \"Please specify your education level\",\n" +
            "      \"value\": true\n" +
            "    },\n" +
            "    \"value\":\"primary\",\n" +
            "    \"openmrs_entity_parent\": \"\",\n" +
            "    \"label\": \"Highest level of school\",\n" +
            "    \"type\": \"native_radio\",\n" +
            "    \"key\": \"educ_level_2\",\n" +
            "    \"label_text_style\": \"bold\"\n" +
            "  }\n" +
            "]";

    private String multiStepForm =
            "{\n" + "  \"count\": \"2\",\n" + "  \"step1\": {\n" + "    \"title\": \"Demographic Info\",\n" +
                    "    \"next\": \"step2\",\n" + "    \"fields\":" + STEP_1_FIELDS + "},\n" + "  \"step2\": {\n" + "    \"title\": \"Current Pregnancy\",\n" +
                    "    \"fields\": [\n" + "      {\n" + "        \"key\": \"lmp_known\",\n" +
                    "        \"openmrs_entity_parent\": \"\",\n" + "        \"openmrs_entity\": \"concept\",\n" +
                    "        \"openmrs_entity_id\": \"165258AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                    "        \"type\": \"native_radio\",\n" + "        \"label\": \"LMP known?\",\n" +
                    "        \"label_text_style\": \"bold\",\n" + "        \"max_date\": \"today\",\n" +
                    "        \"options\": [\n" + "          {\n" + "            \"key\": \"yes\",\n" +
                    "            \"text\": \"Yes\",\n" + "            \"openmrs_entity_parent\": \"\",\n" +
                    "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                    "            \"specify_info\": \"specify date\",\n" +
                    "            \"specify_widget\": \"date_picker\",\n" + "            \"max_date\": \"today\",\n" +
                    "            \"min_date\": \"today-280d\"\n" + "          },\n" + "          {\n" +
                    "            \"key\": \"no\",\n" + "            \"text\": \"No\",\n" +
                    "            \"openmrs_entity_parent\": \"\",\n" + "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" + "          }\n" +
                    "        ],\n" + "        \"v_required\": {\n" + "          \"value\": true\n" + "        }\n" +
                    "      }\n" + "    ]\n" + "  }\n" + "}";


    private final String JSON_ARRAY = "{\n" +
            "  \"json_array\":" + STEP_1_FIELDS + "\n" +
            "}";

    private final String JSON_OBJ = "{\n" +
            "  \"dont_know\": {\n" +
            "    \"openmrs_entity\": \"concept\",\n" +
            "    \"openmrs_entity_id\": \"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "    \"openmrs_entity_parent\": \"\",\n" +
            "    \"text\": \"Don't know\"\n" +
            "  },\n" +
            "  \"primary\": {\n" +
            "    \"openmrs_entity\": \"concept\",\n" +
            "    \"openmrs_entity_id\": \"1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "    \"openmrs_entity_parent\": \"\",\n" +
            "    \"text\": \"Primary\"\n" +
            "  },\n" +
            "  \"secondary\": {\n" +
            "    \"openmrs_entity\": \"concept\",\n" +
            "    \"openmrs_entity_id\": \"1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "    \"openmrs_entity_parent\": \"\",\n" +
            "    \"text\": \"Secondary\"\n" +
            "  }\n" +
            "}";

    private String jsonFormNoStepCount =
            "{\n" +
                    "  \"step1\": {\n" +
                    "    \"title\": \"Demographic Info\",\n" +
                    "    \"next\": \"step2\",\n" +
                    "    \"fields\": [\n" +
                    "      {\n" +
                    "        \"key\": \"educ_level\",\n" +
                    "        \"openmrs_entity_parent\": \"\",\n" +
                    "        \"openmrs_entity\": \"concept\",\n" +
                    "        \"openmrs_entity_id\": \"1712AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                    "        \"type\": \"native_radio\",\n" +
                    "        \"label\": \"Highest level of school\",\n" +
                    "        \"label_text_style\": \"bold\",\n" +
                    "        \"options\": [\n" +
                    "          {\n" +
                    "            \"key\": \"none\",\n" +
                    "            \"openmrs_entity_parent\": \"\",\n" +
                    "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                    "            \"text\": \"None\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"key\": \"dont_know\",\n" +
                    "            \"text\": \"Don't know\",\n" +
                    "            \"openmrs_entity_parent\": \"\",\n" +
                    "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"key\": \"primary\",\n" +
                    "            \"text\": \"Primary\",\n" +
                    "            \"openmrs_entity_parent\": \"\",\n" +
                    "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1713AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"key\": \"secondary\",\n" +
                    "            \"text\": \"Secondary\",\n" +
                    "            \"openmrs_entity_parent\": \"\",\n" +
                    "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"1714AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"key\": \"higher\",\n" +
                    "            \"text\": \"Higher\",\n" +
                    "            \"openmrs_entity_parent\": \"\",\n" +
                    "            \"openmrs_entity\": \"concept\",\n" +
                    "            \"openmrs_entity_id\": \"160292AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                    "          }\n" +
                    "        ],\n" +
                    "        \"v_required\": {\n" +
                    "          \"value\": true,\n" +
                    "          \"err\": \"Please specify your education level\"\n" +
                    "        }\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}";

    private String eventFormFields = "[{\"key\":\"contact_reason\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160288AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
            "\"type\":\"native_radio\",\"label\":\"Reason for coming to facility\",\"label_text_style\":\"bold\"," +
            "\"options\":[{\"key\":\"specific_complaint\",\"text\":\"Specific complaint\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]," +
            "\"value\":\"specific_complaint\",\"v_required\":{\"value\":\"true\",\"err\":\"Reason for coming to facility " +
            "is required\"}},{\"key\":\"specific_complaint\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
            "\"type\":\"check_box\",\"label\":\"Specific complaint(s)\",\"label_text_style\":\"bold\"," +
            "\"text_color\":\"#000000\",\"exclusive\":[\"dont_know\",\"none\"]," +
            "\"options\":[{\"key\":\"abnormal_discharge\",\"text\":\"Abnormal vaginal discharge\",\"value\":false," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"123395AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"altered_skin_color\"," +
            "\"text\":\"Jaundice\",\"value\":true,\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"136443AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"value\":\"['altered_skin_color']\"}," +
            "{\"key\":\"specific_complaint_other\",\"openmrs_entity_parent\":\" 5219AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
            "\"type\":\"normal_edit_text\",\"edit_text_style\":\"bordered\",\"hint\":\"Specify\",\"value\":\"Not Sure what" +
            " to add\"},{\"key\":\"repiratory_exam\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\"," +
            "\"label\":\"Respiratory exam\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"extra_rel\":true," +
            "\"has_extra_rel\":\"resOne3\",\"options\":[{\"key\":\"resOne1\",\"text\":\"Not done\"," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"136443AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"resOne2\",\"text\":\"Normal\"," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"136453AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"resOne3\",\"text\":\"Abnormal\"," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"100003AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"specify_info\":\"Specify\"," +
            "\"specify_info_color\":\"#b5b5b5\",\"specify_widget\":\"check_box\"," +
            "\"content_form\":\"child_enrollment_sub_form\",\"content_form_location\":\"\",\"secondary_suffix\":\"test\"," +
            "\"secondary_value\":[{\"key\":\"respiratory_exam_abnormal\",\"type\":\"check_box\"," +
            "\"values\":[\"slow_breathing:Slow breathing:true\",\"other:Other (specify):true\"]," +
            "\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\"," +
            "\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"Place_Birth\"," +
            "\"type\":\"spinner\",\"values\":[\"Health facility\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}," +
            "{\"key\":\"respiratory_exam_radio_button\",\"type\":\"native_radio\",\"values\":[\"1:Not done\"]," +
            "\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_radio_button\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}," +
            "{\"key\":\"respiratory_exam_abnormal_other\",\"type\":\"edit_text\",\"values\":[\"Very Sick\"]," +
            "\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}}]}],\"is-rule-check\":false," +
            "\"value\":\"resOne3\"},{\"key\":\"accordion_blood_type\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"," +
            "\"text\":\"Blood Type test\",\"type\":\"expansion_panel\",\"display_bottom_section\":true," +
            "\"content_form\":\"tests_blood_type_sub_form\",\"container\":\"anc_test\"," +
            "\"value\":[{\"key\":\"respiratory_exam_abnormal\",\"type\":\"check_box\",\"values\":[\"slow_breathing:Slow " +
            "breathing:true\",\"other:Other (specify):true\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "{\"key\":\"respiratory_exam_abnormal\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\"," +
            "\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]},{\"key\":\"Place_Birth\"," +
            "\"type\":\"spinner\",\"values\":[\"Health facility\"],\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}," +
            "{\"key\":\"respiratory_exam_radio_button\",\"type\":\"native_radio\",\"values\":[\"1:Not done\"]," +
            "\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}," +
            "\"value_openmrs_attributes\":[{\"key\":\"respiratory_exam_radio_button\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}]}," +
            "{\"key\":\"respiratory_exam_abnormal_other\",\"type\":\"edit_text\",\"values\":[\"Very Sick\"]," +
            "\"openmrs_attributes\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\"," +
            "\"openmrs_entity_id\":\"165300AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}}]}]";

    private String formMetaData = "{\n" + "    \"start\": {\n" + "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" + "      \"openmrs_data_type\": \"start\",\n" +
            "      \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" + "    },\n" + "    \"end\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" + "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"end\",\n" +
            "      \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" + "    },\n" + "    \"today\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" + "      \"openmrs_entity\": \"encounter\",\n" +
            "      \"openmrs_entity_id\": \"encounter_date\",\n" + "        \"value\":\"2019-03-13\"\n" + "    },\n" +
            "    \"deviceid\": {\n" + "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" + "      \"openmrs_data_type\": \"deviceid\",\n" +
            "      \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"specific437598439875984\"\n" + "    },\n" + "    \"subscriberid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" + "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"subscriberid\",\n" +
            "      \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"4379857437985798437598\"\n" + "    },\n" + "    \"simserial\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" + "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"simserial\",\n" +
            "      \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"326587364756436875643875\"\n" + "    },\n" + "    \"phonenumber\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" + "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"phonenumber\",\n" +
            "      \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value\":\"+3u465632453\"\n" + "    },\n" + "    \"encounter_location\": \"\",\n" +
            "    \"look_up\": {\n" + "      \"entity_id\": \"\",\n" + "      \"value\": \"\"\n" + "    }\n" + "  }";

    private String clientFormFields = "[{\"key\":\"first_name\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First " +
            "name\",\"edit_type\":\"name\",\"value\":\"John\"},{\"key\":\"last_name\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"hint\":\"Last " +
            "name\",\"edit_type\":\"name\",\"value\":\"Doe\"},{\"key\":\"gender\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"hidden\",\"value\":\"F\"}," +
            "{\"key\":\"dob\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\"," +
            "\"openmrs_entity_id\":\"birthdate\",\"type\":\"hidden\",\"value\":\"12-12-1990\"},{\"key\":\"dob_unknown\"," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate_estimated\"," +
            "\"type\":\"check_box\",\"options\":[{\"key\":\"dob_unknown\",\"text\":\"DOB unknown?\"," +
            "\"text_size\":\"18px\",\"value\":\"false\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\"," +
            "\"openmrs_entity_id\":\"\"}]},{\"key\":\"age\",\"openmrs_entity_parent\":\"\"," +
            "\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"age\",\"type\":\"hidden\",\"value\":\"\"}," +
            "{\"key\":\"age_entered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\"," +
            "\"openmrs_entity_id\":\"age\",\"type\":\"edit_text\",\"hint\":\"Age\"},{\"key\":\"home_address\"," +
            "\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address2\"," +
            "\"type\":\"edit_text\",\"hint\":\"Home address\",\"edit_type\":\"name\",\"value\":\"Nairobi\"}]";


    private final String SINGLE_STEP_WITH_SECTIONS = "{\n" +
            "  \"count\": \"1\",\n" +
            "  \"encounter_type\": \"encounter_type\",\n" +
            "  \"entity_id\": \"\",\n" +
            "  \"metadata\": {\n" +
            "    \"start\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"start\",\n" +
            "      \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"end\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"end\",\n" +
            "      \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"today\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"encounter\",\n" +
            "      \"openmrs_entity_id\": \"encounter_date\"\n" +
            "    },\n" +
            "    \"deviceid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"deviceid\",\n" +
            "      \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"subscriberid\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"subscriberid\",\n" +
            "      \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"simserial\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"simserial\",\n" +
            "      \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"phonenumber\": {\n" +
            "      \"openmrs_entity_parent\": \"\",\n" +
            "      \"openmrs_entity\": \"concept\",\n" +
            "      \"openmrs_data_type\": \"phonenumber\",\n" +
            "      \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "    },\n" +
            "    \"encounter_location\": \"\"\n" +
            "  },\n" +
            "  \"step1\": {\n" +
            "    \"title\": \"Step 1\",\n" +
            "    \"next\": \"step2\",\n" +
            "    \"display_back_button\": \"true\",\n" +
            "    \"sections\": [\n" +
            "      {\n" +
            "        \"fields\": [\n" +
            "          {\n" +
            "            \"key\": \"gps\",\n" +
            "            \"type\": \"gps\",\n" +
            "            \"openmrs_entity_parent\": \"\",\n" +
            "            \"openmrs_entity\": \"\",\n" +
            "            \"openmrs_entity_id\": \"\",\n" +
            "            \"value\": \"gps\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"key\": \"lbl_scan_respiratory_specimen_barcode\",\n" +
            "            \"type\": \"label\",\n" +
            "            \"text\": \"Scan respiratory specimen barcode\",\n" +
            "            \"text_color\": \"#000000\",\n" +
            "            \"top_margin\": \"15dp\",\n" +
            "            \"has_bg\": true,\n" +
            "            \"has_drawable_end\": true,\n" +
            "            \"bg_color\": \"#ffffff\",\n" +
            "            \"value\": \"scan_respiratory_specimen_barcode\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"key\": \"lbl_affix_respiratory_specimen_label\",\n" +
            "            \"type\": \"label\",\n" +
            "            \"text\": \"Manually affix label\",\n" +
            "            \"top_margin\": \"30dp\",\n" +
            "            \"text_color\": \"#000000\",\n" +
            "            \"has_bg\": true,\n" +
            "            \"has_drawable_end\": true,\n" +
            "            \"bg_color\": \"#ffffff\",\n" +
            "            \"value\": \"affix_respiratory_specimen_label\"\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        formjson = new JSONObject(formresultJson);
    }

    @Test
    public void assertFillAttributesReturnNotNull() throws Exception {
        assertNotNull(JsonFormUtils.extractAttributes(JsonFormUtils.fields(formjson)));
    }

    @Test
    public void assertFillAttributesWithBindTypeNotReturnNullReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        assertNotNull(JsonFormUtils.extractAttributes(JsonFormUtils.fields(formjson), bindtype));
    }

    @Test
    public void assertExtracIdentifiersReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        assertNotNull(JsonFormUtils.extractIdentifiers(JsonFormUtils.fields(formjson)));
    }

    @Test
    public void assertExtracIdentifiersWithBindTypeReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        assertNotNull(JsonFormUtils.extractIdentifiers(JsonFormUtils.fields(formjson), bindtype));
    }

    @Test
    public void assertExtracAddressesReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        assertNotNull(JsonFormUtils.extractAddresses(JsonFormUtils.fields(formjson)));
    }

    @Test
    public void assertExtracAddressesWithBindTypeReturnNotNull() throws Exception {
        JSONObject formjson = new JSONObject(formresultJson);
        assertNotNull(JsonFormUtils.extractAddresses(JsonFormUtils.fields(formjson), bindtype));
    }

    @Test
    public void formatDateReturnsinRequiredFormat() throws Exception {
        Date date = new Date();
        String dateInFormat = dd_MM_yyyy.format(date);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        String dateinFMT = fmt.format(dd_MM_yyyy.parse(dateInFormat));
        String returnedDateinString = (JsonFormUtils.formatDate(dateInFormat));
        Assert.assertEquals(dateinFMT, returnedDateinString);
    }

    @Test
    public void formatDateReturnsDate() throws Exception {
        SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dd_MM_yyyy.parse("22-05-1988");
        String dateInFormat = dd_MM_yyyy.format(date);
        String dateInFormat2 = yyyy_MM_dd.format(date);
        Date datereturned = (JsonFormUtils.formatDate(dateInFormat, true));
        Date datereturned2 = (JsonFormUtils.formatDate(dateInFormat2, true));
        long diff = date.getTime() - datereturned.getTime();
        long diff2 = date.getTime() - datereturned2.getTime();
        Assert.assertEquals(diff, 0l);
        Assert.assertEquals(diff2, 0l);
    }

    @Test
    public void addObservationAddsObservationToEvent() throws Exception {
        String observationJsonObjectString =
                "{\"v_required\":{\"value\":\"false\",\"err\":\"Enter the child's birth weight\"},\"v_min\":{\"value\":\"0" +
                        ".1\",\"err\":\"Weight must be greater than 0\"},\"value\":\"5\",\"openmrs_entity\":\"concept\"," +
                        "\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"hint\":\"জন্মের সময় ওজন (kg) " +
                        "*\",\"openmrs_entity_parent\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\"," +
                        "\"key\":\"Birth_Weight\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"}}";
        JSONObject observationJsonObject = new JSONObject(observationJsonObjectString);
        Event event = Mockito.mock(Event.class);
        JsonFormUtils.addObservation(event, observationJsonObject);
        Mockito.verify(event, Mockito.atLeastOnce()).addObs(any(Obs.class));
    }

    @Test
    public void addObservationAddsObservationFormSubmissionFieldToEvent() throws Exception {
        String observationJsonObjectString =
                "{\"v_required\":{\"value\":\"false\",\"err\":\"Enter the child's birth weight\"},\"v_min\":{\"value\":\"0" +
                        ".1\",\"err\":\"Weight must be greater than 0\"},\"value\":\"5\",\"openmrs_entity\":\"\"," +
                        "\"openmrs_entity_id\":\"\",\"hint\":\"জন্মের সময় ওজন (kg) *\",\"openmrs_entity_parent\":\"\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"Birth_Weight\"," +
                        "\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"}}";
        JSONObject observationJsonObject = new JSONObject(observationJsonObjectString);
        Event event = Mockito.mock(Event.class);
        JsonFormUtils.addObservation(event, observationJsonObject);
        Mockito.verify(event, Mockito.atLeastOnce()).addObs(any(Obs.class));
    }

    @Test
    public void addObservationAddsCombinedObservationFormSubmissionFieldForCheckboxesToEvent() throws Exception {
        String observationJsonObjectString =
                "      {\n" +
                        "        \"key\": \"testMicrosResult\",\n" +
                        "        \"openmrs_entity_parent\": \"\",\n" +
                        "        \"openmrs_entity\": \"\",\n" +
                        "        \"openmrs_entity_id\": \"\",\n" +
                        "        \"type\": \"check_box\",\n" +
                        "        \"label\": \"Microscopy Result\",\n" +
                        "        \"value\": \"true\",\n" +
                        "        \"options\": [\n" +
                        "          {\n" +
                        "            \"key\": \"Negative\",\n" +
                        "            \"text\": \"Negative\",\n" +
                        "            \"value\": \"false\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"key\": \"PositiveFalciparum\",\n" +
                        "            \"text\": \"Positive - Falciparum\",\n" +
                        "            \"value\": \"false\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"key\": \"PositiveVivax\",\n" +
                        "            \"text\": \"Positive - Vivax\",\n" +
                        "            \"value\": \"true\"\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"key\": \"Fg\",\n" +
                        "            \"text\": \"Fg\",\n" +
                        "            \"value\": \"true\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }";
        JSONObject observationJsonObject = new JSONObject(observationJsonObjectString);
        Event event = Mockito.mock(Event.class);
        JsonFormUtils.addObservation(event, observationJsonObject);
        Mockito.verify(event, Mockito.atLeastOnce()).addObs(obsArgumentCaptor.capture());
        List<Object> values = obsArgumentCaptor.getValue().getValues();
        assertEquals(2, values.size());
        assertTrue(values.contains("Fg"));
        assertTrue(values.contains("Positive - Vivax"));
        assertFalse(values.contains("Positive - Falciparum"));

    }

    @Test
    public void assertfilladdressStartDateAddsStartDate() throws Exception {
        String addressJsonWithStartDateString =
                "{\"value\":\"2017-05-22\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"startDate\"," +
                        "\"hint\":\"address of household start date\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"date\",\"type\":\"edit_text\",\"key\":\"address_start_date\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithStartDateString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getStartDate().getTime() -
                org.smartregister.clientandeventmodel.DateUtil.parseDate("2017-05-22").getTime(), 0);
    }

    @Test
    public void assertfilladdressEndDateAddsEndDate() throws Exception {
        String addressJsonWithEndDateString =
                "{\"value\":\"2017-05-22\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"end_date\"," +
                        "\"hint\":\"address of household end date\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"date\",\"type\":\"edit_text\",\"key\":\"address_end_date\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithEndDateString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getEndDate().getTime() -
                org.smartregister.clientandeventmodel.DateUtil.parseDate("2017-05-22").getTime(), 0);
    }

    @Test
    public void assertfilladdressLongitudeAddsLongitude() throws Exception {
        String addressJsonWithLongitudeString =
                "{\"value\":\"34.044494\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"longitude\"," +
                        "\"hint\":\"address of household longitude\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"address_longitude\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithLongitudeString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getLongitude(), "34.044494");
    }

    @Test
    public void assertfilladdresslatitudeAddslatitude() throws Exception {
        String addressJsonWithStartlatitudeString =
                "{\"value\":\"34.044494\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"latitude\"," +
                        "\"hint\":\"address of household latitude\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"address_latitude\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithStartlatitudeString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getLatitude(), "34.044494");
    }

    @Test
    public void assertfilladdressGeopointAddsGeopoint() throws Exception {
        String addressJsonWithGeopointString =
                "{\"value\":\"34.044494 -84.695704 4 76 = lat lon alt prec\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"geopoint\",\"hint\":\"address of household geopoint\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"address_geopoint\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithGeopointString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getGeopoint(),
                "34.044494 -84.695704 4 76 = lat lon alt prec");
    }

    @Test
    public void assertfilladdressPostal_codeAddsPostal_code() throws Exception {
        String addressJsonWithStartPostal_code =
                "{\"value\":\"4021\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"postal_code\"," +
                        "\"hint\":\"address of household postal_code\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"postal_code\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithStartPostal_code), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getPostalCode(), "4021");
    }

    @Test
    public void assertfilladdressSub_townAddsSub_town() throws Exception {
        String addressJsonWithSub_townString =
                "{\"value\":\"Kotwali\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"sub_town\"," +
                        "\"hint\":\"address of household sub_town\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"sub_town\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithSub_townString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getSubTown(), "Kotwali");
    }

    @Test
    public void assertfilladdressTownAddsTown() throws Exception {
        String addressJsonWithTownString =
                "{\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"town\"," +
                        "\"hint\":\"address of household town\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"town\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithTownString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getTown(), "Chittagong");
    }

    @Test
    public void assertfilladdressSub_districtAddsSub_district() throws Exception {
        String addressJsonWithsub_districtString =
                "{\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"sub_district\"," +
                        "\"hint\":\"address of household sub_district\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"sub_district\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithsub_districtString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getSubDistrict(), "Chittagong");
    }

    @Test
    public void assertfilladdressDistrictAddsDistrict() throws Exception {
        String addressJsonWithDistrictString =
                "{\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"district\"," +
                        "\"hint\":\"address of household district\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"district\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithDistrictString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCountyDistrict(), "Chittagong");
    }

    @Test
    public void assertfilladdressCityVillageAddsCityVillage() throws Exception {
        String addressJsonWithCityVillageString =
                "{\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"cityVillage\"," +
                        "\"hint\":\"address of household cityVillage\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"cityVillage\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithCityVillageString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCityVillage(), "Chittagong");
    }

    @Test
    public void assertfilladdressStateAddsState() throws Exception {
        String addressJsonWithStateString =
                "{\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"state\"," +
                        "\"hint\":\"address of household state\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"state\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithStateString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getStateProvince(), "Chittagong");
    }

    @Test
    public void assertfilladdressCountryAddsCountry() throws Exception {
        String addressJsonWithCountryString =
                "{\"value\":\"Bangladesh\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"country\"," +
                        "\"hint\":\"address of household country\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"country\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillAddressFields(new JSONObject(addressJsonWithCountryString), addressHashMap);
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCountry(), "Bangladesh");
    }

    @Test
    public void assertfillSubformaddressStartDateAddsStartDate() throws Exception {
        String addressJsonWithStartDateString =
                "{\"entity_id\":\"mother\",\"value\":\"2017-05-22\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"startDate\",\"hint\":\"address of household start date\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"date\"," +
                        "\"type\":\"edit_text\",\"key\":\"address_start_date\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithStartDateString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getStartDate().getTime() -
                org.smartregister.clientandeventmodel.DateUtil.parseDate("2017-05-22").getTime(), 0);
    }

    @Test
    public void assertfillSubFormaddressEndDateAddsEndDate() throws Exception {
        String addressJsonWithEndDateString =
                "{\"entity_id\":\"mother\",\"value\":\"2017-05-22\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"end_date\",\"hint\":\"address of household end date\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"date\"," +
                        "\"type\":\"edit_text\",\"key\":\"address_end_date\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithEndDateString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getEndDate().getTime() -
                org.smartregister.clientandeventmodel.DateUtil.parseDate("2017-05-22").getTime(), 0);
    }

    @Test
    public void assertfillSubFormaddressLongitudeAddsLongitude() throws Exception {
        String addressJsonWithLongitudeString =
                "{\"entity_id\":\"mother\",\"value\":\"34.044494\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"longitude\",\"hint\":\"address of household longitude\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"address_longitude\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithLongitudeString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getLongitude(), "34.044494");
    }

    @Test
    public void assertfillSubFormaddresslatitudeAddslatitude() throws Exception {
        String addressJsonWithStartlatitudeString =
                "{\"entity_id\":\"mother\",\"value\":\"34.044494\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"latitude\",\"hint\":\"address of household latitude\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"address_latitude\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithStartlatitudeString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getLatitude(), "34.044494");
    }

    @Test
    public void assertfillSubFormaddressGeopointAddsGeopoint() throws Exception {
        String addressJsonWithGeopointString =
                "{\"entity_id\":\"mother\",\"value\":\"34.044494 -84.695704 4 76 = lat lon alt prec\"," +
                        "\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"geopoint\",\"hint\":\"address of " +
                        "household geopoint\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"address_geopoint\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithGeopointString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getGeopoint(),
                "34.044494 -84.695704 4 76 = lat lon alt prec");
    }

    @Test
    public void assertfillSubFormaddressPostal_codeAddsPostal_code() throws Exception {
        String addressJsonWithStartPostal_code =
                "{\"entity_id\":\"mother\",\"value\":\"4021\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"postal_code\",\"hint\":\"address of household postal_code\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"postal_code\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithStartPostal_code), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getPostalCode(), "4021");
    }

    @Test
    public void assertfillSubFormaddressSub_townAddsSub_town() throws Exception {
        String addressJsonWithSub_townString =
                "{\"entity_id\":\"mother\",\"value\":\"Kotwali\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"sub_town\",\"hint\":\"address of household sub_town\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"sub_town\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithSub_townString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getSubTown(), "Kotwali");
    }

    @Test
    public void assertfillSubFormaddressTownAddsTown() throws Exception {
        String addressJsonWithTownString =
                "{\"entity_id\":\"mother\",\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"town\",\"hint\":\"address of household town\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"town\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithTownString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getTown(), "Chittagong");
    }

    @Test
    public void assertfillSubformaddressSub_districtAddsSub_district() throws Exception {
        String addressJsonWithsub_districtString =
                "{\"entity_id\":\"mother\",\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"sub_district\",\"hint\":\"address of household sub_district\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"sub_district\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithsub_districtString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getSubDistrict(), "Chittagong");
    }

    @Test
    public void assertfillSubformaddressDistrictAddsDistrict() throws Exception {
        String addressJsonWithDistrictString =
                "{\"entity_id\":\"mother\",\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"district\",\"hint\":\"address of household district\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"district\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithDistrictString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCountyDistrict(), "Chittagong");
    }

    @Test
    public void assertfillSubformaddressCityVillageAddsCityVillage() throws Exception {
        String addressJsonWithCityVillageString =
                "{\"entity_id\":\"mother\",\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"cityVillage\",\"hint\":\"address of household cityVillage\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"cityVillage\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithCityVillageString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCityVillage(), "Chittagong");
    }

    @Test
    public void assertfillSubformaddressStateAddsState() throws Exception {
        String addressJsonWithStateString =
                "{\"entity_id\":\"mother\",\"value\":\"Chittagong\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"state\",\"hint\":\"address of household state\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"state\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithStateString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getStateProvince(), "Chittagong");
    }

    @Test
    public void assertfillSubformaddressCountryAddsCountry() throws Exception {
        String addressJsonWithCountryString =
                "{\"entity_id\":\"mother\",\"value\":\"Bangladesh\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"country\",\"hint\":\"address of household country\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"country\"}";
        HashMap<String, Address> addressHashMap = new HashMap<String, Address>();
        JsonFormUtils.fillSubFormAddressFields(new JSONObject(addressJsonWithCountryString), addressHashMap, "mother");
        assertTrue(addressHashMap.size() > 0);
        Assert.assertEquals(addressHashMap.get("usual_residence").getCountry(), "Bangladesh");
    }

    @Test
    public void assertMergeWillMergeJsonObjects() throws Exception {
        String addressJsonWithCountryString =
                "{\"entity_id\":\"mother\",\"value\":\"Bangladesh\",\"openmrs_entity\":\"person_address\"," +
                        "\"openmrs_entity_id\":\"country\",\"hint\":\"address of household country\"," +
                        "\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_data_type\":\"text\"," +
                        "\"type\":\"edit_text\",\"key\":\"country\"}";
        String originalString =
                "{\"value\":\"Bangladesh\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"country\"," +
                        "\"hint\":\"address of household country\",\"openmrs_entity_parent\":\"usual_residence\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"key\":\"country\"}";
        JSONObject updatedExpected = new JSONObject(addressJsonWithCountryString);
        JSONObject original = new JSONObject(originalString);
        JSONObject toMerge = new JSONObject();
        toMerge.put("entity_id", "mother");
        JSONObject updated = JsonFormUtils.merge(original, toMerge);
        assertTrue(areEqual(updated, updatedExpected));

    }

    @Test
    public void testGetMultiStepFormFields() throws JSONException {
        assertNotNull(multiStepForm);

        JSONObject jsonForm = new JSONObject(multiStepForm);
        assertNotNull(jsonForm);

        JSONArray formFields = JsonFormUtils.getMultiStepFormFields(jsonForm);
        assertNotNull(formFields);
    }

    @Test
    public void testGetMultiStepFormFieldsWithoutFormCount() throws JSONException {
        JSONObject jsonForm = new JSONObject(jsonFormNoStepCount);
        assertNotNull(jsonForm);

        JSONArray formFields = JsonFormUtils.getMultiStepFormFields(jsonForm);
        assertNotNull(formFields);
        Assert.assertEquals(1, formFields.length());
    }

    @Test
    public void testEventCreationForForm() throws JSONException {
        JSONArray fields = new JSONArray(eventFormFields);
        assertNotNull(fields);

        JSONObject metadata = new JSONObject(formMetaData);
        assertNotNull(metadata);

        FormTag formTag = new FormTag();
        formTag.providerId = "52c9534da60e66bfc6d1641b3359894c";
        formTag.appVersion = 1;
        formTag.databaseVersion = 20;

        assertNotNull(formTag);

        Event event =
                JsonFormUtils.createEvent(fields, metadata, formTag, "97dc48f681ddcf188b2758fba89635fe", "Quick Check", "");
        assertNotNull(event.getEventType());
        Assert.assertEquals(event.getObs().size(), 20);
        Assert.assertEquals(event.getProviderId(), "52c9534da60e66bfc6d1641b3359894c");
    }

    @Test
    public void testClientCreation() throws JSONException {
        JSONArray fields = new JSONArray(clientFormFields);
        assertNotNull(fields);

        FormTag formTag = new FormTag();
        formTag.providerId = "52c9534da60e66bfc6d1641b3359894c";
        formTag.appVersion = 1;
        formTag.databaseVersion = 20;

        assertNotNull(formTag);
        Client client = JsonFormUtils.createBaseClient(fields, formTag, "97dc48f681ddcf188b2758fba89635fe");
        Assert.assertEquals(client.getGender(), "F");
        Assert.assertEquals(client.getFirstName(), "John");
    }

    @Test
    public void testConvertToOpenMRSDate() {
        String date = "12-12-2019";
        String openmrsDate = JsonFormUtils.convertToOpenMRSDate(date);
        Assert.assertEquals(openmrsDate, "2019-12-12");
    }

    @Test
    public void testGetFieldJsonObject() throws JSONException {
        JSONArray fields = new JSONArray(clientFormFields);
        assertNotNull(fields);

        JSONObject field = JsonFormUtils.getFieldJSONObject(fields, "first_name");
        assertNotNull(field);
        assertTrue(field.has("value"));
        Assert.assertEquals(field.get("value"), "John");
    }

    @Test
    public void testAddMultiSelectListObservations() throws Exception {
        Event event = new Event();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put(OPENMRS_ENTITY_ID, "entityId");
        jsonObject1.put(OPENMRS_ENTITY, "concept");
        jsonObject1.put(AllConstants.TEXT, "text");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put(OPENMRS_ENTITY_ID, "entityId2");
        jsonObject2.put(OPENMRS_ENTITY, "concept");
        jsonObject2.put(AllConstants.TEXT, "text2");

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject1);
        jsonArray.put(jsonObject2);
        jsonObject.put(JsonFormUtils.KEY, "key");
        jsonObject.put(JsonFormUtils.OPENMRS_ENTITY_PARENT, "parentCode");

        jsonObject.put(JsonFormUtils.VALUE, jsonArray);
        Whitebox.invokeMethod(JsonFormUtils.class, "addMultiSelectListObservations", event, jsonObject);

        Assert.assertEquals(2, event.getObs().size());
        Obs obs = event.getObs().get(0);
        Assert.assertEquals("key", obs.getFormSubmissionField());
        Assert.assertEquals("entityId", obs.getValue());
        Assert.assertEquals("text", obs.getHumanReadableValues().get(0));
        Assert.assertEquals("concept", obs.getFieldType());
        Assert.assertEquals("parentCode", obs.getParentCode());

        Obs obs2 = event.getObs().get(1);
        Assert.assertEquals("key", obs2.getFormSubmissionField());
        Assert.assertEquals("entityId2", obs2.getValue());
        Assert.assertEquals("concept", obs2.getFieldType());
        Assert.assertEquals("parentCode", obs2.getParentCode());
        Assert.assertEquals("text2", obs2.getHumanReadableValues().get(0));
    }

    public static boolean areEqual(Object ob1, Object ob2) throws JSONException {
        Object obj1Converted = convertJsonElement(ob1);
        Object obj2Converted = convertJsonElement(ob2);
        return obj1Converted.equals(obj2Converted);
    }

    private static Object convertJsonElement(Object elem) throws JSONException {
        if (elem instanceof JSONObject) {
            JSONObject obj = (JSONObject) elem;
            Iterator<String> keys = obj.keys();
            Map<String, Object> jsonMap = new HashMap<>();
            while (keys.hasNext()) {
                String key = keys.next();
                jsonMap.put(key, convertJsonElement(obj.get(key)));
            }
            return jsonMap;
        } else if (elem instanceof JSONArray) {
            JSONArray arr = (JSONArray) elem;
            Set<Object> jsonSet = new HashSet<>();
            for (int i = 0; i < arr.length(); i++) {
                jsonSet.add(convertJsonElement(arr.get(i)));
            }
            return jsonSet;
        } else {
            return elem;
        }
    }

    @Test
    public void testSetGlobalCheckBoxPropertyShouldSetCorrectProperty() throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONObject metadata = new JSONObject();
        metadata.put(SAVE_ALL_CHECKBOX_OBS_AS_ARRAY, true);
        jsonObject.put(AllConstants.TYPE, AllConstants.CHECK_BOX);

        Whitebox.invokeMethod(JsonFormUtils.class, "setGlobalCheckBoxProperty", metadata, jsonObject);
        assertTrue(jsonObject.optBoolean(SAVE_OBS_AS_ARRAY));

        jsonObject.remove(SAVE_OBS_AS_ARRAY);
        metadata.put(SAVE_ALL_CHECKBOX_OBS_AS_ARRAY, false);
        Whitebox.invokeMethod(JsonFormUtils.class, "setGlobalCheckBoxProperty", metadata, jsonObject);
        assertFalse(jsonObject.optBoolean(SAVE_OBS_AS_ARRAY));

        jsonObject.remove(SAVE_OBS_AS_ARRAY);
        metadata.put(SAVE_ALL_CHECKBOX_OBS_AS_ARRAY, true);
        jsonObject.put(AllConstants.TYPE, AllConstants.TEXT);
        Whitebox.invokeMethod(JsonFormUtils.class, "setGlobalCheckBoxProperty", metadata, jsonObject);
        assertFalse(jsonObject.optBoolean(SAVE_OBS_AS_ARRAY));
    }

    @Test
    public void testGetSingleStepFormFieldsShouldGetStep1Fields() throws JSONException {
        JSONArray actualFieldsJsonArr = JsonFormUtils.getSingleStepFormfields(new JSONObject(multiStepForm));
        assertNotNull(actualFieldsJsonArr);
        String expectedFieldsJsonArr = new JSONArray(STEP_1_FIELDS).toString();
        assertEquals(actualFieldsJsonArr.toString(), expectedFieldsJsonArr);
    }

    @Test
    public void testToJSONObjectShouldReturnCorrectJsonObject() throws JSONException {
        assertNull(JsonFormUtils.toJSONObject(null));

        JSONObject actualJsonObj = JsonFormUtils.toJSONObject(multiStepForm);
        assertNotNull(actualJsonObj);
        assertEquals(actualJsonObj.toString(), new JSONObject(multiStepForm).toString());
    }

    @Test
    public void testIsBlankJsonArrayShouldReturnCorrectJsonArrayType() throws Exception {
        JSONArray jsonArray = new JSONArray();
        assertTrue(Whitebox.invokeMethod(JsonFormUtils.class, "isBlankJsonArray", (JSONArray) null));
        assertTrue(Whitebox.invokeMethod(JsonFormUtils.class, "isBlankJsonArray", jsonArray));
        jsonArray.put(1);
        jsonArray.put(2);
        assertFalse(Whitebox.invokeMethod(JsonFormUtils.class, "isBlankJsonArray", jsonArray));
    }

    @Test
    public void testValueShouldGetCorrectValue() throws JSONException {
        String value = JsonFormUtils.value(new JSONArray(STEP_1_FIELDS), "entity", "entity_id");
        assertEquals("Secondary", value);
        assertNull(JsonFormUtils.value(new JSONArray(STEP_1_FIELDS), "entity1", "entity_id1"));
    }

    @Test
    public void testGetFieldValueShouldGetCorrectValue() throws JSONException {
        String value = JsonFormUtils.getFieldValue(new JSONArray(STEP_1_FIELDS), "educ_level");
        assertEquals("Secondary", value);
        assertNull(JsonFormUtils.getFieldValue(new JSONArray(STEP_1_FIELDS), "school_level"));
    }

    @Test
    public void testGetJSONObjectShouldGetCorrectObject() throws JSONException {
        JSONArray jsonArray = new JSONArray(STEP_1_FIELDS);
        JSONObject actualJsonObj = JsonFormUtils.getJSONObject(jsonArray, 1);
        assertEquals(jsonArray.get(1), actualJsonObj);
        assertNull(JsonFormUtils.getJSONObject(jsonArray, 100));
    }

    @Test
    public void testGetJSONArrayShouldGetCorrectJSONArray() throws JSONException {
        JSONObject jsonObject = new JSONObject(JSON_ARRAY);
        assertEquals(jsonObject.getJSONArray("json_array"), JsonFormUtils.getJSONArray(jsonObject, "json_array"));
        assertNull(JsonFormUtils.getJSONArray(jsonObject, "json_array_1"));
    }

    @Test
    public void testGetJSONObjectByFieldShouldGetCorrectObject() throws JSONException {
        JSONObject jsonObject = new JSONObject(JSON_OBJ);
        JSONObject actualJsonObj = JsonFormUtils.getJSONObject(jsonObject, "secondary");
        assertEquals(jsonObject.optJSONObject("secondary"), actualJsonObj);
        assertNull(JsonFormUtils.getJSONObject(jsonObject, "non_existent_key"));
    }

    @Test
    public void testisBlankJsonObjectShouldReturnCorrectJsonObjectType() throws Exception {
        JSONObject jsonObject = new JSONObject();
        assertTrue(Whitebox.invokeMethod(JsonFormUtils.class, "isBlankJsonObject", (JSONObject) null));
        assertTrue(Whitebox.invokeMethod(JsonFormUtils.class, "isBlankJsonObject", jsonObject));
        jsonObject.put("fields", new JSONArray(STEP_1_FIELDS));
        jsonObject.put("json_obj", new JSONObject(JSON_OBJ));
        assertFalse(Whitebox.invokeMethod(JsonFormUtils.class, "isBlankJsonObject", jsonObject));
    }

    @Test
    public void testGetStringShouldGetCorrectString() throws JSONException {
        String text = JsonFormUtils.getString(new JSONObject(JSON_OBJ).optJSONObject("primary").toString(), AllConstants.TEXT);
        assertEquals("Primary", text);
        assertNull(JsonFormUtils.getString("", AllConstants.TEXT));
    }

    @Test
    public void testGetLongShouldGetCorrectLong() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("long_key", 900);
        long longVal = JsonFormUtils.getLong(jsonObject, "long_key");
        assertEquals(900, longVal);

        jsonObject = new JSONObject();
        assertNull(JsonFormUtils.getLong(jsonObject, "long_key"));
    }

    @Test
    public void testAddToJSONObjectShouldAddFieldToJsonObject() {
        JSONObject jsonObject = new JSONObject();
        JsonFormUtils.addToJSONObject(jsonObject, "key", "value");
        assertEquals("value", jsonObject.optString("key"));
    }

    @Test
    public void testGetNamesShouldGetAllKeysInJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", "value1");
        jsonObject.put("key2", "value2");
        jsonObject.put("key3", "value3");
        jsonObject.put("key4", "value4");
        jsonObject.put("key5", "value5");

        String[] keys = JsonFormUtils.getNames(jsonObject);
        assertEquals(keys.length, jsonObject.length());
        for (int i = 0; i < keys.length; i++) {
            assertTrue(jsonObject.has(keys[i]));
        }
    }

    @Test
    public void testMergeShouldCorrectlyUpdateValues() throws JSONException {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("key1", "value1");
        jsonObject1.put("key2", "value2");
        jsonObject1.put("key3", "value3");
        jsonObject1.put("key4", "value4");
        jsonObject1.put("key5", "value5");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("key1", "value6");
        jsonObject2.put("key3", "value7");
        jsonObject2.put("key5", "value8");

        JSONObject mergedJsonObj = JsonFormUtils.merge(jsonObject1, jsonObject2);
        for (int i = 1; i <= jsonObject1.length(); i++) {
            String key = "key" + i;
            if (i % 2 == 0) {
                assertEquals(jsonObject1.get(key), mergedJsonObj.get(key));
            } else {
                assertEquals(jsonObject2.get(key), mergedJsonObj.get(key));
            }
        }
    }

    @Test
    public void testFormatDateShouldCorrectlyFormatDateStr() throws ParseException {
        String formattedDate = JsonFormUtils.formatDate("02-44-2970");
        assertEquals("2973-08-02T00:00:00.000Z", formattedDate);
    }

    @Test
    public void testFormatDateShouldCorrectlyFormatDate() throws ParseException {
        Date formattedDate = JsonFormUtils.formatDate("02-12-2970", false);
        assertEquals(dd_MM_yyyy.parse("02-12-2970"), formattedDate);

        formattedDate = JsonFormUtils.formatDate("02-12-2970", false);
        assertEquals(yyyyMMdd.parse("2970-12-02").toString(), formattedDate.toString());

        formattedDate = JsonFormUtils.formatDate("2970-12-02 01:12:34", false);
        assertEquals(yyyyMMddHHmmss.parse("2970-12-02 01:12:34").toString(), formattedDate.toString());
    }

    @Test
    public void testGetFieldValueFromJsonObjShouldGetCorrectValue() {
        assertEquals("Secondary", JsonFormUtils.getFieldValue(multiStepForm, "educ_level"));
        assertEquals("primary", JsonFormUtils.getFieldValue(multiStepForm, "educ_level_2"));
    }

    @Test
    public void testSectionFieldsShouldGetCorrectSectionMap() throws JSONException {
        Map<String, String> sectionsMap = JsonFormUtils.sectionFields(new JSONObject(SINGLE_STEP_WITH_SECTIONS));
        assertEquals("gps", sectionsMap.get("gps"));
        assertNotNull("scan_respiratory_specimen_barcode", sectionsMap.get("lbl_scan_respiratory_specimen_barcode"));
        assertNotNull("affix_respiratory_specimen_label", sectionsMap.get("lbl_affix_respiratory_specimen_label"));
    }

    @Test
    public void testFillSubFormIdentifiersShouldAddCorrectIdentifiers() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ENTITY_ID, "entity_id");
        jsonObject.put(VALUE, "value");
        jsonObject.put(OPENMRS_ENTITY, PERSON_INDENTIFIER);
        jsonObject.put(OPENMRS_ENTITY_ID, "openmrs_entity_id");
        Map<String, String> pids = new HashMap<>();
        JsonFormUtils.fillSubFormIdentifiers(pids, jsonObject, "entity_id");
        assertEquals(1, pids.size());
        assertEquals("value", pids.get(OPENMRS_ENTITY_ID));
    }

    @Test
    public void testFillSubFormAttributesShouldFillCorrectAttributes() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ENTITY_ID, "entity_id");
        jsonObject.put(VALUE, "value");
        jsonObject.put(OPENMRS_ENTITY, PERSON_ATTRIBUTE);
        jsonObject.put(OPENMRS_ENTITY_ID, "openmrs_entity_id");
        Map<String, Object> patientAttributes = new HashMap<>();
        JsonFormUtils.fillSubFormAttributes(patientAttributes, jsonObject, "entity_id");
        assertEquals(1, patientAttributes.size());
        assertEquals("value", patientAttributes.get(OPENMRS_ENTITY_ID));
    }

    @Test
    public void testGetSubFormFieldValueShouldGetCorrectValue() throws JSONException {
        JSONArray jsonArray = new JSONArray(STEP_1_FIELDS);
        String value = JsonFormUtils.getSubFormFieldValue(jsonArray, FormEntityConstants.Person.first_name, "entity_id");
        assertEquals("primary", value);
    }

    @Test
    public void testCreateObservationShouldCreateCorrectObservation() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY, "key");
        Event event = new Event();
        List values = new ArrayList<>();
        Map<String, Object> keyValPairs = new HashMap<>();
        values.add("value1");
        values.add("value2");
        Whitebox.invokeMethod(JsonFormUtils.class, "createObservation", event, jsonObject, keyValPairs, values);
        List<Obs> obsList = event.getObs();
        assertEquals(1, obsList.size());
        Obs obs = obsList.get(0);
        assertEquals("formsubmissionField", obs.getFieldType());
        assertEquals("text", obs.getFieldDataType());
        assertEquals("key", obs.getFormSubmissionField());
        assertEquals("key", obs.getFieldCode());
        assertFalse(obs.isSaveObsAsArray());
        assertEquals(values, obs.getValues());
        assertEquals(keyValPairs, obs.getKeyValPairs());
    }

    @Test
    public void addObservationAddsGpsObservationsToEvent() throws Exception {
        String observationJsonObjectString =
                "{\"value\":\"-2.4535 37.324 0.2 0.3\",\"openmrs_entity\":\"\"," +
                        "\"openmrs_entity_id\":\"\",\"openmrs_entity_parent\":\"\"," +
                        "\"openmrs_data_type\":\"text\",\"type\":\"gps\",\"key\":\"gps\"}";
        JSONObject observationJsonObject = new JSONObject(observationJsonObjectString);
        Event event = Mockito.spy(new Event());
        JsonFormUtils.addObservation(event, observationJsonObject);
        Mockito.verify(event, Mockito.times(5)).addObs(any(Obs.class));
        String values[] = observationJsonObject.optString(VALUE).split(" ");
        String latitudeKey = observationJsonObject.getString(KEY) + "_" + AllConstants.GpsConstants.LATITUDE;
        String longitudeKey = observationJsonObject.getString(KEY) + "_" + AllConstants.GpsConstants.LONGITUDE;
        String altitudeKey = observationJsonObject.getString(KEY) + "_" + AllConstants.GpsConstants.ALTITUDE;
        String accuracyKey = observationJsonObject.getString(KEY) + "_" + AllConstants.GpsConstants.ACCURACY;
        List<String> formSubmissionFields = Arrays.asList(latitudeKey, longitudeKey, accuracyKey, altitudeKey, "gps");
        for (Obs obs : event.getObs()) {
            String formSubmissionField = obs.getFormSubmissionField();
            assertTrue(formSubmissionFields.contains(formSubmissionField));
            if (formSubmissionField.equals(latitudeKey)) {
                assertEquals(values[0], obs.getValues().get(0));
            } else if (formSubmissionField.equals(longitudeKey)) {
                assertEquals(values[1], obs.getValues().get(0));
            } else if (formSubmissionField.equals(altitudeKey)) {
                assertEquals(values[2], obs.getValues().get(0));
            } else if (formSubmissionField.equals(accuracyKey)) {
                assertEquals(values[3], obs.getValues().get(0));
            }
        }
    }

    @Test
    public void testCreateObservationForNativeRadioShouldAddKeyValuePairsToObservation() throws Exception {
        String strJsonObj = "{\"key\":\"not_good\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"Quel est le problème avec ce produit ?\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"worn_broken\",\"text\":\"Usé, endommagé, ou cassé\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"expired\",\"text\":\"Expiré\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"parts_missing\",\"text\":\"Pièces manquantes\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"},{\"key\":\"other\",\"text\":\"Autre (préciser)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\"}],\"v_required\":{\"value\":true,\"err\":\"Ce champ est requis\"},\"relevance\":{\"step1:flag_problem\":{\"ex-checkbox\":[{\"or\":[\"not_good\"]}]}},\"is-rule-check\":false,\"is_visible\":true,\"value\":\"worn_broken\"}";
        JSONObject jsonObject = new JSONObject(strJsonObj);
        Event event = new Event();
        String value = "worn_broken";
        Whitebox.invokeMethod(JsonFormUtils.class, "createObservation", event, jsonObject, value);
        List<Obs> obsList = event.getObs();
        assertEquals(1, obsList.size());
        Obs obs = obsList.get(0);
        assertNotNull(obs.getKeyValPairs());
        assertEquals(1, obs.getKeyValPairs().size());
        assertNotNull(obs.getKeyValPairs().get(value));
        assertEquals("Usé, endommagé, ou cassé", obs.getKeyValPairs().get(value));
        assertEquals(value, obs.getValue());
    }
}
