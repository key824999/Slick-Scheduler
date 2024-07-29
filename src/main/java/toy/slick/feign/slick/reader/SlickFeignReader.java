package toy.slick.feign.slick.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import toy.slick.common.Const;
import toy.slick.feign.interfaces.FeignResponseReader;
import toy.slick.feign.slick.SlickFeign;
import toy.slick.feign.slick.vo.response.EconomicEvent;
import toy.slick.feign.slick.vo.response.FearAndGreed;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class SlickFeignReader implements FeignResponseReader {

    public Optional<String> getFearAndGreedTelegramMessage(Response slickResponse) throws IOException {
        Optional<FearAndGreed> fearAndGreed = this.getFearAndGreed(slickResponse);

        if (fearAndGreed.isEmpty()) {
            throw new NullPointerException("fearAndGreed is empty");
        }

        String rating = fearAndGreed.get().getRating();
        double score = fearAndGreed.get().getScore();

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

        return messageBuilder.isEmpty()
                ? Optional.empty()
                : Optional.of(messageBuilder.toString());
    }

    public Optional<String> getEconomicEventListTelegramMessage(Response slickResponse, ZonedDateTime targetDateTime) throws IOException {
        List<EconomicEvent> economicEventList = this.getEconomicEventList(slickResponse);

        Map<String, List<String>> countryEconomicEventListMap = new HashMap<>();

        for (EconomicEvent economicEvent : economicEventList) {
            String importance = economicEvent.getImportance();
            String eventId = economicEvent.getId();
            String eventName = economicEvent.getName();
            String country = economicEvent.getCountry();
            String actualValue = economicEvent.getActual();
            String forecastValue = StringUtils.defaultIfBlank(economicEvent.getForecast(), "-");
            String previousValue = economicEvent.getPrevious();

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
            return Optional.of(Const.CHECK_MARK
                    + " <b>"
                    + targetDateTime.format(Const.DateTimeFormat.yyyyMMdd_DotBlank.getDateTimeFormatter())
                    + " [" + targetDateTime.getZone().getId() + "]\n"
                    + "Important <a href='https://m.investing.com/economic-calendar/'>Economic Index List</a> is Empty</b>");
        }

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder
                .append(Const.CHECK_MARK)
                .append(" <b>")
                .append(targetDateTime.format(Const.DateTimeFormat.yyyyMMdd_DotBlank.getDateTimeFormatter()))
                .append(" [")
                .append(targetDateTime.getZone().getId())
                .append("]")
                .append("\n")
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

        return messageBuilder.isEmpty()
                ? Optional.empty()
                : Optional.of(messageBuilder.toString());
    }

    private Optional<FearAndGreed> getFearAndGreed(Response slickResponse) throws IOException {
        JsonObject data = this.getDataObject(slickResponse);

        String rating = data.get("rating").getAsString();
        double score = Double.parseDouble(data.get("score").getAsString());

        return StringUtils.isBlank(rating)
                ? Optional.empty()
                : Optional.of(FearAndGreed.builder()
                .rating(rating)
                .score(score)
                .build());
    }

    private List<EconomicEvent> getEconomicEventList(Response slickResponse) throws IOException {
        List<EconomicEvent> economicEventList = new ArrayList<>();

        JsonArray data = this.getDataArray(slickResponse);

        for (JsonElement row : data) {
            JsonObject rowObject = row.getAsJsonObject();

            economicEventList.add(EconomicEvent.builder()
                    .zonedDateTime(rowObject.get("zonedDateTime").getAsString())
                    .importance(rowObject.get("importance").getAsString())
                    .id(rowObject.get("id").getAsString())
                    .name(rowObject.get("name").getAsString())
                    .country(rowObject.get("country").getAsString())
                    .actual(rowObject.get("actual").getAsString())
                    .forecast(rowObject.get("forecast").getAsString())
                    .previous(rowObject.get("previous").getAsString())
                    .build());
        }

        return economicEventList;
    }

    public Optional<String> getSPXTelegramMessage(Response SPXResponse) throws IOException {
        JsonObject SPXJsonObj = this.getDataObject(SPXResponse);

        String price = SPXJsonObj.get("price").getAsString();
        String priceChange = SPXJsonObj.get("priceChange").getAsString();
        String priceChangePercent = SPXJsonObj.get("priceChangePercent").getAsString();
        String titleIcon = priceChange.startsWith("-") ? Const.DOWN_CHART : Const.UP_CHART;

        return StringUtils.isBlank(SPXJsonObj.get("price").getAsString())
                ? Optional.empty()
                : Optional.of(titleIcon + "<b><a href='https://www.investing.com/indices/us-spx-500'>S&P 500 (SPX)</a></b>\n"
                + " - price : <b><u>" + price + "</u></b>\n"
                + " - change : <b><u>" + priceChange + " (" + priceChangePercent + ")</u></b>\n");
    }

    public Optional<String> getDJITelegramMessage(Response DJIResponse) throws IOException {
        JsonObject DJIJsonObj = this.getDataObject(DJIResponse);

        String price = DJIJsonObj.get("price").getAsString();
        String priceChange = DJIJsonObj.get("priceChange").getAsString();
        String priceChangePercent = DJIJsonObj.get("priceChangePercent").getAsString();
        String titleIcon = priceChange.startsWith("-") ? Const.DOWN_CHART : Const.UP_CHART;

        return StringUtils.isBlank(DJIJsonObj.get("price").getAsString())
                ? Optional.empty()
                : Optional.of(titleIcon + "<b><a href='https://www.investing.com/indices/us-30'>Dow Jones Industrial Average (DJI)</a></b>\n"
                + " - price : <b><u>" + price + "</u></b>\n"
                + " - change : <b><u>" + priceChange + " (" + priceChangePercent + ")</u></b>\n");
    }

    public Optional<String> getIXICTelegramMessage(Response IXICResponse) throws IOException {
        JsonObject IXICJsonObj = this.getDataObject(IXICResponse);

        String price = IXICJsonObj.get("price").getAsString();
        String priceChange = IXICJsonObj.get("priceChange").getAsString();
        String priceChangePercent = IXICJsonObj.get("priceChangePercent").getAsString();
        String titleIcon = priceChange.startsWith("-") ? Const.DOWN_CHART : Const.UP_CHART;

        return StringUtils.isBlank(IXICJsonObj.get("price").getAsString())
                ? Optional.empty()
                : Optional.of(titleIcon + "<b><a href='https://www.investing.com/indices/nasdaq-composite'>NASDAQ Composite (IXIC)</a></b>\n"
                + " - price : <b><u>" + price + "</u></b>\n"
                + " - change : <b><u>" + priceChange + " (" + priceChangePercent + ")</u></b>\n");
    }

    private JsonObject getDataObject(Response slickResponse) throws IOException {
        if (slickResponse.status() != 200) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(slickResponse.status()), slickResponse.reason());
        }

        JsonObject response = JsonParser.parseString(this.getResponseBody(slickResponse)).getAsJsonObject();

        JsonObject data = null;

        if (SlickFeign.CODE_SUCCESS.equals(response.get("code").getAsString())) {
            data = response.get("data").getAsJsonObject();
        }

        return data;
    }

    private JsonArray getDataArray(Response slickResponse) throws IOException {
        if (slickResponse.status() != 200) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(slickResponse.status()), slickResponse.reason());
        }

        JsonObject response = JsonParser.parseString(this.getResponseBody(slickResponse)).getAsJsonObject();

        JsonArray data = null;

        if (SlickFeign.CODE_SUCCESS.equals(response.get("code").getAsString())) {
            data = response.get("data").getAsJsonArray();
        }

        return data;
    }
}
