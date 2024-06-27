package toy.slick.feign;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SlickFeign", url = "https://port-0-slick-springboot-lxshnvm735fbe3a8.sel5.cloudtype.app")
public interface SlickFeign {

    @GetMapping(value = "/economicInfo/fearAndGreed")
    Response getFearAndGreed();

    @GetMapping(value = "/economicInfo/economicEvent/list/{yyyyMMdd-UTC}")
    Response getEconomicEventList(@PathVariable("yyyyMMdd-UTC") String date);
}
