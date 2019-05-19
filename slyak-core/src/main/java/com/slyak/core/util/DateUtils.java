package com.slyak.core.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {


    public static final FastDateFormat JDK_TIME_FORMAT = FastDateFormat.getInstance("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

    public static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    public static final FastDateFormat DATEMIN_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm");

    public static final FastDateFormat DATETIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private static final List<FastDateFormat> CUSTOM_FORMATS = Lists.newArrayList(JDK_TIME_FORMAT);


    public static Date parse(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Calendar) {
            return ((Calendar) value).getTime();
        }

        if (value instanceof Date) {
            return (Date) value;
        }

        if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        }

        if (value instanceof String) {
            String strVal = ((String) value).trim();
            if (strVal.length() == 0) {
                return null;
            } else if (strVal.indexOf('-') != -1) {
                FastDateFormat format;
                switch (strVal.length()) {
                    case 8:
                    case 9:
                        format = FastDateFormat.getInstance("yyyy-M-d");
                        break;
                    case 10:
                        format = DATE_FORMAT;
                        break;
                    case 16:
                        format = DATEMIN_FORMAT;
                        break;
                    case 19:
                        format = strVal.indexOf('T') > -1 ? DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT : DATETIME_FORMAT;
                        break;
                    default:
                        format = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT;
                }
                try {
                    return format.parse(strVal);
                } catch (ParseException ignored) {
                }
            } else {
                try {
                    return new Date(Long.parseLong(strVal));
                } catch (NumberFormatException ignored) {
                }
            }
            for (FastDateFormat fdf : CUSTOM_FORMATS) {
                try {
                    return fdf.parse(strVal);
                } catch (ParseException ignored) {
                }
            }
        }
        throw new IllegalArgumentException("Can not cast to Date, value : " + value);
    }
}
