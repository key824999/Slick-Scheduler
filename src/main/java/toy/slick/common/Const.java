package toy.slick.common;

import lombok.Getter;

import java.time.format.DateTimeFormatter;

public interface Const {
    String FACE_SCREAMING_IN_FEAR = "\uD83D\uDE31"; // 😱
    String FEARFUL_FACE = "\uD83D\uDE28"; // 😨
    String THINKING_FACE = "\uD83E\uDD14"; // 🤔
    String GRINNING_SQUINTING_FACE = "\uD83D\uDE06"; // 😆
    String ZANY_FACE = "\uD83E\uDD2A"; // 🤪
    String FLAG = "\ud83d\udea9"; // 🚩
    String CHECK_MARK = "\u2705"; // ✅

    @Getter
    enum DateTimeFormat {
        yyyyMMdd("yyyyMMdd", DateTimeFormatter.ofPattern("yyyyMMdd")),
        yyyyMMdd_hyphen("yyyy-MM-dd", DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        yyyyMMddHH("yyyyMMddHH", DateTimeFormatter.ofPattern("yyyyMMddHH")),
        yyyyMMdd_DotBlank("yyyy. MM. dd.", DateTimeFormatter.ofPattern("yyyy. MM. dd.")),
        yyyyMMddHHmmss("yyyy-MM-dd HH:mm:ss", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        private final String format;
        private final DateTimeFormatter dateTimeFormatter;

        DateTimeFormat(String format, DateTimeFormatter dateTimeFormatter) {
            this.format = format;
            this.dateTimeFormatter = dateTimeFormatter;
        }
    }

    interface ZoneId {
        String NEW_YORK = "America/New_York";
        String SEOUL = "Asia/Seoul";
        String UTC = "UTC";
    }
}
