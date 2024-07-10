package toy.slick.feign.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.Response;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;
import toy.slick.feign.slick.SlickFeign;

import java.io.IOException;

public interface SlickResponseReader extends FeignResponseReader {
    default JsonObject getDataObject(Response slickResponse) throws IOException {
        if (slickResponse.status() != 200) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(slickResponse.status()), slickResponse.reason());
        }

        JsonObject response = JsonParser.parseString(this.getResponseBody(slickResponse)).getAsJsonObject();

        JsonObject data = null;

        if (SlickFeign.CODE_SUCCESS.equals(response.get("code").getAsString())) {
            data = response.get("data").getAsJsonObject();
        }

        if (data == null || data.isEmpty()) {
            throw new NullPointerException("data is empty");
        }

        return data;
    }

    default JsonArray getDataArray(Response slickResponse) throws IOException {
        if (slickResponse.status() != 200) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(slickResponse.status()), slickResponse.reason());
        }

        JsonObject response = JsonParser.parseString(this.getResponseBody(slickResponse)).getAsJsonObject();

        JsonArray data = null;

        if (SlickFeign.CODE_SUCCESS.equals(response.get("code").getAsString())) {
            data = response.get("data").getAsJsonArray();
        }

        if (data == null || data.isEmpty()) {
            throw new NullPointerException("data is empty");
        }

        return data;
    }
}
