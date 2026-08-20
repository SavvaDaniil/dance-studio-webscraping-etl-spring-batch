package org.savvadaniil.shared.model.raw;

import java.time.LocalDateTime;

public class Abonement {

    private String title;
    private String priceStr;
    private LocalDateTime extractAt;

    public Abonement(String title, String priceStr, LocalDateTime extractAt) {
        this.title = title;
        this.priceStr = priceStr;
        this.extractAt = extractAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPriceStr() {
        return priceStr;
    }

    public void setPriceStr(String priceStr) {
        this.priceStr = priceStr;
    }

    public LocalDateTime getExtractAt() {
        return extractAt;
    }

    public void setExtractAt(LocalDateTime extractAt) {
        this.extractAt = extractAt;
    }
}
