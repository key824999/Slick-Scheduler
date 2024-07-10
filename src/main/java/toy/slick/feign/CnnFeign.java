package toy.slick.feign;

import feign.Response;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "CnnFeign", url = "https://production.dataviz.cnn.io")
public interface CnnFeign {

    @GetMapping(value = "/index/fearandgreed/graphdata", headers = {
            "accept-language=ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
            "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    })
    Response getFearAndGreed();

    @Getter
    @Builder
    class FearAndGreed {
        private String rating;
        private double score;
    }
}
