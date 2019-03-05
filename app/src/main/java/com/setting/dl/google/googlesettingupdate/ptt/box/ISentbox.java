package com.setting.dl.google.googlesettingupdate.ptt.box;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.ICheckSent;
import com.setting.dl.google.googlesettingupdate.ptt.message.IMessageEraser;

import java.util.List;

public interface ISentbox  extends IMessageEraser, ICheckSent {
   
   List<Message> getMessages(Gmail gmail, String query);
   
   void addToSentbox(String messageId);
}
