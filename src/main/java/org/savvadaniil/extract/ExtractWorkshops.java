package org.savvadaniil.extract;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.savvadaniil.shared.config.DanceStudioWebsiteConfiguration;
import org.savvadaniil.shared.model.raw.WorkshopBlock;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExtractWorkshops {

    private final DanceStudioWebsiteConfiguration danceStudioWebsiteConfiguration;

    public ExtractWorkshops(DanceStudioWebsiteConfiguration danceStudioWebsiteConfiguration) {
        this.danceStudioWebsiteConfiguration = danceStudioWebsiteConfiguration;
    }

    public List<WorkshopBlock> extract(LocalDateTime extractAt, Page page, boolean isDebug){
        List<WorkshopBlock> workshopBlocks = new ArrayList<>();

        page.navigate(
                this.danceStudioWebsiteConfiguration.getBaseUrl() + "/workshops/",
                new Page.NavigateOptions()
                        .setTimeout(60000)
        );

        Locator workshops_grid_items = page.locator("div.ds-example-workshops-grid-item");

        workshops_grid_items.first().waitFor(
                new Locator.WaitForOptions()
                        .setTimeout(60000)
        );

        int count_workshops_grid_items = workshops_grid_items.count();

        if (isDebug) {
            System.out.println("Количество мастер-классов: " + count_workshops_grid_items);
        }

        for (int i = 0; i < count_workshops_grid_items; i++) {

            if (isDebug) {
                System.out.println("--- Workshop " + (i + 1) + "/" + count_workshops_grid_items );
            }

            Locator workshops_grid_item = workshops_grid_items.nth(i);
            workshops_grid_item.locator("a").click();

            Locator modal = page.locator("div.modal-content").nth(3);

            modal.waitFor(
                    new Locator.WaitForOptions()
                            .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                            .setTimeout(60000)
            );

            Locator workshop_details =
                    modal.locator("div.workshop-details");

            Locator popup_title = workshop_details.locator("div.popup-title");

            String name = popup_title.locator("h3")
                            .innerText()
                            .trim();

            Locator p_text_ends = workshop_details.locator("li p.text-end");

            String dates = p_text_ends.nth(0)
                            .innerText()
                            .trim();

            String times = p_text_ends.nth(1)
                            .innerText()
                            .trim();

            String branchName = p_text_ends.nth(2)
                            .innerText()
                            .trim();

            String styleName = p_text_ends.nth(3)
                            .innerText()
                            .trim();

            Locator workshop_price = modal.locator("div.workshop-price");

            String description = workshop_price.locator("p.mb-0")
                            .nth(0)
                            .innerText()
                            .trim();

            String priceStr = workshop_price.locator("p.mb-0")
                            .nth(1)
                            .locator("strong")
                            .innerText()
                            .trim()
                            //.replace("₽", "")
                            .trim();

            WorkshopBlock workshopBlock = new WorkshopBlock(
                    extractAt,
                    dates,
                    times,
                    name,
                    branchName,
                    styleName,
                    description,
                    priceStr
            );

            workshopBlocks.add(workshopBlock);

            if (isDebug) {
                System.out.println(name + " | "
                        + dates + " | "
                        + times + " | "
                        + branchName + " | "
                        + styleName + " | "
                        + priceStr
                );
            }

            Locator btn_modal_close = modal.locator("button.btn-close");
            btn_modal_close.click();
            modal.waitFor(new Locator.WaitForOptions()
                            .setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN)
                            .setTimeout(60000)
            );
        }

        return workshopBlocks;
    }
}
