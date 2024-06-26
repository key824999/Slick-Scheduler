package toy.slick.feign;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "TelegramFeign", url = "https://api.telegram.org")
public interface TelegramFeign {

    @GetMapping("/{apiToken}/sendMessage")
    Response sendText(@PathVariable("apiToken") String apiToken,
                      @RequestParam("chat_id") String chatId,
                      @RequestParam("text") String text);

    @GetMapping(value = "/{apiToken}/sendMessage", params = {
            "parse_mode=html"
    })
    Response sendHtmlWithPreview(@PathVariable("apiToken") String apiToken,
                                 @RequestParam("chat_id") String chatId,
                                 @RequestParam("text") String text);

    @GetMapping(value = "/{apiToken}/sendMessage", params = {
            "parse_mode=html",
            "disable_web_page_preview=true"
    })
    Response sendHtmlWithoutPreview(@PathVariable("apiToken") String apiToken,
                                 @RequestParam("chat_id") String chatId,
                                 @RequestParam("text") String text);

}
