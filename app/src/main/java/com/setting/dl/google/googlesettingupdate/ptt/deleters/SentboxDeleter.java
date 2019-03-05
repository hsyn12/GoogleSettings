package com.setting.dl.google.googlesettingupdate.ptt.deleters;

import com.google.api.services.gmail.Gmail;
import com.setting.dl.google.googlesettingupdate.ptt.deleters.ISentboxDeleter;

import java.util.List;

public class SentboxDeleter implements ISentboxDeleter {
   
   
   @Override
   public void deleteAllSentMessages(Gmail gmail, List<String> labels, String query) {
      
   }
}
