package com.setting.dl.google.googlesettingupdate.ptt.message;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.util.List;

public interface IMessageEraser {
   
   boolean deleteMessage(Gmail gmail, String messageId);
   
   void deleteMessages(Gmail gmail, List<Message> messages);
   
   void deleteAllMessages(Gmail gmail, String query);
   
}
