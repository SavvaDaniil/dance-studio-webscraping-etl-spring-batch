package org.savvadaniil.shared.model.stage;

import java.time.LocalTime;

public class Schedule {

    private String branch_name;
    private LocalTime time_from;
    private String teacher_name;
    private String style_name;
    private String level_name;
    private int weekday;

    public Schedule(String branch_name, LocalTime time_from, String teacher_name, String style_name, String level_name, int weekday) {
        this.branch_name = branch_name;
        this.time_from = time_from;
        this.teacher_name = teacher_name;
        this.style_name = style_name;
        this.level_name = level_name;
        this.weekday = weekday;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public LocalTime getTime_from() {
        return time_from;
    }

    public void setTime_from(LocalTime time_from) {
        this.time_from = time_from;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getStyle_name() {
        return style_name;
    }

    public void setStyle_name(String style_name) {
        this.style_name = style_name;
    }

    public String getLevel_name() {
        return level_name;
    }

    public void setLevel_name(String level_name) {
        this.level_name = level_name;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }
}
