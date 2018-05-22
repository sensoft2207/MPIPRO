package com.mxi.myinnerpharmacy.model;

/**
 * Created by vishal on 20/2/18.
 */

public class HertRateHistory {

    String datee;
    String timee;
    String breathing_rate;
    String heart_rate;
    String prescriptionname;
    String self_talk_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getCalibration_state_name() {
        return calibration_state_name;
    }

    public void setCalibration_state_name(String calibration_state_name) {
        this.calibration_state_name = calibration_state_name;
    }

    public String getDatee() {
        return datee;
    }

    public void setDatee(String datee) {
        this.datee = datee;
    }

    public String getTimee() {
        return timee;
    }

    public void setTimee(String timee) {
        this.timee = timee;
    }

    public String getBreathing_rate() {
        return breathing_rate;
    }

    public void setBreathing_rate(String breathing_rate) {
        this.breathing_rate = breathing_rate;
    }

    public String getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(String heart_rate) {
        this.heart_rate = heart_rate;
    }

    public String getPrescriptionname() {
        return prescriptionname;
    }

    public void setPrescriptionname(String prescriptionname) {
        this.prescriptionname = prescriptionname;
    }

    public String getSelf_talk_id() {
        return self_talk_id;
    }

    public void setSelf_talk_id(String self_talk_id) {
        this.self_talk_id = self_talk_id;
    }

    String calibration_state_name;
}
