package toy.slick.feign.investing;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "InvestingFeign", url = "https://sslecal2.investing.com")
public interface InvestingFeign {

    @GetMapping("")
    Response getEconomicCalendar();
}
