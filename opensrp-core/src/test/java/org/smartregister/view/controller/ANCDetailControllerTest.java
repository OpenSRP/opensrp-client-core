package org.smartregister.view.controller;

import android.content.Context;

import com.google.gson.Gson;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.domain.TimelineEvent;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.repository.AllTimelineEvents;
import org.smartregister.util.DateUtil;
import org.smartregister.util.EasyMap;
import org.smartregister.view.contract.ANCDetail;
import org.smartregister.view.contract.CoupleDetails;
import org.smartregister.view.contract.LocationDetails;
import org.smartregister.view.contract.PregnancyDetails;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ANCDetailControllerTest extends BaseUnitTest {

    @Mock
    private Context context;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllBeneficiaries allBeneficiaries;
    @Mock
    private AllTimelineEvents allTimelineEvents;

    private String caseId = "1234-5678-1234";
    private ANCDetailController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        DateUtil.fakeIt(new LocalDate(2012, 8, 1));
        controller = new ANCDetailController(context, caseId, allEligibleCouples, allBeneficiaries, allTimelineEvents);
    }

    @Test
    public void shouldGetANCDetailsAsJSON() {
        TimelineEvent pregnancyEvent = TimelineEvent.forStartOfPregnancy(caseId, "2011-10-21", "2011-10-21");
        TimelineEvent ancEvent = TimelineEvent.forANCCareProvided(caseId, "2", "2011-12-22", new HashMap<String, String>());
        TimelineEvent eventVeryCloseToCurrentDate = TimelineEvent.forANCCareProvided(caseId, "2", "2012-07-29", new HashMap<String, String>());

        HashMap<String, String> details = new HashMap<String, String>();
        details.put("ashaName", "Shiwani");

        Mockito.when(allBeneficiaries.findMotherWithOpenStatus(caseId)).thenReturn(new Mother(caseId, "EC CASE 1", "TC 1", "2011-10-22").withDetails(details));
        Map<String, String> ecDetails = EasyMap.mapOf("caste", "st");
        ecDetails.put("economicStatus", "bpl");
        Mockito.when(allEligibleCouples.findByCaseID("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "Woman 1", "Husband 1", "EC Number 1", "Village 1", "Subcenter 1", ecDetails).withPhotoPath("photo path"));
        Mockito.when(allTimelineEvents.forCase(caseId)).thenReturn(Arrays.asList(pregnancyEvent, ancEvent, eventVeryCloseToCurrentDate));

        ANCDetail expectedDetail = new ANCDetail(caseId, "TC 1",
                new CoupleDetails("Woman 1", "Husband 1", "EC Number 1", false)
                        .withCaste("st")
                        .withEconomicStatus("bpl")
                        .withPhotoPath("photo path"),
                new LocationDetails("Village 1", "Subcenter 1"),
                new PregnancyDetails("9", "2012-07-28", 4))
                .addTimelineEvents(Arrays.asList(eventFor(eventVeryCloseToCurrentDate, "29-07-2012"), eventFor(ancEvent, "22-12-2011"), eventFor(pregnancyEvent, "21-10-2011")))
                .addExtraDetails(details);

        String actualJson = controller.get();
        ANCDetail actualDetail = new Gson().fromJson(actualJson, ANCDetail.class);

        Assert.assertEquals(expectedDetail, actualDetail);
    }

    private org.smartregister.view.contract.TimelineEvent eventFor(TimelineEvent pregnancyEvent, String expectedRelativeTime) {
        return new org.smartregister.view.contract.TimelineEvent(pregnancyEvent.type(), pregnancyEvent.title(), new String[]{pregnancyEvent.detail1(), pregnancyEvent.detail2()}, expectedRelativeTime);
    }
}
