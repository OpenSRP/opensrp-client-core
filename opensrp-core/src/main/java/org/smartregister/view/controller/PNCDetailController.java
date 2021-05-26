package org.smartregister.view.controller;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.AllConstants;
import org.smartregister.domain.EligibleCouple;
import org.smartregister.domain.Mother;
import org.smartregister.repository.AllBeneficiaries;
import org.smartregister.repository.AllEligibleCouples;
import org.smartregister.repository.AllTimelineEvents;
import org.smartregister.util.DateUtil;
import org.smartregister.util.TimelineEventComparator;
import org.smartregister.view.activity.CameraLaunchActivity;
import org.smartregister.view.contract.CoupleDetails;
import org.smartregister.view.contract.LocationDetails;
import org.smartregister.view.contract.PregnancyOutcomeDetails;
import org.smartregister.view.contract.TimelineEvent;
import org.smartregister.view.contract.pnc.PNCDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.smartregister.AllConstants.ENTITY_ID;
import static org.smartregister.AllConstants.WOMAN_TYPE;

public class PNCDetailController {
    private final Context context;
    private final String caseId;
    private final AllEligibleCouples allEligibleCouples;
    private final AllBeneficiaries allBeneficiaries;
    private final AllTimelineEvents allTimelineEvents;

    public PNCDetailController(Context context, String caseId, AllEligibleCouples
            allEligibleCouples, AllBeneficiaries allBeneficiaries, AllTimelineEvents
                                       allTimelineEvents) {
        this.context = context;
        this.caseId = caseId;
        this.allEligibleCouples = allEligibleCouples;
        this.allBeneficiaries = allBeneficiaries;
        this.allTimelineEvents = allTimelineEvents;
    }

    @JavascriptInterface
    public String get() {
        Mother mother = allBeneficiaries.findMotherWithOpenStatus(caseId);
        EligibleCouple couple = allEligibleCouples.findByCaseID(mother.ecCaseId());

        LocalDate deliveryDate = LocalDate.parse(mother.referenceDate());
        Days postPartumDuration = Days.daysBetween(deliveryDate, DateUtil.today());

        PNCDetail detail = new PNCDetail(caseId, mother.thayiCardNumber(),
                new CoupleDetails(couple.wifeName(), couple.husbandName(), couple.ecNumber(),
                        couple.isOutOfArea()).withCaste(couple.getDetail("caste"))
                        .withEconomicStatus(couple.getDetail("economicStatus"))
                        .withPhotoPath(couple.photoPath()),
                new LocationDetails(couple.village(), couple.subCenter()),
                new PregnancyOutcomeDetails(deliveryDate.toString(), postPartumDuration.getDays()))
                .addTimelineEvents(getEvents()).addExtraDetails(mother.details());

        return new Gson().toJson(detail);
    }

    @JavascriptInterface
    public void takePhoto() {
        Intent intent = new Intent(context, CameraLaunchActivity.class);
        intent.putExtra(AllConstants.TYPE, WOMAN_TYPE);
        Mother mother = allBeneficiaries.findMotherWithOpenStatus(caseId);
        intent.putExtra(ENTITY_ID, mother.ecCaseId());
        context.startActivity(intent);
    }

    @JavascriptInterface
    private List<TimelineEvent> getEvents() {
        List<org.smartregister.domain.TimelineEvent> events = allTimelineEvents.forCase(caseId);
        List<TimelineEvent> timelineEvents = new ArrayList<TimelineEvent>();

        Collections.sort(events, new TimelineEventComparator());

        for (org.smartregister.domain.TimelineEvent event : events) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd-MM-YYYY");
            timelineEvents.add(new TimelineEvent(event.type(), event.title(),
                    new String[]{event.detail1(), event.detail2()},
                    event.referenceDate().toString(dateTimeFormatter)));
        }

        return timelineEvents;
    }
}
