package com.setting.dl.google.googlesettingupdate.ptt.deleters;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.util.List;

public interface IInboxDeleter {
   
   boolean deleteMessage(Gmail gmail, String messageId);
   List<Message> deleteMessages(Gmail gmail, List<Message> messages);
   List<Message> deleteAllMessages(Gmail gmail, List<String> labels, String query);
}
