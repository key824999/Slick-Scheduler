package toy.slick.feign.economicCalendar.reader;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import toy.slick.common.Const;
import toy.slick.feign.interfaces.FeignResponseReader;
import toy.slick.feign.economicCalendar.vo.response.EconomicEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InvestingFeignReader implements FeignResponseReader {

    public List<EconomicEvent> getEconomicEventList(Response investingResponse) throws IOException {
        String responseBody = this.getResponseBody(investingResponse);

        Element table = Jsoup.parse(responseBody).getElementById("ecEventsTable");
        Elements rows = table.select("tbody tr");

        return rows.stream()
                .parallel()
                .filter(row -> row.hasAttr("event_attr_id"))
                .filter(row -> StringUtils.isNotBlank(row.getElementsByClass("act").first().text()))
                .map(row -> {
                    ZonedDateTime time = ZonedDateTime.of(LocalDateTime.parse(row.attr("event_timestamp"), Const.DateTimeFormat.yyyyMMddHHmmss.getDateTimeFormatter()), ZoneId.of(Const.ZoneId.UTC));
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
