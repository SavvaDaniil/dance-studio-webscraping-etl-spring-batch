package org.savvadaniil.shared.model.raw;

import java.time.LocalDateTime;

public class WorkshopBlock {

    private final LocalDateTime extractAt;
    private final String dates;
    private final String times;
    private final String name;
    private final String branchName;
    private final String styleName;
    private final String description;
    private final String priceStr;

    public WorkshopBlock(LocalDateTime extractAt, String dates, String times, String name, String branchName, String styleName, String description, String priceStr) {
        this.extractAt = extractAt;
        this.dates = dates;
        this.times = times;
        this.name = name;
        this.branchName = branchName;
        this.styleName = styleName;
        this.description = description;
        this.priceStr = priceStr;
    }

    public LocalDateTime getExtractAt() {
        return extractAt;
    }

    public String getDates() {
        return dates;
    }

    public String getTimes() {
        return times;
    }

    public String getName() {
        return name;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getStyleName() {
        return styleName;
    }

    public String getDescription() {
        return description;
    }

    public String getPriceStr() {
        return priceStr;
    }
}
