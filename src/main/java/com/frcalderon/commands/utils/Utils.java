package com.frcalderon.commands.utils;

import java.time.format.DateTimeFormatter;

public class Utils {

    public static DateTimeFormatter localDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }
}
