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
import org.smartregister.domain.Child;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.domain.TimelineEvent;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.repository.AllTimelineEvents;
import org.smartregister.util.DateUtil;
import org.smartregister.view.contract.BirthDetails;
import org.smartregister.view.contract.ChildDetail;
import org.smartregister.view.contract.CoupleDetails;
import org.smartregister.view.contract.LocationDetails;

import java.util.Arrays;
import java.util.HashMap;

public class ChildDetailControllerTest {

    @Mock
    private Context context;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllBeneficiaries allBeneficiaries;
    @Mock
    private AllTimelineEvents allTimelineEvents;

    private String caseId = "1234-5678-1234";
    private ChildDetailController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        DateUtil.fakeIt(new LocalDate(2012, 8, 1));
        controller = new ChildDetailController(context, caseId, allEligibleCouples, allBeneficiaries, allTimelineEvents);
    }

    @Test
    public void shouldGetChildDetailsAsJSON() throws Exception {
        TimelineEvent birthEvent = TimelineEvent.forChildBirthInChildProfile(caseId, "2011-10-21", null, null);
        TimelineEvent ancEvent = TimelineEvent.forMotherPNCVisit(caseId, "2", "2011-12-22", "bps 1", "bpd 1", "temp 1", "hb 1");
        TimelineEvent eventVeryCloseToCurrentDate = TimelineEvent.forMotherPNCVisit(caseId, "2", "2012-07-29", "bps 2", "bpd 2", "temp 2", "hb 2");

        HashMap<String, String> details = new HashMap<String, String>();
        details.put("ashaName", "Shiwani");
        details.put("dateOfDelivery", "2012-07-28");
        details.put("isHighRisk", "yes");
        details.put("highRiskReason", "Anaemia");

        Mockito.when(allBeneficiaries.findChild(caseId)).thenReturn(new Child(caseId, "Mother-Case-Id", "TC 1", "2012-07-28", "male", details).withPhotoPath("photo path"));
        Mockito.when(allBeneficiaries.findMother("Mother-Case-Id")).thenReturn(new Mother(caseId, "EC CASE 1", "TC 1", "2011-10-22").withDetails(details));
        Mockito.when(allEligibleCouples.findByCaseID("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "Woman 1", "Husband 1", "EC Number 1", "Village 1", "Subcenter 1", new HashMap<String, String>()));
        Mockito.when(allTimelineEvents.forCase(caseId)).thenReturn(Arrays.asList(birthEvent, ancEvent, eventVeryCloseToCurrentDate));

        ChildDetail expectedDetail = new ChildDetail(caseId, "TC 1",
                new CoupleDetails("Woman 1", "Husband 1", "EC Number 1", false),
                new LocationDetails("Village 1", "Subcenter 1"),
                new BirthDetails("2012-07-28", "4 days", "male"), "photo path")
                .addTimelineEvents(Arrays.asList(eventFor(eventVeryCloseToCurrentDate, "29-07-2012"), eventFor(ancEvent, "22-12-2011"), eventFor(birthEvent, "21-10-2011")))
                .addExtraDetails(details);

        String actualJson = controller.get();
        ChildDetail actualDetail = new Gson().fromJson(actualJson, ChildDetail.class);

        Assert.assertEquals(expectedDetail, actualDetail);
    }

    private org.smartregister.view.contract.TimelineEvent eventFor(TimelineEvent pregnancyEvent, String expectedRelativeTime) {
        return new org.smartregister.view.contract.TimelineEvent(pregnancyEvent.type(), pregnancyEvent.title(), new String[]{pregnancyEvent.detail1(), pregnancyEvent.detail2()}, expectedRelativeTime);
    }
}
