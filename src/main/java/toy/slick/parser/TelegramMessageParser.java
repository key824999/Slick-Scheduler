package toy.slick.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import toy.slick.common.Const;
import toy.slick.feign.SlickFeign;
import toy.slick.feign.interfaces.SlickResponseReader;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TelegramMessageParser implements SlickResponseReader {
    @Value("${slick.api.requestApiKey}")
    private String slickRequestApiKey;

    private final SlickFeign slickFeign;

    public TelegramMessageParser(SlickFeign slickFeign) {
        this.slickFeign = slickFeign;
    }

    public String parseFearAndGreed() throws IOException {
        JsonObject data = this.getDataObject(slickFeign.getFearAndGreed(slickRequestApiKey));

        String rating = data.get("rating").getAsString();
        double score = Double.parseDouble(data.get("score").getAsString());

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder
                .append("Current <a href='https://www.cnn.com/markets/fear-and-greed'>CNN Fear & Greed Index</a> Information").append("\n")
                .append(" - Rating : ")
                .append(rating)
                .append("\n")
                .append(" - Score : ")
                .append(String.format("%.2f", score))
                .append(" ");

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

    public String parseEconomicEventList(ZonedDateTime zonedDateTime) throws IOException {
        JsonArray data = this.getDataArray(slickFeign.getEconomicEventList(slickRequestApiKey,
                zonedDateTime.format(Const.DateTimeFormat.yyyyMMdd.dateTimeFormatter)));

        Map<String, List<String>> countryEconomicEventListMap = new HashMap<>();

        for (JsonElement row : data) {
            JsonObject rowObject = row.getAsJsonObject();

            String importance = rowObject.get("importance").getAsString();
            String eventId = rowObject.get("id").getAsString();
            String eventName = rowObject.get("name").getAsString();
            String country = rowObject.get("country").getAsString();
            String actualValue = rowObject.get("actual").getAsString();
            String forecastValue = StringUtils.defaultIfBlank(rowObject.get("forecast").getAsString(), "-");
            String previousValue = rowObject.get("previous").getAsString();

            if ("Low".equals(importance) || StringUtils.isBlank(actualValue)) {
                continue;
            }

            String message = "<a href='https://m.investing.com/economic-calendar/" + eventId + "'>" + eventName + "</a>" + "\n"
                    + " : " + actualValue + " | " + forecastValue + " | " + previousValue;

            if (!countryEconomicEventListMap.containsKey(country)) {
                countryEconomicEventListMap.put(country, new ArrayList<>());
            }

            countryEconomicEventListMap.get(country).add(message);
        }

        if (countryEconomicEventListMap.isEmpty()) {
            return Const.CHECK_MARK
                    + zonedDateTime.format(Const.DateTimeFormat.yyyyMMdd_DotBlank.dateTimeFormatter)
                    + " [" + zonedDateTime.getZone().getId() + "] "
                    + "No important <a href='https://m.investing.com/economic-calendar/'>Economic Index List</a>";
        }

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder
                .append(Const.CHECK_MARK)
                .append(" <b>")
                .append(zonedDateTime.format(Const.DateTimeFormat.yyyyMMdd_DotBlank.dateTimeFormatter))
                .append(" [")
                .append(zonedDateTime.getZone().getId())
                .append("] ")
                .append("Important <a href='https://m.investing.com/economic-calendar/'>Economic Index List</a></b>")
                .append("\n")
                .append(" : Actual | Forecast | Previous")
                .append("\n")
                .append("————————").append("\n");

        for (String country : countryEconomicEventListMap.keySet()) {
            messageBuilder
                    .append(Const.FLAG)
                    .append("<b>")
                    .append(country)
                    .append("</b>")
                    .append("\n")
                    .append(String.join("\n", countryEconomicEventListMap.get(country)))
                    .append("\n")
                    .append("————————")
                    .append("\n");
        }

        return messageBuilder.toString();
    }
}
