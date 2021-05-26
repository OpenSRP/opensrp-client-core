package org.smartregister.repository;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.Child;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.domain.TimelineEvent;
import org.smartregister.util.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.smartregister.util.EasyMap.create;
import static org.smartregister.util.EasyMap.mapOf;

public class ChildRepositoryTest extends AndroidTestCase {
    private ChildRepository repository;
    private TimelineEventRepository timelineEventRepository;
    private MotherRepository motherRepository;
    private EligibleCoupleRepository ecRepository;
    private AlertRepository alertRepository;

    private static final Map<String, String> EXTRA_DETAILS = mapOf("some-key", "some-value");

    @Override
    protected void setUp() throws Exception {
        timelineEventRepository = new TimelineEventRepository();
        motherRepository = new MotherRepository();
        ecRepository = new EligibleCoupleRepository();
        alertRepository = new AlertRepository();
        repository = new ChildRepository();

        Session session = new Session().setPassword("password").setRepositoryName("drishti.db" + new Date().getTime());
        new Repository(new RenamingDelegatingContext(getContext(), "test_"), session, repository, timelineEventRepository, alertRepository, motherRepository, ecRepository);
    }

    public void testShouldInsertChildForExistingMother() throws Exception {
        repository.add(new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS));

        assertEquals(asList(new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS)), repository.all());
    }

    public void testShouldUpdateChild() throws Exception {
        Child child = new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS).setIsClosed(false);
        repository.add(child);
        child.setIsClosed(true);

        repository.update(child);
        assertEquals(child, repository.find("CASE A"));
    }

    public void testShouldCloseChild() throws Exception {
        Child child = new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS).setIsClosed(false);
        repository.add(child);

        repository.close("CASE A");
        assertTrue(repository.find("CASE A").isClosed());
    }

    public void testShouldFetchAllOpenChildren() throws Exception {
        Child firstChild = new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS);
        Child secondChild = new Child("CASE B", "CASE X", "TC 1", "2012-06-10", "female", EXTRA_DETAILS);
        Child closedChild = new Child("CASE C", "CASE X", "TC 1", "2012-06-10", "female", EXTRA_DETAILS).setIsClosed(true);
        repository.add(firstChild);
        repository.add(secondChild);
        repository.add(closedChild);

        List<Child> children = repository.all();

        assertTrue(children.contains(firstChild));
        assertTrue(children.contains(secondChild));
        assertFalse(children.contains(closedChild));
    }

    public void testShouldFetchChildrenByTheirOwnCaseId() throws Exception {
        repository.add(new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS));
        repository.add(new Child("CASE B", "CASE X", "TC 1", "2012-06-10", "female", EXTRA_DETAILS));
        repository.add(new Child("CASE C", "CASE X", "TC 1", "2012-06-10", "female", EXTRA_DETAILS).setIsClosed(true));

        assertEquals(new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS), repository.find("CASE A"));
        assertEquals(new Child("CASE B", "CASE X", "TC 1", "2012-06-10", "female", EXTRA_DETAILS), repository.find("CASE B"));
        assertEquals(new Child("CASE C", "CASE X", "TC 1", "2012-06-10", "female", EXTRA_DETAILS).setIsClosed(true), repository.find("CASE C"));
    }

    public void testShouldCountChildren() throws Exception {
        assertEquals(0, repository.count());

        repository.add(new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS));
        assertEquals(1, repository.count());

        repository.add(new Child("CASE B", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS));
        assertEquals(2, repository.count());

        repository.add(new Child("CASE C", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS).setIsClosed(true));
        assertEquals(2, repository.count());

        repository.close("CASE B");
        assertEquals(1, repository.count());
    }

    public void testShouldMarkAsClosedWhenAChildIsClosed() throws Exception {
        Child firstChild = new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS);
        Child secondChild = new Child("CASE B", "CASE X", "TC 1", "2012-06-10", "female", EXTRA_DETAILS);

        repository.add(firstChild);
        repository.add(secondChild);
        alertRepository.createAlert(new Alert("CASE A", "Ante Natal Care - Normal", "ANC 1", AlertStatus.normal, "2012-01-01", "2012-01-11"));
        alertRepository.createAlert(new Alert("CASE B", "Ante Natal Care - Normal", "ANC 1", AlertStatus.normal, "2012-01-01", "2012-01-11"));

        repository.close("CASE A");

        assertEquals(firstChild.setIsClosed(true), repository.find(firstChild.caseId()));
        assertEquals(secondChild, repository.find(secondChild.caseId()));
    }

    public void testShouldDeleteCorrespondingTimelineEventsWhenAChildIsDeleted() throws Exception {
        repository.add(new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS));
        repository.add(new Child("CASE B", "CASE X", "TC 1", "2012-06-10", "female", EXTRA_DETAILS));

        repository.close("CASE A");

        assertEquals(new ArrayList<TimelineEvent>(), timelineEventRepository.allFor("CASE A"));
    }

    public void testShouldUpdateMotherDetails() throws Exception {
        Map<String, String> details = mapOf("some-key", "some-value");
        Child child = new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", details);
        repository.add(child);

        Map<String, String> newDetails = create("some-key", "some-new-value").put("some-other-key", "blah").map();
        repository.updateDetails("CASE A", newDetails);

        Child expectedChildWithNewDetails = new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", newDetails);
        assertEquals(asList(expectedChildWithNewDetails), repository.all());
    }

    public void testShouldFindAllChildrenByCaseIds() {
        Child child1 = new Child("CASE A", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS);
        Child child2 = new Child("CASE B", "CASE X", "TC 1", "2012-06-09", "female", EXTRA_DETAILS);
        Child child3 = new Child("CASE C", "CASE Y", "TC 2", "2012-06-09", "female", EXTRA_DETAILS).setIsClosed(true);
        repository.add(child1);
        repository.add(child2);
        repository.add(child3);

        List<Child> childrenByCaseIds = repository.findChildrenByCaseIds("CASE A", "CASE C");

        assertEquals(asList(child1, child3), childrenByCaseIds);
    }

    public void testShouldFetchAllChildrenWithECAndMother() throws Exception {
        Map<String, String> ecDetails = create("wifeAge", "26")
                .put("caste", "others")
                .put("economicStatus", "apl")
                .map();
        Map<String, String> childDetails = create("weight", "3")
                .put("name", "chinnu")
                .put("isChildHighRisk", "yes")
                .map();
        EligibleCouple ec = new EligibleCouple("ec id 1", "amma", "appa", "ec no 1", "chikkamagalur", null, ecDetails).asOutOfArea();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi no 1", "2013-01-01").withDetails(Collections.<String, String>emptyMap());
        Child child = new Child("child id 1", "mother id 1", "thayi no 1", "2013-01-02", "female", childDetails).withMother(mother).withEC(ec);
        repository.add(child);
        motherRepository.add(mother);
        ecRepository.add(ec);

        List<Child> children = repository.allChildrenWithMotherAndEC();

        assertTrue(children.contains(child));
    }

    public void testShouldUpdatePhotoPathCase() throws Exception {
        Child child = new Child("CASE X", "Mother 1", "1234567", "2012-01-01", "female", new HashMap<String, String>());
        Child anotherChild = new Child("CASE Y", "Mother 2", "1234567", "2012-01-01", "female", new HashMap<String, String>());
        repository.add(child);
        repository.add(anotherChild);

        repository.updatePhotoPath("CASE X", "photo/path/to/child/x");

        assertEquals("photo/path/to/child/x", repository.find("CASE X").photoPath());
        assertEquals(null, repository.find("CASE Y").photoPath());
    }

    public void testShouldFetchAllChildrenByECId() throws Exception {
        Map<String, String> ecDetails = create("wifeAge", "26")
                .put("caste", "others")
                .put("economicStatus", "apl")
                .map();
        Map<String, String> childDetails = create("weight", "3")
                .put("name", "chinnu")
                .put("isChildHighRisk", "yes")
                .map();
        EligibleCouple ec = new EligibleCouple("ec id 1", "amma", "appa", "ec no 1", "chikkamagalur", null, ecDetails).asOutOfArea();
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi no 1", "2013-01-01").withDetails(Collections.<String, String>emptyMap());
        Child child = new Child("child id 1", "mother id 1", "thayi no 1", "2013-01-02", "female", childDetails);
        EligibleCouple anotherEC = new EligibleCouple("ec id 2", "amma", "appa", "ec no 1", "chikkamagalur", null, ecDetails).asOutOfArea();
        Mother anotherMother = new Mother("mother id 2", "ec id 2", "thayi no 1", "2013-01-01").withDetails(Collections.<String, String>emptyMap());
        Child anotherChild = new Child("child id 2", "mother id 2", "thayi no 1", "2013-01-02", "female", childDetails);
        repository.add(child);
        motherRepository.add(mother);
        ecRepository.add(ec);
        repository.add(anotherChild);
        motherRepository.add(anotherMother);
        ecRepository.add(anotherEC);

        List<Child> children = repository.findAllChildrenByECId("ec id 1");

        assertTrue(children.contains(child));
        assertFalse(children.contains(anotherChild));
    }

    public void testShouldDeleteChild() throws Exception {
        repository.add(new Child("child id 1", "mother id 1", "TC 1", "2012-06-09", "female", EXTRA_DETAILS));

        repository.delete("child id 1");

        assertTrue(repository.all().isEmpty());
    }
}
