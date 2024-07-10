package toy.slick.feign;

import feign.Response;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "SlickFeign", url = "https://port-0-slick-api-lxshnvm735fbe3a8.sel5.cloudtype.app/api")
public interface SlickFeign {

    @GetMapping(value = "/economicInfo/fearAndGreed")
    Response getFearAndGreed(@RequestHeader String requestApiKey);

    @PutMapping(value = "/economicInfo/fearAndGreed")
    Response putFearAndGreed(@RequestHeader String requestApiKey,
                             FearAndGreed fearAndGreed);

    @GetMapping(value = "/economicInfo/economicEvent/list/{yyyy-MM-dd_UTC}")
    Response getEconomicEventList(@RequestHeader String requestApiKey,
                                  @PathVariable("yyyy-MM-dd_UTC") String date);

    @PutMapping(value = "/economicInfo/economicEvent")
    Response putEconomicEvent(@RequestHeader String requestApiKey,
                              EconomicEvent economicEvent);

    @PutMapping(value = "/economicInfo/economicEvent/list")
    Response putEconomicEventList(@RequestHeader String requestApiKey,
                                  List<EconomicEvent> economicEventList);

    @Getter
    @Builder
    class FearAndGreed {
        private String rating;
        private double score;
    }

    @Getter
    @Builder
    class EconomicEvent {
        private String zonedDateTime;
        private String id;
        private String name;
        private String country;
        private String importance;
        private String actual;
        private String forecast;
        private String previous;
    }
}
