package org.savvadaniil.shared.model.raw;

import java.time.LocalDateTime;

public class ScheduleRow {

    private final LocalDateTime extractAt;
    private final String branchName;
    private final String weekdayShort;
    private final String timeFrom;
    private final String teacherName;
    private final String styleName;
    private final String level;

    public ScheduleRow(LocalDateTime extractAt, String branchName, String weekdayShort, String timeFrom, String teacherName, String styleName, String level) {
        this.extractAt = extractAt;
        this.branchName = branchName;
        this.weekdayShort = weekdayShort;
        this.timeFrom = timeFrom;
        this.teacherName = teacherName;
        this.styleName = styleName;
        this.level = level;
    }

    public LocalDateTime getExtractAt() {
        return extractAt;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getWeekdayShort() {
        return weekdayShort;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getStyleName() {
        return styleName;
    }

    public String getLevel() {
        return level;
    }
}
