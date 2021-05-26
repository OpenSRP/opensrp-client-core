package org.smartregister.repository;


import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.domain.form.FormData;
import org.smartregister.domain.form.FormField;
import org.smartregister.domain.form.FormInstance;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.util.FormSubmissionBuilder;
import org.smartregister.util.Session;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.smartregister.domain.SyncStatus.PENDING;
import static org.smartregister.domain.SyncStatus.SYNCED;
import static org.smartregister.util.EasyMap.create;
import static org.smartregister.util.EasyMap.mapOf;

public class FormDataRepositoryTest extends AndroidTestCase {
    private FormDataRepository repository;
    private EligibleCoupleRepository eligibleCoupleRepository;
    private MotherRepository motherRepository;

    @Override
    protected void setUp() throws Exception {
        repository = new FormDataRepository();
        AlertRepository alertRepository = new AlertRepository();
        TimelineEventRepository timelineEventRepository = new TimelineEventRepository();
        ChildRepository childRepository = new ChildRepository();
        motherRepository = new MotherRepository();
        eligibleCoupleRepository = new EligibleCoupleRepository();
        Session session = new Session().setPassword("password").setRepositoryName("opensrp.db" + new Date().getTime());
        new Repository(new RenamingDelegatingContext(getContext(), "test_"), session,
                repository, eligibleCoupleRepository, alertRepository, timelineEventRepository, childRepository, motherRepository);
    }

    public void testShouldRunQueryAndGetUniqueResult() throws Exception {
        Map<String, String> details = create("Hello", "There").put("Also", "This").put("someKey", "someValue").map();
        EligibleCouple eligibleCouple = new EligibleCouple("CASE X", "Wife 1", "Husband 1", "EC Number", "Village 1", "SubCenter 1", details);
        eligibleCoupleRepository.add(eligibleCouple);
        String sql = MessageFormat.format("select * from eligible_couple where eligible_couple.id = ''{0}''", eligibleCouple.caseId());

        String result = repository.queryUniqueResult(sql,null);

        Map<String, String> fieldValues = new Gson().fromJson(result, new TypeToken<Map<String, String>>() {
        }.getType());
        assertEquals(eligibleCouple.caseId(), fieldValues.get("id"));
        assertEquals(eligibleCouple.wifeName(), fieldValues.get("wifeName"));
        assertEquals("someValue", fieldValues.get("someKey"));
    }

    public void testReturnsEmptyResultWhenQueryResultsAreEmpty() throws Exception {
        String sql = MessageFormat.format("select * from eligible_couple where eligible_couple.id = ''{0}''", "");

        String result = repository.queryUniqueResult(sql, null);

        Map<String, String> fieldValues = new Gson().fromJson(result, new TypeToken<Map<String, String>>() {
        }.getType());
        assertEquals(0, fieldValues.size());
    }

    public void testShouldRunQueryAndGetListAsResult() throws Exception {
        Map<String, String> details = create("Hello", "There").put("Also", "This").put("someKey", "someValue").map();
        EligibleCouple firstEligibleCouple = new EligibleCouple("CASE X", "Wife 1", "Husband 1", "EC Number 1", "Village 1", "SubCenter 1", details);
        EligibleCouple secondEligibleCouple = new EligibleCouple("CASE Y", "Wife 2", "Husband 2", "EC Number 2", "Village 1", "SubCenter 1", details);
        eligibleCoupleRepository.add(firstEligibleCouple);
        eligibleCoupleRepository.add(secondEligibleCouple);
        String sql = MessageFormat.format("select * from eligible_couple where eligible_couple.village = ''{0}''", "Village 1");

        String results = repository.queryList(sql, null);

        List<Map<String, String>> fieldValues = new Gson().fromJson(results, new TypeToken<List<Map<String, String>>>() {
        }.getType());
        assertEquals(firstEligibleCouple.caseId(), fieldValues.get(0).get("id"));
        assertEquals(secondEligibleCouple.caseId(), fieldValues.get(1).get("id"));
    }

    public void testShouldSaveFormSubmission() throws Exception {
        Map<String, String> params = create("instanceId", "id 1").put("entityId", "entity id 1").put("formName", "form name").map();
        String paramsJSON = new Gson().toJson(params);

        FormInstance instance = new FormInstance(new FormData("entity 1", "default", asList(new FormField("field1.1", "value1.1", "source1.1")), null), "1");
        String instanceId = repository.saveFormSubmission(paramsJSON, new Gson().toJson(instance), "1");

        FormSubmission actualFormSubmission = repository.fetchFromSubmission("id 1");
        assertNotNull(actualFormSubmission);
        assertEquals(new FormSubmission("id 1", "entity id 1", "form name", new Gson().toJson(instance), "some version", PENDING, "1"), actualFormSubmission);
        assertEquals("id 1", instanceId);
    }

    public void testShouldCheckForSyncStatusWhenSavingFormSubmission() throws Exception {
        Map<String, String> params = create("instanceId", "id 1").put("entityId", "entity id 1").put("formName", "form name").put("sync_status", SYNCED.value()).map();
        String paramsJSON = new Gson().toJson(params);

        FormInstance instance = new FormInstance(new FormData("entity 1", "default", asList(new FormField("field1.1", "value1.1", "source1.1")), null), "1");
        String instanceId = repository.saveFormSubmission(paramsJSON, new Gson().toJson(instance), "1");

        FormSubmission actualFormSubmission = repository.fetchFromSubmission("id 1");
        assertNotNull(actualFormSubmission);
        assertEquals(new FormSubmission("id 1", "entity id 1", "form name", new Gson().toJson(instance), "some version", SYNCED, "1"), actualFormSubmission);
        assertEquals("id 1", instanceId);
    }

    public void testShouldCheckFormSubmissionExistence() throws Exception {
        FormSubmission firstFormSubmission = FormSubmissionBuilder.create().withInstanceId("instance id 1").withVersion("122").build();
        repository.saveFormSubmission(firstFormSubmission);

        assertTrue(repository.submissionExists("instance id 1"));
        assertFalse(repository.submissionExists("invalid instance Id"));
    }

    public void testShouldSaveNewEC() throws Exception {
        Map<String, String> fields =
                create("id", "entity id 1")
                        .put("wifeName", "asha")
                        .put("husbandName", "raja")
                        .put("ecNumber", "ec 123")
                        .put("currentMethod", "ocp")
                        .put("isHighPriority", "no")
                        .map();
        String fieldsJSON = new Gson().toJson(fields);

        String entityId = repository.saveEntity("eligible_couple", fieldsJSON);

        EligibleCouple savedEC = eligibleCoupleRepository.findByCaseID(entityId);
        Map<String, String> expectedDetails = create("currentMethod", "ocp").put("isHighPriority", "no").map();
        EligibleCouple expectedEligibleCouple = new EligibleCouple(entityId, "asha", "raja", "ec 123", null, null, expectedDetails);
        assertEquals(expectedEligibleCouple, savedEC);
    }

    public void testShouldUpdateEC() throws Exception {
        Map<String, String> fields =
                create("id", "entity id 1")
                        .put("husbandName", "raja")
                        .put("ecNumber", "ec 123")
                        .put("wifeName", "asha").put("village", "")
                        .put("currentMethod", "ocp")
                        .put("isHighPriority", "no")
                        .map();
        String fieldsJSON = new Gson().toJson(fields);
        Map<String, String> oldDetails = create("currentMethod", "condom")
                .put("isHighPriority", "yes")
                .put("bloodGroup", "o-ve")
                .map();
        EligibleCouple oldEC = new EligibleCouple("entity id 1", "old wife name", "old husband name", "ec 123", "old village", "sub center", oldDetails);
        eligibleCoupleRepository.add(oldEC);

        String entityId = repository.saveEntity("eligible_couple", fieldsJSON);

        assertEquals(entityId, "entity id 1");
        EligibleCouple savedEC = eligibleCoupleRepository.findByCaseID(entityId);
        Map<String, String> expectedDetails = create("currentMethod", "ocp")
                .put("isHighPriority", "no")
                .put("bloodGroup", "o-ve")
                .map();
        EligibleCouple expectedEligibleCouple = new EligibleCouple("entity id 1", "asha", "raja", "ec 123", "", "sub center", expectedDetails);
        assertEquals(expectedEligibleCouple, savedEC);
    }

    public void testShouldUpdateMotherEntity() throws Exception {
        Map<String, String> fields =
                create("id", "entity id 1")
                        .put("thayiCardNumber", "thayi1")
                        .put("referenceDate", "2013-01-05")
                        .put("ecCaseId", "ec 123")
                        .put("isHighPriority", "no")
                        .map();
        String fieldsJSON = new Gson().toJson(fields);
        Mother oldMother = new Mother("entity id 1", "ec 123", "thayi2", "2013-01-01");
        motherRepository.add(oldMother);

        String entityId = repository.saveEntity("mother", fieldsJSON);

        assertEquals(entityId, "entity id 1");
        Mother savedMother = motherRepository.findById(entityId);
        Map<String, String> expectedDetails = mapOf("isHighPriority", "no");
        Mother expectedMother = new Mother("entity id 1", "ec 123", "thayi1", "2013-01-05").withDetails(expectedDetails).withType("ANC");
        assertEquals(expectedMother, savedMother);
    }

    public void testShouldFetchPendingFormSubmissions() throws Exception {
        FormInstance instance1 = new FormInstance(new FormData("entity 1", "default", asList(new FormField("field1.1", "value1.1", "source1.1")), null), "1");
        FormInstance instance2 = new FormInstance(new FormData("entity 2", "default", asList(new FormField("field2.1", "value2.1", "source2.1")), null), "1");
        FormInstance instance3 = new FormInstance(new FormData("entity 3", "default", asList(new FormField("field3.1", "value3.1", "source3.1")), null), "1");
        FormSubmission firstSubmission = new FormSubmission("id 1", "entity id 1", "form name", new Gson().toJson(instance1), "some version", PENDING, "1");
        FormSubmission secondSubmission = new FormSubmission("id 2", "entity id 2", "form name", new Gson().toJson(instance2), "some other version", PENDING, "1");
        FormSubmission thirdSubmission = new FormSubmission("id 3", "entity id 3", "form name", new Gson().toJson(instance3), "some other version", SYNCED, "1");
        repository.saveFormSubmission(firstSubmission);
        repository.saveFormSubmission(secondSubmission);
        repository.saveFormSubmission(thirdSubmission);

        List<FormSubmission> pendingFormSubmissions = repository.getPendingFormSubmissions();

        assertEquals(asList(firstSubmission, secondSubmission), pendingFormSubmissions);
    }

    public void testShouldFetchPendingFormSubmissionsCount() throws Exception {
        FormInstance instance1 = new FormInstance(new FormData("entity 1", "default", asList(new FormField("field1.1", "value1.1", "source1.1")), null), "1");
        FormInstance instance2 = new FormInstance(new FormData("entity 2", "default", asList(new FormField("field2.1", "value2.1", "source2.1")), null), "1");
        FormInstance instance3 = new FormInstance(new FormData("entity 3", "default", asList(new FormField("field3.1", "value3.1", "source3.1")), null), "1");
        FormSubmission firstSubmission = new FormSubmission("id 1", "entity id 1", "form name", new Gson().toJson(instance1), "some version", PENDING, "1");
        FormSubmission secondSubmission = new FormSubmission("id 2", "entity id 2", "form name", new Gson().toJson(instance2), "some other version", PENDING, "1");
        FormSubmission thirdSubmission = new FormSubmission("id 3", "entity id 3", "form name", new Gson().toJson(instance3), "some other version", SYNCED, "1");
        repository.saveFormSubmission(firstSubmission);
        repository.saveFormSubmission(secondSubmission);
        repository.saveFormSubmission(thirdSubmission);

        long pendingFormSubmissionsCount = repository.getPendingFormSubmissionsCount();

        assertEquals(2, pendingFormSubmissionsCount);
    }

    public void testShouldMarkPendingFormSubmissionsAsSynced() throws Exception {
        FormSubmission firstSubmission = new FormSubmission("id 1", "entity id 1", "form name", "", "some version", PENDING, "1");
        FormSubmission secondSubmission = new FormSubmission("id 2", "entity id 2", "form name", "", "some other version", PENDING, "1");
        FormSubmission thirdSubmission = new FormSubmission("id 3", "entity id 3", "form name", "", "some other version", PENDING, "1");
        repository.saveFormSubmission(firstSubmission);
        repository.saveFormSubmission(secondSubmission);
        repository.saveFormSubmission(thirdSubmission);

        repository.markFormSubmissionsAsSynced(asList(firstSubmission, secondSubmission));

        assertEquals(firstSubmission.setSyncStatus(SYNCED), repository.fetchFromSubmission("id 1"));
        assertEquals(secondSubmission.setSyncStatus(SYNCED), repository.fetchFromSubmission("id 2"));
        assertEquals(thirdSubmission, repository.fetchFromSubmission("id 3"));
    }

    public void testShouldUpdateServerVersionByInstanceId() throws Exception {
        FormSubmission firstSubmission = new FormSubmission("instance 1", "entity id 1", "form name", "", "some version", SYNCED, "1");
        FormSubmission secondSubmission = new FormSubmission("instance 2", "entity id 2", "form name", "", "some other version", SYNCED, "1");
        FormSubmission thirdSubmission = new FormSubmission("instance 3", "entity id 3", "form name", "", "some other version", PENDING, "1");
        repository.saveFormSubmission(firstSubmission);
        repository.saveFormSubmission(secondSubmission);
        repository.saveFormSubmission(thirdSubmission);

        repository.updateServerVersion("instance 1", "0");
        repository.updateServerVersion("instance 2", "1");

        assertEquals(firstSubmission.setServerVersion("0"), repository.fetchFromSubmission("instance 1"));
        assertEquals(secondSubmission.setServerVersion("1"), repository.fetchFromSubmission("instance 2"));
        assertEquals(thirdSubmission, repository.fetchFromSubmission("instance 3"));
    }
}