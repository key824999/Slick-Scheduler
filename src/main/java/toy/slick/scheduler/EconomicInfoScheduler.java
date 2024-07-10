package toy.slick.scheduler;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Const;
import toy.slick.feign.cnn.reader.CnnFeignReader;
import toy.slick.feign.cnn.CnnFeign;
import toy.slick.feign.cnn.vo.response.FearAndGreed;
import toy.slick.feign.investing.InvestingFeign;
import toy.slick.feign.investing.reader.InvestingFeignReader;
import toy.slick.feign.investing.vo.response.EconomicEvent;
import toy.slick.feign.slick.SlickFeign;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class EconomicInfoScheduler {
    private final String SLICK_REQUEST_API_KEY;

    private final SlickFeign slickFeign;
    private final CnnFeign cnnFeign;
    private final InvestingFeign investingFeign;

    private final CnnFeignReader cnnFeignReader;
    private final InvestingFeignReader investingFeignReader;

    public EconomicInfoScheduler(@Value("${slick.api.requestApiKey}") String SLICK_REQUEST_API_KEY,
                                 SlickFeign slickFeign,
                                 CnnFeign cnnFeign,
                                 InvestingFeign investingFeign,
                                 CnnFeignReader cnnFeignReader, InvestingFeignReader investingFeignReader) {
        this.SLICK_REQUEST_API_KEY = SLICK_REQUEST_API_KEY;
        this.slickFeign = slickFeign;
        this.cnnFeign = cnnFeign;
        this.investingFeign = investingFeign;
        this.cnnFeignReader = cnnFeignReader;
        this.investingFeignReader = investingFeignReader;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "40 25,55 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveEconomicEventList() throws IOException {
        List<EconomicEvent> economicEventList;

        try (Response response = investingFeign.getEconomicCalendar()) {
            economicEventList = investingFeignReader.getEconomicEventList(response);
        }

        if (CollectionUtils.isEmpty(economicEventList)) {
            throw new NullPointerException("economicEventList is empty");
        }

        List<toy.slick.feign.slick.vo.request.EconomicEvent> newEconomicEventList = economicEventList
                .stream()
                .map(economicEvent -> toy.slick.feign.slick.vo.request.EconomicEvent.builder()
                        .actual(economicEvent.getActual())
                        .country(economicEvent.getCountry())
                        .id(economicEvent.getId())
                        .forecast(economicEvent.getForecast())
                        .importance(economicEvent.getImportance())
                        .name(economicEvent.getName())
                        .previous(economicEvent.getPrevious())
                        .zonedDateTime(economicEvent.getZonedDateTime())
                        .build())
                .toList();

        try (Response response = slickFeign.putEconomicEventList(SLICK_REQUEST_API_KEY, newEconomicEventList)) {
            log.info(response.toString());
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "5 */20 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveFearAndGreed() throws IOException {
        Optional<FearAndGreed> fearAndGreed;

        try (Response response = cnnFeign.getFearAndGreed()) {
            fearAndGreed = cnnFeignReader.getFearAndGreed(response);
        }

        if (fearAndGreed.isEmpty()) {
            throw new NullPointerException("fearAndGreed is empty"); // TODO: Exception message -> property
        }

        toy.slick.feign.slick.vo.request.FearAndGreed newFearAndGreed = toy.slick.feign.slick.vo.request.FearAndGreed.builder()
                .rating(fearAndGreed.get().getRating())
                .score(fearAndGreed.get().getScore())
                .build();

        try (Response feignResponse = slickFeign.putFearAndGreed(SLICK_REQUEST_API_KEY, newFearAndGreed)) {
            log.info(feignResponse.toString());
        }
    }
}
