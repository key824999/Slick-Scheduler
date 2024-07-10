package toy.slick.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import toy.slick.common.Const;
import toy.slick.feign.interfaces.FeignResponseReader;
import toy.slick.parser.vo.EconomicEvent;
import toy.slick.parser.vo.FearAndGreed;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EconomicInfoParser implements FeignResponseReader {

    public Optional<FearAndGreed> parseFearAndGreed(Response feignResponse) throws IOException {
        String feignResponseBody = this.bodyToString(feignResponse);

        if (feignResponse.status() >= 400) {
            log.error(feignResponseBody);

            return Optional.empty();
        }

        JsonObject jsonObject = JsonParser.parseString(feignResponseBody)
                .getAsJsonObject()
                .get("fear_and_greed")
                .getAsJsonObject();

        String rating = jsonObject.get("rating").getAsString();
        double score = Double.parseDouble(jsonObject.get("score").getAsString());

        return Optional.of(FearAndGreed.builder()
                .rating(rating)
                .score(score)
                .build());
    }

    public List<EconomicEvent> parseEconomicCalendar(Response feignResponse) throws IOException {
        String feignResponseBody = this.bodyToString(feignResponse);

        if (feignResponse.status() >= 400) {
            log.error(feignResponseBody);

            return Collections.emptyList();
        }

        Element table = Jsoup.parse(feignResponseBody).getElementById("ecEventsTable");
        Elements rows = table.select("tbody tr");

        return rows.stream()
                .parallel()
                .filter(row -> row.hasAttr("event_attr_id"))
                .filter(row -> StringUtils.isNotBlank(row.getElementsByClass("act").first().text()))
                .map(row -> {
                    ZonedDateTime time = ZonedDateTime.of(LocalDateTime.parse(row.attr("event_timestamp"), Const.DateTimeFormat.yyyyMMddHHmmss.dateTimeFormatter), ZoneId.of(Const.ZoneId.UTC));
                    String country = row.getElementsByClass("flagCur").first().getElementsByTag("span").first().attr("title");
                    String importance = row.getElementsByClass("sentiment").first().attr("title").split(" ")[0];
                    String id = row.attr("event_attr_id");
                    String name = row.getElementsByClass("event").first().text();
                    String actual = row.getElementsByClass("act").first().text();
                    String forecast = row.getElementsByClass("fore").first().text();
                    String previous = row.getElementsByClass("prev").first().text();

                    return EconomicEvent.builder()
                            .id(id)
                            .name(name)
                            .zonedDateTime(time.toString())
                            .country(country)
                            .importance(importance)
                            .actual(actual)
                            .forecast(forecast)
                            .previous(previous)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
