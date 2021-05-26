package org.smartregister.clientandeventmodel;

import java.util.Date;

public class ContactPoint {

    private String type;
    private String use;
    private String number;
    private int preference;
    private Date startDate;
    private Date endDate;

    public ContactPoint() {

    }

    public ContactPoint(String type, String use, String number, int preference, Date startDate,
                        Date endDate) {
        this.type = type;
        this.use = use;
        this.number = number;
        this.preference = preference;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getPreference() {
        return preference;
    }

    public void setPreference(int preference) {
        this.preference = preference;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

}
