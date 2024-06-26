package toy.slick.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Const;
import toy.slick.parser.EconomicInfoParser;
import toy.slick.repository.mongo.EconomicEventRepository;
import toy.slick.repository.mongo.FearAndGreedRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class EconomicInfoScheduler {
    private final EconomicInfoParser economicInfoParser;
    private final FearAndGreedRepository fearAndGreedRepository;
    private final EconomicEventRepository economicEventRepository;

    public EconomicInfoScheduler(EconomicInfoParser economicInfoParser,
                                 FearAndGreedRepository fearAndGreedRepository,
                                 EconomicEventRepository economicEventRepository) {
        this.economicInfoParser = economicInfoParser;
        this.fearAndGreedRepository = fearAndGreedRepository;
        this.economicEventRepository = economicEventRepository;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "40 25,55 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveEconomicCalendar() {
        try {
            List<EconomicEventRepository.EconomicEvent> economicEventList = economicInfoParser.parseEconomicCalendar();

            if (CollectionUtils.isNotEmpty(economicEventList)) {
                economicEventList.stream()
                        .map(economicEvent -> economicEvent.toMongoData(economicEvent.getId()))
                        .forEach(economicEventRepository::save);
            } else {
                throw new NullPointerException("Parsing result list is empty");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "5 */20 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveFearAndGreed() {
        try {
            Optional<FearAndGreedRepository.FearAndGreed> fearAndGreed = economicInfoParser.parseFearAndGreed();

            if (fearAndGreed.isPresent()) {
                String id = new SimpleDateFormat("yyyyMMddHH").format(new Date());

                fearAndGreedRepository.save(fearAndGreed.get().toMongoData(id));
            } else {
                throw new NullPointerException("Parsing result is Null");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
