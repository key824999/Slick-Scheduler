package toy.slick.feign;

import feign.Response;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "InvestingFeign", url = "https://sslecal2.investing.com")
public interface InvestingFeign {

    @GetMapping("")
    Response getEconomicCalendar();

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
