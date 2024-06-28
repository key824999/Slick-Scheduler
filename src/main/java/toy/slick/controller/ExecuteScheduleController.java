package toy.slick.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.slick.scheduler.TelegramScheduler;

import java.io.IOException;

@RestController
@RequestMapping("/execute/schedule")
public class ExecuteScheduleController {
    private final TelegramScheduler telegramScheduler;

    public ExecuteScheduleController(TelegramScheduler telegramScheduler) {
        this.telegramScheduler = telegramScheduler;
    }

    @GetMapping("/telegram/fearAndGreed")
    public String fearAndGreed() throws IOException {
        telegramScheduler.sendFearAndGreed();

        return HttpStatus.OK.getReasonPhrase();
    }

    @GetMapping("/telegram/yesterdayEconomicEventList")
    public String yesterdayEconomicEventList() throws IOException {
        telegramScheduler.sendYesterdayEconomicEventList();

        return HttpStatus.OK.getReasonPhrase();
    }
}
