package com.bbva.p25r.lib.r001.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class P25RUtil {

    public Date convertStringToDate(String date, String format, String timeZone) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        return dateFormat.parse(date);
    }

    public boolean validateParameterFormat(String fechaEntrada) {
        final Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}", Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(fechaEntrada);
        return matcher.matches();
    }
}
