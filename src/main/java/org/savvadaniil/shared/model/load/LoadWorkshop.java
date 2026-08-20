package org.savvadaniil.shared.model.load;

import java.time.LocalDate;
import java.time.LocalTime;

public class LoadWorkshop {

    private LocalTime timeFrom;
    private LocalTime timeTo;
    private String name;
    private String dates;
    private int price;
    private LocalDate dateStart;

    private int branchId;
    private int styleId;

    public LoadWorkshop(LocalTime timeFrom, LocalTime timeTo, String name, String dates, int price, LocalDate dateStart, int branchId, int styleId) {
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.name = name;
        this.dates = dates;
        this.price = price;
        this.dateStart = dateStart;
        this.branchId = branchId;
        this.styleId = styleId;
    }

    public LocalTime getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(LocalTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    public LocalTime getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(LocalTime timeTo) {
        this.timeTo = timeTo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getStyleId() {
        return styleId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }
}
