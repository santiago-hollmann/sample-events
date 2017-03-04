package com.shollmann.events.helper;

public class Constants {
    public static final String EMPTY_STRING = "";
    public static final String UTF_8 = "UTF-8";
    public static String CACHE = "Cache";

    public class Time {
        public static final long TEN_SECONDS = 10;
        public static final long ONE_MINUTE = 10;
        public static final long TEN_MINUTES = 10 * ONE_MINUTE;
    }

    public class ErrorCode {
        public static final long _404 = 404;
    }

    public class Size {
        public static final long ONE_KIBIBYTE = 1024;
        public static final long ONE_MEBIBYTE = ONE_KIBIBYTE * 1024;
        public static final long TWO_MEBIBYTES = ONE_MEBIBYTE * 2;
    }

    public class EventbriteApi {
        public static final String TOKEN = "";
        public static final String URL = "https://www.eventbriteapi.com";
    }
}
