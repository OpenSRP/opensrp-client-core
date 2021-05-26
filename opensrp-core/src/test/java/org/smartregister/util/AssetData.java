package org.smartregister.util;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 17-03-2020.
 */
public class AssetData {

    public static final String ec_client_classification_json = "{\n" +
            "  \"case_classification_rules\": [\n" +
            "    {\n" +
            "      \"comment\": \"Child: This rule checks whether a given case belongs to Child register\",\n" +
            "      \"rule\": {\n" +
            "        \"type\": \"event\",\n" +
            "        \"fields\": [\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"New Woman Registration\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_client\",\n" +
            "              \"ec_mother_details\"\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"Birth Registration\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_client\",\n" +
            "              \"ec_child_details\"\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"Update Birth Registration\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_client\",\n" +
            "              \"ec_child_details\"\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"ANC Close\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_mother_details\"\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"ANC Registration\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_client\",\n" +
            "              \"ec_mother_details\"\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"Update ANC Registration\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_client\",\n" +
            "              \"ec_mother_details\"\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"Visit\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_mother_details\"\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"Opd Registration\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_client\"\n" +
            "            ]\n" +
            "          }\n" +
            "\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}
