package toy.slick.feign.parser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import toy.slick.common.Const;
import toy.slick.feign.CnnFeign;
import toy.slick.feign.InvestingFeign;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class TelegramMessageParser {
    private final CnnFeign cnnFeign;
    private final InvestingFeign investingFeign;

    public TelegramMessageParser(CnnFeign cnnFeign,
                                 InvestingFeign investingFeign) {
        this.cnnFeign = cnnFeign;
        this.investingFeign = investingFeign;
    }

    public String parseFearAndGreed() throws IOException {
        Response feignResponse = cnnFeign.getFearAndGreed();
        String feignResponseBody = this.bodyToString(feignResponse);

        if (feignResponse.status() >= 400) {
            return feignResponseBody;
        }

        JsonObject jsonObject = JsonParser.parseString(feignResponseBody)
                .getAsJsonObject()
                .get("fear_and_greed")
                .getAsJsonObject();

        String rating = jsonObject.get("rating").getAsString();
        double score = Double.parseDouble(jsonObject.get("score").getAsString());

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append("Current <a href='https://www.cnn.com/markets/fear-and-greed'>CNN Fear & Greed Index</a> Information").append("\n")
                .append(" - Rating : ").append(rating).append("\n")
                .append(" - Score : ").append(String.format("%.2f", score)).append(" ");

        if (score < 25) {
            messageBuilder.append(Const.FACE_SCREAMING_IN_FEAR);
        } else if (score < 30) {
            messageBuilder.append(Const.FEARFUL_FACE);
        } else if (score < 70) {
            messageBuilder.append(Const.THINKING_FACE);
        } else if (score < 75) {
            messageBuilder.append(Const.GRINNING_SQUINTING_FACE);
        } else {
            messageBuilder.append(Const.ZANY_FACE);
        }

        return messageBuilder.toString();
    }

    public String parseEconomicCalendar(ZonedDateTime startTime) throws IOException {
        Response feignResponse = investingFeign.getEconomicCalendar();
        String feignResponseBody = this.bodyToString(feignResponse);

        if (feignResponse.status() >= 400) {
            return feignResponseBody;
        }

        Element table = Jsoup.connect("https://sslecal2.investing.com/").get().getElementById("ecEventsTable");

        String startDay = startTime.format(DateTimeFormatter.ofPattern("EEEE").withLocale(Locale.US));
        Elements dayRows = table.select("tbody tr .theDay");

        Element parseRow = null;

        for (Element dayRow : dayRows) {
            if (dayRow.text().contains(startDay)) {
                parseRow = dayRow.parent();
            }
        }

        Map<String, List<String>> countryContentMap = new HashMap<>();

        while (parseRow != null) {
            parseRow = parseRow.nextElementSibling();

            if (parseRow == null || StringUtils.isEmpty(parseRow.id())) {
                break;
            }

            if (!parseRow.hasAttr("event_attr_id")) {
                continue;
            }

            String importance = parseRow.getElementsByClass("sentiment").first().attr("title").split(" ")[0].trim();
            String actualValue = parseRow.getElementsByClass("act").first().text().trim();

            if ("Low".equals(importance) || StringUtils.isBlank(actualValue)) {
                continue;
            }

            String eventId = parseRow.attr("event_attr_id").trim();
            String eventName = parseRow.getElementsByClass("event").first().text().trim();
            String country = parseRow.getElementsByClass("flagCur").first().getElementsByTag("span").first().attr("title").trim();
            String forecastValue = StringUtils.defaultIfBlank(parseRow.getElementsByClass("fore").first().text().trim(), "<code>-</code>");
            String previousValue = parseRow.getElementsByClass("prev").first().text().trim();


            String content = "<a href='https://m.investing.com/economic-calendar/" + eventId + "'>" + eventName + "</a>" + "\n"
                    + " : " + actualValue + " | " + forecastValue + " | " + previousValue;

            if (!countryContentMap.containsKey(country)) {
                countryContentMap.put(country, new ArrayList<>());
            }

            countryContentMap.get(country).add(content);
        }

        if (countryContentMap.isEmpty()) {
            return Const.CHECK_MARK + " There's no today's important <a href='https://m.investing.com/economic-calendar/'>Economic Index List</a>";
        }

        StringBuilder resultMessageBuilder = new StringBuilder();

        resultMessageBuilder
                .append(Const.CHECK_MARK).append(" <b>Today's important <a href='https://m.investing.com/economic-calendar/'>Economic Index List</a></b>").append("\n")
                .append(" : Actual | Forecast | Previous").append("\n")
                .append("—————————————").append("\n");

        for (String country : countryContentMap.keySet()) {
            resultMessageBuilder
                    .append(Const.FLAG).append("<b>").append(country).append("</b>").append("\n")
                    .append(String.join("\n", countryContentMap.get(country))).append("\n")
                    .append("—————————————").append("\n");
        }

        return resultMessageBuilder.toString();
    }

    private String bodyToString(Response feignResponse) throws IOException {
        return IOUtils.toString(feignResponse.body().asReader(feignResponse.charset()));
    }
}
