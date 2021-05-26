package org.smartregister.repository;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.Child;
import org.smartregister.domain.Mother;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AllBeneficiariesTest extends BaseUnitTest {
    @Mock
    private MotherRepository motherRepository;
    @Mock
    private ChildRepository childRepository;
    @Mock
    private AlertRepository alertRepository;
    @Mock
    private TimelineEventRepository timelineEventRepository;
    @Mock
    private Child child;
    @Mock
    private Mother mother;

    private AllBeneficiaries allBeneficiaries;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        allBeneficiaries = new AllBeneficiaries(motherRepository, childRepository, alertRepository, timelineEventRepository);
    }

    @Test
    public void assertFindMotherWithOpenStatus() {
        Mockito.when(motherRepository.findOpenCaseByCaseID(Mockito.anyString())).thenReturn(Mockito.mock(Mother.class));
        Assert.assertNotNull(allBeneficiaries.findMotherWithOpenStatus(""));
    }

    @Test
    public void assertFindMotherReturnsMother() {
        Mockito.when(motherRepository.findByCaseIds(Mockito.anyString())).thenReturn(new ArrayList<Mother>());
        Assert.assertNull(allBeneficiaries.findMother(""));
        List<Mother> list = new ArrayList<Mother>();
        list.add(Mockito.mock(Mother.class));
        Mockito.when(motherRepository.findByCaseIds(Mockito.anyString())).thenReturn(list);
        Assert.assertNotNull(allBeneficiaries.findMother(""));
    }

    @Test
    public void assertFindMotherByECCaseIdReturnsMother() {
        Mockito.when(motherRepository.findAllCasesForEC(Mockito.anyString())).thenReturn(new ArrayList<Mother>());
        Assert.assertNull(allBeneficiaries.findMotherByECCaseId(""));
        List<Mother> list = new ArrayList<Mother>();
        list.add(Mockito.mock(Mother.class));
        Mockito.when(motherRepository.findAllCasesForEC(Mockito.anyString())).thenReturn(list);
        Assert.assertNotNull(allBeneficiaries.findMotherByECCaseId(""));
    }

    @Test
    public void assertFindAllChildrenByCaseIDs() {
        Mockito.when(childRepository.findChildrenByCaseIds(Mockito.any(String[].class))).thenReturn(Mockito.mock(List.class));
        Assert.assertNotNull(allBeneficiaries.findAllChildrenByCaseIDs(new ArrayList<String>()));
    }

    @Test
    public void assertAllChildrenWithMotherAndEC() {
        Mockito.when(childRepository.allChildrenWithMotherAndEC()).thenReturn(Mockito.mock(List.class));
        Assert.assertNotNull(allBeneficiaries.allChildrenWithMotherAndEC());
    }

    @Test
    public void assertFindAllChildrenByECId() {
        Mockito.when(childRepository.findAllChildrenByECId(Mockito.anyString())).thenReturn(Mockito.mock(List.class));
        Assert.assertNotNull(allBeneficiaries.findAllChildrenByECId(""));
    }

    @Test
    public void assertFindMotherWithOpenStatusByECId() {
        Mockito.when(motherRepository.findMotherWithOpenStatusByECId(Mockito.anyString())).thenReturn(Mockito.mock(Mother.class));
        Assert.assertNotNull(allBeneficiaries.findMotherWithOpenStatusByECId(""));
    }

    @Test
    public void assertIsPregnant() {
        Mockito.when(motherRepository.isPregnant(Mockito.anyString())).thenReturn(false);
        Assert.assertEquals(allBeneficiaries.isPregnant(""), false);
    }

    @Test
    public void assertSwitchMotherToPNC() {
        Mockito.doNothing().when(motherRepository).switchToPNC(Mockito.anyString());
        allBeneficiaries.switchMotherToPNC("");
        Mockito.verify(motherRepository, Mockito.times(1)).switchToPNC(Mockito.anyString());
    }

    @Test
    public void assertFindAllMothersByCaseIDs() {
        Mockito.when(motherRepository.findByCaseIds(Mockito.any(String[].class))).thenReturn(Mockito.mock(List.class));
        Assert.assertNotNull(allBeneficiaries.findAllMothersByCaseIDs(new ArrayList<String>()));
    }

    @Test
    public void assertFindAllChildrenByMotherId() {
        Mockito.when(childRepository.findByMotherCaseId(Mockito.anyString())).thenReturn(Mockito.mock(List.class));
        Assert.assertNotNull(allBeneficiaries.findAllChildrenByMotherId(""));
    }

    @Test
    public void assertFindChild() {
        Mockito.when(childRepository.find(Mockito.anyString())).thenReturn(Mockito.mock(Child.class));
        Assert.assertNotNull(allBeneficiaries.findChild(""));
    }

    @Test
    public void assertANCcountReturnsLong() {
        Mockito.when(motherRepository.ancCount()).thenReturn(0l);
        Assert.assertEquals(allBeneficiaries.ancCount(), 0l);
    }

    @Test
    public void assertPNCcountReturnsLong() {
        Mockito.when(motherRepository.pncCount()).thenReturn(0l);
        Assert.assertEquals(allBeneficiaries.pncCount(), 0l);
    }

    @Test
    public void assertChildcountReturnsLong() {
        Mockito.when(childRepository.count()).thenReturn(0l);
        Assert.assertEquals(allBeneficiaries.childCount(), 0l);
    }

    @Test
    public void assertAllANCsWithECReturnsList() {
        Mockito.when(motherRepository.allMothersOfATypeWithEC(Mockito.anyString())).thenReturn(Mockito.mock(List.class));
        Assert.assertNotNull(allBeneficiaries.allANCsWithEC());
    }

    @Test
    public void assertAllPNCsWithECReturnsList() {
        Mockito.when(motherRepository.allMothersOfATypeWithEC(Mockito.anyString())).thenReturn(Mockito.mock(List.class));
        Assert.assertNotNull(allBeneficiaries.allPNCsWithEC());
    }

    @Test
    public void shouldDeleteTimelineEventsAndAlertsWhileClosingMother() throws Exception {
        allBeneficiaries.closeMother("entity id 1");

        Mockito.verify(alertRepository).deleteAllAlertsForEntity("entity id 1");
        Mockito.verify(timelineEventRepository).deleteAllTimelineEventsForEntity("entity id 1");
    }

    @Test
    public void shouldDeleteTimelineEventsAndAlertsForAllMothersWhenECIsClosed() throws Exception {
        Mockito.when(motherRepository.findAllCasesForEC("ec id 1"))
                .thenReturn(Arrays.asList(new Mother("mother id 1", "ec id 1", "12345", "2012-12-12"), new Mother("mother id 2", "ec id 2", "123456", "2012-12-10")));

        allBeneficiaries.closeAllMothersForEC("ec id 1");

        Mockito.verify(alertRepository).deleteAllAlertsForEntity("mother id 1");
        Mockito.verify(alertRepository).deleteAllAlertsForEntity("mother id 2");
        Mockito.verify(timelineEventRepository).deleteAllTimelineEventsForEntity("mother id 1");
        Mockito.verify(timelineEventRepository).deleteAllTimelineEventsForEntity("mother id 2");
        Mockito.verify(motherRepository).close("mother id 1");
        Mockito.verify(motherRepository).close("mother id 2");
    }

    @Test
    public void shouldDeleteTimelineEventsAndAlertsWhenAChildIsClosed() throws Exception {
        Mockito.when(childRepository.find("child id 1"))
                .thenReturn(new Child("child id 1", "mother id 1", "male", new HashMap<String, String>()));

        allBeneficiaries.closeChild("child id 1");

        Mockito.verify(alertRepository).deleteAllAlertsForEntity("child id 1");
        Mockito.verify(timelineEventRepository).deleteAllTimelineEventsForEntity("child id 1");
        Mockito.verify(childRepository).close("child id 1");
    }

    @Test
    public void shouldNotFailClosingMotherWhenECIsClosedAndDoesNotHaveAnyMothers() throws Exception {
        Mockito.when(motherRepository.findAllCasesForEC("ec id 1")).thenReturn(null);
        Mockito.when(motherRepository.findAllCasesForEC("ec id 1")).thenReturn(Collections.<Mother>emptyList());

        allBeneficiaries.closeAllMothersForEC("ec id 1");

        Mockito.verifyZeroInteractions(alertRepository);
        Mockito.verifyZeroInteractions(timelineEventRepository);
        Mockito.verify(motherRepository, Mockito.times(0)).close(Mockito.any(String.class));
    }

    @Test
    public void shouldDelegateToChildRepositoryWhenUpdateChildIsCalled() throws Exception {
        allBeneficiaries.updateChild(child);

        Mockito.verify(childRepository).update(child);
    }

    @Test
    public void shouldDelegateToMotherRepositoryWhenUpdateMotherIsCalled() throws Exception {
        allBeneficiaries.updateMother(mother);

        Mockito.verify(motherRepository).update(mother);
    }
}
