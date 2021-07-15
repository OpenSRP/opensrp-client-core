package org.smartregister.view.contract;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class ECDetailTest {
    private ECDetail ecDetail;
    private String caseId = "1234-5678-1234";
    @Before
    public void setup(){
       ecDetail = new ECDetail(caseId,"Kogelo","kisumu", "456", true,
       "addres1", "sd-card/photos", new ArrayList<Child>(),null, null );
    }

    @Test
    public void addTimelineEventsReturnsTheSameECDdetailObject() {
        List<TimelineEvent> timelineEvents = new ArrayList<>();
        timelineEvents.add(getAnEvent());
        Assert.assertEquals(ecDetail.addTimelineEvents(timelineEvents), ecDetail);
    }


    private TimelineEvent getAnEvent(){
        return new TimelineEvent("PREGNANCY","pregnancy event",
                new String[]{"ANC","Registered"},
                DateUtil.formatDateForTimelineEvent("2011-10-21"));
    }
}