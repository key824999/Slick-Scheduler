package toy.slick.feign.slick;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import toy.slick.feign.slick.vo.request.DowJonesIndustrialAverage;
import toy.slick.feign.slick.vo.request.EconomicEvent;
import toy.slick.feign.slick.vo.request.FearAndGreed;
import toy.slick.feign.slick.vo.request.NasdaqComposite;
import toy.slick.feign.slick.vo.request.StandardAndPoor500;

import java.util.List;

@FeignClient(name = "SlickFeign", url = "https://port-0-slick-api-lxshnvm735fbe3a8.sel5.cloudtype.app/api")
public interface SlickFeign {
    String CODE_SUCCESS = "0000";

    @GetMapping(value = "/economicInfo/fearAndGreed")
    Response getFearAndGreed(@RequestHeader String requestApiKey);

    @PutMapping(value = "/economicInfo/fearAndGreed")
    Response putFearAndGreed(@RequestHeader String requestApiKey,
                             FearAndGreed fearAndGreed);

    @GetMapping(value = "/economicInfo/economicEvent/list/{targetDate}")
    Response getEconomicEventList(@RequestHeader String requestApiKey,
                                  @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") String targetDate);

    @PutMapping(value = "/economicInfo/economicEvent")
    Response putEconomicEvent(@RequestHeader String requestApiKey,
                              EconomicEvent economicEvent);

    @PutMapping(value = "/economicInfo/economicEvent/list")
    Response putEconomicEventList(@RequestHeader String requestApiKey,
                                  List<EconomicEvent> economicEventList);

    @PutMapping(value = "/economicInfo/DJI")
    Response putDJI(@RequestHeader String requestApiKey,
                    DowJonesIndustrialAverage dowJonesIndustrialAverage);

    @PutMapping(value = "/economicInfo/SPX")
    Response putSPX(@RequestHeader String requestApiKey,
                    StandardAndPoor500 standardAndPoor500);

    @PutMapping(value = "/economicInfo/IXIC")
    Response putIXIC(@RequestHeader String requestApiKey,
                     NasdaqComposite nasdaqComposite);

    @GetMapping(value = "/economicInfo/DJI")
    Response getDJI(@RequestHeader String requestApiKey);

    @GetMapping(value = "/economicInfo/SPX")
    Response getSPX(@RequestHeader String requestApiKey);

    @GetMapping(value = "/economicInfo/IXIC")
    Response getIXIC(@RequestHeader String requestApiKey);
}
