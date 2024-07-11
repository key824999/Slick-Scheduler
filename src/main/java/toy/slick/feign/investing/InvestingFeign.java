package toy.slick.feign.investing;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "InvestingFeign", url = "https://investing.com")
public interface InvestingFeign {

    @GetMapping(value = "/indices/us-30")
    Response getDowJonesIndustrialAverage();

    @GetMapping(value = "/indices/us-spx-500")
    Response getStandardAndPoor500();

    @GetMapping(value = "/indices/nasdaq-composite")
    Response getNasdaqComposite();
}
