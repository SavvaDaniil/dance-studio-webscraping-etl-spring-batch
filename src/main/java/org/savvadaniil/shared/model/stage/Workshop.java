package org.savvadaniil.shared.model.stage;

import java.time.LocalDate;
import java.time.LocalTime;

public class Workshop {

    private LocalTime time_from;
    private LocalTime time_to;
    private String name;
    private String dates;
    private String branch_name;
    private String style_name;
    private int price;
    private LocalDate date_start;

    public Workshop(LocalTime time_from, LocalTime time_to, String name, String dates, String branch_name, String style_name, int price, LocalDate date_start) {
        this.time_from = time_from;
        this.time_to = time_to;
        this.name = name;
        this.dates = dates;
        this.branch_name = branch_name;
        this.style_name = style_name;
        this.price = price;
        this.date_start = date_start;
    }

    public LocalTime getTime_from() {
        return time_from;
    }

    public void setTime_from(LocalTime time_from) {
        this.time_from = time_from;
    }

    public LocalTime getTime_to() {
        return time_to;
    }

    public void setTime_to(LocalTime time_to) {
        this.time_to = time_to;
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

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public String getStyle_name() {
        return style_name;
    }

    public void setStyle_name(String style_name) {
        this.style_name = style_name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public LocalDate getDate_start() {
        return date_start;
    }

    public void setDate_start(LocalDate date_start) {
        this.date_start = date_start;
    }
}
