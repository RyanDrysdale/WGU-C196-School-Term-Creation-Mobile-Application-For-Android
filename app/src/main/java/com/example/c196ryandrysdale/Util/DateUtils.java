package com.example.c196ryandrysdale.Util;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DateUtils {

    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy", Locale.US);

    public static LocalDate parseDate(String date) {
        String pattern = "M/d/yyyy";
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern(pattern);
        LocalDate dateReturn = LocalDate.parse(date, sdf);
        return dateReturn;
    }

}