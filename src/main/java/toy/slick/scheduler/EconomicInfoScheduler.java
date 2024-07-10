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
import toy.slick.converter.EconomicInfoConverter;
import toy.slick.feign.CnnFeign;
import toy.slick.feign.InvestingFeign;
import toy.slick.feign.SlickFeign;

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
    private final EconomicInfoConverter economicInfoConverter;

    public EconomicInfoScheduler(@Value("${slick.api.requestApiKey}") String SLICK_REQUEST_API_KEY,
                                 SlickFeign slickFeign,
                                 CnnFeign cnnFeign,
                                 InvestingFeign investingFeign,
                                 EconomicInfoConverter economicInfoConverter) {
        this.SLICK_REQUEST_API_KEY = SLICK_REQUEST_API_KEY;
        this.slickFeign = slickFeign;
        this.cnnFeign = cnnFeign;
        this.investingFeign = investingFeign;
        this.economicInfoConverter = economicInfoConverter;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "40 25,55 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveEconomicEventList() throws IOException {
        List<InvestingFeign.EconomicEvent> investingEconomicEventList;

        try (Response feignResponse = investingFeign.getEconomicCalendar()) {
            investingEconomicEventList = economicInfoConverter.getInvestingEconomicCalendar(feignResponse);
        }

        if (CollectionUtils.isEmpty(investingEconomicEventList)) {
            throw new NullPointerException("Parsing result list is empty");
        }

        List<SlickFeign.EconomicEvent> newEconomicEventList = investingEconomicEventList
                .stream()
                .map(economicEvent -> SlickFeign.EconomicEvent.builder()
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

        try (Response feignResponse = slickFeign.putEconomicEventList(SLICK_REQUEST_API_KEY, newEconomicEventList)) {
            log.info(feignResponse.toString());
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "5 */20 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveFearAndGreed() throws IOException {
        Optional<CnnFeign.FearAndGreed> cnnFearAndGreed;

        try (Response feignResponse = cnnFeign.getFearAndGreed()) {
            cnnFearAndGreed = economicInfoConverter.getCnnFearAndGreed(feignResponse);
        }

        if (cnnFearAndGreed.isEmpty()) {
            throw new NullPointerException("Parsing result is null"); // TODO: Exception message -> property
        }

        SlickFeign.FearAndGreed newFearAndGreed = SlickFeign.FearAndGreed.builder()
                .rating(cnnFearAndGreed.get().getRating())
                .score(cnnFearAndGreed.get().getScore())
                .build();

        try (Response feignResponse = slickFeign.putFearAndGreed(SLICK_REQUEST_API_KEY, newFearAndGreed)) {
            log.info(feignResponse.toString());
        }
    }
}
