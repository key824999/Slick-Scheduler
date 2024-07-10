package toy.slick.feign.slick.vo.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FearAndGreed {
    private String rating;
    private double score;
}
