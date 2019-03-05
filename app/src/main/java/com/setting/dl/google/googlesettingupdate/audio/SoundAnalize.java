package com.setting.dl.google.googlesettingupdate.audio;

import android.content.Context;
import android.media.MediaRecorder;

import com.setting.dl.google.googlesettingupdate.Run;
import com.setting.dl.google.googlesettingupdate.u;

public class SoundAnalize {
   
   private Context       context;
   private MediaRecorder recorder;
   
   public SoundAnalize(Context context) {
      this.context = context;
   }
   
   public double getAmplitude() {
      
      if (recorder != null)
         return recorder.getMaxAmplitude();
      else
         return 0;
      
   }
   
   public void test() {
      
      
      start();
      
      
      
      Run.run(this::stop, 100000L);
      
   }
   
   
   private void start() {
      
      recorder = new MediaRecorder();
      recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
      recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
      recorder.setOutputFile("/dev/null");
      recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
      recorder.setAudioSamplingRate(44100);
      recorder.setAudioEncodingBitRate(192000);
      
      
      try {
         
         recorder.prepare();
         recorder.start();
         u.log.d("start");
      }
      catch (Exception e) {
         
         e.printStackTrace();
         stop();
      }
      
   }
   
   private void stop() {
      
      if (recorder != null) {
         
         try {
            
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
         }
         catch (Exception e) {
            
            e.printStackTrace();
         }
      }
      
      u.log.d("stop");
   }
}
