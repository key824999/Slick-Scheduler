package toy.slick.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping("/fearAndGreed/{zoneId}")
    public String executeFearAndGreed(@PathVariable String zoneId) throws IOException {
        switch (zoneId) {
            case "NewYork" -> telegramScheduler.sendFearAndGreedNewYorkTimeZone();
            case "Seoul" -> telegramScheduler.sendFearAndGreedSeoulTimeZone();
        }

        return HttpStatus.OK.getReasonPhrase();
    }

    @TimeLogAspect.TimeLog
    @GetMapping("/economicEventList")
    public String executeEconomicEventList() throws IOException {
        telegramScheduler.sendEconomicEventList();

        return HttpStatus.OK.getReasonPhrase();
    }
}
