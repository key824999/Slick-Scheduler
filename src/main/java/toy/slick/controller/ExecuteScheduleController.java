package toy.slick.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import toy.slick.scheduler.TelegramScheduler;

import java.io.IOException;

@RestController
@RequestMapping("/execute/schedule")
public class ExecuteScheduleController {
    private final String PASSWORD;
    private final TelegramScheduler telegramScheduler;

    public ExecuteScheduleController(@Value("${password.executeSchedule}") String password,
                                     TelegramScheduler telegramScheduler) {
        PASSWORD = password;
        this.telegramScheduler = telegramScheduler;
    }

    @GetMapping("/telegram/fearAndGreed")
    public ResponseEntity<String> fearAndGreed(@RequestParam String password) throws IOException {
        if (!StringUtils.equals(PASSWORD, password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        telegramScheduler.sendFearAndGreed();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/telegram/yesterdayEconomicEventList")
    public ResponseEntity<String> yesterdayEconomicEventList(@RequestParam String password) throws IOException {
        if (!StringUtils.equals(PASSWORD, password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        telegramScheduler.sendYesterdayEconomicEventList();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/telegram/sendIndices")
    public ResponseEntity<String> sendIndices(@RequestParam String password) throws IOException {
        if (!StringUtils.equals(PASSWORD, password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        telegramScheduler.sendIndices();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
