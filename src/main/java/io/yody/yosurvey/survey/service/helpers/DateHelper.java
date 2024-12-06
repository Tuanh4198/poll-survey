package io.yody.yosurvey.survey.service.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    public static String formatDate(Date date, String format) {
        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date (month/day/year)
        DateFormat df = new SimpleDateFormat(format);

        // Get the date today using Calendar object.
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        return df.format(date);
    }
}
