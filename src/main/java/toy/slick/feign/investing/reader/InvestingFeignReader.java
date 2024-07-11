package toy.slick.feign.investing.reader;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import toy.slick.feign.interfaces.FeignResponseReader;
import toy.slick.feign.investing.vo.response.DowJonesIndustrialAverage;
import toy.slick.feign.investing.vo.response.NasdaqComposite;
import toy.slick.feign.investing.vo.response.StandardAndPoor500;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class InvestingFeignReader implements FeignResponseReader {
    public Optional<DowJonesIndustrialAverage> getDowJonesIndustrialAverageList(Response investingFeignResponse) throws IOException {
        String responseBody = this.getResponseBody(investingFeignResponse);

        Document document = Jsoup.parse(responseBody);

        String price = this.parsePrice(document);
        String priceChange = this.parsePriceChange(document);
        String priceChangePercent = this.parsePriceChangePercent(document);

        return StringUtils.isBlank(price)
                ? Optional.empty()
                : Optional.of(DowJonesIndustrialAverage.builder()
                .price(price)
                .priceChange(priceChange)
                .priceChangePercent(priceChangePercent.substring(1, priceChangePercent.length() - 1))
                .build());
    }

    public Optional<StandardAndPoor500> getStandardAndPoor500(Response investingFeignResponse) throws IOException {
        String responseBody = this.getResponseBody(investingFeignResponse);

        Document document = Jsoup.parse(responseBody);

        String price = this.parsePrice(document);
        String priceChange = this.parsePriceChange(document);
        String priceChangePercent = this.parsePriceChangePercent(document);

        return StringUtils.isBlank(price)
                ? Optional.empty()
                : Optional.of(StandardAndPoor500.builder()
                .price(price)
                .priceChange(priceChange)
                .priceChangePercent(priceChangePercent.substring(1, priceChangePercent.length() - 1))
                .build());
    }

    public Optional<NasdaqComposite> getNasdaqComposite(Response investingFeignResponse) throws IOException {
        String responseBody = this.getResponseBody(investingFeignResponse);

        Document document = Jsoup.parse(responseBody);

        String price = this.parsePrice(document);
        String priceChange = this.parsePriceChange(document);
        String priceChangePercent = this.parsePriceChangePercent(document);

        return StringUtils.isBlank(price)
                ? Optional.empty()
                : Optional.of(NasdaqComposite.builder()
                .price(price)
                .priceChange(priceChange)
                .priceChangePercent(priceChangePercent.substring(1, priceChangePercent.length() - 1))
                .build());
    }

    private String parsePrice(Document investingIndices) {
        return investingIndices.getElementsByAttributeValue("data-test", "instrument-price-last").first().text();
    }

    private String parsePriceChange(Document investingIndices) {
        return investingIndices.getElementsByAttributeValue("data-test", "instrument-price-change").first().text();
    }

    private String parsePriceChangePercent(Document investingIndices) {
        return investingIndices.getElementsByAttributeValue("data-test", "instrument-price-change-percent").first().text();
    }
}
