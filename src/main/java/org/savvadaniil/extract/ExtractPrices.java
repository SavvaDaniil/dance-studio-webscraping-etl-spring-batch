package org.savvadaniil.extract;

import org.savvadaniil.shared.config.DanceStudioWebsiteConfiguration;
import org.savvadaniil.shared.model.raw.Abonement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExtractPrices {

    private final DanceStudioWebsiteConfiguration danceStudioWebsiteConfiguration;

    public ExtractPrices(DanceStudioWebsiteConfiguration danceStudioWebsiteConfiguration) {
        this.danceStudioWebsiteConfiguration = danceStudioWebsiteConfiguration;
    }

    public List<Abonement> extract(LocalDateTime extractAt,boolean isDebug) throws IOException {
        List<Abonement> abonements = new ArrayList<>();

        Document document = Jsoup.connect(this.danceStudioWebsiteConfiguration.getBaseUrl() + "/prices/").get();

        Elements singlePrices = document.select("div.ds-example-single-price");

        for (Element singlePrice : singlePrices) {

            Elements labels = singlePrice.select("p.mb-0");

            String title = labels
                    .get(0)
                    .select("strong")
                    .text()
                    .strip();

            String price = labels
                    .get(1)
                    .text()
                    .strip();

            if (isDebug) {
                System.out.printf("title: %s; price: %s%n", title, price);
            }

            abonements.add(
                    new Abonement(
                            title,
                            price,
                            extractAt
                    )
            );
        }

        return abonements;
    }
}
