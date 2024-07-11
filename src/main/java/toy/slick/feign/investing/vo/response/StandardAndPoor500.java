package toy.slick.feign.investing.vo.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StandardAndPoor500 {
    private String price;
    private String priceChange;
    private String priceChangePercent;
}
