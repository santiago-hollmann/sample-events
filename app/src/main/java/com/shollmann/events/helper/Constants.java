package com.shollmann.events.helper;

public class Constants {
    public static final String EMPTY_STRING = "";
    public static final String UTF_8 = "UTF-8";
    public static final String CACHE = "Cache";
    public static final String COORDINATES_FORMAT = "###.###";

    public class Time {
        public static final long TEN_SECONDS = 10;
        public static final long ONE_MINUTE = 10;
        public static final long TEN_MINUTES = 10 * ONE_MINUTE;
    }

    public class Size {
        public static final long ONE_KIBIBYTE = 1024;
        public static final long ONE_MEBIBYTE = ONE_KIBIBYTE * 1024;
        public static final long TWO_MEBIBYTES = ONE_MEBIBYTE * 2;
    }

    public class EventbriteApi {
        public static final String TOKEN = "VBEQ2ZP7SOEWDHH3PVOI";
        public static final String URL = "https://www.eventbriteapi.com";
    }
}
