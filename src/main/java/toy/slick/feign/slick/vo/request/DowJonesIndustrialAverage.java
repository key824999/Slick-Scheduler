package toy.slick.feign.slick.vo.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DowJonesIndustrialAverage {
    private String price;
    private String priceChange;
    private String priceChangePercent;
}
