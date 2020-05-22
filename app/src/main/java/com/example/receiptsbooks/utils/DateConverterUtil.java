package com.example.receiptsbooks.utils;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateConverterUtil {
    private static final long oneDayMillis = 86400000;
    private static final long oneHourMillis = 3600000;
    private static final long oneMinuteMillis = 60000;
    private static final long oneSecondMillis = 1000;
    @TypeConverter
    public static Date revertDate(long value) {
        return new Date(value);
    }

    @TypeConverter
    public static long converterDate(Date value) {
        return value.getTime();
    }

    public static String dateToString(Date date,boolean isComplete){
        SimpleDateFormat sdf;
        if (isComplete){
            sdf = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss");
        }else{
            sdf = new SimpleDateFormat("yyyy/MM/dd");
        }
        return sdf.format(date);
    }

    public static Date stringToData(String str){
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(m.replaceAll("").trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateFormat(Date date){
        long intervalDate = System.currentTimeMillis() - date.getTime();
        if (intervalDate < oneMinuteMillis){
            return intervalDate/ oneSecondMillis +"秒前";
        }else if(intervalDate < oneHourMillis){
            return intervalDate/oneMinuteMillis+"分钟前";
        }else if (intervalDate < oneDayMillis){
            return intervalDate/oneHourMillis+"小时前";
        }else if (intervalDate < 3*oneDayMillis){
            return intervalDate/oneDayMillis+"天前";
        }else{
            return dateToString(date,false);
        }
    }
}
