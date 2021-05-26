package org.smartregister.repository;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.util.DateUtil;
import org.smartregister.util.EasyMap;
import org.smartregister.util.Session;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.smartregister.util.EasyMap.mapOf;

public class MotherRepositoryTest extends AndroidTestCase {
    private MotherRepository repository;
    private AlertRepository alertRepository;
    private EligibleCoupleRepository eligibleCoupleRepository;
    private LocalDate today;

    @Override
    protected void setUp() throws Exception {
        today = DateUtil.today();
        alertRepository = new AlertRepository();

        repository = new MotherRepository();

        eligibleCoupleRepository = new EligibleCoupleRepository();

        Session session = new Session().setPassword("password").setRepositoryName("opensrp.db" + new Date().getTime());
        new Repository(new RenamingDelegatingContext(getContext(), "test_"), session, repository, alertRepository, eligibleCoupleRepository);
    }

    public void testShouldInsertMother() throws Exception {
        Map<String, String> details = mapOf("some-key", "some-value");
        Mother mother = new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withDetails(details).withType("ANC");

        repository.add(mother);

        assertEquals(asList(mother), repository.allANCs());
    }

    public void testShouldFetchANCAndCorrespondingEC() throws Exception {
        Map<String, String> details = EasyMap.create("some-key", "some-value").map();
        EligibleCouple firstEligibleCouple = new EligibleCouple("EC Case 1", "Wife 1", "Husband 1", "EC Number 1", "Village 1", "SubCenter 1", details).withPhotoPath("photo path");
        EligibleCouple secondEligibleCouple = new EligibleCouple("EC Case 2", "Wife 2", "Husband 2", "EC Number 2", "Village 2", "SubCenter 2", details);
        EligibleCouple thirdEligibleCouple = new EligibleCouple("EC Case 3", "Wife 3", "Husband 3", "EC Number 3", "Village 3", "SubCenter 3", details);
        Mother firstMother = new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withDetails(details).withType("ANC");
        Mother secondMother = new Mother("CASE Y", "EC Case 2", "TC 2", "2012-06-08").withType("ANC");
        Mother thirdMother = new Mother("CASE Z", "EC Case 3", "TC 3", "2012-06-08").setIsClosed(true).withType("FP");
        eligibleCoupleRepository.add(firstEligibleCouple);
        eligibleCoupleRepository.add(secondEligibleCouple);
        eligibleCoupleRepository.add(thirdEligibleCouple);
        repository.add(firstMother);
        repository.add(secondMother);
        repository.add(thirdMother);


        List<Pair<Mother, EligibleCouple>> ancsWithEC = repository.allMothersOfATypeWithEC("ANC");

        assertEquals(asList(Pair.of(firstMother, firstEligibleCouple), Pair.of(secondMother, secondEligibleCouple)), ancsWithEC);
    }

    public void testShouldFetchPNCAndCorrespondingEC() throws Exception {
        Map<String, String> details = mapOf("some-key", "some-value");
        EligibleCouple firstEligibleCouple = new EligibleCouple("EC Case 1", "Wife 1", "Husband 1", "EC Number 1", "Village 1", "SubCenter 1", details);
        EligibleCouple secondEligibleCouple = new EligibleCouple("EC Case 2", "Wife 2", "Husband 2", "EC Number 2", "Village 2", "SubCenter 2", details);
        EligibleCouple thirdEligibleCouple = new EligibleCouple("EC Case 3", "Wife 3", "Husband 3", "EC Number 3", "Village 3", "SubCenter 3", details);
        Mother firstMother = new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withDetails(details).withType("PNC");
        Mother secondMother = new Mother("CASE Y", "EC Case 2", "TC 2", "2012-06-08").withType("PNC");
        Mother thirdMother = new Mother("CASE Z", "EC Case 3", "TC 3", "2012-06-08").setIsClosed(true).withType("ANC");
        eligibleCoupleRepository.add(firstEligibleCouple);
        eligibleCoupleRepository.add(secondEligibleCouple);
        eligibleCoupleRepository.add(thirdEligibleCouple);
        repository.add(firstMother);
        repository.switchToPNC("CASE X");
        repository.add(secondMother);
        repository.switchToPNC("CASE Y");
        repository.add(thirdMother);
        repository.switchToPNC("CASE Z");


        List<Pair<Mother, EligibleCouple>> pncsWithEC = repository.allMothersOfATypeWithEC("PNC");

        assertEquals(asList(Pair.of(firstMother, firstEligibleCouple), Pair.of(secondMother, secondEligibleCouple)), pncsWithEC);
    }

    public void testShouldLoadAllANCsBasedOnType() throws Exception {
        Mother mother = new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("ANC");
        repository.add(mother);
        repository.add(new Mother("CASE Y", "EC Case 2", "TC 2", today.minusDays(280).toString()).withType("ANC"));
        repository.switchToPNC("CASE Y");

        assertEquals(asList(mother), repository.allANCs());
    }

    public void testShouldNotLoadClosedANC() throws Exception {
        Mother firstMother = new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("ANC");
        Mother secondMother = new Mother("CASE Y", "EC Case 2", "TC 2", "2012-06-08").setIsClosed(true).withType("ANC");
        repository.add(firstMother);
        repository.add(secondMother);

        assertEquals(asList(firstMother), repository.allANCs());
    }

    public void testShouldSwitchWomanToPNC() throws Exception {
        repository.add(new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("ANC"));
        repository.add(new Mother("CASE Y", "EC Case 2", "TC 2", "2012-06-08").withType("PNC"));

        repository.switchToPNC("CASE X");

        assertEquals(asList(new Mother("CASE Y", "EC Case 2", "TC 2", "2012-06-08").withType("ANC")), repository.allANCs());
        assertEquals(asList(new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("PNC")), repository.allPNCs());
    }

    public void testShouldNotFetchPNCIfWomanCaseIsClosed() throws Exception {
        repository.add(new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").setIsClosed(true));

        repository.switchToPNC("CASE X");

        assertEquals(Collections.<Mother>emptyList(), repository.allPNCs());
    }

    public void testShouldFindAMotherByCaseId() throws Exception {
        repository.add(new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("ANC"));
        repository.add(new Mother("CASE Y", "EC Case 2", "TC 2", "2012-06-08").withType("ANC"));

        assertEquals(new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("ANC"), repository.findOpenCaseByCaseID("CASE X"));
        assertEquals(new Mother("CASE Y", "EC Case 2", "TC 2", "2012-06-08").withType("ANC"), repository.findOpenCaseByCaseID("CASE Y"));
        assertEquals(null, repository.findOpenCaseByCaseID("CASE NOT FOUND"));
    }

    public void testShouldNotFindAClosedMotherByCaseId() throws Exception {
        repository.add(new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("ANC"));
        repository.add(new Mother("CASE Y", "EC Case 2", "TC 2", "2012-06-08").setIsClosed(true).withType("EC"));

        assertEquals(new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("ANC"), repository.findOpenCaseByCaseID("CASE X"));
        assertEquals(null, repository.findOpenCaseByCaseID("CASE Y"));
        assertEquals(null, repository.findOpenCaseByCaseID("CASE NOT FOUND"));
    }

    public void testShouldCountANCsAndPNCs() throws Exception {
        repository.add(new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("ANC"));
        repository.add(new Mother("CASE Y", "EC Case 1", "TC 2", "2012-06-08").withType("ANC"));
        repository.add(new Mother("CASE Z", "EC Case 2", "TC 3", "2012-06-08").setIsClosed(true).withType("ANC"));
        assertEquals(2, repository.ancCount());
        assertEquals(0, repository.pncCount());

        repository.switchToPNC("CASE X");
        repository.switchToPNC("CASE Z");
        assertEquals(1, repository.ancCount());
        assertEquals(1, repository.pncCount());

        repository.close("CASE Y");
        assertEquals(0, repository.ancCount());
        assertEquals(1, repository.pncCount());

        repository.close("CASE NOT FOUND");
        assertEquals(0, repository.ancCount());
        assertEquals(1, repository.pncCount());
    }

    public void testShouldMarkAsClosedWhenMotherIsClosed() throws Exception {
        Mother mother = new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("ANC");
        repository.add(mother);

        repository.close(mother.caseId());
        assertEquals(asList(mother.setIsClosed(true)), repository.findByCaseIds(mother.caseId()));
    }

    public void testShouldCloseAllMothersForEC() throws Exception {
        Mother mother1 = new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("ANC");
        Mother mother2 = new Mother("CASE Y", "EC Case 1", "TC 2", "2012-06-08").withType("ANC");
        Mother mother3 = new Mother("CASE Z", "EC Case 2", "TC 3", "2012-06-08").withType("ANC");

        repository.add(mother1);
        repository.add(mother2);
        repository.add(mother3);

        repository.closeAllCasesForEC("EC Case 1");

        assertEquals(asList(mother3), repository.allANCs());
    }

    public void testShouldFindAllChildrenByCaseIds() {
        Mother mother1 = new Mother("CASE X", "EC Case 1", "TC 1", "2012-06-08").withType("ANC");
        Mother mother2 = new Mother("CASE Y", "EC Case 1", "TC 2", "2012-06-08").withType("ANC");
        Mother mother3 = new Mother("CASE Z", "EC Case 2", "TC 3", "2012-06-08").setIsClosed(true).withType("ANC");
        repository.add(mother1);
        repository.add(mother2);
        repository.add(mother3);

        List<Mother> mothers = repository.findByCaseIds("CASE X", "CASE Z");

        assertEquals(asList(mother1, mother3), mothers);
    }

    public void testShouldFindAOpenMotherByECId() throws Exception {
        repository.add(new Mother("mother id 1", "ec id 1", "TC 1", "2012-06-08").withType("ANC"));
        repository.add(new Mother("mother id 3", "ec id 1", "TC 1", "2012-06-08").withType("ANC").setIsClosed(true));
        repository.add(new Mother("mother id 2 ", "ec id 2", "TC 2", "2012-06-08").setIsClosed(true).withType("EC"));

        assertEquals(new Mother("mother id 1", "ec id 1", "TC 1", "2012-06-08").withType("ANC"), repository.findMotherWithOpenStatusByECId("ec id 1"));
        assertEquals(null, repository.findMotherWithOpenStatusByECId("ec id 2 "));
        assertEquals(null, repository.findMotherWithOpenStatusByECId("non existent EC"));
    }

    public void testShouldFindIfTheMotherIsPregnantByECId() throws Exception {
        repository.add(new Mother("mother id 1", "ec id 1", "TC 1", "2012-06-08").withType("ANC"));
        repository.add(new Mother("mother id 2 ", "ec id 2", "TC 2", "2012-06-08").setIsClosed(true).withType("EC"));
        repository.add(new Mother("mother id 3", "ec id 3", "TC 1", "2012-06-08").withType("ANC").setIsClosed(true));
        Mother pnc = new Mother("mother id 4 ", "ec id 4", "TC 4", "2012-06-08");
        repository.add(pnc);
        repository.switchToPNC(pnc.caseId());

        assertTrue(repository.isPregnant("ec id 1"));
        assertFalse(repository.isPregnant("ec id 2"));
        assertFalse(repository.isPregnant("ec id 3"));
        assertFalse(repository.isPregnant("ec id 4"));
    }

    public void testShouldUpdateMother() throws Exception {
        Mother mother = new Mother("mother id 1", "ec id 1", "TC 1", "2012-06-08").withType("ANC").setIsClosed(false);
        repository.add(mother);

        mother.setIsClosed(true);
        repository.update(mother);

        assertEquals(mother, repository.findById("mother id 1"));
    }
}
