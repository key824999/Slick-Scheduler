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
import toy.slick.feign.slick.SlickFeign;
import toy.slick.feign.slick.reader.SlickFeignReader;
import toy.slick.feign.telegram.TelegramFeign;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class TelegramScheduler {
    private final String SLICK_REQUEST_API_KEY;
    private final String SLICK_BOT_API_TOKEN;
    private final String SLICK_CHAT_ID;

    private final SlickFeign slickFeign;
    private final TelegramFeign telegramFeign;

    private final SlickFeignReader slickFeignReader;

    public TelegramScheduler(@Value("${api.key.slick}") String SLICK_REQUEST_API_KEY,
                             @Value("${telegram.bot.slick.apiToken}") String SLICK_BOT_API_TOKEN,
                             @Value("${telegram.chat.slick.id}") String SLICK_CHAT_ID,
                             SlickFeign slickFeign, TelegramFeign telegramFeign,
                             SlickFeignReader slickFeignReader) {
        this.SLICK_REQUEST_API_KEY = SLICK_REQUEST_API_KEY;
        this.SLICK_BOT_API_TOKEN = SLICK_BOT_API_TOKEN;
        this.SLICK_CHAT_ID = SLICK_CHAT_ID;
        this.slickFeign = slickFeign;
        this.telegramFeign = telegramFeign;
        this.slickFeignReader = slickFeignReader;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "0 0 8 * * 1-5", zone = Const.ZoneId.SEOUL)
    @Scheduled(cron = "0 30 8 * * 1-5", zone = Const.ZoneId.NEW_YORK)
    public void sendFearAndGreed() throws IOException {
        Optional<String> message;

        try (Response response = slickFeign.getFearAndGreed(SLICK_REQUEST_API_KEY)) {
            message = slickFeignReader.getFearAndGreedTelegramMessage(response);
        }

        if (message.isEmpty()) {
            throw new NullPointerException("message is empty");
        }

        try (Response response = telegramFeign.sendHtmlWithoutPreview(SLICK_BOT_API_TOKEN, SLICK_CHAT_ID, message.get())) {
            log.info(response.toString());
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "0 35 2 * * *", zone = Const.ZoneId.UTC)
    public void sendYesterdayEconomicEventList() throws IOException {
        Optional<String> message;
        ZonedDateTime yesterdayDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC)).minusDays(1);

        try (Response response = slickFeign.getEconomicEventList(SLICK_REQUEST_API_KEY,
                yesterdayDateTime.format(Const.DateTimeFormat.yyyyMMdd_hyphen.getDateTimeFormatter()))) {
            message = slickFeignReader.getEconomicEventListTelegramMessage(response, yesterdayDateTime);
        }

        if (message.isEmpty()) {
            throw new NullPointerException("message is empty");
        }

        try (Response response = telegramFeign.sendHtmlWithoutPreview(SLICK_BOT_API_TOKEN, SLICK_CHAT_ID, message.get())) {
            log.info(response.toString());
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "0 25 8 * * 2-6", zone = Const.ZoneId.SEOUL)
    public void sendIndices() throws IOException {
        StringBuilder messageBuilder = new StringBuilder();

        try (Response DJIResponse = slickFeign.getDJI(SLICK_REQUEST_API_KEY);
             Response SPXResponse = slickFeign.getSPX(SLICK_REQUEST_API_KEY);
             Response IXICResponse = slickFeign.getIXIC(SLICK_REQUEST_API_KEY)) {
            slickFeignReader.getDJITelegramMessage(DJIResponse).ifPresent(messageBuilder::append);
            slickFeignReader.getSPXTelegramMessage(SPXResponse).ifPresent(messageBuilder::append);
            slickFeignReader.getIXICTelegramMessage(IXICResponse).ifPresent(messageBuilder::append);
        }

        if (messageBuilder.isEmpty()) {
            throw new NullPointerException("message is empty");
        }

        try (Response response = telegramFeign.sendHtmlWithoutPreview(SLICK_BOT_API_TOKEN, SLICK_CHAT_ID, messageBuilder.toString())) {
            log.info(response.toString());
        }
    }
}
