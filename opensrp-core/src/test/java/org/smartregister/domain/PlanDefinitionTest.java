package org.smartregister.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.DateTypeConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by samuelgithengi on 4/29/19.
 */
public class PlanDefinitionTest {

    public static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter("yyyy-MM-dd"))
            .registerTypeAdapter(LocalDate.class, new DateTypeConverter())
            .disableHtmlEscaping()
            .create();

    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static String planDefinitionJSON = "{\"identifier\":\"4708ca0a-d0d6-4199-bb1b-8701803c2d02\",\"version\":\"1\",\"name\":\"2019_IRS_Season\",\"title\":\"2019 IRS Operational Plan\",\"status\":\"active\",\"date\":\"2019-03-27\",\"effectivePeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-07-31\"},\"jurisdiction\":[{\"code\":\"3421\"},{\"code\":\"3429\"},{\"code\":\"3436\"},{\"code\":\"3439\"}],\"goal\":[{\"id\":\"BCC_complete\",\"description\":\"Complete BCC for the operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of BCC communication activities that happened\",\"detail\":{\"detailQuantity\":{\"value\":1.0,\"comparator\":\">=\",\"unit\":\"each\"}},\"due\":\"2019-04-01\"}]},{\"id\":\"90_percent_of_structures_sprayed\",\"description\":\"Spray 90 % of structures in the operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of structures sprayed\",\"detail\":{\"detailQuantity\":{\"value\":90.0,\"comparator\":\">=\",\"unit\":\"percent\"}},\"due\":\"2019-05-31\"}]}],\"action\":[{\"identifier\":\"990af508-f1a9-4793-841f-49a7b6438827\",\"prefix\":1,\"title\":\"Perform BCC\",\"description\":\"Perform BCC for the operational area\",\"code\":\"BCC\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-10\"},\"reason\":\"Routine\",\"goalId\":\"BCC_complete\",\"subjectCodableConcept\":{\"text\":\"Operational_Area\"},\"taskTemplate\":\"Action1_Perform_BCC\",\"type\":\"create\"},{\"identifier\":\"8276be06-97d3-4815-8d39-0bc158dc1d91\",\"prefix\":2,\"title\":\"Spray Structures\",\"description\":\"Visit each structure in the operational area and attempt to spray\",\"code\":\"IRS\",\"timingPeriod\":{\"start\":\"2019-04-10\",\"end\":\"2019-07-31\"},\"reason\":\"Routine\",\"goalId\":\"90_percent_of_structures_sprayed\",\"subjectCodableConcept\":{\"text\":\"Residential_Structure\"},\"taskTemplate\":\"Action2_Spray_Structures\",\"type\":\"create\"}],\"experimental\":false}";

    public static String fiPlanDefinitionJSON = "{\"identifier\":\"10f9e9fa-ce34-4b27-a961-72fab5206ab6\",\"version\":\"1\",\"name\":\"A1-Tha Luang Village 1 Focus 01\",\"title\":\"A1-Tha Luang Village 1 Focus 01\",\"status\":\"active\",\"date\":\"2019-04-02\",\"effectivePeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-07-31\"},\"useContext\":[{\"code\":\"fiStatus\",\"valueCodableConcept\":\"A1\"},{\"code\":\"fiReason\",\"valueCodableConcept\":\"Routine\"}],\"jurisdiction\":[{\"code\":\"450fc15b-5bd2-468a-927a-49cb10d3bcac\"}],\"goal\":[{\"id\":\"Case_Confirmation\",\"description\":\"Confirm the index case\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Case confirmation complete\",\"detail\":{\"detailQuantity\":{\"value\":1.0,\"comparator\":\"=\",\"unit\":\"each\"}},\"due\":\"2019-04-01\"}]},{\"id\":\"RACD_register_family_1km_radius\",\"description\":\"Visit all residential structures (100%) within a 1 km radius of a confirmed index case\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of residential structures visited\",\"detail\":{\"detailQuantity\":{\"value\":100.0,\"comparator\":\"=\",\"unit\":\"percent\"}},\"due\":\"2019-06-30\"}]},{\"id\":\"RACD_bednet_dist_1km_radius\",\"description\":\"Visit 90% of residential structures in the operational area and provide nets\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of residential structures visited\",\"detail\":{\"detailQuantity\":{\"value\":90.0,\"comparator\":\">=\",\"unit\":\"percent\"}},\"due\":\"2019-06-30\"}]},{\"id\":\"RACD_blood_screening_1km_radius\",\"description\":\"Test and treat all people (100%) registered within a 1 km radius of a confirmed index case\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of registered people tested\",\"detail\":{\"detailQuantity\":{\"value\":100.0,\"comparator\":\"=\",\"unit\":\"percent\"}},\"due\":\"2019-06-30\"}]},{\"id\":\"Larval_Dipping_Min_3_Sites\",\"description\":\"Perform a minimum of three larval dipping activities in the operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of larval dipping activities\",\"detail\":{\"detailQuantity\":{\"value\":3.0,\"comparator\":\">=\",\"unit\":\"each\"}},\"due\":\"2019-06-30\"}]},{\"id\":\"Mosquito_Collection_Min_3_Traps\",\"description\":\"Set a minimum of three mosquito collection traps and complete the mosquito collection process\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of larval dipping activities\",\"detail\":{\"detailQuantity\":{\"value\":3.0,\"comparator\":\">=\",\"unit\":\"each\"}},\"due\":\"2019-06-30\"}]}],\"action\":[{\"identifier\":\"7df6c642-484d-426c-8b08-d27153bdfc14\",\"prefix\":1,\"title\":\"Case Confirmation\",\"description\":\"Confirm the index case\",\"code\":\"Case Confirmation\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-02\"},\"reason\":\"Investigation\",\"goalId\":\"Case_Confirmation\",\"subjectCodableConcept\":{\"text\":\"Person\"},\"taskTemplate\":\"Case_Confirmation\",\"type\":\"create\"},{\"identifier\":\"95515b0d-b9c0-496e-83c7-7af8b4924d1f\",\"prefix\":2,\"title\":\"RACD Register Families\",\"description\":\"Visit all residential structures (100%) within a 1 km radius of a confirmed index case\",\"code\":\"RACD Register Families\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-08\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_register_family_1km_radius\",\"subjectCodableConcept\":{\"text\":\"Residential_Structure\"},\"taskTemplate\":\"RACD_register_families\",\"type\":\"create\"},{\"identifier\":\"fb6339fa-1a7a-4ea8-9404-580eaedd9acf\",\"prefix\":3,\"title\":\"Bednet Distribution\",\"description\":\"Visit 90% of residential structures in the operational area and provide nets\",\"code\":\"Bednet_Distribution\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-30\"},\"reason\":\"Routine\",\"goalId\":\"RACD_bednet_dist_1km_radius\",\"subjectCodableConcept\":{\"text\":\"Residential_Structure\"},\"taskTemplate\":\"ITN_Visit_Structures\",\"type\":\"create\"},{\"identifier\":\"1e88cf04-f88f-46de-84b2-bd8fef10d4c0\",\"prefix\":4,\"title\":\"RACD Blood screning\",\"description\":\"Visit all residential structures (100%) within a 1 km radius of a confirmed index case and test each registered person\",\"code\":\"Blood Screening\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-30\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_blood_screening_1km_radius\",\"subjectCodableConcept\":{\"text\":\"Person\"},\"taskTemplate\":\"RACD_Blood_Screening\",\"type\":\"create\"},{\"identifier\":\"7ea5e7da-f9ed-4b6e-9dcc-425b670f7835\",\"prefix\":5,\"title\":\"Larval Dipping\",\"description\":\"Perform a minimum of three larval dipping activities in the operational area\",\"code\":\"Larval Dipping\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-08\"},\"reason\":\"Investigation\",\"goalId\":\"Larval_Dipping_Min_3_Sites\",\"subjectCodableConcept\":{\"text\":\"Breeding_Site\"},\"taskTemplate\":\"Larval_Dipping\",\"type\":\"create\"},{\"identifier\":\"e5e0b279-2e43-4825-826c-d447c2ede5b7\",\"prefix\":6,\"title\":\"Mosquito Collection\",\"description\":\"Set a minimum of three mosquito collection traps and complete the mosquito collection process\",\"code\":\"Mosquito Collection\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-08\"},\"reason\":\"Investigation\",\"goalId\":\"Mosquito_Collection_Min_3_Traps\",\"subjectCodableConcept\":{\"text\":\"Mosquito_Collection_Point\"},\"taskTemplate\":\"Mosquito_Collection_Point\",\"type\":\"create\"}],\"experimental\":false}";
    @Test
    public void testDeserialize() {
        PlanDefinition planDefinition = gson.fromJson(planDefinitionJSON, PlanDefinition.class);
        assertNotNull(planDefinition);
        assertEquals("4708ca0a-d0d6-4199-bb1b-8701803c2d02", planDefinition.getIdentifier());
        assertEquals("1", planDefinition.getVersion());
        assertEquals("2019_IRS_Season", planDefinition.getName());
        assertEquals("2019 IRS Operational Plan", planDefinition.getTitle());
        assertEquals(PlanDefinition.PlanStatus.ACTIVE, planDefinition.getStatus());
        assertEquals("2019-03-27", planDefinition.getDate().toString(formatter));

        assertEquals(4, planDefinition.getJurisdiction().size());
        assertEquals("3421", planDefinition.getJurisdiction().get(0).getCode());

        assertEquals(2, planDefinition.getGoals().size());

        assertEquals("BCC_complete", planDefinition.getGoals().get(0).getId());
        assertEquals("Complete BCC for the operational area", planDefinition.getGoals().get(0).getDescription());
        assertEquals("medium-priority", planDefinition.getGoals().get(0).getPriority());
        assertEquals(1, planDefinition.getGoals().get(0).getTargets().size());

        Target target = planDefinition.getGoals().get(0).getTargets().get(0);
        assertEquals("Number of BCC communication activities that happened", target.getMeasure());
        assertEquals("2019-04-01", target.getDue().toString(formatter));
        assertEquals(1, target.getDetail().getDetailQuantity().getValue(), 0.000001);
        assertEquals(">=", target.getDetail().getDetailQuantity().getComparator());
        assertEquals("each", target.getDetail().getDetailQuantity().getUnit());


        assertEquals(2, planDefinition.getActions().size());

        Action action = planDefinition.getActions().get(1);
        assertEquals("8276be06-97d3-4815-8d39-0bc158dc1d91", action.getIdentifier());
        assertEquals(2, action.getPrefix());
        assertEquals("Spray Structures", action.getTitle());
        assertEquals("Visit each structure in the operational area and attempt to spray", action.getDescription());
        assertEquals("IRS", action.getCode());

        assertEquals("2019-04-10", action.getTimingPeriod().getStart().toString(formatter));
        assertEquals("2019-07-31", action.getTimingPeriod().getEnd().toString(formatter));
        assertEquals("Routine", action.getReason());
        assertEquals("90_percent_of_structures_sprayed", action.getGoalId());
        assertEquals("Residential_Structure", action.getSubjectCodableConcept().getText());
        assertEquals("Action2_Spray_Structures", action.getTaskTemplate());

    }


    @Test
    public void testSerialize() {
        PlanDefinition planDefinition = gson.fromJson(planDefinitionJSON, PlanDefinition.class);
        assertEquals(planDefinitionJSON, gson.toJson(planDefinition));
    }

    @Test
    public void testFIPlan() {
        String fIPlanJSON = "{\"identifier\":\"10f9e9fa-ce34-4b27-a961-72fab5206ab6\",\"version\":\"1\",\"name\":\"A1-Tha Luang Village 1 Focus 01\",\"title\":\"A1-Tha Luang Village 1 Focus 01\",\"status\":\"active\",\"date\":\"2019-04-02\",\"effectivePeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-07-31\"},\"useContext\":[{\"code\":\"fiStatus\",\"valueCodableConcept\":\"A1\"},{\"code\":\"fiReason\",\"valueCodableConcept\":\"Routine\"}],\"jurisdiction\":[{\"code\":\"450fc15b-5bd2-468a-927a-49cb10d3bcac\"}],\"goal\":[{\"id\":\"Case_Confirmation\",\"description\":\"Confirm the index case\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Case confirmation complete\",\"detail\":{\"detailQuantity\":{\"value\":1.0,\"comparator\":\"=\",\"unit\":\"each\"}},\"due\":\"2019-04-01\"}]},{\"id\":\"RACD_register_family_1km_radius\",\"description\":\"Visit all residential structures (100%) within a 1 km radius of a confirmed index case\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of residential structures visited\",\"detail\":{\"detailQuantity\":{\"value\":100.0,\"comparator\":\"=\",\"unit\":\"percent\"}},\"due\":\"2019-06-30\"}]},{\"id\":\"RACD_bednet_dist_1km_radius\",\"description\":\"Visit 90% of residential structures in the operational area and provide nets\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of residential structures visited\",\"detail\":{\"detailQuantity\":{\"value\":90.0,\"comparator\":\">=\",\"unit\":\"percent\"}},\"due\":\"2019-06-30\"}]},{\"id\":\"RACD_blood_screening_1km_radius\",\"description\":\"Test and treat all people (100%) registered within a 1 km radius of a confirmed index case\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of registered people tested\",\"detail\":{\"detailQuantity\":{\"value\":100.0,\"comparator\":\"=\",\"unit\":\"percent\"}},\"due\":\"2019-06-30\"}]},{\"id\":\"Larval_Dipping_Min_3_Sites\",\"description\":\"Perform a minimum of three larval dipping activities in the operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of larval dipping activities\",\"detail\":{\"detailQuantity\":{\"value\":3.0,\"comparator\":\">=\",\"unit\":\"each\"}},\"due\":\"2019-06-30\"}]},{\"id\":\"Mosquito_Collection_Min_3_Traps\",\"description\":\"Set a minimum of three mosquito collection traps and complete the mosquito collection process\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of larval dipping activities\",\"detail\":{\"detailQuantity\":{\"value\":3.0,\"comparator\":\">=\",\"unit\":\"each\"}},\"due\":\"2019-06-30\"}]}],\"action\":[{\"identifier\":\"7df6c642-484d-426c-8b08-d27153bdfc14\",\"prefix\":1,\"title\":\"Case Confirmation\",\"description\":\"Confirm the index case\",\"code\":\"Case Confirmation\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-02\"},\"reason\":\"Investigation\",\"goalId\":\"Case_Confirmation\",\"subjectCodableConcept\":{\"text\":\"Person\"},\"taskTemplate\":\"Case_Confirmation\",\"type\":\"create\"},{\"identifier\":\"95515b0d-b9c0-496e-83c7-7af8b4924d1f\",\"prefix\":2,\"title\":\"RACD Register Families\",\"description\":\"Visit all residential structures (100%) within a 1 km radius of a confirmed index case\",\"code\":\"RACD Register Families\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-08\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_register_family_1km_radius\",\"subjectCodableConcept\":{\"text\":\"Residential_Structure\"},\"taskTemplate\":\"RACD_register_families\",\"type\":\"create\"},{\"identifier\":\"fb6339fa-1a7a-4ea8-9404-580eaedd9acf\",\"prefix\":3,\"title\":\"Bednet Distribution\",\"description\":\"Visit 90% of residential structures in the operational area and provide nets\",\"code\":\"Bednet_Distribution\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-30\"},\"reason\":\"Routine\",\"goalId\":\"RACD_bednet_dist_1km_radius\",\"subjectCodableConcept\":{\"text\":\"Residential_Structure\"},\"taskTemplate\":\"ITN_Visit_Structures\",\"type\":\"create\"},{\"identifier\":\"1e88cf04-f88f-46de-84b2-bd8fef10d4c0\",\"prefix\":4,\"title\":\"RACD Blood screning\",\"description\":\"Visit all residential structures (100%) within a 1 km radius of a confirmed index case and test each registered person\",\"code\":\"Blood Screening\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-30\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_blood_screening_1km_radius\",\"subjectCodableConcept\":{\"text\":\"Person\"},\"taskTemplate\":\"RACD_Blood_Screening\",\"type\":\"create\"},{\"identifier\":\"7ea5e7da-f9ed-4b6e-9dcc-425b670f7835\",\"prefix\":5,\"title\":\"Larval Dipping\",\"description\":\"Perform a minimum of three larval dipping activities in the operational area\",\"code\":\"Larval Dipping\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-08\"},\"reason\":\"Investigation\",\"goalId\":\"Larval_Dipping_Min_3_Sites\",\"subjectCodableConcept\":{\"text\":\"Breeding_Site\"},\"taskTemplate\":\"Larval_Dipping\",\"type\":\"create\"},{\"identifier\":\"e5e0b279-2e43-4825-826c-d447c2ede5b7\",\"prefix\":6,\"title\":\"Mosquito Collection\",\"description\":\"Set a minimum of three mosquito collection traps and complete the mosquito collection process\",\"code\":\"Mosquito Collection\",\"timingPeriod\":{\"start\":\"2019-04-01\",\"end\":\"2019-04-08\"},\"reason\":\"Investigation\",\"goalId\":\"Mosquito_Collection_Min_3_Traps\",\"subjectCodableConcept\":{\"text\":\"Mosquito_Collection_Point\"},\"taskTemplate\":\"Mosquito_Collection_Point\",\"type\":\"create\"}],\"experimental\":false}";
        PlanDefinition planDefinition = gson.fromJson(fIPlanJSON, PlanDefinition.class);
        assertEquals(fIPlanJSON, gson.toJson(planDefinition));
    }

}
