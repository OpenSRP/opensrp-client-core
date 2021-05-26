package org.smartregister.util;

/**
 * Created by ndegwamartin on 02/06/2020.
 */
public final class LoginResponseTestData {

    public static final String USER_DETAILS_REQUEST_SERVER_RESPONSE = "{\n" +
            "   \"locations\":{\n" +
            "      \"locationsHierarchy\":{\n" +
            "         \"map\":{\n" +
            "            \"583j4bve4-9a36-459a-99fc-bc0356d99999\":{\n" +
            "               \"id\":\"583j4bve4-9a36-459a-99fc-bc0356d99999\",\n" +
            "               \"label\":\"Kenya\",\n" +
            "               \"node\":{\n" +
            "                  \"locationId\":\"583j4bve4-9a36-459a-99fc-bc0356d99999\",\n" +
            "                  \"name\":\"Kenya\",\n" +
            "                  \"tags\":[\n" +
            "                     \"Country\"\n" +
            "                  ],\n" +
            "                  \"voided\":false\n" +
            "               },\n" +
            "               \"children\":{\n" +
            "                  \"3x3tsh58-e6db-4dsa-8be1-e860ec299999\":{\n" +
            "                     \"id\":\"3x3tsh58-e6db-4dsa-8be1-e860ec299999\",\n" +
            "                     \"label\":\"Nairobi\",\n" +
            "                     \"node\":{\n" +
            "                        \"locationId\":\"3x3tsh58-e6db-4dsa-8be1-e860ec299999\",\n" +
            "                        \"name\":\"Nairobi\",\n" +
            "                        \"parentLocation\":{\n" +
            "                           \"locationId\":\"583j4bve4-9a36-459a-99fc-bc0356d99999\",\n" +
            "                           \"voided\":false\n" +
            "                        },\n" +
            "                        \"tags\":[\n" +
            "                           \"Province\"\n" +
            "                        ],\n" +
            "                        \"voided\":false\n" +
            "                     },\n" +
            "                     \"children\":{\n" +
            "                        \"93437b43-485d-44df-8eaf-434449579999\":{\n" +
            "                           \"id\":\"93437b43-485d-44df-8eaf-434449579999\",\n" +
            "                           \"label\":\"Kasarani\",\n" +
            "                           \"node\":{\n" +
            "                              \"locationId\":\"93437b43-485d-44df-8eaf-434449579999\",\n" +
            "                              \"name\":\"Kasarani\",\n" +
            "                              \"parentLocation\":{\n" +
            "                                 \"locationId\":\"3x3tsh58-e6db-4dsa-8be1-e860ec299999\",\n" +
            "                                 \"voided\":false\n" +
            "                              },\n" +
            "                              \"tags\":[\n" +
            "                                 \"District\"\n" +
            "                              ],\n" +
            "                              \"voided\":false\n" +
            "                           },\n" +
            "                           \"children\":{\n" +
            "                              \"9ue62k3a-88f3-886f-b990-bb529b399999\":{\n" +
            "                                 \"id\":\"9ue62k3a-88f3-886f-b990-bb529b399999\",\n" +
            "                                 \"label\":\"Roysambu\",\n" +
            "                                 \"node\":{\n" +
            "                                    \"locationId\":\"9ue62k3a-88f3-886f-b990-bb529b399999\",\n" +
            "                                    \"name\":\"Roysambu\",\n" +
            "                                    \"parentLocation\":{\n" +
            "                                       \"locationId\":\"93437b43-485d-44df-8eaf-434449579999\",\n" +
            "                                       \"voided\":false\n" +
            "                                    },\n" +
            "                                    \"tags\":[\n" +
            "                                       \"Ward\"\n" +
            "                                    ],\n" +
            "                                    \"voided\":false\n" +
            "                                 },\n" +
            "                                 \"children\":{\n" +
            "                                    \"kdjafls993-33d1-4322-9342-062j8cf99999\":{\n" +
            "                                       \"id\":\"kdjafls993-33d1-4322-9342-062j8cf99999\",\n" +
            "                                       \"label\":\"Thika Road Health Center\",\n" +
            "                                       \"node\":{\n" +
            "                                          \"locationId\":\"kdjafls993-33d1-4322-9342-062j8cf99999\",\n" +
            "                                          \"name\":\"Thika Road Health Center\",\n" +
            "                                          \"parentLocation\":{\n" +
            "                                             \"locationId\":\"9ue62k3a-88f3-886f-b990-bb529b399999\",\n" +
            "                                             \"voided\":false\n" +
            "                                          },\n" +
            "                                          \"tags\":[\n" +
            "                                             \"Facility\"\n" +
            "                                          ],\n" +
            "                                          \"voided\":false\n" +
            "                                       },\n" +
            "                                       \"parent\":\"9ue62k3a-88f3-886f-b990-bb529b399999\"\n" +
            "                                    }\n" +
            "                                 },\n" +
            "                                 \"parent\":\"93437b43-485d-44df-8eaf-434449579999\"\n" +
            "                              }\n" +
            "                           },\n" +
            "                           \"parent\":\"3x3tsh58-e6db-4dsa-8be1-e860ec299999\"\n" +
            "                        }\n" +
            "                     },\n" +
            "                     \"parent\":\"583j4bve4-9a36-459a-99fc-bc0356d99999\"\n" +
            "                  }\n" +
            "               }\n" +
            "            }\n" +
            "         },\n" +
            "         \"parentChildren\":{\n" +
            "            \"9ue62k3a-88f3-886f-b990-bb529b399999\":[\n" +
            "               \"kdjafls993-33d1-4322-9342-062j8cf99999\"\n" +
            "            ],\n" +
            "            \"93437b43-485d-44df-8eaf-434449579999\":[\n" +
            "               \"9ue62k3a-88f3-886f-b990-bb529b399999\"\n" +
            "            ],\n" +
            "            \"583j4bve4-9a36-459a-99fc-bc0356d99999\":[\n" +
            "               \"3x3tsh58-e6db-4dsa-8be1-e860ec299999\"\n" +
            "            ],\n" +
            "            \"3x3tsh58-e6db-4dsa-8be1-e860ec299999\":[\n" +
            "               \"93437b43-485d-44df-8eaf-434449579999\"\n" +
            "            ]\n" +
            "         }\n" +
            "      }\n" +
            "   },\n" +
            "   \"team\":{\n" +
            "      \"identifier\":\"93c6526-6667-3333-a611112-f3b309999999\",\n" +
            "      \"locations\":[\n" +
            "         {\n" +
            "            \"display\":\"Health Team Kasarani\",\n" +
            "            \"name\":\"Health Team Kasarani\",\n" +
            "            \"uuid\":\"kdjafls993-33d1-4322-9342-062j8cf99999\"\n" +
            "         }\n" +
            "      ],\n" +
            "      \"team\":{\n" +
            "         \"teamName\":\"Health Team Kasarani\",\n" +
            "         \"organizationIds\":[\n" +
            "            1.0\n" +
            "         ],\n" +
            "         \"display\":\"Health Team Kasarani\",\n" +
            "         \"location\":{\n" +
            "            \"display\":\"Health Team Kasarani\",\n" +
            "            \"name\":\"Health Team Kasarani\",\n" +
            "            \"uuid\":\"kdjafls993-33d1-4322-9342-062j8cf99999\"\n" +
            "         },\n" +
            "         \"uuid\":\"abf1be43-32da-4848-9b50-630fb89ec0ef\"\n" +
            "      },\n" +
            "      \"uuid\":\"93c6526-6667-3333-a611112-f3b309999999\"\n" +
            "   },\n" +
            "   \"time\":{\n" +
            "      \"time\":\"2020-06-02 08:21:40\",\n" +
            "      \"timeZone\":\"Africa/Nairobi\"\n" +
            "   },\n" +
            "   \"user\":{\n" +
            "      \"username\":\"demo\",\n" +
            "      \"roles\":[\n" +
            "         \"ROLE_OPENMRS\",\n" +
            "         \"ROLE_ALL_EVENTS\",\n" +
            "         \"ROLE_PLANS_FOR_USER\",\n" +
            "         \"ROLE_offline_access\",\n" +
            "         \"ROLE_uma_authorization\"\n" +
            "      ],\n" +
            "      \"permissions\":[\n" +
            "         \"ROLE_OPENMRS\",\n" +
            "         \"ROLE_ALL_EVENTS\",\n" +
            "         \"ROLE_PLANS_FOR_USER\",\n" +
            "         \"ROLE_offline_access\",\n" +
            "         \"ROLE_uma_authorization\"\n" +
            "      ],\n" +
            "      \"preferredName\":\"Demo User\",\n" +
            "      \"baseEntityId\":\"93c6526-6667-3333-a611112-f3b309999999\",\n" +
            "      \"attributes\":{\n" +
            "\n" +
            "      },\n" +
            "      \"voided\":false\n" +
            "   },\n" +
            "   \"jurisdictions\":[\n" +
            "      \"Health Team Kasarani\"\n" +
            "   ]\n" +
            "}";
}
