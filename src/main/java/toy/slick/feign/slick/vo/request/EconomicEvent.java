package toy.slick.feign.slick.vo.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EconomicEvent {
    private String zonedDateTime;
    private String id;
    private String name;
    private String country;
    private String importance;
    private String actual;
    private String forecast;
    private String previous;
}