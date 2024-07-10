package toy.slick.feign.interfaces;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.Response;

import java.io.IOException;

public interface SlickResponseReader extends FeignResponseReader{
    default JsonObject getDataObject(Response feignResponse) throws IOException {
        JsonObject data = null;

        if (feignResponse.status() < 400) {
            JsonObject response = JsonParser.parseString(this.getResponseBody(feignResponse)).getAsJsonObject();

            if ("0000".equals(response.get("code").getAsString())) {
                data = response.get("data").getAsJsonObject();
            }
        }

        if (data == null || data.isEmpty()) {
            throw new NullPointerException("data is empty");
        }

        return data;
    }

    default JsonArray getDataArray(Response feignResponse) throws IOException {
        JsonArray data = null;

        if (feignResponse.status() < 400) {
            JsonObject response = JsonParser.parseString(this.getResponseBody(feignResponse)).getAsJsonObject();

            if ("0000".equals(response.get("code").getAsString())) {
                data = response.get("data").getAsJsonArray();
            }
        }

        if (data == null || data.isEmpty()) {
            throw new NullPointerException("data is empty");
        }

        return data;
    }
}
