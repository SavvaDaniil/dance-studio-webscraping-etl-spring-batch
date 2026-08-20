package org.savvadaniil.shared.model.load;

import java.time.LocalTime;

public class LoadSchedule {

    private LocalTime timeFrom;
    private int weekday;
    private int branchId;
    private int teacherId;
    private int styleId;
    private Integer levelId;

    public LoadSchedule(LocalTime timeFrom, int weekday, int branchId, int teacherId, int styleId, Integer levelId) {
        this.timeFrom = timeFrom;
        this.weekday = weekday;
        this.branchId = branchId;
        this.teacherId = teacherId;
        this.styleId = styleId;
        this.levelId = levelId;
    }

    public LocalTime getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(LocalTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getStyleId() {
        return styleId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }
}
