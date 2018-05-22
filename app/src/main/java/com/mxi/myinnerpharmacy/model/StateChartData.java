package com.mxi.myinnerpharmacy.model;

import java.io.Serializable;

/**
 * Created by mxi on 2/2/18.
 */

public class StateChartData implements Serializable {

    String dateChart;
    String State_calibration;

    public String getState_calibration() {
        return State_calibration;
    }

    public void setState_calibration(String state_calibration) {
        State_calibration = state_calibration;
    }

    public String getDateChart() {
        return dateChart;
    }

    public void setDateChart(String dateChart) {
        this.dateChart = dateChart;
    }


}
