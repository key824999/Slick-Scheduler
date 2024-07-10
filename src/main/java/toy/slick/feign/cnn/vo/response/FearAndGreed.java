package toy.slick.feign.cnn.vo.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FearAndGreed {
    private String rating;
    private double score;
}
