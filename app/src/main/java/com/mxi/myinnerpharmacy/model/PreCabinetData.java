package com.mxi.myinnerpharmacy.model;

/**
 * Created by vishal on 23/2/18.
 */

public class PreCabinetData {

    String id;
    String activity_name;
    String value;
    String date;
    String time;
    String media_id;
    String media_type;
    String media_name;
    String prescription_videopath;

    public String getPrescription_audiopath() {
        return prescription_audiopath;
    }

    public void setPrescription_audiopath(String prescription_audiopath) {
        this.prescription_audiopath = prescription_audiopath;
    }

    public String getPrescription_videopath() {
        return prescription_videopath;
    }

    public void setPrescription_videopath(String prescription_videopath) {
        this.prescription_videopath = prescription_videopath;
    }

    String prescription_audiopath;

    public String getMedia_file() {
        return media_file;
    }

    public void setMedia_file(String media_file) {
        this.media_file = media_file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getMedia_name() {
        return media_name;
    }

    public void setMedia_name(String media_name) {
        this.media_name = media_name;
    }

    String media_file;
}
