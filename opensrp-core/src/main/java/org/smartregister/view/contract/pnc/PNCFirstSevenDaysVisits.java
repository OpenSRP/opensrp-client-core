package org.smartregister.view.contract.pnc;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PNCFirstSevenDaysVisits {
    @SerializedName("circles")
    private List<PNCCircleDatum> pncCircleData;
    @SerializedName("statuses")
    private List<PNCStatusDatum> pncStatusData;
    @SerializedName("active_color")
    private PNCStatusColor pncVisitStatusColor;
    @SerializedName("ticks")
    private List<PNCTickDatum> pncTickData;
    @SerializedName("lines")
    private List<PNCLineDatum> pncLineData;
    @SerializedName("day_nos")
    private List<PNCVisitDaysDatum> visitDaysData;

    public PNCFirstSevenDaysVisits(List<PNCCircleDatum> pncCircleData, List<PNCStatusDatum>
            pncStatusData, PNCStatusColor pncVisitStatusColor, List<PNCTickDatum> pncTickData,
                                   List<PNCLineDatum> pncLineData, List<PNCVisitDaysDatum>
                                           visitDaysData) {
        this.pncCircleData = pncCircleData;
        this.pncStatusData = pncStatusData;
        this.pncVisitStatusColor = pncVisitStatusColor;
        this.pncTickData = pncTickData;
        this.pncLineData = pncLineData;
        this.visitDaysData = visitDaysData;
    }

    public List<PNCCircleDatum> pncCircleData() {
        return pncCircleData;
    }

    public List<PNCStatusDatum> pncStatusData() {
        return pncStatusData;
    }

    public PNCStatusColor pncVisitStatusColor() {
        return pncVisitStatusColor;
    }

    public List<PNCTickDatum> pncTickData() {
        return pncTickData;
    }

    public List<PNCLineDatum> pncLineData() {
        return pncLineData;
    }

    public List<PNCVisitDaysDatum> visitDaysData() {
        return visitDaysData;
    }
}
