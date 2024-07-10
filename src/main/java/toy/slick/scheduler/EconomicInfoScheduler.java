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
import toy.slick.feign.CnnFeign;
import toy.slick.feign.InvestingFeign;
import toy.slick.feign.SlickFeign;
import toy.slick.parser.EconomicInfoParser;
import toy.slick.parser.vo.EconomicEvent;
import toy.slick.parser.vo.FearAndGreed;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class EconomicInfoScheduler {
    @Value("${slick.api.requestApiKey}")
    private String slickRequestApiKey;

    private final SlickFeign slickFeign;
    private final CnnFeign cnnFeign;
    private final InvestingFeign investingFeign;
    private final EconomicInfoParser economicInfoParser;

    public EconomicInfoScheduler(SlickFeign slickFeign,
                                 CnnFeign cnnFeign,
                                 InvestingFeign investingFeign,
                                 EconomicInfoParser economicInfoParser) {
        this.slickFeign = slickFeign;
        this.cnnFeign = cnnFeign;
        this.investingFeign = investingFeign;
        this.economicInfoParser = economicInfoParser;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "40 25,55 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveEconomicEventList() throws IOException {
        List<EconomicEvent> economicEventList = economicInfoParser.parseEconomicCalendar(investingFeign.getEconomicCalendar());

        if (CollectionUtils.isNotEmpty(economicEventList)) {
            try (Response response = slickFeign.putEconomicEventList(slickRequestApiKey, economicEventList)) {
                log.info(String.valueOf(response.status()), response);
            }
        } else {
            throw new NullPointerException("Parsing result list is empty");
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "5 */20 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveFearAndGreed() throws IOException {
        Optional<FearAndGreed> fearAndGreed = economicInfoParser.parseFearAndGreed(cnnFeign.getFearAndGreed());

        if (fearAndGreed.isPresent()) {
            try (Response response = slickFeign.putFearAndGreed(slickRequestApiKey, fearAndGreed.get())) {
                log.info(String.valueOf(response.status()), response);
            }
        } else {
            throw new NullPointerException("Parsing result is null"); // TODO: Exception message -> property
        }
    }
}
