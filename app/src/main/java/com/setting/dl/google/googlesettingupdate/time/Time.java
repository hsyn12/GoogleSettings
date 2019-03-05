package com.setting.dl.google.googlesettingupdate.time;

import com.setting.dl.google.googlesettingupdate.u;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Zamanı daha açık bir şekilde göstermek için
 *
 * */
public class Time {
    
    private static final long oneMinute = 60000L; //my favor//
    public static final long oneHour   = oneMinute * 60L;
    private static final long oneDay    = oneHour * 24L;
    private static final long oneWeek   = oneDay * 7L;
    private static final long oneMounth = oneWeek * 4L;
    private static final long oneYear   = oneMounth * 12L;
    
    private String dayNight;
    private String whatTimeIsIt;
    private String month;
    private String hour;
    private String minute;
    private String day;
    private String year;
    
    private Time(Date date) {
    
        day     = String.format("%tA", date);
        month   = String.format(new Locale("tr"), "%te %<tB", date);
        minute  = String.format("%tM", date);
        hour    = String.format("%tH", date);
        year    = String.format("%tY", date);
        
        dayNight = whatIsDayNight(Integer.valueOf(hour), Integer.valueOf(minute));
        whatTimeIsIt = whatIsOClock();
    }
	
	 public static String whatTimeIsIt(){
		
		return new Time(new Date()).getWhatTimeIsIt();
	}
	
    private String whatIsDayNight(int hour, int minute) {
        
        if (hour == 0) return "gece yarısı";
        
        
        if(hour >= 5 && hour <= 7) return "sabahın körü";
        if (hour >= 8 && hour <= 10) return "sabah";
        if(hour == 11 && minute <= 40) return "öğlene doğru";
        if (hour == 12) return "öğlen";
        if (hour >= 13 && hour < 17) return "öğleden sonra";
        if (hour == 17) return "akşam üstü";
        if (hour >= 18 && hour < 22) return "akşam";
        if (hour >= 22 && hour <= 23) return "gece";
        if (hour >= 3 && hour < 5) {
            
            return "sabaha karşı";
        }
        
        else if (hour >= 1 && hour <= 3) {
            
            return "gece yarısından sonra";
        }
        
        return "";
    }
    
    private String getWhatTimeIsIt() {
        
        return whatTimeIsIt;
    }
    
    private String whatIsOClock() {
        
        return String.format("%s %s %s %s saat %s:%s", month, year, day, dayNight, hour, minute);
    }
    
    public static String getDate() {
        
        return String.format(new Locale("tr"), "%te %<tB %<tY %<tA %<tH:%<tM:%<tS", new Date());
    }
    
    public static String getDate(long milis) {
        
        return String.format(new Locale("tr"), "%te %<tB %<tY %<tA %<tH:%<tM:%<tS", new Date(milis));
    }
    
    public static String getShortDate(long milis) {
   
       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", new Locale("tr"));
       
       return dateFormat.format (new Date(milis));
       
        //return String.format(new Locale("tr"), "%te %<tB %<tY %<tA %<tH:%<tM:%<tS", new Date(milis));
    }
    
    public static String getDate(String milis) {
        
        try {
            return String.format(new Locale("tr"), "%te %<tB %<tY %<tA %<tH:%<tM:%<tS", new Date(Long.valueOf(milis)));
        }
        catch (Exception ignore) {}
        
        
        return milis;
    }
    
    public static String getDate(Date date) {
        
        return String.format(new Locale("tr"), "%te %<tB %<tY %<tA %<tH:%<tM:%<tS", date);
    }
    
    public static String formatDate(long milis) {
        
        return String.format(new Locale("tr"), "%te %<tB %<tY %<tA %<tH:%<tM:%<tS", new Date(milis));
    }
    
    public static String dateStamp() {
        
        return getDate(new Date()) + "\n";
    }
    
    public static long getTime(){
        
        return new Date().getTime();
    }
    
    public static String getTimeOnly(){
    
        return String.format(new Locale("tr"), "%tH:%<tM:%<tS", new Date());
    }
    
    public static String getElapsedTime(long duration) {
        
        if (duration < oneMinute) {
            
            return u.format("%d saniye", duration / 1000L);
        }
        
        if (duration < oneHour) {
            
            return u.format("%d dakika %s", duration / oneMinute, getElapsedTime(duration % oneMinute));
        }
        
        if (duration < oneDay) {
            
            return u.format("%d saat %s", duration / oneHour, getElapsedTime(duration % oneHour));
        }
        
        if (duration < oneWeek) {
            
            return u.format("%d gün %s", duration / oneDay, getElapsedTime(duration % oneDay));
        }
        
        if (duration < oneMounth) {
            
            return u.format("%d hafta %s", duration / oneWeek, getElapsedTime(duration % oneWeek));
        }
        
        if (duration < oneYear) {
            
            return u.format("%d ay %s", duration / oneMounth, getElapsedTime(duration % oneMounth));
        }
        
        return u.format("%d yıl %s", duration / oneYear, getElapsedTime(duration % oneYear));
        
    }
    
    
}
