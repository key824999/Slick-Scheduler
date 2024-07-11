package toy.slick.feign.slick.vo.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StandardAndPoor500 {
    private String price;
    private String priceChange;
    private String priceChangePercent;
}
