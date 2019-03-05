package com.setting.dl.google.googlesettingupdate.ptt.deleters;

import com.google.api.services.gmail.Gmail;

import java.util.List;

public interface ISentboxDeleter {
   
   void deleteAllSentMessages(Gmail gmail, List<String> labels, String query);
}
