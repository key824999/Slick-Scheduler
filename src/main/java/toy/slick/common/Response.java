package toy.slick.common;

import lombok.Getter;

@Getter
public class Response<T> {
    @Getter
    public enum CodeMessage {
        SUCCESS("0000", "Success"),
        UNKNOWN_ERROR("9999", "Unknown Error");

        private final String code;
        private final String message;

        CodeMessage(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    private final String code;
    private final String message;
    private final T data;

    public Response(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Response(T data) {
        this.code = CodeMessage.SUCCESS.getCode();
        this.message = CodeMessage.SUCCESS.getMessage();
        this.data = data;
    }
}
