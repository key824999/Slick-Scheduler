package toy.slick.feign.slick.vo.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FearAndGreed {
    private String rating;
    private double score;
}
