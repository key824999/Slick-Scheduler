package toy.slick.parser;

import feign.Response;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public interface FeignParser {
    default String bodyToString(Response feignResponse) throws IOException {
        return IOUtils.toString(feignResponse.body().asReader(feignResponse.charset()));
    }
}
