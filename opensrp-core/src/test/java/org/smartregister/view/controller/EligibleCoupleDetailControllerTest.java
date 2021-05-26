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
import org.smartregister.domain.TimelineEvent;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.repository.AllTimelineEvents;
import org.smartregister.util.DateUtil;
import org.smartregister.view.contract.Child;
import org.smartregister.view.contract.CoupleDetails;
import org.smartregister.view.contract.ECDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EligibleCoupleDetailControllerTest extends BaseUnitTest {
    @Mock
    private Context context;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private AllTimelineEvents allTimelineEvents;

    private String caseId = "1234-5678-1234";
    private EligibleCoupleDetailController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        DateUtil.fakeIt(new LocalDate(2012, 8, 1));
        controller = new EligibleCoupleDetailController(context, caseId, allEligibleCouples, allTimelineEvents);
    }

    @Test
    public void shouldGetANCDetailsAsJSON() {
        TimelineEvent pregnancyEvent = TimelineEvent.forStartOfPregnancyForEC(caseId, "TC 1", "2011-10-21", "2011-10-21");
        TimelineEvent fpEvent = TimelineEvent.forChangeOfFPMethod(caseId, "condom", "iud", "2011-12-22");
        TimelineEvent eventVeryCloseToCurrentDate = TimelineEvent.forChangeOfFPMethod(caseId, "iud", "condom", "2012-07-29");

        HashMap<String, String> details = new HashMap<String, String>();
        details.put("ashaName", "Shiwani");
        details.put("isHighPriority", "1");

        Mockito.when(allEligibleCouples.findByCaseID(caseId)).thenReturn(new EligibleCouple("EC CASE 1", "Woman 1", "Husband 1", "EC Number 1", "Village 1", "Subcenter 1", details));
        Mockito.when(allTimelineEvents.forCase(caseId)).thenReturn(Arrays.asList(pregnancyEvent, fpEvent, eventVeryCloseToCurrentDate));

        ECDetail expectedDetail = new ECDetail(caseId, "Village 1", "Subcenter 1", "EC Number 1", true, null, null, new ArrayList<Child>(), new CoupleDetails("Woman 1", "Husband 1", "EC Number 1", false), details)
                .addTimelineEvents(Arrays.asList(eventFor(eventVeryCloseToCurrentDate, "29-07-2012"), eventFor(fpEvent, "22-12-2011"), eventFor(pregnancyEvent, "21-10-2011")));

        String actualJson = controller.get();
        ECDetail actualDetail = new Gson().fromJson(actualJson, ECDetail.class);

        Assert.assertEquals(expectedDetail, actualDetail);
    }

    private org.smartregister.view.contract.TimelineEvent eventFor(TimelineEvent pregnancyEvent, String expectedRelativeTime) {
        return new org.smartregister.view.contract.TimelineEvent(pregnancyEvent.type(), pregnancyEvent.title(), new String[]{pregnancyEvent.detail1(), pregnancyEvent.detail2()}, expectedRelativeTime);
    }
}
