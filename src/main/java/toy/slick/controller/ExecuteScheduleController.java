package toy.slick.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.scheduler.TelegramScheduler;

import java.io.IOException;

@RestController
@RequestMapping("/execute/schedule")
public class ExecuteScheduleController {
    private final TelegramScheduler telegramScheduler;

    public ExecuteScheduleController(TelegramScheduler telegramScheduler) {
        this.telegramScheduler = telegramScheduler;
    }

    @TimeLogAspect.TimeLog
    @GetMapping("/fearAndGreed")
    public String executeFearAndGreed() throws IOException {
        telegramScheduler.sendFearAndGreed();

        return HttpStatus.OK.getReasonPhrase();
    }

    @TimeLogAspect.TimeLog
    @GetMapping("/economicEventList")
    public String executeEconomicEventList() throws IOException {
        telegramScheduler.sendYesterdayEconomicEventList();

        return HttpStatus.OK.getReasonPhrase();
    }
}
