package org.savvadaniil.extract;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.savvadaniil.shared.config.DanceStudioWebsiteConfiguration;
import org.savvadaniil.shared.model.raw.ScheduleRow;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExtractSchedules {

    private final DanceStudioWebsiteConfiguration danceStudioWebsiteConfiguration;

    public ExtractSchedules(DanceStudioWebsiteConfiguration danceStudioWebsiteConfiguration) {
        this.danceStudioWebsiteConfiguration = danceStudioWebsiteConfiguration;
    }

    public List<ScheduleRow> extract(LocalDateTime extractAt, Page page, boolean isDebug){
        List<ScheduleRow> scheduleRows = new ArrayList<>();
        List<String> weekdays = new ArrayList<>();

        page.navigate(
                this.danceStudioWebsiteConfiguration.getBaseUrl() + "/raspisanie/",
                new Page.NavigateOptions().setTimeout(60_000)
        );

        page.setDefaultTimeout(60_000);

        Locator ul = page.locator("ul.ds-example-filter");
        ul.waitFor();

        Locator branchesLi = ul.locator("li");

        int countBranches = branchesLi.count();

        // Исследуем каждый филиал
        for (int b = 0; b < countBranches; b++) {

            // Нажатие на ссылку филиала
            Locator branchLi = branchesLi.nth(b);
            String branchName = branchLi
                    .locator("a")
                    .innerText()
                    .trim();

            if (b != 0) {
                branchLi.click();
                branchLi.click();

                if (isDebug) {
                    System.out.println("Click по " + branchName);
                }
            }

            if (isDebug) {
                System.out.println("--- Working with branch " + branchName);
            }

            // Ожидание загрузки расписание в iframe
            Locator iframeSchedule = page.locator("div.content iframe");
            iframeSchedule.waitFor(
                    new Locator.WaitForOptions()
                            .setState(WaitForSelectorState.ATTACHED)
            );

            FrameLocator scheduleFrame = page.frameLocator("div.content iframe");
            Locator items = scheduleFrame.locator("div.schedule-item");
            items.first().waitFor();

            // Получаем дни недели только один раз.
            if (weekdays.isEmpty()) {
                Locator div_schedule_week_header = scheduleFrame.locator("div.schedule-week div.schedule-header");
                div_schedule_week_header.waitFor();

                Locator div_schedule_weekdays = div_schedule_week_header.locator("div.schedule-date-wrapper");
                int countWeekdays = div_schedule_weekdays.count();

                for (int w = 0; w < countWeekdays; w++) {
                    String weekday = div_schedule_weekdays
                                    .nth(w)
                                    .locator("span")
                                    .nth(1)
                                    .innerText();

                    weekdays.add(weekday);
                }
            }

            Locator schedule_rows =
                    scheduleFrame.locator(
                            "div.schedule-week div.schedule-row"
                    );

            schedule_rows.first().waitFor();
            int countRows = schedule_rows.count();

            // Рассмотрение каждого строки таблицы времени
            for (int i = 0; i < countRows; i++) {

                Locator schedule_row = schedule_rows.nth(i);

                String scheduleTime = schedule_row
                                .locator("div.schedule-time")
                                .innerText()
                                .trim();

                if (isDebug) {
                    System.out.println("Парсинг времени " + scheduleTime);
                }

                // получение всех горизонтальных ячеек (дней) следующие правее ячейки времени
                Locator schedule_cells = schedule_row.locator("div.schedule-cell");

                int count_schedule_cells = schedule_cells.count();
                if (count_schedule_cells == 0) {
                    continue;
                }

                // Проверяем каждый ячейку дня
                for (int j = 0; j < count_schedule_cells; j++) {

                    if (isDebug) {
                        System.out.println("\tCheck weekday by index " + j);
                    }

                    // ячейки пересения дня и времени, сами занятия
                    Locator schedule_items = schedule_cells
                                    .nth(j)
                                    .locator("div.schedule-item");

                    int countItems = schedule_items.count();
                    if (countItems == 0) {
                        continue;
                    }

                    // сами занятия
                    for (int l = 0; l < countItems; l++) {

                        Locator schedule_item = schedule_items.nth(l);
                        String styleName = schedule_item
                                        .locator("div.card-body div.header")
                                        .innerText()
                                        .trim();

                        String teacherName = schedule_item
                                        .locator("div.card-body div.footer span.desc")
                                        .innerText()
                                        .trim();

                        if (teacherName.contains("Отмена занятия")
                                || teacherName.contains("Перенос")
                                || teacherName.contains("Отпуск")
                        ) {
                            continue;
                        }

                        String levelName = extractLevel(schedule_item);

                        if (isDebug) {
                            System.out.println("\t\t" + styleName + " - " + teacherName + " (" + levelName + ")");
                        }

                        scheduleRows.add(
                                new ScheduleRow(
                                        extractAt,
                                        branchName,
                                        weekdays.get(j),
                                        scheduleTime,
                                        teacherName,
                                        styleName,
                                        levelName
                                )
                        );
                    }
                }
            }
        }

        return scheduleRows;
    }

    @Nullable
    private String extractLevel(Locator scheduleItem) {

        Locator level_names = scheduleItem.locator("div.card-body div.si");

        int count_level_names = level_names.count();
        if (count_level_names == 0) {
            return null;
        }

        if (count_level_names == 1) {
            return level_names.first().innerText().trim();
        }

        for (int i = 0; i < count_level_names; i++) {

            String levelName = level_names
                            .nth(i)
                            .innerText()
                            .trim()
                    .toLowerCase();

            if (levelName.contains("тг")
                    || levelName.contains("@")
                    || levelName.contains("запись")
                    || levelName.contains("вэлком")
            ) {
                continue;
            }

            return levelName;
        }

        return null;
    }
}
