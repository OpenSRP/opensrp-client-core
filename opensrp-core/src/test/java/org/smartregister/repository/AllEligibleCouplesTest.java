package org.smartregister.repository;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.EligibleCouple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllEligibleCouplesTest extends BaseUnitTest {

    @Mock
    private EligibleCoupleRepository eligibleCoupleRepository;
    @Mock
    private TimelineEventRepository timelineEventRepository;
    @Mock
    private AlertRepository alertRepository;

    private AllEligibleCouples allEligibleCouples;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        allEligibleCouples = new AllEligibleCouples(eligibleCoupleRepository, alertRepository, timelineEventRepository);
    }

    @Test
    public void shouldFetchAllAlertsFromRepository() throws Exception {
        List<EligibleCouple> expectedCouples = Arrays.asList(new EligibleCouple("Case X", "Wife 1", "Husband 1", "EC Number 1", "village", "subcenter", new HashMap<String, String>()),
                new EligibleCouple("Case Y", "Wife 2", "Husband 2", "EC Number 2", "village", "subcenter", new HashMap<String, String>()));
        Mockito.when(eligibleCoupleRepository.allEligibleCouples()).thenReturn(expectedCouples);

        List<EligibleCouple> couples = allEligibleCouples.all();

        Assert.assertEquals(expectedCouples, couples);
    }

    @Test
    public void shouldCloseEC() throws Exception {
        allEligibleCouples.close("entity id 1");

        Mockito.verify(alertRepository).deleteAllAlertsForEntity("entity id 1");
        Mockito.verify(eligibleCoupleRepository).close("entity id 1");
        Mockito.verify(timelineEventRepository).deleteAllTimelineEventsForEntity("entity id 1");
    }

    @Test
    public void assertFindByCaseID() {
        Mockito.when(eligibleCoupleRepository.findByCaseID(Mockito.anyString())).thenReturn(Mockito.mock(EligibleCouple.class));
        Assert.assertNotNull(allEligibleCouples.findByCaseID(""));
    }

    @Test
    public void assertFindByCaseIDs() {
        Mockito.when(eligibleCoupleRepository.findByCaseIDs(Mockito.anyString())).thenReturn(Mockito.mock(ArrayList.class));
        Assert.assertNotNull(allEligibleCouples.findByCaseIDs(new ArrayList<String>()));
    }

    @Test
    public void assertUpdatePhotoPathCallsRepositoryUpdate() {
        Mockito.doNothing().when(eligibleCoupleRepository).updatePhotoPath(Mockito.anyString(), Mockito.anyString());
        allEligibleCouples.updatePhotoPath("", "");
        Mockito.verify(eligibleCoupleRepository, Mockito.times(1)).updatePhotoPath(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void assertMergeDetailsCallsRepositoryUpdate() {
        Mockito.doNothing().when(eligibleCoupleRepository).mergeDetails(Mockito.anyString(), Mockito.any(Map.class));
        allEligibleCouples.mergeDetails("", new HashMap<String, String>());
        Mockito.verify(eligibleCoupleRepository, Mockito.times(1)).mergeDetails(Mockito.anyString(), Mockito.any(Map.class));
    }

    @Test
    public void assertCountreturnsLong() {
        Mockito.when(eligibleCoupleRepository.count()).thenReturn(0l);
        Assert.assertEquals(allEligibleCouples.count(), 0l);
    }

    @Test
    public void assertFPCountreturnsLong() {
        Mockito.when(eligibleCoupleRepository.fpCount()).thenReturn(0l);
        Assert.assertEquals(allEligibleCouples.fpCount(), 0l);
    }

    @Test
    public void assertVillagesReturnsList() {
        Mockito.when(eligibleCoupleRepository.villages()).thenReturn(new ArrayList<String>());
        Assert.assertNotNull(allEligibleCouples.villages());
    }

}
