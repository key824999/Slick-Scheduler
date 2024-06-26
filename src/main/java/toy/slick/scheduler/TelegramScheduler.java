package toy.slick.scheduler;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Const;
import toy.slick.feign.TelegramFeign;
import toy.slick.feign.parser.TelegramMessageParser;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class TelegramScheduler {
    private final String SLICK_BOT_API_TOKEN;
    private final String SLICK_CHAT_ID;
    private final TelegramFeign telegramFeign;
    private final TelegramMessageParser telegramMessageParser;

    public TelegramScheduler(@Value("${telegram.bot.slick.apiToken}") String SLICK_BOT_API_TOKEN,
                             @Value("${telegram.chat.slick.id}") String SLICK_CHAT_ID,
                             TelegramFeign telegramFeign,
                             TelegramMessageParser telegramMessageParser) {
        this.SLICK_BOT_API_TOKEN = SLICK_BOT_API_TOKEN;
        this.SLICK_CHAT_ID = SLICK_CHAT_ID;
        this.telegramFeign = telegramFeign;
        this.telegramMessageParser = telegramMessageParser;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "0 0 8 * * 1-5", zone = Const.ZoneId.SEOUL)
    public void sendFearAndGreedSeoulTimeZone() throws Exception {
        String message = telegramMessageParser.parseFearAndGreed();

        try (Response response = telegramFeign.sendHtmlWithoutPreview(SLICK_BOT_API_TOKEN, SLICK_CHAT_ID, message)) {
            log.info(response.toString());
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "0 30 8 * * 1-5", zone = Const.ZoneId.NEW_YORK)
    public void sendFearAndGreedNewYorkTimeZone() throws Exception {
        String message = telegramMessageParser.parseFearAndGreed();

        try (Response response = telegramFeign.sendHtmlWithoutPreview(SLICK_BOT_API_TOKEN, SLICK_CHAT_ID, message)) {
            log.info(response.toString());
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "0 55 23 * * *", zone = Const.ZoneId.NEW_YORK)
    public void sendEconomicCalendar() throws Exception {
        String message = telegramMessageParser.parseEconomicCalendar(ZonedDateTime.now(ZoneId.of(Const.ZoneId.NEW_YORK)));

        try (Response response = telegramFeign.sendHtmlWithoutPreview(SLICK_BOT_API_TOKEN, SLICK_CHAT_ID, message)) {
            log.info(response.toString());
        }
    }
}
